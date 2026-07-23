package com.neilos.utils;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.zip.*;

/**
 * FileUtils - Comprehensive file and I/O utility class for NeilOS
 * Provides extensive file operations, directory management, compression,
 * and file system utilities.
 * 
 * Features:
 * - File read/write operations (text, binary)
 * - Directory operations (create, delete, copy, move)
 * - File search and filtering
 * - File size formatting
 * - File permissions management
 * - File hash calculation (MD5, SHA-1, SHA-256)
 * - File compression (ZIP, GZIP)
 * - File locking
 * - File watching
 * - Temporary file management
 * - File type detection
 * - Path manipulation
 * - File backup/restore
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class FileUtils {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default buffer size for file operations */
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    /** File size units */
    public static final long KB = 1024;
    public static final long MB = KB * 1024;
    public static final long GB = MB * 1024;
    public static final long TB = GB * 1024;
    
    /** Common file extensions */
    public static final String EXT_TEXT = ".txt";
    public static final String EXT_JSON = ".json";
    public static final String EXT_XML = ".xml";
    public static final String EXT_CSV = ".csv";
    public static final String EXT_PROPERTIES = ".properties";
    public static final String EXT_JAVA = ".java";
    public static final String EXT_CLASS = ".class";
    public static final String EXT_JAR = ".jar";
    public static final String EXT_ZIP = ".zip";
    public static final String EXT_GZIP = ".gz";
    public static final String EXT_PDF = ".pdf";
    public static final String EXT_IMAGE = ".png";
    public static final String EXT_IMAGE_JPG = ".jpg";
    public static final String EXT_IMAGE_JPEG = ".jpeg";
    public static final String EXT_IMAGE_GIF = ".gif";
    public static final String EXT_IMAGE_BMP = ".bmp";
    public static final String EXT_IMAGE_SVG = ".svg";
    public static final String EXT_AUDIO = ".mp3";
    public static final String EXT_AUDIO_WAV = ".wav";
    public static final String EXT_VIDEO = ".mp4";
    public static final String EXT_VIDEO_AVI = ".avi";
    public static final String EXT_VIDEO_MKV = ".mkv";
    public static final String EXT_EXECUTABLE = ".exe";
    public static final String EXT_SCRIPT_BAT = ".bat";
    public static final String EXT_SCRIPT_SH = ".sh";
    public static final String EXT_SCRIPT_PS1 = ".ps1";
    public static final String EXT_PYTHON = ".py";
    public static final String EXT_HTML = ".html";
    public static final String EXT_CSS = ".css";
    public static final String EXT_JS = ".js";
    public static final String EXT_JSONP = ".jsonp";
    
    // ============================================================
    // FILE READ OPERATIONS
    // ============================================================
    
    /**
     * Reads a file as a String using UTF-8 encoding
     * 
     * @param filePath The file path
     * @return File content as String
     * @throws IOException If an I/O error occurs
     */
    public static String readFile(String filePath) throws IOException {
        return readFile(new File(filePath), StandardCharsets.UTF_8);
    }
    
    /**
     * Reads a file as a String using the specified encoding
     * 
     * @param filePath The file path
     * @param charset The character encoding
     * @return File content as String
     * @throws IOException If an I/O error occurs
     */
    public static String readFile(String filePath, Charset charset) throws IOException {
        return readFile(new File(filePath), charset);
    }
    
    /**
     * Reads a file as a String using UTF-8 encoding
     * 
     * @param file The file
     * @return File content as String
     * @throws IOException If an I/O error occurs
     */
    public static String readFile(File file) throws IOException {
        return readFile(file, StandardCharsets.UTF_8);
    }
    
    /**
     * Reads a file as a String using the specified encoding
     * 
     * @param file The file
     * @param charset The character encoding
     * @return File content as String
     * @throws IOException If an I/O error occurs
     */
    public static String readFile(File file, Charset charset) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset))) {
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, bytesRead);
            }
            return content.toString();
        }
    }
    
    /**
     * Reads a file line by line
     * 
     * @param file The file
     * @param lineConsumer Consumer for each line
     * @throws IOException If an I/O error occurs
     */
    public static void readFileLines(File file, Consumer<String> lineConsumer) throws IOException {
        readFileLines(file, StandardCharsets.UTF_8, lineConsumer);
    }
    
    /**
     * Reads a file line by line with specified encoding
     * 
     * @param file The file
     * @param charset The character encoding
     * @param lineConsumer Consumer for each line
     * @throws IOException If an I/O error occurs
     */
    public static void readFileLines(File file, Charset charset, Consumer<String> lineConsumer) 
            throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line);
            }
        }
    }
    
    /**
     * Reads a file as a byte array
     * 
     * @param file The file
     * @return Byte array of file content
     * @throws IOException If an I/O error occurs
     */
    public static byte[] readBytes(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }
    
    // ============================================================
    // FILE WRITE OPERATIONS
    // ============================================================
    
    /**
     * Writes a String to a file using UTF-8 encoding
     * 
     * @param filePath The file path
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    public static void writeFile(String filePath, String content) throws IOException {
        writeFile(new File(filePath), content, StandardCharsets.UTF_8, false);
    }
    
    /**
     * Writes a String to a file using the specified encoding
     * 
     * @param filePath The file path
     * @param content The content to write
     * @param charset The character encoding
     * @throws IOException If an I/O error occurs
     */
    public static void writeFile(String filePath, String content, Charset charset) throws IOException {
        writeFile(new File(filePath), content, charset, false);
    }
    
    /**
     * Writes a String to a file
     * 
     * @param file The file
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, content, StandardCharsets.UTF_8, false);
    }
    
    /**
     * Writes a String to a file
     * 
     * @param file The file
     * @param content The content to write
     * @param charset The character encoding
     * @param append Whether to append to the file
     * @throws IOException If an I/O error occurs
     */
    public static void writeFile(File file, String content, Charset charset, boolean append) 
            throws IOException {
        createParentDirectories(file);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset))) {
            writer.write(content);
        }
    }
    
    /**
     * Writes lines to a file
     * 
     * @param file The file
     * @param lines The lines to write
     * @throws IOException If an I/O error occurs
     */
    public static void writeLines(File file, List<String> lines) throws IOException {
        writeLines(file, lines, StandardCharsets.UTF_8, false);
    }
    
    /**
     * Writes lines to a file
     * 
     * @param file The file
     * @param lines The lines to write
     * @param charset The character encoding
     * @param append Whether to append to the file
     * @throws IOException If an I/O error occurs
     */
    public static void writeLines(File file, List<String> lines, Charset charset, boolean append) 
            throws IOException {
        createParentDirectories(file);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    /**
     * Writes bytes to a file
     * 
     * @param file The file
     * @param data The byte data to write
     * @throws IOException If an I/O error occurs
     */
    public static void writeBytes(File file, byte[] data) throws IOException {
        createParentDirectories(file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }
    
    /**
     * Appends content to a file
     * 
     * @param file The file
     * @param content The content to append
     * @throws IOException If an I/O error occurs
     */
    public static void appendFile(File file, String content) throws IOException {
        writeFile(file, content, StandardCharsets.UTF_8, true);
    }
    
    /**
     * Appends content to a file with specified encoding
     * 
     * @param file The file
     * @param content The content to append
     * @param charset The character encoding
     * @throws IOException If an I/O error occurs
     */
    public static void appendFile(File file, String content, Charset charset) throws IOException {
        writeFile(file, content, charset, true);
    }
    
    // ============================================================
    // DIRECTORY OPERATIONS
    // ============================================================
    
    /**
     * Creates a directory (including parent directories)
     * 
     * @param dirPath The directory path
     * @return true if directory was created or already exists
     */
    public static boolean createDirectory(String dirPath) {
        return createDirectory(new File(dirPath));
    }
    
    /**
     * Creates a directory (including parent directories)
     * 
     * @param dir The directory
     * @return true if directory was created or already exists
     */
    public static boolean createDirectory(File dir) {
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }
    
    /**
     * Creates parent directories for a file
     * 
     * @param file The file
     * @throws IOException If directories cannot be created
     */
    public static void createParentDirectories(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create parent directories: " + parent.getPath());
            }
        }
    }
    
    /**
     * Deletes a directory and all its contents
     * 
     * @param dir The directory to delete
     * @return true if deletion was successful
     */
    public static boolean deleteDirectory(File dir) {
        if (!dir.exists()) {
            return true;
        }
        
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (!deleteDirectory(file)) {
                            return false;
                        }
                    } else {
                        if (!file.delete()) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return dir.delete();
    }
    
    /**
     * Deletes a directory and all its contents (using NIO)
     * 
     * @param dirPath The directory path
     * @throws IOException If deletion fails
     */
    public static void deleteDirectoryNIO(String dirPath) throws IOException {
        deleteDirectoryNIO(Paths.get(dirPath));
    }
    
    /**
     * Deletes a directory and all its contents (using NIO)
     * 
     * @param dir The directory path
     * @throws IOException If deletion fails
     */
    public static void deleteDirectoryNIO(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                 .sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         // Continue with other files
                     }
                 });
        }
    }
    
    /**
     * Copies a directory
     * 
     * @param src The source directory
     * @param dest The destination directory
     * @throws IOException If copy fails
     */
    public static void copyDirectory(File src, File dest) throws IOException {
        copyDirectory(src, dest, true);
    }
    
    /**
     * Copies a directory
     * 
     * @param src The source directory
     * @param dest The destination directory
     * @param overwrite Whether to overwrite existing files
     * @throws IOException If copy fails
     */
    public static void copyDirectory(File src, File dest, boolean overwrite) throws IOException {
        if (!src.exists()) {
            throw new FileNotFoundException("Source directory not found: " + src.getPath());
        }
        if (!src.isDirectory()) {
            throw new IOException("Source is not a directory: " + src.getPath());
        }
        
        if (!dest.exists()) {
            dest.mkdirs();
        }
        
        File[] files = src.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(dest, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile, overwrite);
                } else {
                    copyFile(file, destFile, overwrite);
                }
            }
        }
    }
    
    /**
     * Copies a file
     * 
     * @param src The source file
     * @param dest The destination file
     * @throws IOException If copy fails
     */
    public static void copyFile(File src, File dest) throws IOException {
        copyFile(src, dest, true);
    }
    
    /**
     * Copies a file
     * 
     * @param src The source file
     * @param dest The destination file
     * @param overwrite Whether to overwrite existing files
     * @throws IOException If copy fails
     */
    public static void copyFile(File src, File dest, boolean overwrite) throws IOException {
        if (!src.exists()) {
            throw new FileNotFoundException("Source file not found: " + src.getPath());
        }
        if (src.isDirectory()) {
            throw new IOException("Source is a directory: " + src.getPath());
        }
        
        if (dest.exists()) {
            if (!overwrite) {
                throw new IOException("Destination file already exists: " + dest.getPath());
            }
            if (dest.isDirectory()) {
                dest = new File(dest, src.getName());
            }
        }
        
        createParentDirectories(dest);
        
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    /**
     * Moves a file
     * 
     * @param src The source file
     * @param dest The destination file
     * @throws IOException If move fails
     */
    public static void moveFile(File src, File dest) throws IOException {
        copyFile(src, dest);
        if (!src.delete()) {
            throw new IOException("Failed to delete source file: " + src.getPath());
        }
    }
    
    /**
     * Moves a directory
     * 
     * @param src The source directory
     * @param dest The destination directory
     * @throws IOException If move fails
     */
    public static void moveDirectory(File src, File dest) throws IOException {
        copyDirectory(src, dest);
        if (!deleteDirectory(src)) {
            throw new IOException("Failed to delete source directory: " + src.getPath());
        }
    }
    
    // ============================================================
    // FILE SEARCH
    // ============================================================
    
    /**
     * Searches for files in a directory
     * 
     * @param dir The directory to search
     * @param pattern The filename pattern (glob)
     * @param recursive Whether to search subdirectories
     * @return List of matching files
     */
    public static List<File> findFiles(File dir, String pattern, boolean recursive) {
        List<File> results = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return results;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return results;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursive) {
                    results.addAll(findFiles(file, pattern, true));
                }
            } else {
                if (file.getName().matches(pattern.replace("*", ".*").replace("?", "."))) {
                    results.add(file);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Searches for files by extension
     * 
     * @param dir The directory to search
     * @param extension The file extension (e.g., ".txt")
     * @param recursive Whether to search subdirectories
     * @return List of matching files
     */
    public static List<File> findFilesByExtension(File dir, String extension, boolean recursive) {
        List<File> results = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return results;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return results;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursive) {
                    results.addAll(findFilesByExtension(file, extension, true));
                }
            } else {
                if (file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                    results.add(file);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Searches for files containing text
     * 
     * @param dir The directory to search
     * @param searchText The text to search for
     * @param recursive Whether to search subdirectories
     * @return List of files containing the text
     * @throws IOException If an I/O error occurs
     */
    public static List<File> findFilesContainingText(File dir, String searchText, boolean recursive) 
            throws IOException {
        List<File> results = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return results;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return results;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursive) {
                    results.addAll(findFilesContainingText(file, searchText, true));
                }
            } else {
                if (fileContainsText(file, searchText)) {
                    results.add(file);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Checks if a file contains specific text
     * 
     * @param file The file to check
     * @param searchText The text to search for
     * @return true if the file contains the text
     * @throws IOException If an I/O error occurs
     */
    public static boolean fileContainsText(File file, String searchText) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchText)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File may be binary, skip
        }
        return false;
    }
    
    // ============================================================
    // FILE SIZE FORMATTING
    // ============================================================
    
    /**
     * Formats a file size in a human-readable format
     * 
     * @param bytes The file size in bytes
     * @return Formatted file size (e.g., "2.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 0) return "0 B";
        if (bytes < KB) return bytes + " B";
        if (bytes < MB) return String.format("%.2f KB", bytes / (double) KB);
        if (bytes < GB) return String.format("%.2f MB", bytes / (double) MB);
        if (bytes < TB) return String.format("%.2f GB", bytes / (double) GB);
        return String.format("%.2f TB", bytes / (double) TB);
    }
    
    /**
     * Gets the size of a file or directory
     * 
     * @param file The file or directory
     * @return Size in bytes
     */
    public static long getSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        
        if (file.isDirectory()) {
            long size = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    size += getSize(f);
                }
            }
            return size;
        }
        
        return file.length();
    }
    
    /**
     * Gets the size of a file or directory with a progress callback
     * 
     * @param file The file or directory
     * @param callback Callback for progress updates
     * @return Size in bytes
     */
    public static long getSizeWithProgress(File file, Consumer<Long> callback) {
        if (!file.exists()) {
            return 0;
        }
        
        AtomicLong totalSize = new AtomicLong(0);
        AtomicLong processedFiles = new AtomicLong(0);
        
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    totalSize.addAndGet(getSizeWithProgress(f, callback));
                    processedFiles.incrementAndGet();
                    if (callback != null) {
                        callback.accept(totalSize.get());
                    }
                }
            }
        } else {
            totalSize.addAndGet(file.length());
            if (callback != null) {
                callback.accept(totalSize.get());
            }
        }
        
        return totalSize.get();
    }
    
    // ============================================================
    // FILE HASH CALCULATION
    // ============================================================
    
    /**
     * Calculates the MD5 hash of a file
     * 
     * @param file The file
     * @return MD5 hash as hex string
     * @throws Exception If hash calculation fails
     */
    public static String md5(File file) throws Exception {
        return hash(file, "MD5");
    }
    
    /**
     * Calculates the SHA-1 hash of a file
     * 
     * @param file The file
     * @return SHA-1 hash as hex string
     * @throws Exception If hash calculation fails
     */
    public static String sha1(File file) throws Exception {
        return hash(file, "SHA-1");
    }
    
    /**
     * Calculates the SHA-256 hash of a file
     * 
     * @param file The file
     * @return SHA-256 hash as hex string
     * @throws Exception If hash calculation fails
     */
    public static String sha256(File file) throws Exception {
        return hash(file, "SHA-256");
    }
    
    /**
     * Calculates the hash of a file using the specified algorithm
     * 
     * @param file The file
     * @param algorithm The hash algorithm
     * @return Hash as hex string
     * @throws Exception If hash calculation fails
     */
    public static String hash(File file, String algorithm) throws Exception {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // ============================================================
    // FILE COMPRESSION
    // ============================================================
    
    /**
     * Creates a ZIP file from a directory
     * 
     * @param sourceDir The source directory
     * @param zipFile The output ZIP file
     * @throws IOException If compression fails
     */
    public static void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipDirectory(sourceDir, sourceDir, zos);
        }
    }
    
    /**
     * Recursively adds files to a ZIP archive
     */
    private static void zipDirectory(File rootDir, File dir, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(rootDir, file, zos);
            } else {
                String relativePath = rootDir.toURI().relativize(file.toURI()).getPath();
                ZipEntry entry = new ZipEntry(relativePath);
                zos.putNextEntry(entry);
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }
                
                zos.closeEntry();
            }
        }
    }
    
    /**
     * Extracts a ZIP file to a directory
     * 
     * @param zipFile The ZIP file
     * @param destDir The destination directory
     * @throws IOException If extraction fails
     */
    public static void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    createParentDirectories(file);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
    
    /**
     * Compresses a file using GZIP
     * 
     * @param inputFile The input file
     * @param outputFile The output GZIP file
     * @throws IOException If compression fails
     */
    public static void gzip(File inputFile, File outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    /**
     * Decompresses a GZIP file
     * 
     * @param inputFile The GZIP file
     * @param outputFile The output file
     * @throws IOException If decompression fails
     */
    public static void gunzip(File inputFile, File outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gzis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    // ============================================================
    // FILE PERMISSIONS
    // ============================================================
    
    /**
     * Sets file permissions (POSIX)
     * 
     * @param file The file
     * @param permissions The permissions (e.g., "rwxr-xr-x")
     * @throws IOException If setting permissions fails
     */
    public static void setPermissions(File file, String permissions) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        Path path = file.toPath();
        Set<PosixFilePermission> perms = new HashSet<>();
        
        // Owner permissions
        if (permissions.length() > 0 && permissions.charAt(0) == 'r') perms.add(PosixFilePermission.OWNER_READ);
        if (permissions.length() > 1 && permissions.charAt(1) == 'w') perms.add(PosixFilePermission.OWNER_WRITE);
        if (permissions.length() > 2 && permissions.charAt(2) == 'x') perms.add(PosixFilePermission.OWNER_EXECUTE);
        
        // Group permissions
        if (permissions.length() > 3 && permissions.charAt(3) == 'r') perms.add(PosixFilePermission.GROUP_READ);
        if (permissions.length() > 4 && permissions.charAt(4) == 'w') perms.add(PosixFilePermission.GROUP_WRITE);
        if (permissions.length() > 5 && permissions.charAt(5) == 'x') perms.add(PosixFilePermission.GROUP_EXECUTE);
        
        // Others permissions
        if (permissions.length() > 6 && permissions.charAt(6) == 'r') perms.add(PosixFilePermission.OTHERS_READ);
        if (permissions.length() > 7 && permissions.charAt(7) == 'w') perms.add(PosixFilePermission.OTHERS_WRITE);
        if (permissions.length() > 8 && permissions.charAt(8) == 'x') perms.add(PosixFilePermission.OTHERS_EXECUTE);
        
        Files.setPosixFilePermissions(path, perms);
    }
    
    /**
     * Gets file permissions as a string (POSIX)
     * 
     * @param file The file
     * @return Permission string (e.g., "rwxr-xr-x")
     * @throws IOException If getting permissions fails
     */
    public static String getPermissions(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        Path path = file.toPath();
        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
        
        char[] permChars = new char[9];
        permChars[0] = perms.contains(PosixFilePermission.OWNER_READ) ? 'r' : '-';
        permChars[1] = perms.contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-';
        permChars[2] = perms.contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-';
        permChars[3] = perms.contains(PosixFilePermission.GROUP_READ) ? 'r' : '-';
        permChars[4] = perms.contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-';
        permChars[5] = perms.contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-';
        permChars[6] = perms.contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-';
        permChars[7] = perms.contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-';
        permChars[8] = perms.contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-';
        
        return new String(permChars);
    }
    
    // ============================================================
    // FILE TYPE DETECTION
    // ============================================================
    
    /**
     * Gets the file extension
     * 
     * @param file The file
     * @return The file extension (e.g., ".txt"), or empty string if none
     */
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot) : "";
    }
    
    /**
     * Gets the file extension without the dot
     * 
     * @param file The file
     * @return The file extension (e.g., "txt"), or empty string if none
     */
    public static String getExtensionWithoutDot(File file) {
        String ext = getExtension(file);
        return ext.isEmpty() ? "" : ext.substring(1);
    }
    
    /**
     * Gets the file name without extension
     * 
     * @param file The file
     * @return The file name without extension
     */
    public static String getBaseName(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }
    
    /**
     * Detects the MIME type of a file
     * 
     * @param file The file
     * @return The MIME type, or "application/octet-stream" if unknown
     * @throws IOException If detection fails
     */
    public static String getMimeType(File file) throws IOException {
        if (!file.exists()) {
            return "application/octet-stream";
        }
        
        // Use Files.probeContentType if available
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType != null) {
            return mimeType;
        }
        
        // Fallback to extension-based detection
        String ext = getExtension(file).toLowerCase();
        switch (ext) {
            case ".txt": return "text/plain";
            case ".html": return "text/html";
            case ".htm": return "text/html";
            case ".xml": return "text/xml";
            case ".css": return "text/css";
            case ".js": return "application/javascript";
            case ".json": return "application/json";
            case ".csv": return "text/csv";
            case ".pdf": return "application/pdf";
            case ".png": return "image/png";
            case ".jpg":
            case ".jpeg": return "image/jpeg";
            case ".gif": return "image/gif";
            case ".bmp": return "image/bmp";
            case ".svg": return "image/svg+xml";
            case ".mp3": return "audio/mpeg";
            case ".wav": return "audio/wav";
            case ".mp4": return "video/mp4";
            case ".avi": return "video/x-msvideo";
            case ".mkv": return "video/x-matroska";
            case ".zip": return "application/zip";
            case ".gz": return "application/gzip";
            case ".jar": return "application/java-archive";
            case ".exe": return "application/x-msdownload";
            case ".sh": return "application/x-sh";
            case ".py": return "text/x-python";
            case ".java": return "text/x-java";
            case ".class": return "application/java-vm";
            default: return "application/octet-stream";
        }
    }
    
    // ============================================================
    // TEMPORARY FILE MANAGEMENT
    // ============================================================
    
    /**
     * Creates a temporary file with a prefix and suffix
     * 
     * @param prefix The file prefix
     * @param suffix The file suffix (e.g., ".txt")
     * @return The temporary file
     * @throws IOException If creation fails
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }
    
    /**
     * Creates a temporary directory
     * 
     * @param prefix The directory prefix
     * @return The temporary directory
     * @throws IOException If creation fails
     */
    public static File createTempDirectory(String prefix) throws IOException {
        File tempDir = File.createTempFile(prefix, "");
        tempDir.delete();
        tempDir.mkdirs();
        return tempDir;
    }
    
    /**
     * Creates a temporary file with content
     * 
     * @param prefix The file prefix
     * @param suffix The file suffix
     * @param content The content to write
     * @return The temporary file
     * @throws IOException If creation or writing fails
     */
    public static File createTempFileWithContent(String prefix, String suffix, String content) 
            throws IOException {
        File tempFile = createTempFile(prefix, suffix);
        writeFile(tempFile, content);
        return tempFile;
    }
    
    /**
     * Deletes a temporary file when the JVM exits
     * 
     * @param file The file to delete on exit
     */
    public static void deleteOnExit(File file) {
        if (file.exists()) {
            file.deleteOnExit();
        }
    }
    
    // ============================================================
    // PATH UTILITIES
    // ============================================================
    
    /**
     * Gets the canonical path of a file
     * 
     * @param file The file
     * @return The canonical path
     * @throws IOException If getting canonical path fails
     */
    public static String getCanonicalPath(File file) throws IOException {
        return file.getCanonicalPath();
    }
    
    /**
     * Checks if a path is absolute
     * 
     * @param path The path
     * @return true if the path is absolute
     */
    public static boolean isAbsolute(String path) {
        return Paths.get(path).isAbsolute();
    }
    
    /**
     * Normalizes a path
     * 
     * @param path The path to normalize
     * @return The normalized path
     */
    public static String normalizePath(String path) {
        return Paths.get(path).normalize().toString();
    }
    
    /**
     * Joins path components
     * 
     * @param base The base path
     * @param parts The path parts to join
     * @return The joined path
     */
    public static String joinPath(String base, String... parts) {
        Path path = Paths.get(base);
        for (String part : parts) {
            path = path.resolve(part);
        }
        return path.toString();
    }
    
    /**
     * Gets the relative path from one file to another
     * 
     * @param from The source path
     * @param to The target path
     * @return The relative path
     */
    public static String getRelativePath(File from, File to) {
        try {
            Path fromPath = from.getCanonicalFile().toPath();
            Path toPath = to.getCanonicalFile().toPath();
            return fromPath.relativize(toPath).toString();
        } catch (IOException e) {
            return to.getPath();
        }
    }
    
    // ============================================================
    // FILE LOCKING
    // ============================================================
    
    /**
     * Locks a file
     * 
     * @param file The file to lock
     * @return The file lock, or null if locking fails
     * @throws IOException If locking fails
     */
    public static FileLock lockFile(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        return raf.getChannel().tryLock();
    }
    
    /**
     * Locks a file (blocking)
     * 
     * @param file The file to lock
     * @param timeout The timeout in milliseconds
     * @return The file lock, or null if timeout expires
     * @throws IOException If locking fails
     * @throws InterruptedException If interrupted
     */
    public static FileLock lockFile(File file, long timeout) throws IOException, InterruptedException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        long start = System.currentTimeMillis();
        FileLock lock = null;
        while (lock == null && (System.currentTimeMillis() - start) < timeout) {
            lock = raf.getChannel().tryLock();
            if (lock == null) {
                Thread.sleep(100);
            }
        }
        return lock;
    }
    
    /**
     * Unlocks a file
     * 
     * @param lock The file lock
     * @throws IOException If unlocking fails
     */
    public static void unlockFile(FileLock lock) throws IOException {
        if (lock != null && lock.isValid()) {
            lock.release();
        }
    }
    
    // ============================================================
    // FILE BACKUP
    // ============================================================
    
    /**
     * Creates a backup of a file
     * 
     * @param file The file to backup
     * @return The backup file
     * @throws IOException If backup fails
     */
    public static File backupFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        String backupName = file.getName() + ".backup";
        File backupFile = new File(file.getParent(), backupName);
        copyFile(file, backupFile);
        return backupFile;
    }
    
    /**
     * Creates a timestamped backup of a file
     * 
     * @param file The file to backup
     * @return The backup file
     * @throws IOException If backup fails
     */
    public static File backupFileWithTimestamp(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getPath());
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupName = file.getName() + "." + timestamp + ".backup";
        File backupFile = new File(file.getParent(), backupName);
        copyFile(file, backupFile);
        return backupFile;
    }
    
    /**
     * Restores a file from backup
     * 
     * @param backupFile The backup file
     * @param destFile The destination file
     * @throws IOException If restore fails
     */
    public static void restoreFromBackup(File backupFile, File destFile) throws IOException {
        if (!backupFile.exists()) {
            throw new FileNotFoundException("Backup file not found: " + backupFile.getPath());
        }
        copyFile(backupFile, destFile);
    }
    
    // ============================================================
    // FILE SYSTEM UTILITIES
    // ============================================================
    
    /**
     * Gets the free space on the file system
     * 
     * @param file Any file on the file system
     * @return Free space in bytes
     */
    public static long getFreeSpace(File file) {
        return file.getFreeSpace();
    }
    
    /**
     * Gets the total space on the file system
     * 
     * @param file Any file on the file system
     * @return Total space in bytes
     */
    public static long getTotalSpace(File file) {
        return file.getTotalSpace();
    }
    
    /**
     * Gets the usable space on the file system
     * 
     * @param file Any file on the file system
     * @return Usable space in bytes
     */
    public static long getUsableSpace(File file) {
        return file.getUsableSpace();
    }
    
    /**
     * Checks if the file system is readable
     * 
     * @param file Any file on the file system
     * @return true if readable
     */
    public static boolean isFileSystemReadable(File file) {
        return file.canRead();
    }
    
    /**
     * Checks if the file system is writable
     * 
     * @param file Any file on the file system
     * @return true if writable
     */
    public static boolean isFileSystemWritable(File file) {
        return file.canWrite();
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of FileUtils
     */
    public static void main(String[] args) {
        try {
            System.out.println("📁 FileUtils Demo");
            System.out.println("═".repeat(60));
            
            // Create temporary file
            System.out.println("\n📌 Creating temporary file:");
            File tempFile = createTempFile("neilos_test", ".txt");
            System.out.println("  Temp file: " + tempFile.getAbsolutePath());
            
            // Write to file
            System.out.println("\n📌 Writing to file:");
            String content = "This is a test file for FileUtils\nLine 2\nLine 3";
            writeFile(tempFile, content);
            System.out.println("  Wrote " + content.length() + " characters");
            
            // Read from file
            System.out.println("\n📌 Reading from file:");
            String readContent = readFile(tempFile);
            System.out.println("  Read content:\n" + readContent);
            
            // File info
            System.out.println("\n📌 File Information:");
            System.out.println("  Name: " + tempFile.getName());
            System.out.println("  Size: " + formatFileSize(tempFile.length()));
            System.out.println("  Extension: " + getExtension(tempFile));
            System.out.println("  Base name: " + getBaseName(tempFile));
            System.out.println("  MIME type: " + getMimeType(tempFile));
            
            // Hash
            System.out.println("\n📌 File Hash:");
            System.out.println("  MD5: " + md5(tempFile));
            System.out.println("  SHA-1: " + sha1(tempFile));
            System.out.println("  SHA-256: " + sha256(tempFile));
            
            // Create temporary directory
            System.out.println("\n📌 Creating temporary directory:");
            File tempDir = createTempDirectory("neilos_test_dir");
            System.out.println("  Temp dir: " + tempDir.getAbsolutePath());
            
            // Copy file
            File copyFile = new File(tempDir, "copy.txt");
            System.out.println("\n📌 Copying file:");
            copyFile(tempFile, copyFile);
            System.out.println("  Copied to: " + copyFile.getAbsolutePath());
            
            // Find files
            System.out.println("\n📌 Finding files:");
            List<File> files = findFiles(tempDir, "*.txt", true);
            System.out.println("  Found " + files.size() + " text files");
            for (File f : files) {
                System.out.println("    " + f.getName());
            }
            
            // Format size
            System.out.println("\n📌 Size formatting:");
            System.out.println("  1024 bytes: " + formatFileSize(1024));
            System.out.println("  1048576 bytes: " + formatFileSize(1048576));
            System.out.println("  1073741824 bytes: " + formatFileSize(1073741824));
            
            // Delete temp files
            System.out.println("\n📌 Cleaning up:");
            deleteDirectory(tempDir);
            tempFile.delete();
            System.out.println("  Cleanup complete");
            
            System.out.println("\n✅ Demo completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}