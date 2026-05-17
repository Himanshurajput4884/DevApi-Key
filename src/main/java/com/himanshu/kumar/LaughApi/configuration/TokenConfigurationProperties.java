package com.himanshu.kumar.LaughApi.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "com.himanshu.kumar.LaughApi.token")
public class TokenConfigurationProperties {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9+/]*={0,2}$", message = "Secret key must be Base64 encoded")
    private String secretKey;

    @NotNull
    @Positive
    private Integer tokenValidityMinutes;


}
