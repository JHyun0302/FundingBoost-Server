package kcs.funding.fundingboost.domain.security.service;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.REDIS_BLACK_KEY;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisTemplateService {

    private final RedisTemplate<String, String> refreshTokenTemplate;

    public RedisTemplateService(RedisTemplate<String, String> refreshTokenTemplate) {
        this.refreshTokenTemplate = refreshTokenTemplate;
    }

    public void addBlackList(String accessToken) {
        SetOperations<String, String> setOperations = refreshTokenTemplate.opsForSet();
        setOperations.add(REDIS_BLACK_KEY, accessToken);
    }

    public boolean isBlackList(String accessToken) {
        return refreshTokenTemplate.opsForSet().isMember(REDIS_BLACK_KEY, accessToken);
    }
}
