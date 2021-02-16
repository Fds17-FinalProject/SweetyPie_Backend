package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.BookmarkDto;
import com.mip.sharebnb.dto.GoogleMemberDto;
import com.mip.sharebnb.dto.LoginDto;
import com.mip.sharebnb.dto.MemberDto;
import com.mip.sharebnb.exception.InvalidTokenException;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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

        assertThrows(InvalidTokenException.class, () -> authService.isInTheInvalidTokenList(request));
    }

    @Test
    void loginServiceTest() {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test123@gmail.com");
        loginDto.setPassword("1234");

        String token = authService.login(loginDto);

        assertThat(token).isNotEmpty();
    }

}