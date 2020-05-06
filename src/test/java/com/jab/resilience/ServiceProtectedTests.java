package com.jab.resilience;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.jab.resilience.Constants.CircuitBreaker1;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ServiceProtectedTests {

    //State machine stages
    private static final String FIRST_STATE = Scenario.STARTED;
    private static final String SECOND_STATE = "second";
    private static final String THIRD_STATE = "third";

    static int port = 8090;

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup () {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @AfterEach
    public void teardown () {
        wireMockServer.stop();
        //transitionToClosedState("CB1");
    }

    @Autowired
    private ServiceProtected service;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

        /*
    @Test
    public void given_normalScenario_when_retrieve_then_Ok() {

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
            .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("greek.json")));

        then(service.retrieve("http://localhost:8090/greek")).isNotNull();
    }


    @Test
    public void given_normalScenario_when_forceOpen_then_Ko() {

        createStateMachine();

        checkHealthStatus("CB1", CircuitBreaker.State.CLOSED);

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");

        checkHealthStatus("CB1", CircuitBreaker.State.OPEN);
    }

     */

    @Test
    public void given_normalScenario_when_forceOpenAndWait_then_Ok() {

        createStateMachine();

        checkHealthStatus(CircuitBreaker1, CircuitBreaker.State.CLOSED);

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");

        //TODO Improve this line
        sleep(1);

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");

        //TODO Review the configuraiton better
        checkHealthStatus(CircuitBreaker1, CircuitBreaker.State.HALF_OPEN);
    }

        /*
    @Test
    public void given_openState_when_retrieve_then_Ko() {

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
            .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("greek.json")));

        transitionToOpenState("CB1");

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");

        checkHealthStatus("CB1", CircuitBreaker.State.OPEN);
    }


    @Test
    public void given_closeState_when_retrieve_then_Ok() {

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
            .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("greek.json")));

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");

        checkHealthStatus("CB1", CircuitBreaker.State.CLOSED);
    }

     */

    @SneakyThrows
    private void sleep(int seconds) {
        Thread.sleep(seconds * 1000);
    }

    private void createStateMachine() {
        createWireMockStub(FIRST_STATE, SECOND_STATE);
        createWireMockStub(SECOND_STATE, THIRD_STATE);
        createWireMockStub(THIRD_STATE, FIRST_STATE);
    }

    private void createWireMockStub(String currentState, String nextState) {

        if(currentState.equals(FIRST_STATE)) {
            wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
                .inScenario("java tips")
                .whenScenarioStateIs(currentState)
                .willSetStateTo(nextState)
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("greek.json")));
        } else if(currentState.equals(SECOND_STATE)) {
            wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
                .inScenario("java tips")
                .whenScenarioStateIs(currentState)
                .willSetStateTo(nextState)
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("greek.json")));
        } else {
            wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
                .inScenario("java tips")
                .whenScenarioStateIs(currentState)
                .willSetStateTo(nextState)
                .willReturn(WireMock.aResponse()
                    .withStatus(500)));
        }
    }

    private void checkHealthStatus(String circuitBreakerName, CircuitBreaker.State state) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        then(circuitBreaker.getState()).isEqualTo(state);
    }

    private void transitionToOpenState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToOpenState();
    }

    private void transitionToClosedState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        if(!circuitBreaker.getState().equals(CircuitBreaker.State.CLOSED)) {
            circuitBreaker.transitionToClosedState();
        }
    }
}
