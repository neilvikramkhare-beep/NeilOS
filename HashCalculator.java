package com.neilos.security;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * HashCalculator - Comprehensive hashing utility for NeilOS
 * Supports multiple hash algorithms, file hashing, batch processing,
 * and hash verification.
 * 
 * Supported Algorithms:
 * - MD5, SHA-1, SHA-256, SHA-384, SHA-512
 * - SHA-3 (224, 256, 384, 512)
 * - BLAKE2b, BLAKE2s
 * - CRC32, CRC32C
 * - HMAC variants
 * - RIPEMD-160, RIPEMD-256, RIPEMD-320
 * - Whirlpool
 * - Tiger, Tiger2
 * - GOST3411
 * - SM3
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class HashCalculator {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default hash algorithm */
    public static final String DEFAULT_ALGORITHM = "SHA-256";
    
    /** Supported hash algorithms */
    public static final String[] SUPPORTED_ALGORITHMS = {
        "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512",
        "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512",
        "BLAKE2b-512", "BLAKE2s-256",
        "CRC32", "CRC32C",
        "RIPEMD-160", "RIPEMD-256", "RIPEMD-320",
        "Whirlpool", "Tiger", "Tiger2",
        "GOST3411", "SM3"
    };
    
    /** HMAC supported algorithms */
    public static final String[] HMAC_ALGORITHMS = {
        "HmacMD5", "HmacSHA1", "HmacSHA256", 
        "HmacSHA384", "HmacSHA512", "HmacSHA3-256"
    };
    
    /** Default buffer size for file reading (8MB) */
    public static final int DEFAULT_BUFFER_SIZE = 8192 * 1024;
    
    /** Maximum file size for memory hashing (100MB) */
    public static final long MAX_MEMORY_HASH_SIZE = 100 * 1024 * 1024;
    
    // ============================================================
    // STATIC INITIALIZATION
    // ============================================================
    
    static {
        // Add Bouncy Castle provider for additional algorithms
        Security.addProvider(new BouncyCastleProvider());
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String algorithm;
    private int bufferSize;
    private boolean showProgress;
    private String hmacKey;
    private ExecutorService executor;
    private AtomicInteger processedFiles;
    private AtomicInteger totalFiles;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor using SHA-256
     */
    public HashCalculator() {
        this(DEFAULT_ALGORITHM);
    }
    
    /**
     * Constructor with specified algorithm
     * 
     * @param algorithm The hash algorithm to use
     */
    public HashCalculator(String algorithm) {
        this.algorithm = validateAlgorithm(algorithm);
        this.bufferSize = DEFAULT_BUFFER_SIZE;
        this.showProgress = true;
        this.processedFiles = new AtomicInteger(0);
        this.totalFiles = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool(
            Math.min(Runtime.getRuntime().availableProcessors(), 4)
        );
    }
    
    // ============================================================
    // ALGORITHM VALIDATION
    // ============================================================
    
    /**
     * Validates if the algorithm is supported
     * 
     * @param algorithm The algorithm name
     * @return The validated algorithm name
     * @throws IllegalArgumentException If algorithm is not supported
     */
    public static String validateAlgorithm(String algorithm) {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            return DEFAULT_ALGORITHM;
        }
        
        String algo = algorithm.trim().toUpperCase();
        
        // Check if algorithm is supported
        for (String supported : SUPPORTED_ALGORITHMS) {
            if (supported.equalsIgnoreCase(algo)) {
                return supported;
            }
        }
        
        // Check if algorithm is available via Java security
        try {
            MessageDigest.getInstance(algo);
            return algo;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(
                "Unsupported hash algorithm: " + algorithm + 
                "\nSupported algorithms: " + String.join(", ", SUPPORTED_ALGORITHMS)
            );
        }
    }
    
    /**
     * Gets the list of supported algorithms
     * 
     * @return Array of supported algorithm names
     */
    public static String[] getSupportedAlgorithms() {
        return SUPPORTED_ALGORITHMS.clone();
    }
    
    /**
     * Checks if an algorithm is supported
     * 
     * @param algorithm The algorithm name
     * @return true if supported
     */
    public static boolean isSupported(String algorithm) {
        try {
            validateAlgorithm(algorithm);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // ============================================================
    // HASHING METHODS - STRING
    // ============================================================
    
    /**
     * Hashes a string using the current algorithm
     * 
     * @param input The input string
     * @return The hash as a hex string
     */
    public String hashString(String input) {
        if (input == null) {
            return null;
        }
        return hashBytes(input.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Hashes a string using the specified algorithm
     * 
     * @param input The input string
     * @param algorithm The hash algorithm to use
     * @return The hash as a hex string
     */
    public static String hashString(String input, String algorithm) {
        if (input == null) {
            return null;
        }
        HashCalculator calculator = new HashCalculator(algorithm);
        return calculator.hashString(input);
    }
    
    /**
     * Hashes bytes using the current algorithm
     * 
     * @param data The input bytes
     * @return The hash as a hex string
     */
    public String hashBytes(byte[] data) {
        if (data == null || data.length == 0) {
            return getEmptyHash();
        }
        
        try {
            if (algorithm.startsWith("CRC")) {
                return hashCRC(data);
            } else {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                byte[] hashBytes = digest.digest(data);
                return bytesToHex(hashBytes);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not found: " + algorithm, e);
        }
    }
    
    /**
     * Hashes bytes using the specified algorithm
     * 
     * @param data The input bytes
     * @param algorithm The hash algorithm to use
     * @return The hash as a hex string
     */
    public static String hashBytes(byte[] data, String algorithm) {
        HashCalculator calculator = new HashCalculator(algorithm);
        return calculator.hashBytes(data);
    }
    
    // ============================================================
    // HASHING METHODS - FILE
    // ============================================================
    
    /**
     * Hashes a file using the current algorithm
     * 
     * @param filePath The file path
     * @return The hash as a hex string
     */
    public String hashFile(String filePath) throws IOException {
        return hashFile(new File(filePath));
    }
    
    /**
     * Hashes a file using the current algorithm
     * 
     * @param file The file to hash
     * @return The hash as a hex string
     */
    public String hashFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("File not found: " + (file != null ? file.getPath() : "null"));
        }
        
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Cannot hash a directory: " + file.getPath());
        }
        
        if (file.length() == 0) {
            return getEmptyHash();
        }
        
        try {
            if (algorithm.startsWith("CRC")) {
                return hashFileCRC(file);
            } else {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                
                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis, bufferSize)) {
                    
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    long totalRead = 0;
                    long fileSize = file.length();
                    
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        digest.update(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                        
                        if (showProgress && totalRead % (bufferSize * 10) == 0) {
                            updateProgress(totalRead, fileSize, file.getName());
                        }
                    }
                }
                
                byte[] hashBytes = digest.digest();
                return bytesToHex(hashBytes);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not found: " + algorithm, e);
        }
    }
    
    /**
     * Hashes a file using the specified algorithm
     * 
     * @param file The file to hash
     * @param algorithm The hash algorithm to use
     * @return The hash as a hex string
     */
    public static String hashFile(File file, String algorithm) throws IOException {
        HashCalculator calculator = new HashCalculator(algorithm);
        return calculator.hashFile(file);
    }
    
    /**
     * Hashes a file and returns the result with metadata
     * 
     * @param file The file to hash
     * @return HashResult containing the hash and metadata
     */
    public HashResult hashFileWithMetadata(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        String hash = hashFile(file);
        long endTime = System.currentTimeMillis();
        
        return new HashResult(
            file.getPath(),
            file.getName(),
            file.length(),
            algorithm,
            hash,
            endTime - startTime,
            new Date()
        );
    }
    
    // ============================================================
    // BATCH HASHING
    // ============================================================
    
    /**
     * Hashes multiple files using the current algorithm
     * 
     * @param files The files to hash
     * @return Map of file paths to hash results
     */
    public Map<String, HashResult> hashFiles(List<File> files) {
        Map<String, HashResult> results = new LinkedHashMap<>();
        totalFiles.set(files.size());
        processedFiles.set(0);
        
        if (files.isEmpty()) {
            return results;
        }
        
        for (File file : files) {
            try {
                HashResult result = hashFileWithMetadata(file);
                results.put(file.getPath(), result);
                processedFiles.incrementAndGet();
                
                if (showProgress) {
                    updateBatchProgress(processedFiles.get(), totalFiles.get(), file.getName());
                }
            } catch (IOException e) {
                results.put(file.getPath(), HashResult.error(file.getPath(), e.getMessage()));
                processedFiles.incrementAndGet();
            }
        }
        
        return results;
    }
    
    /**
     * Hashes files in a directory recursively
     * 
     * @param directory The directory to scan
     * @param recursive Whether to scan subdirectories
     * @return Map of file paths to hash results
     */
    public Map<String, HashResult> hashDirectory(File directory, boolean recursive) throws IOException {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + (directory != null ? directory.getPath() : "null"));
        }
        
        List<File> files = new ArrayList<>();
        collectFiles(directory, files, recursive);
        
        return hashFiles(files);
    }
    
    /**
     * Collects all files in a directory
     * 
     * @param directory The directory to scan
     * @param files The list to add files to
     * @param recursive Whether to scan subdirectories
     */
    private void collectFiles(File directory, List<File> files, boolean recursive) {
        File[] fileList = directory.listFiles();
        if (fileList == null) {
            return;
        }
        
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (recursive) {
                    collectFiles(file, files, true);
                }
            } else {
                files.add(file);
            }
        }
    }
    
    // ============================================================
    // PARALLEL HASHING
    // ============================================================
    
    /**
     * Hashes multiple files in parallel
     * 
     * @param files The files to hash
     * @return Map of file paths to hash results
     */
    public Map<String, HashResult> hashFilesParallel(List<File> files) {
        Map<String, HashResult> results = Collections.synchronizedMap(new LinkedHashMap<>());
        totalFiles.set(files.size());
        processedFiles.set(0);
        
        if (files.isEmpty()) {
            return results;
        }
        
        List<Thread> threads = new ArrayList<>();
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), files.size());
        int filesPerThread = (int) Math.ceil((double) files.size() / threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            int start = i * filesPerThread;
            int end = Math.min(start + filesPerThread, files.size());
            
            List<File> subList = files.subList(start, end);
            Thread thread = new Thread(() -> {
                for (File file : subList) {
                    try {
                        HashResult result = hashFileWithMetadata(file);
                        results.put(file.getPath(), result);
                        processedFiles.incrementAndGet();
                        
                        if (showProgress) {
                            updateBatchProgress(processedFiles.get(), totalFiles.get(), file.getName());
                        }
                    } catch (IOException e) {
                        results.put(file.getPath(), HashResult.error(file.getPath(), e.getMessage()));
                        processedFiles.incrementAndGet();
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return results;
    }
    
    // ============================================================
    // CRC HASHING
    // ============================================================
    
    /**
     * Calculates CRC hash of byte data
     * 
     * @param data The input bytes
     * @return The CRC hash as a hex string
     */
    private String hashCRC(byte[] data) {
        Checksum checksum;
        if ("CRC32".equalsIgnoreCase(algorithm)) {
            checksum = new CRC32();
        } else if ("CRC32C".equalsIgnoreCase(algorithm)) {
            checksum = new CRC32C();
        } else {
            throw new UnsupportedOperationException("Unsupported CRC algorithm: " + algorithm);
        }
        
        checksum.update(data, 0, data.length);
        long value = checksum.getValue();
        return String.format("%08x", value);
    }
    
    /**
     * Calculates CRC hash of a file
     * 
     * @param file The file to hash
     * @return The CRC hash as a hex string
     */
    private String hashFileCRC(File file) throws IOException {
        Checksum checksum;
        if ("CRC32".equalsIgnoreCase(algorithm)) {
            checksum = new CRC32();
        } else if ("CRC32C".equalsIgnoreCase(algorithm)) {
            checksum = new CRC32C();
        } else {
            throw new UnsupportedOperationException("Unsupported CRC algorithm: " + algorithm);
        }
        
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis, bufferSize)) {
            
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            long totalRead = 0;
            long fileSize = file.length();
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                checksum.update(buffer, 0, bytesRead);
                totalRead += bytesRead;
                
                if (showProgress && totalRead % (bufferSize * 10) == 0) {
                    updateProgress(totalRead, fileSize, file.getName());
                }
            }
        }
        
        long value = checksum.getValue();
        return String.format("%08x", value);
    }
    
    // ============================================================
    // HMAC HASHING
    // ============================================================
    
    /**
     * Calculates HMAC of a string
     * 
     * @param input The input string
     * @param key The HMAC key
     * @param algorithm The HMAC algorithm
     * @return The HMAC as a hex string
     */
    public static String hmacString(String input, String key, String algorithm) {
        if (input == null || key == null) {
            return null;
        }
        
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                algorithm
            );
            
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            
            byte[] hmacBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calculates HMAC of a file
     * 
     * @param file The file to hash
     * @param key The HMAC key
     * @param algorithm The HMAC algorithm
     * @return The HMAC as a hex string
     */
    public static String hmacFile(File file, String key, String algorithm) throws IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("File not found: " + (file != null ? file.getPath() : "null"));
        }
        
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                algorithm
            );
            
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead;
                
                while ((bytesRead = bis.read(buffer)) != -1) {
                    mac.update(buffer, 0, bytesRead);
                }
            }
            
            byte[] hmacBytes = mac.doFinal();
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed: " + e.getMessage(), e);
        }
    }
    
    // ============================================================
    // VERIFICATION
    // ============================================================
    
    /**
     * Verifies that a hash matches the expected hash
     * 
     * @param input The input string
     * @param expectedHash The expected hash
     * @return true if the hash matches
     */
    public boolean verifyString(String input, String expectedHash) {
        if (input == null || expectedHash == null) {
            return false;
        }
        String computedHash = hashString(input);
        return computedHash != null && computedHash.equalsIgnoreCase(expectedHash);
    }
    
    /**
     * Verifies that a file's hash matches the expected hash
     * 
     * @param file The file to verify
     * @param expectedHash The expected hash
     * @return true if the hash matches
     */
    public boolean verifyFile(File file, String expectedHash) throws IOException {
        if (file == null || !file.exists()) {
            return false;
        }
        if (expectedHash == null) {
            return false;
        }
        String computedHash = hashFile(file);
        return computedHash != null && computedHash.equalsIgnoreCase(expectedHash);
    }
    
    /**
     * Verifies a hash against a hash file (MD5SUM, SHA256SUM format)
     * 
     * @param file The file to verify
     * @param hashFile The hash file containing the expected hash
     * @return true if the hash matches
     */
    public boolean verifyFromHashFile(File file, File hashFile) throws IOException {
        if (!hashFile.exists()) {
            throw new FileNotFoundException("Hash file not found: " + hashFile.getPath());
        }
        
        String content = new String(Files.readAllBytes(hashFile.toPath()), StandardCharsets.UTF_8);
        
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // Parse hash file format: "hash  filename"
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                String hash = parts[0];
                String fileName = parts[1];
                
                // Check if this line matches our file
                if (file.getName().equals(fileName) || file.getPath().endsWith(fileName)) {
                    // Detect algorithm from hash length
                    String detectedAlgorithm = detectAlgorithmFromHash(hash);
                    if (detectedAlgorithm != null) {
                        HashCalculator calculator = new HashCalculator(detectedAlgorithm);
                        String computedHash = calculator.hashFile(file);
                        if (computedHash != null && computedHash.equalsIgnoreCase(hash)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    // ============================================================
    // UTILITY METHODS
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
     * Detects the algorithm from the hash length
     * 
     * @param hash The hash string
     * @return The detected algorithm, or null if unknown
     */
    public static String detectAlgorithmFromHash(String hash) {
        if (hash == null) {
            return null;
        }
        
        int length = hash.length();
        switch (length) {
            case 32:
                return "MD5";
            case 40:
                return "SHA-1";
            case 64:
                return "SHA-256";
            case 96:
                return "SHA-384";
            case 128:
                return "SHA-512";
            case 56:
                return "SHA3-224";
            case 64:
                return "SHA3-256";
            case 96:
                return "SHA3-384";
            case 128:
                return "SHA3-512";
            case 40:
                return "RIPEMD-160";
            case 64:
                return "RIPEMD-256";
            case 80:
                return "RIPEMD-320";
            case 128:
                return "Whirlpool";
            case 48:
                return "Tiger";
            case 56:
                return "GOST3411";
            case 64:
                return "SM3";
            case 8:
                return "CRC32";
            default:
                return null;
        }
    }
    
    /**
     * Gets the empty hash for the current algorithm
     * 
     * @return The hash of an empty input
     */
    public String getEmptyHash() {
        return hashBytes(new byte[0]);
    }
    
    /**
     * Gets the empty hash for a specified algorithm
     * 
     * @param algorithm The hash algorithm
     * @return The hash of an empty input
     */
    public static String getEmptyHash(String algorithm) {
        HashCalculator calculator = new HashCalculator(algorithm);
        return calculator.getEmptyHash();
    }
    
    // ============================================================
    // PROGRESS UPDATES
    // ============================================================
    
    /**
     * Updates progress for a single file hash
     * 
     * @param current The current bytes processed
     * @param total The total bytes
     * @param fileName The file name
     */
    private void updateProgress(long current, long total, String fileName) {
        if (!showProgress) {
            return;
        }
        int percent = (int) ((current * 100) / total);
        System.out.print("\rHashing " + fileName + ": " + percent + "% (" + 
                        formatSize(current) + "/" + formatSize(total) + ")");
        if (current >= total) {
            System.out.println();
        }
    }
    
    /**
     * Updates progress for batch hashing
     * 
     * @param processed The number of processed files
     * @param total The total number of files
     * @param fileName The current file name
     */
    private void updateBatchProgress(int processed, int total, String fileName) {
        if (!showProgress) {
            return;
        }
        int percent = (processed * 100) / total;
        System.out.print("\rProcessing " + processed + "/" + total + " files (" + 
                        percent + "%) - Current: " + fileName + "                    ");
        if (processed >= total) {
            System.out.println();
        }
    }
    
    /**
     * Formats a size in bytes to a human-readable string
     * 
     * @param bytes The size in bytes
     * @return Formatted string
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = validateAlgorithm(algorithm);
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void setBufferSize(int bufferSize) {
        if (bufferSize < 1024) {
            throw new IllegalArgumentException("Buffer size must be at least 1024 bytes");
        }
        this.bufferSize = bufferSize;
    }
    
    public boolean isShowProgress() {
        return showProgress;
    }
    
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
    
    public String getHmacKey() {
        return hmacKey;
    }
    
    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * HashResult - Contains the result of a hash operation
     */
    public static class HashResult {
        private final String filePath;
        private final String fileName;
        private final long fileSize;
        private final String algorithm;
        private final String hash;
        private final long timeMs;
        private final Date timestamp;
        private final boolean success;
        private final String errorMessage;
        
        public HashResult(String filePath, String fileName, long fileSize, 
                         String algorithm, String hash, long timeMs, Date timestamp) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.algorithm = algorithm;
            this.hash = hash;
            this.timeMs = timeMs;
            this.timestamp = timestamp;
            this.success = true;
            this.errorMessage = null;
        }
        
        private HashResult(String filePath, String fileName, String errorMessage) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.fileSize = -1;
            this.algorithm = null;
            this.hash = null;
            this.timeMs = 0;
            this.timestamp = new Date();
            this.success = false;
            this.errorMessage = errorMessage;
        }
        
        public static HashResult error(String filePath, String errorMessage) {
            String fileName = filePath != null ? new File(filePath).getName() : "unknown";
            return new HashResult(filePath, fileName, errorMessage);
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public String getAlgorithm() {
            return algorithm;
        }
        
        public String getHash() {
            return hash;
        }
        
        public long getTimeMs() {
            return timeMs;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @Override
        public String toString() {
            if (!success) {
                return "❌ " + fileName + " - Error: " + errorMessage;
            }
            return "✅ " + fileName + " [" + algorithm + "] " + hash + 
                   " (" + formatSize(fileSize) + ", " + timeMs + "ms)";
        }
        
        private String formatSize(long bytes) {
            if (bytes < 1024) {
                return bytes + " B";
            }
            if (bytes < 1024 * 1024) {
                return String.format("%.2f KB", bytes / 1024.0);
            }
            if (bytes < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            }
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
        
        public String toDetailedString() {
            if (!success) {
                return "❌ HASH ERROR\n" +
                       "File: " + fileName + "\n" +
                       "Path: " + filePath + "\n" +
                       "Error: " + errorMessage;
            }
            
            return "════════════════════════════════════════════════════════\n" +
                   "📊 HASH RESULT\n" +
                   "════════════════════════════════════════════════════════\n" +
                   "File:        " + fileName + "\n" +
                   "Path:        " + filePath + "\n" +
                   "Size:        " + formatSize(fileSize) + "\n" +
                   "Algorithm:   " + algorithm + "\n" +
                   "Hash:        " + hash + "\n" +
                   "Time:        " + timeMs + " ms\n" +
                   "Timestamp:   " + timestamp + "\n" +
                   "════════════════════════════════════════════════════════";
        }
    }
    
    // ============================================================
    // CLEANUP
    // ============================================================
    
    /**
     * Shuts down the executor service
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}