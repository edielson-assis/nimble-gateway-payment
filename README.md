# 💳 Nimble Gateway Payment

### Desafio Técnico — Backend Java (Spring Boot)

Este projeto foi desenvolvido como parte do desafio técnico para a vaga de **Desenvolvedor Java Pleno**, com foco na construção de uma API para gestão de cobranças, transações e pagamentos.  
A aplicação foi construída seguindo boas práticas de arquitetura, segurança e documentação.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA / Hibernate**
- **Spring Validation**
- **Spring Security (JWT)**
- **OpenFeign (integração com autorizador externo)**
- **JUnit / Mockito**
- **Lombok**
- **PostgreSQL**
- **Flyway (controle de migrations)**
- **Swagger / OpenAPI 3**
- **Docker / Docker Compose**

---

## 🧾 Requisitos do Projeto

Todos os requisitos do desafio foram atendidos ✅

### 🧍‍♂️ Usuário e Conta
- [x] Cadastro de usuários
- [x] Criação automática de conta bancária vinculada ao usuário
- [x] Validação de CPF e e-mail únicos
- [x] Criptografia de senha com BCrypt
- [x] Autenticação e autorização via JWT

### 💰 Conta e Transações
- [x] Realizar depósitos em conta
- [x] Consultar saldo
- [x] Registrar transações financeiras
- [x] Listar histórico de transações do usuário
- [x] Evitar acesso a saldo de terceiros (uso de DTO seguro)
- [x] Persistência e controle transacional no banco de dados

### 💳 Cobranças (Charges)
- [x] Criação de cobranças entre usuários
- [x] Pagamento de cobranças com **saldo da conta**
- [x] Pagamento de cobranças com **cartão de crédito**
- [x] Consulta ao **autorizador externo** antes de processar pagamentos
- [x] Atualização de status da cobrança (PENDING → PAID / CANCELED)
- [x] Registro de transações associadas às cobranças

### ❌ Cancelamento de Cobranças
- [x] Cancelamento de cobranças **pendentes**
- [x] Cancelamento de cobranças **pagas com saldo** (estorno automático)
- [x] Cancelamento de cobranças **pagas com cartão** (consulta ao autorizador externo)
- [x] Garantia de que **apenas o originador** da cobrança possa cancelá-la
- [x] Bloqueio de cancelamentos incorretos (método de pagamento inconsistente)

### 🧠 Integração Externa
- [x] Integração com o **autorizador externo** (API pública da Nimble)
- [x] Validação de autorização antes de depósitos, pagamentos e cancelamentos
- [x] Tratamento de erros e timeouts do serviço externo

### 🧩 Mapeamento e DTOs
- [x] Uso de **Mappers** para conversão entre entidades e DTOs
- [x] DTOs de segurança para ocultar dados sensíveis
- [x] Documentação detalhada com Swagger (OpenAPI 3)

### 🧰 Migrations e Banco de Dados
- [x] Criação automática das tabelas via Flyway
- [x] Migrations versionadas (`V1__...` a `V8__...`)
- [x] Relacionamentos e chaves estrangeiras definidos corretamente

### 🧪 Testes

#### ✅ O que foi implementado
- Foram implementados **testes unitários** para os principais métodos da **camada de serviço** (Service layer), cobrindo fluxos críticos como:
  - Criação e validação de `User` / `Account`;
  - Processamento de depósitos e pagamentos com saldo;
  - Lógica de pagamento via cartão (fluxo de integração/decisão, mocks do autorizador);
  - Cancelamento de cobranças (pendentes, pagas com saldo, pagas com cartão);
  - Registro de `Transaction` e regras de estorno.

> Observação: os testes unitários usam mocks para isolar dependências externas (repositórios, adaptadores do autorizador, serviços auxiliares), garantindo que a lógica de negócio seja verificada de forma determinística.

#### 🛠 Ferramentas utilizadas
- **JUnit 5** (Jupiter) — framework de testes
- **Mockito** — mock/stub de dependências
- **Spring Boot Test** — suporte ao contexto quando necessário

---

## 📦 Funcionalidades Extras Implementadas

