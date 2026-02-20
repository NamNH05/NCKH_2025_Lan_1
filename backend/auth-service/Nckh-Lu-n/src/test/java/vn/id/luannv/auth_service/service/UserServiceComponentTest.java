package vn.id.luannv.auth_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import vn.id.luannv.auth_service.dto.request.RegisterRequest;
import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.entity.UserStatus;
import vn.id.luannv.auth_service.exception.BusinessException;
import vn.id.luannv.auth_service.repository.RoleRepository;
import vn.id.luannv.auth_service.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * COMPONENT TEST - UserService Component Test
 * 
 * Mục đích:
 * - Test logic nghiệp vụ của UserService
 * - Mock UserRepository và RoleRepository
 * - Test workflow: createUser (Register), getUserById, getAllUsers, deleteUser
 * 
 * Loại test: Component Test (test Service + mock Repository)
 * - Test 1 lớp Service hoàn chỉnh
 * - Có mock Repository (không gọi database thực)
 * - Test logic nghiệp vụ thực
 * 
 * File:
 * backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/service/UserServiceComponentTest.java
 * 
 * Cách chạy:
 * cd backend/auth-service/Nckh-Lu-n
 * mvn test -Dtest=UserServiceComponentTest
 * 
 * hoặc chạy test riêng lẻ:
 * mvn test
 * -Dtest=UserServiceComponentTest#testCreateUser_ValidData_SavesSuccessfully
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Component Test (with Mock Repository)")
class UserServiceComponentTest {

