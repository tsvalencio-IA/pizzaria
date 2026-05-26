# Como gerar o APK pelo GitHub Actions

## 1. Subir o projeto no GitHub

Suba este pacote inteiro em um repositório.

A estrutura precisa ficar assim:

```txt
.github/workflows/build-apk.yml
android-motoboy/
painel-web/
firebase/
docs/
```

## 2. Rodar o Actions

1. Abra o repositório no GitHub.
2. Clique em **Actions**.
3. Escolha **Gerar APK Motoboy**.
4. Clique em **Run workflow**.
5. Aguarde terminar.
6. Baixe o artifact chamado:

```txt
pizzaria-motoboy-apk-debug
```

Dentro dele estará:

```txt
app-debug.apk
```

## 3. Instalar no celular

No Android do motoboy:

1. Baixe o APK.
2. Autorize instalação de fonte externa.
3. Instale.
4. Abra o app.
5. Coloque empresa ID, e-mail e senha do motoboy.
6. Clique em **Entrar / Salvar login**.
7. Clique em **Iniciar turno e GPS**.
8. Autorize localização e notificações.

## 4. Sobre Google Play

Não precisa publicar na Google Play para funcionar.

Mas o Android ainda exige:

- permissão de localização;
- notificação fixa durante rastreamento;
- app não ser fechado à força;
- bateria sem bloqueio agressivo no aparelho.

## 5. APK debug x APK final

Este pacote gera APK debug instalável para MVP e testes reais.

Para vender em escala, o ideal é gerar APK release assinado com chave própria da thIAguinho Soluções. Isso pode ser adicionado depois no GitHub Actions usando Secrets.