- [x] Máscara automática de CPF nos DTOs de resposta
- [x] Log estruturado de operações financeiras
- [x] Rollback automático em falhas de transações
- [x] Tratamento global de exceções (`@ControllerAdvice`)
- [x] Testes manuais via Swagger e Postman

---

## ⚙️ Execução do Projeto

### 🐳 Via Docker Compose


- Docker e Docker Compose instalados no sistema. Você pode baixar o Docker Desktop (que já inclui o Docker Compose) a partir do [site oficial do Docker](https://www.docker.com/).


### Passo 1: Obtenha o arquivo `docker-compose.yml`

Baixe o arquivo `docker-compose.yml` fornecido no repositório. Esse arquivo contém as definições de configuração necessárias para rodar a aplicação e suas dependências, como o banco de dados.

### Passo 2: Execute o Docker Compose

No terminal, navegue até a pasta onde você salvou o `docker-compose.yml` e execute o seguinte comando:

```
docker compose up -d
```

### Passo 3: Verifique os Logs (Opcional)

Para verificar se a aplicação está funcionando corretamente, você pode inspecionar os logs com o comando:

```
docker compose logs -f
```

Esse comando exibirá os logs de todos os containers, permitindo que você veja o status da aplicação e do banco de dados.

### Passo 4: Acesse a Aplicação

Após o Docker Compose iniciar todos os containers, a aplicação estará acessível. Você poderá acessá-la no navegador em:

```
http://localhost:8080/swagger-ui/index.html
```
Isso fará com que a aplicação seja inicializada na porta 8765.

## Parar e Remover os Containers

```
docker compose down
```
Esse comando encerra a execução dos containers e remove os recursos associados, liberando espaço no sistema.

## Outra alternativa para rodar a aplicação 

Abra o terminal do git bash na pasta onde deseja salvar o projeto e digite o seguinte comando: 

```
git clone git@github.com:edielson-assis/nimble-gateway-payment.git
```
Depois de clonar o projeto, siga as instruções do passo 2 em diante para configurar e iniciar a aplicação.

---

## 🔐 Autenticação

1. **Crie um usuário** 
   Endpoint: `POST /api/v1/auth/signup`

2. **Faça login e obtenha o JWT**  
   Endpoint: `POST /api/v1/auth/signin`

3. **Use o token JWT** para acessar endpoints protegidos:  

```
   Authorization: Bearer <token>
```

---

## 📊 Estrutura das Entidades Principais

| Entidade | Descrição |
|-----------|------------|
| **User** | Representa o usuário do sistema. Contém informações como nome completo, CPF, e-mail, tipo de usuário (comum ou moderador) e roles de acesso. |
| **Account** | Conta bancária vinculada ao usuário. Armazena o saldo, histórico de transações e está associada diretamente ao usuário dono da conta. |
| **Charge** | Representa uma cobrança criada por um usuário (*originator*) para outro (*recipient*). Contém o valor, status, método de pagamento e referência à transação correspondente. |
| **Transaction** | Registro de movimentações financeiras entre contas, como pagamentos, depósitos e estornos. Contém tipo, valor, contas envolvidas, status e data da operação. |
| **CardPayment** | Dados de um pagamento realizado com cartão de crédito. Armazena o nome do titular, data de vencimento e os últimos quatro dígitos do cartão, garantindo segurança e anonimização. |


## 🧠 Pontos de Melhoria / Implementações Futuras

- [ ] Implementar histórico detalhado de pagamentos e estornos

- [ ] Adicionar testes unitários e de integração automatizados

- [ ] Implementar monitoramento de falhas no autorizador externo

- [ ] Suporte a múltiplos cartões por usuário

- [ ] Geração de relatórios financeiros (PDF/CSV)

- [ ] Auditoria completa de transações (createdBy, updatedBy)

---

## 👨‍💻 Autor

**Edielson Assis**  
Desenvolvedor Java | Spring Boot  

🔗 [LinkedIn](https://www.linkedin.com/in/edielson-assis)  
💻 [GitHub](https://github.com/edielson-assis)

---

> Projeto desenvolvido com dedicação, seguindo boas práticas e princípios de arquitetura limpa.  
> **“Código limpo é como uma história bem contada — fácil de ler, difícil de esquecer.”**
