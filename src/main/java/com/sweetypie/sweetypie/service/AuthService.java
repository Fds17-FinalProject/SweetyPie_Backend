package com.sweetypie.sweetypie.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sweetypie.sweetypie.dto.*;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    final static String GOOGLE_TOKEN_BASE_URL = "https://oauth2.googleapis.com/token";
    final static String GOOGLE_REDIRECT_URL = "https://sweetypie.netlify.app/redirect/oauth2callback";
    final static String GOOGLE_REVOKE_TOKEN_BASE_URL = "https://oauth2.googleapis.com/revoke";

    @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds;

    @Value("${google.client_id}")
    String clientId;
    @Value("${google.client_secret}")
    String clientSecret;

    public String login(LoginDto loginDto) {

        checkMemberStatus(loginDto);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    private void checkMemberStatus(LoginDto loginDto) {
        Member member = memberRepository
                .findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("로그인할 멤버가 존재하지 않습니다."));
        if (member.isSocialMember() & !loginDto.isSocialMember()) {
            throw new InputNotValidException("구글회원은 구글로 로그인하기를 이용해주세요");
        }

    }

    public Map<String, String> googleLogin(String authCode) throws JsonProcessingException {

        Map<String, String> googleUserInfo = getGoogleUserInfo(authCode);

        GoogleMemberDto memberDto = pareUserInfoToGoogleMemberDto(googleUserInfo);

        // 로그인이 가능할 때 로그인을 해서 JWT 토큰을 리턴한다
        if (isLoginPossible(memberDto, googleUserInfo)) {
            LoginDto loginDto = new LoginDto(memberDto.getEmail(), memberDto.getSocialId(), true);
            Map<String, String> map = new HashMap<>();
            map.put("token", login(loginDto));

            return map;
        // 로그인이 불가능 할 때 회원가입 가능하게 회원정보를 리턴한다
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("email", memberDto.getEmail());
            map.put("name", memberDto.getName());
            map.put("socialId", memberDto.getSocialId());

            return map;
        }

    }

    public String signupBeforeGoogleLogin(GoogleMemberDto memberDto) {

        memberService.signupGoogleMember(memberDto);
        LoginDto loginDto = new LoginDto(memberDto.getEmail(), memberDto.getSocialId(), true);

        return login(loginDto);

    }

    public void logout(String token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, token, Duration.ofSeconds(tokenValidityInSeconds));
    }

    private Map<String, String> getGoogleUserInfo(String authCode) throws JsonProcessingException {

        //HTTP Request를 위한 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //요청시 파라미터는 스네이크 케이스로 세팅되므로 Object mapper에 미리 설정해준다.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        GoogleTokenResponseDto googleResponse = getGoogleResponse(restTemplate, mapper, authCode);

        return getGetUserInfoFromGoogleResponse(restTemplate, mapper, googleResponse);

    }

    private Map<String, String> getGetUserInfoFromGoogleResponse(RestTemplate restTemplate, ObjectMapper mapper, GoogleTokenResponseDto googleResponse) throws JsonProcessingException {
        //ID Token만 추출 (사용자의 정보는 jwt로 인코딩 되어있다)
        String jwtToken = googleResponse.getIdToken();
        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                .queryParam("id_token", jwtToken).encode().toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        Map<String, String> userInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>(){});
        userInfo.put("accessToken", googleResponse.getAccessToken());
        return userInfo;
    }

    private GoogleTokenResponseDto getGoogleResponse(RestTemplate restTemplate, ObjectMapper mapper, String authCode ) throws JsonProcessingException {
        //Google OAuth Access Token 요청을 위한 파라미터 세팅
        GoogleTokenRequestDto googleOAuthRequestParam = GoogleTokenRequestDto
                .builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(authCode)
                .redirectUri(GOOGLE_REDIRECT_URL)
                .grantType("authorization_code").build();


        //AccessToken 발급 요청
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(GOOGLE_TOKEN_BASE_URL, googleOAuthRequestParam, String.class);

        //Token Request
        return  mapper.readValue(resultEntity.getBody(), new TypeReference<GoogleTokenResponseDto>() {});
    }

    private GoogleMemberDto pareUserInfoToGoogleMemberDto (Map<String, String> userInfo) {

        return  GoogleMemberDto.builder()
                .email(userInfo.get("email"))
                .name(userInfo.get("name"))
                .socialId(userInfo.get("sub"))
                .build();
    }

    private boolean isLoginPossible(GoogleMemberDto memberDto, Map<String, String> userInfo) {
        Optional<Member> optionalMember = memberRepository.findMemberIncludeDeletedMember(memberDto.getEmail());

        if (!optionalMember.isPresent()) {
            return false;
        } else {
            Member member = optionalMember.get();

            if (!member.isDeleted() && member.isSocialMember()) {
                return true;
            // google 메일이 일반회원으로 가입되어 있을때 에러를 내보낸다
            } else {
                // 액세스 토큰이 필요없으니 만료시킨다
                revokeAccessToken(userInfo.get("accessToken"));
                //탈퇴한
                if (member.isDeleted()) {
                    throw new InputNotValidException("탈퇴한 Google 회원입니다. 다른 계정을 이용해주세요");
                } else {
                    throw new DuplicateValueExeption("일반회원으로 가입된 email입니다. 일반 로그인을 이용해주세요");
                }
            }
        }
    }

    private void revokeAccessToken(String token) {

        RestTemplate restTemplate = new RestTemplate();
        final String requestUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_REVOKE_TOKEN_BASE_URL)
                .queryParam("token", token).encode().toUriString();

        String resultJson = restTemplate.postForObject(requestUrl, null, String.class);
    }

}
