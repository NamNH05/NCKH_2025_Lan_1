package vn.id.luannv.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/ping")
    public String ping() {
        return "OK - Auth Service is running";
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return Map.of(
                "username", auth.getName(),
                "authorities", auth.getAuthorities()
        );
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String userAccess() {
        return "USER or ADMIN can access this";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "ONLY ADMIN can access this";
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus().name())
                .role(
                        user.getRoles().stream()
                                .map(r -> r.getName())
                                .toList()
                                .toString()
                )
                .build();
    }
}
