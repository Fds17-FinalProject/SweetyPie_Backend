package com.sweetypie.sweetypie.security.jwt;

import com.sweetypie.sweetypie.dto.LoginDto;
import com.sweetypie.sweetypie.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
@Transactional
class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    AuthService authService;

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    private final LoginDto loginDto = new LoginDto("test123@gmail.com","12345678a!", false );

    private String token;

    @BeforeEach
    void setting() {
        token = authService.login(loginDto);
    }

    @DisplayName("토크생성-성공")
    @Test
    void createTokenTest() {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String result = tokenProvider.createToken(authentication);
        assertThat(result).isNotEmpty();
    }

    @DisplayName("토큰에서MemberId얻기-성공")
    @Test
    void parseTokenToGetMemberIdTest() {
        Long result = tokenProvider.parseTokenToGetMemberId(token);
        assertThat(result).isEqualTo(1L);
    }

    @DisplayName("토큰에서Authentication얻기-성공")
    @Test
    void getAuthenticationTest() {

        Authentication result = tokenProvider.getAuthentication(token);

        assertThat(result.getName()).isEqualTo("test123@gmail.com");
        assertThat(result.getCredentials()).isEqualTo(token);
    }

    @DisplayName("토큰검증-성공")
    @Test
    void validateTokenTest() {
        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @DisplayName("토큰검증-실패-만료된토큰")
    @Test
    void validateTokenExpiredFailTest() {
        token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZW1iZXJAbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9NRU1CRVIiLCJtZW1iZXJJZCI6NywiZXhwIjoxNjEzODgwNDk0fQ.MRQls_oiIS1VKtvOwSrF_TAsriHpd09pfgnFSn6CvYoxEaaUFpAHs2QNVIdf4uC8xrV_PXgSDRUpYwtgcam4Bw";
        boolean result = tokenProvider.validateToken(token);
        assertThat(result).isFalse();
    }

    @DisplayName("토큰검증-실패-로그아웃된토큰")
    @Test
    void validateTokenLogoutTokenFailTest() {
        authService.logout(JwtFilter.HEADER_PREFIX+token);
        boolean result = tokenProvider.validateToken(token);
        assertThat(result).isFalse();
    }
}