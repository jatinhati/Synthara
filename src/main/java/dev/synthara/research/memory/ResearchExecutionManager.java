package dev.synthara.research.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResearchExecutionManager {

    private static final Logger log = LoggerFactory.getLogger(ResearchExecutionManager.class);
    private static final String PROGRESS_PREFIX = "synthara:research:progress:";

    private final RedisTemplate<String, String> redis;

    public ResearchExecutionManager(@Qualifier("researchCacheRedisTemplate") RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public void updateProgress(String workflowId, String stage) {
        redis.opsForValue().set(PROGRESS_PREFIX + workflowId, "Stage: " + stage);
    }

    public String getProgress(String workflowId) {
        String v = redis.opsForValue().get(PROGRESS_PREFIX + workflowId);
        return v != null ? v : "Unknown";
    }

    public void cleanup(String workflowId) {
        redis.delete(PROGRESS_PREFIX + workflowId);
    }
}
