package com.neilos.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages application windows with title bars and close buttons
 */
public class WindowManager {
    private JFrame parent;
    private Map<String, JDialog> windows;
    
    public WindowManager(JFrame parent) {
        this.parent = parent;
        this.windows = new HashMap<>();
    }
    
    public JDialog createWindow(String title, String id, Color accentColor, Component content) {
        JDialog dialog = new JDialog(parent, title, false);
        dialog.setModal(false);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setBackground(Color.decode("#101826"));
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(accentColor);
        titleBar.setPreferredSize(new Dimension(800, 35));
        titleBar.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        JButton closeButton = new JButton("✕");
        closeButton.setBackground(Color.decode("#ef4444"));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(35, 35));
        closeButton.addActionListener(e -> {
            dialog.dispose();
            windows.remove(id);
        });
        titleBar.add(closeButton, BorderLayout.EAST);
        
        // Make title bar draggable
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int x, y;
            
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                dialog.setLocation(e.getXOnScreen() - x, e.getYOnScreen() - y);
            }
        };
        titleBar.addMouseListener(dragAdapter);
        titleBar.addMouseMotionListener(dragAdapter);
        
        dialog.add(titleBar, BorderLayout.NORTH);
        dialog.add(content, BorderLayout.CENTER);
        
        windows.put(id, dialog);
        return dialog;
    }
    
    public void closeWindow(String id) {
        JDialog dialog = windows.get(id);
        if (dialog != null) {
            dialog.dispose();
            windows.remove(id);
        }
    }
    
    public void closeAllWindows() {
        for (JDialog dialog : windows.values()) {
            dialog.dispose();
        }
        windows.clear();
    }
}