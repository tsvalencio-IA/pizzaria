package br.com.thiaguinho.pizzariarastreamento

data class AuthSession(
    val uid: String,
    val idToken: String,
    val refreshToken: String,
    val expiresAtMillis: Long
)
