package br.com.thiaguinho.pizzariarastreamento

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeliveriesActivity : Activity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var storage: AuthStorage
    private val authRepository = AuthRepository()
    private val repo = FirebaseDeliveriesRepository()

    private lateinit var listContainer: LinearLayout
    private lateinit var msg: TextView
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = AuthStorage(this)
        buildUi()
        load()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 34, 28, 34)
            setBackgroundColor(Color.rgb(17, 24, 39))
        }
        scroll.addView(root)

        val title = TextView(this).apply {
            text = "Minhas entregas"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 10)
        }
        root.addView(title)

        msg = TextView(this).apply {
            text = ""
            textSize = 13f
            setTextColor(Color.rgb(209, 213, 219))
            setPadding(0, 0, 0, 14)
        }
        root.addView(msg)

        btnRefresh = Button(this).apply {
            text = "ATUALIZAR LISTA"
            setBackgroundColor(Color.rgb(37, 99, 235))
            setTextColor(Color.WHITE)
        }
        btnRefresh.setOnClickListener { load() }
        root.addView(btnRefresh)

        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        root.addView(listContainer)

        val back = Button(this).apply {
            text = "VOLTAR"
            setBackgroundColor(Color.rgb(75, 85, 99))
            setTextColor(Color.WHITE)
        }
        back.setOnClickListener { finish() }
        root.addView(back)

        setContentView(scroll)
    }

    private fun load() {
        hideKeyboard()
        msg.text = "Carregando..."
        listContainer.removeAllViews()

        scope.launch {
            try {
                val empresaId = storage.getEmpresaId()
                val session = ensureSession() ?: run {
                    msg.text = "Faça login no app e tente novamente."
                    return@launch
                }

                val deliveries = repo.fetchMyOpenDeliveries(empresaId, session)
                if (deliveries.isEmpty()) {
                    msg.text = "Nenhuma entrega atribuída pra você agora."
                    return@launch
                }

                msg.text = "Entregas ativas: ${deliveries.size}"
                deliveries.forEach { addCard(empresaId, session, it) }

            } catch (e: Exception) {
                msg.text = "Erro: ${e.message}"
            }
        }
    }

    private suspend fun ensureSession(): AuthSession? {
        val current = storage.loadSession() ?: return null
        if (System.currentTimeMillis() < current.expiresAtMillis - 30_000) return current
        val refreshed = authRepository.refresh(current.refreshToken)
        storage.saveSession(refreshed)
        return refreshed
    }

    private fun addCard(empresaId: String, session: AuthSession, d: DeliveryItem) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(Color.rgb(31, 41, 55))
        }

        fun row(label: String, value: String) {
            val tv = TextView(this).apply {
                text = "$label: $value"
                textSize = 13f
                setTextColor(Color.rgb(229, 231, 235))
                setPadding(0, 2, 0, 2)
            }
            card.addView(tv)
        }

        row("Código", d.code)
        if (d.customer.isNotBlank()) row("Cliente", d.customer)
        if (d.address.isNotBlank()) row("Endereço", d.address)
        row("Status", d.status.uppercase(Locale.getDefault()))
        if (d.promisedAt > 0) row("Promessa", formatTime(d.promisedAt))

        
        val actionsWrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 14, 0, 0)
        }

        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 10, 0, 0) }

        val btnMapa = Button(this).apply {
            text = "ABRIR ROTA"
            setBackgroundColor(Color.rgb(22, 163, 74))
            setTextColor(Color.WHITE)
        }
        btnMapa.setOnClickListener { openRoute(d) }

        val btnEmEntrega = Button(this).apply {
            text = "INICIAR ENTREGA"
            setBackgroundColor(Color.rgb(37, 99, 235))
            setTextColor(Color.WHITE)
        }
        btnEmEntrega.setOnClickListener {
            scope.launch {
                try {
                    repo.updateStatus(empresaId, session, d.id, "em_entrega", null)
                    Toast.makeText(this@DeliveriesActivity, "Status: em entrega.", Toast.LENGTH_SHORT).show()
                    load()
                } catch (e: Exception) {
                    Toast.makeText(this@DeliveriesActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        val btnEntregue = Button(this).apply {
            text = "MARCAR ENTREGUE"
            setBackgroundColor(Color.rgb(220, 38, 38))
            setTextColor(Color.WHITE)
        }
        btnEntregue.setOnClickListener {
            promptNote("Relato opcional (entrega)") { note ->
                scope.launch {
                    try {
                        repo.updateStatus(empresaId, session, d.id, "entregue", note)
                        Toast.makeText(this@DeliveriesActivity, "Entrega marcada como entregue.", Toast.LENGTH_SHORT).show()
                        load()
                    } catch (e: Exception) {
                        Toast.makeText(this@DeliveriesActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val btnRelato = Button(this).apply {
            text = "RELATO / OCORRÊNCIA"
            setBackgroundColor(Color.rgb(245, 158, 11))
            setTextColor(Color.BLACK)
        }
        btnRelato.setOnClickListener {
            promptNote("Digite o relato/ocorrência") { note ->
                if (note.isBlank()) {
                    Toast.makeText(this@DeliveriesActivity, "Nada para salvar.", Toast.LENGTH_SHORT).show()
                    return@promptNote
                }
                scope.launch {
                    try {
                        // mantém status atual, grava apenas nota em lastUpdate
                        repo.updateStatus(empresaId, session, d.id, d.status, note)
                        Toast.makeText(this@DeliveriesActivity, "Relato salvo.", Toast.LENGTH_SHORT).show()
                        load()
                    } catch (e: Exception) {
                        Toast.makeText(this@DeliveriesActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        row1.addView(btnMapa, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(0,0,12,0) })
        row1.addView(btnEmEntrega, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        row2.addView(btnEntregue, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(0,0,12,0) })
        row2.addView(btnRelato, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        actionsWrap.addView(row1)
        actionsWrap.addView(row2)
        card.addView(actionsWrap)

        val spacer = TextView(this).apply { text=""; setPadding(0, 0, 0, 14) }
        listContainer.addView(card)
        listContainer.addView(spacer)
    }

    
    private fun promptNote(title: String, onSubmit: (String) -> Unit) {
        val input = EditText(this).apply {
            hint = "Opcional"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.rgb(156, 163, 175))
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("SALVAR") { _, _ ->
                onSubmit(input.text?.toString()?.trim() ?: "")
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

private fun openRoute(d: DeliveryItem) {
        val uri = when {
            d.lat != null && d.lng != null -> Uri.parse("google.navigation:q=${d.lat},${d.lng}")
            d.address.isNotBlank() -> Uri.parse("google.navigation:q=" + Uri.encode(d.address))
            else -> null
        }
        if (uri == null) {
            Toast.makeText(this, "Sem endereço/posição para rota.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            Toast.makeText(this, "Não consegui abrir o mapa.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTime(ms: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
        return sdf.format(Date(ms))
    }

    private fun hideKeyboard() {
        val v = currentFocus ?: return
        v.clearFocus()
    }
}