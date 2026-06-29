# portfolio-api-java — Guia do projeto

Documento de referência para agentes e contribuidores. Define convenções e expectativas deste repositório enquanto o projeto evolui.

## Identidade

| Item | Valor |
|------|-------|
| Repositório | `portfolio-api-java` |
| Artifact Maven | `portfolio-api-java` |
| GroupId | `com.scryng` |
| Package base | `com.scryng.portfolio` |
| Stack | Java 17, Spring Boot 3.x, Maven, JPA/Hibernate, PostgreSQL |

---

## Requisitos do projeto

Regras mandatórias da implementação.

### Stack e persistência

- **Spring Boot** — framework base da aplicação
- **JPA + Hibernate** — camada de persistência
- **PostgreSQL** — banco de dados (local via `docker-compose.yml`)

### Arquitetura

- **MVC** — Model (entidades/DTOs), View (JSON via REST), Controller (endpoints HTTP)
- **Camadas separadas** — controller, service e repository com responsabilidades distintas
- **Clean Code e SOLID** — código legível, coesão por responsabilidade, dependências bem definidas

Diretrizes por camada:

1. **Controllers finos** — recebem request, delegam, devolvem response
2. **Regra de negócio no service** — não no controller nem no repository
3. **Repository** — apenas acesso a dados, sem lógica de negócio

### API

- **DTOs e mapeamento** — requests/responses via DTOs; entidades JPA nunca expostas nos endpoints; mapper dedicado (manual ou MapStruct)
- **Swagger/OpenAPI** — documentação automática dos endpoints (SpringDoc OpenAPI)
- **Tratamento global de exceções** — handler centralizado (`@ControllerAdvice`) para respostas de erro consistentes
- **Paginação e filtros** — listagem de projetos com page/size/sort e filtros relevantes

### Segurança

- **Spring Security** — autenticação básica com usuário/senha hardcoded ou em memória (dev/protótipo)

### Testes

- **Testes unitários** — toda regra de negócio relevante (services) deve ter teste
- **Cobertura mínima de 70%** — nas classes de regra de negócio (services)

---

## Branches

Padrão: **GitHub Flow** (branch principal estável + branches de curta duração).

Formato obrigatório: `{prefix}/{description}` — tudo em **minúsculas**, **kebab-case**, **inglês**, verbo no **infinitivo**, curto e descritivo.

| Prefix | Uso | Exemplo |
|--------|-----|---------|
| `main` | Sempre deployável; protegida; merge via PR ou revisão local | — |
| `feat/` | Nova funcionalidade | `feat/add-member-sync` |
| `fix/` | Correção de bug | `fix/handle-null-portfolio` |
| `chore/` | Manutenção (deps, config, CI) | `chore/update-spring-boot` |
| `docs/` | Apenas documentação | `docs/add-api-guide` |
| `test/` | Cobertura ou ajustes de teste | `test/cover-member-service` |

Regras:

- Sempre `{prefix}/{description}` — ex.: `feat/add-jwt-auth`, nunca `feat/AddJwtAuth` ou `feat/nova-coisa`
- Descrição curta (2–4 palavras), verbo no infinitivo: `add`, `fix`, `update`, `remove`, `refactor`
- Branch partindo sempre de `main` atualizada
- Deletar branch após merge
- Evitar branches long-lived sem necessidade

---

## Commits

