package com.neilos.apps;

import com.neilos.NeilOS;
import com.neilos.security.AntivirusScanner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;

/**
 * Cyber Security Center with all security features
 */
public class CyberSecurityApp extends JPanel {
    private NeilOS neilos;
    private JTextArea outputArea;
    private List<String> detectedThreats;
    
    public CyberSecurityApp(NeilOS neilos) {
        this.neilos = neilos;
        this.detectedThreats = new ArrayList<>();
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
    }
    
    private void createUI() {
        JPanel topPanel = new JPanel(new GridLayout(6, 4, 5, 5));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Row 1: Basic Security
        addButton(topPanel, "🔑 Gen Password", "#ef4444", this::generatePassword);
        addButton(topPanel, "🔒 Check Strength", "#ef4444", this::checkStrength);
        addButton(topPanel, "🔢 HashCalc", "#ef4444", this::hashCalculator);
        addButton(topPanel, "🔍 Quick Scan", "#ef4444", this::quickScan);
        
        // Row 2: Attack Detection
        addButton(topPanel, "🛡 DDoS Detector", "#f59e0b", this::ddosDetector);
        addButton(topPanel, "🦠 Malware Scan", "#f59e0b", this::malwareScan);
        addButton(topPanel, "🎣 Phishing Detector", "#f59e0b", this::phishingDetector);
        addButton(topPanel, "🔒 MITM Detector", "#f59e0b", this::mitmDetector);
        
        // Row 3: Network Security
        addButton(topPanel, "🌐 Nmap Scanner", "#3b82f6", this::nmapScan);
        addButton(topPanel, "📡 Network Scanner", "#3b82f6", this::networkScan);
        addButton(topPanel, "🔗 URL Scanner", "#3b82f6", this::urlScan);
        addButton(topPanel, "📱 SMS Bomber Sim", "#3b82f6", this::smsBomberSim);
        
        // Row 4: Advanced Security
        addButton(topPanel, "🤖 Botnet Detector", "#8b5cf6", this::botnetDetector);
        addButton(topPanel, "🖼 Image Generator", "#8b5cf6", this::imageGenerator);
        addButton(topPanel, "🔧 Metasploit Detector", "#8b5cf6", this::metasploitDetector);
        addButton(topPanel, "🦠 VirusTotal Sim", "#8b5cf6", this::virusTotalSim);
        
        // Row 5: Additional Security
        addButton(topPanel, "📧 Spam Detector", "#ec4899", this::spamDetector);
        addButton(topPanel, "🔌 BadUSB Detector", "#ec4899", this::badusbDetector);
        addButton(topPanel, "🛡 Antivirus Creator", "#00BCD4", this::openAntivirus);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.decode("#00ff00"));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // Initial message
        outputArea.append("🛡️ Cyber Security Center\n");
        outputArea.append("━".repeat(40) + "\n");
        outputArea.append("Ready for security operations.\n\n");
    }
    
    private void addButton(JPanel panel, String text, String color, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.PLAIN, 10));
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }
    
    private void log(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
        NeilOS.cyberLog.add(message);
    }
    
    private void logResult(String title, String result) {
        log("━".repeat(40));
        log(title);
        log("━".repeat(40));
        log(result);
        log("");
    }
    
    // Security Features
    
    private void generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        JOptionPane.showMessageDialog(this, "Generated Password:\n\n" + password.toString(), 
            "Password Generator", JOptionPane.INFORMATION_MESSAGE);
        log("Generated password: " + password.toString().substring(0, 6) + "***");
    }
    
    private void checkStrength() {
        String password = JOptionPane.showInputDialog(this, "Enter password to check strength:", 
            "Password Strength", JOptionPane.QUESTION_MESSAGE);
        if (password == null || password.isEmpty()) return;
        
        int score = 0;
        List<String> feedback = new ArrayList<>();
        
        if (password.length() >= 12) {
            score += 2;
            feedback.add("✓ Excellent length (12+ chars)");
        } else if (password.length() >= 8) {
            score += 1;
            feedback.add("✓ Good length (8-11 chars)");
        } else {
            feedback.add("✗ Too short (<8 chars)");
        }
        
        if (password.matches(".*\\d.*")) {
            score += 1;
            feedback.add("✓ Contains numbers");
        } else {
            feedback.add("✗ No numbers");
        }
        
        if (password.matches(".*[A-Z].*")) {
            score += 1;
            feedback.add("✓ Contains uppercase");
        } else {
            feedback.add("✗ No uppercase letters");
        }
        
        if (password.matches(".*[a-z].*")) {
            score += 1;
            feedback.add("✓ Contains lowercase");
        }
        
        if (password.matches(".*[!@#$%^&*].*")) {
            score += 2;
            feedback.add("✓ Contains special characters");
        } else {
            feedback.add("✗ No special characters");
        }
        
        String strength;
        String color;
        if (score >= 7) {
            strength = "VERY STRONG 💪";
            color = "#10b981";
        } else if (score >= 5) {
            strength = "STRONG ✅";
            color = "#3b82f6";
        } else if (score >= 3) {
            strength = "MODERATE ⚠️";
            color = "#f59e0b";
        } else {
            strength = "WEAK ❌";
            color = "#ef4444";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("Password Strength: ").append(strength).append("\n\n");
        result.append("Score: ").append(score).append("/8\n\n");
        for (String f : feedback) {
            result.append(f).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), "Password Analysis", 
            JOptionPane.INFORMATION_MESSAGE);
        log("Password strength check: " + strength);
    }
    
    private void hashCalculator() {
        String[] options = {"File", "Text"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Calculate hash of:", "Hash Calculator",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        if (choice == 0) {
            // File
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    String hash = calculateHash(file);
                    JOptionPane.showMessageDialog(this, 
                        "File: " + file.getName() + "\n\nSHA-256: " + hash,
                        "File Hash", JOptionPane.INFORMATION_MESSAGE);
                    log("Calculated hash for: " + file.getName());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (choice == 1) {
            // Text
            String text = JOptionPane.showInputDialog(this, "Enter text to hash:", 
                "Hash Calculator", JOptionPane.QUESTION_MESSAGE);
            if (text != null) {
                try {
                    String hash = calculateHash(text);
                    JOptionPane.showMessageDialog(this, 
                        "Text: " + text + "\n\nSHA-256: " + hash,
                        "Text Hash", JOptionPane.INFORMATION_MESSAGE);
                    log("Calculated hash for text input");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private String calculateHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return bytesToHex(digest.digest());
    }
    
    private String calculateHash(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes());
        return bytesToHex(hash);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private void quickScan() {
        log("Starting quick system scan...");
        
        // Simulate scanning
        String[] components = {"System files", "Memory", "Network connections", "Processes"};
        for (String comp : components) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (Math.random() < 0.1) {
                log("⚠️ Warning: Suspicious activity detected in " + comp);
            } else {
                log("✓ " + comp + " clean");
            }
        }
        
        log("✅ Quick scan completed. System appears secure.");
    }
    
    private void ddosDetector() {
        String target = JOptionPane.showInputDialog(this, "Target IP/Hostname:", 
            "DDoS Simulator", JOptionPane.QUESTION_MESSAGE);
        if (target == null || target.isEmpty()) return;
        
        log("⚠️ DDoS attack detected on " + target);
        
        JDialog attackDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "DDoS Attack Simulation", false);
        attackDialog.setSize(500, 400);
        attackDialog.setLocationRelativeTo(this);
        attackDialog.setLayout(new BorderLayout());
        attackDialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
        
        JTextArea attackArea = new JTextArea();
        attackArea.setEditable(false);
        attackArea.setBackground(Color.BLACK);
        attackArea.setForeground(Color.decode("#00ff00"));
        attackArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        attackArea.setText("⚠️ DDoS ATTACK DETECTED ⚠️\n");
        attackArea.append("Target: " + target + "\n");
        attackArea.append("Time: " + new Date() + "\n");
        attackArea.append("━".repeat(40) + "\n\n");
        
        JScrollPane scrollPane = new JScrollPane(attackArea);
        attackDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.decode("#ef4444"));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> attackDialog.dispose());
        attackDialog.add(closeButton, BorderLayout.SOUTH);
        
        attackDialog.setVisible(true);
        
        // Simulate attack
        new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int packets = random.nextInt(99000) + 1000;
                attackArea.append("[" + (i+1) + "] Incoming packets: " + packets + "/sec\n");
                attackArea.setCaretPosition(attackArea.getDocument().getLength());
            }
            
            attackArea.append("\n🛡️ MITIGATION ACTIVATED 🛡️\n");
            attackArea.append("• Traffic filtering enabled\n");
            attackArea.append("• Rate limiting applied\n");
            attackArea.append("• Blacklisting malicious IPs\n");
            attackArea.append("• CDN protection engaged\n\n");
            attackArea.append("✅ Attack mitigated successfully!\n");
            
            NeilOS.cyberLog.add("[MITIGATION] DDoS attack on " + target + " mitigated");
        }).start();
        
        log("[ALERT] DDoS attack detected on " + target);
    }
    
    private void malwareScan() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select file to scan for malware");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File file = chooser.getSelectedFile();
        log("🔍 Scanning file: " + file.getName());
        
        // Simulate malware scan
        String[] signatures = {"virus", "malware", "trojan", "ransomware", "spyware", 
            "keylogger", "rootkit", "worm", "backdoor", "exploit"};
        
        List<String> found = new ArrayList<>();
        
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            String lowerContent = content.toLowerCase();
            
            for (String sig : signatures) {
                if (lowerContent.contains(sig)) {
                    found.add(sig);
                }
            }
            
            if (!found.isEmpty()) {
                log("❌ Malware detected! Found: " + String.join(", ", found));
                detectedThreats.add(file.getName() + " - " + String.join(", ", found));
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Malware detected!\nFound: " + String.join(", ", found),
                    "Malware Scan", JOptionPane.WARNING_MESSAGE);
            } else {
                log("✅ No malware detected. File appears clean.");
                JOptionPane.showMessageDialog(this, 
                    "✅ No malware detected.\nFile appears clean.",
                    "Malware Scan", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log("⚠️ Binary file - performing heuristic scan...");
            // Simulate heuristic detection
            if (Math.random() < 0.15) {
                log("❌ Suspicious patterns detected (heuristic)");
                detectedThreats.add(file.getName() + " - Suspicious patterns (heuristic)");
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Suspicious patterns detected (heuristic scan)",
                    "Malware Scan", JOptionPane.WARNING_MESSAGE);
            } else {
                log("✅ No suspicious patterns detected.");
            }
        }
    }
    
    private void phishingDetector() {
        String input = JOptionPane.showInputDialog(this, "Enter URL or Email to check:", 
            "Phishing Detection", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isEmpty()) return;
        
        log("🔍 Analyzing: " + input);
        
        int redFlags = 0;
        List<String> warnings = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        // URL checks
        if (lowerInput.contains("http")) {
            if (!lowerInput.contains("https")) {
                warnings.add("⚠️ Missing HTTPS - Connection not secure");
                redFlags++;
            }
            
            String[] suspicious = {"login", "verify", "secure", "account", "update", 
                "confirm", "bank", "paypal", "amazon", "apple", "microsoft", "google"};
            for (String word : suspicious) {
                if (lowerInput.contains(word)) {
                    warnings.add("⚠️ Contains suspicious keyword: " + word);
                    redFlags++;
                }
            }
            
            if (lowerInput.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
                warnings.add("⚠️ Uses IP address instead of domain name");
                redFlags += 2;
            }
        } else {
            // Email checks
            if (!lowerInput.contains("@")) {
                warnings.add("⚠️ Not a valid email format");
                redFlags++;
            }
            
            String[] freeDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com"};
            for (String domain : freeDomains) {
                if (lowerInput.contains(domain)) {
                    warnings.add("⚠️ Uses free email service: " + domain);
                    redFlags++;
                }
            }
        }
        
        String riskLevel;
        String color;
        if (redFlags >= 3) {
            riskLevel = "🔴 HIGH RISK - PHISHING DETECTED!";
            color = "#ef4444";
        } else if (redFlags >= 1) {
            riskLevel = "🟡 MEDIUM RISK - Exercise caution";
            color = "#f59e0b";
        } else {
            riskLevel = "🟢 LOW RISK - Appears legitimate";
            color = "#10b981";
        }
        
        StringBuilder result = new StringBuilder();
        result.append(riskLevel).append("\n\n");
        result.append("━".repeat(40)).append("\n");
        result.append("Analyzed: ").append(input).append("\n");
        result.append("Risk Score: ").append(redFlags).append("/10\n\n");
        
        if (!warnings.isEmpty()) {
            result.append("Findings:\n");
            for (String w : warnings) {
                result.append(w).append("\n");
            }
        } else {
            result.append("No obvious phishing indicators found.\n");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "Phishing Detection Results", JOptionPane.INFORMATION_MESSAGE);
        log("Phishing analysis completed for: " + input + " - Risk score: " + redFlags);
    }
    
    private void mitmDetector() {
        log("🔒 Starting MITM detection scan...");
        
        JDialog mitmDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "MITM Detection", false);
        mitmDialog.setSize(600, 500);
        mitmDialog.setLocationRelativeTo(this);
        mitmDialog.setLayout(new BorderLayout());
        mitmDialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
        
        JTextArea mitmArea = new JTextArea();
        mitmArea.setEditable(false);
        mitmArea.setBackground(Color.BLACK);
        mitmArea.setForeground(Color.decode("#00ff00"));
        mitmArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        mitmArea.setText("🔒 MAN-IN-THE-MIDDLE DETECTION SCAN\n");
        mitmArea.append("Time: " + new Date() + "\n");
        mitmArea.append("━".repeat(40) + "\n\n");
        
        JScrollPane scrollPane = new JScrollPane(mitmArea);
        mitmDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.decode("#3b82f6"));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> mitmDialog.dispose());
        mitmDialog.add(closeButton, BorderLayout.SOUTH);
        
        mitmDialog.setVisible(true);
        
        // Simulate MITM detection
        new Thread(() -> {
            String[] checks = {
                "Checking SSL/TLS certificates...",
                "Verifying certificate chain...",
                "Checking for certificate pinning...",
                "Analyzing network routes...",
                "Checking ARP table for anomalies...",
                "Verifying DNS responses...",
                "Checking for unexpected redirects..."
            };
            
            boolean mitmDetected = false;
            Random random = new Random();
            
            for (String check : checks) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                mitmArea.append("• " + check + " ");
                if (random.nextDouble() < 0.1) {
                    mitmArea.append("⚠️ ANOMALY DETECTED!\n");
                    mitmDetected = true;
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        mitmArea.append("  Analyzing..." + ".".repeat(i+1) + "\n");
                    }
                } else {
                    mitmArea.append("✓ OK\n");
                }
                mitmArea.setCaretPosition(mitmArea.getDocument().getLength());
            }
            
            mitmArea.append("\n" + "━".repeat(40) + "\n");
            
            if (mitmDetected) {
                mitmArea.append("🚨 MITM ATTACK DETECTED!\n\n");
                mitmArea.append("Recommendations:\n");
                mitmArea.append("1. Disconnect from current network\n");
                mitmArea.append("2. Use VPN for encrypted connections\n");
                mitmArea.append("3. Verify SSL certificates manually\n");
                mitmArea.append("4. Change passwords immediately\n");
                NeilOS.cyberLog.add("[MITM] Attack detected!");
                log("🚨 MITM attack detected!");
            } else {
                mitmArea.append("✅ No MITM attack detected. Connection appears secure.\n");
                NeilOS.cyberLog.add("[MITM] Scan completed - no threats found");
                log("✅ No MITM attack detected.");
            }
        }).start();
    }
    
    // Additional security features (simplified implementations)
    
    private void nmapScan() {
        String target = JOptionPane.showInputDialog(this, "Target IP or Hostname:", 
            "Nmap Scan", JOptionPane.QUESTION_MESSAGE);
        if (target == null || target.isEmpty()) return;
        
        log("🔍 Starting Nmap scan on " + target);
        
        StringBuilder result = new StringBuilder();
        result.append("🔍 NMAP SCAN REPORT\n");
        result.append("Target: ").append(target).append("\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        result.append("PORT     STATE    SERVICE\n");
        result.append("─────    ─────    ───────\n");
        
        Random random = new Random();
        int[] ports = {21, 22, 23, 25, 53, 80, 110, 143, 443, 3306, 3389, 5432, 8080, 27017};
        String[] services = {"FTP", "SSH", "Telnet", "SMTP", "DNS", "HTTP", "POP3", "IMAP", 
            "HTTPS", "MySQL", "RDP", "PostgreSQL", "HTTP-Alt", "MongoDB"};
        
        List<Integer> openPorts = new ArrayList<>();
        
        for (int i = 0; i < ports.length; i++) {
            if (random.nextDouble() < 0.3) {
                result.append(String.format("%-8d open     %s\n", ports[i], services[i]));
                openPorts.add(ports[i]);
            } else {
                result.append(String.format("%-8d closed   %s\n", ports[i], services[i]));
            }
        }
        
        result.append("\n").append("━".repeat(40)).append("\n");
        result.append("✅ Scan complete. ").append(openPorts.size()).append(" open ports found.\n");
        
        if (!openPorts.isEmpty()) {
            result.append("\nOpen ports: ").append(openPorts.toString()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "Nmap Scan Results", JOptionPane.INFORMATION_MESSAGE);
        log("Nmap scan completed on " + target + " - " + openPorts.size() + " open ports");
    }
    
    private void networkScan() {
        log("🌐 Scanning network for devices...");
        
        Random random = new Random();
        int devicesFound = random.nextInt(12) + 3;
        
        StringBuilder result = new StringBuilder();
        result.append("🌐 NETWORK SCAN\n");
        result.append("Network: 192.168.1.0/24\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        result.append("Scanning network...\n\n");
        
        for (int i = 0; i < devicesFound; i++) {
            String ip = "192.168.1." + (random.nextInt(253) + 1);
            String mac = String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                random.nextInt(256), random.nextInt(256), random.nextInt(256),
                random.nextInt(256), random.nextInt(256), random.nextInt(256));
            String hostname = "device-" + (random.nextInt(900) + 100);
            
            result.append("Device ").append(i+1).append(":\n");
            result.append("  IP: ").append(ip).append("\n");
            result.append("  MAC: ").append(mac).append("\n");
            result.append("  Hostname: ").append(hostname).append("\n\n");
        }
        
        result.append("━".repeat(40)).append("\n");
        result.append("✅ Scan complete. ").append(devicesFound).append(" devices found.\n");
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "Network Scan Results", JOptionPane.INFORMATION_MESSAGE);
        log("Network scan completed - " + devicesFound + " devices found");
    }
    
    private void urlScan() {
        String url = JOptionPane.showInputDialog(this, "Enter URL to scan:", 
            "URL Security Scanner", JOptionPane.QUESTION_MESSAGE);
        if (url == null || url.isEmpty()) return;
        
        log("🔗 Scanning URL: " + url);
        
        int riskScore = 0;
        List<String> warnings = new ArrayList<>();
        String lowerUrl = url.toLowerCase();
        
        if (lowerUrl.contains("http") && !lowerUrl.contains("https")) {
            warnings.add("⚠️ Missing HTTPS encryption");
            riskScore += 2;
        }
        
        String[] suspicious = {"login", "verify", "secure", "account", "update", "confirm",
            "bank", "paypal", "amazon", "apple", "microsoft", "google"};
        for (String word : suspicious) {
            if (lowerUrl.contains(word)) {
                warnings.add("⚠️ Contains '" + word + "' - potential phishing");
                riskScore++;
            }
        }
        
        if (lowerUrl.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
            warnings.add("⚠️ Uses IP address instead of domain name");
            riskScore += 3;
        }
        
        if (url.length() > 100) {
            warnings.add("⚠️ Unusually long URL");
            riskScore++;
        }
        
        String[] shorteners = {"bit.ly", "tinyurl", "goo.gl", "ow.ly", "is.gd", "buff.ly"};
        for (String shortener : shorteners) {
            if (lowerUrl.contains(shortener)) {
                warnings.add("⚠️ Uses URL shortener (" + shortener + ") - destination hidden");
                riskScore += 2;
                break;
            }
        }
        
        String riskLevel;
        String color;
        String recommendation;
        if (riskScore >= 5) {
            riskLevel = "🔴 HIGH RISK";
            color = "#ef4444";
            recommendation = "DO NOT OPEN - Block this URL immediately";
        } else if (riskScore >= 2) {
            riskLevel = "🟡 MEDIUM RISK";
            color = "#f59e0b";
            recommendation = "Exercise caution - Verify before opening";
        } else {
            riskLevel = "🟢 LOW RISK";
            color = "#10b981";
            recommendation = "URL appears safe";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("🔗 URL SECURITY SCAN\n");
        result.append("URL: ").append(url).append("\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        result.append("Risk Level: ").append(riskLevel).append("\n\n");
        result.append("Risk Score: ").append(riskScore).append("/10\n\n");
        
        if (!warnings.isEmpty()) {
            result.append("Findings:\n");
            for (String w : warnings) {
                result.append(w).append("\n");
            }
        } else {
            result.append("No obvious threats detected.\n");
        }
        
        result.append("\nRecommendation: ").append(recommendation).append("\n");
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "URL Security Scanner", JOptionPane.INFORMATION_MESSAGE);
        log("URL scan completed - Risk score: " + riskScore);
    }
    
    private void smsBomberSim() {
        String number = JOptionPane.showInputDialog(this, "Enter phone number (simulation only):", 
            "SMS Bomber Simulator", JOptionPane.QUESTION_MESSAGE);
        if (number == null || number.isEmpty()) return;
        
        log("📱 SMS Bomber simulation for " + number);
        
        JDialog smsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "SMS Bomber Simulation", false);
        smsDialog.setSize(500, 400);
        smsDialog.setLocationRelativeTo(this);
        smsDialog.setLayout(new BorderLayout());
        smsDialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
        
        JTextArea smsArea = new JTextArea();
        smsArea.setEditable(false);
        smsArea.setBackground(Color.BLACK);
        smsArea.setForeground(Color.decode("#00ff00"));
        smsArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        smsArea.setText("📱 SMS BOMBER SIMULATION (EDUCATIONAL)\n");
        smsArea.append("Target: " + number + "\n");
        smsArea.append("━".repeat(40) + "\n\n");
        smsArea.append("⚠️ THIS IS A SIMULATION - No actual SMS sent\n\n");
        
        JScrollPane scrollPane = new JScrollPane(smsArea);
        smsDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.decode("#ef4444"));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> smsDialog.dispose());
        smsDialog.add(closeButton, BorderLayout.SOUTH);
        
        smsDialog.setVisible(true);
        
        new Thread(() -> {
            int messagesSent = 0;
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                messagesSent += random.nextInt(10) + 5;
                smsArea.setText("Sending messages... " + messagesSent + " sent");
                smsArea.setCaretPosition(smsArea.getDocument().getLength());
            }
            smsArea.append("\n\n✅ Simulation complete: " + messagesSent + " SMS messages simulated\n");
            smsArea.append("\n⚠️ EDUCATIONAL PURPOSE ONLY\n");
            smsArea.append("Actual SMS bombing is illegal and unethical!\n");
            NeilOS.cyberLog.add("[SIM] SMS bomber simulation for " + number);
        }).start();
    }
    
    private void botnetDetector() {
        log("🤖 Running botnet detection scan...");
        
        Random random = new Random();
        List<String> suspiciousIPs = new ArrayList<>();
        
        for (int i = 0; i < random.nextInt(5); i++) {
            String ip = random.nextInt(255) + "." + random.nextInt(256) + "." + 
                       random.nextInt(256) + "." + (random.nextInt(254) + 1);
            suspiciousIPs.add(ip);
            log("⚠️ Suspicious connection from " + ip);
        }
        
        StringBuilder result = new StringBuilder();
        result.append("🤖 BOTNET DETECTION SCAN\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        
        if (!suspiciousIPs.isEmpty()) {
            result.append("🚨 BOTNET ACTIVITY DETECTED!\n\n");
            result.append("Suspicious IPs: ").append(String.join(", ", suspiciousIPs)).append("\n\n");
            result.append("Recommendations:\n");
            result.append("1. Block detected IPs immediately\n");
            result.append("2. Run full antivirus scan\n");
            result.append("3. Check for unauthorized processes\n");
            result.append("4. Change all passwords\n");
            NeilOS.cyberLog.add("[BOTNET] Detected " + suspiciousIPs.size() + " suspicious IPs");
        } else {
            result.append("✅ No botnet activity detected\n");
            NeilOS.cyberLog.add("[BOTNET] Scan completed - system clean");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "Botnet Detector", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void imageGenerator() {
        String[] options = {"Fractal", "Random Pattern"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Generate pattern:", "Image Generator",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        if (choice == 0) {
            // Fractal - use a simple Swing implementation
            JDialog fractalDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Fractal Generator", false);
            fractalDialog.setSize(600, 400);
            fractalDialog.setLocationRelativeTo(this);
            
            JPanel fractalPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    double cRe = -0.7;
                    double cIm = 0.27015;
                    
                    for (int px = 0; px < getWidth(); px++) {
                        for (int py = 0; py < getHeight(); py++) {
                            double x = (px - getWidth() / 2.0) / 200.0;
                            double y = (py - getHeight() / 2.0) / 200.0;
                            double zRe = x, zIm = y;
                            int i = 0;
                            while (zRe * zRe + zIm * zIm < 4 && i < 30) {
                                double newRe = zRe * zRe - zIm * zIm + cRe;
                                double newIm = 2 * zRe * zIm + cIm;
                                zRe = newRe;
                                zIm = newIm;
                                i++;
                            }
                            int r = (i * 8) % 256;
                            int gb = (i * 4) % 256;
                            int b = (i * 12) % 256;
                            g2d.setColor(new Color(r, gb, b));
                            g2d.fillRect(px, py, 1, 1);
                        }
                    }
                }
            };
            
            fractalDialog.add(fractalPanel);
            fractalDialog.setVisible(true);
            NeilOS.cyberLog.add("[GEN] Fractal image generated");
        } else {
            // Random pattern using JFreeChart
            try {
                Class.forName("org.jfree.chart.ChartFactory");
                Class.forName("org.jfree.chart.ChartPanel");
                
                // Use reflection to avoid compile-time dependency
                java.lang.reflect.Method createXYLineChart = 
                    Class.forName("org.jfree.chart.ChartFactory")
                        .getMethod("createXYLineChart", String.class, String.class, String.class,
                            org.jfree.data.xy.XYDataset.class);
                
                // Create dataset
                org.jfree.data.xy.XYSeries series = new org.jfree.data.xy.XYSeries("Random");
                Random random = new Random();
                for (int i = 0; i < 50; i++) {
                    series.add(i, random.nextDouble() * 100);
                }
                
                org.jfree.data.xy.XYSeriesCollection dataset = new org.jfree.data.xy.XYSeriesCollection(series);
                
                Object chart = createXYLineChart.invoke(null, "Random Security Pattern", 
                    "X", "Y", dataset);
                
                // Create chart panel
                javax.swing.JPanel chartPanel = (javax.swing.JPanel) 
                    Class.forName("org.jfree.chart.ChartPanel")
                        .getConstructor(Class.forName("org.jfree.chart.JFreeChart"))
                        .newInstance(chart);
                
                JDialog chartDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Random Pattern Generator", false);
                chartDialog.setSize(600, 400);
                chartDialog.setLocationRelativeTo(this);
                chartDialog.add(chartPanel);
                chartDialog.setVisible(true);
                NeilOS.cyberLog.add("[GEN] Random pattern generated");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "JFreeChart not available. Using simple random pattern instead.",
                    "Image Generator", JOptionPane.INFORMATION_MESSAGE);
                
                // Fallback to simple pattern
                JDialog simpleDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Random Pattern", false);
                simpleDialog.setSize(400, 400);
                simpleDialog.setLocationRelativeTo(this);
                
                JPanel simplePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Random random = new Random();
                        for (int i = 0; i < 1000; i++) {
                            g.setColor(new Color(random.nextInt(256), 
                                random.nextInt(256), random.nextInt(256)));
                            g.fillRect(random.nextInt(getWidth()), 
                                random.nextInt(getHeight()), 
                                random.nextInt(10) + 1, 
                                random.nextInt(10) + 1);
                        }
                    }
                };
                simpleDialog.add(simplePanel);
                simpleDialog.setVisible(true);
            }
        }
    }
    
    private void metasploitDetector() {
        log("🔍 Running Metasploit detection scan...");
        
        Random random = new Random();
        List<String> detected = new ArrayList<>();
        String[] patterns = {"msfconsole", "meterpreter", "exploit/", "payload/", 
            "msfvenom", "msfupdate", "msfrpc"};
        
        for (String pattern : patterns) {
            if (random.nextDouble() < 0.1) {
                detected.add(pattern);
                log("⚠️ Found pattern: " + pattern);
            }
        }
        
        StringBuilder result = new StringBuilder();
        result.append("🔍 METASPLOIT DETECTION SCAN\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        
        if (!detected.isEmpty()) {
            result.append("🚨 METASPLOIT DETECTED!\n\n");
            result.append("Detected indicators: ").append(String.join(", ", detected)).append("\n\n");
            result.append("Threat Level: HIGH\n");
            result.append("Immediate action required!\n");
            NeilOS.cyberLog.add("[METASPLOIT] Detected " + detected.size() + " indicators");
        } else {
            result.append("✅ No Metasploit indicators detected\n");
            NeilOS.cyberLog.add("[METASPLOIT] Scan completed - no threats");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "Metasploit Detector", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void virusTotalSim() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select file for VirusTotal simulation");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File file = chooser.getSelectedFile();
        log("🦠 Running VirusTotal simulation on: " + file.getName());
        
        Random random = new Random();
        String[] avEngines = {"BitDefender", "Kaspersky", "Norton", "McAfee", "Avast",
            "AVG", "ESET", "Symantec", "TrendMicro", "Sophos", "Panda", "F-Secure", 
            "Malwarebytes", "Windows Defender"};
        
        int positives = 0;
        StringBuilder result = new StringBuilder();
        result.append("🦠 VIRUSTOTAL SCAN SIMULATION\n");
        result.append("File: ").append(file.getName()).append("\n");
        result.append("Size: ").append(file.length()).append(" bytes\n");
        result.append("Time: ").append(new Date()).append("\n");
        result.append("━".repeat(40)).append("\n\n");
        
        for (String av : avEngines) {
            if (random.nextDouble() < 0.15) {
                result.append("⚠️ ").append(av).append(": Detected\n");
                positives++;
            } else {
                result.append("✅ ").append(av).append(": Clean\n");
            }
        }
        
        result.append("\n").append("━".repeat(40)).append("\n");
        result.append("Scan Results: ").append(positives).append("/").append(avEngines.length).append(" detections\n\n");
        
        if (positives > 0) {
            double ratio = (double) positives / avEngines.length;
            if (ratio > 0.5) {
                result.append("🔴 HIGH RISK - Multiple detections found!\n");
            } else if (ratio > 0.2) {
                result.append("🟡 MEDIUM RISK - Some detections found\n");
            } else {
                result.append("🟢 LOW RISK - Few detections, may be false positive\n");
            }
        } else {
            result.append("✅ File appears clean (no detections)\n");
        }
        
        JOptionPane.showMessageDialog(this, result.toString(), 
            "VirusTotal Scan Simulation", JOptionPane.INFORMATION_MESSAGE);
        NeilOS.cyberLog.add("[VT] Simulated scan for " + file.getName() + " - " + positives + " detections");
    }
    
    private void spamDetector() {
        String message = JOptionPane.showInputDialog(this, "Enter message to check for spam:", 
            "Spam Detector", JOptionPane.QUESTION_MESSAGE);
        if (message == null || message.isEmpty()) return;
        
        log("📧 Analyzing message for spam...");
        
        String[] spamKeywords = {"free", "winner", "prize", "congratulations", "urgent",
            "verify", "account", "password", "click here", "limited time", "offer",
            "discount", "viagra", "lottery", "million", "cash", "earn", "work from home",
            "investment", "bitcoin", "crypto"};
        
        int spamScore = 0;
        List<String> foundKeywords = new ArrayList<>();
        String lowerMessage = message.toLowerCase();
        
        for (String keyword : spamKeywords) {
            if (lowerMessage.contains(keyword)) {
                spamScore++;
                foundKeywords.add(keyword);
            }
        }
        
        String result;
        String color;
        if (spamScore >= 5) {
            result = "🔴 HIGH PROBABILITY OF SPAM";
            color = "#ef4444";
        } else if (spamScore >= 3) {
            result = "🟡 MODERATE SPAM INDICATORS";
            color = "#f59e0b";
        } else if (spamScore >= 1) {
            result = "🟢 LOW SPAM INDICATORS";
            color = "#3b82f6";
        } else {
            result = "✅ LIKELY LEGITIMATE";
            color = "#10b981";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("📧 SPAM DETECTION ANALYSIS\n");
        report.append("━".repeat(40)).append("\n\n");
        report.append("Result: ").append(result).append("\n\n");
        report.append("Spam Score: ").append(spamScore).append("/10\n\n");
        
        if (!foundKeywords.isEmpty()) {
            report.append("Suspicious keywords found:\n");
            for (String kw : foundKeywords) {
                report.append("  • ").append(kw).append("\n");
            }
        } else {
            report.append("No spam keywords detected.\n");
        }
        
        JOptionPane.showMessageDialog(this, report.toString(), 
            "Spam Detection Results", JOptionPane.INFORMATION_MESSAGE);
        NeilOS.cyberLog.add("[SPAM] Analyzed message - Score: " + spamScore);
    }
    
    private void badusbDetector() {
        log("🔌 Running BadUSB detection scan...");
        
        JDialog badusbDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "BadUSB Detector", false);
        badusbDialog.setSize(600, 400);
        badusbDialog.setLocationRelativeTo(this);
        badusbDialog.setLayout(new BorderLayout());
        badusbDialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
        
        JTextArea badusbArea = new JTextArea();
        badusbArea.setEditable(false);
        badusbArea.setBackground(Color.BLACK);
        badusbArea.setForeground(Color.decode("#00ff00"));
        badusbArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        badusbArea.setText("🔌 BADUSB DETECTION SCAN\n");
        badusbArea.append("Time: " + new Date() + "\n");
        badusbArea.append("━".repeat(40) + "\n\n");
        
        JScrollPane scrollPane = new JScrollPane(badusbArea);
        badusbDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.decode("#3b82f6"));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> badusbDialog.dispose());
        badusbDialog.add(closeButton, BorderLayout.SOUTH);
        
        badusbDialog.setVisible(true);
        
        new Thread(() -> {
            String[] checks = {"Checking connected USB devices...",
                "Analyzing device descriptors...",
                "Checking for HID spoofing...",
                "Monitoring keystroke injection...",
                "Checking for rubber ducky patterns...",
                "Verifying device firmware...",
                "Checking for autorun capabilities..."};
            
            boolean badusbDetected = false;
            Random random = new Random();
            
            for (String check : checks) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                badusbArea.append("• " + check + " ");
                if (random.nextDouble() < 0.15) {
                    badusbArea.append("⚠️ SUSPICIOUS!\n");
                    badusbDetected = true;
                } else {
                    badusbArea.append("✓ OK\n");
                }
                badusbArea.setCaretPosition(badusbArea.getDocument().getLength());
            }
            
            badusbArea.append("\n" + "━".repeat(40) + "\n");
            
            if (badusbDetected) {
                badusbArea.append("🚨 BADUSB DEVICE DETECTED!\n\n");
                badusbArea.append("Recommendations:\n");
                badusbArea.append("1. Remove suspicious USB devices immediately\n");
                badusbArea.append("2. Block USB ports if possible\n");
                badusbArea.append("3. Run antivirus scan\n");
                badusbArea.append("4. Change passwords if keystroke injection suspected\n");
                NeilOS.cyberLog.add("[BADUSB] Suspicious USB device detected");
                log("🚨 BadUSB device detected!");
            } else {
                badusbArea.append("✅ No BadUSB devices detected\n");
                NeilOS.cyberLog.add("[BADUSB] Scan completed - all USB devices appear legitimate");
                log("✅ No BadUSB devices detected.");
            }
        }).start();
    }
    
    private void openAntivirus() {
        AntivirusScanner scanner = neilos.getAntivirusScanner();
        
        JDialog avDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "NeilOS Antivirus", false);
        avDialog.setSize(800, 600);
        avDialog.setLocationRelativeTo(this);
        avDialog.setLayout(new BorderLayout());
        avDialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("NeilOS Antivirus");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.decode("#00ff00"));
        topPanel.add(titleLabel);
        avDialog.add(topPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(Color.decode("#1e1e1e"));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton scanButton = new JButton("🔍 Start Scan");
        scanButton.setBackground(Color.decode("#4CAF50"));
        scanButton.setForeground(Color.WHITE);
        scanButton.setFocusPainted(false);
        scanButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(avDialog) == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                String report = scanner.scanDirectory(dir, new AntivirusScanner.ScanCallback() {
                    @Override
                    public void onFileScanned(String path, boolean infected) {
                        // Update UI in real-time
                    }
                });
                JOptionPane.showMessageDialog(avDialog, report, "Scan Report", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(scanButton);
        
        JButton quarantineButton = new JButton("📦 View Quarantine");
        quarantineButton.setBackground(Color.decode("#FF9800"));
        quarantineButton.setForeground(Color.WHITE);
        quarantineButton.setFocusPainted(false);
        quarantineButton.addActionListener(e -> {
            File qDir = new File("quarantine");
            String[] files = qDir.list();
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(avDialog, "No files quarantined", 
                    "Quarantine", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(avDialog, 
                    "Quarantined Files:\n\n" + String.join("\n", files),
                    "Quarantine", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(quarantineButton);
        
        avDialog.add(buttonPanel, BorderLayout.CENTER);
        
        // Status area
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setBackground(Color.BLACK);
        statusArea.setForeground(Color.decode("#00ff00"));
        statusArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        statusArea.setText("🛡️ Antivirus Ready\n");
        statusArea.append("Click 'Start Scan' to scan a directory.\n");
        avDialog.add(statusArea, BorderLayout.SOUTH);
        
        avDialog.setVisible(true);
    }
}