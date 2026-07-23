package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Random;

/**
 * AI Assistant with ChatGPT, Grok, DeepSeek integration
 */
public class AIApp extends JPanel {
    private NeilOS neilos;
    private JTextArea outputArea;
    private Random random;
    
    public AIApp(NeilOS neilos) {
        this.neilos = neilos;
        this.random = new Random();
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        showWelcomeMessage();
    }
    
    private void createUI() {
        // Top panel with AI buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton chatgptBtn = createAIButton("🤖 ChatGPT", "#10b981", "https://chat.openai.com/");
        JButton grokBtn = createAIButton("🧠 Grok", "#f59e0b", "https://x.ai/grok");
        JButton deepseekBtn = createAIButton("📊 DeepSeek", "#8b5cf6", "https://deepseek.com/");
        JButton localBtn = createLocalAIButton("💬 Ask Local AI", "#3b82f6");
        
        topPanel.add(chatgptBtn);
        topPanel.add(grokBtn);
        topPanel.add(deepseekBtn);
        topPanel.add(localBtn);
        
        JButton logBtn = new JButton("📋 View Log");
        logBtn.setBackground(Color.decode("#ef4444"));
        logBtn.setForeground(Color.WHITE);
        logBtn.setFocusPainted(false);
        logBtn.addActionListener(e -> viewLog());
        topPanel.add(logBtn);
        
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
    
    private JButton createAIButton(String text, String color, String url) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            openAI(url);
            append("\n[AI] Opened: " + text + "\n");
        });
        return btn;
    }
    
    private JButton createLocalAIButton(String text, String color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.PLAIN, 11));
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> askLocalAI());
        return btn;
    }
    
    private void showWelcomeMessage() {
        outputArea.setText("");
        append("🤖 AI ASSISTANT HUB\n");
        append("━".repeat(40) + "\n\n");
        append("Available AI Services:\n");
        append("  • ChatGPT - OpenAI's conversational AI\n");
        append("  • Grok - xAI's cutting-edge assistant\n");
        append("  • DeepSeek - Advanced reasoning AI\n");
        append("  • Local AI - Built-in assistant (limited)\n\n");
        append("Type 'help' for more information.\n");
        append("━".repeat(40) + "\n\n");
        append("Ready for AI interactions.\n");
    }
    
    private void openAI(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            NeilOS.aiLog.add("[AI] Opened: " + url);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open browser: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void askLocalAI() {
        String question = JOptionPane.showInputDialog(this, "Ask the AI Assistant:", 
            "Local AI", JOptionPane.QUESTION_MESSAGE);
        if (question == null || question.isEmpty()) return;
        
        NeilOS.aiLog.add("User: " + question);
        append("\n👤 User: " + question + "\n");
        
        // Simulate AI thinking
        append("🤔 AI is thinking...\n");
        
        // Simulate processing delay
        try {
            Thread.sleep(1000 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate response
        String response = generateAIResponse(question);
        NeilOS.aiLog.add("AI: " + response);
        append("🤖 AI: " + response + "\n\n");
    }
    
    private String generateAIResponse(String question) {
        String lowerQ = question.toLowerCase();
        
        // Pattern-based responses
        if (lowerQ.contains("hello") || lowerQ.contains("hi") || lowerQ.contains("hey")) {
            return "Hello! How can I help you today?";
        }
        
        if (lowerQ.contains("weather") || lowerQ.contains("temperature")) {
            return "I don't have access to real-time weather data. Please check a weather service for current conditions.";
        }
        
        if (lowerQ.contains("help") || lowerQ.contains("?")) {
            return "I'm a simple local AI assistant. I can answer basic questions, but for complex tasks, please use ChatGPT, Grok, or DeepSeek.";
        }
        
        if (lowerQ.contains("time") || lowerQ.contains("date")) {
            return "Current time is " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
        }
        
        if (lowerQ.contains("name") || lowerQ.contains("who")) {
            return "I'm NeilOS AI Assistant, a simple local AI built into the system.";
        }
        
        if (lowerQ.contains("thank")) {
            return "You're welcome! Happy to help.";
        }
        
        if (lowerQ.contains("joke") || lowerQ.contains("funny")) {
            String[] jokes = {
                "Why do programmers prefer dark mode? Because light attracts bugs!",
                "What do you call a programmer from Finland? Nerdic.",
                "Why did the Java developer wear glasses? Because he couldn't C#.",
                "How many programmers does it take to change a light bulb? None, that's a hardware problem."
            };
            return jokes[random.nextInt(jokes.length)];
        }
        
        if (lowerQ.contains("code") || lowerQ.contains("programming") || lowerQ.contains("python")) {
            return "For programming help, I'd recommend using Code Studio or one of the AI services. I'm more of a general assistant.";
        }
        
        // Default responses
        String[] defaultResponses = {
            "That's an interesting question! I'm still learning and improving.",
            "Let me think about that... I'll get back to you on this topic.",
            "Great question! For more detailed answers, try ChatGPT or Grok.",
            "I don't have enough knowledge about that yet. I'm learning every day!",
            "That's beyond my current knowledge. I'm a simple local AI assistant."
        };
        return defaultResponses[random.nextInt(defaultResponses.length)];
    }
    
    private void viewLog() {
        if (NeilOS.aiLog.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No AI interactions logged yet.", 
                "AI Log", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder log = new StringBuilder();
        log.append("📋 AI INTERACTION LOG\n");
        log.append("━".repeat(40)).append("\n\n");
        
        for (String entry : NeilOS.aiLog) {
            log.append(entry).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, log.toString(), 
            "AI Log", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void append(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}