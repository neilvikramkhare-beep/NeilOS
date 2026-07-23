package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Kernel Manager - System management and control center
 * Handles system diagnostics, reboot, shutdown, file management, and logs
 */
public class KernelApp extends JPanel {
    private NeilOS neilos;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Random random;
    private Timer animationTimer;
    private int animationProgress;
    
    // System information
    private String osName;
    private String osVersion;
    private String osArch;
    private String javaVersion;
    private String userName;
    private String userHome;
    private int availableProcessors;
    private long maxMemory;
    private long totalMemory;
    private long freeMemory;
    
    public KernelApp(NeilOS neilos) {
        this.neilos = neilos;
        this.random = new Random();
        this.animationProgress = 0;
        
        // Gather system information
        gatherSystemInfo();
        
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        showKernelInfo();
    }
    
    /**
     * Gather system information on initialization
     */
    private void gatherSystemInfo() {
        osName = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");
        osArch = System.getProperty("os.arch");
        javaVersion = System.getProperty("java.version");
        userName = System.getProperty("user.name");
        userHome = System.getProperty("user.home");
        availableProcessors = Runtime.getRuntime().availableProcessors();
        maxMemory = Runtime.getRuntime().maxMemory();
        totalMemory = Runtime.getRuntime().totalMemory();
        freeMemory = Runtime.getRuntime().freeMemory();
    }
    
