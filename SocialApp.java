package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Social Media Hub with Instagram, Facebook, WhatsApp, LinkedIn integration
 */
public class SocialApp extends JPanel {
    private NeilOS neilos;
    private JTextArea timelineArea;
    private JLabel postCountLabel;
    
    public SocialApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
    }
    
    private void createUI() {
        // Top panel with social media buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton instagramBtn = createSocialButton("📸 Instagram", "#E1306C", "https://www.instagram.com/");
        JButton facebookBtn = createSocialButton("📘 Facebook", "#1877F2", "https://www.facebook.com/");
        JButton whatsappBtn = createSocialButton("💬 WhatsApp", "#25D366", "https://web.whatsapp.com/");
        JButton linkedinBtn = createSocialButton("💼 LinkedIn", "#0A66C2", "https://www.linkedin.com/");
        JButton postBtn = createButton("✏️ Create Post", "#10b981", this::createPost);
        JButton likeBtn = createButton("❤️ Like Post", "#f59e0b", this::likeLatestPost);
        
        topPanel.add(instagramBtn);
        topPanel.add(facebookBtn);
        topPanel.add(whatsappBtn);
        topPanel.add(linkedinBtn);
        topPanel.add(postBtn);
        topPanel.add(likeBtn);
        
        postCountLabel = new JLabel("📊 Total Posts: 0");
        postCountLabel.setForeground(Color.decode("#ef4444"));
        postCountLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        topPanel.add(postCountLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Timeline area
        timelineArea = new JTextArea();
        timelineArea.setEditable(false);
        timelineArea.setBackground(Color.decode("#1e1e1e"));
        timelineArea.setForeground(Color.decode("#a0aec0"));
        timelineArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        timelineArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(timelineArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        refreshTimeline();
    }
    
    private JButton createSocialButton(String text, String color, String url) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.addActionListener(e -> openBrowser(url));
        return btn;
    }
    
    private JButton createButton(String text, String color, Runnable action) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    private void openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            addPost("Opened " + url);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open browser: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createPost() {
        String text = JOptionPane.showInputDialog(this, "Write something:", "Create Post", JOptionPane.QUESTION_MESSAGE);
        if (text != null && !text.isEmpty()) {
            addPost(text);
            JOptionPane.showMessageDialog(this, "Post created!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void addPost(String text) {
        Map<String, Object> post = new HashMap<>();
        post.put("text", text);
        post.put("likes", 0);
        post.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        NeilOS.socialPosts.add(post);
        refreshTimeline();
    }
    
    private void likeLatestPost() {
        if (NeilOS.socialPosts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No posts to like.", "Timeline", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Map<String, Object> latest = NeilOS.socialPosts.get(NeilOS.socialPosts.size() - 1);
        int likes = (int) latest.get("likes");
        latest.put("likes", likes + 1);
        refreshTimeline();
        JOptionPane.showMessageDialog(this, "You liked the latest post!", "Liked", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshTimeline() {
        postCountLabel.setText("📊 Total Posts: " + NeilOS.socialPosts.size());
        
        if (NeilOS.socialPosts.isEmpty()) {
            timelineArea.setText("No posts yet.\nClick 'Create Post' to start sharing!");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Timeline\n");
        sb.append("━".repeat(40)).append("\n\n");
        
        // Show posts in reverse chronological order (newest first)
        for (int i = NeilOS.socialPosts.size() - 1; i >= 0; i--) {
            Map<String, Object> post = NeilOS.socialPosts.get(i);
            sb.append("[").append(post.get("time")).append("] ");
            sb.append(post.get("text")).append("\n");
            sb.append("❤️ ").append(post.get("likes")).append(" likes\n");
            sb.append("━".repeat(40)).append("\n\n");
        }
        
        timelineArea.setText(sb.toString());
        timelineArea.setCaretPosition(0);
    }
}