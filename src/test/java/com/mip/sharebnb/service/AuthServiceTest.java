package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.GoogleMemberDto;
import com.mip.sharebnb.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")

class AuthServiceTest {

    AuthService authService;
    TokenProvider tokenProvider;

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

}