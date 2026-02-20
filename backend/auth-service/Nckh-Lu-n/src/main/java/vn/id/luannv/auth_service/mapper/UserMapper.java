package vn.id.luannv.auth_service.mapper;

import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;

import java.util.stream.Collectors;

/**
 * UserMapper: map User entity -> UserResponse DTO
 */
public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(",")))
                .status(user.getStatus().name())
                .build();
    }
}
