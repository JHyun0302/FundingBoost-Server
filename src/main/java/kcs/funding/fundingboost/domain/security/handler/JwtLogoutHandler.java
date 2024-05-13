package kcs.funding.fundingboost.domain.security.handler;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.REDIS_BLACK_KEY;

import jakarta.servlet.http.HttpServletRequest;
import kcs.funding.fundingboost.domain.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void handle(HttpServletRequest request) {
        String accessToken = request.getHeader("access_token");
        String refreshToken = request.getHeader("refresh_token");

        // refreshToken 삭제
        refreshTokenRepository.deleteById(refreshToken);

        // accessToken 블랙 처리
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(REDIS_BLACK_KEY, accessToken);
    }
}
