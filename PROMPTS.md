# Prompting — boas práticas (Cursor / Claude)

Guia para formular prompts ao trabalhar neste repositório com agentes de IA. Baseado em [Claude prompting best practices](https://platform.claude.com/docs/en/build-with-claude/prompt-engineering/claude-prompting-best-practices) e [Cursor prompt engineering best practices](https://forum.cursor.com/t/cursor-prompt-engineering-best-practices/1592).

> Regras do projeto (stack, camadas, commits, branches): ver `AGENTS.md`.
>
> As regras essenciais abaixo também estão em `.cursor/rules/prompting.mdc` (`alwaysApply: true`) — o agente as recebe automaticamente em todo chat.

---

## Princípios gerais

### Seja claro e direto

Trate o agente como alguém competente, mas **sem contexto** do projeto. Instruções vagas geram resultados vagos.

| Menos efetivo | Mais efetivo |
|---------------|--------------|
| `Criar endpoint de projetos` | `Adicionar GET /api/projects com paginação (page, size, sort) e filtro por status. Seguir camadas em @AGENTS.md.` |
| `Melhorar o código` | `Refatorar ProjectService: extrair validação de datas para método privado, sem alterar comportamento. Manter cobertura ≥ 70%.` |

**Regra de ouro:** se um colega novo não entenderia o pedido, o agente também não entenderá.

### Diga o que fazer (não só o que evitar)

| Menos efetivo | Mais efetivo |
|---------------|--------------|
| `Não use entidade JPA no controller` | `Expor DTOs nos endpoints; mapear entidade ↔ DTO no service ou mapper dedicado` |
| `Pode sugerir mudanças?` | `Implementar a correção no código e rodar mvn test` |

Para **implementação**, seja explícito: *implemente*, *adicione*, *corrija* — não *sugira* ou *pode*.

### Forneça contexto e motivo

Explique **por quê** quando ajudar o agente a priorizar:

```
Adicionar @ControllerAdvice global porque hoje erros de domínio retornam 500 genérico.
Padronizar resposta JSON com status, message e timestamp.
```

### Use exemplos quando o formato importa

Inclua 1–3 exemplos concretos (request/response, assinatura de método, commit) quando quiser formato ou estilo específico. Exemplos devem ser **relevantes** ao caso e **variados** o suficiente para não fixar um padrão errado.

### Estruture prompts longos

Em tarefas complexas, separe blocos (markdown ou XML):

```markdown
## Contexto
@AGENTS.md — requisitos de camadas e testes

## Arquivos
- Controller: @src/main/java/.../ProjectController.java
- Service: @src/main/java/.../ProjectService.java

## Tarefa
Implementar filtro por status na listagem paginada.

## Critérios de aceite
- page, size, sort via Spring Data
- filtro opcional ?status=
- testes unitários no service
```

Em contexto extenso: coloque **documentos e código no topo**, **instrução e pergunta no final** (melhora foco do modelo).

---

## Contexto no Cursor

O sucesso no Cursor depende de **gerenciamento de contexto** — cite arquivos específicos em vez de depender só do codebase inteiro.

### Preferir `@` em arquivos concretos

| Evitar | Preferir |
|--------|----------|
| `@codebase` sozinho | `@AGENTS.md` + `@pom.xml` + arquivo(s) da feature |
| Prompt sem referências | `@ProjectService.java` + `@ProjectController.java` |

`@codebase` é útil como complemento (ex.: buscar nome de função), não como única fonte de contexto.

### Rotule o papel de cada referência

Deixe claro o que cada `@` representa:

```
Seguindo @AGENTS.md (requisitos) e o padrão de @ProjectController.java (estilo),
implementar POST /api/projects no service e controller correspondentes.
```

### Reutilize contexto recorrente

Para fluxos repetidos (backend, testes, utilitários), monte um bloco fixo:

```
Regras: @AGENTS.md
Entidades/DTOs: @src/main/java/.../domain/
Utilitários existentes: verificar @.../util/ antes de criar métodos novos
```

### Antes de criar código novo

Instrua o agente a **reutilizar** o que já existe:

```
Antes de escrever helper ou client HTTP, verificar se já existe em @.../util/
ou no service relacionado. Não duplicar métodos existentes.
```

---

## Prompts adaptados a este projeto

### Nova feature (API)

```
Seguindo @AGENTS.md:
- Implementar [descrição] com controller fino, lógica no service, JPA no repository
- DTOs na API (sem expor entidade)
- Tratamento via exception handler global
- Testes unitários no service (meta ≥ 70% cobertura)
- Documentar endpoint no Swagger

Arquivos de referência: @[...]
```

### Correção de bug

```
Corrigir [comportamento] em @[arquivo].
Reproduzir com teste que falha, corrigir, garantir mvn test verde.
Não alterar comportamento fora do escopo.
```

### Refatoração

```
Refatorar @[arquivo] para [objetivo].
Sem mudança de comportamento externo.
Manter/atualizar testes existentes.
```

### Pergunta sobre o código

```
Investigar @[arquivo] e responder: [pergunta].
Não especular — ler o código antes de afirmar.
```

---

## Escopo e comportamento do agente

### Evitar over-engineering

Pedir explicitamente quando quiser diff mínimo:

```
Escopo mínimo: apenas o necessário para [tarefa].
Não refatorar código adjacente, não adicionar abstrações extras,
não criar arquivos temporários sem limpar ao final.
```

### Ações destrutivas

Para operações irreversíveis (force push, delete em massa, drop), o agente deve **confirmar** — não assumir permissão implícita.

### Verificação

Sempre que possível, incluir no prompt:

```
Verificar com mvn test (e mvn compile se alterou dependências).
```

---

## Checklist rápido do prompt

- [ ] Objetivo claro e acionável (implementar / corrigir / explicar)
- [ ] `@AGENTS.md` citado quando envolver código ou convenções
- [ ] Arquivos relevantes referenciados com `@`
- [ ] Critérios de aceite ou restrições explícitos
- [ ] Escopo delimitado (o que **não** fazer, se necessário)
- [ ] Comando de verificação (`mvn test`, etc.)

---

## Implementação por fases (portfólio)

Sempre citar `@AGENTS.md` (regras de negócio e pacotes). **Uma fase por prompt** — não implementar tudo de uma vez.

### Fase 1 — Domínio e infraestrutura

```
Seguindo @AGENTS.md, implementar Fase 1:
- Enums: ProjectStatus, RiskLevel, MemberRole
- Entidades atualizadas (Project.members, Member.externalId/role)
- domain.exception (BusinessException + específicas)
- domain.rule isoladas (risco, transição de status, exclusão, alocação)
- Repositories, DTOs, MapStruct mappers
- Config (Security, OpenAPI)
- Mock API externa de membros (/api/external/members)
Verificar: mvn compile
```

### Fase 2 — Services e API de projetos

```
Seguindo @AGENTS.md, implementar Fase 2:
- ProjectService: CRUD, update status, delete com regras
- Member sync via client da API externa
- Controllers + GlobalExceptionHandler
- Paginação e filtro por status na listagem
Verificar: mvn test
```

### Fase 3 — Relatório, testes e docs

```
Seguindo @AGENTS.md, implementar Fase 3:
- PortfolioReportService + endpoint GET /api/portfolio/report
- Testes unitários das rules e services (≥ 70% em service*)
- README.md com setup completo
Verificar: mvn verify
```

### Regras críticas (não esquecer)

- `RiskLevel` é **calculado**, não persistido
- Status: sequência fixa; `CANCELLED` a qualquer momento
- Membros: só via API externa mock; alocação só `EMPLOYEE`
- Exclusão bloqueada em `STARTED`, `IN_PROGRESS`, `CLOSED`

---

## Referências

- [Claude — Prompting best practices](https://platform.claude.com/docs/en/build-with-claude/prompt-engineering/claude-prompting-best-practices)
- [Cursor Forum — Prompt engineering best practices](https://forum.cursor.com/t/cursor-prompt-engineering-best-practices/1592)
