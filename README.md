# springboot-resilience4j

[![Build Status](https://travis-ci.org/jabrena/springboot-resilience4j.svg?branch=master)](https://travis-ci.org/jabrena/springboot-resilience4j)

## How to build?

```
chmod +x mvnw
./mvnw clean test
```

Other targets:

```
mvn clean test jacoco:report
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

## Documentation

- https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html
- https://resilience4j.readme.io/docs/getting-started-3
- https://spring.io/projects/spring-cloud-circuitbreaker#learn
- https://cloud.spring.io/spring-cloud-static/spring-cloud-circuitbreaker/2.0.0.M1/reference/html/

## References

- https://github.com/spring-cloud-samples/spring-cloud-circuitbreaker-demo

## Articulos

- https://piotrminkowski.com/2019/12/11/circuit-breaking-in-spring-cloud-gateway-with-resilience4j/

## Examples

- https://github.com/LearningByExample/testing-resilience
- https://github.com/resilience4j/resilience4j-spring-boot2-demo
- https://github.com/spring-cloud-samples/spring-cloud-circuitbreaker-demo/tree/master/spring-cloud-circuitbreaker-demo-resilience4j

## Books

- https://www.manning.com/books/reactive-design-patterns

## Others

- https://start.spring.io/

## Doubts

- How to implement the logs using Spring Boot objects?

```
            circuitBreaker..getEventPublisher()
                 .onSuccess(event -> LOGGER.info(event.toString()))
                 .onError(event -> LOGGER.info(event.toString()))
                 .onIgnoredError(event -> LOGGER.info(event.toString()))
                 .onReset(event -> LOGGER.info(event.toString()))
                 .onStateTransition(event -> LOGGER.info(event.toString()));

```