Padrão: **[iuricode/padroes-de-commits](https://github.com/iuricode/padroes-de-commits)** — Conventional Commits com emoji no início. Mensagens em **inglês**.

```
<emoji> <tipo>: <descrição curta>

[corpo opcional — impacto, motivo, instruções]

[rodapé opcional — Reviewed-by, Refs #123]
```

### Tipos

| Tipo | Quando usar | Emoji |
|------|-------------|-------|
| `feat` | Novo recurso | ✨ `:sparkles:` |
| `fix` | Correção de bug | 🐛 `:bug:` |
| `docs` | Documentação | 📚 `:books:` |
| `test` | Testes | 🧪 `:test_tube:` |
| `refactor` | Refatoração sem mudar comportamento | ♻️ `:recycle:` |
| `perf` | Melhoria de performance | ⚡ `:zap:` |
| `style` | Formatação, lint (sem mudança de lógica) | 👌 `:ok_hand:` |
| `build` | Build, deps, Maven | 📦 `:package:` |
| `chore` | Manutenção, config | 🔧 `:wrench:` |
| `ci` | Integração contínua | 🧱 `:bricks:` |
| `cleanup` | Limpeza de código morto | 🧹 `:broom:` |
| `remove` | Remoção de arquivo ou funcionalidade | 🗑️ `:wastebasket:` |
| `raw` | Config, dados, parâmetros | 🗃️ `:card_file_box:` |

Referência completa de emojis: [tabela no repositório](https://github.com/iuricode/padroes-de-commits#padr%C3%B5es-de-emojis-).

### Exemplos

```
:sparkles: feat: add project listing endpoint
:bug: fix: validate end date before start
:books: docs: update README with local setup
:test_tube: test: cover status transition rules
:wrench: chore: bump spring boot to 3.5.16
:recycle: refactor: extract portfolio validation
```

### Regras

- Formato: `{emoji} {tipo}: {descrição}` — emoji representando a intenção do commit
- Subject curto: até ~4 palavras no título; detalhes no corpo
- Imperativo, lowercase, sem ponto final: `add`, não `added` ou `adds`
- Um commit = uma mudança lógica
- Não commitar `target/`, `.idea/`, arquivos de IDE ou secrets

---

## Pull requests (quando aplicável)

### Título

- Em **português**, verbo no **infinitivo**, primeira letra **maiúscula**
- Descreve de forma **geral** o conjunto de mudanças da PR — não o detalhe de um único commit
- **Atualizar o título** quando o escopo da PR crescer, para refletir o que ela passa a englobar

Exemplos: `Adicionar endpoint de listagem de projetos`, `Corrigir validação de datas`, `Configurar autenticação JWT`

> Commits seguem o padrão iuricode (inglês + emoji); o título da PR é independente e em português.

### Regras

- Descrever **o quê** e **por quê**, não só o diff
- PRs pequenos e focados > PRs gigantes
- Rodar `mvn test` antes de abrir ou mergear

---

## Convenções Java / Spring

| Item | Convenção |
|------|-----------|
| Classes | PascalCase |
| Métodos / variáveis | camelCase |
| Constantes | UPPER_SNAKE_CASE |
| Enums | UPPER_SNAKE_CASE |
| Packages | lowercase, sem hífen |
| Orçamento / dinheiro | `BigDecimal` |
| Datas sem hora | `LocalDate` |
| Injeção de dependência | construtor (preferir `@RequiredArgsConstructor` com Lombok) |

---

## Configuração local

```bash
# Java 17 (macOS Homebrew)
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Verificar
java -version
mvn -version
```

---

## Comandos úteis

```bash
docker compose up -d   # subir PostgreSQL local
mvn spring-boot:run    # subir aplicação
mvn test               # rodar testes
mvn clean verify       # build completo
```

Adicionar aqui comandos específicos (Docker, etc.) conforme o projeto for ganhando infraestrutura.

---

## O que versionar

| Versionar | Ignorar |
|-----------|---------|
| Código-fonte | `target/` |
| `pom.xml` | `.idea/`, `.vscode/` (salvo settings compartilhados) |
| `README.md`, `AGENTS.md`, `PROMPTS.md`, `.cursor/rules/` | Logs, `.class` |
| `docker-compose.yml`, `.env.example` (se existir) | Credenciais reais (`.env`) |
| Collection Postman (se existir) | |

---

## Checklist antes de commitar

- [ ] Código compila (`mvn compile`)
- [ ] Testes passam (`mvn test`)
- [ ] Cobertura ≥ 70% nas classes de service (quando aplicável)
- [ ] Sem arquivos acidentais (IDE, `target/`, secrets)
- [ ] Mensagem de commit segue padrão iuricode
- [ ] Branch nomeada corretamente

---

## Evolução deste documento

Atualizar conforme o projeto crescer:

- Adicionar estrutura de pastas quando estabilizar
- Documentar regras de negócio quando forem definidas
- Registrar decisões arquiteturais relevantes

Não duplicar o que já está no `README.md`: README = como rodar e apresentar; AGENTS.md = como contribuir e manter consistência.

---

## Prompting (Cursor / IA)

Boas práticas para formular prompts neste repositório estão em **[PROMPTS.md](./PROMPTS.md)**. Regras essenciais do agente também em **`.cursor/rules/prompting.mdc`** (`alwaysApply: true`).

Resumo:

- Seja **claro e direto** — objetivo, arquivos `@` relevantes e critérios de aceite
- Prefira **`@arquivo` específico** a `@codebase` sozinho; cite `@AGENTS.md` em tarefas de código
- Peça **implementação** quando quiser código (`implementar`, `corrigir`), não apenas sugestões
- Delimite **escopo** para evitar refactors ou arquivos extras não solicitados
- Inclua **verificação** (`mvn test`) no pedido quando aplicável

Referências: [Claude prompting best practices](https://platform.claude.com/docs/en/build-with-claude/prompt-engineering/claude-prompting-best-practices) · [Cursor forum](https://forum.cursor.com/t/cursor-prompt-engineering-best-practices/1592)
