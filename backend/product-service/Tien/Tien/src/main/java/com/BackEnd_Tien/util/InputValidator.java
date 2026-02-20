package com.BackEnd_Tien.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for input validation to prevent injection attacks
 * and ensure data integrity
 */
public class InputValidator {
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    // Constants
    private static final int MAX_KEYWORD_LENGTH = 255;
    private static final int MAX_CATEGORY_LENGTH = 100;
    
    /**
     * Validate keyword for search
     * @param keyword Keyword to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidKeyword(String keyword) {
        if (keyword == null) {
            return true; // null is acceptable for optional keyword
        }
        
        if (keyword.trim().isEmpty()) {
            return true; // empty is acceptable
        }
        
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            logger.warn("Keyword length exceeds maximum: {}", keyword.length());
            return false;
        }
        
        // Prevent common SQL injection patterns
        String[] forbiddenPatterns = {
            "';", "--", "/*", "*/", "xp_", "sp_", "UNION", "SELECT", 
            "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "EXEC"
        };
        
        String upperKeyword = keyword.toUpperCase();
        for (String pattern : forbiddenPatterns) {
            if (upperKeyword.contains(pattern)) {
                logger.warn("Suspicious pattern detected in keyword: {}", pattern);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate category name
     * @param category Category to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        
        if (category.length() > MAX_CATEGORY_LENGTH) {
            logger.warn("Category length exceeds maximum: {}", category.length());
            return false;
        }
        
        // Allow alphanumeric, spaces, hyphens, and underscores
        return category.matches("^[a-zA-Z0-9\\s_-]+$");
    }
    
    /**
     * Validate product quantity
     * @param quantity Quantity to validate
     * @return true if valid (>0), false otherwise
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0 && quantity <= 10000;
    }
}
