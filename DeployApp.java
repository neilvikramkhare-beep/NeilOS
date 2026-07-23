package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Deployment center for projects
 */
public class DeployApp extends JPanel {
    private NeilOS neilos;
    private JTextArea logArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Random random;
    
    public DeployApp(NeilOS neilos) {
        this.neilos = neilos;
        this.random = new Random();
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        showWelcome();
    }
    
    private void createUI() {
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton deployBtn = new JButton("🚀 Deploy Project");
        deployBtn.setBackground(Color.decode("#10b981"));
        deployBtn.setForeground(Color.WHITE);
        deployBtn.setFocusPainted(false);
        deployBtn.addActionListener(e -> deployProject());
        topPanel.add(deployBtn);
        
        JButton viewBtn = new JButton("📋 View Logs");
        viewBtn.setBackground(Color.decode("#3b82f6"));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        viewBtn.addActionListener(e -> viewLogs());
        topPanel.add(viewBtn);
        
        JButton clearBtn = new JButton("🗑 Clear Logs");
        clearBtn.setBackground(Color.decode("#ef4444"));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearLogs());
        topPanel.add(clearBtn);
        
        statusLabel = new JLabel("Ready for deployment");
        statusLabel.setForeground(Color.decode("#10b981"));
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        topPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with progress
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode("#101826"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.decode("#10b981"));
        progressBar.setBackground(Color.decode("#1e1e1e"));
        centerPanel.add(progressBar, BorderLayout.NORTH);
        
        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.decode("#00ff00"));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void showWelcome() {
        logArea.setText("");
        append("🚀 DEPLOYMENT CENTER\n");
        append("━".repeat(40) + "\n\n");
        append("Ready to deploy your projects.\n");
        append("Click 'Deploy Project' to start.\n\n");
        append("Recent deployments:\n");
        
        if (NeilOS.deploymentLogs.isEmpty()) {
            append("  No deployments yet.\n");
        } else {
            for (int i = Math.max(0, NeilOS.deploymentLogs.size() - 5); 
                 i < NeilOS.deploymentLogs.size(); i++) {
                append("  • " + NeilOS.deploymentLogs.get(i) + "\n");
            }
        }
    }
    
    private void deployProject() {
        String project = JOptionPane.showInputDialog(this, "Enter project name:", 
            "Deploy Project", JOptionPane.QUESTION_MESSAGE);
        if (project == null || project.isEmpty()) return;
        
        statusLabel.setText("Deploying: " + project);
        logArea.setText("");
        progressBar.setValue(0);
        
        new Thread(() -> {
            String[] steps = {
                "Building " + project + "...",
                "Compiling source code...",
                "Running tests...",
                "Packaging artifacts...",
                "Uploading to repository...",
                "Deploying to production...",
                "Verifying deployment...",
                "✅ Deployment complete!"
            };
            
            for (int i = 0; i < steps.length; i++) {
                try {
                    Thread.sleep(500 + random.nextInt(500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                int progress = (i + 1) * 100 / steps.length;
                progressBar.setValue(progress);
                append("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + steps[i] + "\n");
                
                // Add random log entries
                if (random.nextDouble() < 0.3 && i < steps.length - 1) {
                    String[] extraLogs = {
                        "  • Processing file " + (i + 1) + "/" + steps.length,
                        "  • Checking dependencies...",
                        "  • Optimizing build...",
                        "  • Generating documentation...",
                        "  • Running security scan..."
                    };
                    append("  " + extraLogs[random.nextInt(extraLogs.length)] + "\n");
                }
            }
            
            NeilOS.deploymentLogs.add("[SUCCESS] " + project + " deployed at " + 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            statusLabel.setText("✅ Deployment complete: " + project);
            append("\n✅ Deployment completed successfully!\n");
            
            JOptionPane.showMessageDialog(this, 
                "Deployment completed successfully!\nProject: " + project,
                "Deployment Success", JOptionPane.INFORMATION_MESSAGE);
                
        }).start();
    }
    
    private void viewLogs() {
        if (NeilOS.deploymentLogs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No deployment logs available.", 
                "Logs", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📋 DEPLOYMENT LOGS\n");
        sb.append("━".repeat(40)).append("\n\n");
        
        for (String log : NeilOS.deploymentLogs) {
            sb.append(log).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), 
            "Deployment Logs", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Clear all deployment logs?", "Confirm", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            NeilOS.deploymentLogs.clear();
            showWelcome();
            statusLabel.setText("Logs cleared");
            JOptionPane.showMessageDialog(this, "Logs cleared!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void append(String text) {
        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}