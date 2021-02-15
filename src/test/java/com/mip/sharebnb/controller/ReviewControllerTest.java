package com.mip.sharebnb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mip.sharebnb.dto.ReviewDto;
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

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void getReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("리뷰 등록하기")
    @Test
    void postReview() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 1L, 2L, 3, "좋아요"))))
                .andExpect(status().isOk());
    }

    @DisplayName("리뷰 등록하기 (이미 작성)")
    @Test
    void postReview2() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post("/api/review")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        new ReviewDto(1L, 1L, 1L, 3, "좋아요"))))
                .andExpect(status().isBadRequest());
    }
}