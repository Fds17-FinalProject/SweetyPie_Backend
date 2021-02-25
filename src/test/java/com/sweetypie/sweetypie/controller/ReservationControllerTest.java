package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.dto.ReservationDto;
import com.sweetypie.sweetypie.service.AuthService;
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

    private String token;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void getToken() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);
    }

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
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
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test12345@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservation")
                .header("Authorization", token))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @DisplayName("예약하기")
    @Test
    void makeAReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2100, 5, 11))
                        .checkoutDate(LocalDate.of(2100, 5, 12))
                        .accommodationId(1L)
                        .totalGuestNum(4)
                        .adultNum(2)
                        .childNum(1)
                        .infantNum(1)
                        .totalPrice(52800)
                        .build())))
                .andExpect(status().isOk());
    }

    @DisplayName("예약하기 요청된 Id 값으로 정보를 찾을 수 없을 때 예외")
    @Test
    void makeAReservationDataNotFoundException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .accommodationId(0L)// 0인 숙박정보가 없어서 예외발생
                        .reservationId(1L)
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 20))
                        .totalGuestNum(2)
                        .adultNum(2)
                        .childNum(0)
                        .infantNum(0)
                        .totalPrice(11000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약하기 totalGuestNum or totalPrice validation 예외")
    @Test
    void makeAReservationInvalidationException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .accommodationId(1L)
                        .reservationId(1L)
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(-1)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약하기 현재 날짜이전으로 예약이 들어왔을 때 예외")
    @Test
    void makeAReservationInvalidDateException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2020, 3, 20))
                        .checkoutDate(LocalDate.of(2020, 3, 23))
                        .totalGuestNum(2)
                        .adultNum(2)
                        .childNum(0)
                        .infantNum(0)
                        .accommodationId(1L)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약하기 중복된 예약 날짜가 들어왔을 때 예외")
    @Test
    void makeAReservationDuplicateValueException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(2)
                        .adultNum(2)
                        .childNum(0)
                        .infantNum(0)
                        .accommodationId(1L)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약하기 숙박의 수용인원보다 예약된 최대인원이 초과하였을 때 예외")
    @Test
    void makeAReservationToTalGuestNumGreaterThanCapacity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .accommodationId(1L)
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(8)
                        .adultNum(4)
                        .childNum(4)
                        .infantNum(0)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약하기 총 가격이 맞지 않을 때 예외")
    @Test
    void makeAReservationCheckTotalPriceDiscrepancies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .accommodationId(1L)
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(2)
                        .adultNum(2)
                        .childNum(0)
                        .infantNum(0)
                        .totalPrice(10000)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 변경")
    @Test
    void updateReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2027, 2, 25))
                        .checkoutDate(LocalDate.of(2027, 2, 28))
                        .totalGuestNum(4)
                        .adultNum(2)
                        .childNum(1)
                        .infantNum(1)
                        .totalPrice(138400)
                        .build())))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 변경시 예약내역을 찾을 수 없을 때 예외")
    @Test
    void updateReservationDataNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/100")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(4)
                        .adultNum(2)
                        .childNum(1)
                        .infantNum(1)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("예약 변경시 중복된 날짜 예외")
    @Test
    void updateReservationDuplicateDateException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(4)
                        .adultNum(2)
                        .childNum(1)
                        .infantNum(1)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 변경을 요청한 회원정보와 예약된 회원정보의 불일치 예외")
    @Test
    void updateReservationInconsistencyBetweenRequestMemberAndReservedMember() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test12345@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 2, 20))
                        .checkoutDate(LocalDate.of(2022, 2, 22))
                        .totalGuestNum(4)
                        .adultNum(2)
                        .childNum(1)
                        .infantNum(1)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 변경 숙박의 수용 가능한 인원보다 예약된 최대인원을 초과하였을 때 예외")
    @Test
    void updateReservationTotalGuestNumGreaterThanCapacity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservation/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ReservationDto.builder()
                        .checkInDate(LocalDate.of(2022, 3, 20))
                        .checkoutDate(LocalDate.of(2022, 3, 22))
                        .totalGuestNum(8)
                        .adultNum(4)
                        .childNum(4)
                        .infantNum(0)
                        .totalPrice(95600)
                        .build())))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약 취소")
    @Test
    void deleteReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservation/1")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @DisplayName("예약 취소를 요청한 회원정보와 예약된 회원정보의 불일치 예외")
    @Test
    void deleteReservationInconsistencyBetweenRequestMemberAndReservedMember() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test12345@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservation/1")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

}