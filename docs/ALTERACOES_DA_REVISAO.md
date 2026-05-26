# Alterações da revisão técnica — V2 corrigida

## Objetivo

Esta versão V2 incorpora a validação técnica recebida depois da primeira entrega do pacote Android nativo + painel web.

A arquitetura foi mantida:

- Android nativo em Kotlin para o motoboy.
- Foreground Service de localização.
- Firebase Auth.
- Firebase Realtime Database via REST no Android.
- Painel web estático com Firebase Web SDK + Leaflet/OpenStreetMap.
- GitHub Actions para gerar APK debug instalável, sem Google Play.
- Sem servidor próprio, sem Cloud Functions e sem Google Maps pago.

## Arquivos alterados

### 1. firebase/database.rules.json

Correções aplicadas:

- Admin lê e escreve tudo da empresa.
- Motoboy ativo lê apenas o próprio nó.
- Motoboy ativo escreve apenas a própria localização e o próprio status.
- Sem autenticação não acessa nada.
- A escrita de `status` também passou a exigir `profile.active == true`.
- A validação de `location` passou a exigir:
  - `lat` numérico entre -90 e 90;
  - `lng` numérico entre -180 e 180;
  - `updatedAt` numérico.

### 2. android-motoboy/app/src/main/java/br/com/thiaguinho/pizzariarastreamento/MainActivity.kt

Correções aplicadas:

- Antes de iniciar o rastreamento, o app agora verifica se a localização do aparelho está ligada.
- Se GPS/localização estiver desligado, o app abre a tela de configuração de localização.
- Antes de iniciar o rastreamento no Android 13+, o app verifica se a permissão de notificação foi autorizada.
- Se notificação estiver negada, o app avisa que o Android pode encerrar o rastreamento.
- Foi adicionado botão para abrir diretamente a tela de localização do aparelho.
- As mensagens operacionais ficaram mais claras para implantação real com motoboys.

## O que continua igual

- Não foi trocada a stack.
- Não foi adicionado servidor.
- Não foi adicionado Cloud Functions.
- Não foi adicionado Google Maps pago.
- Não foi removido o painel web.
- Não foi removido GitHub Actions.
- Não foi removido o modelo de baixo custo.

## Verdade técnica

O APK debug gerado pelo GitHub Actions é instalável diretamente no Android, desde que o celular permita instalar aplicativos de fonte externa.

Para rastreamento real em primeiro plano, o motoboy precisa:

1. permitir localização;
2. permitir notificações;
3. manter a localização do aparelho ligada;
4. evitar modo economia de bateria agressivo para este app;
5. não forçar o fechamento do aplicativo.

Em aparelhos como Xiaomi, Samsung e Motorola, pode ser necessário liberar manualmente a economia de bateria para manter o serviço ativo durante o turno.
