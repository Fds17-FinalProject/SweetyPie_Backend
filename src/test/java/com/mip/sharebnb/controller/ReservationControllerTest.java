package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class ReservationControllerTest {

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

    @DisplayName("예약 내역 조회")
    @Test
    void getReservations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].checkInDate").value("2022-02-10"))
                .andExpect(jsonPath("$.[0].checkoutDate").value("2022-02-12"))
                .andExpect(jsonPath("$.[0].isWrittenReview").value(true))
                .andExpect(jsonPath("$.[0].city").value("서울특별시"))
                .andExpect(jsonPath("$.[0].gu").value("마포구"))
                .andExpect(jsonPath("$.[0].accommodationPicture.url").value("picture"));
    }

    @DisplayName("예약 내역 조회시 없을 때")
    @Test
    void getReservationsIfNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation/10"))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @DisplayName("예약하기")
    @Test
    void makeAReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2100, 5, 11))
                        .checkoutDate(LocalDate.of(2100, 5, 12))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk());
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
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 20))
                        .guestNum(-1)
                        .totalPrice(11000) // 음수 값이 올 수 없어서 에러 발생
                        .build())))
                .andExpect(status().isNotFound());
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
                .andExpect(status().isNotFound());
    }

    @DisplayName("중복된 예약 날짜가 들어왔을 때 예외")
    @Test
    void makeAReservationDuplicateValueException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .guestNum(3)
                        .memberId(1L)
                        .accommodationId(1L)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 수정")
    @Test
    void updateReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2027, 2, 25))
                        .checkoutDate(LocalDate.of(2027, 2, 28))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 변경시 예약내역을 찾을 수 없을 때 예외")
    @Test
    void updateReservationDataNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2020, 2, 20))
                        .checkoutDate(LocalDate.of(2020, 2, 22))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isNotFound());

    }

    @DisplayName("예약 변경시 중복된 날짜 예외")
    @Test
    void updateReservationDuplicateDateException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .guestNum(3)
                        .totalPrice(30000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 취소")
    @Test
    void deleteReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservation/1"))
                .andExpect(status().isOk());
    }

}