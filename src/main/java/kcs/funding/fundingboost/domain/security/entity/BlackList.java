package kcs.funding.fundingboost.domain.security.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "refreshToken")
public class BlackList {

    @Id
    private String token;

    @TimeToLive
    private Long expiration;

    public BlackList(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }
}
