package com.mip.sharebnb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
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
                .andExpect(jsonPath("$.city").value("강릉시"))
                .andExpect(jsonPath("$.gu").value("주문진읍"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.accommodationPictures", hasSize(5)))
                .andExpect(jsonPath("$.accommodationPictures[0].url").value("https://a0.muscache.com/pictures/7586ebad-aaf5-4aa6-b966-8660d4820b51.jpg"));

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodations/city/강릉?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @DisplayName("메인 검색 기능 (검색어, 체크인, 체크아웃, 인원수)")
    @Test
    void getAccommodationsBySearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accommodations/search?searchKeyword=강릉&checkIn=2021-02-03&checkout=2021-02-05&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("강릉시"))
                .andExpect(jsonPath("$.content[0].gu").value("주문진읍"));
    }
}