# Instalação rápida — 1 pizzaria (admin + até 6 motoboys)

Se você só quer “rodar” sem ler tudo:

1) Firebase
- Criar projeto
- Ativar Auth (email/senha)
- Criar usuário admin + motoboys
- Ativar Realtime Database
- Colar rules: `firebase/database.rules.json`

2) Dados iniciais
- Colar o JSON-base em RTDB (ajustar empresaId e UIDs): `firebase/primeiros-dados-modelo.json`

3) Configurar
- Painel: `painel-web/assets/js/firebase-config.js`
- Android: `android-motoboy/.../Config.kt`

4) Gerar APK
- Rodar Action “Gerar APK Motoboy”
- Baixar artifact e instalar `app-debug.apk`

5) Celular do motoboy
- Permitir Localização + Notificação
- Ligar GPS
- Ajustar bateria (obrigatório): `docs/GUIA_BATERIA_MARCAS.md`
- Iniciar turno e conferir notificação fixa

6) Gestor
- Hospedar painel (GitHub Pages / Vercel)
- Login admin
- Ver motoboys no mapa

thIAguinho Soluções — tecnologia sob medida.
