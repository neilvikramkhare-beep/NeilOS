package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File Explorer with desktop integration
 */
public class FileExplorer extends JPanel {
    private NeilOS neilos;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTextArea fileContentArea;
    private JLabel pathLabel;
    private File currentDirectory;
    
    public FileExplorer(NeilOS neilos) {
        this.neilos = neilos;
        this.currentDirectory = new File(System.getProperty("user.home") + "/Desktop");
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        refreshFileTree();
    }
    
    private void createUI() {
        // Top panel with actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton createBtn = new JButton("📄 Create File");
        createBtn.setBackground(Color.decode("#f59e0b"));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> createFile());
        topPanel.add(createBtn);
        
        JButton deleteBtn = new JButton("🗑 Delete");
        deleteBtn.setBackground(Color.decode("#ef4444"));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteFile());
        topPanel.add(deleteBtn);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(Color.decode("#3b82f6"));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshFileTree());
        topPanel.add(refreshBtn);
        
        JButton openExplorerBtn = new JButton("📂 Open Desktop");
        openExplorerBtn.setBackground(Color.decode("#10b981"));
        openExplorerBtn.setForeground(Color.WHITE);
        openExplorerBtn.setFocusPainted(false);
        openExplorerBtn.addActionListener(e -> openSystemExplorer());
        topPanel.add(openExplorerBtn);
        
        pathLabel = new JLabel("📍 " + currentDirectory.getAbsolutePath());
        pathLabel.setForeground(Color.decode("#f59e0b"));
        pathLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        topPanel.add(pathLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Split pane with tree and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(Color.decode("#101826"));
        
        // File tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Desktop");
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.setBackground(Color.decode("#1e1e1e"));
        fileTree.setForeground(Color.WHITE);
        fileTree.setFont(new Font("Consolas", Font.PLAIN, 11));
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                String fileName = node.toString();
                File file = new File(currentDirectory, fileName);
                if (file.exists() && file.isFile()) {
                    displayFileContent(file);
                }
            }
        });
        
        JScrollPane treeScroll = new JScrollPane(fileTree);
        treeScroll.setBackground(Color.decode("#1e1e1e"));
        splitPane.setLeftComponent(treeScroll);
        
        // File content area
        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);
        fileContentArea.setBackground(Color.BLACK);
        fileContentArea.setForeground(Color.decode("#00ff00"));
        fileContentArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane contentScroll = new JScrollPane(fileContentArea);
        contentScroll.setBackground(Color.BLACK);
        splitPane.setRightComponent(contentScroll);
        
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void refreshFileTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();
        
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isDirectory()) {
                    name = "📁 " + name;
                } else {
                    name = "📄 " + name;
                }
                root.add(new DefaultMutableTreeNode(name));
            }
        }
        
        treeModel.reload();
        fileContentArea.setText("Select a file to view its contents.");
        pathLabel.setText("📍 " + currentDirectory.getAbsolutePath());
    }
    
    private void displayFileContent(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            fileContentArea.setText(content);
            fileContentArea.setCaretPosition(0);
        } catch (Exception e) {
            fileContentArea.setText("Could not display file content:\n" + e.getMessage());
        }
    }
    
    private void createFile() {
        String name = JOptionPane.showInputDialog(this, "Enter filename:", "Create File", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.isEmpty()) {
            try {
                File newFile = new File(currentDirectory, name);
                if (newFile.createNewFile()) {
                    Files.write(newFile.toPath(), ("Created by NeilOS on " + 
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n").getBytes());
                    NeilOS.files.add(name);
                    refreshFileTree();
                    JOptionPane.showMessageDialog(this, "File created: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteFile() {
        String name = JOptionPane.showInputDialog(this, "Enter filename to delete:", "Delete File", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.isEmpty()) {
            File file = new File(currentDirectory, name);
            if (file.exists()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Delete file: " + name + "?", "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (file.delete()) {
                        NeilOS.files.remove(name);
                        refreshFileTree();
                        JOptionPane.showMessageDialog(this, "Deleted: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not delete file!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openSystemExplorer() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("explorer " + currentDirectory.getAbsolutePath());
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + currentDirectory.getAbsolutePath());
            } else {
                Runtime.getRuntime().exec("xdg-open " + currentDirectory.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open explorer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}