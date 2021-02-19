package com.sweetypie.sweetypie.security.jwt;

import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
@Transactional
class TokenProviderTest {

    @Autowired
    AuthService authService;

    @Autowired
    TokenProvider tokenProvider;

    private String token;

    @BeforeEach
    void getToken() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        token = authService.login(loginDto);
    }

    @Test
    void parseTokenToGetUserIdTest() {
        long userId = tokenProvider.parseTokenToGetUserId(token);
        System.out.println("userID : >>>>>>" + userId);

    }

}