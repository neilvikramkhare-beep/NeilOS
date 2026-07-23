package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Notes application with notepad integration
 */
public class NotesApp extends JPanel {
    private NeilOS neilos;
    private JTextArea notesArea;
    private JButton addButton;
    private JButton viewButton;
    private JButton notepadButton;
    private JLabel countLabel;
    
    public NotesApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        refreshNotes();
    }
    
    private void createUI() {
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        addButton = new JButton("📝 Add Note");
        addButton.setBackground(Color.decode("#f59e0b"));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addNote());
        topPanel.add(addButton);
        
        viewButton = new JButton("📋 Show Notes");
        viewButton.setBackground(Color.decode("#3b82f6"));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(e -> viewNotes());
        topPanel.add(viewButton);
        
        notepadButton = new JButton("📄 Open Notepad");
        notepadButton.setBackground(Color.decode("#10b981"));
        notepadButton.setForeground(Color.WHITE);
        notepadButton.setFocusPainted(false);
        notepadButton.addActionListener(e -> openNotepad());
        topPanel.add(notepadButton);
        
        countLabel = new JLabel("Total Notes: 0");
        countLabel.setForeground(Color.decode("#f59e0b"));
        countLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        topPanel.add(countLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Notes display
        notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setBackground(Color.decode("#1e1e1e"));
        notesArea.setForeground(Color.decode("#a0aec0"));
        notesArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        notesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addNote() {
        String note = JOptionPane.showInputDialog(this, "Write note:", "Add Note", JOptionPane.QUESTION_MESSAGE);
        if (note != null && !note.isEmpty()) {
            try {
                neilos.getDbManager().saveNote(note);
                refreshNotes();
                JOptionPane.showMessageDialog(this, "Note saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewNotes() {
        try {
            List<String> notes = neilos.getDbManager().loadNotes();
            if (notes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No notes saved.", "Notes", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < notes.size(); i++) {
                sb.append(i + 1).append(". ").append(notes.get(i)).append("\n\n");
            }
            
            JOptionPane.showMessageDialog(this, sb.toString(), "Saved Notes", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openNotepad() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("notepad.exe");
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", "-a", "TextEdit"});
            } else {
                // Try to open a text editor on Linux
                try {
                    Runtime.getRuntime().exec(new String[]{"gedit"});
                } catch (Exception e) {
                    // Fallback to system default editor
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "~"});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open notepad. Please open manually.", 
                "Notepad", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void refreshNotes() {
        try {
            List<String> notes = neilos.getDbManager().loadNotes();
            countLabel.setText("Total Notes: " + notes.size());
            
            if (notes.isEmpty()) {
                notesArea.setText("No notes saved.\nClick 'Add Note' to create one.");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("📌 Recent Notes:\n");
            sb.append("━".repeat(30)).append("\n\n");
            
            // Show last 10 notes
            int start = Math.max(0, notes.size() - 10);
            for (int i = start; i < notes.size(); i++) {
                String note = notes.get(i);
                String preview = note.length() > 60 ? note.substring(0, 60) + "..." : note;
                sb.append(i + 1).append(". ").append(preview).append("\n");
            }
            
            if (notes.size() > 10) {
                sb.append("\n... and ").append(notes.size() - 10).append(" more notes.");
            }
            
            notesArea.setText(sb.toString());
        } catch (SQLException e) {
            notesArea.setText("Error loading notes: " + e.getMessage());
        }
    }
}