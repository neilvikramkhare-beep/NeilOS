package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * Code Studio - Multi-language programming environment
 */
public class CodeStudioApp extends JPanel {
    private NeilOS neilos;
    private JComboBox<String> languageSelector;
    private JTextArea codeArea;
    private JTextArea outputArea;
    private JButton runButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private String currentLanguage;
    private Map<String, CodeTemplate> templates;
    
    private static class CodeTemplate {
        String extension;
        String template;
        String runner;
        
        CodeTemplate(String extension, String template, String runner) {
            this.extension = extension;
            this.template = template;
            this.runner = runner;
        }
    }
    
    public CodeStudioApp(NeilOS neilos) {
        this.neilos = neilos;
        this.currentLanguage = "Python";
        this.templates = new HashMap<>();
        initializeTemplates();
        
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        loadTemplate("Python");
    }
    
    private void initializeTemplates() {
        templates.put("Python", new CodeTemplate(".py", 
            "# Python Program\n\nprint(\"Hello, World!\")", "python"));
        templates.put("JavaScript", new CodeTemplate(".js",
            "// JavaScript Program\n\nconsole.log(\"Hello, World!\");", "node"));
        templates.put("Java", new CodeTemplate(".java",
            "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}", "javac"));
        templates.put("C++", new CodeTemplate(".cpp",
            "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hello, World!\" << endl;\n    return 0;\n}", "g++"));
        templates.put("C", new CodeTemplate(".c",
            "#include <stdio.h>\n\nint main() {\n    printf(\"Hello, World!\\n\");\n    return 0;\n}", "gcc"));
        templates.put("C#", new CodeTemplate(".cs",
            "using System;\n\nclass Program {\n    static void Main() {\n        Console.WriteLine(\"Hello, World!\");\n    }\n}", "csc"));
        templates.put("Ruby", new CodeTemplate(".rb",
            "# Ruby Program\n\nputs \"Hello, World!\"", "ruby"));
        templates.put("Go", new CodeTemplate(".go",
            "package main\n\nimport \"fmt\"\n\nfunc main() {\n    fmt.Println(\"Hello, World!\")\n}", "go"));
        templates.put("Rust", new CodeTemplate(".rs",
            "fn main() {\n    println!(\"Hello, World!\");\n}", "rustc"));
        templates.put("PHP", new CodeTemplate(".php",
            "<?php\necho \"Hello, World!\\n\";\n?>", "php"));
        templates.put("Swift", new CodeTemplate(".swift",
            "import Swift\n\nprint(\"Hello, World!\")", "swift"));
        templates.put("Kotlin", new CodeTemplate(".kt",
            "fun main() {\n    println(\"Hello, World!\")\n}", "kotlin"));
        templates.put("HTML/CSS", new CodeTemplate(".html",
            "<!DOCTYPE html>\n<html>\n<head>\n    <title>My Page</title>\n    <style>\n        body { font-family: Arial; text-align: center; padding: 50px; }\n        h1 { color: blue; }\n    </style>\n</head>\n<body>\n    <h1>Hello, World!</h1>\n    <p>Welcome to NeilOS Code Studio</p>\n</body>\n</html>", "browser"));
        templates.put("SQL", new CodeTemplate(".sql",
            "-- SQL Database Query\n\nCREATE TABLE users (\n    id INT PRIMARY KEY,\n    name VARCHAR(100),\n    email VARCHAR(100)\n);\n\nSELECT * FROM users;", "sqlite3"));
        templates.put("Bash", new CodeTemplate(".sh",
            "#!/bin/bash\n\necho \"Hello, World!\"\n\n# List files\nls -la", "bash"));
        templates.put("Perl", new CodeTemplate(".pl",
            "#!/usr/bin/perl\n\nprint \"Hello, World!\\n\";", "perl"));
        templates.put("Lua", new CodeTemplate(".lua",
            "-- Lua Program\n\nprint(\"Hello, World!\")", "lua"));
        templates.put("R", new CodeTemplate(".r",
            "# R Program\n\nprint(\"Hello, World!\")\n\n# Create a vector\ndata <- c(1, 2, 3, 4, 5)\nprint(mean(data))", "Rscript"));
        templates.put("Dart", new CodeTemplate(".dart",
            "void main() {\n    print(\"Hello, World!\");\n}", "dart"));
        templates.put("TypeScript", new CodeTemplate(".ts",
            "// TypeScript Program\n\nlet message: string = \"Hello, World!\";\nconsole.log(message);", "ts-node"));
    }
    
