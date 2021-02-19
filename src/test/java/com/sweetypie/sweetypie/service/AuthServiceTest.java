package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.GoogleMemberDto;
import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
@Transactional
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @DisplayName("구글가입이후로그인")
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

    @DisplayName("로그아웃테스트")
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
    void loginServiceTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        String token = authService.login(loginDto);

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(authentication.getName()).isEqualTo(loginDto.getEmail());
    }
}