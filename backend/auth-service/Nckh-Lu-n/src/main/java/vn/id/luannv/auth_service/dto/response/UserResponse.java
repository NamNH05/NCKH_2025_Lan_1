package vn.id.luannv.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserResponse implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
}
