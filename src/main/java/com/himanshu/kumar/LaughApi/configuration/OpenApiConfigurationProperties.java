package com.himanshu.kumar.LaughApi.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.himanshu.kumar.LaughApi")
public class OpenApiConfigurationProperties {
    private OpenAPI openAPI = new OpenAPI();

    @Getter
    @Setter
    public class OpenAPI {

        /****
         * Determines whether Swagger v3 API Documentation and related endpoints are
         * accessible bypassing Authentication and Authorization checks.
         */


        private boolean enabled;
        private String title;
        private String description;
        private String apiVersion;
    }
}
