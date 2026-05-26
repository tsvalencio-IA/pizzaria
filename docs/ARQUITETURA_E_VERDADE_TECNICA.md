# Arquitetura e verdade técnica

## Objetivo

A pizzaria precisa saber onde estão os motoboys para decidir se mantém prazo normal de entrega ou se aumenta a promessa de tempo quando não houver motoboy disponível.

## Solução correta

- App Android nativo para motoboy.
- Painel web para gestor.
- Firebase Auth para login.
- Firebase Realtime Database para localização ao vivo.
- GitHub Actions para gerar APK.
- Leaflet/OpenStreetMap para mapa gratuito.

## Por que Android nativo?

PWA e navegador não garantem rastreamento confiável quando a tela bloqueia, quando o navegador é minimizado ou quando o Android economiza bateria.

O app nativo usa Foreground Service, que mostra uma notificação fixa e permite continuar enviando GPS enquanto o turno estiver ativo.

## Como o tempo é calculado no MVP

O painel calcula uma estimativa simples de retorno:

1. Pega a última posição do motoboy.
2. Calcula distância em linha reta até a pizzaria.
3. Usa velocidade real se o GPS informou.
4. Se não houver velocidade, usa média padrão de 32 km/h.

Isso é barato e sem API paga.

## O que não tem custo inicial alto

- Não usa Google Maps pago.
- Não usa OpenAI API.
- Não usa servidor próprio.
- Não usa Cloud Functions.
- Não exige Play Store.

## Próxima evolução

Depois de validar com a pizzaria:

- roteirização real por endereço;
- ordem de entregas por motoboy;
- alerta sonoro para pedido acima de 20 minutos;
- histórico por dia;
- relatório de produtividade;
- assinatura APK release;
- envio de WhatsApp para cliente com prazo atualizado.
