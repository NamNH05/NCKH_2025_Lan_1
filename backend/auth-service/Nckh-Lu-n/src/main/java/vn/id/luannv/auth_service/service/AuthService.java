package vn.id.luannv.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.id.luannv.auth_service.config.JwtUtil;
import vn.id.luannv.auth_service.client.AuditClient;
import vn.id.luannv.auth_service.client.AuditLogRequest;
import vn.id.luannv.auth_service.dto.request.LoginRequest;
import vn.id.luannv.auth_service.dto.request.RegisterRequest;
import vn.id.luannv.auth_service.dto.response.AuthResponse;
import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.entity.UserStatus;
import vn.id.luannv.auth_service.exception.BusinessException;
import vn.id.luannv.auth_service.repository.UserRepository;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlacklistService blacklistService;
    private final AuditClient auditClient;
    private final ObjectMapper objectMapper;

    public UserResponse register(RegisterRequest request) {
        UserResponse response = userService.createUser(request);
        
        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("auth-service")
                .actionType("CREATE")
                .entityName("USER")
                .entityId(response.getId().toString())
                .userId(response.getId().toString())
                .newValue(objectMapper.writeValueAsString(response))
                .ipAddress("0.0.0.0")
                .userAgent("auth-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit user registration: {}", e.getMessage());
        }
        
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not active");
        }

        String token = jwtUtil.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(",")))
                .status(user.getStatus().toString())
                .build();
        
        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("auth-service")
                .actionType("LOGIN")
                .entityName("USER")
                .entityId(user.getId().toString())
                .userId(user.getId().toString())
                .ipAddress("0.0.0.0")
                .userAgent("auth-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit user login: {}", e.getMessage());
        }
        
        return response;
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String logout(String token) {
        Instant expiry = jwtUtil.extractAllClaims(token).getExpiration().toInstant();
        blacklistService.blacklistToken(token, expiry);
        
        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("auth-service")
                .actionType("LOGOUT")
                .entityName("USER")
                .userId("unknown")
                .ipAddress("0.0.0.0")
                .userAgent("auth-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit user logout: {}", e.getMessage());
        }
        
        return "Logged out successfully";
    }

}
