package vn.id.luannv.auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * INTEGRATION TEST - Auth Service Flow
 * 
 * Mục đích: Kiểm thử tích hợp workflow: login → token save → profile access
 * Loại test: Integration Test (tích hợp UserService + AuthService + Controller
 * + Database)
 * 
 * File:
 * backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/AuthIntegrationTest.java
 * 
 * Cách chạy:
 * mvn test -Dtest=AuthIntegrationTest
 * mvn test -Dtest=AuthIntegrationTest#testAuthFlow_LoginAndAccessProfile
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Service - Integration Test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ Test Case 1: Complete Auth Flow
    @Test
    @DisplayName("Should complete full auth flow: login → access protected endpoint")
    void testAuthFlow_LoginAndAccessProfile() throws Exception {
        // ===== STEP 1: LOGIN =====
        // Arrange
        String loginRequest = """
                    {
                        "username": "vinh3305",
                        "password": "v03032005"
                    }
                """;

        // Act & Assert - Login
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("vinh3305"))
                .andExpect(jsonPath("$.email").exists())
                .andReturn();

        // Extract token from response
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        System.out.println("✅ Step 1 PASS: Login successful");
        System.out.println("  Token obtained (length: " + token.length() + ")");

        // ===== STEP 2: ACCESS PROTECTED ENDPOINT =====
        // Act & Assert - Get Profile with token
        mockMvc.perform(get("/api/test/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("vinh3305"))
                .andExpect(jsonPath("$.authorities").isArray())
                .andReturn();

        System.out.println("✅ Step 2 PASS: Protected endpoint accessed with token");
        System.out.println("✅ INTEGRATION TEST COMPLETE: Auth flow works end-to-end");
    }

    // ✅ Test Case 2: Wrong Password Login
    @Test
    @DisplayName("Should reject login with wrong password")
    void testAuthFlow_WrongPassword_LoginFails() throws Exception {
        // Arrange
        String loginRequest = """
                    {
                        "username": "vinh3305",
                        "password": "wrongpassword"
                    }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isBadRequest()) // Service returns 400, not 401
                .andExpect(jsonPath("$.message").exists());

        System.out.println("✅ TC2 PASS: Wrong password rejected");
    }

    // ✅ Test Case 3: Empty Credentials
    @Test
    @DisplayName("Should reject login with empty username")
    void testAuthFlow_EmptyUsername_ValidationFails() throws Exception {
        // Arrange
        String loginRequest = """
                    {
                        "username": "",
                        "password": "password123"
                    }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        System.out.println("✅ TC3 PASS: Empty username rejected");
    }

    // ✅ Test Case 4: Access Protected Endpoint Without Token
    @Test
    @DisplayName("Should deny access to protected endpoint without token")
    void testAuthFlow_NoToken_AccessDenied() throws Exception {
        // Act & Assert - Try to access protected endpoint without token
        mockMvc.perform(get("/api/test/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ TC4 PASS: Protected endpoint denied without token");
    }

    // ✅ Test Case 5: Invalid Token Format
    @Test
    @DisplayName("Should reject invalid token format")
    void testAuthFlow_InvalidToken_AccessDenied() throws Exception {
        // Act & Assert - Try with invalid token
        mockMvc.perform(get("/api/test/me")
                .header("Authorization", "Bearer invalid_token_format")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ TC5 PASS: Invalid token rejected");
    }
}
