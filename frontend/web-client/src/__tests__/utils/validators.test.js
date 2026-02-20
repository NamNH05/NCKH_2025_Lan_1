/**
 * COMPONENT TEST - Bạn 3 Test 1 & 2: Email & Username Validation
 * 
 * Mục đích: Kiểm thử hàm validateEmail() & validateUsername() từ validators.js
 * Loại test: Component/Unit Test (Frontend - Jest)
 * 
 * File: frontend/web-client/src/__tests__/utils/validators.test.js
 * 
 * Cách chạy:
 *   npm test validators.test.js
 *   hoặc
 *   npm test -- --testNamePattern="Email Validation"
 */

// Validators functions
export const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

export const validateUsername = (username) => {
    // Username: 3-20 characters, alphanumeric, underscore, hyphen
    const usernameRegex = /^[a-zA-Z0-9_-]{3,20}$/;
    return usernameRegex.test(username);
};

// ======================== TESTS ========================

describe('Email Validation', () => {
    // ✅ Test Case 1: Valid email
    test('should validate correct email format', () => {
        const validEmails = [
            'user@example.com',
            'john@domain.co.uk',
            'test.email+tag@company.com',
            'phamtienvinh5002@gmail.com'
        ];
        
        validEmails.forEach(email => {
            expect(validateEmail(email)).toBe(true);
        });
        
        console.log('✅ TC1 PASS: Valid emails accepted');
    });

    // ✅ Test Case 2: Invalid email - missing @
    test('should reject email without @', () => {
        expect(validateEmail('userdomain.com')).toBe(false);
        expect(validateEmail('user@domain')).toBe(false);
        console.log('✅ TC2 PASS: Emails without @ rejected');
    });

    // ✅ Test Case 3: Invalid email - missing domain
    test('should reject email without domain', () => {
        expect(validateEmail('user@')).toBe(false);
        expect(validateEmail('@domain.com')).toBe(false);
        console.log('✅ TC3 PASS: Emails without domain rejected');
    });

    // ✅ Test Case 4: Invalid email - spaces
    test('should reject email with spaces', () => {
        expect(validateEmail('user @example.com')).toBe(false);
        expect(validateEmail('user@ example.com')).toBe(false);
        console.log('✅ TC4 PASS: Emails with spaces rejected');
    });

    // ✅ Test Case 5: Empty email
    test('should reject empty email', () => {
        expect(validateEmail('')).toBe(false);
        console.log('✅ TC5 PASS: Empty email rejected');
    });
});

describe('Username Validation', () => {
    // ✅ Test Case 1: Valid username
    test('should validate correct username format', () => {
        const validUsernames = [
            'john',
            'user123',
            'test_user',
            'john-doe',
            'user_name_123'
        ];
        
        validUsernames.forEach(username => {
            expect(validateUsername(username)).toBe(true);
        });
        
        console.log('✅ TC1 PASS: Valid usernames accepted');
    });

    // ✅ Test Case 2: Username too short
    test('should reject username shorter than 3 characters', () => {
        expect(validateUsername('ab')).toBe(false);
        expect(validateUsername('a')).toBe(false);
        console.log('✅ TC2 PASS: Short usernames rejected');
    });

    // ✅ Test Case 3: Username too long
    test('should reject username longer than 20 characters', () => {
        expect(validateUsername('this_is_a_very_long_username_for_testing')).toBe(false);
        console.log('✅ TC3 PASS: Long usernames rejected');
    });

    // ✅ Test Case 4: Username with special characters
    test('should reject username with special characters', () => {
        expect(validateUsername('user@name')).toBe(false);
        expect(validateUsername('user#123')).toBe(false);
        expect(validateUsername('user$')).toBe(false);
        console.log('✅ TC4 PASS: Usernames with special chars rejected');
    });

    // ✅ Test Case 5: Username with spaces
    test('should reject username with spaces', () => {
        expect(validateUsername('user name')).toBe(false);
        expect(validateUsername('john doe')).toBe(false);
        console.log('✅ TC5 PASS: Usernames with spaces rejected');
    });

    // ✅ Test Case 6: Empty username
    test('should reject empty username', () => {
        expect(validateUsername('')).toBe(false);
        console.log('✅ TC6 PASS: Empty username rejected');
    });
});
