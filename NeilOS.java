package com.neilos;

import com.neilos.apps.*;
import com.neilos.database.DatabaseManager;
import com.neilos.security.AntivirusScanner;
import com.neilos.terminal.TerminalMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * NeilOS - Java Virtual Machine Implementation
 * Main entry point for the NeilOS desktop environment
 */
public class NeilOS extends JFrame {
    private static NeilOS instance;
    
    public static final int SCREEN_WIDTH = 1400;
    public static final int SCREEN_HEIGHT = 850;
    
    public static String currentApp = "desktop";
    public static String currentUser = "guest";
    public static String currentTheme = "Dark";
    public static final List<String> themes = Arrays.asList("Dark", "Light", "Blue", "Matrix", "Neon");
    
    // Data stores
    public static final List<String> files = new ArrayList<>(Arrays.asList(
        "kernel.sys", "config.cfg", "root.py", "notes.txt"
    ));
    public static final List<String> terminalHistory = new ArrayList<>();
    public static final List<Map<String, Object>> socialPosts = new ArrayList<>();
    public static final List<Map<String, Object>> patients = new ArrayList<>();
    public static final List<String> cyberLog = new ArrayList<>();
    public static final List<String> kernelLogs = new ArrayList<>();
    public static final List<String> deploymentLogs = new ArrayList<>();
    public static final List<String> aiLog = new ArrayList<>();
    public static final List<String> installedPackages = new ArrayList<>(Arrays.asList(
        "kernel", "calculator", "terminal", "browser"
    ));
    public static final List<String> runningProcesses = new ArrayList<>();
    public static final List<String> API_LIST = new ArrayList<>(Arrays.asList(
        "Weather API", "Maps API", "Payments API", "AI API"
    ));
    
    // Banking
    public static double bankBalance = 5000.0;
    public static double loanBalance = 0.0;
    public static final double loanInterestRate = 10.0;
    public static final List<String> bankLedger = new ArrayList<>();
    
    // Terminal
    public static TerminalMode currentTerminalMode;
    
    // Quarantine
    public static final String quarantineDir = "quarantine";
    public static final String logsDir = "logs";
    public static final String reportFile = "scan_report.txt";
    
    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> appPanels;
    private DesktopRenderer desktopRenderer;
    
    // Database
    private DatabaseManager dbManager;
    
    // Security
    private AntivirusScanner antivirusScanner;
    
    public NeilOS() {
        instance = this;
        
        // Initialize directories
        new File(quarantineDir).mkdirs();
        new File(logsDir).mkdirs();
        
        // Initialize database
        try {
            dbManager = new DatabaseManager();
            dbManager.initialize();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
        
        // Initialize terminal
        currentTerminalMode = TerminalMode.UBUNTU;
        terminalHistory.add("NeilOS Terminal");
        terminalHistory.add("Type help for commands");
        
        // Initialize security
        antivirusScanner = new AntivirusScanner();
        
        // Initialize logs
        kernelLogs.add("Kernel boot sequence initialized.");
        cyberLog.add("Cyber Security Core Ready");
        aiLog.add("AI: Hello!");
        bankLedger.add("[SYSTEM] Initial Deposit +5000");
        
        // Setup UI
        setupUI();
        
        // Show boot screen
        showBootScreen();
        
        // Start real-time protection if available
        startRealtimeProtection();
    }
    
    private void setupUI() {
        setTitle("NeilOS Advanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setBackground(Color.decode("#0b1020"));
        setLayout(new BorderLayout());
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#0b1020"));
        add(mainPanel);
        
        cardLayout = new CardLayout();
        appPanels = new HashMap<>();
        
        // Initialize desktop renderer
        desktopRenderer = new DesktopRenderer(this);
        appPanels.put("desktop", desktopRenderer);
        
        // Initialize all app panels
        initializeApps();
        
        // Create container panel for card layout
        JPanel cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(Color.decode("#0b1020"));
        
        for (Map.Entry<String, JPanel> entry : appPanels.entrySet()) {
            cardContainer.add(entry.getValue(), entry.getKey());
        }
        
        mainPanel.add(cardContainer, BorderLayout.CENTER);
        
        // Add mouse listener for click detection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    private void initializeApps() {
        appPanels.put("bank", new BankApp(this));
        appPanels.put("files", new FileExplorer(this));
        appPanels.put("terminal", new TerminalApp(this));
        appPanels.put("search", new SearchApp(this));
        appPanels.put("cyber", new CyberSecurityApp(this));
        appPanels.put("network", new NetworkApp(this));
        appPanels.put("ai", new AIApp(this));
        appPanels.put("monitor", new SystemMonitor(this));
        appPanels.put("calculator", new CalculatorApp(this));
        appPanels.put("clinic", new ClinicApp(this));
        appPanels.put("social", new SocialApp(this));
        appPanels.put("deploy", new DeployApp(this));
        appPanels.put("api", new APIApp(this));
        appPanels.put("games", new GamesApp(this));
        appPanels.put("kernel", new KernelApp(this));
        appPanels.put("notes", new NotesApp(this));
        appPanels.put("code_studio", new CodeStudioApp(this));
        appPanels.put("animator", new AnimatorApp(this));
    }
    
    private void showBootScreen() {
        JDialog bootDialog = new JDialog(this, "NeilOS Booting", true);
        bootDialog.setUndecorated(true);
        bootDialog.setSize(600, 400);
        bootDialog.setLocationRelativeTo(this);
        bootDialog.setBackground(Color.BLACK);
        
        JPanel bootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bootPanel.setLayout(null);
        bootDialog.add(bootPanel);
        
        // Title
        JLabel titleLabel = new JLabel("NeilOS");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#00ffee"));
        titleLabel.setBounds(200, 50, 200, 50);
        bootPanel.add(titleLabel);
        
        // Boot messages
        JLabel[] messages = new JLabel[8];
        String[] bootStages = {
            "Loading Kernel...",
            "Loading Graphics Engine...",
            "Mounting File System...",
            "Loading Applications...",
            "Loading Code Studio...",
            "Loading Animator Studio with AI...",
            "Loading Antivirus Protection...",
            "Desktop Ready..."
        };
        
        for (int i = 0; i < messages.length; i++) {
            messages[i] = new JLabel(bootStages[i]);
            messages[i].setFont(new Font("Consolas", Font.PLAIN, 12));
            messages[i].setForeground(Color.decode("#00ff88"));
            messages[i].setBounds(100, 120 + i * 30, 400, 20);
            messages[i].setVisible(false);
            bootPanel.add(messages[i]);
        }
        
        bootDialog.setVisible(true);
        
        // Animate boot
        new Thread(() -> {
            for (int i = 0; i < messages.length; i++) {
                try {
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() -> {
                        messages[i].setVisible(true);
                        bootDialog.repaint();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            SwingUtilities.invokeLater(() -> {
                bootDialog.dispose();
                setVisible(true);
                switchApp("desktop");
            });
        }).start();
    }
    
    private void handleClick(int x, int y) {
        desktopRenderer.handleClick(x, y);
    }
    
    public void switchApp(String appId) {
        currentApp = appId;
        if (appPanels.containsKey(appId)) {
            JPanel container = (JPanel) mainPanel.getComponent(0);
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, appId);
        }
        repaint();
    }
    
    private void startRealtimeProtection() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
    
    public DatabaseManager getDbManager() {
        return dbManager;
    }
    
    public AntivirusScanner getAntivirusScanner() {
        return antivirusScanner;
    }
    
    public static NeilOS getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NeilOS();
        });
    }
}