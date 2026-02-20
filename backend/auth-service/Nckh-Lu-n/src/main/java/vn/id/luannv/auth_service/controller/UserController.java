package vn.id.luannv.auth_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.id.luannv.auth_service.config.JwtUtil;
import vn.id.luannv.auth_service.dto.request.RegisterRequest;
import vn.id.luannv.auth_service.dto.request.UpdateProfileRequest;
import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.exception.BusinessException;
import vn.id.luannv.auth_service.repository.UserRepository;
import vn.id.luannv.auth_service.service.UserService;
import io.jsonwebtoken.Claims;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    /**
     * Update user profile - User can update their own profile
     * Or admin can update any user profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("=== UPDATE PROFILE REQUEST ===");
        log.info("Target user ID: {}", id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", auth);
        log.info("Principal: {}", auth != null ? auth.getPrincipal() : "null");
        log.info("Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        
        String authHeader = httpRequest.getHeader("Authorization");
        log.info("Authorization header present: {}", authHeader != null);
        
        try {
            // Verify user access: allow if user is updating own profile or is admin
            verifyUserAccess(id, httpRequest);
            
            UserResponse updatedUser = userService.updateProfile(id, request);
            log.info("Profile updated successfully for user: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete user (set status to INACTIVE) - Admin only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    /**
     * Verify that current user has access to modify the specified userId
     * Allows: User modifying their own data OR Admin modifying any data
     */
    private void verifyUserAccess(Long targetUserId, HttpServletRequest request) {
        log.info("=== VERIFY USER ACCESS ===");
        log.info("Target user ID: {}", targetUserId);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Auth name: {}", authentication != null ? authentication.getName() : "null");
        
        // Check if user has ADMIN role - admins can do anything
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> {
                    log.info("Checking authority: {}", auth.getAuthority());
                    return auth.getAuthority().equals("ROLE_ADMIN");
                });
        
        log.info("Is admin: {}", isAdmin);
        if (isAdmin) {
            log.info("User is ADMIN, granting access");
            return;
        }
        
        // Non-admin: must be updating own profile
        // Try to extract user ID from JWT token first
        String authHeader = request.getHeader("Authorization");
        log.info("Auth header: {}", authHeader);
        
        Long currentUserId = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                log.info("JWT claims: {}", claims);
                Object idObj = claims.get("id");
                log.info("ID from JWT: {}", idObj);
                if (idObj != null) {
                    currentUserId = ((Number) idObj).longValue();
                }
            } catch (Exception e) {
                log.error("Error extracting ID from JWT: {}", e.getMessage());
                // Fall back to username lookup
            }
        }
        
        // If couldn't extract from JWT, try username lookup
        if (currentUserId == null) {
            String username = authentication.getName();
            log.info("Extracting user ID from username: {}", username);
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("Current user not found"));
            currentUserId = currentUser.getId();
        }
        
        log.info("Current user ID: {}, Target user ID: {}", currentUserId, targetUserId);
        
        if (!currentUserId.equals(targetUserId)) {
            log.error("Access denied: user {} trying to modify user {}", currentUserId, targetUserId);
            throw new BusinessException("Access denied: Cannot modify other user's profile");
        }
        
        log.info("Access granted");
    }
}
