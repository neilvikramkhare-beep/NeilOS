package com.neilos.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Games hub with Guess Game, Snakes and Ladders, and Maze Solver
 */
public class GamesApp extends JPanel {
    private NeilOS neilos;
    
    public GamesApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
    }
    
    private void createUI() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.decode("#101826"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[][] games = {
            {"🎯 Guess Number", "#a855f7", this::guessGame},
            {"🐍 Snakes & Ladders", "#10b981", this::snakesAndLadders},
            {"🧩 Maze Solver", "#3b82f6", this::mazeSolver}
        };
        
        for (int i = 0; i < games.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            JButton btn = new JButton(games[i][0]);
            btn.setFont(new Font("Consolas", Font.BOLD, 14));
            btn.setBackground(Color.decode(games[i][1]));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(200, 60));
            btn.addActionListener(e -> ((Runnable) games[i][2]).run());
            centerPanel.add(btn, gbc);
        }
        
        // Game description
        JLabel descLabel = new JLabel("🎮 Available Games:");
        descLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        descLabel.setForeground(Color.decode("#a855f7"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        centerPanel.add(descLabel, gbc);
        
        String[] descriptions = {
            "• Guess Number - Classic number guessing game (1-10)",
            "• Snakes & Ladders - Classic board game with dice",
            "• Maze Solver - Find the path through the maze"
        };
        
        for (int i = 0; i < descriptions.length; i++) {
            JLabel label = new JLabel(descriptions[i]);
            label.setFont(new Font("Consolas", Font.PLAIN, 12));
            label.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 2 + i;
            gbc.gridwidth = 3;
            centerPanel.add(label, gbc);
        }
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void guessGame() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Guess Number Game", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#1e1e1e"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Guess a number between 1 and 10!");
        messageLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.NORTH);
        
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.BOLD, 20));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.decode("#00ff00"));
        mainPanel.add(inputField, BorderLayout.CENTER);
        
        JButton guessButton = new JButton("Guess!");
        guessButton.setFont(new Font("Consolas", Font.BOLD, 14));
        guessButton.setBackground(Color.decode("#4CAF50"));
        guessButton.setForeground(Color.WHITE);
        mainPanel.add(guessButton, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        
        Random random = new Random();
        int target = random.nextInt(10) + 1;
        int attempts = 0;
        
        guessButton.addActionListener(e -> {
            try {
                int guess = Integer.parseInt(inputField.getText());
                attempts++;
                
                if (guess == target) {
                    JOptionPane.showMessageDialog(dialog, 
                        "🎉 Correct! You got it in " + attempts + " tries!", 
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else if (guess < target) {
                    messageLabel.setText("📈 Too low! Try again. (Attempt " + attempts + ")");
                    messageLabel.setForeground(Color.decode("#f59e0b"));
                    inputField.setText("");
                } else {
                    messageLabel.setText("📉 Too high! Try again. (Attempt " + attempts + ")");
                    messageLabel.setForeground(Color.decode("#ef4444"));
                    inputField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number!", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        inputField.addActionListener(e -> guessButton.doClick());
        
        dialog.setVisible(true);
    }
    
    private void snakesAndLadders() {
        new SnakesAndLaddersGame(this);
    }
    
    private void mazeSolver() {
        new MazeSolverGame(this);
    }
    
    /**
     * Snakes and Ladders Game Implementation
     */
    private static class SnakesAndLaddersGame {
        private JDialog dialog;
        private JLabel statusLabel;
        private JLabel positionLabel;
        private JButton rollButton;
        private int position = 0;
        private int rolls = 0;
        
        private final int[] snakes = {98, 95, 93, 87, 64, 62, 54, 17};
        private final int[] snakeEnds = {78, 75, 73, 24, 60, 19, 34, 7};
        private final int[] ladders = {9, 21, 28, 36, 51, 71, 80};
        private final int[] ladderEnds = {31, 42, 84, 44, 67, 91, 100};
        
        public SnakesAndLaddersGame(Component parent) {
            dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), 
                "Snakes and Ladders", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());
            dialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
            
            JPanel boardPanel = new JPanel(new GridLayout(10, 10));
            boardPanel.setBackground(Color.decode("#1e1e1e"));
            
            // Create board
            for (int i = 0; i < 100; i++) {
                int num = 100 - i;
                JLabel cell = new JLabel(String.valueOf(num));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(((i / 10) % 2 == 0) ? 
                    Color.decode("#3d3d3d") : Color.decode("#4d4d4d"));
                cell.setForeground(Color.WHITE);
                cell.setFont(new Font("Consolas", Font.PLAIN, 8));
                boardPanel.add(cell);
            }
            
            dialog.add(boardPanel, BorderLayout.CENTER);
            
            // Control panel
            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.setBackground(Color.decode("#1e1e1e"));
            
            positionLabel = new JLabel("Position: 0");
            positionLabel.setForeground(Color.decode("#00ffee"));
            positionLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
            controlPanel.add(positionLabel);
            
            rollButton = new JButton("🎲 Roll Dice");
            rollButton.setBackground(Color.decode("#4CAF50"));
            rollButton.setForeground(Color.WHITE);
            rollButton.setFocusPainted(false);
            rollButton.addActionListener(e -> rollDice());
            controlPanel.add(rollButton);
            
            JButton resetButton = new JButton("🔄 Reset");
            resetButton.setBackground(Color.decode("#f44336"));
            resetButton.setForeground(Color.WHITE);
            resetButton.setFocusPainted(false);
            resetButton.addActionListener(e -> resetGame());
            controlPanel.add(resetButton);
            
            statusLabel = new JLabel("Click Roll Dice to start!");
            statusLabel.setForeground(Color.decode("#00ffee"));
            statusLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
            controlPanel.add(statusLabel);
            
            dialog.add(controlPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
        }
        
        private void rollDice() {
            rolls++;
            int dice = new Random().nextInt(6) + 1;
            position += dice;
            
            // Check for snakes
            for (int i = 0; i < snakes.length; i++) {
                if (position == snakes[i]) {
                    position = snakeEnds[i];
                    statusLabel.setText("🐍 Snake! Moved to " + position);
                    break;
                }
            }
            
            // Check for ladders
            boolean ladderFound = false;
            for (int i = 0; i < ladders.length; i++) {
                if (position == ladders[i]) {
                    position = ladderEnds[i];
                    statusLabel.setText("🪜 Ladder! Climbed to " + position);
                    ladderFound = true;
                    break;
                }
            }
            
            if (!ladderFound && position < 100) {
                statusLabel.setText("🎲 Rolled " + dice + ". Position: " + position);
            }
            
            if (position >= 100) {
                position = 100;
                statusLabel.setText("🎉 Congratulations! You won in " + rolls + " rolls!");
                rollButton.setEnabled(false);
                JOptionPane.showMessageDialog(dialog, 
                    "🎉 You won Snakes and Ladders!\nRolls: " + rolls,
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
            
            positionLabel.setText("Position: " + position);
        }
        
        private void resetGame() {
            position = 0;
            rolls = 0;
            rollButton.setEnabled(true);
            statusLabel.setText("Game reset. Click Roll Dice to start!");
            positionLabel.setText("Position: 0");
        }
    }
    
    /**
     * Maze Solver Game Implementation
     */
    private static class MazeSolverGame {
        private JDialog dialog;
        private JLabel statusLabel;
        
        private final int[][] maze = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,1,0,0,0,0,1},
            {1,0,1,0,1,0,1,1,0,1},
            {1,0,1,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1},
            {1,0,0,0,0,0,1,0,0,1},
            {1,1,1,1,1,0,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,1,1,0,1},
            {1,1,1,1,1,1,1,1,1,1}
        };
        
        private final int[] start = {1, 1};
        private final int[] end = {8, 8};
        private JPanel mazePanel;
        private JButton[][] cells;
        private java.util.List<int[]> solution;
        
        public MazeSolverGame(Component parent) {
            dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), 
                "Maze Solver", true);
            dialog.setSize(550, 550);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());
            dialog.getContentPane().setBackground(Color.decode("#1e1e1e"));
            
            // Maze panel
            mazePanel = new JPanel(new GridLayout(10, 10));
            mazePanel.setBackground(Color.decode("#1e1e1e"));
            cells = new JButton[10][10];
            
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    JButton cell = new JButton();
                    cell.setBackground(maze[i][j] == 1 ? 
                        Color.decode("#3d3d3d") : Color.decode("#1e1e1e"));
                    cell.setBorder(BorderFactory.createLineBorder(Color.decode("#555555")));
                    cell.setEnabled(false);
                    cells[i][j] = cell;
                    mazePanel.add(cell);
                }
            }
            
            // Mark start and end
            cells[start[0]][start[1]].setBackground(Color.decode("#4CAF50"));
            cells[end[0]][end[1]].setBackground(Color.decode("#f44336"));
            
            dialog.add(mazePanel, BorderLayout.CENTER);
            
            // Control panel
            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.setBackground(Color.decode("#1e1e1e"));
            
            JButton solveButton = new JButton("🧩 Solve");
            solveButton.setBackground(Color.decode("#4CAF50"));
            solveButton.setForeground(Color.WHITE);
            solveButton.setFocusPainted(false);
            solveButton.addActionListener(e -> solveMaze());
            controlPanel.add(solveButton);
            
            JButton resetButton = new JButton("🔄 Reset");
            resetButton.setBackground(Color.decode("#f44336"));
            resetButton.setForeground(Color.WHITE);
            resetButton.setFocusPainted(false);
            resetButton.addActionListener(e -> resetMaze());
            controlPanel.add(resetButton);
            
            statusLabel = new JLabel("Click 'Solve' to find path!");
            statusLabel.setForeground(Color.decode("#00ffee"));
            statusLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
            controlPanel.add(statusLabel);
            
            dialog.add(controlPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
        }
        
        private void solveMaze() {
            solution = findPath();
            if (solution != null) {
                statusLabel.setText("✅ Path found! Length: " + solution.size() + " steps");
                
                // Highlight path (excluding start and end)
                for (int i = 1; i < solution.size() - 1; i++) {
                    int[] pos = solution.get(i);
                    cells[pos[0]][pos[1]].setBackground(Color.decode("#00ffee"));
                }
                
                JOptionPane.showMessageDialog(dialog, 
                    "✅ Path found!\nSteps: " + solution.size(),
                    "Maze Solver", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("❌ No path found!");
                JOptionPane.showMessageDialog(dialog, 
                    "❌ No path found!",
                    "Maze Solver", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private java.util.List<int[]> findPath() {
            boolean[][] visited = new boolean[10][10];
            java.util.List<int[]> path = new java.util.ArrayList<>();
            
            if (dfs(start[0], start[1], visited, path)) {
                return path;
            }
            return null;
        }
        
        private boolean dfs(int r, int c, boolean[][] visited, java.util.List<int[]> path) {
            if (r < 0 || r >= 10 || c < 0 || c >= 10) return false;
            if (maze[r][c] == 1 || visited[r][c]) return false;
            if (r == end[0] && c == end[1]) {
                path.add(new int[]{r, c});
                return true;
            }
            
            visited[r][c] = true;
            path.add(new int[]{r, c});
            
            int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            for (int[] dir : dirs) {
                if (dfs(r + dir[0], c + dir[1], visited, path)) {
                    return true;
                }
            }
            
            path.remove(path.size() - 1);
            return false;
        }
        
        private void resetMaze() {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    cells[i][j].setBackground(maze[i][j] == 1 ? 
                        Color.decode("#3d3d3d") : Color.decode("#1e1e1e"));
                }
            }
            cells[start[0]][start[1]].setBackground(Color.decode("#4CAF50"));
            cells[end[0]][end[1]].setBackground(Color.decode("#f44336"));
            solution = null;
            statusLabel.setText("Maze reset. Click 'Solve' to find path!");
        }
    }
}