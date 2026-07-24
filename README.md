# fiapx-processor-service

**Responsabilidade:** Processamento assíncrono de vídeos — extrai frames e gera arquivo ZIP.

Hackathon SOAT — Fase 5 | FIAP X | Microsserviço 2 de 2

---

## Stack

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Mensageria | RabbitMQ |
| Observabilidade | Micrometer + Prometheus |
| Testes | JUnit 5 + JaCoCo |
| CI/CD | GitHub Actions |

---

## Repositórios relacionados

| Repositório | Responsabilidade |
|---|---|
| [fiapx-api-service](https://github.com/ricartefelipe/fiapx-api-service) | API, auth, upload, status |
| [fiapx-processor-service](https://github.com/ricartefelipe/fiapx-processor-service) | Processamento assíncrono de vídeos |

---

## Como rodar localmente

Requer RabbitMQ ativo (via `docker-compose.infra.yml` do repositório `fiapx-api-service`).

```bash
./mvnw spring-boot:run
```

O serviço sobe na porta **8081**.

---

## Testes

```bash
./mvnw -Pci clean verify
```

---

## CI/CD

- Pipeline em `.github/workflows/ci.yml`
- GitFlow documentado em `docs/GITFLOW.md`
