# 🤖 Aura AI

Projeto acadêmico desenvolvido em **Java** com o objetivo de simular uma solução de inteligência conversacional para análise de reuniões comerciais.

A **Aura AI** processa transcrições de reuniões e identifica informações relevantes como sentimento do cliente, risco de churn, oportunidades comerciais, concorrentes mencionados, alertas e produtos TOTVS citados durante a conversa.

---

## 📌 Funcionalidades

- Cadastro de clientes
- Cadastro de produtos TOTVS
- Registro de reuniões
- Cadastro de transcrições
- Análise automática das reuniões
- Identificação de sentimento
- Cálculo de risco de churn
- Identificação de oportunidades comerciais
- Identificação de concorrentes
- Identificação de produtos TOTVS mencionados
- Geração automática de alertas
- Persistência em PostgreSQL
- Interface gráfica utilizando Java Swing

---

## 🧠 Regras de Negócio

A principal lógica do sistema está concentrada na classe `AnaliseIA`.

Principais métodos:

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

Produto identificado:
Protheus

Alertas:
UPSELL
CONCORRENTE
```

---

## 🏗️ Arquitetura

O projeto utiliza uma arquitetura dividida em camadas:

```text
Usuário
   ↓
Swing / View
   ↓
Controller
   ↓
Model / Service
   ↓
DAO
   ↓
ConnectionFactory
   ↓
JDBC
   ↓
PostgreSQL
```

### Estrutura do Projeto

```text
src
│
├── app
│   └── Main.java
│
├── controller
│   ├── ClienteController.java
│   ├── ProdutoController.java
│   ├── ReuniaoController.java
│   └── AnaliseController.java
│
├── dao
│   ├── ClienteDAO.java
│   ├── ClienteDAOImpl.java
│   ├── ProdutoDAO.java
│   ├── ProdutoDAOImpl.java
│   ├── ReuniaoDAO.java
│   ├── ReuniaoDAOImpl.java
│   ├── AnaliseIADAO.java
│   └── AnaliseIADAOImpl.java
│
├── domain
│   ├── model
│   └── service
│       └── AnaliseIA.java
│
├── factory
│   └── ConnectionFactory.java
│
├── test
│   └── TesteRegrasNegocio.java
│
└── view
    ├── TelaPrincipal.java
    ├── TelaCliente.java
    ├── TelaProduto.java
    ├── TelaReuniao.java
    └── TelaAnalise.java
```

---

## 🗃️ Banco de Dados

O projeto utiliza **PostgreSQL** para persistência das informações.

Banco utilizado:

```text
aura_ai
```

### Estrutura das Tabelas

| Tabela | Principais Campos |
|---|---|
| `cliente` | `id`, `nome`, `cnpj`, `segmento`, `email` |
| `reuniao` | `id`, `titulo`, `data_hora`, `duracao_min`, `responsavel`, `status`, `cliente_id` |
| `transcricao` | `id`, `texto_bruto`, `qtd_palavras`, `idioma`, `reuniao_id` |
| `produto_totvs` | `id`, `nome`, `categoria`, `versao`, `descricao` |
| `analise_ia` | `id`, `sentimento`, `risco_churn`, `oportunidade`, `persona`, `budget`, `concorrente`, `reuniao_id` |
| `alerta` | `id`, `tipo`, `descricao`, `prioridade`, `status`, `analise_id` |
| `analise_produto` | `analise_id`, `produto_id`, `contexto`, `relevancia`, `tipo_mencao` |

### Relacionamentos

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

Os relacionamentos entre as tabelas são controlados por **chaves estrangeiras**, garantindo a integridade dos dados no PostgreSQL.
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

---

## 💾 DAO e CRUD

O sistema utiliza o padrão **DAO (Data Access Object)** para separar o acesso ao banco das regras de negócio.

As operações CRUD implementadas são:

```text
CREATE
READ
UPDATE
DELETE
```

Exemplo:

```java
clienteDAO.inserir(cliente);
clienteDAO.buscarPorId(id);
clienteDAO.listarTodos();
clienteDAO.atualizar(cliente);
clienteDAO.excluir(id);
```

---

## 🏭 Factory e Singleton

A conexão com o PostgreSQL é centralizada na classe:

```text
ConnectionFactory.java
```

Exemplo:

```java
Connection conexao =
        ConnectionFactory
                .getInstance()
                .getConnection();
```

A classe utiliza os padrões **Factory** e **Singleton** para centralizar a criação das conexões JDBC.

---

## 🖥️ Interface Gráfica

A interface gráfica foi desenvolvida utilizando **Java Swing**.

O sistema possui as seguintes telas:

```text
Tela Principal
├── Clientes
├── Produtos TOTVS
├── Reuniões
└── Análise IA
```

A interface permite realizar operações de cadastro, consulta, atualização, exclusão e análise das reuniões.

---

## 🧪 Testes

O projeto possui uma classe específica para testar as principais regras de negócio:

```text
TesteRegrasNegocio.java
```

Ela instancia objetos do sistema e testa métodos como:

```java
identificarSentimento()
calcularRiscoChurn()
calcularOportunidade()
identificarConcorrente()
mapearProduto()
```

São utilizados cenários positivos e negativos para validar o comportamento da aplicação.

Exemplo:

```text
[APROVADO] Identificação de sentimento
[APROVADO] Cálculo do risco de churn
[APROVADO] Cálculo da oportunidade comercial
[APROVADO] Identificação de concorrente
[APROVADO] Identificação do produto Protheus

TODOS OS TESTES FORAM EXECUTADOS COM SUCESSO!
```

---

## 🛠️ Tecnologias Utilizadas

- Java
- Java Swing
- JDBC
- PostgreSQL
- DBeaver
- IntelliJ IDEA
- Programação Orientada a Objetos
- DAO
- Factory
- Singleton
- Arquitetura em camadas

---

## ⚙️ Configuração

### 1. Criar o banco

```sql
CREATE DATABASE aura_ai;
```

### 2. Configurar a conexão

Na classe `ConnectionFactory.java`:

```java
private static final String URL =
        "jdbc:postgresql://localhost:5432/aura_ai";

private static final String USUARIO =
        "postgres";

private static final String SENHA =
        "sua_senha";
```

### 3. Adicionar o driver JDBC

Adicione o arquivo `.jar` do PostgreSQL às dependências do projeto.

Exemplo:

```text
postgresql-x.x.x.jar
```

### 4. Executar o projeto

Execute:

```text
app.Main
```

A interface principal da Aura AI será iniciada.

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

Projeto desenvolvido para a disciplina de Java da **FIAP - Engenharia de Software**, aplicando conceitos de:

- Orientação a Objetos
- CRUD
- JDBC
- PostgreSQL
- DAO
- Factory
- Singleton
- Controllers
- Swing
- Regras de negócio
- Persistência de dados
- Relacionamentos entre entidades
- Tratamento de exceções
- Transações com Commit e Rollback
