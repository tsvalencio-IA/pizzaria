# Guia rápido — Ajuste de bateria para não matar o GPS (Android)

Este guia é para **uso real em rua**. Mesmo com Foreground Service, alguns celulares “matam” o app para economizar bateria.

> Objetivo: garantir que o motoboy **não perca o rastreamento** durante o turno.

## Checklist universal (faça em qualquer Android)

1. **Configurações > Apps > (App Motoboy) > Bateria**
   - Defina como **“Sem restrições”** / **“Não otimizar”** / **“Permitir em segundo plano”** (o nome muda por fabricante).
2. **Configurações > Apps > (App Motoboy) > Notificações**
   - Permitir **todas** (principalmente “notificação em primeiro plano / permanente”).
3. **Configurações > Localização**
   - Localização **Ligada**
   - “Precisão da localização do Google” (se existir) **Ligada**.
4. No app, iniciar turno e conferir a notificação fixa: **“Motoboy GPS ativo”**.

## Xiaomi / Redmi / Poco (MIUI / HyperOS)

1. **Configurações > Apps > Gerenciar apps > (App) > Economia de bateria**
   - Colocar **Sem restrições**.
2. **Segurança > Bateria > Economia de bateria do app**
   - Desligar otimização para o app.
3. **Segurança > Permissões > Inicialização automática**
   - Ativar o app (Auto-start).
4. **Tela de apps recentes**
   - “Travar” o app (ícone de cadeado) para ele não ser encerrado.

## Samsung (One UI)

1. **Configurações > Assistência do aparelho e bateria > Bateria > Limites de uso em segundo plano**
   - Remover o app de “Apps em suspensão” / “Suspensão profunda”.
2. **Configurações > Apps > (App) > Bateria**
   - Permitir **atividade em segundo plano** e usar **Sem restrição** (quando existir).
3. **Configurações > Apps > (App) > Notificações**
   - Garantir notificações habilitadas.

## Motorola

1. **Configurações > Bateria > Otimização de bateria**
   - Colocar o app como **Não otimizar**.
2. **Configurações > Apps > (App) > Bateria**
   - Permitir execução em segundo plano.

## OPPO / Realme / OnePlus (ColorOS / Realme UI / OxygenOS)

1. **Configurações > Bateria**
   - Desativar otimização agressiva para o app.
2. **Gerenciador de apps / Auto-iniciar**
   - Ativar auto-start para o app.
3. **Permitir execução em segundo plano** e **Sem restrições**.

## Huawei / Honor (EMUI)

1. **Configurações > Bateria > Inicialização de apps**
   - Desativar “Gerenciar automaticamente” e permitir:
     - Auto-iniciar
     - Execução em segundo plano
     - Execução em segundo plano após fechar

## Como confirmar em 30 segundos (teste rápido)

1. Iniciar turno.
2. Voltar para home e bloquear a tela por 2 minutos.
3. Abrir o painel do gestor e ver se o **updatedAt** continua atualizando.
4. Se parar, repetir ajustes de bateria acima.

thIAguinho Soluções — tecnologia sob medida.