    private void createUI() {
        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel langLabel = new JLabel("Language:");
        langLabel.setForeground(Color.WHITE);
        topPanel.add(langLabel);
        
        String[] languages = templates.keySet().toArray(new String[0]);
        Arrays.sort(languages);
        languageSelector = new JComboBox<>(languages);
        languageSelector.setSelectedItem("Python");
        languageSelector.setBackground(Color.decode("#1e1e1e"));
        languageSelector.setForeground(Color.WHITE);
        languageSelector.addActionListener(e -> {
            String selected = (String) languageSelector.getSelectedItem();
            if (selected != null) {
                currentLanguage = selected;
                loadTemplate(selected);
                updateStatus("Language: " + selected);
            }
        });
        topPanel.add(languageSelector);
        
        runButton = new JButton("▶ Run");
        runButton.setBackground(Color.decode("#10b981"));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.addActionListener(e -> runCode());
        topPanel.add(runButton);
        
        saveButton = new JButton("💾 Save");
        saveButton.setBackground(Color.decode("#3b82f6"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveCode());
        topPanel.add(saveButton);
        
        loadButton = new JButton("📂 Load");
        loadButton.setBackground(Color.decode("#f59e0b"));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.addActionListener(e -> loadCode());
        topPanel.add(loadButton);
        
        clearButton = new JButton("🗑 Clear");
        clearButton.setBackground(Color.decode("#ef4444"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearAll());
        topPanel.add(clearButton);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.decode("#00ff00"));
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        topPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Split pane with code and output
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(Color.decode("#101826"));
        
        // Code area
        codeArea = new JTextArea();
        codeArea.setBackground(Color.BLACK);
        codeArea.setForeground(Color.decode("#d4d4d4"));
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        codeArea.setCaretColor(Color.WHITE);
        codeArea.setTabSize(4);
        codeArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStatus("Modified"); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStatus("Modified"); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStatus("Modified"); }
        });
        
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setTopComponent(codeScroll);
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.decode("#00ff00"));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setText("Code Studio Ready\n");
        outputArea.append("Select a language and start coding!\n");
        outputArea.append("Click 'Run' to execute your code.\n");
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBottomComponent(outputScroll);
        
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadTemplate(String language) {
        CodeTemplate template = templates.get(language);
        if (template != null) {
            codeArea.setText(template.template);
            updateStatus("Loaded " + language + " template");
        }
    }
    
    private void runCode() {
        String code = codeArea.getText();
        if (code.trim().isEmpty()) {
            outputArea.append("❌ No code to run!\n");
            return;
        }
        
        String language = currentLanguage;
        CodeTemplate template = templates.get(language);
        if (template == null) {
            outputArea.append("❌ Language not supported: " + language + "\n");
            return;
        }
        
        outputArea.append("\n" + "━".repeat(40) + "\n");
        outputArea.append("▶ Running " + language + " code...\n");
        outputArea.append("━".repeat(40) + "\n");
        
        updateStatus("Running...");
        runButton.setEnabled(false);
        
        new Thread(() -> {
            try {
                // Create temporary file
                Path tempFile = Files.createTempFile("neilos_code_", template.extension);
                Files.write(tempFile, code.getBytes());
                
                if (language.equals("HTML/CSS")) {
                    // Open in browser
                    Desktop.getDesktop().browse(tempFile.toUri());
                    outputArea.append("✅ HTML opened in browser\n");
                } else {
                    // Execute code
                    ProcessBuilder pb;
                    String runner = template.runner;
                    
                    if (language.equals("Java")) {
                        // Compile and run Java
                        pb = new ProcessBuilder("javac", tempFile.toString());
                        Process compile = pb.start();
                        int compileResult = compile.waitFor();
                        if (compileResult != 0) {
                            outputArea.append("❌ Compilation failed\n");
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(compile.getErrorStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    outputArea.append(line + "\n");
                                }
                            }
                            return;
                        }
                        // Run compiled class
                        String className = tempFile.getFileName().toString().replace(".java", "");
                        pb = new ProcessBuilder("java", "-cp", tempFile.getParent().toString(), className);
                    } else if (language.equals("C") || language.equals("C++")) {
                        // Compile C/C++
                        String outFile = tempFile.getParent() + "/output";
                        pb = new ProcessBuilder(runner, tempFile.toString(), "-o", outFile);
                        Process compile = pb.start();
                        int compileResult = compile.waitFor();
                        if (compileResult != 0) {
                            outputArea.append("❌ Compilation failed\n");
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(compile.getErrorStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    outputArea.append(line + "\n");
                                }
                            }
                            return;
                        }
                        // Run compiled binary
                        pb = new ProcessBuilder(outFile);
                    } else {
                        // Direct execution
                        pb = new ProcessBuilder(runner, tempFile.toString());
                    }
                    
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    
                    // Read output
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            outputArea.append(line + "\n");
                        }
                    }
                    
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        outputArea.append("\n✅ Program executed successfully (exit code: 0)\n");
                        updateStatus("Execution successful");
                    } else {
                        outputArea.append("\n⚠️ Program exited with code: " + exitCode + "\n");
                        updateStatus("Execution failed with code: " + exitCode);
                    }
                }
                
                // Cleanup
                Files.deleteIfExists(tempFile);
                // Cleanup compiled files
                if (language.equals("Java")) {
                    String className = tempFile.getFileName().toString().replace(".java", ".class");
                    Path classFile = tempFile.getParent().resolve(className);
                    Files.deleteIfExists(classFile);
                }
                if (language.equals("C") || language.equals("C++")) {
                    Path outFile = tempFile.getParent().resolve("output");
                    Files.deleteIfExists(outFile);
                }
                
            } catch (Exception e) {
                outputArea.append("❌ Error: " + e.getMessage() + "\n");
                updateStatus("Error: " + e.getMessage());
                e.printStackTrace(outputArea);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    runButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void saveCode() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Code");
        CodeTemplate template = templates.get(currentLanguage);
        chooser.setSelectedFile(new File("code" + template.extension));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(codeArea.getText());
                }
                updateStatus("Saved to: " + file.getName());
                outputArea.append("💾 Saved to: " + file.getName() + "\n");
            } catch (Exception e) {
                outputArea.append("❌ Error saving: " + e.getMessage() + "\n");
                updateStatus("Save failed");
            }
        }
    }
    
    private void loadCode() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Code");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String content = new String(Files.readAllBytes(file.toPath()));
                codeArea.setText(content);
                
                // Try to detect language from extension
                String name = file.getName();
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex > 0) {
                    String ext = name.substring(dotIndex);
                    for (Map.Entry<String, CodeTemplate> entry : templates.entrySet()) {
                        if (entry.getValue().extension.equals(ext)) {
                            languageSelector.setSelectedItem(entry.getKey());
                            currentLanguage = entry.getKey();
                            break;
                        }
                    }
                }
                
                updateStatus("Loaded: " + file.getName());
                outputArea.append("📂 Loaded: " + file.getName() + "\n");
            } catch (Exception e) {
                outputArea.append("❌ Error loading: " + e.getMessage() + "\n");
                updateStatus("Load failed");
            }
        }
    }
    
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Clear all code?", "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            codeArea.setText("");
            outputArea.setText("");
            outputArea.append("Code Studio Ready\n");
            outputArea.append("Select a language and start coding!\n");
            updateStatus("Cleared");
        }
    }
    
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("📌 " + message);
        });
    }
}