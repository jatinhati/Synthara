package dev.synthara.research.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ResearchAsyncConfig {

    @Bean(name = "researchExecutor")
    public Executor researchExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setCorePoolSize(4); e.setMaxPoolSize(8); e.setQueueCapacity(50);
        e.setThreadNamePrefix("research-");
        e.setWaitForTasksToCompleteOnShutdown(true);
        e.setAwaitTerminationSeconds(30);
        e.initialize();
        return e;
    }
}
