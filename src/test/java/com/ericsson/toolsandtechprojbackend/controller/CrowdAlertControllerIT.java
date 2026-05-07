package com.ericsson.toolsandtechprojbackend.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
class CrowdAlertControllerIT {

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }

    @Test
    void getActiveAlerts_returns200() {
        given()
        .when()
            .get("/alerts")
        .then()
            .statusCode(200);
    }

    @Test
    void getActiveAlerts_returnsOnlyActiveAlerts() {
        given()
        .when()
            .get("/alerts")
        .then()
            .statusCode(200)
            .body("status", everyItem(equalTo("ACTIVE")));
    }

    @Test
    void getActiveAlerts_containsMainStageAlert() {
        given()
        .when()
            .get("/alerts")
        .then()
            .statusCode(200)
            .body("area.name", hasItem("Main Stage"))
            .body("message", hasItem("Main Stage is at full capacity"));
    }
}
