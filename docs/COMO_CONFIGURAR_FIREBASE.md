# Como configurar o Firebase

## 1. Criar projeto

1. Acesse Firebase Console.
2. Crie um projeto novo para a pizzaria.
3. Entre em **Authentication**.
4. Ative o provedor **E-mail/senha**.
5. Entre em **Realtime Database**.
6. Crie o banco em modo teste apenas para configurar.
7. Depois cole as regras de `firebase/database.rules.json`.

## 2. Criar usuários

Crie no Firebase Authentication:

- 1 usuário gestor/admin;
- 1 usuário para cada motoboy.

Depois de criar cada usuário, abra o usuário e copie o **UID**.

## 3. Primeiros dados do banco

Abra `firebase/primeiros-dados-modelo.json` e troque:

- `COLE_AQUI_O_UID_DO_GESTOR` pelo UID do gestor;
- `COLE_AQUI_O_UID_DO_MOTOBOY_1` pelo UID do motoboy 1;
- `COLE_AQUI_O_UID_DO_MOTOBOY_2` pelo UID do motoboy 2;
- adicione os demais motoboys seguindo o mesmo modelo.

Depois importe esse JSON no Realtime Database.

Atenção: cada motoboy precisa estar com `profile.active: true`. Se estiver `false`, ausente ou escrito como texto, as regras bloqueiam o envio de GPS.

## 4. Configuração do painel web

Edite:

```txt
painel-web/assets/js/firebase-config.js
```

Cole os dados do Firebase Web App e ajuste:

```js
empresaId: "pizzaria_modelo",
nomePizzaria: "Nome da pizzaria",
baseLat: -20.0000,
baseLng: -49.0000
```

## 5. Configuração do app Android

Edite:

```txt
android-motoboy/app/src/main/java/br/com/thiaguinho/pizzariarastreamento/Config.kt
```

Preencha:

```kotlin
const val FIREBASE_API_KEY = "SUA_API_KEY"
const val FIREBASE_DATABASE_URL = "https://SEU-PROJETO-default-rtdb.firebaseio.com"
const val DEFAULT_EMPRESA_ID = "pizzaria_modelo"
```

## 6. Regras do banco

No Realtime Database, cole o conteúdo de:

```txt
firebase/database.rules.json
```

## 7. Importante

O UID do motoboy no banco precisa ser o mesmo UID do Firebase Authentication.

O UID do admin precisa existir em `/deliveryApp/{empresaId}/roles/{uidDoAdmin}` com valor exato `admin`.

O app Android grava a localização em:

```txt
/deliveryApp/{empresaId}/drivers/{uidDoMotoboy}/location
```

O painel lê os motoboys em:

```txt
/deliveryApp/{empresaId}/drivers
```
