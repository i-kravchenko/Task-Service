package org.example.task_service.configuration.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.swagger")
public class SwaggerProperties
{
    private String title;
    private String version;
    private String description;
    private Contact contact;
    @Setter
    @Getter
    public static class Contact {
        private String name;
        private String email;

    }
}
