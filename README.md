# SS.COM RSS feed parser

SS.COM Parser is a simple RSS feed parser of the advertising site https://ss.com/ written in Java and [Spring Boot](https://spring.io/projects/spring-boot). It uses [Docker](https://www.docker.com/) as primary execution environment and [Gradle](https://gradle.org/) build tool for building application image.


## Quickstart

For a quick start, use the following instructions

```shell
git clone https://github.com/AlekseyChudov/ss-parser.git
cd ss-parser
./gradlew jibDockerBuild
docker run -it --rm ss-parser:1.1
```

Congratulations! You have just compiled the SS.COM Parser Java application, packed it into the Docker image and ran the Docker container. You can now lean back and enjoy the logs :)


## Configuration

Spring Boot lets you [customize configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config) in a various ways. To customize Docker application configuration it is convenient to use environment variables. The properties supported by SS.COM Parser and Spring Boot with their default values are shown in [application.yml](https://github.com/AlekseyChudov/ss-parser/blob/master/src/main/resources/application.yml) and [Appendix A, Common application properties](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#common-application-properties) respectively. SS.COM Parser uses [Spring Expression Language (SpEL)](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions) as filter engine.

Create environment variables file `ss-parser.env`. Do not forget to convert the values from yaml or properties format into environment variables format.

```shell
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=user@gmail.com
SPRING_MAIL_PASSWORD=secret

SS_PARSER_MAIL_FROM=user@gmail.com
SS_PARSER_MAIL_TO=user@gmail.com
SS_PARSER_MAIL_ENABLED=true

SS_PARSER_CAR_EXPRESSION=mark == 'Lexus' and year >= 2013

SS_PARSER_FLAT_EXPRESSION=region matches 'Плявниеки|Пурвциемс' and rooms > 3 and area > 100

SS_PARSER_HOME_EXPRESSION=region matches 'Дарзциемс|Плявниеки|Пурвциемс|Межциемс' and area > 200 and land > 600
```

Then run the Docker container with the new configuration file.

```shell
docker run -it --rm --env-file ss-parser.env ss-parser:1.1
```
