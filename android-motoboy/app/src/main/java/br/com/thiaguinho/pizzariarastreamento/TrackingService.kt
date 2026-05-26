package br.com.thiaguinho.pizzariarastreamento

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackingService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var storage: AuthStorage
    private val authRepository = AuthRepository()
    private val locationRepository = FirebaseLocationRepository()
    private var locationManager: LocationManager? = null
    private var lastSentText: String = "Aguardando GPS"

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            sendLocation(location)
        }
    }

    override fun onCreate() {
        super.onCreate()
        storage = AuthStorage(this)
        createNotificationChannel()
        startForeground(Config.NOTIFICATION_ID, buildNotification("Rastreamento iniciado"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        setOnlineStatus(true, "Motoboy iniciou o rastreamento")
        return START_STICKY
    }

    override fun onDestroy() {
        locationManager?.removeUpdates(locationListener)
        try {
            runBlocking {
                val session = getValidSessionOrNull()
                if (session != null) {
                    locationRepository.sendStatus(storage.getEmpresaId(), session, false, "Motoboy parou o rastreamento")
                }
            }
        } catch (_: Exception) {
            // Evita travar o encerramento se o celular estiver sem internet.
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            stopSelf()
            return
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        providers.forEach { provider ->
            try {
                if (locationManager?.isProviderEnabled(provider) == true) {
                    locationManager?.requestLocationUpdates(
                        provider,
                        Config.LOCATION_INTERVAL_MS,
                        Config.LOCATION_MIN_DISTANCE_METERS,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }
            } catch (_: SecurityException) {
                stopSelf()
            }
        }
    }

    private fun sendLocation(location: Location) {
        serviceScope.launch {
            try {
                val session = getValidSessionOrNull() ?: run {
                    updateNotification("Login expirado. Abra o app e entre novamente.")
                    stopSelf()
                    return@launch
                }
                val empresaId = storage.getEmpresaId()
                val battery = Battery.percent(this@TrackingService)
                locationRepository.sendLocation(empresaId, session, location, battery)
                lastSentText = "GPS enviado às ${timeNow()}"
                updateNotification(lastSentText)
            } catch (e: Exception) {
                updateNotification("Falha ao enviar GPS: ${e.message?.take(70) ?: "erro"}")
            }
        }
    }

    private suspend fun getValidSessionOrNull(): AuthSession? {
        val session = storage.loadSession() ?: return null
        val shouldRefresh = System.currentTimeMillis() > session.expiresAtMillis - (5 * 60 * 1000L)
        if (!shouldRefresh) return session
        return try {
            val refreshed = authRepository.refresh(session.refreshToken)
            storage.saveSession(refreshed)
            refreshed
        } catch (_: Exception) {
            null
        }
    }

    private fun setOnlineStatus(active: Boolean, message: String) {
        serviceScope.launch {
            try {
                val session = getValidSessionOrNull() ?: return@launch
                locationRepository.sendStatus(storage.getEmpresaId(), session, active, message)
            } catch (_: Exception) {
                // Status é auxiliar; localização continua tentando.
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Config.NOTIFICATION_CHANNEL_ID,
                "Rastreamento de entregas",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Mostra que o GPS do motoboy está ativo durante o turno."
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, Config.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Motoboy GPS ativo")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Config.NOTIFICATION_ID, buildNotification(text))
    }

    private fun timeNow(): String {
        return SimpleDateFormat("HH:mm:ss", Locale("pt", "BR")).format(Date())
    }
}
