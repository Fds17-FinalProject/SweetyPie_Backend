package com.mip.sharebnb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mip.sharebnb.dto.GoogleMemberDto;
import com.mip.sharebnb.dto.LoginDto;
import com.mip.sharebnb.dto.TokenDto;
import com.mip.sharebnb.exception.MemberAlreadySignupException;
import com.mip.sharebnb.security.jwt.JwtFilter;
import com.mip.sharebnb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
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

    @GetMapping("login/google")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestParam(value = "code") String authCode) throws JsonProcessingException {

        Map<String, String> map;
        // 이미 가입되있다면 409 리턴
        try {
            map = authService.googleLogin(authCode);
        } catch (MemberAlreadySignupException e) {

            return new ResponseEntity<>(new HashMap<>(), HttpStatus.CONFLICT);
        }
        // 로그인 되면 200과 token 리턴
        if (map.get("token")!=null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.HEADER_PREFIX + map.get("token"));
            return new ResponseEntity<>(map, httpHeaders,  HttpStatus.OK);
        // 로그인 안되면 203과 memberDto 리턴
        } else {
            return new ResponseEntity<>(map, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
    }

    @PostMapping ("login/google")
    public ResponseEntity<TokenDto> signupBeforeGoogleLogin(@Valid @RequestBody GoogleMemberDto memberDto) {

        String token = authService.signupBeforeGoogleLogin(memberDto);

        return ResponseEntity.ok(new TokenDto(token));
    }
}
