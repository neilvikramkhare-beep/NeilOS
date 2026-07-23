package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * Enhanced calculator with mathematical functions
 */
public class CalculatorApp extends JPanel {
    private JTextField display;
    private double currentValue;
    private String currentOperator;
    private boolean newNumber;
    
    public CalculatorApp(NeilOS neilos) {
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
    }
    
    private void createUI() {
        // Display
        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Consolas", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(Color.decode("#1e1e1e"));
        display.setForeground(Color.decode("#00ff00"));
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBackground(Color.decode("#101826"));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[][] buttons = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"},
            {"C", "√", "^", "%"}
        };
        
        for (String[] row : buttons) {
            for (String text : row) {
                JButton btn = createButton(text);
                buttonPanel.add(btn);
            }
        }
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 16));
        btn.setBackground(Color.decode("#3d3d3d"));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.decode("#555555")));
        btn.addActionListener(e -> handleButtonClick(text));
        
        if (text.matches("[0-9.]")) {
            btn.setBackground(Color.decode("#2d2d2d"));
        } else if (text.matches("[+\\-*/]")) {
            btn.setBackground(Color.decode("#1a3a5c"));
            btn.setForeground(Color.decode("#4fc3f7"));
        } else if (text.equals("=")) {
            btn.setBackground(Color.decode("#2e7d32"));
            btn.setForeground(Color.decode("#81c784"));
        } else if (text.equals("C")) {
            btn.setBackground(Color.decode("#7f1d1d"));
            btn.setForeground(Color.decode("#ef5350"));
        } else if (text.matches("[√^%]")) {
            btn.setBackground(Color.decode("#4a148c"));
            btn.setForeground(Color.decode("#ce93d8"));
        }
        
        return btn;
    }
    
    private void handleButtonClick(String text) {
        String currentText = display.getText();
        
        switch (text) {
            case "C":
                clear();
                break;
            case "=":
                calculate();
                break;
            case "√":
                sqrt();
                break;
            case "^":
                display.setText(currentText + " ^ ");
                break;
            case "%":
                percentage();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                setOperator(text);
                break;
            default:
                // Number or decimal
                if (currentText.equals("0") || newNumber) {
                    display.setText(text);
                    newNumber = false;
                } else {
                    display.setText(currentText + text);
                }
                break;
        }
    }
    
    private void clear() {
        display.setText("0");
        currentValue = 0;
        currentOperator = null;
        newNumber = false;
    }
    
    private void setOperator(String operator) {
        try {
            currentValue = Double.parseDouble(display.getText());
            currentOperator = operator;
            newNumber = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
    
    private void calculate() {
        if (currentOperator == null) {
            return;
        }
        
        try {
            double secondValue = Double.parseDouble(display.getText());
            double result = 0;
            
            switch (currentOperator) {
                case "+":
                    result = currentValue + secondValue;
                    break;
                case "-":
                    result = currentValue - secondValue;
                    break;
                case "*":
                    result = currentValue * secondValue;
                    break;
                case "/":
                    if (secondValue != 0) {
                        result = currentValue / secondValue;
                    } else {
                        display.setText("Error: Division by zero");
                        return;
                    }
                    break;
            }
            
            display.setText(String.valueOf(result));
            currentOperator = null;
            newNumber = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
    
    private void sqrt() {
        try {
            double value = Double.parseDouble(display.getText());
            if (value < 0) {
                display.setText("Error: Negative sqrt");
                return;
            }
            display.setText(String.valueOf(Math.sqrt(value)));
            newNumber = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
    
    private void percentage() {
        try {
            double value = Double.parseDouble(display.getText());
            display.setText(String.valueOf(value / 100));
            newNumber = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
}