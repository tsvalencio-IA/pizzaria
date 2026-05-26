package br.com.thiaguinho.pizzariarastreamento

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var storage: AuthStorage
    private val authRepository = AuthRepository()

    private lateinit var empresaEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var statusText: TextView
    private lateinit var loginButton: Button
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = AuthStorage(this)
        buildUi()
        requestInitialPermissions()
        refreshStatus()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 42, 32, 42)
            setBackgroundColor(Color.rgb(17, 24, 39))
        }
        scroll.addView(root)

        val title = TextView(this).apply {
            text = "Motoboy GPS"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 12)
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Pizzaria Rastreamento • thIAguinho Soluções Digitais"
            textSize = 14f
            setTextColor(Color.rgb(209, 213, 219))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 28)
        }
        root.addView(subtitle)

        empresaEdit = field("Empresa ID", storage.getEmpresaId(), false)
        root.addView(label("Empresa ID"))
        root.addView(empresaEdit)

        emailEdit = field("E-mail do motoboy", storage.getEmail(), false)
        root.addView(label("E-mail"))
        root.addView(emailEdit)

        passwordEdit = field("Senha", storage.getPassword(), true)
        root.addView(label("Senha"))
        root.addView(passwordEdit)

        loginButton = actionButton("Entrar / Salvar login", Color.rgb(37, 99, 235))
        loginButton.setOnClickListener { signIn() }
        root.addView(loginButton)

        startButton = actionButton("INICIAR TURNO E GPS", Color.rgb(22, 163, 74))
        startButton.setOnClickListener { startTracking() }
        root.addView(startButton)

        stopButton = actionButton("PARAR TURNO", Color.rgb(220, 38, 38))
        stopButton.setOnClickListener { stopTracking() }
        root.addView(stopButton)

        val locationHelp = actionButton("Abrir localização do aparelho", Color.rgb(75, 85, 99))
        locationHelp.setOnClickListener { openLocationSettings() }
        root.addView(locationHelp)

        val batteryHelp = actionButton("Abrir economia de bateria", Color.rgb(75, 85, 99))
        batteryHelp.setOnClickListener { openBatterySettings() }
        root.addView(batteryHelp)

        statusText = TextView(this).apply {
            textSize = 15f
            setTextColor(Color.rgb(243, 244, 246))
            setPadding(0, 26, 0, 20)
        }
        root.addView(statusText)

        val truth = TextView(this).apply {
            text = "Importante: mantenha a localização ligada, permita as notificações e não force o fechamento do app. Enquanto o GPS estiver ativo, uma notificação fixa aparecerá no celular. Em alguns aparelhos, também é necessário liberar o app na economia de bateria."
            textSize = 13f
            setTextColor(Color.rgb(209, 213, 219))
            setPadding(0, 24, 0, 8)
        }
        root.addView(truth)

        val footer = TextView(this).apply {
            text = "Powered by thIAguinho Soluções Digitais"
            textSize = 12f
            setTextColor(Color.rgb(156, 163, 175))
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 0)
        }
        root.addView(footer)

        setContentView(scroll)
    }

    private fun label(text: String): TextView = TextView(this).apply {
        this.text = text
        textSize = 13f
        setTextColor(Color.rgb(209, 213, 219))
        setPadding(0, 10, 0, 6)
    }

    private fun field(hint: String, value: String, password: Boolean): EditText = EditText(this).apply {
        this.hint = hint
        setText(value)
        textSize = 16f
        setSingleLine(true)
        setTextColor(Color.WHITE)
        setHintTextColor(Color.rgb(156, 163, 175))
        setBackgroundColor(Color.rgb(31, 41, 55))
        setPadding(22, 16, 22, 16)
        inputType = if (password) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    private fun actionButton(text: String, color: Int): Button = Button(this).apply {
        this.text = text
        textSize = 15f
        setTextColor(Color.WHITE)
        setBackgroundColor(color)
        setPadding(12, 14, 12, 14)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 22, 0, 0)
        layoutParams = params
    }

    private fun signIn() {
        hideKeyboard()
        val empresaId = empresaEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()

        if (empresaId.isBlank() || email.isBlank() || password.isBlank()) {
            toast("Preencha empresa, e-mail e senha.")
            return
        }

        setButtons(false)
        statusText.text = "Entrando no Firebase..."
        scope.launch {
            try {
                val session = authRepository.signIn(email, password)
                storage.saveEmpresaId(empresaId)
                storage.saveCredentials(email, password)
                storage.saveSession(session)
                statusText.text = "Login OK. UID do motoboy: ${session.uid}\nAgora clique em INICIAR TURNO E GPS."
                toast("Login realizado")
            } catch (e: Exception) {
                statusText.text = "Erro no login: ${e.message}"
                toast("Falha no login")
            } finally {
                setButtons(true)
            }
        }
    }

    private fun startTracking() {
        hideKeyboard()
        storage.saveEmpresaId(empresaEdit.text.toString().trim().ifBlank { Config.DEFAULT_EMPRESA_ID })

        if (storage.loadSession() == null) {
            toast("Faça login antes de iniciar o GPS.")
            return
        }

        if (!hasLocationPermission()) {
            requestInitialPermissions()
            statusText.text = "Autorize a localização do aparelho e clique novamente em INICIAR TURNO E GPS."
            toast("Autorize a localização e clique novamente.")
            return
        }

        if (!hasNotificationPermission()) {
            requestInitialPermissions()
            statusText.text = "Autorize as notificações do app. Sem notificação, o Android pode encerrar o rastreamento. Depois clique novamente em INICIAR TURNO E GPS."
            toast("Autorize as notificações do app.")
            return
        }

        if (!isAnyLocationProviderEnabled()) {
            statusText.text = "A localização do aparelho está desligada. Ligue o GPS/localização e depois clique novamente em INICIAR TURNO E GPS."
            toast("Ligue a localização do aparelho.")
            openLocationSettings()
            return
        }

        val intent = Intent(this, TrackingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        statusText.text = "GPS iniciado. Veja a notificação fixa no celular. Se o ponto não aparecer no painel, confirme internet, login, UID ativo no Firebase e regras do Realtime Database."
        toast("Rastreamento iniciado")
    }

    private fun stopTracking() {
        stopService(Intent(this, TrackingService::class.java))
        statusText.text = "Turno parado. O painel receberá status offline."
        toast("Rastreamento parado")
    }

    private fun requestInitialPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 177)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 177) return

        val denied = permissions.filterIndexed { index, _ -> grantResults.getOrNull(index) != PackageManager.PERMISSION_GRANTED }
        when {
            denied.any { it == Manifest.permission.ACCESS_FINE_LOCATION || it == Manifest.permission.ACCESS_COARSE_LOCATION } -> {
                statusText.text = "Localização negada. O GPS não vai funcionar enquanto essa permissão não for autorizada."
                toast("Localização negada")
            }
            denied.any { Build.VERSION.SDK_INT >= 33 && it == Manifest.permission.POST_NOTIFICATIONS } -> {
                statusText.text = "Notificação negada. O Android pode encerrar o rastreamento com mais facilidade. Autorize nas configurações do app antes de iniciar o turno."
                toast("Notificação negada")
            }
            else -> {
                statusText.text = "Permissões autorizadas. Faça login e clique em INICIAR TURNO E GPS."
                toast("Permissões autorizadas")
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    private fun isAnyLocationProviderEnabled(): Boolean {
        return try {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            false
        }
    }

    private fun refreshStatus() {
        val session = storage.loadSession()
        statusText.text = if (session != null) {
            "Login salvo. UID: ${session.uid}\nEmpresa: ${storage.getEmpresaId()}"
        } else {
            "Sem login salvo. Entre com o e-mail e senha do motoboy."
        }
    }

    private fun setButtons(enabled: Boolean) {
        loginButton.isEnabled = enabled
        startButton.isEnabled = enabled
        stopButton.isEnabled = enabled
    }

    private fun openLocationSettings() {
        try {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (_: Exception) {
                toast("Não foi possível abrir as configurações de localização.")
            }
        }
    }

    private fun openBatterySettings() {
        try {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (_: Exception) {
                toast("Não foi possível abrir as configurações.")
            }
        }
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
