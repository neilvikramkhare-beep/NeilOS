package com.neilos.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User - Comprehensive user management for NeilOS
 * Handles user authentication, authorization, preferences,
 * and user-related operations.
 * 
 * Features:
 * - User authentication (login/logout)
 * - Password management (hash, change, reset)
 * - User roles and permissions
 * - User preferences and settings
 * - Session management
 * - User activity tracking
 * - Account lockout and security
 * - Password recovery
 * - User groups
 * - Activity logging
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class User {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Maximum username length */
    public static final int MAX_USERNAME_LENGTH = 50;
    
    /** Minimum username length */
    public static final int MIN_USERNAME_LENGTH = 3;
    
    /** Maximum password length */
    public static final int MAX_PASSWORD_LENGTH = 128;
    
    /** Minimum password length */
    public static final int MIN_PASSWORD_LENGTH = 8;
    
    /** Maximum failed login attempts before lockout */
    public static final int MAX_FAILED_ATTEMPTS = 5;
    
    /** Lockout duration in minutes */
    public static final int LOCKOUT_DURATION_MINUTES = 30;
    
    /** Session timeout in minutes */
    public static final int SESSION_TIMEOUT_MINUTES = 60;
    
    /** Password expiry in days */
    public static final int PASSWORD_EXPIRY_DAYS = 90;
    
    /** Default user role */
    public static final String DEFAULT_ROLE = "USER";
    
    /** Admin role */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /** Guest role */
    public static final String ROLE_GUEST = "GUEST";
    
    /** Date format for timestamps */
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /** Password reset token expiry in minutes */
    public static final int RESET_TOKEN_EXPIRY_MINUTES = 60;
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * User session information
     */
    public static class Session {
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime loginTime;
        private LocalDateTime lastActivity;
        private boolean active;
        private Map<String, Object> attributes;
        
        public Session() {
            this.sessionId = UUID.randomUUID().toString();
            this.loginTime = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
            this.active = true;
            this.attributes = new ConcurrentHashMap<>();
        }
        
        public Session(String ipAddress, String userAgent) {
            this();
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
        }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public LocalDateTime getLoginTime() { return loginTime; }
        public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
        
        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { 
            this.attributes = attributes != null ? attributes : new ConcurrentHashMap<>();
        }
        
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
        
        public boolean isExpired() {
            return lastActivity.plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(LocalDateTime.now());
        }
        
        public void updateActivity() {
            this.lastActivity = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("Session[%s] IP: %s, Login: %s, Active: %b",
                sessionId.substring(0, 8), ipAddress, 
                loginTime.format(DATE_FORMATTER), active);
        }
    }
    
    /**
     * Password reset token
     */
    public static class PasswordResetToken {
        private String token;
        private String userId;
        private String username;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean used;
        private String ipAddress;
        private String userAgent;
        
        public PasswordResetToken(String username) {
            this.username = username;
            this.token = UUID.randomUUID().toString().replace("-", "");
            this.createdAt = LocalDateTime.now();
            this.expiresAt = createdAt.plusMinutes(RESET_TOKEN_EXPIRY_MINUTES);
            this.used = false;
        }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        
        public boolean isUsed() { return used; }
        public void setUsed(boolean used) { this.used = used; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public boolean isValid() {
            return !used && expiresAt.isAfter(LocalDateTime.now());
        }
        
        @Override
        public String toString() {
            return String.format("ResetToken[%s] for %s, Expires: %s, Used: %b",
                token.substring(0, 8), username,
                expiresAt.format(DATE_FORMATTER), used);
        }
    }
    
    /**
     * User activity log entry
     */
    public static class ActivityLog {
        private LocalDateTime timestamp;
        private String action;
        private String details;
        private String ipAddress;
        private String userAgent;
        private String result;
        private Map<String, String> metadata;
        
        public ActivityLog(String action) {
            this.timestamp = LocalDateTime.now();
            this.action = action;
            this.metadata = new HashMap<>();
            this.result = "SUCCESS";
        }
        
        public ActivityLog(String action, String details) {
            this(action);
            this.details = details;
        }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { 
            this.metadata = metadata != null ? metadata : new HashMap<>(); 
        }
        
        public void addMetadata(String key, String value) {
            metadata.put(key, value);
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s (%s) %s",
                timestamp.format(DATE_FORMATTER), action, 
                details != null ? details : "", result, 
                ipAddress != null ? "IP: " + ipAddress : "");
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String role;
    private boolean enabled;
    private boolean locked;
    private boolean expired;
    private boolean credentialsExpired;
    private int failedLoginAttempts;
    private LocalDateTime lastLogin;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lockedUntil;
    private String passwordRecoveryEmail;
    private String phoneNumber;
    private String profilePictureUrl;
    private Map<String, String> preferences;
    private Set<String> groups;
    private List<ActivityLog> activityLogs;
    private Set<Session> activeSessions;
    private List<String> permissions;
    private Map<String, String> metadata;
    private boolean twoFactorEnabled;
    private String twoFactorSecret;
    private boolean forcePasswordChange;
    private String createdBy;
    private String lastModifiedBy;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.locked = false;
        this.expired = false;
        this.credentialsExpired = false;
        this.failedLoginAttempts = 0;
        this.role = DEFAULT_ROLE;
        this.preferences = new ConcurrentHashMap<>();
        this.groups = new HashSet<>();
        this.activityLogs = new ArrayList<>();
        this.activeSessions = new HashSet<>();
        this.permissions = new ArrayList<>();
        this.metadata = new ConcurrentHashMap<>();
        this.twoFactorEnabled = false;
        this.forcePasswordChange = false;
        setDefaultPreferences();
    }
    
    /**
     * Constructor with basic user information
     * 
     * @param username The username
     * @param passwordHash The hashed password
     */
    public User(String username, String passwordHash) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordChangedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with full user information
     * 
     * @param username The username
     * @param passwordHash The hashed password
     * @param email The email address
     * @param fullName The full name
     */
    public User(String username, String passwordHash, String email, String fullName) {
        this(username, passwordHash);
        this.email = email;
        this.fullName = fullName;
        this.passwordRecoveryEmail = email;
    }
    
    // ============================================================
    // AUTHENTICATION METHODS
    // ============================================================
    
    /**
     * Authenticates a user with password
     * 
     * @param password The plain text password
     * @return true if authentication successful
     */
    public boolean authenticate(String password, String ipAddress, String userAgent) {
        // Check if user is locked
        if (isLocked()) {
            return false;
        }
        
        // Check if user is expired
        if (isExpired()) {
            return false;
        }
        
        // Check if user is enabled
        if (!isEnabled()) {
            return false;
        }
        
        // Verify password
        boolean authenticated = verifyPassword(password);
        
        if (authenticated) {
            // Reset failed attempts
            failedLoginAttempts = 0;
            lastLogin = LocalDateTime.now();
            
            // Create session
            Session session = new Session(ipAddress, userAgent);
            activeSessions.add(session);
            
            // Log activity
            logActivity("LOGIN_SUCCESS", "User logged in successfully", ipAddress, userAgent);
            
            // Update last login
            lastLogin = LocalDateTime.now();
            
            // Check if password needs to be changed
            if (isPasswordExpired()) {
                forcePasswordChange = true;
                logActivity("PASSWORD_EXPIRED", "Password expired, change required", ipAddress, userAgent);
            }
            
            updatedAt = LocalDateTime.now();
            return true;
        } else {
            // Increment failed attempts
            failedLoginAttempts++;
            
            // Check if account should be locked
            if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount("Too many failed login attempts");
                logActivity("ACCOUNT_LOCKED", "Account locked due to failed attempts", ipAddress, userAgent);
            }
            
            logActivity("LOGIN_FAILED", "Failed login attempt", ipAddress, userAgent);
            updatedAt = LocalDateTime.now();
            return false;
        }
    }
    
    /**
     * Logs out a user session
     * 
     * @param sessionId The session ID
     * @return true if session was found and closed
     */
    public boolean logout(String sessionId) {
        for (Session session : activeSessions) {
            if (session.getSessionId().equals(sessionId)) {
                session.setActive(false);
                logActivity("LOGOUT", "User logged out", session.getIpAddress(), session.getUserAgent());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifies a password against the stored hash
     * 
     * @param password The plain text password
     * @return true if password matches
     */
    private boolean verifyPassword(String password) {
        // In production, use proper password verification with salt
        // This is a simplified version
        return passwordHash != null && passwordHash.equals(hashPassword(password));
    }
    
    /**
     * Hashes a password (simplified version)
     * 
     * @param password The plain text password
     * @return The hashed password
     */
    private String hashPassword(String password) {
        // In production, use bcrypt, PBKDF2, or Argon2
        // This is a simplified SHA-256 hash
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password; // Fallback - NOT for production
        }
    }
    
    // ============================================================
    // PASSWORD MANAGEMENT
    // ============================================================
    
    /**
     * Changes the user's password
     * 
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return true if password changed successfully
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            return false;
        }
        
        if (!isValidPassword(newPassword)) {
            return false;
        }
        
        passwordHash = hashPassword(newPassword);
        passwordChangedAt = LocalDateTime.now();
        forcePasswordChange = false;
        credentialsExpired = false;
        updatedAt = LocalDateTime.now();
        logActivity("PASSWORD_CHANGED", "Password changed successfully", null, null);
        return true;
    }
    
    /**
     * Resets the user's password (admin only)
     * 
     * @param newPassword The new password
     * @param adminUser The admin performing the reset
     * @return true if password reset successfully
     */
    public boolean resetPassword(String newPassword, User adminUser) {
        if (!isValidPassword(newPassword)) {
            return false;
        }
        
        passwordHash = hashPassword(newPassword);
        passwordChangedAt = LocalDateTime.now();
        forcePasswordChange = true;
        updatedAt = LocalDateTime.now();
        logActivity("PASSWORD_RESET", "Password reset by admin: " + adminUser.getUsername(), null, null);
        return true;
    }
    
    /**
     * Validates a password against security requirements
     * 
     * @param password The password to validate
     * @return true if password is valid
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH || 
            password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates a password reset token
     * 
     * @param ipAddress The request IP address
     * @param userAgent The user agent
     * @return PasswordResetToken object
     */
    public PasswordResetToken createPasswordResetToken(String ipAddress, String userAgent) {
        PasswordResetToken token = new PasswordResetToken(username);
        token.setUserId(id);
        token.setIpAddress(ipAddress);
        token.setUserAgent(userAgent);
        logActivity("PASSWORD_RESET_TOKEN", "Password reset token created", ipAddress, userAgent);
        return token;
    }
    
    /**
     * Validates a password reset token
     * 
     * @param token The token to validate
     * @return true if token is valid
     */
    public boolean validateResetToken(PasswordResetToken token) {
        if (token == null) {
            return false;
        }
        
        return token.isValid() && 
               token.getUserId().equals(id) && 
               token.getUsername().equals(username);
    }
    
    // ============================================================
    // ACCOUNT MANAGEMENT
    // ============================================================
    
    /**
     * Locks the user account
     * 
     * @param reason The reason for locking
     */
    public void lockAccount(String reason) {
        this.locked = true;
        this.lockedUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
        logActivity("ACCOUNT_LOCKED", "Account locked: " + reason, null, null);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Unlocks the user account
     */
    public void unlockAccount() {
        this.locked = false;
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
        logActivity("ACCOUNT_UNLOCKED", "Account unlocked", null, null);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Disables the user account
     * 
     * @param reason The reason for disabling
     */
    public void disableAccount(String reason) {
        this.enabled = false;
        logActivity("ACCOUNT_DISABLED", "Account disabled: " + reason, null, null);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enables the user account
     */
    public void enableAccount() {
        this.enabled = true;
        logActivity("ACCOUNT_ENABLED", "Account enabled", null, null);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Deletes the user account (soft delete)
     */
    public void deleteAccount() {
        this.enabled = false;
        this.expired = true;
        logActivity("ACCOUNT_DELETED", "Account deleted", null, null);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Restores a deleted account
     */
    public void restoreAccount() {
        this.enabled = true;
        this.expired = false;
        logActivity("ACCOUNT_RESTORED", "Account restored", null, null);
        updatedAt = LocalDateTime.now();
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
    /**
     * Gets an active session by ID
     * 
     * @param sessionId The session ID
     * @return The session, or null if not found
     */
    public Session getSession(String sessionId) {
        for (Session session : activeSessions) {
            if (session.getSessionId().equals(sessionId) && session.isActive()) {
                session.updateActivity();
                return session;
            }
        }
        return null;
    }
    
    /**
     * Gets all active sessions
     * 
     * @return Set of active sessions
     */
    public Set<Session> getActiveSessions() {
        // Remove expired sessions
        activeSessions.removeIf(session -> !session.isActive() || session.isExpired());
        return new HashSet<>(activeSessions);
    }
    
    /**
     * Terminates all active sessions
     */
    public void terminateAllSessions() {
        for (Session session : activeSessions) {
            session.setActive(false);
        }
        activeSessions.clear();
        logActivity("ALL_SESSIONS_TERMINATED", "All sessions terminated", null, null);
    }
    
    /**
     * Terminates a specific session
     * 
     * @param sessionId The session ID
     * @return true if session was terminated
     */
    public boolean terminateSession(String sessionId) {
        for (Session session : activeSessions) {
            if (session.getSessionId().equals(sessionId)) {
                session.setActive(false);
                logActivity("SESSION_TERMINATED", "Session terminated: " + sessionId, null, null);
                return true;
            }
        }
        return false;
    }
    
    // ============================================================
    // PERMISSION MANAGEMENT
    // ============================================================
    
    /**
     * Checks if the user has a specific permission
     * 
     * @param permission The permission to check
     * @return true if user has the permission
     */
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        
        // Admin has all permissions
        if (ROLE_ADMIN.equals(role)) {
            return true;
        }
        
        return permissions.contains(permission);
    }
    
    /**
     * Adds a permission to the user
     * 
     * @param permission The permission to add
     */
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            updatedAt = LocalDateTime.now();
            logActivity("PERMISSION_ADDED", "Added permission: " + permission, null, null);
        }
    }
    
    /**
     * Removes a permission from the user
     * 
     * @param permission The permission to remove
     */
    public void removePermission(String permission) {
        permissions.remove(permission);
        updatedAt = LocalDateTime.now();
        logActivity("PERMISSION_REMOVED", "Removed permission: " + permission, null, null);
    }
    
    /**
     * Checks if the user is in a specific group
     * 
     * @param group The group name
     * @return true if user is in the group
     */
    public boolean isInGroup(String group) {
        return groups.contains(group);
    }
    
    /**
     * Adds the user to a group
     * 
     * @param group The group name
     */
    public void addGroup(String group) {
        groups.add(group);
        updatedAt = LocalDateTime.now();
        logActivity("GROUP_ADDED", "Added to group: " + group, null, null);
    }
    
    /**
     * Removes the user from a group
     * 
     * @param group The group name
     */
    public void removeGroup(String group) {
        groups.remove(group);
        updatedAt = LocalDateTime.now();
        logActivity("GROUP_REMOVED", "Removed from group: " + group, null, null);
    }
    
    // ============================================================
    // PREFERENCES
    // ============================================================
    
    /**
     * Sets default user preferences
     */
    private void setDefaultPreferences() {
        preferences.put("theme", "dark");
        preferences.put("language", "en");
        preferences.put("notifications", "true");
        preferences.put("fontSize", "14");
        preferences.put("terminalHistorySize", "1000");
        preferences.put("autoSave", "true");
        preferences.put("confirmDeletion", "true");
        preferences.put("showHiddenFiles", "false");
        preferences.put("timezone", "UTC");
        preferences.put("dateFormat", "yyyy-MM-dd");
        preferences.put("timeFormat", "HH:mm:ss");
        preferences.put("firstLogin", "true");
    }
    
    /**
     * Gets a preference value
     * 
     * @param key The preference key
     * @return The preference value, or null if not found
     */
    public String getPreference(String key) {
        return preferences.get(key);
    }
    
    /**
     * Gets a preference value with default
     * 
     * @param key The preference key
     * @param defaultValue The default value
     * @return The preference value or default
     */
    public String getPreference(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }
    
    /**
     * Sets a preference value
     * 
     * @param key The preference key
     * @param value The preference value
     */
    public void setPreference(String key, String value) {
        preferences.put(key, value);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Removes a preference
     * 
     * @param key The preference key
     */
    public void removePreference(String key) {
        preferences.remove(key);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Gets all preferences
     * 
     * @return Map of all preferences
     */
    public Map<String, String> getPreferences() {
        return new HashMap<>(preferences);
    }
    
    /**
     * Gets a preference as boolean
     * 
     * @param key The preference key
     * @param defaultValue The default value
     * @return The preference value as boolean
     */
    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        String value = preferences.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets a preference as integer
     * 
     * @param key The preference key
     * @param defaultValue The default value
     * @return The preference value as integer
     */
    public int getPreferenceInt(String key, int defaultValue) {
        String value = preferences.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    // ============================================================
    // ACTIVITY LOGGING
    // ============================================================
    
    /**
     * Logs a user activity
     * 
     * @param action The action performed
     * @param details Additional details
     * @param ipAddress The IP address
     * @param userAgent The user agent
     */
    public void logActivity(String action, String details, String ipAddress, String userAgent) {
        ActivityLog log = new ActivityLog(action, details);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        activityLogs.add(log);
        
        // Keep only last 1000 logs
        if (activityLogs.size() > 1000) {
            activityLogs.subList(0, activityLogs.size() - 1000).clear();
        }
    }
    
    /**
     * Gets the user's activity logs
     * 
     * @param limit The maximum number of logs to return
     * @return List of activity logs
     */
    public List<ActivityLog> getActivityLogs(int limit) {
        int start = Math.max(0, activityLogs.size() - limit);
        return new ArrayList<>(activityLogs.subList(start, activityLogs.size()));
    }
    
    /**
     * Gets the user's full activity log
     * 
     * @return List of all activity logs
     */
    public List<ActivityLog> getActivityLogs() {
        return new ArrayList<>(activityLogs);
    }
    
    /**
     * Clears the user's activity logs
     */
    public void clearActivityLogs() {
        activityLogs.clear();
        logActivity("LOGS_CLEARED", "Activity logs cleared", null, null);
    }
    
    // ============================================================
    // TWO-FACTOR AUTHENTICATION
    // ============================================================
    
    /**
     * Enables two-factor authentication
     * 
     * @param secret The TOTP secret
     * @return true if enabled
     */
    public boolean enableTwoFactor(String secret) {
        this.twoFactorEnabled = true;
        this.twoFactorSecret = secret;
        updatedAt = LocalDateTime.now();
        logActivity("2FA_ENABLED", "Two-factor authentication enabled", null, null);
        return true;
    }
    
    /**
     * Disables two-factor authentication
     * 
     * @return true if disabled
     */
    public boolean disableTwoFactor() {
        this.twoFactorEnabled = false;
        this.twoFactorSecret = null;
        updatedAt = LocalDateTime.now();
        logActivity("2FA_DISABLED", "Two-factor authentication disabled", null, null);
        return true;
    }
    
    /**
     * Checks if two-factor authentication is enabled
     * 
     * @return true if enabled
     */
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    
    // ============================================================
    // VALIDATION METHODS
    // ============================================================
    
    /**
     * Validates the user data
     * 
     * @return List of validation errors
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        } else if (username.length() < MIN_USERNAME_LENGTH) {
            errors.add("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
        } else if (username.length() > MAX_USERNAME_LENGTH) {
            errors.add("Username must be at most " + MAX_USERNAME_LENGTH + " characters");
        } else if (!username.matches("^[a-zA-Z0-9_\\-]+$")) {
            errors.add("Username can only contain letters, numbers, underscores, and hyphens");
        }
        
        // Validate email
        if (email != null && !email.isEmpty()) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                               "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!email.matches(emailRegex)) {
                errors.add("Invalid email format");
            }
        }
        
        return errors;
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Checks if the user account is locked
     * 
     * @return true if locked
     */
    public boolean isLocked() {
        if (!locked) {
            return false;
        }
        
        // Check if lock period has expired
        if (lockedUntil != null && lockedUntil.isBefore(LocalDateTime.now())) {
            unlockAccount();
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if the password is expired
     * 
     * @return true if password is expired
     */
    public boolean isPasswordExpired() {
        if (passwordChangedAt == null) {
            return true;
        }
        return passwordChangedAt.plusDays(PASSWORD_EXPIRY_DAYS).isBefore(LocalDateTime.now());
    }
    
    /**
     * Gets the account status as a string
     * 
     * @return Account status description
     */
    public String getAccountStatus() {
        if (!enabled) {
            return "DISABLED";
        }
        if (isLocked()) {
            return "LOCKED";
        }
        if (expired) {
            return "EXPIRED";
        }
        if (forcePasswordChange) {
            return "PASSWORD_CHANGE_REQUIRED";
        }
        return "ACTIVE";
    }
    
    /**
     * Checks if the user is currently online
     * 
     * @return true if user has active sessions
     */
    public boolean isOnline() {
        for (Session session : activeSessions) {
            if (session.isActive() && !session.isExpired()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates a copy of the user
     * 
     * @return A shallow copy of the user
     */
    public User copy() {
        User copy = new User();
        copy.id = this.id;
        copy.username = this.username;
        copy.passwordHash = this.passwordHash;
        copy.email = this.email;
        copy.fullName = this.fullName;
        copy.role = this.role;
        copy.enabled = this.enabled;
        copy.locked = this.locked;
        copy.expired = this.expired;
        copy.credentialsExpired = this.credentialsExpired;
        copy.failedLoginAttempts = this.failedLoginAttempts;
        copy.lastLogin = this.lastLogin;
        copy.passwordChangedAt = this.passwordChangedAt;
        copy.createdAt = this.createdAt;
        copy.updatedAt = this.updatedAt;
        copy.lockedUntil = this.lockedUntil;
        copy.passwordRecoveryEmail = this.passwordRecoveryEmail;
        copy.phoneNumber = this.phoneNumber;
        copy.profilePictureUrl = this.profilePictureUrl;
        copy.preferences = new HashMap<>(this.preferences);
        copy.groups = new HashSet<>(this.groups);
        copy.activityLogs = new ArrayList<>(this.activityLogs);
        copy.activeSessions = new HashSet<>(this.activeSessions);
        copy.permissions = new ArrayList<>(this.permissions);
        copy.metadata = new HashMap<>(this.metadata);
        copy.twoFactorEnabled = this.twoFactorEnabled;
        copy.twoFactorSecret = this.twoFactorSecret;
        copy.forcePasswordChange = this.forcePasswordChange;
        copy.createdBy = this.createdBy;
        copy.lastModifiedBy = this.lastModifiedBy;
        return copy;
    }
    
    // ============================================================
    // TO STRING METHODS
    // ============================================================
    
    @Override
    public String toString() {
        return String.format("User[%s] %s (%s) - %s", 
            id.substring(0, 8), username, role, getAccountStatus());
    }
    
    /**
     * Gets a detailed string representation
     * 
     * @return Detailed user information
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(60)).append("\n");
        sb.append("👤 USER DETAILS\n");
        sb.append("═".repeat(60)).append("\n");
        sb.append("ID:              ").append(id).append("\n");
        sb.append("Username:        ").append(username).append("\n");
        sb.append("Full Name:       ").append(fullName != null ? fullName : "Not set").append("\n");
        sb.append("Email:           ").append(email != null ? email : "Not set").append("\n");
        sb.append("Phone:           ").append(phoneNumber != null ? phoneNumber : "Not set").append("\n");
        sb.append("Role:            ").append(role).append("\n");
        sb.append("Status:          ").append(getAccountStatus()).append("\n");
        sb.append("Online:          ").append(isOnline() ? "✅ Yes" : "❌ No").append("\n");
        sb.append("Groups:          ").append(groups.isEmpty() ? "None" : String.join(", ", groups)).append("\n");
        sb.append("2FA Enabled:     ").append(twoFactorEnabled ? "✅ Yes" : "❌ No").append("\n");
        sb.append("Created By:      ").append(createdBy != null ? createdBy : "System").append("\n");
        sb.append("Created At:      ").append(createdAt.format(DATE_FORMATTER)).append("\n");
        sb.append("Updated At:      ").append(updatedAt.format(DATE_FORMATTER)).append("\n");
        sb.append("Last Login:      ").append(lastLogin != null ? lastLogin.format(DATE_FORMATTER) : "Never").append("\n");
        sb.append("Failed Attempts: ").append(failedLoginAttempts).append("/").append(MAX_FAILED_ATTEMPTS).append("\n");
        
        if (!permissions.isEmpty()) {
            sb.append("\n📋 Permissions:\n");
            for (String perm : permissions) {
                sb.append("  • ").append(perm).append("\n");
            }
        }
        
        if (!preferences.isEmpty()) {
            sb.append("\n⚙️ Preferences:\n");
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                sb.append("  • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        if (!activityLogs.isEmpty()) {
            sb.append("\n📜 Recent Activity:\n");
            List<ActivityLog> recent = getActivityLogs(5);
            for (ActivityLog log : recent) {
                sb.append("  • ").append(log.toString()).append("\n");
            }
        }
        
        sb.append("═".repeat(60)).append("\n");
        return sb.toString();
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash;
        this.passwordChangedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { 
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRole() { return role; }
    public void setRole(String role) { 
        this.role = role;
        this.updatedAt = LocalDateTime.now();
        logActivity("ROLE_CHANGED", "Role changed to: " + role, null, null);
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { 
        this.enabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { 
        this.expired = expired;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isCredentialsExpired() { return credentialsExpired; }
    public void setCredentialsExpired(boolean credentialsExpired) { 
        this.credentialsExpired = credentialsExpired;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { 
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public LocalDateTime getPasswordChangedAt() { return passwordChangedAt; }
    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) { 
        this.passwordChangedAt = passwordChangedAt;
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    
    public String getPasswordRecoveryEmail() { return passwordRecoveryEmail; }
    public void setPasswordRecoveryEmail(String passwordRecoveryEmail) { 
        this.passwordRecoveryEmail = passwordRecoveryEmail;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { 
        this.profilePictureUrl = profilePictureUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<String> getGroups() { return new HashSet<>(groups); }
    public void setGroups(Set<String> groups) { 
        this.groups = groups != null ? groups : new HashSet<>();
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getPermissions() { return new ArrayList<>(permissions); }
    public void setPermissions(List<String> permissions) { 
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Map<String, String> getMetadata() { return new HashMap<>(metadata); }
    public void setMetadata(Map<String, String> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
        updatedAt = LocalDateTime.now();
    }
    
    public String getMetadata(String key) {
        return metadata.get(key);
    }
    
    public String getTwoFactorSecret() { return twoFactorSecret; }
    public void setTwoFactorSecret(String twoFactorSecret) { this.twoFactorSecret = twoFactorSecret; }
    
    public boolean isForcePasswordChange() { return forcePasswordChange; }
    public void setForcePasswordChange(boolean forcePasswordChange) { 
        this.forcePasswordChange = forcePasswordChange;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { 
        this.lastModifiedBy = lastModifiedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of User
     */
    public static void main(String[] args) {
        System.out.println("👤 User Demo");
        System.out.println("═".repeat(60));
        
        // Create a user
        User user = new User("john_doe", "password123", "john@example.com", "John Doe");
        System.out.println("📝 Created user: " + user);
        
        // Set preferences
        user.setPreference("theme", "dark");
        user.setPreference("language", "en");
        user.setPreference("fontSize", "14");
        
        // Set role
        user.setRole("ADMIN");
        
        // Add permissions
        user.addPermission("user:create");
        user.addPermission("user:delete");
        user.addPermission("system:admin");
        
        // Add groups
        user.addGroup("administrators");
        user.addGroup("developers");
        
        // Authenticate
        System.out.println("\n🔐 Authentication Test:");
        boolean auth = user.authenticate("password123", "192.168.1.100", "Mozilla/5.0");
        System.out.println("  Authentication: " + (auth ? "✅ Success" : "❌ Failed"));
        
        // Check permissions
        System.out.println("\n🔑 Permission Check:");
        System.out.println("  user:create: " + user.hasPermission("user:create"));
        System.out.println("  user:delete: " + user.hasPermission("user:delete"));
        System.out.println("  system:admin: " + user.hasPermission("system:admin"));
        System.out.println("  non:existent: " + user.hasPermission("non:existent"));
        
        // Check groups
        System.out.println("\n👥 Group Check:");
        System.out.println("  administrators: " + user.isInGroup("administrators"));
        System.out.println("  developers: " + user.isInGroup("developers"));
        System.out.println("  users: " + user.isInGroup("users"));
        
        // Preferences
        System.out.println("\n⚙️ Preferences:");
        System.out.println("  Theme: " + user.getPreference("theme"));
        System.out.println("  Language: " + user.getPreference("language"));
        System.out.println("  Font Size: " + user.getPreference("fontSize"));
        
        // Activity logs
        System.out.println("\n📜 Activity Logs:");
        user.logActivity("FILE_UPLOAD", "Uploaded document.pdf", "192.168.1.100", "Mozilla/5.0");
        user.logActivity("FILE_DELETE", "Deleted old file.txt", "192.168.1.100", "Mozilla/5.0");
        user.logActivity("SETTINGS_CHANGE", "Changed theme to dark", "192.168.1.100", "Mozilla/5.0");
        
        List<ActivityLog> logs = user.getActivityLogs(3);
        for (ActivityLog log : logs) {
            System.out.println("  " + log);
        }
        
        // Sessions
        System.out.println("\n💻 Sessions:");
        Session session = new Session("192.168.1.100", "Mozilla/5.0");
        user.activeSessions.add(session);
        System.out.println("  " + session);
        System.out.println("  Is online: " + user.isOnline());
        
        // Password reset token
        System.out.println("\n🔑 Password Reset Token:");
        PasswordResetToken token = user.createPasswordResetToken("192.168.1.100", "Mozilla/5.0");
        System.out.println("  Token: " + token);
        System.out.println("  Valid: " + token.isValid());
        
        // Detailed user info
        System.out.println("\n📋 Detailed User Info:");
        System.out.println(user.toDetailedString());
        
        // Validation
        System.out.println("\n✅ Validation:");
        List<String> errors = user.validate();
        if (errors.isEmpty()) {
            System.out.println("  User data is valid");
        } else {
            System.out.println("  Errors:");
            for (String error : errors) {
                System.out.println("  ❌ " + error);
            }
        }
        
        System.out.println("\n✅ Demo completed!");
    }
}