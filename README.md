# 🤖 Aura AI

Projeto acadêmico desenvolvido em **Java** que simula uma solução de inteligência conversacional para análise de reuniões comerciais.

A **Aura AI** processa transcrições de reuniões e identifica sentimento do cliente, risco de churn, oportunidades comerciais, concorrentes mencionados, produtos TOTVS citados e alertas.

A solução é composta por uma **API RESTful (Spring Boot)** conectada ao **PostgreSQL** e um **front-end Java Swing** que consome essa API via HTTP.

## 📑 Sumário

1. [Funcionalidades](#-funcionalidades)
2. [Arquitetura](#️-arquitetura)
3. [Regras de negócio da análise](#-regras-de-negócio-da-análise)
4. [Banco de dados](#️-banco-de-dados)
5. [Configuração e execução](#️-configuração-e-execução)
6. [API REST](#-api-rest)
7. [Front-end Swing](#️-front-end-swing)
8. [Testes](#-testes)
9. [Solução de problemas](#-solução-de-problemas)
10. [Tecnologias](#️-tecnologias)
11. [Desenvolvedores](#-desenvolvedores)

---

## 📌 Funcionalidades

- CRUD de clientes e de produtos TOTVS
- Registro de reuniões e de suas transcrições
- Análise automática da reunião: sentimento, risco de churn, oportunidade, concorrente, persona, budget e produtos mencionados
- Geração automática de alertas (`CHURN_RISK`, `UPSELL`, `CONCORRENTE`) e resolução de alertas
- API RESTful com validação de entrada e respostas de erro padronizadas
- Persistência em PostgreSQL com transações (commit e rollback)
- Interface gráfica em Java Swing

---

## 🏗️ Arquitetura

```text
Swing (view)
   ↓  HTTP / JSON
ApiClient (client)
   ↓
Controller REST  ←→  DTO
   ↓
Service (validações e regras)
   ↓
DAO (JDBC)
   ↓
ConnectionFactory
   ↓
PostgreSQL
```

Pacote base: `br.com.fiap.auraai`

```text
src/main
├── java/br/com/fiap/auraai
│   ├── AuraAiApplication   Ponto de entrada da API (Spring Boot)
│   ├── app                 Main do front-end Swing
│   ├── client              Clientes HTTP usados pelo Swing
│   ├── controller          Controllers REST
│   ├── dto                 Objetos de entrada e saída da API (records)
│   ├── service             Regras de negócio e validações
│   ├── dao                 Acesso ao banco (JDBC)
│   ├── domain.model        Entidades e enums de domínio
│   ├── exception           Exceções da aplicação e tratamento global
│   ├── factory             ConnectionFactory (Factory + Singleton)
│   └── view                Telas Swing
└── resources               application.properties
src/test                    Testes JUnit 5 e MockMvc
db                          schema.sql e script de migração
docs                        Collection do Postman
```

### Responsabilidade de cada camada

| Camada | Responsabilidade |
|---|---|
| **Controller** | Recebe a requisição HTTP, valida o corpo (`@Valid`) e devolve o DTO com o status correto |
| **Service** | Regras de negócio, validações e tradução dos erros do banco em exceções da aplicação |
| **DAO** | SQL com `PreparedStatement`, transações e mapeamento de `ResultSet` para o modelo |
| **Model** | Entidades com validação nos setters e enums de domínio |
| **Exception** | `AuraAIException` e subclasses, convertidas em HTTP pelo `GlobalExceptionHandler` |

### Padrões de projeto

DAO, Factory e Singleton (`ConnectionFactory`), DTO, injeção de dependência (Spring) e arquitetura em camadas.

### Tratamento de exceções

| Exceção | HTTP | Quando |
|---|---|---|
| `RecursoNaoEncontradoException` | 404 | Registro inexistente |
| `ConflitoException` | 409 | Duplicidade ou vínculo existente |
| `RegraNegocioException` / `IllegalStateException` | 422 | Requisição válida que viola uma regra de negócio |
| `IllegalArgumentException` | 400 | Validação dos modelos (setters) |
| `ErroPersistenciaException` | 500 | Falha inesperada no banco |

Os erros do PostgreSQL são traduzidos pelo `SQLState`: `23503` (chave estrangeira) e `23505` (duplicidade) viram **409**, `23514` (check) e `22001` (texto longo demais) viram **422**.

---

## 🧠 Regras de negócio da análise

A lógica está na classe `AnaliseIA` e usa palavras-chave do texto da transcrição (sem diferenciar maiúsculas de minúsculas).

| Condição no texto | Sentimento | Risco de churn | Oportunidade |
|---|---|---|---|
| contém `insatisfeito`, `problema` ou `cancelar` | `NEGATIVO` | 0.85 | 0.25 |
| contém `interesse`, `contratar` ou `expandir` | `POSITIVO` | 0.15 | 0.90 |
| nenhuma das anteriores | `NEUTRO` | 0.30 | 0.40 |

- **Concorrente:** `SAP` se o texto contém "sap"; `Senior` se contém "senior"; caso contrário, nenhum.
- **Persona:** `Decisor`. **Budget estimado:** `50000.00`.
- **Produtos:** cada produto cadastrado cujo nome aparece no texto é registrado (uma vez por análise), com relevância `0.90` e tipo de menção `ATUAL`.
- **Alertas gerados:**

| Alerta | Condição | Prioridade |
|---|---|---|
| `CHURN_RISK` | risco de churn ≥ 0.80 | `ALTA` |
| `UPSELL` | oportunidade ≥ 0.80 | `ALTA` |
| `CONCORRENTE` | concorrente identificado | `MEDIA` |

- Ao concluir a análise, a reunião passa para o status `PROCESSADA` **na mesma transação** em que a análise é gravada.

### Enums do domínio

| Enum | Valores |
|---|---|
| `StatusReuniao` | `PENDENTE`, `PROCESSADA` |
| `Sentimento` | `POSITIVO`, `NEGATIVO`, `NEUTRO` |
| `TipoAlerta` | `CHURN_RISK`, `UPSELL`, `CONCORRENTE` |
| `PrioridadeAlerta` | `ALTA`, `MEDIA`, `BAIXA` |
| `StatusAlerta` | `ABERTO`, `RESOLVIDO` |
| `TipoMencao` | `ATUAL`, `POTENCIAL` |

---

## 🗃️ Banco de Dados

Banco: `aura_ai` (PostgreSQL). Script completo em [`db/schema.sql`](db/schema.sql).

| Tabela | Principais campos |
|---|---|
| `cliente` | `id`, `nome`, `cnpj` (único), `segmento`, `email` |
| `reuniao` | `id`, `titulo`, `data_hora`, `duracao_min`, `responsavel`, `status`, `cliente_id` |
| `transcricao` | `id`, `texto_bruto`, `qtd_palavras`, `idioma`, `reuniao_id` (único) |
| `produto_totvs` | `id`, `nome`, `categoria`, `versao`, `descricao` |
| `analise_ia` | `id`, `sentimento`, `risco_churn`, `oportunidade`, `persona`, `budget`, `concorrente`, `reuniao_id` |
| `alerta` | `id`, `tipo`, `descricao`, `prioridade`, `status`, `analise_id` |
| `analise_produto` | `analise_id`, `produto_id` (chave composta), `contexto`, `relevancia`, `tipo_mencao` |

```text
CLIENTE
   ↓ 1:N
REUNIAO
   ├── TRANSCRICAO   (1:1)
   └── ANALISE_IA    (1:N)
          ├── ALERTA            (1:N)
          └── ANALISE_PRODUTO   (N:M com PRODUTO_TOTVS)
```

### Regras de integridade

- `transcricao`, `analise_ia`, `alerta` e `analise_produto` são excluídos **em cascata** com a reunião ou a análise a que pertencem.
- **Cliente** com reuniões e **produto** já identificado em análises **não podem ser excluídos** (a API responde `409`).
- `CHECK` no banco: `duracao_min > 0`, status da reunião (`PENDENTE`/`PROCESSADA`), `risco_churn`, `oportunidade` e `relevancia` entre 0 e 1.
- Índices nas chaves estrangeiras para acelerar consultas e exclusões.

Se o banco já existia antes desta versão, rode [`db/migracao_banco_existente.sql`](db/migracao_banco_existente.sql) no DBeaver.

---

## ⚙️ Configuração e Execução

### Requisitos

- Java 17 ou superior
- Maven 3.9+ (ou o Maven embutido no IntelliJ)
- PostgreSQL

### 1. Criar o banco

```sql
CREATE DATABASE aura_ai;
```

Conecte-se ao banco `aura_ai` (por exemplo, pelo DBeaver) e execute `db/schema.sql`.

### 2. Configurar a conexão

A senha **não fica no código**. Use uma das opções (a variável de ambiente tem prioridade):

| Opção | Como |
|---|---|
| Variáveis de ambiente | `DB_URL`, `DB_USER`, `DB_PASSWORD` |
| Arquivo `db.properties` | Copie `db.properties.example` para `db.properties` na raiz do projeto e preencha a senha (o arquivo é ignorado pelo Git) |

Valores padrão: `DB_URL=jdbc:postgresql://localhost:5432/aura_ai` e `DB_USER=postgres`. A senha não tem padrão.

### 3. Subir a API

```bash
mvn spring-boot:run
```

ou execute a classe `br.com.fiap.auraai.AuraAiApplication` no IntelliJ. A API fica em `http://localhost:8080` (porta em `application.properties`, chave `server.port`).

### 4. Abrir o front-end Swing

Com a API em execução:

```bash
mvn compile exec:java
```

ou execute a classe `br.com.fiap.auraai.app.Main` no IntelliJ. Para apontar para outra URL, defina a variável de ambiente `AURA_API_URL` (padrão `http://localhost:8080`).

---

## 🌐 API REST

**Base URL:** `http://localhost:8080` · **Formato:** JSON (`Content-Type: application/json`) · **Datas:** ISO 8601 (`2026-10-20T14:30:00`)

A collection do Postman está em [`docs/AuraAI.postman_collection.json`](docs/AuraAI.postman_collection.json). Importe no Postman, suba a API e use **Run collection**: as pastas 1 a 5 percorrem o fluxo completo (cada `POST` guarda o ID criado para os requests seguintes), a pasta 6 traz exemplos de erro (400, 404, 409) e a pasta 7 apaga os dados criados.

### Visão geral dos endpoints

| Recurso | Verbo | URI | Sucesso | Erros possíveis |
|---|---|---|---|---|
| Clientes | GET | `/clientes` | 200 | 500 |
| | GET | `/clientes/{id}` | 200 | 400, 404 |
| | POST | `/clientes` | 201 | 400, 409 (CNPJ duplicado) |
| | PUT | `/clientes/{id}` | 200 | 400, 404, 409 |
| | DELETE | `/clientes/{id}` | 204 | 404, 409 (possui reuniões) |
| Produtos | GET | `/produtos` | 200 | 500 |
| | GET | `/produtos/{id}` | 200 | 400, 404 |
| | POST | `/produtos` | 201 | 400 |
| | PUT | `/produtos/{id}` | 200 | 400, 404 |
| | DELETE | `/produtos/{id}` | 204 | 404, 409 (usado em análises) |
| Reuniões | GET | `/reunioes` | 200 | 500 |
| | GET | `/reunioes/{id}` | 200 | 400, 404 |
| | POST | `/reunioes` | 201 | 400, 404 (cliente inexistente) |
| | PUT | `/reunioes/{id}` | 200 | 400, 404 |
| | DELETE | `/reunioes/{id}` | 204 | 404 |
| Transcrição | GET | `/reunioes/{id}/transcricao` | 200 | 404 |
| | POST | `/reunioes/{id}/transcricao` | 201 | 400, 404, 409 (já existe) |
| | PUT | `/reunioes/{id}/transcricao` | 200 | 400, 404 |
| | DELETE | `/reunioes/{id}/transcricao` | 204 | 404 |
| Análises | POST | `/reunioes/{id}/analises` | 201 | 404, 422 (sem transcrição) |
| | GET | `/analises` | 200 | 500 |
| | GET | `/analises/{id}` | 200 | 400, 404 |
| | GET | `/analises/{id}/alertas` | 200 | 404 |
| | PATCH | `/analises/{id}/alertas/{alertaId}/resolver` | 200 | 404 |
| | PUT | `/analises/{id}` | 200 | 400, 404 |
| | DELETE | `/analises/{id}` | 204 | 404 |

### Significado dos códigos de status

| Código | Quando |
|---|---|
| `200 OK` | Consulta ou atualização realizada |
| `201 Created` | Registro criado (ou análise gerada) |
| `204 No Content` | Exclusão realizada, sem corpo na resposta |
| `400 Bad Request` | Campos inválidos, JSON malformado ou ID que não é numérico |
| `404 Not Found` | Recurso, cliente, reunião, análise, alerta ou transcrição inexistente |
| `405 Method Not Allowed` | Verbo HTTP não suportado pela URI |
| `409 Conflict` | CNPJ duplicado, transcrição já existente ou exclusão bloqueada por vínculo |
| `415 Unsupported Media Type` | Corpo enviado sem `application/json` |
| `422 Unprocessable Entity` | Regra de negócio violada (ex.: analisar reunião sem transcrição) |
| `500 Internal Server Error` | Erro inesperado ou falha de conexão com o banco |

### Formato padrão de erro

Todas as respostas de erro têm o mesmo formato. Erros de validação trazem **todas** as mensagens juntas em `mensagem`.

```json
{
  "timestamp": "2026-10-20T14:30:00.123",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Cliente não encontrado.",
  "caminho": "/clientes/99"
}
```

### Clientes

**Campos de entrada (`POST` e `PUT`)**

| Campo | Tipo | Regra |
|---|---|---|
| `nome` | texto | obrigatório, até 150 caracteres |
| `cnpj` | texto | obrigatório, formato `00.000.000/0000-00` (pontuação opcional), único |
| `segmento` | texto | obrigatório, até 100 caracteres |
| `email` | texto | obrigatório, e-mail válido, até 150 caracteres |

**Exemplo: `POST /clientes`**

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Armor Solutions",
    "cnpj": "12.345.678/0001-90",
    "segmento": "Desenvolvimento de Jogos",
    "email": "contato@armorsolutions.com"
  }'
```

Resposta `201`:

```json
{
  "id": 1,
  "nome": "Armor Solutions",
  "cnpj": "12.345.678/0001-90",
  "segmento": "Desenvolvimento de Jogos",
  "email": "contato@armorsolutions.com"
}
```

Erros comuns: `400` com `"O nome do cliente é obrigatório."`, `409` com `"Já existe um cliente cadastrado com este CNPJ."` e, no `DELETE`, `409` com `"Não é possível excluir: o cliente possui reuniões vinculadas."`.

### Produtos

**Campos de entrada (`POST` e `PUT`)**

| Campo | Tipo | Regra |
|---|---|---|
| `nome` | texto | obrigatório, até 150 caracteres |
| `categoria` | texto | obrigatório, até 100 caracteres |
| `versao` | texto | obrigatório, até 50 caracteres |
| `descricao` | texto | obrigatório |

**Exemplo: `POST /produtos`**

```json
{
  "nome": "Protheus",
  "categoria": "ERP",
  "versao": "12.1",
  "descricao": "Sistema integrado de gestão empresarial."
}
```

Resposta `201`: o mesmo objeto acrescido de `"id"`. O `DELETE` retorna `409` se o produto já foi identificado em alguma análise.

> O nome do produto é usado para encontrá-lo no texto das transcrições. Cadastre os produtos **antes** de rodar a análise.

### Reuniões

**Campos de entrada (`POST` e `PUT`)**

| Campo | Tipo | Regra |
|---|---|---|
| `titulo` | texto | obrigatório, até 150 caracteres |
| `dataHora` | data e hora | obrigatório, ISO 8601 |
| `duracaoMin` | inteiro | mínimo 1 |
| `responsavel` | texto | obrigatório, até 150 caracteres |
| `clienteId` | número | obrigatório, cliente precisa existir (senão `404`) |
| `status` | texto | opcional; `PENDENTE` ou `PROCESSADA`. No `PUT`, se omitido, mantém o status atual. No `POST`, a reunião nasce `PENDENTE` |

**Exemplo: `POST /reunioes`**

```json
{
  "titulo": "Projeto TOTVS - Armor Solutions",
  "dataHora": "2026-10-20T14:30:00",
  "duracaoMin": 60,
  "responsavel": "Matheus",
  "clienteId": 1
}
```

Resposta `201`:

```json
{
  "id": 1,
  "titulo": "Projeto TOTVS - Armor Solutions",
  "dataHora": "2026-10-20T14:30:00",
  "duracaoMin": 60,
  "responsavel": "Matheus",
  "status": "PENDENTE",
  "cliente": {
    "id": 1,
    "nome": "Armor Solutions",
    "cnpj": "12.345.678/0001-90",
    "segmento": "Desenvolvimento de Jogos",
    "email": "contato@armorsolutions.com"
  }
}
```

Excluir uma reunião remove também sua transcrição e suas análises.

### Transcrição

Cada reunião possui **no máximo uma** transcrição.

| Campo | Tipo | Regra |
|---|---|---|
| `textoBruto` | texto | obrigatório |
| `idioma` | texto | obrigatório, até 20 caracteres (ex.: `pt-BR`) |

**Exemplo: `POST /reunioes/1/transcricao`**

```json
{
  "textoBruto": "A Armor Solutions demonstrou interesse em contratar o Protheus para melhorar a gestão da empresa. A organização pretende expandir suas operações e também informou que está avaliando o SAP.",
  "idioma": "pt-BR"
}
```

Resposta `201` (a quantidade de palavras é calculada pelo servidor):

```json
{
  "id": 1,
  "textoBruto": "A Armor Solutions demonstrou interesse ...",
  "qtdPalavras": 29,
  "idioma": "pt-BR",
  "reuniaoId": 1
}
```

- `POST` em reunião que já tem transcrição retorna `409`.
- `PUT` substitui o texto e o idioma e recalcula `qtdPalavras`. Análises já geradas **não** são recalculadas; rode uma nova análise se necessário.
- `GET` e `DELETE` retornam `404` se a reunião não tiver transcrição.

### Análises

**Gerar a análise: `POST /reunioes/{id}/analises`** (sem corpo)

Lê a transcrição da reunião, aplica as [regras de negócio](#-regras-de-negócio-da-análise), grava a análise, os alertas e os produtos identificados, e marca a reunião como `PROCESSADA`. Pode ser executada mais de uma vez; cada execução cria uma nova análise.

- `404` se a reunião não existir
- `422` com `"A reunião não possui transcrição."` se não houver transcrição

Resposta `201`:

```json
{
  "id": 1,
  "sentimento": "POSITIVO",
  "riscoChurn": 0.15,
  "oportunidade": 0.9,
  "persona": "Decisor",
  "budget": 50000.00,
  "concorrente": "SAP",
  "reuniao": {
    "id": 1,
    "titulo": "Projeto TOTVS - Armor Solutions",
    "dataHora": "2026-10-20T14:30:00",
    "duracaoMin": 60,
    "responsavel": "Matheus",
    "status": "PROCESSADA",
    "cliente": { "id": 1, "nome": "Armor Solutions", "cnpj": "12.345.678/0001-90", "segmento": "Desenvolvimento de Jogos", "email": "contato@armorsolutions.com" }
  },
  "alertas": [
    { "id": 1, "tipo": "UPSELL", "descricao": "Cliente demonstrou oportunidade comercial.", "prioridade": "ALTA", "status": "ABERTO" },
    { "id": 2, "tipo": "CONCORRENTE", "descricao": "Concorrente mencionado na reunião: SAP", "prioridade": "MEDIA", "status": "ABERTO" }
  ],
  "produtosIdentificados": [
    {
      "produto": { "id": 1, "nome": "Protheus", "categoria": "ERP", "versao": "12.1", "descricao": "Sistema integrado de gestão empresarial." },
      "contexto": "Produto mencionado durante a reunião comercial.",
      "relevancia": 0.9,
      "tipoMencao": "ATUAL"
    }
  ]
}
```

> Campos sem valor (por exemplo, `concorrente` quando nenhum é identificado) são omitidos da resposta.

**Consultas**

| Endpoint | Retorno |
|---|---|
| `GET /analises` | Lista de análises completas |
| `GET /analises/{id}` | Uma análise completa |
| `GET /analises/{id}/alertas` | Somente os alertas da análise |

**Resolver alerta: `PATCH /analises/{id}/alertas/{alertaId}/resolver`** (sem corpo)

Muda o status do alerta de `ABERTO` para `RESOLVIDO` e devolve o alerta atualizado. Retorna `404` se o alerta não pertencer àquela análise.

**Ajustar dados comerciais: `PUT /analises/{id}`**

Permite corrigir manualmente a persona, o budget e o concorrente identificados pela IA.

| Campo | Tipo | Regra |
|---|---|---|
| `persona` | texto | obrigatório, até 100 caracteres |
| `budget` | decimal | obrigatório, maior ou igual a zero |
| `concorrente` | texto | opcional, até 100 caracteres (vazio remove o concorrente) |

```json
{
  "persona": "Decisor",
  "budget": 75000.00,
  "concorrente": "SAP"
}
```

**Excluir: `DELETE /analises/{id}`** remove a análise, seus alertas e seus produtos identificados (`204`).

### Fluxo completo de uso

1. `POST /clientes`: cadastra o cliente.
2. `POST /produtos`: cadastra os produtos que podem ser citados.
3. `POST /reunioes`: agenda a reunião do cliente.
4. `POST /reunioes/{id}/transcricao`: registra o texto da reunião.
5. `POST /reunioes/{id}/analises`: executa a análise da IA.
6. `GET /analises/{id}`: consulta o resultado e `PATCH .../resolver` para tratar os alertas.

---

## 🖥️ Front-end Swing

O front-end não acessa o banco: todas as operações passam pela API.

| Tela | O que faz |
|---|---|
| Tela Principal | Menu de acesso às demais telas |
| Clientes | Cadastrar, atualizar, excluir e listar clientes |
| Produtos TOTVS | Cadastrar, atualizar, excluir e listar produtos |
| Reuniões | Cadastrar, atualizar e excluir reuniões; registrar e consultar a transcrição |
| Análise IA | Executar a análise de uma reunião, ver o histórico e excluir análises |

As mensagens de erro exibidas nas telas são as mesmas devolvidas pela API (campo `mensagem`). Se a API estiver fora do ar, a tela informa que não foi possível conectar.

---

## 🧪 Testes

```bash
mvn test
```

| Teste | O que cobre |
|---|---|
| `AnaliseIATest` | Regras de negócio da análise (cenários positivo e negativo, produto sem duplicidade, análise sem transcrição) |
| `ClienteControllerTest`, `ProdutoControllerTest`, `ReuniaoControllerTest`, `AnaliseControllerTest` | Códigos de status (200, 201, 204, 400, 404, 409, 422) e formato de erro de cada endpoint, com MockMvc |
| `ClienteServiceTest`, `ReuniaoServiceTest`, `AnaliseServiceTest` | Regras dos services, conflitos e tradução dos erros do banco, com Mockito |

Os testes não precisam do PostgreSQL.

---

## 🩺 Solução de problemas

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| Swing: "Não foi possível conectar à API" | API fora do ar ou URL diferente | Suba a API; confira `AURA_API_URL` |
| API responde `500` "Erro ao acessar o banco de dados." | Senha ausente ou banco inacessível | Configure `DB_PASSWORD` ou `db.properties`; confira se o PostgreSQL está rodando e o banco `aura_ai` existe |
| `409` ao cadastrar cliente | CNPJ já cadastrado | Use outro CNPJ |
| `409` ao excluir cliente ou produto | Possui reuniões ou análises vinculadas | Exclua primeiro os registros dependentes |
| `422` ao analisar | Reunião sem transcrição | Cadastre a transcrição antes |
| Produto não aparece na análise | Nome do produto não consta no texto, ou produto cadastrado depois da análise | Cadastre o produto antes e rode nova análise |
| Erro de classe não encontrada após atualizar | Pacotes renomeados para `br.com.fiap.auraai` | **Build → Rebuild Project** e recarregue o Maven |

---

## 🛠️ Tecnologias Utilizadas

- Java 17
- Spring Boot 3 (Web e Validation)
- Java Swing
- JDBC e PostgreSQL (administração pelo DBeaver)
- Maven
- JUnit 5, Mockito e MockMvc
- Postman
- IntelliJ IDEA

---

## 👨‍💻 Desenvolvedores

| Aluno | RM |
|---|---|
| Matheus Kitamura | RM563205 |
| João Guilherme Guida | RM565244 |
| Gustavo Barroso | RM565705 |
| Victor Oliveira Alves | RM565723 |

---

## 🎓 Projeto Acadêmico

Projeto desenvolvido para a disciplina de Java da **FIAP - Engenharia de Software**, aplicando Orientação a Objetos, CRUD, JDBC, PostgreSQL, DAO, API RESTful com Spring Boot, tratamento de exceções, transações com commit e rollback e relacionamentos entre entidades.
