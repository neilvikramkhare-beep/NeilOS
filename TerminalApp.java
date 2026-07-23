package com.neilos.apps;

import com.neilos.NeilOS;
import com.neilos.terminal.TerminalMode;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Terminal application with multiple terminal modes
 */
public class TerminalApp extends JPanel {
    private NeilOS neilos;
    private JTextArea displayArea;
    private JTextField commandField;
    private JComboBox<String> modeSelector;
    private TerminalMode currentMode;
    private List<String> history;
    private int historyIndex;
    
    public TerminalApp(NeilOS neilos) {
        this.neilos = neilos;
        this.currentMode = NeilOS.currentTerminalMode;
        this.history = new ArrayList<>(NeilOS.terminalHistory);
        this.historyIndex = history.size();
        
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
    }
    
    private void createUI() {
        // Top panel with mode selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setForeground(Color.WHITE);
        topPanel.add(modeLabel);
        
        modeSelector = new JComboBox<>(new String[]{"Ubuntu", "PowerShell", "Command Prompt", "Bash"});
        modeSelector.setSelectedItem("Ubuntu");
        modeSelector.setBackground(Color.decode("#111827"));
        modeSelector.setForeground(Color.WHITE);
        modeSelector.addActionListener(e -> changeMode());
        topPanel.add(modeSelector);
        
        JButton helpButton = new JButton("Help");
        helpButton.setBackground(Color.decode("#3b82f6"));
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.addActionListener(e -> showHelp());
        topPanel.add(helpButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(Color.decode("#ef4444"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> displayArea.setText(""));
        topPanel.add(clearButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.decode("#00ff00"));
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // Command input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#0f172a"));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel promptLabel = new JLabel(currentMode.getPrompt());
        promptLabel.setForeground(Color.decode("#00ffee"));
        promptLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        bottomPanel.add(promptLabel, BorderLayout.WEST);
        
        commandField = new JTextField();
        commandField.setBackground(Color.BLACK);
        commandField.setForeground(Color.WHITE);
        commandField.setFont(new Font("Consolas", Font.PLAIN, 12));
        commandField.setCaretColor(Color.WHITE);
        commandField.addActionListener(e -> executeCommand());
        bottomPanel.add(commandField, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add initial messages
        displayArea.append("NeilOS Terminal\n");
        displayArea.append("Type help for available commands\n");
        displayArea.append("Current mode: " + currentMode.name() + "\n\n");
    }
    
    private void changeMode() {
        String selected = (String) modeSelector.getSelectedItem();
        switch (selected) {
            case "Ubuntu":
                currentMode = TerminalMode.UBUNTU;
                break;
            case "PowerShell":
                currentMode = TerminalMode.POWERSHELL;
                break;
            case "Command Prompt":
                currentMode = TerminalMode.CMD;
                break;
            case "Bash":
                currentMode = TerminalMode.BASH;
                break;
        }
        NeilOS.currentTerminalMode = currentMode;
        
        // Update prompt
        Component[] components = ((JPanel) getComponent(2)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(currentMode.getPrompt());
                break;
            }
        }
        
        displayArea.append("\n[MODE] Switched to " + selected + "\n");
    }
    
    private void executeCommand() {
        String cmd = commandField.getText().trim();
        if (cmd.isEmpty()) {
            return;
        }
        
        // Add to history
        history.add(cmd);
        historyIndex = history.size();
        commandField.setText("");
        
        // Display command
        displayArea.append(currentMode.getPrompt() + cmd + "\n");
        
        // Process command
        String output = processCommand(cmd);
        if (!output.isEmpty()) {
            displayArea.append(output + "\n");
        }
        
        // Scroll to bottom
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }
    
    private String processCommand(String cmd) {
        String lowerCmd = cmd.toLowerCase();
        
        // Help command
        if (lowerCmd.equals("help") || lowerCmd.equals("?")) {
            return getHelpText();
        }
        
        // Clear command
        if (lowerCmd.equals("clear") || lowerCmd.equals("cls")) {
            displayArea.setText("");
            return "";
        }
        
        // Check if it's a built-in command
        if (currentMode.hasCommand(lowerCmd)) {
            return "Command: " + cmd + "\n" + currentMode.getCommandDescription(lowerCmd);
        }
        
        // Try to execute as system command
        try {
            ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                pb = new ProcessBuilder("cmd.exe", "/c", cmd);
            } else {
                pb = new ProcessBuilder("bash", "-c", cmd);
            }
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command exited with code: ").append(exitCode);
            }
            
            return output.toString();
        } catch (Exception e) {
            return "Command not found or error: " + e.getMessage();
        }
    }
    
    private String getHelpText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands for ").append(currentMode.name()).append(":\n");
        sb.append("━".repeat(40)).append("\n");
        
        for (var entry : currentMode.getCommands().entrySet()) {
            sb.append("  ").append(entry.getKey());
            sb.append(" ".repeat(Math.max(1, 20 - entry.getKey().length())));
            sb.append(entry.getValue()).append("\n");
        }
        
        sb.append("\nSpecial commands:\n");
        sb.append("  help / ?          Show this help message\n");
        sb.append("  clear / cls       Clear the terminal\n");
        sb.append("\nType any other command to execute in the system shell.\n");
        
        return sb.toString();
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, getHelpText(), "Help - " + currentMode.name(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
}