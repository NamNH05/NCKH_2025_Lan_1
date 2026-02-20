package vn.id.luannv.auth_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.id.luannv.auth_service.config.JwtUtil;
import vn.id.luannv.auth_service.dto.AddressDTO;
import vn.id.luannv.auth_service.service.AddressService;
import vn.id.luannv.auth_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Get all addresses for a user
     * Security: User can only access their own addresses (unless admin)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAddresses(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            log.info("=== getUserAddresses called for userId={}", userId);
            
            // Security check: User can only view their own addresses
            verifyUserAccess(userId, request);
            log.info("Security check passed");
            
            List<AddressDTO> addresses = addressService.getUserAddresses(userId);
            log.info("Retrieved {} addresses", addresses.size());
            return ResponseEntity.ok(addresses);
        } catch (SecurityException e) {
            log.error("Security error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied", "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error", "error", e.toString()));
        }
    }

    /**
     * Create new address for user
     * Security: User can only create addresses for themselves (unless admin)
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressDTO> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO addressDTO,
            HttpServletRequest request) {
        // Security check
        verifyUserAccess(userId, request);
        
        AddressDTO created = addressService.createAddress(userId, addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update existing address
     * Security: User can only update their own addresses (unless admin)
     */
    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO addressDTO,
            HttpServletRequest request) {
        // Security check
        verifyUserAccess(userId, request);
        
        AddressDTO updated = addressService.updateAddress(userId, addressId, addressDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete address
     * Security: User can only delete their own addresses (unless admin)
     */
    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            HttpServletRequest request) {
        // Security check
        verifyUserAccess(userId, request);
        
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }

    /**
     * Verify that the current user has access to the specified userId
     * Allows: User accessing their own data OR Admin accessing any data
     */
    private void verifyUserAccess(Long targetUserId, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return; // Admin can access any user's data
        }
        
        // For non-admin users, check if accessing their own data
        Long currentUserId = getCurrentUserId(request);
        if (!currentUserId.equals(targetUserId)) {
            throw new SecurityException("Access denied: Cannot access other user's addresses");
        }
    }

    /**
     * Get current user's ID from JWT token
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("No valid Authorization header found");
                throw new SecurityException("No valid token provided");
            }
            
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.extractAllClaims(token);
            
            // Get ID from token claims
            Long userId = claims.get("id", Long.class);
            
            if (userId == null) {
                log.error("No user ID found in token");
                throw new SecurityException("Invalid token: no user ID");
            }
            
            log.debug("Extracted user ID {} from token", userId);
            return userId;
        } catch (SecurityException e) {
            log.error("Security exception in getCurrentUserId: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage(), e);
            throw new SecurityException("Failed to get current user ID: " + e.getMessage());
        }
    }
}

// Simple error response class
class ErrorResponse {
    public String message;
    public String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
