package kcs.funding.fundingboost.domain.dto.response.login;

public record TokenDto(String token) {

    public static TokenDto fromEntity(String token) {
        return new TokenDto(token);
    }
}
