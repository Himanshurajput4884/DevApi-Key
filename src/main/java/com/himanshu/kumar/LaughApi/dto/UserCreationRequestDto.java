package com.himanshu.kumar.LaughApi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Schema(title = "UserCreationRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserCreationRequestDto {

    @NotBlank(message = "email-id must not be empty")
    @Email(message = "email-id must be of valid format")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "email-id of user", example = "himanshu.kumar@gmail.com")
    private String email;

    @NotBlank(message = "password must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "secure password", example = "password")
    private String password;

    @NotBlank(message = "plan must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Plan to be attached with new user password")
    private UUID planId;

}
