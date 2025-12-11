# [POC] ApiCaller

## [Table of Contents]

- [Implementation](#implementation)
- [Usage](#usage)
    - [Example](#example)
    - [Spring RestClient](#restclient)
    - [Unirest](#unirest)
- [Configuration](#configuration)
    - [Default headers](#default-headers)
    - [Log masking](#log-masking)
- [Logs](#logs)

## Introduction

Wrapper for REST clients with built-in logging and log masking mechanism.

Details:

- common `ApiCaller` interface
- built-in request interceptors with logging/log masking
- common `Incoming Request` logging using Spring `OncePerRequestFilter`
- individual `Request`/`Response` logging implementation with common output format
- common log masking processor

## Implementation

Each client is an implementation on `ApiCaller` interface.

> - [x] Incoming request
    [[Reference]](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html)
> - [x] Spring RestClient
    [[Reference]](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
> - [ ] Spring WebClient
    [[Reference]](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)
>- [ ] Spring RestTemplate
   [[Reference]](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate)
> - [x] Kong Unirest
    [[Reference]](https://kong.github.io/unirest-java/)

```java
public interface ApiCaller {

  <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  ResponseEntity<Void> delete(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull String client);
}
```

> `ApiCaller` interface provides additional default methods to skip not required arguments.

## Usage

### Example

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "my.awesome.app",
    "com.rromanowicz.apicaller"
})
public class MyAwesomeApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyAwesomeApplication.class, args);
  }
}
```

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.rromanowicz.apicaller.core.ApiCaller;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TestService {

  private final ApiCaller apiCaller;

  public List<User> fetchUsers() {
    return apiCaller.get("https://fakestoreapi.com/users", Collections.emptyMap(),
        Collections.emptyMap(), new TypeReference<>() {
        });
  }

  public User addUser() {
    return apiCaller.post("https://fakestoreapi.com/users", """
              {
                "id": 567,
                "username": "Test",
                "email": "test@zxc.com",
                "password": "ASD234"
              }
            """, Collections.emptyMap(),
        new TypeReference<>() {
        }
    );
  }

  public record User(Integer id, String username, String email, String password) {

  }
}
```

### Dependency

#### Incoming Requests

```xml

<dependency>
  <groupId>com.rromanowicz.apicaller</groupId>
  <artifactId>incoming</artifactId>
  <version>${apiCaller.version}</version>
</dependency>
```

#### RestClient

```xml

<dependency>
  <groupId>com.rromanowicz.apicaller</groupId>
  <artifactId>restclient</artifactId>
  <version>${apiCaller.version}</version>
</dependency>
```

#### Unirest

```xml

<dependency>
  <groupId>com.rromanowicz.apicaller</groupId>
  <artifactId>unirest</artifactId>
  <version>${apiCaller.version}</version>
</dependency>
```

### Multiple client setup

If needed (because of reasons) it's possible to inject multiple clients using `@Qualifier`
annotation.

```java

@RequiredArgsConstructor
@Service
public class TestService {

  @Qualifier("uniRestCaller")
  private final ApiCaller uniRestCaller;
  @Qualifier("restClientCaller")
  private final ApiCaller restClientCaller;

}
```

## Configuration

Configuration is read from application properties file.

### Default properties

```yaml
logging:
  level:
    com:
      rromanowicz:
        apicaller: INFO

app:
  logging:
    enabled:
      incoming: true
      outgoing: true
    payload:
      incoming:
        request: true
        response: true
      outgoing:
        request: true
        response: true
```

### Log levels

Request/Response logs are set as INFO. Additional logs are available on DEBUG level.

WARN logs will be displayed if error occurs during processing.

```yaml

logging:
  level:
    com:
      rromanowicz:
        apicaller: INFO
```

### Default headers

Default headers are added to every call.

Property name: `app.request.headers`

#### Example

```yaml
app:
  request:
    headers:
      h1: asd
      h2: zxc
      h3: 123
```

### Log masking

Masking is configured/applied based on request url.

Property name: `app.logging.masking`

> Separate configuration is required for incoming/outgoing request and response.

#### Example

```yaml
  logging:
    masking:
      outgoing:
        - url: "fakestoreapi.com/users"
          request:
            body:
              - pattern: "(.*\\\"username\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"email\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"password\\\":\\\")(.*?)(\\\".*)"
                substitution: "$1***$3"
            header:
              - name: "h1"
                replacement: ""
                remove: true
              - name: "h2"
                replacement: "***"
                remove: false
          response:
            body:
              - pattern: "(.*\\\"username\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"email\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"password\\\":\\\")(.*?)(\\\".*)"
                substitution: "$1***$3"
      incoming:
        - url: "/v1/test-x"
          request:
            body:
              - pattern: "(.*\\\"username\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"email\\\":\\\".)(.*?)(.\\\".*)"
                substitution: "$1***$3"
              - pattern: "(.*\\\"password\\\":\\\")(.*?)(\\\".*)"
                substitution: "$1***$3"

```

## Logs

Logs are printed as a JSON object containing both request and response.

<details>

<summary>Sample output</summary>

```json
{
  "request": {
    "method": "GET",
    "url": "https://fakestoreapi.com/users",
    "headers": [
      {
        "h2": "***"
      },
      {
        "h3": "123"
      },
      {
        "Content-Length": "0"
      }
    ]
  },
  "response": {
    "status": 200,
    "body": [
      {
        "id": 1,
        "username": "j***d",
        "email": "j***m",
        "password": "***"
      },
      {
        "id": 2,
        "username": "m***4",
        "email": "m***m",
        "password": "***"
      },
      {
        "id": 3,
        "username": "k***n",
        "email": "k***m",
        "password": "***"
      },
      {
        "id": 4,
        "username": "d***o",
        "email": "d***m",
        "password": "***"
      },
      {
        "id": 5,
        "username": "d***k",
        "email": "d***m",
        "password": "***"
      },
      {
        "id": 6,
        "username": "d***r",
        "email": "d***m",
        "password": "***"
      },
      {
        "id": 7,
        "username": "s***r",
        "email": "m***m",
        "password": "***"
      },
      {
        "id": 8,
        "username": "h***s",
        "email": "w***m",
        "password": "***"
      },
      {
        "id": 9,
        "username": "k***h",
        "email": "k***m",
        "password": "***"
      },
      {
        "id": 10,
        "username": "j***k",
        "email": "j***m",
        "password": "***"
      }
    ]
  }
}
```

```json
{
  "request": {
    "method": "POST",
    "url": "https://fakestoreapi.com/users",
    "body": {
      "id": 567,
      "username": "T***t",
      "email": "t***m",
      "password": "***"
    },
    "headers": [
      {
        "h2": "***"
      },
      {
        "h3": "123"
      },
      {
        "Content-Type": "text/plain;charset=ISO-8859-1"
      },
      {
        "Content-Length": "101"
      }
    ]
  },
  "response": {
    "status": 201,
    "body": {
      "id": 1
    }
  }
}
```

</details>
