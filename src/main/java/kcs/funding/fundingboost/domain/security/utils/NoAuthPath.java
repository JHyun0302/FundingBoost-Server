package kcs.funding.fundingboost.domain.security.utils;

import java.util.List;

public final class NoAuthPath {
    public static List<String> paths = List.of(
            "/api/v1/login",
            "/api/v1/signup",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1",
            "/api/v1/home",
            "/api/v1/items/**");
}
