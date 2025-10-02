# Trilha Auditoria Client

Aplicação Spring Boot (Java 21) que expõe uma API REST para simular o consumo da Trilha Auditoria Serviço.

## Como executar

```bash
mvn spring-boot:run
```

A porta padrão configurada é `9090`. O endpoint remoto pode ser ajustado pela propriedade `trilhaapi.client.base-url`.

## Endpoints

- `POST /api/v1/client/trilhas` — Encaminha uma trilha de auditoria para a API remota.
- `GET /api/v1/client/trilhas` — Consulta trilhas de auditoria na API remota.
