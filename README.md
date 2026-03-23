
# QrCode Generator
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

O Sistema é um gerador de QrCode que é realizado a partir do cadastro de dados de um cliente que entao recebe o QrCode via email para que possa ser resgatado por um Usuario Administrador via leitura com camera para validaçao do token.

## Detalhes da Arquitetura

O projeto é uma aplicação monolitica baseado no padrao de arquitetura MVC, devido foco maior na estruturaçao do backend, o front end está acoplado na camada static do sistema, e utiliza o PostgreSQLpara o armazenamento de dados. O Sistema conta tambem com as seguintes soluçoes arquiteturais:
- Envio de emails assincronos;
- Thread Pool customizada para otimizaçao de memoria
- Camada de Segurança Simples para autenticação do Usuario Administrador
- Token UUID gerado de forma unica para cada QrCode

## Detalhes do EndPoint
<font color="yellow">POST </font> <font color="green"> /qrcode/gerar:</font> Envia os dados do cliente para o banco dados e dispara a requisiçao do email com QrCode

<font color="yellow">POST </font> <font color="green">/qrcode/resgatar:</font> Autentica o token cadastrado e altera o status para Resgatado

<font color="#adff2f"> GET </font> <font color="green"> /cadastros </font>: Retorna os clientes cadastrados no sistema

<font color="blue">PUT </font> <font color="green">/qrcode/resgatar/{id} </font>: Altera manualmente o status de Pendente para Resgatado utilizando o ID 

<font color="red">DELETE </font>  <font color="green">/qrcode/deletar/{id} </font> : Deleta o cliente do banco de dados utilizando o ID


## Tecnologias Propostas
### Frontend:
- HTML, CSS, JavaScript

### Backend
- Java 21 + Spring Boot 4.0.4: Linguagem e Framework principal para o desenvolvimento
- ZXing: Biblioteca para gerar QrCode
- Java Mail Sender: Biblioteca para envio de emails via java
- Spring Security: Camada de Segurança
- PostgreSQL: Banco de dados Relacional
- Lombok: Framework de auxilio
- Validation: Biblioteca do Spring para validaçao de dados especificos.
- Thymeleaf: Biblioteca de auxilio para leitura de HTML
- H2 Database: Banco de Dados in memoria utilizado para testes
- Swagger: Documentação de API

### Ferramentas:
- InteliJ: Editor de Codigo
- GitHub: Controle de versão
- Figma: Prototipo Visual do sistema
- Postman: Teste de API 


## Clonagem do Repositório
### Pre Requisito:
- Java 21 instalado (Acredito que o 17 ainda seja compativel com a versao do Spring Boot 4.0.4 utilizado no projeto)
- PostgreSQL (Caso decida utilizar outro banco será necessario alterar completamente o application.properties)
- Git: Para clonar o projeto GitHub
- Editor de Codigo: Qualquer um compativel para Java

## Configurando
### 1. Clonar o Repositório
```bash
    git clone <URL_DO_REPOSITÓRIO_GITHUB>
    cd <NOME_DO_REPOSITÓRIO>
```
### 2. Configurar o application.properties

    - Criar uma arquivo chamado application.properties na pasta de resources (src/main/java/resources)
    - Na pagina de templates (src/main/java/resources/templates) consta o application-template.properties com todos as linhas que voce deve copiar e colar no application.properties correto.
    - Atente-se para substituir corretamente os campos que solicitam "seu nome" ou "sua senha" (Cuidado para nao salvar senhas importantes e mandar para o repositorio do github)
    - Verifique tambem se a porta do PostgreSQL está condizente com a do seu sistema.
#### 2.1 Senha de 16 digitos do google
Para configurar essa senha voce deve:

    1. Acesse a página principal da sua Conta Google (myaccount.google.com) no navegador e faça o login.

    2. No menu lateral esquerdo, clique na aba Segurança.

    3. Role a página até encontrar a seção "Como você faz login no Google".

    4. Confirme se a Verificação em duas etapas está ativada. Se não estiver, você precisará ativá-la vinculando o seu número de celular.

    5. Clique em cima da opção Verificação em duas etapas (o menu de senhas de app agora fica escondido lá no final desta página).

    6. Role a tela até o limite inferior e procure por Senhas de app. Clique nessa opção.

    7. O Google vai pedir para você dar um nome para essa senha. Digite algo que ajude a identificar no futuro (exemplo: "API Spring Boot" ou "Sistema Java").

    8. Clique no botão Criar.

    9. Uma janela vai aparecer com uma senha de 16 letras destacada. Copie essa senha.

    10. Cole a senha no seu respectivo campo no application.properties SEM os espaços

## Desenvolvedor
- Savio Paiva (8bitibs)

| Contato:   |                   |
 | -------- |-------- |
  |![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white) | www.linkedin.com/in/saviopaiva9b79b2316 |
|![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white) | saviopaivajr@gmail.com |
