# SS.COM RSS feed parser

SS.COM Parser is a simple RSS feed parser of the advertising site https://ss.com/ written in Java
and [Spring Boot](https://spring.io/projects/spring-boot). It uses [Docker](https://www.docker.com/) as the primary
execution environment and [Gradle](https://gradle.org/) build tool for building application image.

## Quickstart

For a quick start, use the following instructions

```shell
git clone https://github.com/acudovs/ss-parser.git
cd ss-parser
./gradlew jibDockerBuild
docker run -it --rm ss-parser:1.18
```

Congratulations! You have just compiled the SS.COM Parser Java application, packed it into the Docker image, and ran the
Docker container. You can now lean back and enjoy the logs :)

## Configuration

Spring Boot lets
you [customize configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
in various ways. To customize Docker application configuration, it is convenient to use environment variables. The
properties supported by SS.COM Parser and Spring Boot with their default values are shown
in [application.yml](https://github.com/acudovs/ss-parser/blob/master/src/main/resources/application.yml)
and [Appendix A, Common application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
respectively. SS.COM Parser
uses [Spring Expression Language (SpEL)](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions)
as a filter engine.

Create environment variables file `ss-parser.env`. To avoid blocking, select
the [most common user agent](https://techblog.willshouse.com/2012/01/03/most-common-user-agents/). Do not forget to
convert the values from YAML or properties format
into [environment variables format](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-relaxed-binding).

```shell
HTTP_AGENT=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36

SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=user@gmail.com
SPRING_MAIL_PASSWORD=secret

SPRING_SECURITY_USER_PASSWORD=your-password

SS_PARSER_MAIL_ENABLED=true
SS_PARSER_MAIL_FROM=user@gmail.com
SS_PARSER_MAIL_TO=user@gmail.com

SS_PARSER_TELEGRAM_ENABLED=true
SS_PARSER_TELEGRAM_BOT_TOKEN=0000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
SS_PARSER_TELEGRAM_CHAT_ID=0

SS_PARSER_CAR_ENABLED=true
SS_PARSER_CAR_EXPRESSION=mark == 'Lexus' and year >= 2023

SS_PARSER_FLAT_SELL_ENABLED=true
SS_PARSER_FLAT_SELL_EXPRESSION=region matches 'Плявниеки|Пурвциемс' and rooms > 3 and area > 100

SS_PARSER_HOME_ENABLED=true
SS_PARSER_HOME_EXPRESSION=region matches 'Дарзциемс|Плявниеки|Пурвциемс|Межциемс' and area > 200 and land > 600
```

Then run the Docker container with the new configuration file.

```shell
docker run -it --rm --env-file ss-parser.env ss-parser:1.18
```

Once you are satisfied with the filter and configuration, just run the Docker container in the background.

```shell
docker run -d --rm --env-file ss-parser.env ss-parser:1.18
```

## Telegram Setup

To receive notifications via Telegram:

1. Open [@BotFather](https://t.me/BotFather) in Telegram and send `/newbot`. Follow the prompts to choose a name and
   username. BotFather will give you a **bot token** in the format `0000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA`.

2. Start a conversation with your new bot (search for it by username and press **Start**), then open the following URL
   in a browser to find your **chat ID**:
   ```
   https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates
   ```
   Look for `"chat":{"id":...}` in the response.

3. Set the environment variables:
   ```shell
   SS_PARSER_TELEGRAM_ENABLED=true
   SS_PARSER_TELEGRAM_BOT_TOKEN=<YOUR_BOT_TOKEN>
   SS_PARSER_TELEGRAM_CHAT_ID=<YOUR_CHAT_ID>
   ```

4. Optionally, set `SS_PARSER_TELEGRAM_ADMIN_CHAT_ID` to a different chat ID to route error messages there. If omitted,
   errors go to the same chat as ads.

## Working Hours

Both mail and Telegram channels support a configurable delivery window. Notifications that arrive outside the window are
held in memory and delivered on the next flush that falls within the window.

Set the window via environment variables (times in `HH:mm` format, local timezone):

```shell
SS_PARSER_MAIL_WORKING_HOURS_START=09:00
SS_PARSER_MAIL_WORKING_HOURS_END=21:00

SS_PARSER_TELEGRAM_WORKING_HOURS_START=09:00
SS_PARSER_TELEGRAM_WORKING_HOURS_END=21:00
```

If neither variable is set for a channel, that channel delivers at any time.

Overnight windows are supported: setting `START=22:00` and `END=08:00` delivers between 22:00 and 08:00.

## Web Dashboard

A web dashboard is available at `http://localhost:8080/` once the application is running. It shows the status of all
ad tasks and notification channels and allows toggling them on/off, adjusting rates, editing SpEL filter expressions,
and configuring working hours - all without restarting the application.

Default credentials: **admin / admin**

Override via environment variables:

```shell
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=your-password
```

## Filter Expressions

Each ad type has a SpEL filter expression that determines which ads trigger a notification. It is evaluated against each
incoming ad - only ads for which it returns `true` are sent. The default expression is `true` (match everything).

Expressions support comparisons (`==`, `!=`, `<`, `>`, `<=`, `>=`), logical operators (`and`, `or`, `not`), the
`matches` operator for regex matching against strings, arithmetic operators, `between`, and `in`/`not in`. See
the [Spring Expression Language reference](https://docs.spring.io/spring-framework/reference/core/expressions/language-ref.html)
for the full list of supported operators and syntax.

### Car

| Field    | Type   | Description                     |
|----------|--------|---------------------------------|
| `mark`   | String | Brand, e.g. `'Lexus'`           |
| `model`  | String | Model name                      |
| `engine` | String | Engine descriptor, e.g. `'3.5'` |
| `year`   | int    | Year of manufacture             |
| `run`    | int    | Mileage in thousands of km      |
| `price`  | int    | Price in EUR                    |

```shell
SS_PARSER_CAR_EXPRESSION=mark == 'Lexus' and year >= 2023 and price < 60000
```

### Flat Sell / Flat Rent

| Field     | Type   | Description                    |
|-----------|--------|--------------------------------|
| `region`  | String | District, e.g. `'Плявниеки'`   |
| `address` | String | Street address                 |
| `series`  | String | Building series, e.g. `'Нов.'` |
| `rooms`   | int    | Number of rooms                |
| `area`    | int    | Area in m²                     |
| `floor`   | int    | Floor number                   |
| `floors`  | int    | Total floors in the building   |
| `price`   | int    | Price in EUR                   |
| `ppm2`    | int    | Price per m² in EUR            |

```shell
SS_PARSER_FLAT_SELL_EXPRESSION=region matches 'Плявниеки|Пурвциемс' and rooms > 3 and area > 100
```

### Home / House

| Field     | Type   | Description         |
|-----------|--------|---------------------|
| `region`  | String | District            |
| `address` | String | Street address      |
| `area`    | int    | Building area in m² |
| `floors`  | int    | Number of floors    |
| `land`    | double | Land area in m²     |
| `price`   | int    | Price in EUR        |

```shell
SS_PARSER_HOME_EXPRESSION=region matches 'Дарзциемс|Плявниеки|Пурвциемс' and area > 200 and land > 600
```

### Notes

- String comparisons are case-sensitive; use `matches` with a regex for multi-value or case-insensitive matching.
- A missing field (e.g. `engine` not listed in the ad) is parsed as `0` for numbers and `''` for strings.
- Expressions can be updated at runtime via the web dashboard or `PATCH /api/tasks/{name}` without restarting.
- Type references (`T(...)`), bean references (`@bean`), and constructors are not permitted in expressions.

## Building for a Specific Architecture

By default `./gradlew jibDockerBuild` builds for the host architecture using JRE 25. Pass `-Parch=<arch>` to target
a different platform. The JRE version is selected automatically - ARM 32-bit uses JRE 17 (the last LTS with `arm/v7`
support), everything else uses JRE 25. The image tag gets an `-<arch>` suffix.

```shell
# Host architecture, JRE 25
./gradlew jibDockerBuild

# ARM 32-bit, JRE 17
./gradlew jibDockerBuild -Parch=arm

# ARM 64-bit, JRE 25
./gradlew jibDockerBuild -Parch=arm64
```
