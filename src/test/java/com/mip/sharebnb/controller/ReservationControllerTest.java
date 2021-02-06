package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
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

import javax.validation.constraints.NotNull;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].checkInDate").value("2020-01-18"))
                .andExpect(jsonPath("$.[0].checkoutDate").value("2020-01-20"))
                .andExpect(jsonPath("$.[0].accommodationDto.city").value("서울시"))
                .andExpect(jsonPath("$.[0].accommodationDto.gu").value("강남구"))
                .andExpect(jsonPath("$.[0].accommodationDto.accommodationPictures.[0].url").value("picture"));

    }

    @Test
    void updateReservation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Reservation.builder()
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 22))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkInDate").value("2020-02-20"))
                .andExpect(jsonPath("$.checkoutDate").value("2020-02-22"))
                .andExpect(jsonPath("$.guestNum").value(3))
                .andExpect(jsonPath("$.totalPrice").value(30000))
                .andReturn();

        logger.info(result::toString);

    }

}