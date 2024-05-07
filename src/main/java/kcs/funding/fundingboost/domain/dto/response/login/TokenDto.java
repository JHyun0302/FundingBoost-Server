package kcs.funding.fundingboost.domain.dto.response.login;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.TOKEN_PREFIX;

public record TokenDto(String token) {

    public static TokenDto fromEntity(String token) {
        return new TokenDto(TOKEN_PREFIX + token);
    }
}
