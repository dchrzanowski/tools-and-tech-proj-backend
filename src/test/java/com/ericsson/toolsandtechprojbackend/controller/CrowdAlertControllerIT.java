package com.ericsson.toolsandtechprojbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrowdAlertControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getActiveAlerts_returns200() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk());
    }

    @Test
    void getActiveAlerts_returnsOnlyActiveAlerts() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("ACTIVE"))));
    }

    @Test
    void getActiveAlerts_containsMainStageAlert() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].area.name", hasItem("Main Stage")))
                .andExpect(jsonPath("$[*].message", hasItem("Main Stage is at full capacity")));
    }
}
