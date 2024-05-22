package kcs.funding.fundingboost.domain.dto.response.login;

public record JwtTokenDto(String accessToken, String refreshToken) {
    public static JwtTokenDto fromEntity(String accessToken, String refreshToken) {
        return new JwtTokenDto(accessToken, refreshToken);
    }
}
