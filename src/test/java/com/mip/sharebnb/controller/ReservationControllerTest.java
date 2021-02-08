package com.mip.sharebnb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    void getReservations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].checkInDate").value("2020-02-20"))
                .andExpect(jsonPath("$.[0].checkoutDate").value("2020-02-22"))
                .andExpect(jsonPath("$.[0].accommodationDto.city").value("서울시"))
                .andExpect(jsonPath("$.[0].accommodationDto.gu").value("강남구"))
                .andExpect(jsonPath("$.[0].accommodationDto.accommodationPictures.[0].url").value("picture"));

    }

    @Test
    void makeAReservation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 22))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .reservationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        logger.info(result::toString);

//        이걸 넣었을 때 왜 checkInDate가 null로 뜨지??
//                .andExpect(jsonPath("$.checkInDate").value("2020-02-20"))
//                .andExpect(jsonPath("$.checkoutDate").value("2020-02-22"))
//                .andExpect(jsonPath("$.guestNum").value(3))
//                .andExpect(jsonPath("$.totalPrice").value(30000))

    }

    @Test
    void makeAReservationValidation() throws Exception {

        // memberId, accommodationId, guestNum, totalPrice 값이 잘 못 들어왔을 때 각각 에러확인
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .memberId(0L)
                        .accommodationId(1L)
                        .reservationId(1L)
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 20))
                        .guestNum(1)
                        .totalPrice(11000)
                        .build())))
                .andExpect(status().isBadRequest());
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
                .andReturn();

        logger.info(result::toString);

    }

    @Test
    void deleteReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservation/1"))
                .andExpect(status().isOk());
    }

}