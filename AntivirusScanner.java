package com.neilos.security;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Antivirus scanner for NeilOS
 */
public class AntivirusScanner {
    private static final String QUARANTINE_DIR = "quarantine";
    private static final String LOG_DIR = "logs";
    private static final String REPORT_FILE = "scan_report.txt";
    
    private int totalFiles;
    private int infectedFiles;
    private List<String> detectedThreats;
    
    // Example malware signatures (SHA256 hashes)
    private static final Map<String, String> MALWARE_SIGNATURES = new HashMap<>();
    
    static {
        MALWARE_SIGNATURES.put(
            "275a021bbfb6488ecf7d0f860b4f2f16f4f3f5d7c6b5e4b63f4f8e8b1f7a4b1",
            "Demo Trojan"
        );
        // Add more signatures as needed
    }
    
    public AntivirusScanner() {
        this.detectedThreats = new ArrayList<>();
        new File(QUARANTINE_DIR).mkdirs();
        new File(LOG_DIR).mkdirs();
    }
    
    public String calculateSHA256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
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
        } catch (NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }
    
    public boolean quarantineFile(File file) {
        try {
            String filename = file.getName();
            String timestamp = String.valueOf(System.currentTimeMillis());
            File dest = new File(QUARANTINE_DIR, timestamp + "_" + filename);
            Files.move(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log("Quarantined: " + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            log("Error quarantining file: " + e.getMessage());
            return false;
        }
    }
    
    public boolean scanFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        String hash = calculateSHA256(file);
        if (hash == null) {
            return false;
        }
        
        if (MALWARE_SIGNATURES.containsKey(hash)) {
            infectedFiles++;
            String threatName = MALWARE_SIGNATURES.get(hash);
            detectedThreats.add(file.getAbsolutePath() + " - " + threatName);
            quarantineFile(file);
            log("Malware detected: " + file.getAbsolutePath() + " (" + threatName + ")");
            return true;
        }
        return false;
    }
    
    public String scanDirectory(File directory, ScanCallback callback) {
        totalFiles = 0;
        infectedFiles = 0;
        detectedThreats.clear();
        
        if (!directory.exists() || !directory.isDirectory()) {
            return "Invalid directory path";
        }
        
        try {
            Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    totalFiles++;
                    File file = path.toFile();
                    boolean infected = scanFile(file);
                    if (callback != null) {
                        callback.onFileScanned(file.getAbsolutePath(), infected);
                    }
                });
        } catch (IOException e) {
            log("Error scanning directory: " + e.getMessage());
        }
        
        String report = generateReport();
        saveReport(report);
        return report;
    }
    
    private String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        sb.append("Files Scanned: ").append(totalFiles).append("\n");
        sb.append("Infected Files: ").append(infectedFiles).append("\n");
        if (!detectedThreats.isEmpty()) {
            sb.append("Threats Detected:\n");
            for (String threat : detectedThreats) {
                sb.append("  - ").append(threat).append("\n");
            }
        }
        sb.append("-".repeat(50)).append("\n");
        return sb.toString();
    }
    
    private void saveReport(String report) {
        try (FileWriter fw = new FileWriter(REPORT_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(report);
        } catch (IOException e) {
            log("Error saving report: " + e.getMessage());
        }
    }
    
    private void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_DIR + File.separator + "antivirus.log", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Log error: " + e.getMessage());
        }
    }
    
    public int getTotalFiles() {
        return totalFiles;
    }
    
    public int getInfectedFiles() {
        return infectedFiles;
    }
    
    public List<String> getDetectedThreats() {
        return new ArrayList<>(detectedThreats);
    }
    
    public interface ScanCallback {
        void onFileScanned(String path, boolean infected);
    }
}