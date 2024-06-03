package kcs.funding.fundingboost.domain.security.utils;

import java.util.List;

public final class NoAuthPath {
    public static List<String> paths = List.of(
            "/api/v1/login",
            "/api/v1/signup",
            "/api/v1/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1",
            "/api/v1/home",
            "/api/v1/items/**",
            //oauth provider uri 설정
            "/api/v1/login/oauth",
            "/api/v1/login/oauth2/code/kakao/**",
            "/login/oauth2/code/kakao/**",
            "/api/v1/access-reissue",
            "/api/v1/fundingboost/**" //actuator
    );
}
