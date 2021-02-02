package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class ReservationControllerTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @Transactional
    void getReservations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].checkInDate").value("2020-01-14"))
                .andExpect(jsonPath("$.[0].guestNum").value(8))
                .andExpect(jsonPath("$.[0].accommodation.accommodationType").value("집전체"));

    }

    @Test
    void updateReservation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReservationDto("1L", "1L", "2020-02-10", "2020-02-12", "5", "10000"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkInDate").value("2020-02-10"))
                .andExpect(jsonPath("$.checkoutDate").value("2020-02-12"))
                .andExpect(jsonPath("$.guestNum").value("5"))
                .andExpect(jsonPath("$.totalPrice").value("10000"))
                .andReturn();

        logger.info(result::toString);
    }
}