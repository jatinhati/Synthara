package dev.synthara.research.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ResearchCacheManager {

    private static final Logger log = LoggerFactory.getLogger(ResearchCacheManager.class);
    private static final String PREFIX = "synthara:research:";

    private final RedisTemplate<String, String> redis;

    public ResearchCacheManager(@Qualifier("researchCacheRedisTemplate") RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public void cacheSearch(String hash, String result) {
        redis.opsForValue().set(PREFIX + "search:" + hash, result, 24, TimeUnit.HOURS);
    }

    public Optional<String> getSearch(String hash) {
        return Optional.ofNullable(redis.opsForValue().get(PREFIX + "search:" + hash));
    }

    public void cacheState(String workflowId, String state) {
        redis.opsForValue().set(PREFIX + "state:" + workflowId, state, 4, TimeUnit.HOURS);
    }

    public Optional<String> getState(String workflowId) {
        return Optional.ofNullable(redis.opsForValue().get(PREFIX + "state:" + workflowId));
    }

    public void cacheReport(String id, String markdown) {
        redis.opsForValue().set(PREFIX + "report:" + id, markdown, 72, TimeUnit.HOURS);
    }
}
