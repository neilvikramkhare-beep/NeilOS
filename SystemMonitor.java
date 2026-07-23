package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * System monitoring with real-time stats
 */
public class SystemMonitor extends JPanel {
    private NeilOS neilos;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JLabel uptimeLabel;
    private JLabel processCountLabel;
    private JTextArea detailsArea;
    private Timer timer;
    
    public SystemMonitor(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        startMonitoring();
    }
    
    private void createUI() {
        // Top stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBackground(Color.decode("#0f172a"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // CPU
        JPanel cpuPanel = createStatPanel("CPU Usage", "#10b981");
        cpuLabel = new JLabel("0%");
        cpuLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        cpuLabel.setForeground(Color.decode("#10b981"));
        cpuPanel.add(cpuLabel);
        statsPanel.add(cpuPanel);
        
        // Memory
        JPanel memoryPanel = createStatPanel("Memory Usage", "#3b82f6");
        memoryLabel = new JLabel("0%");
        memoryLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        memoryLabel.setForeground(Color.decode("#3b82f6"));
        memoryPanel.add(memoryLabel);
        statsPanel.add(memoryPanel);
        
        // Uptime
        JPanel uptimePanel = createStatPanel("System Uptime", "#f59e0b");
        uptimeLabel = new JLabel("0h 0m");
        uptimeLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        uptimeLabel.setForeground(Color.decode("#f59e0b"));
        uptimePanel.add(uptimeLabel);
        statsPanel.add(uptimePanel);
        
        // Processes
        JPanel processPanel = createStatPanel("Running Processes", "#ef4444");
        processCountLabel = new JLabel("0");
        processCountLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        processCountLabel.setForeground(Color.decode("#ef4444"));
        processPanel.add(processCountLabel);
        statsPanel.add(processPanel);
        
        add(statsPanel, BorderLayout.NORTH);
        
        // Details area
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.BLACK);
        detailsArea.setForeground(Color.decode("#00ff00"));
        detailsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createStatPanel(String title, String color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#1e1e1e"));
        panel.setBorder(BorderFactory.createLineBorder(Color.decode(color), 1));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(color), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        titleLabel.setForeground(Color.decode(color));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void startMonitoring() {
        timer = new Timer(1000, e -> updateStats());
        timer.start();
        updateStats();
    }
    
    private void updateStats() {
        try {
            // CPU usage
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double cpuLoad = osBean.getSystemLoadAverage();
            if (cpuLoad < 0) {
                cpuLabel.setText("N/A");
            } else {
                cpuLabel.setText(String.format("%.0f%%", cpuLoad * 100 / osBean.getAvailableProcessors()));
            }
            
            // Memory usage
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double memoryPercent = (double) usedMemory / maxMemory * 100;
            memoryLabel.setText(String.format("%.0f%%", memoryPercent));
            
            // Uptime
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            long uptime = runtimeBean.getUptime() / 1000;
            long hours = uptime / 3600;
            long minutes = (uptime % 3600) / 60;
            uptimeLabel.setText(String.format("%dh %dm", hours, minutes));
            
            // Processes
            int processCount = ManagementFactory.getThreadMXBean().getThreadCount();
            processCountLabel.setText(String.valueOf(processCount));
            
            // Detailed info
            StringBuilder details = new StringBuilder();
            details.append("📊 SYSTEM DETAILS\n");
            details.append("━".repeat(40)).append("\n\n");
            details.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
            details.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
            details.append("Architecture: ").append(System.getProperty("os.arch")).append("\n");
            details.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
            details.append("CPU Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
            details.append("Max Memory: ").append(formatBytes(maxMemory)).append("\n");
            details.append("Total Memory: ").append(formatBytes(totalMemory)).append("\n");
            details.append("Free Memory: ").append(formatBytes(freeMemory)).append("\n");
            details.append("Used Memory: ").append(formatBytes(usedMemory)).append("\n");
            details.append("Thread Count: ").append(processCount).append("\n");
            details.append("User: ").append(System.getProperty("user.name")).append("\n");
            details.append("Working Directory: ").append(System.getProperty("user.dir")).append("\n");
            
            detailsArea.setText(details.toString());
            detailsArea.setCaretPosition(0);
            
        } catch (Exception e) {
            detailsArea.setText("Error updating stats: " + e.getMessage());
        }
    }
    
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
    
    public void stopMonitoring() {
        if (timer != null) {
            timer.stop();
        }
    }
}