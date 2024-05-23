package kcs.funding.fundingboost.domain.security.service;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.REDIS_BLACK_KEY;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTemplateService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addBlackList(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(REDIS_BLACK_KEY, accessToken);
    }

    public boolean isBlackList(String accessToken) {
        return redisTemplate.opsForSet().isMember(REDIS_BLACK_KEY, accessToken);
    }
}
