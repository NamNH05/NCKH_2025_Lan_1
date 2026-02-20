package com.example.audit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for input validation to prevent injection attacks
 * and ensure data integrity
 */
public class InputValidator {
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    // Constants
    private static final int MAX_KEYWORD_LENGTH = 255;
    private static final int MAX_STRING_LENGTH = 500;
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Validate action type - must match enum values
     * @param actionType Action type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidActionType(String actionType) {
        if (actionType == null || actionType.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Try to match with valid action types
            String trimmed = actionType.trim().toUpperCase();
            
            // Allowed action types
            String[] validActions = {"CREATE", "READ", "UPDATE", "DELETE", "LOGIN", "LOGOUT", "EXPORT", "IMPORT"};
            for (String action : validActions) {
                if (trimmed.equals(action)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.warn("Invalid action type provided: {}", actionType);
            return false;
        }
    }
    
    /**
     * Validate entity name - alphanumeric and underscore only
     * @param entityName Entity name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEntityName(String entityName) {
        if (entityName == null || entityName.trim().isEmpty()) {
            return false;
        }
        
        if (entityName.length() > MAX_STRING_LENGTH) {
            return false;
        }
        
        // Allow only alphanumeric, underscore, and dash
        return entityName.matches("^[a-zA-Z0-9_-]+$");
    }
    
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
     * Validate and parse date string
     * @param dateString Date string to validate (format: yyyy-MM-dd HH:mm:ss)
     * @return LocalDateTime if valid, null otherwise
     */
    public static LocalDateTime validateAndParseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format provided: {}", dateString);
            return null;
        }
    }
    
    /**
     * Validate page number
     * @param page Page number
     * @return true if valid (0 or positive), false otherwise
     */
    public static boolean isValidPageNumber(int page) {
        return page >= 0;
    }
    
    /**
     * Validate page size
     * @param size Page size
     * @return true if valid (1-100), false otherwise
     */
    public static boolean isValidPageSize(int size) {
        return size > 0 && size <= 100;
    }
    
    /**
     * Sanitize string input for logging/display
     * @param input String to sanitize
     * @return Sanitized string
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replaceAll("[^a-zA-Z0-9\\s._-]", "")
                    .trim();
    }
}
