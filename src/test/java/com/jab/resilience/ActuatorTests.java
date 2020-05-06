package com.jab.resilience;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActuatorTests {

    @LocalServerPort
    int port;

    @Test
    public void given_app_when_callActuator_then_expectedResults() {

        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate.
            getForEntity(getAddress(), String.class);
        LOGGER.info(response.getBody());

        then(response.getStatusCode()).isEqualTo(HttpStatus.PERMANENT_REDIRECT);
    }

    private String getAddress() {
        return "http://localhost:" + port + "/";
    }

}
