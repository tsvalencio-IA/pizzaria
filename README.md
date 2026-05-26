# Pizzaria GPS Nativo — thIAguinho Soluções Digitais — V2 Corrigida

Sistema base para pizzaria acompanhar motoboys em tempo real com baixo custo.

Esta é a **V2 corrigida** após revisão técnica. Ela mantém a arquitetura original e aplica os ajustes de segurança e robustez operacional.

## O que vem no pacote

- **App Android nativo do motoboy** em Kotlin, com Foreground Service de localização.
- **Painel web do gestor** com Firebase Auth + Realtime Database + mapa gratuito Leaflet/OpenStreetMap.
- **GitHub Actions** para gerar APK automaticamente.
- **Regras corrigidas do Firebase Realtime Database**.
- **Modelo de primeiros dados** para cadastrar pizzaria, admin e motoboys.
- **Prompt pronto** para enviar ao GPT Fábrica de APK revisar e validar.
- **POP de implantação** para configurar Firebase, Auth, RTDB, painel e APK.

## Correções principais da V2

- Regras do Realtime Database ficaram mais seguras.
- Motoboy ativo escreve somente a própria localização/status.
- Driver sem `profile.active == true` não consegue enviar GPS.
- Sem autenticação não há acesso.
- App Android agora verifica se o GPS/localização do aparelho está ligado antes de iniciar.
- App Android trata permissão de notificação no Android 13+ antes de iniciar.
- App Android orienta melhor o motoboy quando algo impede o rastreamento.

## Verdade técnica

O painel do gestor pode ser web.

O app do motoboy precisa ser APK Android nativo para rastreamento sério em primeiro plano. O PWA puro não garante GPS com tela bloqueada ou app em segundo plano.

Este projeto usa:

- Firebase Authentication por e-mail e senha;
- Firebase Realtime Database via REST no app Android;
- Foreground Service Android com notificação fixa;
- GitHub Actions para gerar APK sem Google Play;
- Leaflet/OpenStreetMap no painel para evitar custo de Google Maps.

O app não promete rastreamento com celular desligado, GPS desligado, internet desligada ou app forçado a fechar.

## Estrutura

```txt
/android-motoboy          App Android nativo do motoboy
/painel-web              Painel web do gestor
/firebase                Regras e modelo inicial do Realtime Database
/.github/workflows       Robô para gerar APK
docs                     Passo a passo, revisão e prompt
```

## Ordem correta

1. Criar projeto no Firebase.
2. Ativar Authentication por e-mail/senha.
3. Criar Realtime Database.
4. Colar as regras de `firebase/database.rules.json`.
5. Preencher configurações em:
   - `painel-web/assets/js/firebase-config.js`
   - `android-motoboy/app/src/main/java/br/com/thiaguinho/pizzariarastreamento/Config.kt`
6. Criar usuários no Firebase Authentication.
7. Importar/cadastrar primeiros dados no Realtime Database.
8. Conferir `/roles/{UID_DO_ADMIN}: "admin"`.
9. Conferir `/drivers/{UID_DO_MOTOBOY}/profile/active: true`.
10. Subir no GitHub.
11. Rodar GitHub Actions e baixar o APK.
12. Instalar APK nos celulares dos motoboys.
13. Abrir o painel web do gestor no navegador.

## Documentos importantes

- `docs/COMO_CONFIGURAR_FIREBASE.md`
- `docs/COMO_GERAR_APK_GITHUB_ACTIONS.md`
- `docs/POP_IMPLANTACAO_PIZZARIA.md`
- `docs/ALTERACOES_DA_REVISAO.md`
- `docs/CHECKLIST_DE_TESTE_REAL.md`
- `docs/PROMPT_PARA_FABRICA_APK.md`

## Assinatura

Powered by thIAguinho Soluções Digitais
