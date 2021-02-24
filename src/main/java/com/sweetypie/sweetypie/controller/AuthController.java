package com.sweetypie.sweetypie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweetypie.sweetypie.dto.*;
import com.sweetypie.sweetypie.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {

        String token = authService.login(loginDto);

        return ResponseEntity.ok(new TokenDto(token));
    }

    @GetMapping("/login/google")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestParam(value = "code") String authCode) throws JsonProcessingException {

        Map<String, String> map;
            map = authService.googleLogin(authCode);
        // 로그인 되면 token 리턴
        if (map.get("token")!=null) {
            return ResponseEntity.ok(map);
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
