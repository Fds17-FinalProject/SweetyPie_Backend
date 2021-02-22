package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweetypie.sweetypie.dto.*;
import com.sweetypie.sweetypie.security.jwt.JwtFilter;
import com.sweetypie.sweetypie.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {

        String jwt = authService.login(loginDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestParam(value = "code") String authCode) throws JsonProcessingException {

        Map<String, String> map;
            map = authService.googleLogin(authCode);
        // 로그인 되면 token 리턴
        if (map.get("token")!=null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + map.get("token"));
            return new ResponseEntity<>(map, httpHeaders,  HttpStatus.OK);
        // 로그인 안되면 회원정보를 리턴
        } else {
            return ResponseEntity.ok(map);
        }
    }

    @PostMapping ("/login/google")
    public ResponseEntity<TokenDto> signupBeforeGoogleLogin(@Valid @RequestBody GoogleMemberDto memberDto) {

        String token = authService.signupBeforeGoogleLogin(memberDto);

        return ResponseEntity.ok(new TokenDto(token));
    }

    @GetMapping("/logout")
    @PreAuthorize("authenticated")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }
}
