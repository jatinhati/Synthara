package dev.synthara.research.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.synthara.research.workflow.ResearchWorkflowState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ResearchRedisConfig {

    @Bean
    public RedisTemplate<String, ResearchWorkflowState> researchStateRedisTemplate(
            RedisConnectionFactory cf, ObjectMapper om) {
        RedisTemplate<String, ResearchWorkflowState> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        t.setKeySerializer(new StringRedisSerializer());
        t.setValueSerializer(new GenericJackson2JsonRedisSerializer(om));
        t.afterPropertiesSet();
        return t;
    }

    @Bean
    public RedisTemplate<String, String> researchCacheRedisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        t.setKeySerializer(new StringRedisSerializer());
        t.setValueSerializer(new StringRedisSerializer());
        t.afterPropertiesSet();
        return t;
    }
}
