package com.jab.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;

import static com.jab.resilience.Constants.CircuitBreaker1;

@Slf4j
@Service
public class ServiceProtectedImpl implements ServiceProtected {

    private RestTemplate restTemplate;
    private CircuitBreakerFactory circuitBreakerFactory;

    public ServiceProtectedImpl(RestTemplate restTemplate, CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public String retrieve(String url) {

         Function<String, List<String>>  circuitBreakerRetrieve = param -> {
             CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CircuitBreaker1);

             return circuitBreaker.run(() -> {
                    var response = restTemplate.exchange(
                        param,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>(){});
                    return response.getBody();
                },
                throwable -> List.of("KatakrokerGod"));
        };

        Function<List<String>, String> getFirst = list -> list.stream()
            .peek(LOGGER::info)
            .findFirst()
            .get();

        return circuitBreakerRetrieve.andThen(getFirst).apply(url);
    }

}
