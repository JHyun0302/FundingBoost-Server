package kcs.funding.fundingboost.domain.security.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "accessToken", timeToLive = 5)
public class BlackList {

    @Id
    private String token;

    public static BlackList createBlackList(String accessToken) {
        BlackList blackList = new BlackList();
        blackList.token = accessToken;
        return blackList;
    }
}
