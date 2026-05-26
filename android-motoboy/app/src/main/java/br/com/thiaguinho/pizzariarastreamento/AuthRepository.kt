package br.com.thiaguinho.pizzariarastreamento

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthRepository {
    suspend fun signIn(email: String, password: String): AuthSession = withContext(Dispatchers.IO) {
        if (Config.FIREBASE_API_KEY.startsWith("COLE_AQUI")) {
            error("Preencha FIREBASE_API_KEY em Config.kt antes de gerar o APK.")
        }
        val url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${Config.FIREBASE_API_KEY}"
        val body = JSONObject()
            .put("email", email.trim())
            .put("password", password)
            .put("returnSecureToken", true)
        val json = Network.postJson(url, body)
        val expiresInSeconds = json.optLong("expiresIn", 3600L)
        AuthSession(
            uid = json.getString("localId"),
            idToken = json.getString("idToken"),
            refreshToken = json.getString("refreshToken"),
            expiresAtMillis = System.currentTimeMillis() + (expiresInSeconds * 1000L)
        )
    }

    suspend fun refresh(oldRefreshToken: String): AuthSession = withContext(Dispatchers.IO) {
        val url = "https://securetoken.googleapis.com/v1/token?key=${Config.FIREBASE_API_KEY}"
        val json = Network.postForm(
            url,
            mapOf(
                "grant_type" to "refresh_token",
                "refresh_token" to oldRefreshToken
            )
        )
        val expiresInSeconds = json.optLong("expires_in", 3600L)
        AuthSession(
            uid = json.getString("user_id"),
            idToken = json.getString("id_token"),
            refreshToken = json.getString("refresh_token"),
            expiresAtMillis = System.currentTimeMillis() + (expiresInSeconds * 1000L)
        )
    }
}
