package com.neilos.ui;

import com.neilos.DesktopApp;
import com.neilos.NeilOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesktopRenderer extends JPanel {
    private NeilOS neilos;
    private List<DesktopApp> apps;
    private Map<Rectangle, String> clickAreas;
    private String currentTime;
    private DateTimeFormatter timeFormatter;
    private Timer timer;
    
    public DesktopRenderer(NeilOS neilos) {
        this.neilos = neilos;
        this.apps = new ArrayList<>();
        this.clickAreas = new HashMap<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        initializeApps();
        setupUI();
        startClock();
    }
    
    private void initializeApps() {
        apps.add(new DesktopApp("Bank", "💰", "bank"));
        apps.add(new DesktopApp("Files", "📁", "files"));
        apps.add(new DesktopApp("Terminal", "💻", "terminal"));
        apps.add(new DesktopApp("Search", "🔍", "search"));
        apps.add(new DesktopApp("Cyber", "🛡️", "cyber"));
        apps.add(new DesktopApp("Network", "🌐", "network"));
        apps.add(new DesktopApp("AI", "🤖", "ai"));
        apps.add(new DesktopApp("Monitor", "📊", "monitor"));
        apps.add(new DesktopApp("Calculator", "🧮", "calculator"));
        apps.add(new DesktopApp("Clinic", "🏥", "clinic"));
        apps.add(new DesktopApp("SocialNet", "🌍", "social"));
        apps.add(new DesktopApp("Deploy", "🚀", "deploy"));
        apps.add(new DesktopApp("API", "🔌", "api"));
        apps.add(new DesktopApp("Games", "🎮", "games"));
        apps.add(new DesktopApp("Kernel", "⚙️", "kernel"));
        apps.add(new DesktopApp("Notes", "📝", "notes"));
        apps.add(new DesktopApp("Code Studio", "💻", "code_studio"));
        apps.add(new DesktopApp("Animator", "🎬", "animator"));
    }
    
    private void setupUI() {
        setBackground(Color.decode("#0b1020"));
        setLayout(null);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    private void startClock() {
        timer = new Timer(1000, e -> {
            currentTime = LocalDateTime.now().format(timeFormatter);
            repaint();
        });
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawDesktop(g2d);
        drawAppIcons(g2d);
        drawTaskbar(g2d);
    }
    
    private void drawDesktop(Graphics2D g2d) {
        g2d.setColor(Color.decode("#0b1020"));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.decode("#00ffee"));
        g2d.setFont(new Font("Consolas", Font.BOLD, 14));
        g2d.drawString("NeilOS", 50, 60);
    }
    
    private void drawAppIcons(Graphics2D g2d) {
        clickAreas.clear();
        
        int startX = 50;
        int startY = 120;
        int width = 180;
        int height = 60;
        int padding = 120;
        int cols = 4;
        
        for (int i = 0; i < apps.size(); i++) {
            DesktopApp app = apps.get(i);
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * (width + padding);
            int y = startY + row * (height + padding);
            
            g2d.setColor(Color.decode("#111827"));
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            g2d.setColor(Color.decode("#00ffee"));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
            String text = app.getIcon() + " " + app.getName();
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(text)) / 2;
            int textY = y + height / 2 + fm.getHeight() / 3;
            g2d.drawString(text, textX, textY);
            
            clickAreas.put(new Rectangle(x, y, width, height), app.getAppId());
        }
    }
    
    private void drawTaskbar(Graphics2D g2d) {
        int taskbarY = getHeight() - 50;
        
        g2d.setColor(Color.decode("#111827"));
        g2d.fillRect(0, taskbarY, getWidth(), 50);
        
        String timeStr = currentTime != null ? currentTime : "00:00:00";
        g2d.setColor(Color.decode("#00ffee"));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(timeStr, getWidth() - fm.stringWidth(timeStr) - 20, taskbarY + 30);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("👤 " + NeilOS.currentUser, 20, taskbarY + 30);
        
        // App name
        g2d.setColor(Color.decode("#00ffee"));
        g2d.drawString("📱 " + NeilOS.currentApp, 120, taskbarY + 30);
    }
    
    public void handleClick(int x, int y) {
        for (Map.Entry<Rectangle, String> entry : clickAreas.entrySet()) {
            Rectangle rect = entry.getKey();
            if (rect.contains(x, y)) {
                neilos.switchApp(entry.getValue());
                return;
            }
        }
    }
    
    public void stopClock() {
        if (timer != null) {
            timer.stop();
        }
    }
}