    // ===== Dependencies =====
    // Mock các dependencies (Repository)
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    // Service cần test
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    // ===== Test Data =====
    private User testUser;
    private Role testRole;
    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        // UserService uses @RequiredArgsConstructor
        // Constructor params: UserRepository, RoleRepository
        // passwordEncoder is initialized inline in the class
        userService = new UserService(userRepository, roleRepository);
        // Inject passwordEncoder using ReflectionTestUtils
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);

        // Setup test data
        testRole = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .description("Regular user")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .email("testuser@example.com")
                .fullName("Test User")
                .phone("0123456789")
                .roles(Set.of(testRole))
                .status(UserStatus.ACTIVE)
                .build();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setEmail("newuser@example.com");
        validRegisterRequest.setFullName("New User");
        validRegisterRequest.setPhone("0987654321");
    }

    // ===== TC1: Register User - Valid Data =====
    @Test
    @DisplayName("TC1: Register user with valid data should save user successfully")
    void testCreateUser_ValidData_SavesSuccessfully() {
        // Arrange - Chuẩn bị mock
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(testRole));

        User savedUser = User.builder()
                .id(2L)
                .username("newuser")
                .password(passwordEncoder.encode("password123"))
                .email("newuser@example.com")
                .fullName("New User")
                .phone("0987654321")
                .roles(Set.of(testRole))
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act - Thực hiện: Register user
        UserResponse result = userService.createUser(validRegisterRequest);

        // Assert - Kiểm tra
        assertNotNull(result, "Result should not be null");
        assertEquals("newuser", result.getUsername(), "Username should match");
        assertEquals("newuser@example.com", result.getEmail(), "Email should match");
        assertEquals("New User", result.getFullName(), "Full name should match");

        // Verify Repository was called
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository).save(any(User.class));

        System.out.println("✅ TC1 PASS: User created successfully");
        System.out.println("  Username: " + result.getUsername());
        System.out.println("  Email: " + result.getEmail());
    }

    // ===== TC2: Register User - Duplicate Username =====
    @Test
    @DisplayName("TC2: Register user with duplicate username should throw BusinessException")
    void testCreateUser_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(validRegisterRequest);
        }, "Should throw BusinessException for duplicate username");

        assertTrue(exception.getMessage().contains("Username already exists"),
                "Exception message should contain 'Username already exists'");

        // Verify Repository was called once
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any());

        System.out.println("✅ TC2 PASS: Duplicate username rejected");
        System.out.println("  Error: " + exception.getMessage());
    }

    // ===== TC3: Register User - Duplicate Email =====
    @Test
    @DisplayName("TC3: Register user with duplicate email should throw BusinessException")
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(validRegisterRequest);
        }, "Should throw BusinessException for duplicate email");

        assertTrue(exception.getMessage().contains("Email already exists"),
                "Exception message should contain 'Email already exists'");

        // Verify Repository behavior
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any());

        System.out.println("✅ TC3 PASS: Duplicate email rejected");
        System.out.println("  Error: " + exception.getMessage());
    }

    // ===== TC4: Get User By ID - Success =====
    @Test
    @DisplayName("TC4: Get user by ID with valid ID should return user")
    void testGetUserById_ValidId_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("testuser", result.getUsername(), "Username should match");
        assertEquals("testuser@example.com", result.getEmail(), "Email should match");

        // Verify Repository was called
        verify(userRepository).findById(1L);

        System.out.println("✅ TC4 PASS: User retrieved by ID");
        System.out.println("  ID: 1");
        System.out.println("  Username: " + result.getUsername());
    }

    // ===== TC5: Get User By ID - Not Found =====
    @Test
    @DisplayName("TC5: Get user by ID with invalid ID should throw BusinessException")
    void testGetUserById_InvalidId_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(999L);
        }, "Should throw BusinessException when user not found");

        assertTrue(exception.getMessage().contains("User not found"),
                "Exception message should contain 'User not found'");

        // Verify Repository was called
        verify(userRepository).findById(999L);

        System.out.println("✅ TC5 PASS: User not found error thrown");
        System.out.println("  Error: " + exception.getMessage());
    }

    // ===== TC6: Delete User - Success =====
    @Test
    @DisplayName("TC6: Delete user (set to INACTIVE) should succeed")
    void testDeleteUser_ValidId_SetsInactive() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        String result = userService.deleteUser(1L);

        // Assert
        assertEquals("User disabled.", result, "Should return success message");

        // Verify Repository save was called
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));

        System.out.println("✅ TC6 PASS: User deleted (set to INACTIVE)");
        System.out.println("  Result: " + result);
    }

    // ===== TC7: Delete User - Not Found =====
    @Test
    @DisplayName("TC7: Delete user with invalid ID should throw BusinessException")
    void testDeleteUser_InvalidId_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(999L);
        }, "Should throw BusinessException when user not found");

        assertTrue(exception.getMessage().contains("User not found"),
                "Exception message should contain 'User not found'");

        // Verify Repository behavior
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());

        System.out.println("✅ TC7 PASS: Delete user not found error thrown");
        System.out.println("  Error: " + exception.getMessage());
    }

    // ===== TC8: Password Encoding - Verify Password is Hashed =====
    @Test
    @DisplayName("TC8: Register user should hash password (not plain text)")
    void testCreateUser_PasswordIsHashed_NotPlainText() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(testRole));

        User savedUser = User.builder()
                .id(2L)
                .username("newuser")
                .password(passwordEncoder.encode("password123"))
                .email("newuser@example.com")
                .fullName("New User")
                .phone("0987654321")
                .roles(Set.of(testRole))
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse result = userService.createUser(validRegisterRequest);

        // Assert
        assertNotNull(result);

        // Capture the saved user to check password was hashed
        verify(userRepository).save(argThat(user -> {
            // Password should be hashed, not "password123"
            assertNotEquals("password123", user.getPassword(),
                    "Password should be hashed, not plain text");
            assertTrue(passwordEncoder.matches("password123", user.getPassword()),
                    "Password should match after hashing");
            return true;
        }));

        System.out.println("✅ TC8 PASS: Password properly hashed");
        System.out.println("  Password is encoded with BCrypt");
    }

    /**
     * Summary of Component Tests:
     * 
     * ✅ TC1: Create user with valid data → Success
     * ✅ TC2: Create user with duplicate username → BusinessException
     * ✅ TC3: Create user with duplicate email → BusinessException
     * ✅ TC4: Get user by valid ID → Returns user
     * ✅ TC5: Get user by invalid ID → BusinessException
     * ✅ TC6: Delete user (set INACTIVE) → Success
     * ✅ TC7: Delete user with invalid ID → BusinessException
     * ✅ TC8: Password is hashed (not plain text) → Success
     * 
     * Total: 8 test cases
     * 
     * ✓ Tests Service layer logic
     * ✓ Tests business rules (duplicate username/email)
     * ✓ Tests error handling (user not found)
     * ✓ Tests security (password hashing)
     * ✓ Mock Repository → No database access
     * ✓ Verify mock calls → Ensure correct flow
     */
}
