package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;

/**
 * Google Search integration
 */
public class SearchApp extends JPanel {
    private NeilOS neilos;
    private JTextField searchField;
    private JButton searchButton;
    
    public SearchApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new GridBagLayout());
        createUI();
    }
    
    private void createUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Logo
        JLabel logoLabel = new JLabel("🔍 Google Search");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(Color.decode("#ef4444"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(logoLabel, gbc);
        
        // Search field
        searchField = new JTextField(30);
        searchField.setFont(new Font("Consolas", Font.PLAIN, 14));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(Color.decode("#ef4444"), 2));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(searchField, gbc);
        
        // Search button
        searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(Color.decode("#ef4444"));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        gbc.gridx = 1;
        add(searchButton, gbc);
        
        // Enter key support
        searchField.addActionListener(e -> performSearch());
        
        // Tips
        JLabel tipLabel = new JLabel("💡 Enter any query to search with Google");
        tipLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        tipLabel.setForeground(Color.decode("#a0aec0"));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(tipLabel, gbc);
        
        // Quick links
        JPanel quickLinks = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quickLinks.setBackground(Color.decode("#101826"));
        
        String[][] links = {
            {"Gmail", "https://mail.google.com"},
            {"YouTube", "https://www.youtube.com"},
            {"Google Maps", "https://maps.google.com"},
            {"Google Drive", "https://drive.google.com"},
            {"Google Docs", "https://docs.google.com"},
            {"Google Images", "https://images.google.com"}
        };
        
        for (String[] link : links) {
            JButton btn = new JButton(link[0]);
            btn.setFont(new Font("Consolas", Font.PLAIN, 10));
            btn.setBackground(Color.decode("#1e1e1e"));
            btn.setForeground(Color.decode("#3b82f6"));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.decode("#3b82f6"), 1));
            btn.addActionListener(e -> openUrl(link[1]));
            quickLinks.add(btn);
        }
        
        gbc.gridy = 3;
        add(quickLinks, gbc);
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search query!", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String url = "https://www.google.com/search?q=" + query.replace(" ", "+");
        openUrl(url);
    }
    
    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open browser: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}