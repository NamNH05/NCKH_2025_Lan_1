/**
 * Input Validation Middleware
 * Provides whitelist-based validation for common endpoints
 */

const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email) && email.length <= 255;
};

const validatePassword = (password) => {
  // Minimum 8 chars, at least 1 uppercase, 1 lowercase, 1 number, 1 special char
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
  return passwordRegex.test(password) && password.length <= 128;
};

const validateUsername = (username) => {
  // Alphanumeric, underscore, hyphen only. 3-50 chars
  const usernameRegex = /^[a-zA-Z0-9_-]{3,50}$/;
  return usernameRegex.test(username);
};

const validatePhoneNumber = (phone) => {
  // Simple phone validation: digits, +, -, () only
  const phoneRegex = /^[\d\s+\-()]{7,20}$/;
  return phoneRegex.test(phone);
};

const validateUrl = (url) => {
  try {
    new URL(url);
    return url.length <= 2048;
  } catch {
    return false;
  }
};

const sanitizeInput = (input) => {
  if (typeof input !== 'string') return input;
  // Remove potential XSS characters
  return input
    .trim()
    .replace(/[<>]/g, '')
    .substring(0, 1000); // Max 1000 chars
};

/**
 * Validate login request
 * Note: For login, we don't enforce password complexity - just check it exists
 * Password policy is enforced during registration
 */
const validateLoginInput = (req, res, next) => {
  const { username, email, password } = req.body;

  if (!username && !email) {
    return res.status(400).json({ message: 'Username or email required' });
  }

  if (!password) {
    return res.status(400).json({ message: 'Password required' });
  }

  if (username && !validateUsername(username)) {
    return res.status(400).json({ message: 'Invalid username format' });
  }

  if (email && !validateEmail(email)) {
    return res.status(400).json({ message: 'Invalid email format' });
  }

  // Don't validate password format for login - just ensure it's provided
  // Users with old passwords (before policy change) can still login

  // Sanitize inputs
  req.body.username = username ? sanitizeInput(username) : undefined;
  req.body.email = email ? sanitizeInput(email) : undefined;
  req.body.password = password; // Don't sanitize password

  next();
};

/**
 * Validate register request
 */
const validateRegisterInput = (req, res, next) => {
  const { username, email, password, passwordConfirm } = req.body;

  if (!username || !validateUsername(username)) {
    return res.status(400).json({ message: 'Invalid username (3-50 alphanumeric chars)' });
  }

  if (!email || !validateEmail(email)) {
    return res.status(400).json({ message: 'Invalid email format' });
  }

  if (!password || !validatePassword(password)) {
    return res.status(400).json({ 
      message: 'Password must be 8+ chars with uppercase, lowercase, number, and special character' 
    });
  }

  if (password !== passwordConfirm) {
    return res.status(400).json({ message: 'Passwords do not match' });
  }

  // Sanitize inputs
  req.body.username = sanitizeInput(username);
  req.body.email = sanitizeInput(email);

  next();
};

/**
 * Validate user update request
 */
const validateUserUpdateInput = (req, res, next) => {
  const { email, phone, address, fullName } = req.body;

  if (email && !validateEmail(email)) {
    return res.status(400).json({ message: 'Invalid email format' });
  }

  if (phone && !validatePhoneNumber(phone)) {
    return res.status(400).json({ message: 'Invalid phone format' });
  }

  if (address && address.length > 500) {
    return res.status(400).json({ message: 'Address too long (max 500 chars)' });
  }

  if (fullName && fullName.length > 200) {
    return res.status(400).json({ message: 'Full name too long (max 200 chars)' });
  }

  // Sanitize inputs
  req.body.email = email ? sanitizeInput(email) : undefined;
  req.body.phone = phone ? sanitizeInput(phone) : undefined;
  req.body.address = address ? sanitizeInput(address) : undefined;
  req.body.fullName = fullName ? sanitizeInput(fullName) : undefined;

  next();
};

/**
 * Validate search/filter parameters
 */
const validateSearchInput = (req, res, next) => {
  const { q, search, keyword } = req.query;
  const searchTerm = q || search || keyword;

  if (searchTerm) {
    if (searchTerm.length > 200) {
      return res.status(400).json({ message: 'Search query too long (max 200 chars)' });
    }
    // Sanitize search term
    req.query.q = sanitizeInput(searchTerm);
  }

  next();
};

module.exports = {
  validateEmail,
  validatePassword,
  validateUsername,
  validatePhoneNumber,
  validateUrl,
  sanitizeInput,
  validateLoginInput,
  validateRegisterInput,
  validateUserUpdateInput,
  validateSearchInput,
};
