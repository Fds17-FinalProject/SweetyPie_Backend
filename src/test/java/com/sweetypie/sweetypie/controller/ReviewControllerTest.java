package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.dto.ReviewDto;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    private String token1;
    private String token2;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        token1 = authService.login(loginDto);

        loginDto.setEmail("test12345@gmail.com");
        loginDto.setPassword("12345678a!");

        token2 = authService.login(loginDto);
    }

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void getReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/1")
                .header("Authorization", token1))
                .andExpect(status().isOk());
    }

    @DisplayName("작성한 리뷰 가져오기 (토큰 없음)")
    @Test
    void getReviewException1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/1"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("작성한 리뷰 가져오기 (없는 리뷰)")
    @Test
    void getReviewException2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/100")
                .header("Authorization", token1))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("작성한 리뷰 가져오기 (다른 작성자)")
    @Test
    void getReviewException3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/1")
                .header("Authorization", token2))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("리뷰 등록하기")
    @Test
    void postReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 100L, 3, "좋아요"))))
                .andExpect(status().isOk());
    }

    @DisplayName("리뷰 등록하기 (중복)")
    @Test
    void postReviewException1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 1L, 3, "좋아요"))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("리뷰 등록하기 (없는 예약)")
    @Test
    void postReviewException2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 1000L, 3, "좋아요"))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("리뷰 등록하기 (없는 숙소)")
    @Test
    void postReviewException3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(100L, 1L, 3, "좋아요"))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("리뷰 등록하기 (다른 회원)")
    @Test
    void postReviewException4() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .header("Authorization", token2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 1L, 3, "좋아요"))))
                .andExpect(status().isBadRequest());
    }
}