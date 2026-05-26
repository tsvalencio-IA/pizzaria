package br.com.thiaguinho.pizzariarastreamento

import android.content.Context

class AuthStorage(context: Context) {
    private val prefs = context.getSharedPreferences("motoboy_auth", Context.MODE_PRIVATE)

    fun saveEmpresaId(empresaId: String) {
        prefs.edit().putString("empresaId", empresaId.trim()).apply()
    }

    fun getEmpresaId(): String {
        return prefs.getString("empresaId", null)?.takeIf { it.isNotBlank() }
            ?: Config.DEFAULT_EMPRESA_ID
    }

    fun saveCredentials(email: String, password: String) {
        prefs.edit()
            .putString("email", email.trim())
            .putString("password", password)
            .apply()
    }

    fun getEmail(): String = prefs.getString("email", "") ?: ""
    fun getPassword(): String = prefs.getString("password", "") ?: ""

    fun saveSession(session: AuthSession) {
        prefs.edit()
            .putString("uid", session.uid)
            .putString("idToken", session.idToken)
            .putString("refreshToken", session.refreshToken)
            .putLong("expiresAtMillis", session.expiresAtMillis)
            .apply()
    }

    fun loadSession(): AuthSession? {
        val uid = prefs.getString("uid", null) ?: return null
        val idToken = prefs.getString("idToken", null) ?: return null
        val refreshToken = prefs.getString("refreshToken", null) ?: return null
        val expiresAtMillis = prefs.getLong("expiresAtMillis", 0L)
        return AuthSession(uid, idToken, refreshToken, expiresAtMillis)
    }

    fun clearSession() {
        prefs.edit()
            .remove("uid")
            .remove("idToken")
            .remove("refreshToken")
            .remove("expiresAtMillis")
            .apply()
    }
}
