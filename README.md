<h1 align="center">Nimble Gateway Payment</h1> 

![Badge Concluído](https://img.shields.io/static/v1?label=Status&message=Concluído&color=success&style=for-the-badge)
![Badge Java](https://img.shields.io/static/v1?label=Java&message=17&color=orange&style=for-the-badge&logo=java)
![Badge Springboot](https://img.shields.io/static/v1?label=Springboot&message=v3.5.6&color=brightgreen&style=for-the-badge&logo=spring)
![Badge PostgreSQL](https://img.shields.io/static/v1?label=PostgreSQL&message=v16.4&color=blue&style=for-the-badge&logo=PostgreSQL)
![Badge Docker](https://img.shields.io/static/v1?label=Docker&message=v28.3.2&color=blue&style=for-the-badge&logo=Docker)

<br>


## :book: Descrição do projeto 

<p align="justify">
  O projeto <strong>Nimble Gateway Payment</strong> foi desenvolvido como parte de um desafio técnico, com o objetivo de construir um sistema de pagamentos robusto, escalável e seguro, utilizando <strong>Java 17</strong>, <strong>Spring Boot 3</strong>, <strong>Spring Data JPA</strong>, <strong>Spring Cloud OpenFeign</strong>, <strong>Spring Security</strong> e <strong>Flyway</strong>.
  
  A aplicação foi estruturada seguindo boas práticas de arquitetura, separando claramente as responsabilidades em camadas de controller, service, repository e domain model, além de incluir integração externa com um autorizador de pagamentos.

</p><br>

## ✅ Funcionalidades Implementadas

### 1. Estrutura e Arquitetura

- Criação da base do projeto em Spring Boot 3 com dependências essenciais (Spring Web, JPA, Validation, OpenFeign, Lombok, Flyway, etc.);

- Configuração inicial do banco de dados e versionamento de schema com Flyway;

- Organização modular com pacotes separados por contexto (domain, repository, service, controller, external).

### 2. Domínio e Modelagem

- Implementação das entidades principais:

  - `User:` Usuário da plataforma (comum ou moderador);

  - `Account:` Conta financeira associada ao usuário, com controle de saldo;

  - `Charge:` Representa uma cobrança entre usuários;

  - `Transaction:` Registro de movimentações financeiras (ledger);

  - `CardPayment:` Registro de pagamentos realizados com cartão de crédito/débito.

- Criação e versionamento das migrations SQL para todas as tabelas.

### 3. Repositórios (Persistence Layer)

- Implementação de todos os repositórios com Spring Data JPA:

  - `UserRepository`

  - `AccountRepository`

  - `ChargeRepository`

  - `TransactionRepository`

  - `CardPaymentRepository`

### 4. Serviços (Business Layer)

- Lógica completa de criação e gerenciamento de usuários e contas;

  - Implementação do fluxo de pagamento com saldo interno da conta:

  - Verificação de saldo disponível;

  - Débito da conta do pagador;

  - Crédito na conta do recebedor;

  - Registro da transação (`Transaction`) e atualização do status da cobrança (`ChargeStatus.PAID`).

- Integração inicial com o autorizador de pagamentos externo usando Spring Cloud OpenFeign;

- Tratamento básico de exceções e logs transacionais.

### 5. API e Controladores

- Implementação dos endpoints RESTful para as principais operações:

  - Criação e listagem de cobranças;

  - Pagamento de cobranças com saldo interno (`/api/v1/accounts/charges/pay`);

  - Consulta de usuários e contas.

--------

## Pré-requisito:

- Docker e Docker Compose instalados no sistema. Você pode baixar o Docker Desktop (que já inclui o Docker Compose) a partir do [site oficial do Docker](https://www.docker.com/).


## Como Executar

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

## :books: Linguagens, dependencias e libs utilizadas 

- [Java](https://docs.oracle.com/en/java/javase/17/docs/api/index.html)
- [Maven](https://maven.apache.org/ref/3.9.3/maven-core/index.html)
- [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
- [PostgreSQL](https://www.postgresql.org/docs/16/index.html)
- [Spring Data JPA](https://mvnrepository.com/artifact/org.springframework.data/spring-data-jpa/3.2.1)
- [Bean Validation API](https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api/3.0.2)
- [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)
- [Spring Security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security/3.2.1)
- [Flyway PostgreSQL](https://mvnrepository.com/artifact/org.flywaydb/flyway-database-postgresql)
- [Flyway Core](https://mvnrepository.com/artifact/org.flywaydb/flyway-core/11.1.0)
- [Java JWT](https://mvnrepository.com/artifact/com.auth0/java-jwt/4.4.0)
- [Swagger](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui/2.3.0)
- [Docker](https://docs.docker.com/)
- [Spring cloud](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-server)
- [OpenFeign](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign)

## Licença 

The [Apache License 2.0 License](https://github.com/edielson-assis/nimble-gateway-payment/blob/main/LICENSE) (Apache License 2.0)

Copyright :copyright: 2025 - Nimble
