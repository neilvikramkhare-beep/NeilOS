package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Network management and diagnostics
 */
public class NetworkApp extends JPanel {
    private NeilOS neilos;
    private JTextArea outputArea;
    private JTextField hostField;
    
    public NetworkApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        showNetworkInfo();
    }
    
    private void createUI() {
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton infoBtn = new JButton("🌐 Network Info");
        infoBtn.setBackground(Color.decode("#3b82f6"));
        infoBtn.setForeground(Color.WHITE);
        infoBtn.setFocusPainted(false);
        infoBtn.addActionListener(e -> showNetworkInfo());
        topPanel.add(infoBtn);
        
        JButton pingBtn = new JButton("📡 Ping");
        pingBtn.setBackground(Color.decode("#10b981"));
        pingBtn.setForeground(Color.WHITE);
        pingBtn.setFocusPainted(false);
        pingBtn.addActionListener(e -> pingHost());
        topPanel.add(pingBtn);
        
        hostField = new JTextField(20);
        hostField.setBackground(Color.BLACK);
        hostField.setForeground(Color.WHITE);
        hostField.setFont(new Font("Consolas", Font.PLAIN, 12));
        hostField.setToolTipText("Enter hostname or IP to ping");
        topPanel.add(hostField);
        
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
    }
    
    private void showNetworkInfo() {
        outputArea.setText("");
        append("🌐 NETWORK INFORMATION\n");
        append("━".repeat(40) + "\n\n");
        
        try {
            // Hostname
            String hostname = InetAddress.getLocalHost().getHostName();
            append("Hostname: " + hostname + "\n");
            
            // IP addresses
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        append("IP: " + addr.getHostAddress() + " (" + ni.getDisplayName() + ")\n");
                    }
                }
            }
            
            // MAC address
            NetworkInterface netInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netInterface != null) {
                byte[] mac = netInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder macStr = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macStr.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    append("MAC Address: " + macStr.toString() + "\n");
                }
            }
            
            append("\n✅ Network information retrieved successfully.\n");
            
        } catch (Exception e) {
            append("❌ Error: " + e.getMessage() + "\n");
        }
    }
    
    private void pingHost() {
        String host = hostField.getText().trim();
        if (host.isEmpty()) {
            host = JOptionPane.showInputDialog(this, "Enter hostname or IP to ping:", 
                "Ping", JOptionPane.QUESTION_MESSAGE);
            if (host == null || host.isEmpty()) return;
            hostField.setText(host);
        }
        
        outputArea.setText("");
        append("📡 PINGING " + host + "\n");
        append("━".repeat(40) + "\n\n");
        
        new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName(host);
                append("Resolved: " + address.getHostAddress() + "\n\n");
                
                for (int i = 1; i <= 5; i++) {
                    long startTime = System.currentTimeMillis();
                    boolean reachable = address.isReachable(3000);
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    
                    if (reachable) {
                        append("Reply from " + host + ": bytes=32 time=" + responseTime + "ms TTL=64\n");
                    } else {
                        append("Request timed out.\n");
                    }
                    
                    if (i < 5) {
                        Thread.sleep(500);
                    }
                }
                
                append("\n✅ Ping complete.\n");
                
            } catch (UnknownHostException e) {
                append("❌ Unknown host: " + host + "\n");
            } catch (Exception e) {
                append("❌ Error: " + e.getMessage() + "\n");
            }
        }).start();
    }
    
    private void append(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}