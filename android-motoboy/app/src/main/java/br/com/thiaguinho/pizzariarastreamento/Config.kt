package br.com.thiaguinho.pizzariarastreamento

object Config {
    /*
     * PREENCHA COM OS DADOS DO FIREBASE.
     * Firebase Console > Configurações do projeto > Geral > Seus apps > Web app.
     *
     * Exemplo:
     * const val FIREBASE_API_KEY = "AIzaSyBtC68h9S3dwB_J8OH8LN3xyda2sqo7PkQ"
     * const val FIREBASE_DATABASE_URL = "https://pizza-fa7b5-default-rtdb.firebaseio.com"
     */
    const val FIREBASE_API_KEY = "AIzaSyBtC68h9S3dwB_J8OH8LN3xyda2sqo7PkQ"
    const val FIREBASE_DATABASE_URL = "https://pizza-fa7b5-default-rtdb.firebaseio.com"

    /*
     * Use um código simples, sem espaço e sem acento.
     * O mesmo empresaId deve existir no painel-web/assets/js/firebase-config.js
     * e no Realtime Database em /deliveryApp/{empresaId}
     */
    const val DEFAULT_EMPRESA_ID = "pizzalog"

    /*
     * Intervalo de envio do GPS.
     * Para 6 motoboys, 8 a 12 segundos é um bom começo.
     */
    const val LOCATION_INTERVAL_MS = 8000L
    const val LOCATION_MIN_DISTANCE_METERS = 10f

    const val NOTIFICATION_CHANNEL_ID = "tracking_location_channel"
    const val NOTIFICATION_ID = 177
}
