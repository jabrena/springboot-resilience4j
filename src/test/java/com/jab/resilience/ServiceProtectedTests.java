package com.jab.resilience;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
class ServiceProtectedTests {

    @Autowired
    private ServiceProtected service;

    @Test
    public void given_normalScenario_when_retrieve_then_Ok() {

        then(service.retrieve()).isNotNull();
    }

}