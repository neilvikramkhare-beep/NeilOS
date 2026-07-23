package com.neilos.security;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.util.*;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.net.ssl.*;
import javax.security.auth.Destroyable;

/**
 * SecurityUtils - Comprehensive security utility class for NeilOS
 * Provides encryption, hashing, secure random generation, certificate handling,
 * password management, and various security operations.
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class SecurityUtils {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default character encoding */
    public static final String UTF_8 = "UTF-8";
    
    /** Default encryption algorithm */
    public static final String DEFAULT_ENCRYPTION = "AES";
    
    /** Default encryption mode */
    public static final String DEFAULT_MODE = "GCM";
    
    /** Default encryption padding */
    public static final String DEFAULT_PADDING = "PKCS5Padding";
    
    /** Default encryption transformation */
    public static final String DEFAULT_TRANSFORMATION = "AES/GCM/NoPadding";
    
    /** Default key size in bits */
    public static final int DEFAULT_KEY_SIZE = 256;
    
    /** Default salt size in bytes */
    public static final int DEFAULT_SALT_SIZE = 32;
    
    /** Default IV size in bytes (GCM) */
    public static final int DEFAULT_IV_SIZE = 12;
    
    /** Default iteration count for PBKDF2 */
    public static final int DEFAULT_ITERATIONS = 100000;
    
    /** Password minimum length */
    public static final int MIN_PASSWORD_LENGTH = 8;
    
    /** Password maximum length */
    public static final int MAX_PASSWORD_LENGTH = 128;
    
    /** Secure random algorithm */
    public static final String SECURE_RANDOM_ALGORITHM = "NativePRNGNonBlocking";
    
    /** Key store type */
    public static final String KEY_STORE_TYPE = "PKCS12";
    
    // ============================================================
    // STATIC INITIALIZATION
    // ============================================================
    
    static {
        // Add Bouncy Castle provider if available
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (ClassNotFoundException e) {
            // Bouncy Castle not available, use default providers
        }
    }
    
    // ============================================================
    // ENCRYPTION / DECRYPTION
    // ============================================================
    
    /**
     * Encrypts data using AES with the specified key
     * 
     * @param data The data to encrypt
     * @param key The encryption key (must be 16, 24, or 32 bytes)
     * @return The encrypted data with IV prepended
     * @throws Exception If encryption fails
     */
    public static byte[] encryptAES(byte[] data, byte[] key) throws Exception {
        // Generate random IV
        byte[] iv = generateRandomBytes(DEFAULT_IV_SIZE);
        
        // Create cipher
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(key, DEFAULT_ENCRYPTION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        
        // Encrypt
        byte[] encrypted = cipher.doFinal(data);
        
        // Combine IV + encrypted data
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        
        return result;
    }
    
    /**
     * Decrypts data using AES with the specified key
     * 
     * @param encryptedData The encrypted data (IV prepended)
     * @param key The encryption key
     * @return The decrypted data
     * @throws Exception If decryption fails
     */
    public static byte[] decryptAES(byte[] encryptedData, byte[] key) throws Exception {
        // Extract IV
        byte[] iv = new byte[DEFAULT_IV_SIZE];
        System.arraycopy(encryptedData, 0, iv, 0, DEFAULT_IV_SIZE);
        
        // Extract encrypted data
        byte[] encrypted = new byte[encryptedData.length - DEFAULT_IV_SIZE];
        System.arraycopy(encryptedData, DEFAULT_IV_SIZE, encrypted, 0, encrypted.length);
        
        // Create cipher
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(key, DEFAULT_ENCRYPTION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        
        return cipher.doFinal(encrypted);
    }
    
    /**
     * Encrypts data with password-based encryption
     * 
     * @param data The data to encrypt
     * @param password The password
     * @return The encrypted data with salt and IV prepended
     * @throws Exception If encryption fails
     */
    public static byte[] encryptWithPassword(byte[] data, String password) throws Exception {
        // Generate salt
        byte[] salt = generateRandomBytes(DEFAULT_SALT_SIZE);
        
        // Generate key from password
        byte[] key = deriveKeyFromPassword(password, salt, DEFAULT_ITERATIONS);
        
        // Encrypt
        byte[] encrypted = encryptAES(data, key);
        
        // Combine salt + encrypted
        byte[] result = new byte[salt.length + encrypted.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(encrypted, 0, result, salt.length, encrypted.length);
        
        return result;
    }
    
    /**
     * Decrypts data with password-based encryption
     * 
     * @param encryptedData The encrypted data (salt + IV + data)
     * @param password The password
     * @return The decrypted data
     * @throws Exception If decryption fails
     */
    public static byte[] decryptWithPassword(byte[] encryptedData, String password) throws Exception {
        // Extract salt
        byte[] salt = new byte[DEFAULT_SALT_SIZE];
        System.arraycopy(encryptedData, 0, salt, 0, DEFAULT_SALT_SIZE);
        
        // Extract encrypted data
        byte[] encrypted = new byte[encryptedData.length - DEFAULT_SALT_SIZE];
        System.arraycopy(encryptedData, DEFAULT_SALT_SIZE, encrypted, 0, encrypted.length);
        
        // Generate key from password
        byte[] key = deriveKeyFromPassword(password, salt, DEFAULT_ITERATIONS);
        
        // Decrypt
        return decryptAES(encrypted, key);
    }
    
    /**
     * Encrypts data with RSA public key
     * 
     * @param data The data to encrypt
     * @param publicKey The RSA public key
     * @return The encrypted data
     * @throws Exception If encryption fails
     */
    public static byte[] encryptRSA(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
    
    /**
     * Decrypts data with RSA private key
     * 
     * @param encryptedData The encrypted data
     * @param privateKey The RSA private key
     * @return The decrypted data
     * @throws Exception If decryption fails
     */
    public static byte[] decryptRSA(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }
    
    // ============================================================
    // KEY DERIVATION
    // ============================================================
    
    /**
     * Derives a key from a password using PBKDF2
     * 
     * @param password The password
     * @param salt The salt
     * @param iterations The iteration count
     * @return The derived key
     * @throws Exception If key derivation fails
     */
    public static byte[] deriveKeyFromPassword(String password, byte[] salt, int iterations) 
            throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, DEFAULT_KEY_SIZE);
        SecretKey secret = factory.generateSecret(spec);
        return secret.getEncoded();
    }
    
    /**
     * Derives a key from a password with default iterations
     * 
     * @param password The password
     * @param salt The salt
     * @return The derived key
     * @throws Exception If key derivation fails
     */
    public static byte[] deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        return deriveKeyFromPassword(password, salt, DEFAULT_ITERATIONS);
    }
    
    // ============================================================
    // KEY GENERATION
    // ============================================================
    
    /**
     * Generates a random AES key
     * 
     * @param keySize The key size in bits (128, 192, 256)
     * @return The generated key
     * @throws Exception If key generation fails
     */
    public static SecretKey generateAESKey(int keySize) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(DEFAULT_ENCRYPTION);
        generator.init(keySize, getSecureRandom());
        return generator.generateKey();
    }
    
    /**
     * Generates a default AES key (256-bit)
     * 
     * @return The generated key
     * @throws Exception If key generation fails
     */
    public static SecretKey generateAESKey() throws Exception {
        return generateAESKey(DEFAULT_KEY_SIZE);
    }
    
    /**
     * Generates an RSA key pair
     * 
     * @param keySize The key size in bits (2048, 3072, 4096)
     * @return The generated key pair
     * @throws Exception If key generation fails
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize, getSecureRandom());
        return generator.generateKeyPair();
    }
    
    /**
     * Generates a default RSA key pair (2048-bit)
     * 
     * @return The generated key pair
     * @throws Exception If key generation fails
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        return generateRSAKeyPair(2048);
    }
    
    /**
     * Generates an EC key pair
     * 
     * @param curveName The curve name (e.g., "secp256r1", "secp384r1", "secp521r1")
     * @return The generated key pair
     * @throws Exception If key generation fails
     */
    public static KeyPair generateECKeyPair(String curveName) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec(curveName), getSecureRandom());
        return generator.generateKeyPair();
    }
    
    // ============================================================
    // SECURE RANDOM
    // ============================================================
    
    /**
     * Gets a secure random instance
     * 
     * @return SecureRandom instance
     */
    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }
    
    /**
     * Generates random bytes
     * 
     * @param size The number of bytes to generate
     * @return The random bytes
     */
    public static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        getSecureRandom().nextBytes(bytes);
        return bytes;
    }
    
    /**
     * Generates a random alphanumeric string
     * 
     * @param length The length of the string
     * @return The random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = getSecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generates a secure random password
     * 
     * @param length The password length
     * @param includeSpecial Whether to include special characters
     * @return The generated password
     */
    public static String generateSecurePassword(int length, boolean includeSpecial) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (includeSpecial) {
            chars += "!@#$%^&*()_+-=[]{}|;:,.<>?";
        }
        
        SecureRandom random = getSecureRandom();
        StringBuilder sb = new StringBuilder(length);
        
        // Ensure at least one of each type
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt(26)));
        sb.append("abcdefghijklmnopqrstuvwxyz".charAt(random.nextInt(26)));
        sb.append("0123456789".charAt(random.nextInt(10)));
        if (includeSpecial) {
            sb.append("!@#$%^&*()_+-=[]{}|;:,.<>?".charAt(random.nextInt(32)));
        }
        
        // Fill the rest
        for (int i = sb.length(); i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the string
        char[] array = sb.toString().toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        
        return new String(array);
    }
    
    // ============================================================
    // HASHING
    // ============================================================
    
    /**
     * Calculates SHA-256 hash of data
     * 
     * @param data The data to hash
     * @return The hash as bytes
     * @throws Exception If hashing fails
     */
    public static byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }
    
    /**
     * Calculates SHA-256 hash of a string
     * 
     * @param input The input string
     * @return The hash as hex string
     * @throws Exception If hashing fails
     */
    public static String sha256Hex(String input) throws Exception {
        byte[] hash = sha256(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    /**
     * Calculates SHA-512 hash of data
     * 
     * @param data The data to hash
     * @return The hash as bytes
     * @throws Exception If hashing fails
     */
    public static byte[] sha512(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest(data);
    }
    
    /**
     * Calculates a file's SHA-256 hash
     * 
     * @param file The file to hash
     * @return The hash as hex string
     * @throws Exception If hashing fails
     */
    public static String sha256File(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return bytesToHex(digest.digest());
    }
    
    /**
     * Calculates MD5 hash of data
     * 
     * @param data The data to hash
     * @return The hash as bytes
     * @throws Exception If hashing fails
     */
    public static byte[] md5(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(data);
    }
    
    /**
     * Calculates CRC32 checksum of data
     * 
     * @param data The data to checksum
     * @return The CRC32 value
     */
    public static long crc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
    
    // ============================================================
    // HMAC
    // ============================================================
    
    /**
     * Calculates HMAC-SHA256
     * 
     * @param data The data
     * @param key The HMAC key
     * @return The HMAC as bytes
     * @throws Exception If HMAC fails
     */
    public static byte[] hmacSha256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data);
    }
    
    /**
     * Calculates HMAC-SHA512
     * 
     * @param data The data
     * @param key The HMAC key
     * @return The HMAC as bytes
     * @throws Exception If HMAC fails
     */
    public static byte[] hmacSha512(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA512");
        mac.init(keySpec);
        return mac.doFinal(data);
    }
    
    // ============================================================
    // PASSWORD MANAGEMENT
    // ============================================================
    
    /**
     * Checks password strength
     * 
     * @param password The password to check
     * @return PasswordStrength object with score and feedback
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        int score = 0;
        List<String> feedback = new ArrayList<>();
        
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(0, Arrays.asList("Password cannot be empty"), "WEAK");
        }
        
        // Length check
        if (password.length() >= 12) {
            score += 3;
            feedback.add("✓ Excellent length (12+ chars)");
        } else if (password.length() >= 8) {
            score += 2;
            feedback.add("✓ Good length (8-11 chars)");
        } else {
            feedback.add("✗ Too short (<8 chars)");
        }
        
        // Character checks
        if (password.matches(".*[A-Z].*")) {
            score += 1;
            feedback.add("✓ Contains uppercase letters");
        } else {
            feedback.add("✗ No uppercase letters");
        }
        
        if (password.matches(".*[a-z].*")) {
            score += 1;
            feedback.add("✓ Contains lowercase letters");
        } else {
            feedback.add("✗ No lowercase letters");
        }
        
        if (password.matches(".*\\d.*")) {
            score += 1;
            feedback.add("✓ Contains numbers");
        } else {
            feedback.add("✗ No numbers");
        }
        
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/].*")) {
            score += 2;
            feedback.add("✓ Contains special characters");
        } else {
            feedback.add("✗ No special characters");
        }
        
        // Common patterns
        if (password.toLowerCase().contains("password") || 
            password.toLowerCase().contains("123456") ||
            password.toLowerCase().contains("qwerty")) {
            score -= 2;
            feedback.add("⚠️ Contains common password pattern");
        }
        
        // Repetition check
        if (password.matches(".*(.)\\1{3,}.*")) {
            score -= 1;
            feedback.add("⚠️ Contains repeated characters");
        }
        
        // Strength rating
        String strength;
        if (score >= 8) {
            strength = "VERY STRONG 💪";
        } else if (score >= 6) {
            strength = "STRONG ✅";
        } else if (score >= 4) {
            strength = "MODERATE ⚠️";
        } else {
            strength = "WEAK ❌";
        }
        
        return new PasswordStrength(score, feedback, strength);
    }
    
    /**
     * PasswordStrength result class
     */
    public static class PasswordStrength {
        private final int score;
        private final List<String> feedback;
        private final String strength;
        
        public PasswordStrength(int score, List<String> feedback, String strength) {
            this.score = score;
            this.feedback = feedback;
            this.strength = strength;
        }
        
        public int getScore() { return score; }
        public List<String> getFeedback() { return feedback; }
        public String getStrength() { return strength; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Password Strength: ").append(strength).append("\n");
            sb.append("Score: ").append(score).append("/10\n");
            sb.append("Feedback:\n");
            for (String f : feedback) {
                sb.append("  ").append(f).append("\n");
            }
            return sb.toString();
        }
    }
    
    // ============================================================
    // CERTIFICATE HANDLING
    // ============================================================
    
    /**
     * Loads an X.509 certificate from a file
     * 
     * @param file The certificate file
     * @return The X.509 certificate
     * @throws Exception If loading fails
     */
    public static X509Certificate loadCertificate(File file) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(file)) {
            return (X509Certificate) factory.generateCertificate(fis);
        }
    }
    
    /**
     * Loads an X.509 certificate from a string
     * 
     * @param certString The certificate in PEM format
     * @return The X.509 certificate
     * @throws Exception If loading fails
     */
    public static X509Certificate loadCertificateFromString(String certString) throws Exception {
        String pem = certString
            .replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")
            .replaceAll("\\s", "");
        
        byte[] certBytes = Base64.getDecoder().decode(pem);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }
    
    /**
     * Validates an SSL certificate
     * 
     * @param certificate The certificate to validate
     * @param trustedCAs The trusted CA certificates
     * @return true if valid
     */
    public static boolean validateCertificate(X509Certificate certificate, List<X509Certificate> trustedCAs) {
        try {
            certificate.checkValidity();
            
            // Check if signed by trusted CA
            for (X509Certificate ca : trustedCAs) {
                try {
                    certificate.verify(ca.getPublicKey());
                    return true;
                } catch (Exception e) {
                    // Continue checking other CAs
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ============================================================
    // ENCODING / DECODING
    // ============================================================
    
    /**
     * Converts bytes to hex string
     * 
     * @param bytes The bytes to convert
     * @return Hex string
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Converts hex string to bytes
     * 
     * @param hex The hex string
     * @return The bytes
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * Base64 encodes data
     * 
     * @param data The data to encode
     * @return Base64 encoded string
     */
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    /**
     * Base64 decodes data
     * 
     * @param data The Base64 encoded string
     * @return Decoded bytes
     */
    public static byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }
    
    /**
     * URL-safe Base64 encodes data
     * 
     * @param data The data to encode
     * @return URL-safe Base64 encoded string
     */
    public static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
    
    /**
     * URL-safe Base64 decodes data
     * 
     * @param data The URL-safe Base64 encoded string
     * @return Decoded bytes
     */
    public static byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }
    
    // ============================================================
    // COMPRESSION
    // ============================================================
    
    /**
     * Compresses data using Deflate
     * 
     * @param data The data to compress
     * @return Compressed data
     * @throws Exception If compression fails
     */
    public static byte[] compress(byte[] data) throws Exception {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        deflater.end();
        return baos.toByteArray();
    }
    
    /**
     * Decompresses data
     * 
     * @param data The compressed data
     * @return Decompressed data
     * @throws Exception If decompression fails
     */
    public static byte[] decompress(byte[] data) throws Exception {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            baos.write(buffer, 0, count);
        }
        inflater.end();
        return baos.toByteArray();
    }
    
    // ============================================================
    // URL/URI SECURITY
    // ============================================================
    
    /**
     * Validates a URL for security threats
     * 
     * @param url The URL to validate
     * @return SecurityResult with validation details
     */
    public static SecurityResult validateUrl(String url) {
        List<String> issues = new ArrayList<>();
        int riskScore = 0;
        
        if (url == null || url.isEmpty()) {
            return new SecurityResult(false, "URL is empty", 10);
        }
        
        String lowerUrl = url.toLowerCase();
        
        // Check for HTTPS
        if (!lowerUrl.startsWith("https://")) {
            issues.add("⚠️ Missing HTTPS - Connection not secure");
            riskScore += 2;
        }
        
        // Check for suspicious patterns
        String[] suspiciousPatterns = {
            "login", "verify", "secure", "account", "update", "confirm",
            "bank", "paypal", "amazon", "apple", "microsoft", "google",
            "facebook", "instagram", "twitter"
        };
        
        for (String pattern : suspiciousPatterns) {
            if (lowerUrl.contains(pattern)) {
                issues.add("⚠️ Contains potential phishing keyword: " + pattern);
                riskScore += 1;
            }
        }
        
        // Check for IP address in URL
        if (lowerUrl.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
            issues.add("⚠️ Uses IP address instead of domain name");
            riskScore += 3;
        }
        
        // Check for URL shorteners
        String[] shorteners = {"bit.ly", "tinyurl", "goo.gl", "ow.ly", "is.gd", "buff.ly"};
        for (String shortener : shorteners) {
            if (lowerUrl.contains(shortener)) {
                issues.add("⚠️ Uses URL shortener (" + shortener + ") - destination hidden");
                riskScore += 2;
                break;
            }
        }
        
        // Check for excessive length
        if (url.length() > 100) {
            issues.add("⚠️ Unusually long URL");
            riskScore += 1;
        }
        
        // Check for suspicious characters
        if (url.matches(".*[%\\$\\#\\@\\!\\^\\&\\*].*")) {
            issues.add("⚠️ Contains suspicious characters");
            riskScore += 1;
        }
        
        String riskLevel;
        if (riskScore >= 7) {
            riskLevel = "🔴 HIGH RISK - Do not open";
        } else if (riskScore >= 4) {
            riskLevel = "🟡 MEDIUM RISK - Exercise caution";
        } else if (riskScore >= 1) {
            riskLevel = "🟢 LOW RISK - Some indicators";
        } else {
            riskLevel = "✅ SAFE - No issues detected";
        }
        
        return new SecurityResult(issues.isEmpty(), riskLevel, riskScore, issues);
    }
    
    /**
     * SecurityResult class for validation results
     */
    public static class SecurityResult {
        private final boolean safe;
        private final String riskLevel;
        private final int riskScore;
        private final List<String> issues;
        
        public SecurityResult(boolean safe, String riskLevel, int riskScore, List<String> issues) {
            this.safe = safe;
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.issues = issues;
        }
        
        public SecurityResult(boolean safe, String message, int riskScore) {
            this(safe, message, riskScore, Arrays.asList(message));
        }
        
        public boolean isSafe() { return safe; }
        public String getRiskLevel() { return riskLevel; }
        public int getRiskScore() { return riskScore; }
        public List<String> getIssues() { return issues; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Risk Level: ").append(riskLevel).append("\n");
            sb.append("Risk Score: ").append(riskScore).append("/10\n");
            if (!issues.isEmpty()) {
                sb.append("Issues:\n");
                for (String issue : issues) {
                    sb.append("  ").append(issue).append("\n");
                }
            }
            return sb.toString();
        }
    }
    
    // ============================================================
    // FILE SECURITY
    // ============================================================
    
    /**
     * Securely deletes a file by overwriting with random data
     * 
     * @param file The file to securely delete
     * @param passes The number of overwrite passes
     * @throws IOException If deletion fails
     */
    public static void secureDelete(File file, int passes) throws IOException {
        if (!file.exists()) {
            return;
        }
        
        long length = file.length();
        SecureRandom random = getSecureRandom();
        
        for (int pass = 0; pass < passes; pass++) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.seek(0);
                
                // Write random data
                byte[] buffer = new byte[8192];
                long written = 0;
                while (written < length) {
                    random.nextBytes(buffer);
                    int toWrite = (int) Math.min(buffer.length, length - written);
                    raf.write(buffer, 0, toWrite);
                    written += toWrite;
                }
                
                // Write complement
                raf.seek(0);
                written = 0;
                while (written < length) {
                    for (int i = 0; i < buffer.length && written < length; i++) {
                        buffer[i] = (byte) ~buffer[i];
                        written++;
                    }
                    raf.write(buffer, 0, (int) Math.min(buffer.length, length - (written - buffer.length)));
                }
            }
        }
        
        // Finally delete the file
        Files.delete(file.toPath());
    }
    
    /**
     * Securely deletes a file with default passes (3)
     * 
     * @param file The file to securely delete
     * @throws IOException If deletion fails
     */
    public static void secureDelete(File file) throws IOException {
        secureDelete(file, 3);
    }
    
    /**
     * Checks if a file contains suspicious content
     * 
     * @param file The file to check
     * @return true if suspicious
     * @throws IOException If reading fails
     */
    public static boolean isFileSuspicious(File file) throws IOException {
        // Check file extension
        String name = file.getName().toLowerCase();
        String[] suspiciousExtensions = {
            ".exe", ".bat", ".cmd", ".com", ".scr", ".vbs", ".ps1", ".sh",
            ".js", ".jar", ".app", ".msi", ".dll", ".ocx", ".cpl"
        };
        
        for (String ext : suspiciousExtensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        
        // Check file content for suspicious patterns
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            
            String line;
            int linesRead = 0;
            while ((line = reader.readLine()) != null && linesRead < 100) {
                String lowerLine = line.toLowerCase();
                if (lowerLine.contains("powershell") ||
                    lowerLine.contains("eval(") ||
                    lowerLine.contains("exec(") ||
                    lowerLine.contains("cmd.exe") ||
                    lowerLine.contains("system32") ||
                    lowerLine.contains("regsvr32") ||
                    lowerLine.contains("wscript") ||
                    lowerLine.contains("cscript")) {
                    return true;
                }
                linesRead++;
            }
        } catch (Exception e) {
            // Binary file - may be suspicious
            return true;
        }
        
        return false;
    }
    
    // ============================================================
    // CRYPTOGRAPHIC UTILITIES
    // ============================================================
    
    /**
     * Generates a UUID v4
     * 
     * @return UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a shorter unique ID
     * 
     * @return Short unique ID
     */
    public static String generateShortId() {
        return Long.toHexString(System.currentTimeMillis()) + 
               generateRandomString(8);
    }
    
    /**
     * Creates a secure hash of a file for comparison
     * 
     * @param file The file to hash
     * @return The hash as bytes
     * @throws Exception If hashing fails
     */
    public static byte[] secureHashFile(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return digest.digest();
    }
    
    /**
     * Compares two files securely (constant time)
     * 
     * @param file1 First file
     * @param file2 Second file
     * @return true if files are identical
     * @throws Exception If comparison fails
     */
    public static boolean secureFileCompare(File file1, File file2) throws Exception {
        if (file1.length() != file2.length()) {
            return false;
        }
        
        byte[] hash1 = secureHashFile(file1);
        byte[] hash2 = secureHashFile(file2);
        
        return MessageDigest.isEqual(hash1, hash2);
    }
    
    // ============================================================
    // SSL/TLS UTILITIES
    // ============================================================
    
    /**
     * Creates a trust-all SSL context (for testing only!)
     * 
     * @return SSLContext that trusts all certificates
     * @throws Exception If creation fails
     */
    @Deprecated
    public static SSLContext createTrustAllSSLContext() throws Exception {
        TrustManager[] trustAllManagers = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() { return null; }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllManagers, getSecureRandom());
        return sslContext;
    }
    
    // ============================================================
    // INPUT VALIDATION
    // ============================================================
    
    /**
     * Validates an email address
     * 
     * @param email The email to validate
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    /**
     * Validates an IP address
     * 
     * @param ip The IP to validate
     * @return true if valid
     */
    public static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipRegex);
        return pattern.matcher(ip).matches();
    }
    
    /**
     * Validates a filename for security
     * 
     * @param filename The filename to validate
     * @return true if valid
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        // Disallow path traversal and dangerous characters
        return !filename.contains("..") &&
               !filename.contains("/") &&
               !filename.contains("\\") &&
               !filename.contains(":") &&
               !filename.contains("*") &&
               !filename.contains("?") &&
               !filename.contains("\"") &&
               !filename.contains("<") &&
               !filename.contains(">") &&
               !filename.contains("|");
    }
    
    // ============================================================
    // EXCEPTION CLASSES
    // ============================================================
    
    /**
     * Security exception
     */
    public static class SecurityException extends Exception {
        public SecurityException(String message) {
            super(message);
        }
        public SecurityException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Encryption exception
     */
    public static class EncryptionException extends SecurityException {
        public EncryptionException(String message) {
            super(message);
        }
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Decryption exception
     */
    public static class DecryptionException extends SecurityException {
        public DecryptionException(String message) {
            super(message);
        }
        public DecryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of SecurityUtils
     */
    public static void main(String[] args) {
        try {
            System.out.println("🔐 SecurityUtils Demo");
            System.out.println("═".repeat(60));
            
            // Password generation
            System.out.println("\n📝 Password Generation:");
            String password = generateSecurePassword(16, true);
            System.out.println("  Generated: " + password);
            
            // Password strength check
            System.out.println("\n📊 Password Strength:");
            PasswordStrength strength = checkPasswordStrength(password);
            System.out.println(strength);
            
            // Encryption
            System.out.println("\n🔒 Encryption Demo:");
            String plainText = "Hello, NeilOS Security!";
            byte[] encrypted = encryptWithPassword(plainText.getBytes(), "mySecretPassword");
            System.out.println("  Original: " + plainText);
            System.out.println("  Encrypted (hex): " + bytesToHex(encrypted).substring(0, 40) + "...");
            
            byte[] decrypted = decryptWithPassword(encrypted, "mySecretPassword");
            System.out.println("  Decrypted: " + new String(decrypted));
            
            // URL validation
            System.out.println("\n🌐 URL Validation:");
            String testUrl = "https://secure-site.com/login";
            SecurityResult result = validateUrl(testUrl);
            System.out.println("  URL: " + testUrl);
            System.out.println(result);
            
            // Hash
            System.out.println("\n🔑 Hash Demo:");
            String hash = sha256Hex("Security Test");
            System.out.println("  SHA-256: " + hash);
            
            System.out.println("\n✅ Demo completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}