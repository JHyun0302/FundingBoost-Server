package kcs.funding.fundingboost.domain.security.service;


import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import kcs.funding.fundingboost.domain.dto.response.login.JwtDto;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import kcs.funding.fundingboost.domain.security.CustomUserDetails;
import kcs.funding.fundingboost.domain.security.KakaoOAuth2User;
import kcs.funding.fundingboost.domain.security.entity.KakaoOAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class KaKaoLoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RelationshipRepository relationshipRepository;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final ObjectMapper objectMapper;


    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 서버에서 인증 토큰을 받아오는 메소드
     */
    public String getAccessTokenFromKakao(String clientId, String code) throws IOException {
        // KAKAO 서버에 인증 토큰 발급 요청
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .messageConverters(convert -> convert.add(new AllEncompassingFormHttpMessageConverter()))
                .baseUrl("https://kauth.kakao.com/oauth/token")
                .build();

        //토큰 요청에 들어갈 body
        MultiValueMap<String, String> objectMaping = new LinkedMultiValueMap<>();
        objectMaping.add("grant_type", "authorization_code");
        objectMaping.add("client_id", clientId);
        objectMaping.add("redirect_uri", redirectUri);
        objectMaping.add("code", code);
        objectMaping.add("client_secret", clientSecret);

        //카카오 서버로 post 토큰요청
        ResponseEntity<KakaoOAuthToken> response = restClient.post()
                .body(objectMaping)
                .contentType(APPLICATION_FORM_URLENCODED)
                .retrieve()
                .toEntity(KakaoOAuthToken.class);

        if (response.getBody() == null) {
            throw new OAuth2AuthenticationException("Invalid authorization code");
        }

        //토큰 발췌
        String accessToken = response.getBody().access_token();
        String refreshToken = response.getBody().refresh_token();

        log.info("Access Token : " + accessToken);
        log.info("Refresh Token : " + refreshToken);

        return accessToken;

    }

    /**
     * 토큰으로 사용자 정보 요청 후 인증, access token 및 refresh token 발행
     */
    public JwtDto getJwtDto(String accessToken) throws IOException {
        // kakao 서버에 access token으로 사용자 정보 요청
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .messageConverters(convert -> convert.add(new AllEncompassingFormHttpMessageConverter()))
                .baseUrl("https://kapi.kakao.com/v2/user/me")
                .build();

        // 인증된 사용자로부터 유저정보 가져오기
        ResponseEntity<String> response = restClient.get()
                .headers(header -> header.setBearerAuth(accessToken))
                .headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
                .retrieve()
                .toEntity(String.class);

        //응답에서 사용자 정보 추출
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> attributes = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        log.info("usetInfo : " + attributes.toString());
        KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(attributes);

        if (!kakaoOAuth2User.validateNecessaryFields()) {
            throw new OAuth2AuthenticationException("필수 필드가 누락되었습니다.");
        }

        CustomUserDetails customUserDetails = processLoginAndRelationships(kakaoOAuth2User, accessToken);

        JwtDto jwtDto = createJwtDto(customUserDetails);

        return jwtDto;

    }

    /**
     * 개인정보 저장 및 친구목록 업데이트
     */
    private CustomUserDetails processLoginAndRelationships(KakaoOAuth2User kakaoOAuth2User, String accessToken) {
        String provider = "kakao";
        String providerId = kakaoOAuth2User.getId();
        String username = kakaoOAuth2User.getName();
        String password = passwordEncoder.encode(provider + "_" + providerId);
        String email = kakaoOAuth2User.getEmail();
        String profileImgUrl = kakaoOAuth2User.getImageUrl();
        String uuid = kakaoOAuth2User.getAttribute("id").toString();

        Member findMember = memberRepository.findByEmail(email).orElse(null);
        String json = getFriendsListByKakao(accessToken);
        log.info("json : " + json);

        CustomUserDetails customUserDetails = null;
        if (findMember == null) {
            Member createMember = Member.createMember(username, email, password, profileImgUrl, uuid);
            customUserDetails = new CustomUserDetails(kakaoOAuth2User.getAttributes(), createMember);
            memberRepository.save(createMember);
            processRelationships(json, createMember);
        } else {
            customUserDetails = new CustomUserDetails(kakaoOAuth2User.getAttributes(), findMember);
            processRelationships(json, findMember);
        }

        return customUserDetails;
    }

    /**
     * 친구 목록 업데이트 및 신규유저 관계 생성
     */
    private void processRelationships(String json, Member member) {
        List<Relationship> userRelationships = relationshipRepository.findFriendByMemberId(member.getMemberId());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(json);
            JsonNode elementsNode = rootNode.get("elements");
            if (elementsNode.isArray()) {
                for (JsonNode element : elementsNode) {
                    String id = element.get("id").asText();
                    try {
                        Member friend = memberRepository.findByKakaoUuid(id)
                                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
                        if (!userRelationships.contains(friend)) {
                            relationshipRepository.saveAll(Relationship.createRelationships(member, friend));
                        }
                    } catch (CommonException e) {
                        throw new CommonException(NOT_FOUND_MEMBER);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("JSON 처리 중 오류 발생", e);
        }
    }

    /**
     * 카카오로부터 친구목록 가져오기
     */
    private static String getFriendsListByKakao(String accessToken) {
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .messageConverters(converter -> converter.add(new AllEncompassingFormHttpMessageConverter()))
                .baseUrl("https://kapi.kakao.com/v1/api/talk/friends")
                .build();

        ResponseEntity<String> response = restClient.get()
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(String.class);

        return response.getBody();
    }

    private JwtDto createJwtDto(CustomUserDetails customUserDetails) {

        Long memberId = customUserDetails.getMemberId();
        String accessToken = jwtAuthenticationService.createAccessTokenForOAuth(memberId);
        String refreshToken = jwtAuthenticationService.createRefreshTokenForOAuth(memberId).getToken();

        log.info("jwt accessToken : " + accessToken);
        log.info("jwt refreshToken : " + refreshToken);

        return JwtDto.fromEntity(accessToken, refreshToken);
    }
}