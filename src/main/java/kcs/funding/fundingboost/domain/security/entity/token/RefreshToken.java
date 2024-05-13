package kcs.funding.fundingboost.domain.security.entity.token;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

    @Id
    private String token;
    private Long userId;

    public static RefreshToken createRefreshToken(String refreshToken, Long userId) {
        RefreshToken token = new RefreshToken();
        token.token = refreshToken;
        token.userId = userId;
        return token;
    }
}
