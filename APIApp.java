package com.neilos.apps;

import com.neilos.NeilOS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * API testing and management center
 */
public class APIApp extends JPanel {
    private NeilOS neilos;
    private JTextArea outputArea;
    private JTextField endpointField;
    private JComboBox<String> methodCombo;
    private JTextArea requestBodyArea;
    private Random random;
    
    public APIApp(NeilOS neilos) {
        this.neilos = neilos;
        this.random = new Random();
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        showWelcome();
    }
    
    private void createUI() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Method selector
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.setBackground(Color.decode("#0f172a"));
        
        methodCombo = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE", "PATCH"});
        methodCombo.setBackground(Color.decode("#1e1e1e"));
        methodCombo.setForeground(Color.WHITE);
        methodPanel.add(methodCombo);
        
        // Endpoint field
        endpointField = new JTextField(30);
        endpointField.setBackground(Color.BLACK);
        endpointField.setForeground(Color.WHITE);
        endpointField.setFont(new Font("Consolas", Font.PLAIN, 12));
        endpointField.setToolTipText("Enter API endpoint URL");
        methodPanel.add(endpointField);
        
        JButton testBtn = new JButton("🔌 Test API");
        testBtn.setBackground(Color.decode("#10b981"));
        testBtn.setForeground(Color.WHITE);
        testBtn.setFocusPainted(false);
        testBtn.addActionListener(e -> testAPI());
        methodPanel.add(testBtn);
        
        JButton mockBtn = new JButton("🎭 Mock Response");
        mockBtn.setBackground(Color.decode("#f59e0b"));
        mockBtn.setForeground(Color.WHITE);
        mockBtn.setFocusPainted(false);
        mockBtn.addActionListener(e -> mockAPI());
        methodPanel.add(mockBtn);
        
