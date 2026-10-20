# 🤖 Aura AI

Projeto acadêmico desenvolvido em **Java** com o objetivo de simular uma solução de inteligência conversacional para análise de reuniões comerciais.

A **Aura AI** processa transcrições de reuniões e identifica informações relevantes como sentimento do cliente, risco de churn, oportunidades comerciais, concorrentes mencionados, alertas e produtos TOTVS citados durante a conversa.

A solução é composta por uma **API RESTful (Spring Boot)** e um **front-end Java Swing** que consome essa API via HTTP.

---

## 📌 Funcionalidades

- Cadastro, consulta, atualização e exclusão de clientes
- Cadastro, consulta, atualização e exclusão de produtos TOTVS
- Registro de reuniões e de suas transcrições
- Análise automática das reuniões
- Identificação de sentimento, risco de churn, oportunidade comercial e concorrentes
- Identificação de produtos TOTVS mencionados
- Geração automática de alertas
- API RESTful com respostas de erro padronizadas
- Persistência em PostgreSQL
- Interface gráfica utilizando Java Swing

---

## 🧠 Regras de Negócio

A principal lógica do sistema está concentrada na classe `AnaliseIA`.

```java
identificarSentimento()
calcularRiscoChurn()
calcularOportunidade()
identificarConcorrente()
mapearProduto()
```

Exemplo de transcrição:

> O cliente demonstrou interesse em contratar o Protheus para expandir suas operações e também informou que está avaliando o SAP.

Resultado esperado:

```text
Sentimento: POSITIVO
Risco de Churn: 15%
Oportunidade: 90%
Persona: Decisor
Budget: R$ 50000.00
Concorrente: SAP
Produto identificado: Protheus
Alertas: UPSELL, CONCORRENTE
```

---

## 🏗️ Arquitetura

```text
Swing (view)
   ↓  HTTP / JSON
ApiClient (client)
   ↓
Controller REST  →  DTO
   ↓
Service (validações e regras)
   ↓
DAO (JDBC)
   ↓
ConnectionFactory
   ↓
PostgreSQL
```

### Estrutura do Projeto

```text
src/main
├── java
│   ├── app          Main (Swing) e AuraAiApplication (API)
│   ├── client       Clientes HTTP usados pelo Swing
│   ├── controller   Controllers REST
│   ├── dto          Objetos de entrada e saída da API
│   ├── service      Regras de negócio e validações
│   ├── dao          Acesso ao banco (JDBC)
│   ├── domain.model Entidades de domínio
│   ├── exception    Exceções da aplicação e tratamento global
│   ├── factory      ConnectionFactory (Factory + Singleton)
│   └── view         Telas Swing
└── resources        application.properties
src/test             Testes JUnit
db                   schema.sql e script de migração
```

---

## 🌐 API REST

Base URL: `http://localhost:8080`

| Recurso | Verbo | URI | Sucesso | Erros |
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
| Análises | POST | `/reunioes/{id}/analises` | 201 | 404, 422 (sem transcrição) |
| | GET | `/analises` | 200 | 500 |
| | GET | `/analises/{id}` | 200 | 400, 404 |
| | GET | `/analises/{id}/alertas` | 200 | 404 |
| | PUT | `/analises/{id}` | 200 | 400, 404 |
| | DELETE | `/analises/{id}` | 204 | 404 |

Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2026-10-19T14:30:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Cliente não encontrado.",
  "caminho": "/clientes/99"
}
```

---

## 🗃️ Banco de Dados

Banco: `aura_ai` (PostgreSQL). O script completo está em [`db/schema.sql`](db/schema.sql).

| Tabela | Principais Campos |
|---|---|
| `cliente` | `id`, `nome`, `cnpj`, `segmento`, `email` |
| `reuniao` | `id`, `titulo`, `data_hora`, `duracao_min`, `responsavel`, `status`, `cliente_id` |
| `transcricao` | `id`, `texto_bruto`, `qtd_palavras`, `idioma`, `reuniao_id` |
| `produto_totvs` | `id`, `nome`, `categoria`, `versao`, `descricao` |
| `analise_ia` | `id`, `sentimento`, `risco_churn`, `oportunidade`, `persona`, `budget`, `concorrente`, `reuniao_id` |
| `alerta` | `id`, `tipo`, `descricao`, `prioridade`, `status`, `analise_id` |
| `analise_produto` | `analise_id`, `produto_id`, `contexto`, `relevancia`, `tipo_mencao` |

```text
CLIENTE
   ↓
REUNIAO
   ├── TRANSCRICAO
   └── ANALISE_IA
          ├── ALERTA
          └── ANALISE_PRODUTO
                 ↓
            PRODUTO_TOTVS
```

Os relacionamentos são controlados por chaves estrangeiras. Transcrição, análise, alertas e produtos identificados são excluídos em cascata junto com a reunião ou a análise; clientes e produtos em uso não podem ser excluídos (a API responde 409).

Se o banco já existia antes desta versão, rode [`db/migracao_banco_existente.sql`](db/migracao_banco_existente.sql) no DBeaver.

---

## ⚙️ Configuração e Execução

### Requisitos

- Java 17 ou superior
- Maven 3.9+ (ou o Maven do IntelliJ)
- PostgreSQL

### 1. Criar o banco

```sql
CREATE DATABASE aura_ai;
```

Depois execute `db/schema.sql` conectado ao banco `aura_ai`.

### 2. Configurar a conexão

A senha **não fica no código**. Use uma destas opções:

- Variáveis de ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`
- Arquivo `db.properties` na raiz do projeto (ignorado pelo Git). Copie `db.properties.example` e preencha a senha.

### 3. Subir a API

```bash
mvn spring-boot:run
```

ou execute a classe `app.AuraAiApplication` no IntelliJ.

### 4. Abrir o front-end Swing

Com a API em execução:

```bash
mvn compile exec:java
```

ou execute a classe `app.Main` no IntelliJ. Para apontar para outra URL, defina `AURA_API_URL` (padrão `http://localhost:8080`).

---

## 🧪 Testes

```bash
mvn test
```

- `AnaliseIATest`: regras de negócio da análise (cenários positivo e negativo)
- `ClienteControllerTest`: códigos de status da API (200, 201, 204, 400, 404, 409)

---

## 🛠️ Tecnologias Utilizadas

- Java 17
- Spring Boot 3 (Web, Validation)
- Java Swing
- JDBC
- PostgreSQL e DBeaver
- Maven
- JUnit 5 e MockMvc
- IntelliJ IDEA
- Padrões: DAO, Factory, Singleton, DTO, camadas (Controller, Service, DAO)

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

Projeto desenvolvido para a disciplina de Java da **FIAP - Engenharia de Software**, aplicando conceitos de Orientação a Objetos, CRUD, JDBC, PostgreSQL, DAO, API RESTful com Spring Boot, tratamento de exceções, transações com commit e rollback e relacionamentos entre entidades.
