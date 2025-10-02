# s533-trilha-auditoria-servico

Microsserviço para Trilha de Auditoria Serviço



- Java 21
- Maven 3.8+
- Spring Boot 3.4.4


O projeto segue a estrutura padrão de aplicações Spring Boot:

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br.gov.bnb.s533/
│   │   │       ├── Application.java                     # Classe principal
│   │   │
│   │   │       ├── api/                                 # Interface da aplicação
│   │   │       │   ├── advice/                           # Exception handlers
│   │   │       │   │   └── ExceptionHandlerAdvice.java
│   │   │       │   ├── config/                           # Configurações gerais
│   │   │       │   │   ├── BnbAuthFilterConfiguration.java
│   │   │       │   │   ├── IsKeyConfiguration.java
│   │   │       │   │   ├── JacksonDateTimeConfig.java
│   │   │       │   │   └── OpenAPIConfiguration.java
│   │   │
│   │   │       ├── core/                                 # Lógica de domínio
│   │   │       │   ├── exception/
│   │   │       │   │   └── RequisicaoInvalidaException.java
│   │   │       │   ├── integration/
│   │   │       │   │   └── IntegracaoExemplo.java
│   │   │       │   ├── model/
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   ├── Produto.java
│   │   │       │   │   │   
│   │   │       │   │   │   
│   │   │       │   │   └── enums/
│   │   │       │   │       ├── CalculadoraDeImposto.java
│   │   │       │   │       └── ConsultaStatus.java
│   │   │       │   └── service/
│   │   │       │       └── SaudacaoService.java
│   │   │
│   │   │       ├── v1/
│   │   │       │   ├── controller/
│   │   │       │   │   └── SaudacaoController.java
│   │   │       │   ├── dto/
│   │   │       │       ├── ApiResponse.java
│   │   │       │       ├── MensagemDto.java
│   │   │       │       └── ProblemDetails.java
│   │   │       │   
│   │   │       │       
│   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│
├── src/test/java/br.gov.bnb.s533/
│   └── core/
│       └── model/
│           ├── entity/
│           │   └── ProdutoTest.java
│           ├── enums/
│           │   └── CalculadoraDeImpostoTest.java
│           └── fixture/
│               └── ProdutoFixture.java
│
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```


Para criar um novo projeto usando este archetype, execute o seguinte comando:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=br.gov.bnb.java.archetypes \
  -DarchetypeArtifactId=maven-archetype-jdk21-api-basic \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=br.gov.bnb.s999 \
  -DartifactId=s999-api \
  -Dversion=0.0.1-SNAPSHOT \
  -Ddescription="API de Serviço S999 - Sistema WXYZ" \
  -DinteractiveMode=false
```

Você pode personalizar os valores dos parâmetros `groupId`, `artifactId`, `version` e `description` conforme necessário para o seu projeto.


```bash
# Construir a imagem
docker build -t s533-trilha-auditoria-servico:latest .

# Executar o container
docker run -p 8080:8080 s533-trilha-auditoria-servico:latest
```


- `GET /api/v1/saudacao/ola`: Retorna uma mensagem de saudação
- `GET /api/v1/saudacao/erro`: Retorna uma mensagem de erro
- `GET /api/actuator/health`: Verificação de saúde da aplicação
- `GET /api/actuator/info`: Informações sobre a aplicação



Este projeto inclui um Jenkinsfile configurado para:

1. Compilar o código
2. Executar testes
3. Análise de código com SonarQube
4. Construir e publicar imagem Docker
5. Implantar em ambientes de desenvolvimento e produção



Você pode personalizar este projeto modificando:

- `application-dev.properties` e `application-prod.properties` para configurações específicas de ambiente
- `pom.xml` para adicionar ou remover dependências
- `Dockerfile` para ajustar a construção da imagem Docker
- `Jenkinsfile` para ajustar o pipeline CI/CD



Caso seja necessario implementar cliente(s) SOAP segue link para consulta:

 - https://spring.io/guides/gs/consuming-web-service




Documentação da API

 - http://localhost:8080/api/swagger-ui/index.html



Uso da anotação @JsonView do Jackson

* @JsonView é útil para controle dinâmico de campos.
* Traz overhead moderado, especialmente com uso repetitivo ou em listas grandes.
* Para APIs críticas em performance, crie DTOs dedicados ou use projeções.
* Avalie alternativas como MapStruct + DTOs se performance for crítica.
