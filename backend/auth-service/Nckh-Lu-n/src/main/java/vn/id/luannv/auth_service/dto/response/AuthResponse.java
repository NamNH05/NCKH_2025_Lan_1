package vn.id.luannv.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String token;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
}
