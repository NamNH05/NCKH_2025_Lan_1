package vn.id.luannv.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest: DTO cho login, validate username + password
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
