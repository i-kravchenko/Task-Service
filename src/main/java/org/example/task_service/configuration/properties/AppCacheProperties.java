package org.example.task_service.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties
{
    private Map<String, CacheProperties> config = new HashMap<>();

    public interface CacheNames
    {

        String TASKS = "tasks";
    }
    @Data
    public static class CacheProperties
    {
        private Duration expiry = Duration.ZERO;
    }
}
