package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Represents the User's Put Request")
public class UserPutRequest implements Serializable {
    
    @Schema(description = "Full name of the person", example = "Robert Martin", maxLength = 150, required = true)
    @NotBlank(message = "FullName is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s']+$", message = "Only letters should be typed")
    private String fullName;
}