# Checklist de teste real na pizzaria

## Antes do teste

- [ ] Firebase Auth ativado por e-mail/senha.
- [ ] Realtime Database criado.
- [ ] Regras aplicadas.
- [ ] Admin criado no Firebase Auth.
- [ ] Motoboys criados no Firebase Auth.
- [ ] UIDs dos motoboys cadastrados em `/drivers/{uid}/profile`.
- [ ] Todos os motoboys que vão testar estão com `profile.active: true`.
- [ ] UID do admin cadastrado em `/roles/{uid}: "admin"`.
- [ ] `firebase-config.js` preenchido no painel.
- [ ] `Config.kt` preenchido no Android.
- [ ] APK gerado pelo GitHub Actions.

## Teste do motoboy

- [ ] Instalar APK.
- [ ] Abrir app.
- [ ] Entrar com e-mail/senha.
- [ ] Permitir localização.
- [ ] Permitir notificação no Android 13 ou superior.
- [ ] Confirmar que a localização/GPS do aparelho está ligada.
- [ ] Iniciar turno.
- [ ] Confirmar notificação fixa.
- [ ] Andar com o celular por alguns metros.
- [ ] Confirmar no Firebase se `/location` está atualizando.
- [ ] Bloquear tela e aguardar 2 minutos.
- [ ] Confirmar se continua atualizando.

## Teste do gestor

- [ ] Abrir `painel-web/admin.html` hospedado em HTTPS.
- [ ] Entrar com e-mail do gestor.
- [ ] Ver motoboy online.
- [ ] Conferir mapa.
- [ ] Criar pedido.
- [ ] Atribuir pedido ao motoboy.
- [ ] Marcar pedido como entregue.
- [ ] Conferir alerta quando pedido aguardando passar de 20 minutos.

## Pontos de atenção

Alguns celulares Android têm economia de bateria agressiva. Se o GPS parar, abrir configurações do aparelho e permitir uso de bateria sem restrição para o app.

Se o app avisar que a localização está desligada, ligar a localização do aparelho antes de iniciar o turno.

Se o app avisar que a notificação foi negada, liberar a notificação nas configurações do Android antes de iniciar o turno.
