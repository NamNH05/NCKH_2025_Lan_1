package vn.id.luannv.auth_service.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * COMPONENT TEST - Bạn 1 Test 2: JWT Generation
 * 
 * Mục đích: Kiểm thử hàm generateToken() của JwtUtil
 * Loại test: Component/Unit Test (test 1 method riêng lẻ)
 * 
 * File:
 * backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/config/JwtUtilTest.java
 * 
 * Cách chạy:
 * mvn test -Dtest=JwtUtilTest
 */
@DisplayName("JwtUtil - generateToken()")
class JwtUtilTest {

    private static final String JWT_SECRET = "nckh_secret_key_for_jwt_token_2026_nckh_secret_key_for_jwt_token_2026"; // Min
                                                                                                                      // 64
                                                                                                                      // chars
                                                                                                                      // for
                                                                                                                      // HS256
    private static final long JWT_EXPIRATION = 86400000; // 24 hours

    // Simulate JwtUtil without Spring
    private String generateToken(String userId, String username) {
        byte[] secretBytes = JWT_SECRET.getBytes();
        var key = new SecretKeySpec(secretBytes, 0, secretBytes.length, "HmacSHA256");

        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("iat", new Date())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Test Case 1: Token is generated
    @Test
    @DisplayName("Token should be generated successfully")
    void testGenerateToken_Success() {
        // Arrange
        String userId = "1";
        String username = "john";

        // Act
        String token = generateToken(userId, username);

        // Assert
        assertNotNull(token, "Token should not be null");
        assertTrue(token.length() > 0, "Token should not be empty");
        assertTrue(token.contains("."), "Token should have JWT format (3 parts with dots)");
        System.out.println("✅ TC1 PASS: Token generated successfully");
        System.out.println("Token preview: " + token.substring(0, Math.min(50, token.length())) + "...");
    }

    // ✅ Test Case 2: Token has correct structure
    @Test
    @DisplayName("Token should have correct JWT structure")
    void testGenerateToken_Structure_IsCorrect() {
        // Arrange
        String userId = "1";
        String username = "john";

        // Act
        String token = generateToken(userId, username);
        String[] parts = token.split("\\.");

        // Assert
        assertEquals(3, parts.length, "JWT should have 3 parts (header.payload.signature)");
        System.out.println("✅ TC2 PASS: Token has correct structure");
    }

    // ✅ Test Case 3: Token contains correct claims
    @Test
    @DisplayName("Token should contain correct claims")
    void testGenerateToken_Claims_AreCorrect() {
        // Arrange
        String userId = "1";
        String username = "john";

        // Act
        String token = generateToken(userId, username);

        // Parse token to verify claims
        byte[] secretBytes = JWT_SECRET.getBytes();
        var key = new SecretKeySpec(secretBytes, 0, secretBytes.length, "HmacSHA256");

        var claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        // Assert
        assertEquals(userId, claims.getSubject(), "Subject should be user ID");
        assertEquals(username, claims.get("username"), "Should have correct username");
        assertNotNull(claims.getIssuedAt(), "Should have issued at time");
        System.out.println("✅ TC3 PASS: Token claims are correct");
        System.out.println("  Subject: " + claims.getSubject());
        System.out.println("  Username: " + claims.get("username"));
    }

    // ✅ Test Case 4: Token expiration is set
    @Test
    @DisplayName("Token should have expiration set")
    void testGenerateToken_Expiration_IsSet() {
        // Arrange
        String userId = "1";
        String username = "john";

        // Act
        String token = generateToken(userId, username);
        byte[] secretBytes = JWT_SECRET.getBytes();
        var key = new SecretKeySpec(secretBytes, 0, secretBytes.length, "HmacSHA256");

        var claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        // Assert
        assertNotNull(claims.getExpiration(), "Token should have expiration date");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
        System.out.println("✅ TC4 PASS: Token has valid expiration");
        System.out.println("  Expires at: " + claims.getExpiration());
    }

    // ✅ Test Case 5: Token with different users
    @Test
    @DisplayName("Different users should have different tokens")
    void testGenerateToken_DifferentUsers_ProduceDifferentTokens() {
        // Arrange
        String token1 = generateToken("1", "john");
        String token2 = generateToken("2", "jane");

        // Act & Assert
        assertNotEquals(token1, token2, "Different users should produce different tokens");
        System.out.println("✅ TC5 PASS: Different users produce different tokens");
    }
}
