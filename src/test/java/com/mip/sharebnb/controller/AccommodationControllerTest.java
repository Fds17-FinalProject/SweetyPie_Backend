package com.mip.sharebnb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class AccommodationControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void before(WebApplicationContext was) {
        mockMvc = MockMvcBuilders.webAppContextSetup(was)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("id로 숙박 검색")
    @Test
    void getAccommodation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("서울특별시"))
                .andExpect(jsonPath("$.gu").value("마포구"))
                .andExpect(jsonPath("$.contact").value("010-1234-1234"));
    }

    @DisplayName("모든 숙박 검색")
    @Test
    void getAllAccommodations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodations"))
                .andExpect(status().isOk());
    }

    @DisplayName("도시 명으로 숙박 검색")
    @Test
    void getAccommodationsByCity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodations/city/서울?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @DisplayName("메인 검색 기능 (검색어, 체크인, 체크아웃, 인원수)")
    @Test
    void getAccommodationsBySearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodations/search?searchKeyword=서울&checkIn=2021-04-06&checkout=2021-05-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("서울특별시"))
                .andExpect(jsonPath("$.content[0].gu").value("마포구"));
    }
}