# Projeto: Loja Virtual

Sistema web desenvolvido com Spring Boot para fins educacionais, com foco em persistência de dados, autenticação, MVC, arquitetura em camadas e integração com banco de dados. O tema do projeto é uma Loja Virtual, conforme requisitos da disciplina de Desenvolvimento Web III.

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA + Hibernate
- Thymeleaf
- Spring Security (com filtro customizado)
- Maven
- MySQL
- HTML/CSS/JS
- BCrypt Password Encoder
- Lombok
- Validation API

---

## Estrutura do projeto

```text
src/
 ├── main/
 │   ├── java/
 │   │   └── com.example.trabalho_loja_virtual/
 │   │        ├── Config/
 │   │        ├── Controller/
 │   │        ├── Service/
 │   │        ├── repository/
 │   │        ├── entities/
 │   │        ├── enums/
 │   │        └── TrabalhoLojaVirtualApplication.java
 │   │
 │   └── resources/
 │        ├── templates/
 │        │     ├── login/
 │        │     ├── lojista/
 │        │     └── user/
 │        ├── static/
 │        └── application.properties
```

---

## Funcionalidades

- Cadastro e login de Clientes e Lojistas
- Autenticação com cookies e filtro customizado
- Controle de acesso por roles (CLIENTE / LOJISTA)
- Catálogo de produtos
- Carrinho de compras (sessão)
- Finalização de compra (registro do pedido)
- Histórico de pedidos
- Gerenciamento de produtos, categorias e pedidos pelo lojista
- Controle de estoque
- Validação de dados com Bean Validation
- Criptografia de senhas com BCrypt

---

## Autenticação

As senhas são armazenadas utilizando BCrypt:

```java
PasswordEncoder encoder = new BCryptPasswordEncoder();
```

A validação é realizada com:

```java
encoder.matches(senhaDigitada, senhaCriptografada);
```

A autenticação é gerenciada via cookie (userId) e um filtro customizado que popula o SecurityContext com as roles adequadas.

---

## Como executar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/tigas2909/trabalho.git
cd trabalho_loja_virtual
```

---

### 2. Abrir no VSCode ou IntelliJ

Recomenda-se instalar:

- Extension Pack for Java
- Spring Boot Tools
- Thymeleaf

---

### 3. Configurar o banco de dados

Edite o arquivo:

```text
src/main/resources/application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/loja_virtual?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8086
```

Crie o banco de dados `loja_virtual` se necessário.

---

### 4. Executar o projeto

Via Maven:

```bash
./mvnw spring-boot:run
```

Ou executando a classe:

```java
TrabalhoLojaVirtualApplication
```

Acesse: http://localhost:8086/login

---

## Rotas principais

| Método | Rota | Descrição |
|---|---|---|
| GET | /login | Tela de login |
| POST | /login/enter | Realiza autenticação |
| GET | /login/logout | Realiza logout |
| GET | /login/add | Formulário de cadastro |
| POST | /login/save | Salva novo usuário |
| GET | /cliente/produtos | Catálogo de produtos (cliente) |
| GET | /cliente/carrinho | Visualizar carrinho |
| POST | /cliente/carrinho/adicionar | Adicionar item ao carrinho |
| GET | /cliente/historico | Histórico de pedidos |
| POST | /cliente/pedido/finalizar | Finalizar compra |
| POST | /cliente/pedido/cancelar | Cancelar carrinho |
| GET | /lojista/estoque | Gerenciar produtos (lojista) |
| GET | /lojista/categorias | Gerenciar categorias |
| GET | /lojista/pedidos | Listar pedidos |
| GET | /lojista/pedidos/{id}/editar | Editar pedido |
| POST | /lojista/pedidos/{id}/concluir | Concluir pedido |
| POST | /lojista/pedidos/{id}/cancelar | Cancelar pedido |

---

## Licença

Este projeto é destinado para uso acadêmico e educacional.
