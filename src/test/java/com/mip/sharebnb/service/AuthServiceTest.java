package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.GoogleMemberDto;
import com.mip.sharebnb.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")

class AuthServiceTest {

    AuthService authService;
    TokenProvider tokenProvider;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AuthServiceTest(AuthService authService, TokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @Test
    void signupBeforeGoogleLoginTest() {
        GoogleMemberDto memberDto = GoogleMemberDto.builder()
                                    .email("googlemail@gmail.com")
                                    .name("google")
                                    .birthDate(LocalDate.now())
                                    .contact("0102223333")
                                    .socialId("20123123123")
                                    .build();
        String token = authService.signupBeforeGoogleLogin(memberDto);

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(authentication.getName()).isEqualTo(memberDto.getEmail());
    }

    @Test
    void logoutTest() {
        String token = "ThisIsTestToken1234";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);

        authService.logout(request);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String result = valueOperations.get(token);

        assertThat(result).isEqualTo(token);
    }

    @Test
    void isInTheInvalidTokenListTest() {
        String token = "isInTheInvalidTokenListTest1234";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, token, Duration.ofSeconds(10));

        boolean result = authService.isInTheInvalidTokenList(request);

        assertThat(result).isTrue();
    }

}