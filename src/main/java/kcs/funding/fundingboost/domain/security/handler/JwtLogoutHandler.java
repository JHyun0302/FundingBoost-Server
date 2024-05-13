package kcs.funding.fundingboost.domain.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import kcs.funding.fundingboost.domain.security.repository.RefreshTokenRepository;
import kcs.funding.fundingboost.domain.security.service.RedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplateService redisTemplateService;

    public void handle(HttpServletRequest request) {
        String accessToken = request.getHeader("access_token");
        String refreshToken = request.getHeader("refresh_token");

        // refreshToken 삭제
        refreshTokenRepository.deleteById(refreshToken);

        // accessToken 블랙 처리
        redisTemplateService.addBlackList(accessToken);
    }
}
