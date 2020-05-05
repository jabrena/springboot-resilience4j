package com.jab.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class MainConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {

        //https://github.com/resilience4j/resilience4j/blob/master/resilience4j-circuitbreaker/src/main/java/io/github/resilience4j/circuitbreaker/CircuitBreakerConfig.java
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            //.slidingWindowType(COUNT_BASED)
            //.minimumNumberOfCalls(2)
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slidingWindowSize(2)
            //.slidingWindow()
            //.automaticTransitionFromOpenToHalfOpenEnabled(true)
            //.permittedNumberOfCallsInHalfOpenState()
            //.slowCallDurationThreshold()
            //.slowCallRateThreshold()
            .build();
        return circuitBreakerConfig;
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(
            CircuitBreakerConfig circuitBreakerConfig,
            CircuitBreakerRegistry circuitBreakerRegistry) {

        return factory -> {
            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
            factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
        };
    }
}
