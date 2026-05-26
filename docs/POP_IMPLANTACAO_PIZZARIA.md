# POP de implantação — Pizzaria Rastreamento Motoboy

## 1. Criar o Firebase

No Firebase Console:

1. Crie um projeto.
2. Ative Authentication > Sign-in method > Email/senha.
3. Crie os usuários:
   - 1 usuário admin para o gestor;
   - 1 usuário para cada motoboy.
4. Ative Realtime Database.
5. Em Realtime Database > Rules, cole o conteúdo de `firebase/database.rules.json`.

## 2. Preencher configurações do painel web

Arquivo:

```txt
painel-web/assets/js/firebase-config.js
```

Preencha:

- `apiKey`
- `authDomain`
- `databaseURL`
- `projectId`
- `storageBucket`
- `messagingSenderId`
- `appId`
- `empresaId`
- nome da pizzaria

O `empresaId` deve ser simples, sem espaço e sem acento. Exemplo:

```txt
pizzaria_do_joao
```

## 3. Preencher configurações do Android

Arquivo:

```txt
android-motoboy/app/src/main/java/br/com/thiaguinho/pizzariarastreamento/Config.kt
```

Preencha:

```kotlin
const val FIREBASE_API_KEY = "SUA_API_KEY"
const val FIREBASE_DATABASE_URL = "https://SEU-PROJETO-default-rtdb.firebaseio.com"
const val DEFAULT_EMPRESA_ID = "pizzaria_do_joao"
```

O `DEFAULT_EMPRESA_ID` deve ser igual ao `empresaId` do painel.

## 4. Cadastrar dados iniciais no Realtime Database

Use `firebase/primeiros-dados-modelo.json` como base.

Estrutura mínima:

```json
{
  "deliveryApp": {
    "pizzaria_do_joao": {
      "roles": {
        "UID_DO_ADMIN": "admin"
      },
      "drivers": {
        "UID_DO_MOTOBOY": {
          "profile": {
            "name": "Motoboy 1",
            "phone": "",
            "active": true
          }
        }
      }
    }
  }
}
```

Atenção: o UID vem do Firebase Authentication, não é o e-mail.

Se `active` não estiver `true`, o motoboy não conseguirá enviar GPS.

## 5. Gerar o APK pelo GitHub Actions

1. Suba a pasta completa no GitHub.
2. Entre na aba Actions.
3. Rode o workflow `Gerar APK Motoboy`.
4. Baixe o artifact `pizzaria-motoboy-apk-debug`.
5. Dentro dele estará o `app-debug.apk`.

## 6. Instalar no celular do motoboy

No Android:

1. Baixe o APK.
2. Permita instalação de fonte externa.
3. Instale.
4. Abra o app.
5. Faça login com e-mail e senha do motoboy.
6. Permita localização.
7. Permita notificações.
8. Ligue a localização do aparelho.
9. Clique em `INICIAR TURNO E GPS`.
10. Confirme que apareceu a notificação fixa `Motoboy GPS ativo`.


## 6.1 Ajustar bateria para não matar o rastreamento

Siga o guia por marca:

- `docs/GUIA_BATERIA_MARCAS.md`

Esse passo é **obrigatório em muitos aparelhos** (Xiaomi, Samsung, Motorola etc.).

## 7. Teste real no painel

No painel do gestor:

1. Abra `painel-web/admin.html` pelo GitHub Pages ou Vercel.
2. Faça login com o usuário admin.
3. Veja se o motoboy aparece no mapa.
4. Verifique a última atualização.
5. Pare o turno no celular e veja se o status muda.

## 8. Problemas comuns

### Motoboy não aparece no painel

Verifique:

- o motoboy fez login no app;
- o UID do motoboy existe em `/drivers/{uid}/profile`;
- `active` está `true`;
- o `empresaId` do Android é igual ao do painel;
- o usuário admin tem `/roles/{uid}: "admin"`;
- as regras do Realtime Database foram publicadas;
- o celular está com internet.

### GPS não envia

Verifique:

- permissão de localização;
- localização do aparelho ligada;
- permissão de notificação no Android 13+;
- economia de bateria liberada para o app;
- se o app não foi forçado a fechar.

### GitHub Actions falhou

Verifique:

- se os arquivos estão no caminho correto;
- se a pasta `android-motoboy` está na raiz do repositório;
- se o workflow está em `.github/workflows/build-apk.yml`;
- se o erro foi de configuração Firebase em `Config.kt`.

## 9. Verdade operacional

Este MVP não promete rastreamento com o celular desligado nem com GPS desativado.

O rastreamento em primeiro plano depende de:

- app instalado;
- turno iniciado;
- permissão de localização;
- localização ativa no aparelho;
- notificação ativa;
- internet disponível.

thIAguinho Soluções — tecnologia sob medida.
