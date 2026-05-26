package br.com.thiaguinho.pizzariarastreamento

import android.location.Location
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FirebaseLocationRepository {
    suspend fun sendLocation(
        empresaId: String,
        session: AuthSession,
        location: Location,
        batteryPercent: Int?
    ) = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("lat", location.latitude)
            .put("lng", location.longitude)
            .put("accuracy", location.accuracy.toDouble())
            .put("provider", location.provider ?: "unknown")
            .put("updatedAt", System.currentTimeMillis())
            .put("updatedAtIso", nowIso())
            .put("trackingActive", true)

        if (location.hasSpeed()) body.put("speedMps", location.speed.toDouble())
        if (location.hasBearing()) body.put("bearing", location.bearing.toDouble())
        if (batteryPercent != null) body.put("battery", batteryPercent)

        val baseUrl = cleanDatabaseUrl()
        val empresa = Uri.encode(empresaId)
        val uid = Uri.encode(session.uid)
        val url = "$baseUrl/deliveryApp/$empresa/drivers/$uid/location.json?auth=${Uri.encode(session.idToken)}"
        Network.putJson(url, body)
    }

    suspend fun sendStatus(
        empresaId: String,
        session: AuthSession,
        trackingActive: Boolean,
        message: String
    ) = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("trackingActive", trackingActive)
            .put("message", message)
            .put("updatedAt", System.currentTimeMillis())
            .put("updatedAtIso", nowIso())
        val baseUrl = cleanDatabaseUrl()
        val empresa = Uri.encode(empresaId)
        val uid = Uri.encode(session.uid)
        val url = "$baseUrl/deliveryApp/$empresa/drivers/$uid/status.json?auth=${Uri.encode(session.idToken)}"
        Network.putJson(url, body)
    }

    private fun cleanDatabaseUrl(): String {
        val url = Config.FIREBASE_DATABASE_URL.trim().trimEnd('/')
        if (url.contains("SEU-PROJETO")) {
            error("Preencha FIREBASE_DATABASE_URL em Config.kt antes de gerar o APK.")
        }
        return url
    }

    private fun nowIso(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }
}
