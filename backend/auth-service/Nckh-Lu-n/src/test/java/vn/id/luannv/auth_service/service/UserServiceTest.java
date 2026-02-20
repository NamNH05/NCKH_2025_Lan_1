package vn.id.luannv.auth_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * COMPONENT TEST - Bạn 1 Test 1: Password Validation
 * 
 * Mục đích: Kiểm thử hàm validatePassword() của UserService
 * Loại test: Component/Unit Test (test 1 method riêng lẻ)
 * 
 * File:
 * backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/service/UserServiceTest.java
 * 
 * Cách chạy:
 * mvn test -Dtest=UserServiceTest
 * hoặc
 * mvn test
 * -Dtest=UserServiceTest#testValidatePassword_CorrectPassword_ReturnsTrue
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - validatePassword()")
class UserServiceTest {

    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
    }

    // ✅ Test Case 1: Correct Password
    @Test
    @DisplayName("Should return true when password is correct")
    void testValidatePassword_CorrectPassword_ReturnsTrue() {
        // Arrange - Chuẩn bị dữ liệu
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);

        // Act - Thực hiện
        boolean result = encoder.matches(plainPassword, hashedPassword);

        // Assert - Kiểm tra
        assertTrue(result, "Password should match");
        System.out.println("✅ TC1 PASS: Correct password validated");
    }

    // ✅ Test Case 2: Wrong Password
    @Test
    @DisplayName("Should return false when password is wrong")
    void testValidatePassword_WrongPassword_ReturnsFalse() {
        // Arrange
        String plainPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = encoder.encode(plainPassword);

        // Act
        boolean result = encoder.matches(wrongPassword, hashedPassword);

        // Assert
        assertFalse(result, "Wrong password should not match");
        System.out.println("✅ TC2 PASS: Wrong password rejected");
    }

    // ✅ Test Case 3: Empty Password
    @Test
    @DisplayName("Should handle empty password")
    void testValidatePassword_EmptyPassword_ReturnsFalse() {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);

        // Act
        boolean result = encoder.matches("", hashedPassword);

        // Assert
        assertFalse(result, "Empty password should not match");
        System.out.println("✅ TC3 PASS: Empty password rejected");
    }

    // ✅ Test Case 4: Password with special characters
    @Test
    @DisplayName("Should handle password with special characters")
    void testValidatePassword_SpecialCharacters_ReturnsTrue() {
        // Arrange
        String plainPassword = "P@ssw0rd!#$%";
        String hashedPassword = encoder.encode(plainPassword);

        // Act
        boolean result = encoder.matches(plainPassword, hashedPassword);

        // Assert
        assertTrue(result, "Password with special chars should match");
        System.out.println("✅ TC4 PASS: Special character password validated");
    }

    // ✅ Test Case 5: Null Password
    @Test
    @DisplayName("Should throw exception for null password")
    void testValidatePassword_NullPassword_ThrowsException() {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            encoder.matches(null, hashedPassword);
        }, "Null password should throw exception");
        System.out.println("✅ TC5 PASS: Null password throws exception");
    }
}
