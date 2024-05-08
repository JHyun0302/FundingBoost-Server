package kcs.funding.fundingboost.domain.dto.request.login;

public record SignupDto(
        String nickname,
        String password,
        String email
) {
}
