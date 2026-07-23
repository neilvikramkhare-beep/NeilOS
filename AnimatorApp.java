package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple animator with shapes and keyframes
 */
public class AnimatorApp extends JPanel {
    private NeilOS neilos;
    private JPanel canvasPanel;
    private JPanel timelinePanel;
    private JPanel controlPanel;
    private JComboBox<String> shapeSelector;
    private JButton addButton;
    private JButton playButton;
    private JButton stopButton;
    private JButton clearButton;
    private JSlider speedSlider;
    private JLabel frameLabel;
    private JTextArea infoArea;
    
    private List<AnimatedObject> objects;
    private Timer animationTimer;
    private int currentFrame;
    private boolean isPlaying;
    private int fps;
    private Random random;
    
    private static class AnimatedObject {
        String type;
        int x, y;
        int startX, startY;
        int endX, endY;
        int size;
        Color color;
        int duration;
        int startFrame;
        
        AnimatedObject(String type, int x, int y, int size, Color color) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.startX = x;
            this.startY = y;
            this.endX = x + randomOffset();
            this.endY = y + randomOffset();
            this.size = size;
            this.color = color;
            this.duration = 30 + new Random().nextInt(30);
            this.startFrame = 0;
        }
        
        private int randomOffset() {
            return new Random().nextInt(100) - 50;
        }
    }
    
    public AnimatorApp(NeilOS neilos) {
        this.neilos = neilos;
        this.objects = new ArrayList<>();
        this.random = new Random();
        this.fps = 30;
        this.currentFrame = 0;
        this.isPlaying = false;
        
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        
        // Start animation timer
        animationTimer = new Timer(1000 / fps, e -> {
            if (isPlaying) {
                currentFrame++;
                updateAnimation();
                canvasPanel.repaint();
                updateTimeline();
            }
        });
        animationTimer.start();
    }
    
    private void createUI() {
        // Main split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setBackground(Color.decode("#101826"));
        
        // Canvas panel
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(Color.decode("#1a1a2e"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Grid
                g2d.setColor(Color.decode("#2a2a3e"));
                for (int x = 0; x < getWidth(); x += 50) {
                    g2d.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 50) {
                    g2d.drawLine(0, y, getWidth(), y);
                }
                
                // Draw objects
                for (AnimatedObject obj : objects) {
                    g2d.setColor(obj.color);
                    int x = obj.x;
                    int y = obj.y;
                    int size = obj.size;
                    
                    switch (obj.type) {
                        case "Circle":
                            g2d.fillOval(x - size/2, y - size/2, size, size);
                            break;
                        case "Square":
                            g2d.fillRect(x - size/2, y - size/2, size, size);
                            break;
                        case "Triangle":
                            int[] xPoints = {x, x - size/2, x + size/2};
                            int[] yPoints = {y - size/2, y + size/2, y + size/2};
                            g2d.fillPolygon(xPoints, yPoints, 3);
                            break;
                        case "Star":
                            drawStar(g2d, x, y, size/2);
                            break;
                    }
                }
                
                // Info overlay
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
                g2d.drawString("Frame: " + currentFrame, 10, 20);
                g2d.drawString("Objects: " + objects.size(), 10, 35);
                g2d.drawString("FPS: " + fps, 10, 50);
            }
        };
        canvasPanel.setBackground(Color.decode("#1a1a2e"));
        canvasPanel.setPreferredSize(new Dimension(800, 400));
        mainSplit.setTopComponent(canvasPanel);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#0f172a"));
        
        // Timeline
        timelinePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.decode("#1e1e2e"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Timeline bars for each object
                int yPos = 10;
                for (int i = 0; i < objects.size() && i < 5; i++) {
                    AnimatedObject obj = objects.get(i);
                    g2d.setColor(obj.color);
                    int barWidth = (int)((double)obj.duration / 60 * getWidth());
                    g2d.fillRect(10, yPos, Math.min(barWidth, getWidth() - 20), 20);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Consolas", Font.PLAIN, 8));
                    g2d.drawString(obj.type + " (" + obj.duration + "f)", 15, yPos + 15);
                    yPos += 30;
                }
                
                // Current position marker
                int markerX = (int)((double)currentFrame / 60 * getWidth());
                g2d.setColor(Color.decode("#ef4444"));
                g2d.drawLine(markerX, 0, markerX, getHeight());
            }
        };
        timelinePanel.setBackground(Color.decode("#1e1e2e"));
        timelinePanel.setPreferredSize(new Dimension(800, 120));
        bottomPanel.add(timelinePanel, BorderLayout.CENTER);
        
        // Control panel
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(Color.decode("#0f172a"));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        shapeSelector = new JComboBox<>(new String[]{"Circle", "Square", "Triangle", "Star"});
        shapeSelector.setBackground(Color.decode("#1e1e1e"));
        shapeSelector.setForeground(Color.WHITE);
        controlPanel.add(shapeSelector);
        
        addButton = new JButton("➕ Add Shape");
        addButton.setBackground(Color.decode("#10b981"));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addShape());
        controlPanel.add(addButton);
        
        playButton = new JButton("▶ Play");
        playButton.setBackground(Color.decode("#3b82f6"));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.addActionListener(e -> togglePlay());
        controlPanel.add(playButton);
        
        stopButton = new JButton("⏹ Stop");
        stopButton.setBackground(Color.decode("#ef4444"));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.addActionListener(e -> stopAnimation());
        controlPanel.add(stopButton);
        
        clearButton = new JButton("🗑 Clear All");
        clearButton.setBackground(Color.decode("#f59e0b"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearAll());
        controlPanel.add(clearButton);
        
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setForeground(Color.WHITE);
        controlPanel.add(speedLabel);
        
        speedSlider = new JSlider(5, 60, 30);
        speedSlider.setBackground(Color.decode("#0f172a"));
        speedSlider.setForeground(Color.WHITE);
        speedSlider.addChangeListener(e -> {
            fps = speedSlider.getValue();
            animationTimer.setDelay(1000 / fps);
        });
        controlPanel.add(speedSlider);
        
        frameLabel = new JLabel("Frame: 0");
        frameLabel.setForeground(Color.WHITE);
        frameLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        controlPanel.add(frameLabel);
        
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        mainSplit.setBottomComponent(bottomPanel);
        mainSplit.setDividerLocation(450);
        
        add(mainSplit, BorderLayout.CENTER);
        
        // Info area
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(Color.BLACK);
        infoArea.setForeground(Color.decode("#00ff00"));
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        infoArea.setText("🎬 Animator Studio\n");
        infoArea.append("Add shapes and click Play to animate!\n");
        infoArea.append("Each shape will move randomly across the canvas.\n");
        
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setPreferredSize(new Dimension(200, 80));
        infoScroll.setBorder(BorderFactory.createEmptyBorder());
        add(infoScroll, BorderLayout.SOUTH);
    }
    
    private void addShape() {
        String type = (String) shapeSelector.getSelectedItem();
        int x = random.nextInt(canvasPanel.getWidth() - 100) + 50;
        int y = random.nextInt(canvasPanel.getHeight() - 100) + 50;
        int size = random.nextInt(40) + 30;
        
        Color color = new Color(
            random.nextInt(200) + 55,
            random.nextInt(200) + 55,
            random.nextInt(200) + 55
        );
        
        AnimatedObject obj = new AnimatedObject(type, x, y, size, color);
        objects.add(obj);
        
        infoArea.append("✅ Added " + type + " at (" + x + ", " + y + ")\n");
        infoArea.setCaretPosition(infoArea.getDocument().getLength());
        
        canvasPanel.repaint();
        updateTimeline();
    }
    
    private void updateAnimation() {
        for (AnimatedObject obj : objects) {
            int framesSinceStart = currentFrame - obj.startFrame;
            if (framesSinceStart < obj.duration) {
                float progress = (float) framesSinceStart / obj.duration;
                obj.x = (int)(obj.startX + (obj.endX - obj.startX) * progress);
                obj.y = (int)(obj.startY + (obj.endY - obj.startY) * progress);
                
                // Add some oscillation
                obj.x += (int)(Math.sin(progress * 10) * 5);
                obj.y += (int)(Math.cos(progress * 5) * 5);
            } else {
                // Reset animation
                obj.startX = obj.x;
                obj.startY = obj.y;
                obj.endX = obj.x + randomOffset();
                obj.endY = obj.y + randomOffset();
                obj.startFrame = currentFrame;
                obj.duration = 30 + random.nextInt(30);
                obj.size = random.nextInt(20) + 20;
            }
        }
    }
    
    private int randomOffset() {
        return random.nextInt(150) - 75;
    }
    
    private void updateTimeline() {
        timelinePanel.repaint();
        frameLabel.setText("Frame: " + currentFrame);
    }
    
    private void togglePlay() {
        isPlaying = !isPlaying;
        playButton.setText(isPlaying ? "⏸ Pause" : "▶ Play");
        if (isPlaying) {
            infoArea.append("▶ Animation playing\n");
        } else {
            infoArea.append("⏸ Animation paused\n");
        }
        infoArea.setCaretPosition(infoArea.getDocument().getLength());
    }
    
    private void stopAnimation() {
        isPlaying = false;
        playButton.setText("▶ Play");
        currentFrame = 0;
        for (AnimatedObject obj : objects) {
            obj.x = obj.startX;
            obj.y = obj.startY;
            obj.startFrame = 0;
        }
        canvasPanel.repaint();
        updateTimeline();
        infoArea.append("⏹ Animation stopped\n");
        infoArea.setCaretPosition(infoArea.getDocument().getLength());
    }
    
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Clear all objects?", "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            objects.clear();
            currentFrame = 0;
            isPlaying = false;
            playButton.setText("▶ Play");
            canvasPanel.repaint();
            updateTimeline();
            infoArea.append("🗑 All objects cleared\n");
            infoArea.setCaretPosition(infoArea.getDocument().getLength());
        }
    }
    
    private void drawStar(Graphics2D g2d, int x, int y, int radius) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        double angle = -Math.PI / 2;
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2;
            xPoints[i] = (int)(x + r * Math.cos(angle + i * 2 * Math.PI / 10));
            yPoints[i] = (int)(y + r * Math.sin(angle + i * 2 * Math.PI / 10));
        }
        g2d.fillPolygon(xPoints, yPoints, 10);
    }
}