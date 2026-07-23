package com.neilos.security;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * QuarantineManager - Manages quarantined files in NeilOS
 * Provides secure isolation of suspicious files, with restore,
 * delete, and reporting capabilities.
 * 
 * Features:
 * - Quarantine files with metadata
 * - Restore files to original location
 * - Permanent deletion
 * - Quarantine reporting
 * - Export/Import quarantine data
 * - Quarantine size management
 * - File integrity verification
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class QuarantineManager {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default quarantine directory */
    public static final String DEFAULT_QUARANTINE_DIR = "quarantine";
    
    /** Quarantine metadata file name */
    public static final String METADATA_FILE = "quarantine_metadata.json";
    
    /** Quarantine index file name */
    public static final String INDEX_FILE = "quarantine_index.txt";
    
    /** Maximum quarantine size in bytes (1GB) */
    public static final long MAX_QUARANTINE_SIZE = 1024 * 1024 * 1024;
    
    /** Maximum age of quarantined files in days (30 days) */
    public static final int MAX_AGE_DAYS = 30;
    
    /** Auto-clean threshold (80% of max size) */
    public static final double AUTO_CLEAN_THRESHOLD = 0.8;
    
    /** Quarantine file extension */
    public static final String QUARANTINE_EXTENSION = ".qrn";
    
    /** Metadata file extension */
    public static final String METADATA_EXTENSION = ".meta";
    
    /** Date format for metadata */
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * QuarantineEntry - Represents a quarantined file with metadata
     */
    public static class QuarantineEntry {
        private String id;
        private String originalPath;
        private String originalFileName;
        private String quarantinePath;
        private long fileSize;
        private String fileHash;
        private String threatName;
        private String threatType;
        private String detectedBy;
        private LocalDateTime quarantinedAt;
        private String quarantinedBy;
        private String restoredBy;
        private LocalDateTime restoredAt;
        private boolean isRestored;
        private int restoreCount;
        private String notes;
        private String severity;
        private List<String> scanResults;
        private Map<String, String> metadata;
        
        public QuarantineEntry() {
            this.id = UUID.randomUUID().toString();
            this.quarantinedAt = LocalDateTime.now();
            this.isRestored = false;
            this.restoreCount = 0;
            this.scanResults = new ArrayList<>();
            this.metadata = new HashMap<>();
            this.severity = "MEDIUM";
            this.quarantinedBy = System.getProperty("user.name");
        }
        
        public QuarantineEntry(String originalPath, String threatName) {
            this();
            this.originalPath = originalPath;
            this.originalFileName = new File(originalPath).getName();
            this.threatName = threatName;
            this.threatType = "UNKNOWN";
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getOriginalPath() { return originalPath; }
        public void setOriginalPath(String originalPath) { 
            this.originalPath = originalPath;
            this.originalFileName = new File(originalPath).getName();
        }
        
        public String getOriginalFileName() { return originalFileName; }
        public void setOriginalFileName(String originalFileName) { 
            this.originalFileName = originalFileName; 
        }
        
        public String getQuarantinePath() { return quarantinePath; }
        public void setQuarantinePath(String quarantinePath) { 
            this.quarantinePath = quarantinePath; 
        }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public String getFileHash() { return fileHash; }
        public void setFileHash(String fileHash) { this.fileHash = fileHash; }
        
        public String getThreatName() { return threatName; }
        public void setThreatName(String threatName) { this.threatName = threatName; }
        
        public String getThreatType() { return threatType; }
        public void setThreatType(String threatType) { this.threatType = threatType; }
        
        public String getDetectedBy() { return detectedBy; }
        public void setDetectedBy(String detectedBy) { this.detectedBy = detectedBy; }
        
        public LocalDateTime getQuarantinedAt() { return quarantinedAt; }
        public void setQuarantinedAt(LocalDateTime quarantinedAt) { 
            this.quarantinedAt = quarantinedAt; 
        }
        
        public String getQuarantinedBy() { return quarantinedBy; }
        public void setQuarantinedBy(String quarantinedBy) { 
            this.quarantinedBy = quarantinedBy; 
        }
        
        public String getRestoredBy() { return restoredBy; }
        public void setRestoredBy(String restoredBy) { this.restoredBy = restoredBy; }
        
        public LocalDateTime getRestoredAt() { return restoredAt; }
        public void setRestoredAt(LocalDateTime restoredAt) { this.restoredAt = restoredAt; }
        
        public boolean isRestored() { return isRestored; }
        public void setRestored(boolean restored) { this.isRestored = restored; }
        
        public int getRestoreCount() { return restoreCount; }
        public void setRestoreCount(int restoreCount) { this.restoreCount = restoreCount; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { 
            this.severity = severity != null ? severity : "MEDIUM";
        }
        
        public List<String> getScanResults() { return scanResults; }
        public void setScanResults(List<String> scanResults) { 
            this.scanResults = scanResults != null ? scanResults : new ArrayList<>();
        }
        
        public void addScanResult(String result) {
            if (result != null && !result.isEmpty()) {
                this.scanResults.add(result);
            }
        }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { 
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }
        
        public void addMetadata(String key, String value) {
            if (key != null && value != null) {
                this.metadata.put(key, value);
            }
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("QuarantineEntry{\n");
            sb.append("  id: ").append(id).append("\n");
            sb.append("  originalPath: ").append(originalPath).append("\n");
            sb.append("  originalFileName: ").append(originalFileName).append("\n");
            sb.append("  quarantinePath: ").append(quarantinePath).append("\n");
            sb.append("  fileSize: ").append(fileSize).append("\n");
            sb.append("  fileHash: ").append(fileHash).append("\n");
            sb.append("  threatName: ").append(threatName).append("\n");
            sb.append("  threatType: ").append(threatType).append("\n");
            sb.append("  detectedBy: ").append(detectedBy).append("\n");
            sb.append("  quarantinedAt: ").append(quarantinedAt).append("\n");
            sb.append("  quarantinedBy: ").append(quarantinedBy).append("\n");
            sb.append("  isRestored: ").append(isRestored).append("\n");
            sb.append("  restoreCount: ").append(restoreCount).append("\n");
            sb.append("  severity: ").append(severity).append("\n");
            sb.append("}");
            return sb.toString();
        }
        
        public String toFormattedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("📁 QUARANTINE ENTRY\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("ID:           ").append(id).append("\n");
            sb.append("File:         ").append(originalFileName).append("\n");
            sb.append("Original:     ").append(originalPath).append("\n");
            sb.append("Size:         ").append(formatSize(fileSize)).append("\n");
            sb.append("Hash:         ").append(fileHash != null ? fileHash : "N/A").append("\n");
            sb.append("Threat:       ").append(threatName).append("\n");
            sb.append("Type:         ").append(threatType).append("\n");
            sb.append("Severity:     ").append(severity).append("\n");
            sb.append("Detected By:  ").append(detectedBy != null ? detectedBy : "Unknown").append("\n");
            sb.append("Quarantined:  ").append(quarantinedAt.format(DATE_FORMATTER)).append("\n");
            sb.append("By:           ").append(quarantinedBy).append("\n");
            sb.append("Restored:     ").append(isRestored ? "✅ Yes" : "❌ No").append("\n");
            if (isRestored) {
                sb.append("Restored At:  ").append(restoredAt != null ? restoredAt.format(DATE_FORMATTER) : "Unknown").append("\n");
                sb.append("Restored By:  ").append(restoredBy != null ? restoredBy : "Unknown").append("\n");
            }
            sb.append("Restore Count:").append(restoreCount).append("\n");
            if (notes != null && !notes.isEmpty()) {
                sb.append("Notes:        ").append(notes).append("\n");
            }
            if (!scanResults.isEmpty()) {
                sb.append("Scan Results:\n");
                for (String result : scanResults) {
                    sb.append("  • ").append(result).append("\n");
                }
            }
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
        
        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String quarantineDir;
    private File quarantineDirectory;
    private List<QuarantineEntry> entries;
    private boolean autoClean;
    private long maxSize;
    private int maxAgeDays;
    private boolean initialized;
    private Object lock;
    private QuarantineEventListener eventListener;
    
    // ============================================================
    // INTERFACES
    // ============================================================
    
    /**
     * Event listener for quarantine events
     */
    public interface QuarantineEventListener {
        void onFileQuarantined(QuarantineEntry entry);
        void onFileRestored(QuarantineEntry entry);
        void onFileDeleted(QuarantineEntry entry);
        void onQuarantineCleaned(QuarantineEntry entry);
        void onError(String message, Exception e);
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public QuarantineManager() {
        this(DEFAULT_QUARANTINE_DIR);
    }
    
    /**
     * Constructor with custom quarantine directory
     * 
     * @param quarantineDir The quarantine directory path
     */
    public QuarantineManager(String quarantineDir) {
        this.quarantineDir = quarantineDir;
        this.entries = new ArrayList<>();
        this.autoClean = true;
        this.maxSize = MAX_QUARANTINE_SIZE;
        this.maxAgeDays = MAX_AGE_DAYS;
        this.lock = new Object();
        initialize();
    }
    
    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    /**
     * Initializes the quarantine manager
     */
    private void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Create quarantine directory
            quarantineDirectory = new File(quarantineDir);
            if (!quarantineDirectory.exists()) {
                boolean created = quarantineDirectory.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create quarantine directory: " + quarantineDir);
                }
            }
            
            // Create subdirectories
            createSubdirectories();
            
            // Load existing entries
            loadEntries();
            
            // Perform auto-clean if enabled
            if (autoClean) {
                autoCleanQuarantine();
            }
            
            initialized = true;
            log("Quarantine manager initialized: " + quarantineDirectory.getAbsolutePath());
            
        } catch (Exception e) {
            logError("Failed to initialize quarantine manager: " + e.getMessage(), e);
            initialized = false;
        }
    }
    
    /**
     * Creates quarantine subdirectories
     */
    private void createSubdirectories() {
        String[] subdirs = {"metadata", "backup", "reports"};
        for (String subdir : subdirs) {
            File dir = new File(quarantineDirectory, subdir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
    
    // ============================================================
    // QUARANTINE OPERATIONS
    // ============================================================
    
    /**
     * Quarantines a file
     * 
     * @param file The file to quarantine
     * @param threatName The threat name
     * @return The quarantine entry
     */
    public QuarantineEntry quarantineFile(File file, String threatName) throws IOException {
        return quarantineFile(file, threatName, "UNKNOWN", null);
    }
    
    /**
     * Quarantines a file with additional metadata
     * 
     * @param file The file to quarantine
     * @param threatName The threat name
     * @param threatType The threat type
     * @param notes Additional notes
     * @return The quarantine entry
     */
    public QuarantineEntry quarantineFile(File file, String threatName, 
                                         String threatType, String notes) throws IOException {
        synchronized (lock) {
            if (!file.exists()) {
                throw new FileNotFoundException("File not found: " + file.getPath());
            }
            
            if (file.isDirectory()) {
                throw new IllegalArgumentException("Cannot quarantine a directory: " + file.getPath());
            }
            
            // Check quarantine size
            if (getQuarantineSize() > maxSize) {
                throw new IOException("Quarantine is full. Max size: " + formatSize(maxSize));
            }
            
            // Create entry
            QuarantineEntry entry = new QuarantineEntry(file.getPath(), threatName);
            entry.setThreatType(threatType != null ? threatType : "UNKNOWN");
            entry.setNotes(notes);
            entry.setFileSize(file.length());
            entry.setQuarantinedBy(System.getProperty("user.name"));
            
            // Calculate file hash
            try {
                entry.setFileHash(calculateFileHash(file));
            } catch (Exception e) {
                // Hash calculation failed, continue without hash
                entry.setFileHash(null);
            }
            
            // Generate quarantine path
            String quarantineFileName = entry.getId() + "_" + file.getName() + QUARANTINE_EXTENSION;
            String quarantinePath = new File(quarantineDirectory, quarantineFileName).getPath();
            entry.setQuarantinePath(quarantinePath);
            
            // Move file to quarantine
            Path source = file.toPath();
            Path target = Paths.get(quarantinePath);
            
            try {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Try copying and then deleting
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(source);
            }
            
            // Save metadata
            saveEntryMetadata(entry);
            
            // Add to entries list
            entries.add(entry);
            
            // Update index
            updateIndex();
            
            // Fire event
            if (eventListener != null) {
                eventListener.onFileQuarantined(entry);
            }
            
            log("File quarantined: " + file.getPath() + " -> " + quarantinePath);
            
            // Auto-clean if needed
            if (autoClean && getQuarantineSize() > maxSize * AUTO_CLEAN_THRESHOLD) {
                autoCleanQuarantine();
            }
            
            return entry;
        }
    }
    
    /**
     * Restores a quarantined file to its original location
     * 
     * @param entryId The quarantine entry ID
     * @return true if restore was successful
     */
    public boolean restoreFile(String entryId) {
        synchronized (lock) {
            QuarantineEntry entry = findEntry(entryId);
            if (entry == null) {
                return false;
            }
            
            if (entry.isRestored()) {
                return true; // Already restored
            }
            
            return restoreFile(entry);
        }
    }
    
    /**
     * Restores a quarantined file
     * 
     * @param entry The quarantine entry
     * @return true if restore was successful
     */
    private boolean restoreFile(QuarantineEntry entry) {
        File quarantineFile = new File(entry.getQuarantinePath());
        if (!quarantineFile.exists()) {
            return false;
        }
        
        File originalFile = new File(entry.getOriginalPath());
        
        // Check if original path is available
        if (originalFile.exists()) {
            // Create backup of existing file
            File backup = new File(originalFile.getPath() + ".backup");
            try {
                Files.copy(originalFile.toPath(), backup.toPath(), 
                          StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logError("Failed to create backup: " + e.getMessage(), e);
            }
        }
        
        try {
            // Move file back to original location
            Path source = quarantineFile.toPath();
            Path target = originalFile.toPath();
            
            // Ensure parent directory exists
            File parent = originalFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            // Update entry
            entry.setRestored(true);
            entry.setRestoredAt(LocalDateTime.now());
            entry.setRestoredBy(System.getProperty("user.name"));
            entry.setRestoreCount(entry.getRestoreCount() + 1);
            
            // Update metadata
            saveEntryMetadata(entry);
            
            // Update index
            updateIndex();
            
            // Fire event
            if (eventListener != null) {
                eventListener.onFileRestored(entry);
            }
            
            log("File restored: " + entry.getOriginalPath());
            return true;
            
        } catch (IOException e) {
            logError("Failed to restore file: " + e.getMessage(), e);
            if (eventListener != null) {
                eventListener.onError("Failed to restore file: " + e.getMessage(), e);
            }
            return false;
        }
    }
    
    /**
     * Permanently deletes a quarantined file
     * 
     * @param entryId The quarantine entry ID
     * @return true if deletion was successful
     */
    public boolean deleteFile(String entryId) {
        synchronized (lock) {
            QuarantineEntry entry = findEntry(entryId);
            if (entry == null) {
                return false;
            }
            
            return deleteFile(entry);
        }
    }
    
    /**
     * Permanently deletes a quarantined file
     * 
     * @param entry The quarantine entry
     * @return true if deletion was successful
     */
    private boolean deleteFile(QuarantineEntry entry) {
        File quarantineFile = new File(entry.getQuarantinePath());
        
        try {
            // Delete the file
            if (quarantineFile.exists()) {
                Files.delete(quarantineFile.toPath());
            }
            
            // Delete metadata
            deleteEntryMetadata(entry);
            
            // Remove from list
            entries.remove(entry);
            
            // Update index
            updateIndex();
            
            // Fire event
            if (eventListener != null) {
                eventListener.onFileDeleted(entry);
            }
            
            log("Quarantined file deleted: " + entry.getOriginalPath());
            return true;
            
        } catch (IOException e) {
            logError("Failed to delete file: " + e.getMessage(), e);
            if (eventListener != null) {
                eventListener.onError("Failed to delete file: " + e.getMessage(), e);
            }
            return false;
        }
    }
    
    // ============================================================
    // QUARANTINE MANAGEMENT
    // ============================================================
    
    /**
     * Performs auto-clean of old quarantined files
     */
    public void autoCleanQuarantine() {
        synchronized (lock) {
            List<QuarantineEntry> toRemove = new ArrayList<>();
            LocalDateTime cutoff = LocalDateTime.now().minusDays(maxAgeDays);
            
            for (QuarantineEntry entry : entries) {
                if (!entry.isRestored() && 
                    entry.getQuarantinedAt().isBefore(cutoff)) {
                    toRemove.add(entry);
                }
            }
            
            // Remove old files that exceed quota
            long currentSize = getQuarantineSize();
            for (QuarantineEntry entry : entries) {
                if (currentSize <= maxSize * AUTO_CLEAN_THRESHOLD) {
                    break;
                }
                if (!entry.isRestored()) {
                    toRemove.add(entry);
                    currentSize -= entry.getFileSize();
                }
            }
            
            for (QuarantineEntry entry : toRemove) {
                deleteFile(entry);
                if (eventListener != null) {
                    eventListener.onQuarantineCleaned(entry);
                }
                log("Auto-cleaned: " + entry.getOriginalFileName());
            }
        }
    }
    
    /**
     * Calculates the total quarantine size
     * 
     * @return Total size in bytes
     */
    public long getQuarantineSize() {
        long size = 0;
        for (QuarantineEntry entry : entries) {
            if (!entry.isRestored()) {
                size += entry.getFileSize();
            }
        }
        return size;
    }
    
    /**
     * Gets the number of quarantined files
     * 
     * @return Number of quarantined files
     */
    public int getQuarantineCount() {
        int count = 0;
        for (QuarantineEntry entry : entries) {
            if (!entry.isRestored()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets the total number of entries (including restored)
     * 
     * @return Total entries count
     */
    public int getTotalEntryCount() {
        return entries.size();
    }
    
    // ============================================================
    // QUERY METHODS
    // ============================================================
    
    /**
     * Finds a quarantine entry by ID
     * 
     * @param id The entry ID
     * @return The entry, or null if not found
     */
    public QuarantineEntry findEntry(String id) {
        for (QuarantineEntry entry : entries) {
            if (entry.getId().equals(id)) {
                return entry;
            }
        }
        return null;
    }
    
    /**
     * Finds quarantine entries by original file name
     * 
     * @param fileName The file name
     * @return List of matching entries
     */
    public List<QuarantineEntry> findEntriesByFileName(String fileName) {
        List<QuarantineEntry> result = new ArrayList<>();
        for (QuarantineEntry entry : entries) {
            if (entry.getOriginalFileName().equalsIgnoreCase(fileName)) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Finds quarantine entries by threat name
     * 
     * @param threatName The threat name
     * @return List of matching entries
     */
    public List<QuarantineEntry> findEntriesByThreat(String threatName) {
        List<QuarantineEntry> result = new ArrayList<>();
        for (QuarantineEntry entry : entries) {
            if (entry.getThreatName() != null && 
                entry.getThreatName().toLowerCase().contains(threatName.toLowerCase())) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Gets all quarantine entries
     * 
     * @return List of all entries
     */
    public List<QuarantineEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }
    
    /**
     * Gets active (non-restored) quarantine entries
     * 
     * @return List of active entries
     */
    public List<QuarantineEntry> getActiveEntries() {
        List<QuarantineEntry> result = new ArrayList<>();
        for (QuarantineEntry entry : entries) {
            if (!entry.isRestored()) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Gets restored entries
     * 
     * @return List of restored entries
     */
    public List<QuarantineEntry> getRestoredEntries() {
        List<QuarantineEntry> result = new ArrayList<>();
        for (QuarantineEntry entry : entries) {
            if (entry.isRestored()) {
                result.add(entry);
            }
        }
        return result;
    }
    
    // ============================================================
    // REPORTING
    // ============================================================
    
    /**
     * Generates a quarantine report
     * 
     * @return The report as a string
     */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    QUARANTINE REPORT                         ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append("📊 STATISTICS\n");
        sb.append("┌─────────────────────────────────────────────────────────────┐\n");
        sb.append("│ Total Entries:    ").append(padRight(String.valueOf(entries.size()), 35)).append("│\n");
        sb.append("│ Active Files:     ").append(padRight(String.valueOf(getQuarantineCount()), 35)).append("│\n");
        sb.append("│ Restored Files:   ").append(padRight(String.valueOf(getRestoredEntries().size()), 35)).append("│\n");
        sb.append("│ Total Size:       ").append(padRight(formatSize(getQuarantineSize()), 35)).append("│\n");
        sb.append("│ Max Size:         ").append(padRight(formatSize(maxSize), 35)).append("│\n");
        sb.append("│ Auto-Clean:       ").append(padRight(autoClean ? "Enabled" : "Disabled", 35)).append("│\n");
        sb.append("└─────────────────────────────────────────────────────────────┘\n\n");
        
        if (!entries.isEmpty()) {
            sb.append("📁 QUARANTINED FILES\n");
            sb.append("┌─────────────────────────────────────────────────────────────┐\n");
            
            for (QuarantineEntry entry : entries) {
                String status = entry.isRestored() ? "RESTORED" : "QUARANTINED";
                String line = String.format("│ %s | %s | %s | %s │",
                    entry.getOriginalFileName().length() > 25 ? 
                        entry.getOriginalFileName().substring(0, 22) + "..." :
                        entry.getOriginalFileName(),
                    entry.getThreatName() != null && entry.getThreatName().length() > 15 ?
                        entry.getThreatName().substring(0, 12) + "..." :
                        entry.getThreatName(),
                    entry.getSeverity(),
                    status
                );
                sb.append(line).append("\n");
            }
            sb.append("└─────────────────────────────────────────────────────────────┘\n");
        }
        
        sb.append("\n📅 Report Generated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Exports quarantine report to a file
     * 
     * @param outputPath The output file path
     */
    public void exportReport(String outputPath) throws IOException {
        String report = generateReport();
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(report);
        }
        log("Report exported: " + outputPath);
    }
    
    // ============================================================
    // IMPORT/EXPORT
    // ============================================================
    
    /**
     * Exports quarantine data to a zip archive
     * 
     * @param outputPath The output zip file path
     */
    public void exportQuarantine(String outputPath) throws IOException {
        synchronized (lock) {
            try (FileOutputStream fos = new FileOutputStream(outputPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                
                // Export metadata
                ZipEntry metaEntry = new ZipEntry("metadata.json");
                zos.putNextEntry(metaEntry);
                String metadata = exportMetadataToJson();
                zos.write(metadata.getBytes());
                zos.closeEntry();
                
                // Export index
                ZipEntry indexEntry = new ZipEntry("index.txt");
                zos.putNextEntry(indexEntry);
                String index = generateIndex();
                zos.write(index.getBytes());
                zos.closeEntry();
                
                // Export report
                ZipEntry reportEntry = new ZipEntry("report.txt");
                zos.putNextEntry(reportEntry);
                String report = generateReport();
                zos.write(report.getBytes());
                zos.closeEntry();
                
                // Export quarantined files
                for (QuarantineEntry entry : entries) {
                    if (!entry.isRestored()) {
                        File file = new File(entry.getQuarantinePath());
                        if (file.exists()) {
                            ZipEntry fileEntry = new ZipEntry(entry.getId() + "_" + 
                                entry.getOriginalFileName());
                            zos.putNextEntry(fileEntry);
                            try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = fis.read(buffer)) != -1) {
                                    zos.write(buffer, 0, bytesRead);
                                }
                            }
                            zos.closeEntry();
                        }
                    }
                }
            }
            log("Quarantine exported: " + outputPath);
        }
    }
    
    /**
     * Imports quarantine data from a zip archive
     * 
     * @param inputPath The input zip file path
     */
    public void importQuarantine(String inputPath) throws IOException {
        synchronized (lock) {
            try (FileInputStream fis = new FileInputStream(inputPath);
                 ZipInputStream zis = new ZipInputStream(fis)) {
                
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (name.equals("metadata.json")) {
                        // Read metadata
                        String metadata = new String(zis.readAllBytes());
                        importMetadataFromJson(metadata);
                    } else if (name.equals("index.txt")) {
                        // Skip index, will be regenerated
                    } else if (name.equals("report.txt")) {
                        // Skip report
                    } else if (!name.startsWith("__")) {
                        // This is a quarantined file
                        String fileName = name.substring(name.indexOf('_') + 1);
                        File targetFile = new File(quarantineDirectory, name);
                        
                        // Create directories if needed
                        targetFile.getParentFile().mkdirs();
                        
                        // Write file
                        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = zis.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                    zis.closeEntry();
                }
            }
            
            // Reload entries
            loadEntries();
            log("Quarantine imported: " + inputPath);
        }
    }
    
    // ============================================================
    // METADATA MANAGEMENT
    // ============================================================
    
    /**
     * Saves entry metadata to a file
     * 
     * @param entry The entry to save
     */
    private void saveEntryMetadata(QuarantineEntry entry) {
        try {
            String metaPath = quarantineDirectory + File.separator + "metadata" + 
                             File.separator + entry.getId() + METADATA_EXTENSION;
            
            StringBuilder sb = new StringBuilder();
            sb.append("ID:").append(entry.getId()).append("\n");
            sb.append("ORIGINAL_PATH:").append(entry.getOriginalPath()).append("\n");
            sb.append("ORIGINAL_FILENAME:").append(entry.getOriginalFileName()).append("\n");
            sb.append("QUARANTINE_PATH:").append(entry.getQuarantinePath()).append("\n");
            sb.append("FILE_SIZE:").append(entry.getFileSize()).append("\n");
            sb.append("FILE_HASH:").append(entry.getFileHash() != null ? entry.getFileHash() : "").append("\n");
            sb.append("THREAT_NAME:").append(entry.getThreatName() != null ? entry.getThreatName() : "").append("\n");
            sb.append("THREAT_TYPE:").append(entry.getThreatType() != null ? entry.getThreatType() : "").append("\n");
            sb.append("DETECTED_BY:").append(entry.getDetectedBy() != null ? entry.getDetectedBy() : "").append("\n");
            sb.append("QUARANTINED_AT:").append(entry.getQuarantinedAt().format(DATE_FORMATTER)).append("\n");
            sb.append("QUARANTINED_BY:").append(entry.getQuarantinedBy()).append("\n");
            sb.append("RESTORED_BY:").append(entry.getRestoredBy() != null ? entry.getRestoredBy() : "").append("\n");
            sb.append("RESTORED_AT:").append(entry.getRestoredAt() != null ? 
                entry.getRestoredAt().format(DATE_FORMATTER) : "").append("\n");
            sb.append("IS_RESTORED:").append(entry.isRestored()).append("\n");
            sb.append("RESTORE_COUNT:").append(entry.getRestoreCount()).append("\n");
            sb.append("SEVERITY:").append(entry.getSeverity()).append("\n");
            sb.append("NOTES:").append(entry.getNotes() != null ? entry.getNotes() : "").append("\n");
            sb.append("SCAN_RESULTS:").append(String.join(";", entry.getScanResults())).append("\n");
            
            try (FileWriter writer = new FileWriter(metaPath)) {
                writer.write(sb.toString());
            }
            
        } catch (IOException e) {
            logError("Failed to save metadata for: " + entry.getId(), e);
        }
    }
    
    /**
     * Deletes entry metadata
     * 
     * @param entry The entry to delete metadata for
     */
    private void deleteEntryMetadata(QuarantineEntry entry) {
        try {
            String metaPath = quarantineDirectory + File.separator + "metadata" + 
                             File.separator + entry.getId() + METADATA_EXTENSION;
            File metaFile = new File(metaPath);
            if (metaFile.exists()) {
                Files.delete(metaFile.toPath());
            }
        } catch (IOException e) {
            logError("Failed to delete metadata for: " + entry.getId(), e);
        }
    }
    
    /**
     * Loads all entries from metadata files
     */
    private void loadEntries() {
        entries.clear();
        
        File metadataDir = new File(quarantineDirectory, "metadata");
        if (!metadataDir.exists()) {
            return;
        }
        
        File[] metaFiles = metadataDir.listFiles((dir, name) -> name.endsWith(METADATA_EXTENSION));
        if (metaFiles == null) {
            return;
        }
        
        for (File metaFile : metaFiles) {
            try {
                QuarantineEntry entry = loadEntryMetadata(metaFile);
                if (entry != null) {
                    // Verify quarantine file exists
                    File quarantineFile = new File(entry.getQuarantinePath());
                    if (!quarantineFile.exists() && !entry.isRestored()) {
                        // File is missing, but keep entry for reference
                        entry.setFileSize(0);
                    }
                    entries.add(entry);
                }
            } catch (Exception e) {
                logError("Failed to load metadata: " + metaFile.getName(), e);
            }
        }
        
        // Also scan quarantine directory for orphaned files
        scanForOrphanedFiles();
    }
    
    /**
     * Loads entry metadata from a file
     * 
     * @param metaFile The metadata file
     * @return The entry, or null if loading failed
     */
    private QuarantineEntry loadEntryMetadata(File metaFile) throws IOException {
        QuarantineEntry entry = new QuarantineEntry();
        Map<String, String> properties = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(metaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String key = line.substring(0, colonIndex);
                    String value = line.substring(colonIndex + 1);
                    properties.put(key, value);
                }
            }
        }
        
        // Populate entry
        entry.setId(properties.getOrDefault("ID", UUID.randomUUID().toString()));
        entry.setOriginalPath(properties.getOrDefault("ORIGINAL_PATH", ""));
        entry.setOriginalFileName(properties.getOrDefault("ORIGINAL_FILENAME", ""));
        entry.setQuarantinePath(properties.getOrDefault("QUARANTINE_PATH", ""));
        
        String fileSizeStr = properties.getOrDefault("FILE_SIZE", "0");
        entry.setFileSize(Long.parseLong(fileSizeStr));
        
        entry.setFileHash(properties.getOrDefault("FILE_HASH", null));
        entry.setThreatName(properties.getOrDefault("THREAT_NAME", null));
        entry.setThreatType(properties.getOrDefault("THREAT_TYPE", null));
        entry.setDetectedBy(properties.getOrDefault("DETECTED_BY", null));
        
        String quarantinedAtStr = properties.getOrDefault("QUARANTINED_AT", 
            LocalDateTime.now().format(DATE_FORMATTER));
        entry.setQuarantinedAt(LocalDateTime.parse(quarantinedAtStr, DATE_FORMATTER));
        
        entry.setQuarantinedBy(properties.getOrDefault("QUARANTINED_BY", 
            System.getProperty("user.name")));
        
        String restoredBy = properties.getOrDefault("RESTORED_BY", null);
        if (restoredBy != null && !restoredBy.isEmpty()) {
            entry.setRestoredBy(restoredBy);
        }
        
        String restoredAtStr = properties.getOrDefault("RESTORED_AT", null);
        if (restoredAtStr != null && !restoredAtStr.isEmpty()) {
            entry.setRestoredAt(LocalDateTime.parse(restoredAtStr, DATE_FORMATTER));
        }
        
        entry.setRestored(Boolean.parseBoolean(properties.getOrDefault("IS_RESTORED", "false")));
        
        String restoreCountStr = properties.getOrDefault("RESTORE_COUNT", "0");
        entry.setRestoreCount(Integer.parseInt(restoreCountStr));
        
        entry.setSeverity(properties.getOrDefault("SEVERITY", "MEDIUM"));
        entry.setNotes(properties.getOrDefault("NOTES", null));
        
        String scanResults = properties.getOrDefault("SCAN_RESULTS", "");
        if (!scanResults.isEmpty()) {
            String[] results = scanResults.split(";");
            for (String result : results) {
                if (!result.isEmpty()) {
                    entry.addScanResult(result);
                }
            }
        }
        
        return entry;
    }
    
    /**
     * Scans for orphaned files in quarantine
     */
    private void scanForOrphanedFiles() {
        File[] quarantineFiles = quarantineDirectory.listFiles((dir, name) -> 
            name.endsWith(QUARANTINE_EXTENSION));
        
        if (quarantineFiles == null) {
            return;
        }
        
        Set<String> entryPaths = new HashSet<>();
        for (QuarantineEntry entry : entries) {
            if (!entry.isRestored()) {
                entryPaths.add(entry.getQuarantinePath());
            }
        }
        
        for (File file : quarantineFiles) {
            if (!entryPaths.contains(file.getPath())) {
                // Orphaned file - create entry
                try {
                    QuarantineEntry entry = new QuarantineEntry(
                        file.getPath(), 
                        "Orphaned File (Recovered)"
                    );
                    entry.setQuarantinePath(file.getPath());
                    entry.setFileSize(file.length());
                    entry.setQuarantinedAt(LocalDateTime.now().minusDays(1));
                    entry.setDetectedBy("System Recovery");
                    entry.setSeverity("LOW");
                    entry.addMetadata("recovered", "true");
                    
                    // Try to calculate hash
                    try {
                        entry.setFileHash(calculateFileHash(file));
                    } catch (Exception e) {
                        // Ignore
                    }
                    
                    saveEntryMetadata(entry);
                    entries.add(entry);
                    log("Orphaned file recovered: " + file.getName());
                    
                } catch (Exception e) {
                    logError("Failed to recover orphaned file: " + file.getName(), e);
                }
            }
        }
    }
    
    /**
     * Updates the quarantine index
     */
    private void updateIndex() {
        try {
            String indexPath = quarantineDirectory + File.separator + INDEX_FILE;
            String index = generateIndex();
            try (FileWriter writer = new FileWriter(indexPath)) {
                writer.write(index);
            }
        } catch (IOException e) {
            logError("Failed to update index: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates the quarantine index
     * 
     * @return The index as a string
     */
    private String generateIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Quarantine Index\n");
        sb.append("# Generated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        sb.append("# Format: ID|OriginalPath|ThreatName|Status|Date\n");
        sb.append("#\n");
        
        for (QuarantineEntry entry : entries) {
            String status = entry.isRestored() ? "RESTORED" : "QUARANTINED";
            sb.append(entry.getId()).append("|")
              .append(entry.getOriginalPath()).append("|")
              .append(entry.getThreatName() != null ? entry.getThreatName() : "Unknown").append("|")
              .append(status).append("|")
              .append(entry.getQuarantinedAt().format(DATE_FORMATTER)).append("\n");
        }
        
        return sb.toString();
    }
    
    // ============================================================
    // EXPORT METADATA TO JSON
    // ============================================================
    
    /**
     * Exports all metadata to JSON format
     * 
     * @return JSON string
     */
    private String exportMetadataToJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"1.0\",\n");
        sb.append("  \"exportedAt\": \"").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\",\n");
        sb.append("  \"entries\": [\n");
        
        for (int i = 0; i < entries.size(); i++) {
            QuarantineEntry entry = entries.get(i);
            sb.append("    {\n");
            sb.append("      \"id\": \"").append(entry.getId()).append("\",\n");
            sb.append("      \"originalPath\": \"").append(escapeJson(entry.getOriginalPath())).append("\",\n");
            sb.append("      \"originalFileName\": \"").append(escapeJson(entry.getOriginalFileName())).append("\",\n");
            sb.append("      \"threatName\": \"").append(escapeJson(entry.getThreatName())).append("\",\n");
            sb.append("      \"threatType\": \"").append(escapeJson(entry.getThreatType())).append("\",\n");
            sb.append("      \"severity\": \"").append(entry.getSeverity()).append("\",\n");
            sb.append("      \"fileSize\": ").append(entry.getFileSize()).append(",\n");
            sb.append("      \"fileHash\": \"").append(entry.getFileHash() != null ? entry.getFileHash() : "").append("\",\n");
            sb.append("      \"quarantinedAt\": \"").append(entry.getQuarantinedAt().format(DATE_FORMATTER)).append("\",\n");
            sb.append("      \"quarantinedBy\": \"").append(entry.getQuarantinedBy()).append("\",\n");
            sb.append("      \"isRestored\": ").append(entry.isRestored()).append(",\n");
            sb.append("      \"restoreCount\": ").append(entry.getRestoreCount()).append(",\n");
            sb.append("      \"notes\": \"").append(escapeJson(entry.getNotes())).append("\"\n");
            sb.append("    }").append(i < entries.size() - 1 ? "," : "").append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Imports metadata from JSON
     * 
     * @param json The JSON string
     */
    private void importMetadataFromJson(String json) {
        // Simple JSON parsing (for production, use a proper JSON library)
        // This is a simplified implementation
        String[] lines = json.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("\"id\":")) {
                String id = extractJsonValue(line);
                // Create entry and populate
                QuarantineEntry entry = new QuarantineEntry();
                entry.setId(id);
                // ... parse other fields
                entries.add(entry);
            }
        }
    }
    
    /**
     * Extracts a value from JSON line
     * 
     * @param line The JSON line
     * @return The extracted value
     */
    private String extractJsonValue(String line) {
        int colonIndex = line.indexOf(':');
        if (colonIndex < 0) {
            return "";
        }
        String value = line.substring(colonIndex + 1).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
    
    /**
     * Escapes JSON special characters
     * 
     * @param s The string to escape
     * @return The escaped string
     */
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Calculates the SHA-256 hash of a file
     * 
     * @param file The file to hash
     * @return The hash as a hex string
     */
    private String calculateFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
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
    
    /**
     * Formats a size in bytes to a human-readable string
     * 
     * @param bytes The size in bytes
     * @return Formatted string
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Pads a string to the right
     * 
     * @param s The string to pad
     * @param width The width to pad to
     * @return The padded string
     */
    private String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) {
            return s.substring(0, width);
        }
        return s + " ".repeat(width - s.length());
    }
    
    // ============================================================
    // LOGGING
    // ============================================================
    
    /**
     * Logs a message
     * 
     * @param message The message to log
     */
    private void log(String message) {
        System.out.println("[QuarantineManager] " + message);
    }
    
    /**
     * Logs an error message
     * 
     * @param message The error message
     * @param e The exception
     */
    private void logError(String message, Exception e) {
        System.err.println("[QuarantineManager] ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public String getQuarantineDir() {
        return quarantineDir;
    }
    
    public void setQuarantineDir(String quarantineDir) {
        this.quarantineDir = quarantineDir;
        this.quarantineDirectory = new File(quarantineDir);
        if (!quarantineDirectory.exists()) {
            quarantineDirectory.mkdirs();
        }
        loadEntries();
    }
    
    public boolean isAutoClean() {
        return autoClean;
    }
    
    public void setAutoClean(boolean autoClean) {
        this.autoClean = autoClean;
        if (autoClean) {
            autoCleanQuarantine();
        }
    }
    
    public long getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(long maxSize) {
        if (maxSize < 1024 * 1024) {
            throw new IllegalArgumentException("Max size must be at least 1MB");
        }
        this.maxSize = maxSize;
        if (autoClean && getQuarantineSize() > maxSize * AUTO_CLEAN_THRESHOLD) {
            autoCleanQuarantine();
        }
    }
    
    public int getMaxAgeDays() {
        return maxAgeDays;
    }
    
    public void setMaxAgeDays(int maxAgeDays) {
        if (maxAgeDays < 1) {
            throw new IllegalArgumentException("Max age must be at least 1 day");
        }
        this.maxAgeDays = maxAgeDays;
        if (autoClean) {
            autoCleanQuarantine();
        }
    }
    
    public QuarantineEventListener getEventListener() {
        return eventListener;
    }
    
    public void setEventListener(QuarantineEventListener eventListener) {
        this.eventListener = eventListener;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    // ============================================================
    // CLEANUP
    // ============================================================
    
    /**
     * Cleans up resources
     */
    public void cleanup() {
        // Save any pending changes
        updateIndex();
        
        // Clean up if needed
        if (autoClean) {
            autoCleanQuarantine();
        }
        
        log("Quarantine manager cleaned up");
    }
}