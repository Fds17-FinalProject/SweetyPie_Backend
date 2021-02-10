package com.mip.sharebnb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
                .andExpect(jsonPath("$.[0].isWrittenReview").value(true))
                .andExpect(jsonPath("$.[0].accommodationDto.city").value("서울시"))
                .andExpect(jsonPath("$.[0].accommodationDto.gu").value("강남구"))
                .andExpect(jsonPath("$.[0].accommodationDto.accommodationPictures.[0].url").value("picture"));
    }

    @Test
    void getReservationsIfNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation/10"))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    void makeAReservation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 4, 20))
                        .checkoutDate(LocalDate.of(2022, 4, 23))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        logger.info(result::toString);

    }

    @DisplayName("Id 값이 잘 못 들어와 객체를 찾을 수 없을 때 예외")
    @Test
    void makeAReservationDataNotFoundException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .memberId(0L) // 0인 객체가 없어서 에러발생
                        .accommodationId(1L)
                        .reservationId(1L)
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 20))
                        .guestNum(1)
                        .totalPrice(11000)
                        .build())))
                .andExpect(status().isNotFound());
    }

    @DisplayName("guestNum or totalPrice 값이 잘 못 들어왔을 때 예외")
    @Test
    void makeAReservationInvalidationException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .memberId(1L)
                        .accommodationId(1L)
                        .reservationId(1L)
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 20))
                        .guestNum(-1)
                        .totalPrice(11000) // 음수 값이 올 수 없어서 에러 발생
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("현재 날짜이전으로 예약이 들어왔을 때 예외")
    @Test
    void makeAReservationInvalidDateException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2020, 3, 20))
                        .checkoutDate(LocalDate.of(2020, 3, 23))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("중복된 예약 날짜가 들어왔을 때 예외")
    @Test
    void makeAReservationDuplicateValueException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 3, 22))
                        .checkoutDate(LocalDate.of(2022, 3, 24))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReservation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Reservation.builder()
                        .checkInDate(LocalDate.of(2021, 2, 25))
                        .checkoutDate(LocalDate.of(2021, 2, 28))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        logger.info(result::toString);

    }

    @Test
    void updateReservationNotFoundReservationException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Reservation.builder()
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 22))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateReservationDuplicateDateException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Reservation.builder()
                        .checkInDate(LocalDate.of(2021, 2, 28))
                        .checkoutDate(LocalDate.of(2021, 3, 2))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservation/1"))
                .andExpect(status().isOk());
    }

}