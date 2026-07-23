package com.neilos.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages SQLite database operations for NeilOS
 */
public class DatabaseManager {
    private static final String DB_FILE = "neilos.db";
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
        initialize();
    }
    
    public void initialize() throws SQLException {
        // Create notes table
        String notesTable = """
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT
            )
        """;
        
        // Create users table
        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                password TEXT
            )
        """;
        
        // Create transactions table
        String transactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT,
                amount REAL,
                date TEXT
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(notesTable);
            stmt.execute(usersTable);
            stmt.execute(transactionsTable);
        }
    }
    
    public void saveNote(String content) throws SQLException {
        String sql = "INSERT INTO notes(content) VALUES(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.executeUpdate();
        }
    }
    
    public List<String> loadNotes() throws SQLException {
        List<String> notes = new ArrayList<>();
        String sql = "SELECT content FROM notes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notes.add(rs.getString("content"));
            }
        }
        return notes;
    }
    
    public boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean loginUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public void addTransaction(String type, double amount, String date) throws SQLException {
        String sql = "INSERT INTO transactions(type, amount, date) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        }
    }
    
    public List<Transaction> getTransactions(int limit) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT type, amount, date FROM transactions ORDER BY date DESC LIMIT ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("date")
                    ));
                }
            }
        }
        return transactions;
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}