package br.com.thiaguinho.pizzariarastreamento

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class DeliveryItem(
    val id: String,
    val code: String,
    val customer: String,
    val address: String,
    val status: String,
    val createdAt: Long,
    val promisedAt: Long,
    val lat: Double?,
    val lng: Double?
)

class FirebaseDeliveriesRepository {

    suspend fun fetchMyOpenDeliveries(empresaId: String, session: AuthSession): List<DeliveryItem> =
        withContext(Dispatchers.IO) {

            val baseUrl = cleanDatabaseUrl()
            val empresa = Uri.encode(empresaId)
            val token = Uri.encode(session.idToken)
val uidQuoted = Uri.encode("\"" + session.uid + "\"")
val orderByDriverId = Uri.encode("\"driverId\"")
val orderByDriverUid = Uri.encode("\"driverUid\"")

// ✅ Consulta segura: pega somente entregas atribuídas ao motoboy (evita Permission Denied)
val url1 = "$baseUrl/deliveryApp/$empresa/deliveries.json?orderBy=$orderByDriverId&equalTo=$uidQuoted&auth=$token"
val url2 = "$baseUrl/deliveryApp/$empresa/deliveries.json?orderBy=$orderByDriverUid&equalTo=$uidQuoted&auth=$token"

val json1 = Network.getJson(url1)
val json2 = Network.getJson(url2)
if ((json1 == null || json1.length() == 0) && (json2 == null || json2.length() == 0)) return@withContext emptyList()

val merged = JSONObject()
if (json1 != null) {
  val k1 = json1.keys(); while (k1.hasNext()) { val k = k1.next(); merged.put(k, json1.get(k)) }
}
if (json2 != null) {
  val k2 = json2.keys(); while (k2.hasNext()) { val k = k2.next(); if (!merged.has(k)) merged.put(k, json2.get(k)) }
}

val json = merged
            if (json.length() == 0) return@withContext emptyList()

            val out = ArrayList<DeliveryItem>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val id = keys.next()
                val obj = json.optJSONObject(id) ?: continue

                val driverId = obj.optString("driverId", obj.optString("driverUid", ""))
                if (driverId != session.uid) continue

                val status = obj.optString("status", "aguardando")
                if (status.equals("entregue", true)) continue

                val code = obj.optString("code", id)
                val customer = obj.optString("customer", "")
                val address = obj.optString("address", "")
                val createdAt = obj.optLong("createdAt", 0L)
                val promisedAt = obj.optLong("promisedAt", obj.optLong("promisedNormalAt", 0L))

                val lat = if (obj.has("lat")) obj.optDouble("lat") else null
                val lng = if (obj.has("lng")) obj.optDouble("lng") else null

                out.add(
                    DeliveryItem(
                        id = id,
                        code = code,
                        customer = customer,
                        address = address,
                        status = status,
                        createdAt = createdAt,
                        promisedAt = promisedAt,
                        lat = lat,
                        lng = lng
                    )
                )
            }

            out.sortByDescending { it.createdAt }
            out
        }

    suspend fun updateStatus(
        empresaId: String,
        session: AuthSession,
        deliveryId: String,
        newStatus: String,
        note: String?
    ) = withContext(Dispatchers.IO) {
        val baseUrl = cleanDatabaseUrl()
        val empresa = Uri.encode(empresaId)
        val id = Uri.encode(deliveryId)

        // status (string) + metadata simples
        val statusUrl = "$baseUrl/deliveryApp/$empresa/deliveries/$id/status.json?auth=${Uri.encode(session.idToken)}"
        Network.putRaw(statusUrl, JSONObject.quote(newStatus))

        val meta = JSONObject()
            .put("updatedAt", System.currentTimeMillis())
        if (!note.isNullOrBlank()) meta.put("note", note)

        val metaUrl = "$baseUrl/deliveryApp/$empresa/deliveries/$id/lastUpdate.json?auth=${Uri.encode(session.idToken)}"
        Network.putJson(metaUrl, meta)
    }

    private fun cleanDatabaseUrl(): String {
        val url = Config.FIREBASE_DATABASE_URL.trim().trimEnd('/')
        if (url.contains("SEU-PROJETO")) {
            error("Preencha FIREBASE_DATABASE_URL em Config.kt antes de gerar o APK.")
        }
        return url
    }
}