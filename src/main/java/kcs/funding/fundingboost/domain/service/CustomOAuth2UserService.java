package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import kcs.funding.fundingboost.domain.security.CustomUserDetails;
import kcs.funding.fundingboost.domain.security.KakaoOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RelationshipRepository relationshipRepository;

    @Override
    public CustomUserDetails loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(oAuth2User.getAttributes());

        String json = getFriendsListByKakao(userRequest);

        if (!kakaoOAuth2User.validateNecessaryFields()) {
            throw new OAuth2AuthenticationException("필수 필드가 누락되었습니다.");
        }

        String provider = userRequest.getClientRegistration().getClientId(); // kakao
        String providerId = kakaoOAuth2User.getId();
        String username = kakaoOAuth2User.getName();
        String password = passwordEncoder.encode(provider + "_" + providerId);
        String email = kakaoOAuth2User.getEmail();
        String profileImgUrl = kakaoOAuth2User.getImageUrl();
        String uuid = oAuth2User.getAttribute("id").toString();

        Member findMember = memberRepository.findByEmail(email).orElse(null);

        CustomUserDetails customUserDetails;
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

    //친구 목록 업데이트 및 신규유저 관계 생성
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

    // 카카오로부터 친구목록 가져오기
    private static String getFriendsListByKakao(OAuth2UserRequest userRequest) {
        String token = userRequest.getAccessToken().getTokenValue();
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .messageConverters(converter -> converter.add(new AllEncompassingFormHttpMessageConverter()))
                .baseUrl("https://kapi.kakao.com/v1/api/talk/friends")
                .build();

        ResponseEntity<String> response = restClient.get()
                .headers(header -> header.setBearerAuth(token))
                .retrieve()
                .toEntity(String.class);

        return response.getBody();
    }
}
