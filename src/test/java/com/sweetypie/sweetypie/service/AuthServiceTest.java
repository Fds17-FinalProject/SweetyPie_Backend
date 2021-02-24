package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.GoogleMemberDto;
import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

    @DisplayName("로그인-성공")
    @Test
    void loginServiceTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678a!");

        String token = authService.login(loginDto);

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(authentication.getName()).isEqualTo(loginDto.getEmail());
    }

    @DisplayName("로그인-실패-비밀번호불일치")
    @Test
    void loginServicePasswordFailTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("12345678");
        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> authService.login(loginDto));

        assertThat(ex.getMessage().contains("자격 증명에 실패하였습니다.")).isTrue();
    }

    @DisplayName("로그인-실패-잘못된이메일로접근")
    @Test
    void loginServiceEmailFailTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123");
        loginDto.setPassword("12345678a!");
        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> authService.login(loginDto));

        assertThat(ex.getMessage().contains("로그인할 멤버가 존재하지 않습니다.")).isTrue();
    }

    @DisplayName("로그인-실패-구글회원으로일반회원로그인")
    @Test
    void loginServiceGoogleMemberFailTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("google@gmail.com");
        loginDto.setPassword("12345678a!");
        InputNotValidException ex = assertThrows(InputNotValidException.class, () -> authService.login(loginDto));

        assertThat(ex.getMessage().contains("구글회원은 구글로 로그인하기를 이용해주세요")).isTrue();
    }

    @DisplayName("구글가입이후로그인-성공")
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

    @DisplayName("구글가입이후로그인-실패-중복이메일로회원가입시도")
    @Test
    void signupBeforeGoogleLoginDuplicatedEmailFailTest() {
        GoogleMemberDto memberDto = GoogleMemberDto.builder()
                .email("test123@gmail.com")
                .name("google")
                .birthDate(LocalDate.now())
                .contact("0102223333")
                .socialId("20123123123")
                .build();

        DuplicateValueExeption ex = assertThrows(DuplicateValueExeption.class, () -> authService.signupBeforeGoogleLogin(memberDto));

        assertThat(ex.getMessage().contains("이미 사용되고 있는 Email 입니다.")).isTrue();
    }

    @DisplayName("로그아웃테스트")
    @Test
    void logoutTest() {
        String token = "ThisIsTestToken1234";
        authService.logout(token);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String result = valueOperations.get(token);

        assertThat(result).isEqualTo(token);
    }


}