        JButton clearBtn = new JButton("🗑 Clear");
        clearBtn.setBackground(Color.decode("#ef4444"));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> outputArea.setText(""));
        methodPanel.add(clearBtn);
        
        topPanel.add(methodPanel, BorderLayout.NORTH);
        
        // Request body area
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Color.decode("#0f172a"));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel bodyLabel = new JLabel("Request Body (JSON):");
        bodyLabel.setForeground(Color.WHITE);
        bodyLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        bodyPanel.add(bodyLabel, BorderLayout.NORTH);
        
        requestBodyArea = new JTextArea(3, 50);
        requestBodyArea.setBackground(Color.BLACK);
        requestBodyArea.setForeground(Color.decode("#00ff00"));
        requestBodyArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        requestBodyArea.setText("{\n  \"key\": \"value\"\n}");
        
        JScrollPane bodyScroll = new JScrollPane(requestBodyArea);
        bodyPanel.add(bodyScroll, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.decode("#00ff00"));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createEmptyBorder());
        add(outputScroll, BorderLayout.SOUTH);
        
        // Set preferred sizes
        topPanel.setPreferredSize(new Dimension(800, 60));
        bodyPanel.setPreferredSize(new Dimension(800, 80));
        outputScroll.setPreferredSize(new Dimension(800, 300));
    }
    
    private void showWelcome() {
        outputArea.setText("");
        append("🔌 API CENTER\n");
        append("━".repeat(40) + "\n\n");
        append("API Testing & Management\n\n");
        append("Available endpoints:\n");
        append("  • GET /api/users - Get all users\n");
        append("  • GET /api/users/{id} - Get user by ID\n");
        append("  • POST /api/users - Create user\n");
        append("  • PUT /api/users/{id} - Update user\n");
        append("  • DELETE /api/users/{id} - Delete user\n\n");
        append("💡 Enter a URL and click 'Test API' to make a request.\n");
        append("💡 Click 'Mock Response' for a simulated API response.\n");
    }
    
    private void testAPI() {
        String method = (String) methodCombo.getSelectedItem();
        String endpoint = endpointField.getText().trim();
        
        if (endpoint.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an API endpoint!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        outputArea.setText("");
        append("🔌 " + method + " " + endpoint + "\n");
        append("Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
        append("━".repeat(40) + "\n\n");
        
        // Check if it's a local file or URL
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            // Make real HTTP request
            makeHTTPRequest(method, endpoint);
        } else {
            // Local API simulation
            simulateLocalAPI(method, endpoint);
        }
    }
    
    private void makeHTTPRequest(String method, String endpoint) {
        append("Making HTTP request...\n\n");
        
        new Thread(() -> {
            try {
                URI uri = new URI(endpoint);
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Add headers
                connection.setRequestProperty("User-Agent", "NeilOS-API-Client");
                connection.setRequestProperty("Accept", "application/json");
                
                // For POST/PUT, send request body
                if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
                    connection.setDoOutput(true);
                    String body = requestBodyArea.getText();
                    if (!body.isEmpty()) {
                        connection.setRequestProperty("Content-Type", "application/json");
                        try (java.io.OutputStream os = connection.getOutputStream()) {
                            os.write(body.getBytes());
                            os.flush();
                        }
                        append("📤 Request Body:\n" + body + "\n\n");
                    }
                }
                
                int responseCode = connection.getResponseCode();
                append("Response Code: " + responseCode + "\n");
                
                // Read response
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                
                append("\n📥 Response:\n");
                append(response.toString() + "\n");
                
                connection.disconnect();
                
            } catch (Exception e) {
                append("❌ Error: " + e.getMessage() + "\n");
                append("\n💡 Tip: For local testing, use 'Mock Response' instead.\n");
            }
        }).start();
    }
    
    private void simulateLocalAPI(String method, String endpoint) {
        append("🔬 Simulating local API call...\n\n");
        
        // Simulate delay
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate mock response based on endpoint
        Gson gson = new Gson();
        JsonObject response = new JsonObject();
        
        if (endpoint.contains("users")) {
            if (method.equals("GET")) {
                response.addProperty("status", "success");
                response.addProperty("message", "Users retrieved successfully");
                response.addProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                if (endpoint.contains("/")) {
                    // Single user
                    response.addProperty("user_id", random.nextInt(1000) + 1);
                    response.addProperty("name", "User " + (random.nextInt(100) + 1));
                    response.addProperty("email", "user" + (random.nextInt(100) + 1) + "@example.com");
                } else {
                    // List of users
                    for (int i = 1; i <= 5; i++) {
                        JsonObject user = new JsonObject();
                        user.addProperty("id", i);
                        user.addProperty("name", "User " + i);
                        user.addProperty("email", "user" + i + "@example.com");
                        response.add("user_" + i, user);
                    }
                }
            } else if (method.equals("POST")) {
                response.addProperty("status", "success");
                response.addProperty("message", "User created successfully");
                response.addProperty("user_id", random.nextInt(1000) + 1);
                response.addProperty("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else if (method.equals("PUT")) {
                response.addProperty("status", "success");
                response.addProperty("message", "User updated successfully");
                response.addProperty("updated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else if (method.equals("DELETE")) {
                response.addProperty("status", "success");
                response.addProperty("message", "User deleted successfully");
            }
        } else {
            // Generic response
            response.addProperty("status", "success");
            response.addProperty("method", method);
            response.addProperty("endpoint", endpoint);
            response.addProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.addProperty("message", "API call simulated successfully");
        }
        
        append("📥 Response:\n");
        append(gson.toJson(response) + "\n\n");
        append("✅ API simulation completed.\n");
        
        NeilOS.API_LIST.add(endpoint + " (" + method + ") - " + 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    private void mockAPI() {
        outputArea.setText("");
        append("🎭 MOCK API RESPONSE\n");
        append("━".repeat(40) + "\n\n");
        
        // Generate random mock data
        Gson gson = new Gson();
        JsonObject mockData = new JsonObject();
        mockData.addProperty("status", "success");
        mockData.addProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        mockData.addProperty("message", "This is a mock API response for testing");
        
        // Add random data
        mockData.addProperty("id", random.nextInt(10000) + 1);
        mockData.addProperty("name", "Mock Data " + (random.nextInt(100) + 1));
        mockData.addProperty("value", random.nextDouble() * 1000);
        mockData.addProperty("active", random.nextBoolean());
        
        // Nested data
        JsonObject nested = new JsonObject();
        nested.addProperty("property1", "value1");
        nested.addProperty("property2", "value2");
        nested.addProperty("count", random.nextInt(100) + 1);
        mockData.add("data", nested);
        
        append("📥 Mock Response:\n");
        append(gson.toJson(mockData) + "\n\n");
        append("✅ Mock response generated.\n");
        append("💡 Use this for testing your API client logic.\n");
    }
    
    private void append(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}