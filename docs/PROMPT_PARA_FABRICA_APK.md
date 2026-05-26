# PROMPT PARA ENVIAR AO GPT FÁBRICA DE APK — V2 CORRIGIDA

Você é a Fábrica de APK da thIAguinho Soluções Digitais. Analise, revise e valide este projeto como engenheiro Android sênior, especialista em apps nativos, Firebase, rastreamento por GPS, Foreground Service e GitHub Actions.

## Contexto do projeto

Estamos criando um sistema profissional e de baixo custo para uma pizzaria acompanhar motoboys em tempo real.

A pizzaria já tem sistema próprio de pedidos. Nosso sistema não substitui o sistema da pizzaria. Ele resolve a dor operacional de saber onde está cada motoboy e se a promessa de entrega deve continuar em 30 minutos ou ser aumentada para 40 minutos quando não houver motoboy disponível.

## Observação importante sobre esta versão

Esta é a **V2 corrigida** após validação técnica.

Correções já aplicadas nesta versão:

1. `firebase/database.rules.json` foi endurecido:
   - admin lê e escreve tudo da empresa;
   - motoboy ativo lê somente o próprio nó;
   - motoboy ativo escreve somente a própria localização/status;
   - sem Auth não acessa nada;
   - `status` também exige `profile.active == true`;
   - `location` valida latitude, longitude e timestamp.
2. `MainActivity.kt` foi melhorado:
   - verifica se a localização do aparelho está ligada antes de iniciar o turno;
   - abre tela de localização se GPS/localização estiver desligado;
   - verifica permissão de notificação no Android 13+ antes de iniciar;
   - avisa que sem notificação o Android pode encerrar o rastreamento;
   - adiciona botão para abrir localização do aparelho.
3. Foram adicionados documentos operacionais:
   - `docs/ALTERACOES_DA_REVISAO.md`;
   - `docs/POP_IMPLANTACAO_PIZZARIA.md`.

## Estrutura recebida

```txt
/android-motoboy
/painel-web
/firebase
/.github/workflows
/docs
```

## Stack definida e proibida de trocar

- Android nativo Kotlin para o app do motoboy.
- Foreground Service de localização.
- Firebase Auth por e-mail e senha.
- Firebase Realtime Database para localização em tempo real.
- Android usando REST do Firebase, sem SDK Firebase obrigatório no app.
- Painel web em HTML/CSS/JS puro.
- Leaflet/OpenStreetMap para mapa gratuito.
- GitHub Actions para gerar APK.
- Sem Google Play inicialmente.
- Sem Google Maps pago inicialmente.
- Sem servidor pago inicialmente.
- Sem Cloud Functions no MVP.

## O que você precisa verificar obrigatoriamente

1. Se o projeto Android compila no GitHub Actions.
2. Se o Foreground Service está correto para GPS em primeiro plano.
3. Se as permissões Android estão corretas:
   - INTERNET;
   - ACCESS_FINE_LOCATION;
   - ACCESS_COARSE_LOCATION;
   - FOREGROUND_SERVICE;
   - FOREGROUND_SERVICE_LOCATION;
   - POST_NOTIFICATIONS.
4. Se o app inicia o serviço corretamente após o usuário clicar em iniciar turno.
5. Se o app mostra notificação fixa enquanto o rastreamento está ativo.
6. Se o app bloqueia início do turno quando:
   - usuário não fez login;
   - permissão de localização foi negada;
   - permissão de notificação foi negada no Android 13+;
   - localização/GPS do aparelho está desligada.
7. Se o app envia localização para o Firebase Realtime Database no caminho correto:

```txt
/deliveryApp/{empresaId}/drivers/{uidDoMotoboy}/location
```

8. Se o app atualiza status em:

```txt
/deliveryApp/{empresaId}/drivers/{uidDoMotoboy}/status
```

9. Se o painel web lê corretamente:

```txt
/deliveryApp/{empresaId}/drivers
/deliveryApp/{empresaId}/deliveries
/deliveryApp/{empresaId}/roles
```

10. Se as regras do Realtime Database estão seguras para MVP:
    - gestor lê e escreve tudo da empresa;
    - motoboy ativo escreve apenas a própria localização/status;
    - usuário sem Auth não acessa;
    - driver sem `profile.active == true` não consegue enviar GPS.
11. Se o workflow `.github/workflows/build-apk.yml` gera APK instalável.
12. Se não há dependência desnecessária, custo oculto ou API paga obrigatória.
13. Se o código está simples para manutenção por pessoa não programadora.
14. Se existe algum erro de compilação em Kotlin/Gradle/YAML/Manifest.

## Não pode fazer

- Não simplificar o projeto removendo lógica importante.
- Não trocar Firebase Realtime Database por Firestore.
- Não colocar Google Maps pago como obrigatório.
- Não exigir publicação na Play Store.
- Não transformar o app do motoboy em PWA puro.
- Não remover a assinatura thIAguinho Soluções Digitais.
- Não criar dependência de Node, servidor próprio ou Cloud Functions para o MVP.
- Não prometer rastreamento com celular desligado, GPS desligado, internet desligada ou app forçado a fechar.

## Melhorias permitidas

Você pode melhorar:

- estabilidade do Foreground Service;
- compatibilidade com Android 10, 11, 12, 13, 14 e superior;
- tratamento de token expirado do Firebase;
- tratamento de GPS desligado;
- mensagem de erro para motoboy;
- layout mobile;
- GitHub Actions;
- segurança das regras;
- geração futura de APK release assinado.

Mas não troque a arquitetura de baixo custo.

## Validação esperada

Responda com:

1. Diagnóstico técnico honesto.
2. Lista de riscos reais.
3. Arquivos que precisam ser alterados, se houver.
4. Correções completas, sem placebo.
5. Confirmação se o APK será instalável direto no Android sem Google Play.
6. Confirmação se o painel web pode ser hospedado no GitHub Pages/Vercel.
7. Confirmação se a solução continua de baixo custo para 1 pizzaria com 6 motoboys.
8. Veredito final: APROVADO, APROVADO COM AJUSTES ou REPROVADO.

## Padrão thIAguinho Soluções

O sistema precisa ser profissional, direto, simples para o cliente usar e com verdade técnica absoluta. A entrega precisa ser utilizável por uma pessoa que sabe subir arquivos no GitHub/Firebase, mas não programa.
