# 🚀 Back-end

O repositório do **Back-end** é dedicado ao desenvolvimento e aprimoramento da API principal do nosso projeto. Aqui você encontrará a estrutura do servidor, a integração com o banco de dados e todos os endpoints necessários para a comunicação com o frontend.

---

## 💡 Visão Geral do Projeto

Este backend é um sistema robusto e completo para o gerenciamento de clínicas veterinárias (VetClinic API), construído com Spring Boot. Ele oferece um sistema de autenticação seguro baseado em JWT, controle de acesso por papéis (Usuário e Administrador) e uma API RESTful abrangente para gerenciar diversas entidades e funcionalidades.

---

## 🎯 Funcionalidades Principais

A aplicação possui dois níveis de acesso principais: **Usuário Comum** (`ROLE_USER`) e **Administrador** (`ROLE_ADMIN`).

### Para Usuários Comuns (`ROLE_USER`):
* **Autenticação Segura:** Cadastro de novas contas e login com JWT.
* **Visualização de Informações:** Acesso à lista de clínicas e veterinários disponíveis.
* **Gerenciamento de Pets:** Operações CRUD completas para os próprios animais de estimação.
* **Agendamento de Consultas:** Capacidade de agendar novas consultas para seus pets, escolhendo clínica, especialidade e veterinário, com validação de conflitos de horário.
* **Histórico:** Visualização do histórico de consultas agendadas.

### Para Administradores (`ROLE_ADMIN`):
* **Acesso Total:** Todas as funcionalidades de um usuário comum.
* **Painel de Controle:** Acesso a uma área de gerenciamento centralizada.
* **CRUD Completo:** Gerenciamento total de **todas** as entidades do sistema: Clínicas, Veterinários, Usuários (listagem e exclusão), Pets (de todos os usuários) e Consultas (de todos os usuários).
* **Geração de Relatórios:** Ferramenta para gerar relatórios em PDF das movimentações de consultas, com filtros por período.

---

## 🏗️ Arquitetura e Padrões

O projeto foi construído seguindo as melhores práticas de desenvolvimento para APIs REST, garantindo um código limpo, seguro e escalável.

* **API RESTful:** Endpoints bem definidos seguindo as convenções REST para cada recurso.
* **Arquitetura em Camadas:** Divisão clara de responsabilidades entre `Controller` (camada de API), `Service` (camada de regras de negócio) e `Repository` (camada de acesso a dados).
* **Segurança Stateless com JWT:** A autenticação é feita via tokens JWT, tornando a API sem estado e ideal para ser consumida por SPAs ou aplicativos móveis.
* **Controle de Acesso por Papel (RBAC):** Spring Security é usado para definir permissões granulares para cada endpoint com base nos papéis `ROLE_USER` e `ROLE_ADMIN`.
* **Padrão DTO (Data Transfer Object):** Uso extensivo de DTOs para requisição e resposta, desacoplando a API da estrutura do banco de dados.
* **Gerenciamento de Exceções Global:** Um `@RestControllerAdvice` centraliza o tratamento de erros, retornando respostas JSON padronizadas.
* **Perfis de Configuração:** Uso de perfis (`dev` e `prod`) para separar as configurações de banco de dados (H2 em memória para desenvolvimento e MySQL para produção).

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17+ (compatível com Java 21)
* **Framework Principal:** Spring Boot 3.x
* **Módulos Spring:** Spring Web, Spring Data JPA, Spring Security
* **Banco de Dados:** H2 (para desenvolvimento local) e MySQL (para produção)
* **Persistência:** Hibernate
* **Segurança:** JSON Web Tokens (JJWT)
* **Geração de PDF:** iTextPDF
* **Ferramentas:** Lombok, Maven
* **Documentação da API:** Swagger / OpenAPI 3
* **Testes de API:** Postman / Insomnia

---

## 💻 Pré-requisitos

Antes de começar, verifique se você atendeu aos seguintes requisitos:

* Você tem o **JDK 17 ou superior** (compatível com Java 21) instalado em sua máquina.
* Você possui o **Apache Maven 3.8+** configurado.
* O banco de dados **MySQL** está instalado e em execução (para ambiente de produção, não necessário para desenvolvimento local).
* Você tem um ambiente de desenvolvimento como **IntelliJ IDEA** ou **VSCode** configurado.

---

## 🚀 Como Executar Localmente

Para instalar e executar o **Back-end**, siga estas etapas:

1.  Clone o repositório:
    ```bash
    git clone [https://github.com/02-Bits/Back-end.git](https://github.com/02-Bits/Back-end.git)
    cd Back-end
    ```

2.  **Para desenvolvimento local, nenhuma configuração de banco de dados é necessária!** O projeto está configurado com um perfil `dev` que utiliza o banco de dados em memória H2 por padrão. Ele será criado e populado automaticamente ao iniciar a aplicação.

3.  Execute o projeto usando o Maven Wrapper:

    ### Linux e macOS:
    ```bash
    ./mvnw spring-boot:run
    ```

    ### Windows:
    ```bash
    mvnw.cmd spring-boot:run
    ```

4.  A API estará disponível em `http://localhost:8080`.

5.  Acesse a documentação interativa da API via Swagger em:
    [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📫 Contribuindo para o Back-end

Para contribuir com o **Back-end**, siga estas etapas:

1.  Bifurque este repositório.
2.  Crie um branch: `git checkout -b <nome_do_seu_branch>`.
3.  Faça suas alterações e confirme-as: `git commit -m '<mensagem_do_commit>'`
4.  Envie para o branch original: `git push origin <nome_do_seu_branch>`
5.  Crie a solicitação de pull.

Como alternativa, consulte a documentação do GitHub em [como criar uma solicitação pull](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request).

---

## 🤝 Colaboradores

Agradecemos às seguintes pessoas que contribuíram para este projeto:

* **João Emanuel** ([@JoaoNascimento1802](https://github.com/JoaoNascimento1802)): Desenvolvedor principal responsável pela maior parte da implementação do backend.
* **Felipe** ([@fearaujo293](https://github.com/fearaujo293)): Responsável pela implementação da segurança da aplicação.

O restante da equipe está focada no desenvolvimento do **Frontend** do projeto.

---

## 📄 Licença

Este projeto é para fins educacionais e de portfólio, demonstrando a aplicação de tecnologias modernas de backend e boas práticas de desenvolvimento. Para mais detalhes, veja o arquivo [LICENÇA](LICENSE.md).