    /**
     * Create the user interface
     */
    private void createUI() {
        // Top panel with action buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Diagnostics button
        JButton diagBtn = createButton("⚙ Diagnostics", "#3b82f6", e -> runDiagnostics());
        topPanel.add(diagBtn);
        
        // Reboot button
        JButton rebootBtn = createButton("🔄 Reboot", "#f59e0b", e -> rebootSystem());
        topPanel.add(rebootBtn);
        
        // Shutdown button
        JButton shutdownBtn = createButton("⏻ Shutdown", "#ef4444", e -> shutdownSystem());
        topPanel.add(shutdownBtn);
        
        // View Files button
        JButton filesBtn = createButton("📁 View Files", "#10b981", e -> viewFiles());
        topPanel.add(filesBtn);
        
        // View Logs button
        JButton logsBtn = createButton("📋 View Logs", "#8b5cf6", e -> viewLogs());
        topPanel.add(logsBtn);
        
        // Clear Logs button
        JButton clearLogsBtn = createButton("🗑 Clear Logs", "#ec4899", e -> clearLogs());
        topPanel.add(clearLogsBtn);
        
        // System Info button
        JButton infoBtn = createButton("ℹ System Info", "#06b6d4", e -> showSystemInfo());
        topPanel.add(infoBtn);
        
        // Status label
        statusLabel = new JLabel("🟢 Kernel: Running");
        statusLabel.setForeground(Color.decode("#10b981"));
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        topPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with progress bar and log area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode("#101826"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.decode("#3b82f6"));
        progressBar.setBackground(Color.decode("#1e1e1e"));
        progressBar.setVisible(false);
        centerPanel.add(progressBar, BorderLayout.NORTH);
        
        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.decode("#00ff00"));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#1e1e1e"), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with quick stats
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.decode("#0f172a"));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statsLabel = new JLabel("💻 " + osName + " | 🧠 " + availableProcessors + " cores | 💾 " + formatBytes(totalMemory));
        statsLabel.setForeground(Color.decode("#a0aec0"));
        statsLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        bottomPanel.add(statsLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Helper method to create styled buttons
     */
    private JButton createButton(String text, String color, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }
    
    /**
     * Display kernel information and recent logs
     */
    private void showKernelInfo() {
        logArea.setText("");
        append("╔══════════════════════════════════════════════════════════╗\n");
        append("║                    ⚙ KERNEL MANAGER                    ║\n");
        append("╚══════════════════════════════════════════════════════════╝\n\n");
        
        append("📌 SYSTEM INFORMATION\n");
        append("┌─────────────────────────────────────────────────────────────┐\n");
        append("│  OS:          " + padRight(osName, 40) + "│\n");
        append("│  OS Version:  " + padRight(osVersion, 40) + "│\n");
        append("│  Architecture:" + padRight(osArch, 40) + "│\n");
        append("│  Java:        " + padRight(javaVersion, 40) + "│\n");
        append("│  User:        " + padRight(userName, 40) + "│\n");
        append("│  Cores:       " + padRight(String.valueOf(availableProcessors), 40) + "│\n");
        append("│  Memory:      " + padRight(formatBytes(maxMemory), 40) + "│\n");
        append("└─────────────────────────────────────────────────────────────┘\n\n");
        
        append("🚀 KERNEL STATUS: RUNNING\n");
        append("💡 Use the buttons above for system management\n\n");
        
        append("📋 RECENT KERNEL LOGS:\n");
        append("┌─────────────────────────────────────────────────────────────┐\n");
        
        if (NeilOS.kernelLogs.isEmpty()) {
            append("│  No kernel logs available.                              │\n");
        } else {
            int start = Math.max(0, NeilOS.kernelLogs.size() - 15);
            for (int i = start; i < NeilOS.kernelLogs.size(); i++) {
                String log = NeilOS.kernelLogs.get(i);
                if (log.length() > 47) {
                    log = log.substring(0, 44) + "...";
                }
                append("│  " + padRight(log, 47) + "│\n");
            }
        }
        append("└─────────────────────────────────────────────────────────────┘\n");
        
        logArea.setCaretPosition(0);
    }
    
    /**
     * Run system diagnostics
     */
    private void runDiagnostics() {
        append("\n🔍 RUNNING SYSTEM DIAGNOSTICS...\n");
        append("┌─────────────────────────────────────────────────────────────┐\n");
        
        progressBar.setVisible(true);
        progressBar.setValue(0);
        statusLabel.setText("🟡 Kernel: Running Diagnostics");
        statusLabel.setForeground(Color.decode("#f59e0b"));
        
        String[] tests = {
            "CPU Test", "Memory Test", "Storage Test", "Network Test",
            "Security Test", "Filesystem Test", "Process Check", "System Integrity",
            "Kernel Modules", "Device Drivers", "System Logs", "Performance Check"
        };
        
        String[] testDescriptions = {
            "Testing CPU performance and temperature...",
            "Checking memory allocation and usage...",
            "Verifying storage devices and partitions...",
            "Testing network connectivity and interfaces...",
            "Checking security policies and permissions...",
            "Verifying filesystem integrity...",
            "Checking running processes and threads...",
            "Validating system file integrity...",
            "Checking loaded kernel modules...",
            "Verifying device drivers...",
            "Analyzing system logs for errors...",
            "Benchmarking system performance..."
        };
        
        int passed = 0;
        int warnings = 0;
        int failed = 0;
        
        for (int i = 0; i < tests.length; i++) {
            try {
                Thread.sleep(300 + random.nextInt(300));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            double result = random.nextDouble();
            int progress = (i + 1) * 100 / tests.length;
            progressBar.setValue(progress);
            
            String status;
            if (result < 0.85) {
                status = "✅ PASSED";
                passed++;
                append("│  " + padRight("[" + (i+1) + "] " + tests[i] + ": " + testDescriptions[i], 55) + status + " │\n");
            } else if (result < 0.95) {
                status = "⚠️ WARNING";
                warnings++;
                append("│  " + padRight("[" + (i+1) + "] " + tests[i] + ": " + testDescriptions[i], 55) + status + " │\n");
            } else {
                status = "❌ FAILED";
                failed++;
                append("│  " + padRight("[" + (i+1) + "] " + tests[i] + ": " + testDescriptions[i], 55) + status + " │\n");
            }
            
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        
        append("└─────────────────────────────────────────────────────────────┘\n");
        append("\n📊 DIAGNOSTICS SUMMARY:\n");
        append("  ✅ Passed: " + passed + "\n");
        append("  ⚠️ Warnings: " + warnings + "\n");
        append("  ❌ Failed: " + failed + "\n");
        
        if (failed == 0 && warnings == 0) {
            append("\n✅ All systems passed! System is healthy.\n");
            statusLabel.setText("🟢 Kernel: All Systems Healthy");
            statusLabel.setForeground(Color.decode("#10b981"));
        } else if (failed == 0) {
            append("\n⚠️ System has warnings but is operational.\n");
            statusLabel.setText("🟡 Kernel: System has warnings");
            statusLabel.setForeground(Color.decode("#f59e0b"));
        } else {
            append("\n❌ System has critical issues that need attention!\n");
            statusLabel.setText("🔴 Kernel: Critical Issues Detected");
            statusLabel.setForeground(Color.decode("#ef4444"));
        }
        
        NeilOS.kernelLogs.add("[DIAGNOSTICS] Completed - Passed: " + passed + 
            ", Warnings: " + warnings + ", Failed: " + failed + " - " + 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        progressBar.setVisible(false);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    /**
     * Reboot the system
     */
    private void rebootSystem() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "⚠️ Are you sure you want to reboot the system?\n\n" +
            "All unsaved work will be lost.", 
            "Confirm Reboot", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            append("\n🔄 SYSTEM REBOOT INITIATED\n");
            append("┌─────────────────────────────────────────────────────────────┐\n");
            append("│  Saving system state...                                    │\n");
            append("│  Closing applications...                                  │\n");
            append("│  Syncing filesystems...                                   │\n");
            append("│  Unmounting storage devices...                            │\n");
            append("│  Stopping kernel services...                              │\n");
            append("└─────────────────────────────────────────────────────────────┘\n");
            
            statusLabel.setText("🔄 Kernel: Rebooting...");
            statusLabel.setForeground(Color.decode("#f59e0b"));
            
            NeilOS.kernelLogs.add("[REBOOT] System reboot initiated - " + 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Animate reboot
            new Thread(() -> {
                try {
                    progressBar.setVisible(true);
                    for (int i = 0; i <= 100; i += 2) {
                        Thread.sleep(50);
                        int finalI = i;
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(finalI);
                            append("\rRebooting... " + finalI + "%");
                        });
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    append("\n\n✅ REBOOT COMPLETE\n");
                    append("System has been rebooted successfully.\n");
                    append("All services have been restarted.\n\n");
                    
                    statusLabel.setText("🟢 Kernel: Running");
                    statusLabel.setForeground(Color.decode("#10b981"));
                    
                    // Reset kernel logs after reboot simulation
                    NeilOS.kernelLogs.add("[SYSTEM] Reboot completed - " + 
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    
                    showKernelInfo();
                    
                    JOptionPane.showMessageDialog(this, 
                        "✅ System reboot completed successfully!\n\n" +
                        "NeilOS has been restarted.", 
                        "Reboot Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
            }).start();
        }
    }
    
    /**
     * Shutdown the system
     */
    private void shutdownSystem() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "⚠️ Are you sure you want to shutdown the system?\n\n" +
            "All unsaved work will be lost.", 
            "Confirm Shutdown", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            append("\n⏻ SYSTEM SHUTDOWN INITIATED\n");
            append("┌─────────────────────────────────────────────────────────────┐\n");
            append("│  Saving all data...                                       │\n");
            append("│  Flushing file system buffers...                          │\n");
            append("│  Stopping all services...                                 │\n");
            append("│  Terminating processes...                                 │\n");
            append("│  Powering down hardware...                                │\n");
            append("└─────────────────────────────────────────────────────────────┘\n");
            
            statusLabel.setText("⏻ Kernel: Shutting Down...");
            statusLabel.setForeground(Color.decode("#ef4444"));
            
            NeilOS.kernelLogs.add("[SHUTDOWN] System shutdown initiated - " + 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Animate shutdown
            new Thread(() -> {
                try {
                    progressBar.setVisible(true);
                    for (int i = 0; i <= 100; i += 2) {
                        Thread.sleep(50);
                        int finalI = i;
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(finalI);
                            append("\rShutting down... " + finalI + "%");
                        });
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                SwingUtilities.invokeLater(() -> {
                    append("\n\n🖥️ System is shutting down...\n");
                    append("Goodbye! 👋\n");
                    
                    JOptionPane.showMessageDialog(this, 
                        "🖥️ NeilOS is shutting down.\n\n" +
                        "Thank you for using NeilOS!\n" +
                        "Goodbye! 👋", 
                        "Shutdown", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Exit the application
                    System.exit(0);
                });
            }).start();
        }
    }
    
    /**
     * View kernel files
     */
    private void viewFiles() {
        if (NeilOS.files.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "📁 No kernel files found.\n\n" +
                "The kernel file system is empty.", 
                "Kernel Files", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📁 KERNEL FILES\n");
        sb.append("┌─────────────────────────────────────────────────────────────┐\n");
        
        for (String file : NeilOS.files) {
            // Try to get file size if it exists
            String size = "";
            File f = new File(file);
            if (f.exists()) {
                size = " (" + formatBytes(f.length()) + ")";
            }
            sb.append("│  📄 " + padRight(file + size, 53) + "│\n");
        }
        
        sb.append("└─────────────────────────────────────────────────────────────┘\n");
        sb.append("\n📊 Total: " + NeilOS.files.size() + " files\n");
        
        JOptionPane.showMessageDialog(this, sb.toString(), 
            "Kernel Files", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * View kernel logs
     */
    private void viewLogs() {
        if (NeilOS.kernelLogs.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "📋 No kernel logs available.\n\n" +
                "The kernel log is empty.", 
                "Kernel Logs", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📋 KERNEL LOGS\n");
        sb.append("┌─────────────────────────────────────────────────────────────┐\n");
        
        for (String log : NeilOS.kernelLogs) {
            String truncated = log.length() > 53 ? log.substring(0, 50) + "..." : log;
            sb.append("│  " + padRight(truncated, 53) + "│\n");
        }
        
        sb.append("└─────────────────────────────────────────────────────────────┘\n");
        sb.append("\n📊 Total entries: " + NeilOS.kernelLogs.size() + "\n");
        
        JOptionPane.showMessageDialog(this, sb.toString(), 
            "Kernel Logs", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clear kernel logs
     */
    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "🗑 Are you sure you want to clear all kernel logs?\n\n" +
            "This action cannot be undone.", 
            "Confirm Clear Logs", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            NeilOS.kernelLogs.clear();
            NeilOS.kernelLogs.add("[SYSTEM] Logs cleared by user - " + 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            append("\n🗑 Kernel logs have been cleared.\n");
            statusLabel.setText("🟢 Kernel: Logs Cleared");
            statusLabel.setForeground(Color.decode("#10b981"));
            
            JOptionPane.showMessageDialog(this, 
                "✅ Kernel logs have been cleared successfully.", 
                "Logs Cleared", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Show detailed system information
     */
    private void showSystemInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ℹ️ SYSTEM INFORMATION\n");
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║  OPERATING SYSTEM                                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  Name:           " + padRight(osName, 30) + "║\n");
        sb.append("║  Version:        " + padRight(osVersion, 30) + "║\n");
        sb.append("║  Architecture:   " + padRight(osArch, 30) + "║\n");
        sb.append("║  User:           " + padRight(userName, 30) + "║\n");
        sb.append("║  Home:           " + padRight(userHome, 30) + "║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  JAVA RUNTIME                                           ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  Version:        " + padRight(javaVersion, 30) + "║\n");
        sb.append("║  Vendor:         " + padRight(System.getProperty("java.vendor"), 30) + "║\n");
        sb.append("║  VM Name:        " + padRight(System.getProperty("java.vm.name"), 30) + "║\n");
        sb.append("║  VM Version:     " + padRight(System.getProperty("java.vm.version"), 30) + "║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  HARDWARE RESOURCES                                     ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  CPU Cores:      " + padRight(String.valueOf(availableProcessors), 30) + "║\n");
        sb.append("║  Max Memory:     " + padRight(formatBytes(maxMemory), 30) + "║\n");
        sb.append("║  Total Memory:   " + padRight(formatBytes(totalMemory), 30) + "║\n");
        sb.append("║  Free Memory:    " + padRight(formatBytes(freeMemory), 30) + "║\n");
        sb.append("║  Used Memory:    " + padRight(formatBytes(totalMemory - freeMemory), 30) + "║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  NEILOS STATISTICS                                      ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║  Current User:   " + padRight(NeilOS.currentUser, 30) + "║\n");
        sb.append("║  Current App:    " + padRight(NeilOS.currentApp, 30) + "║\n");
        sb.append("║  Theme:          " + padRight(NeilOS.currentTheme, 30) + "║\n");
        sb.append("║  Kernel Logs:    " + padRight(String.valueOf(NeilOS.kernelLogs.size()), 30) + "║\n");
        sb.append("║  Bank Balance:   $" + padRight(String.format("%,.2f", NeilOS.bankBalance), 28) + "║\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n");
        
        JOptionPane.showMessageDialog(this, sb.toString(), 
            "System Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Helper method to pad strings to a fixed width
     */
    private String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) {
            return s.substring(0, width);
        }
        return s + " ".repeat(width - s.length());
    }
    
    /**
     * Helper method to format bytes to human readable format
     */
    private String formatBytes(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * Helper method to append text to log area
     */
    private void append(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    /**
     * Clean up resources when closing
     */
    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}