package com.jab.resilience;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest
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
    }

    @Autowired
    private ServiceProtected service;

    @Test
    public void given_normalScenario_when_retrieve_then_Ok() {

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/greek"))
            .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("greek.json")));

        then(service.retrieve("http://localhost:8090/greek")).isNotNull();
    }

    @Test
    public void given_normalScenario_when_retrieveMultipleTimes_then_Ko() {

        createWireMockStub(FIRST_STATE, SECOND_STATE);
        createWireMockStub(SECOND_STATE, THIRD_STATE);
        createWireMockStub(THIRD_STATE, FIRST_STATE);

        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("KatakrokerGod");
        then(service.retrieve("http://localhost:8090/greek")).isEqualTo("Zeus");
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
}
