package com.neilos.apps;

import com.neilos.NeilOS;
import com.neilos.database.Transaction;
import com.neilos.ui.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced Bank Application with financial calculations
 */
public class BankApp extends JPanel {
    private NeilOS neilos;
    private WindowManager windowManager;
    private JTextArea outputArea;
    private JLabel balanceLabel;
    private JLabel loanLabel;
    
    public BankApp(NeilOS neilos) {
        this.neilos = neilos;
        this.windowManager = new WindowManager(neilos);
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        
        // Create UI
        createUI();
    }
    
    private void createUI() {
        // Top panel with balance info
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        balanceLabel = new JLabel("💰 Balance: $" + String.format("%,.2f", NeilOS.bankBalance));
        balanceLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        balanceLabel.setForeground(Color.decode("#10b981"));
        topPanel.add(balanceLabel);
        
        loanLabel = new JLabel(NeilOS.loanBalance > 0 ? 
            "🏦 Outstanding Loan: $" + String.format("%,.2f", NeilOS.loanBalance) :
            "✅ No Outstanding Loans");
        loanLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        loanLabel.setForeground(NeilOS.loanBalance > 0 ? Color.decode("#ef4444") : Color.decode("#10b981"));
        topPanel.add(loanLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.decode("#101826"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Basic Banking
        String[][] row1 = {
            {"Deposit", "💰", this::depositMoney},
            {"Withdraw", "🏧", this::withdrawMoney},
            {"Transfer", "💸", this::transferMoney},
            {"History", "📋", this::viewTransactions}
        };
        
        for (int i = 0; i < row1.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            JButton btn = createButton(row1[i][0], row1[i][1], (Runnable) row1[i][2]);
            centerPanel.add(btn, gbc);
        }
        
        // Row 2: Interest Calculations
        String[][] row2 = {
            {"Simple Interest", "📊", this::simpleInterest},
            {"Compound Interest", "📈", this::compoundInterest},
            {"EMI Calculator", "📉", this::emiCalculation}
        };
        
        for (int i = 0; i < row2.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 1;
            JButton btn = createButton(row2[i][0], row2[i][1], (Runnable) row2[i][2]);
            centerPanel.add(btn, gbc);
        }
        
        // Row 3: Tax Calculations
        String[][] row3 = {
            {"GST Calculator", "🧾", this::gstCalculation},
            {"SGST/CGST", "📑", this::sgstCgstCalculation},
            {"Tax Calculator", "💰", this::taxCalculation}
        };
        
        for (int i = 0; i < row3.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 2;
            JButton btn = createButton(row3[i][0], row3[i][1], (Runnable) row3[i][2]);
            centerPanel.add(btn, gbc);
        }
        
        // Row 4: Loan Management
        String[][] row4 = {
            {"Apply Loan", "🏦", this::applyLoan},
            {"Repay Loan", "💳", this::repayLoan}
        };
        
        for (int i = 0; i < row4.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 3;
            JButton btn = createButton(row4[i][0], row4[i][1], (Runnable) row4[i][2]);
            centerPanel.add(btn, gbc);
        }
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String text, String icon, Runnable action) {
        JButton btn = new JButton(icon + " " + text);
        btn.setFont(new Font("Consolas", Font.PLAIN, 12));
        btn.setBackground(Color.decode("#111827"));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.decode("#10b981"), 1));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    // Banking Operations
    private void depositMoney() {
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    NeilOS.bankBalance += amount;
                    NeilOS.bankLedger.add("+ " + amount);
                    try {
                        neilos.getDbManager().addTransaction("Deposit", amount, 
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateBalance();
                    JOptionPane.showMessageDialog(this, 
                        "Deposited $" + String.format("%.2f", amount) + 
                        "\nNew Balance: $" + String.format("%.2f", NeilOS.bankBalance),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void withdrawMoney() {
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0 && amount <= NeilOS.bankBalance) {
                    NeilOS.bankBalance -= amount;
                    NeilOS.bankLedger.add("- " + amount);
                    try {
                        neilos.getDbManager().addTransaction("Withdrawal", amount,
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateBalance();
                    JOptionPane.showMessageDialog(this,
                        "Withdrew $" + String.format("%.2f", amount) +
                        "\nNew Balance: $" + String.format("%.2f", NeilOS.bankBalance),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (amount > NeilOS.bankBalance) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void transferMoney() {
        String recipient = JOptionPane.showInputDialog(this, "Enter recipient:", "Transfer", JOptionPane.QUESTION_MESSAGE);
        if (recipient != null && !recipient.isEmpty()) {
            String input = JOptionPane.showInputDialog(this, "Enter transfer amount:", "Transfer", JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    double amount = Double.parseDouble(input);
                    if (amount > 0 && amount <= NeilOS.bankBalance) {
                        NeilOS.bankBalance -= amount;
                        NeilOS.bankLedger.add("Transfer " + amount + " -> " + recipient);
                        try {
                            neilos.getDbManager().addTransaction("Transfer to " + recipient, amount,
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateBalance();
                        JOptionPane.showMessageDialog(this,
                            "Transferred $" + String.format("%.2f", amount) + " to " + recipient +
                            "\nNew Balance: $" + String.format("%.2f", NeilOS.bankBalance),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else if (amount > NeilOS.bankBalance) {
                        JOptionPane.showMessageDialog(this, "Insufficient funds!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // Interest Calculations
    private void simpleInterest() {
        String principalStr = JOptionPane.showInputDialog(this, "Principal Amount:", "Simple Interest", JOptionPane.QUESTION_MESSAGE);
        if (principalStr == null) return;
        try {
            double principal = Double.parseDouble(principalStr);
            String rateStr = JOptionPane.showInputDialog(this, "Rate of Interest (% per year):", "Simple Interest", JOptionPane.QUESTION_MESSAGE);
            if (rateStr == null) return;
            double rate = Double.parseDouble(rateStr);
            String timeStr = JOptionPane.showInputDialog(this, "Time (in years):", "Simple Interest", JOptionPane.QUESTION_MESSAGE);
            if (timeStr == null) return;
            double time = Double.parseDouble(timeStr);
            
            double interest = (principal * rate * time) / 100;
            double total = principal + interest;
            
            String result = String.format("""
                Simple Interest Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Principal: $%.2f
                Rate: %.1f%% per year
                Time: %.1f years
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Interest: $%.2f
                Total Amount: $%.2f
                """, principal, rate, time, interest, total);
            
            JOptionPane.showMessageDialog(this, result, "Simple Interest", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void compoundInterest() {
        // Similar to simple interest with compounding frequency
        String principalStr = JOptionPane.showInputDialog(this, "Principal Amount:", "Compound Interest", JOptionPane.QUESTION_MESSAGE);
        if (principalStr == null) return;
        try {
            double principal = Double.parseDouble(principalStr);
            String rateStr = JOptionPane.showInputDialog(this, "Rate of Interest (% per year):", "Compound Interest", JOptionPane.QUESTION_MESSAGE);
            if (rateStr == null) return;
            double rate = Double.parseDouble(rateStr);
            String timeStr = JOptionPane.showInputDialog(this, "Time (in years):", "Compound Interest", JOptionPane.QUESTION_MESSAGE);
            if (timeStr == null) return;
            double time = Double.parseDouble(timeStr);
            String nStr = JOptionPane.showInputDialog(this, "Compounding frequency per year (1=Yearly, 4=Quarterly, 12=Monthly):", "Compound Interest", JOptionPane.QUESTION_MESSAGE);
            if (nStr == null) return;
            int n = Integer.parseInt(nStr);
            
            double amount = principal * Math.pow(1 + (rate/100)/n, n * time);
            double interest = amount - principal;
            
            String result = String.format("""
                Compound Interest Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Principal: $%.2f
                Rate: %.1f%% per year
                Time: %.1f years
                Compounding: %d times/year
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Interest Earned: $%.2f
                Total Amount: $%.2f
                """, principal, rate, time, n, interest, amount);
            
            JOptionPane.showMessageDialog(this, result, "Compound Interest", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void emiCalculation() {
        String principalStr = JOptionPane.showInputDialog(this, "Loan Amount:", "EMI Calculator", JOptionPane.QUESTION_MESSAGE);
        if (principalStr == null) return;
        try {
            double principal = Double.parseDouble(principalStr);
            String rateStr = JOptionPane.showInputDialog(this, "Annual Interest Rate (%):", "EMI Calculator", JOptionPane.QUESTION_MESSAGE);
            if (rateStr == null) return;
            double rate = Double.parseDouble(rateStr);
            String monthsStr = JOptionPane.showInputDialog(this, "Loan Tenure (months):", "EMI Calculator", JOptionPane.QUESTION_MESSAGE);
            if (monthsStr == null) return;
            int months = Integer.parseInt(monthsStr);
            
            double monthlyRate = rate / (12 * 100);
            double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1);
            double totalPayment = emi * months;
            double totalInterest = totalPayment - principal;
            
            String result = String.format("""
                EMI Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Loan Amount: $%.2f
                Annual Rate: %.1f%%
                Tenure: %d months
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Monthly EMI: $%.2f
                Total Payment: $%.2f
                Total Interest: $%.2f
                """, principal, rate, months, emi, totalPayment, totalInterest);
            
            JOptionPane.showMessageDialog(this, result, "EMI Calculator", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void gstCalculation() {
        String amountStr = JOptionPane.showInputDialog(this, "Original Amount:", "GST Calculator", JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            String rateStr = JOptionPane.showInputDialog(this, "GST Rate (%):", "GST Calculator", JOptionPane.QUESTION_MESSAGE);
            if (rateStr == null) return;
            double rate = Double.parseDouble(rateStr);
            
            double gstAmount = amount * rate / 100;
            double total = amount + gstAmount;
            
            String result = String.format("""
                GST Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Original Amount: $%.2f
                GST Rate: %.1f%%
                GST Amount: $%.2f
                Total Amount: $%.2f
                """, amount, rate, gstAmount, total);
            
            JOptionPane.showMessageDialog(this, result, "GST Calculator", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sgstCgstCalculation() {
        String amountStr = JOptionPane.showInputDialog(this, "Original Amount:", "SGST/CGST Calculator", JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            String sgstStr = JOptionPane.showInputDialog(this, "SGST Rate (%):", "SGST/CGST", JOptionPane.QUESTION_MESSAGE);
            if (sgstStr == null) return;
            double sgstRate = Double.parseDouble(sgstStr);
            String cgstStr = JOptionPane.showInputDialog(this, "CGST Rate (%):", "SGST/CGST", JOptionPane.QUESTION_MESSAGE);
            if (cgstStr == null) return;
            double cgstRate = Double.parseDouble(cgstStr);
            
            double sgstAmount = amount * sgstRate / 100;
            double cgstAmount = amount * cgstRate / 100;
            double totalTax = sgstAmount + cgstAmount;
            double total = amount + totalTax;
            
            String result = String.format("""
                SGST/CGST Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Original Amount: $%.2f
                SGST Rate: %.1f%%
                CGST Rate: %.1f%%
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                SGST Amount: $%.2f
                CGST Amount: $%.2f
                Total Tax: $%.2f
                Total Amount: $%.2f
                """, amount, sgstRate, cgstRate, sgstAmount, cgstAmount, totalTax, total);
            
            JOptionPane.showMessageDialog(this, result, "SGST/CGST Calculator", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void taxCalculation() {
        String incomeStr = JOptionPane.showInputDialog(this, "Annual Income:", "Tax Calculator", JOptionPane.QUESTION_MESSAGE);
        if (incomeStr == null) return;
        try {
            double income = Double.parseDouble(incomeStr);
            double tax = 0;
            String slab = "";
            
            if (income <= 250000) {
                tax = 0;
                slab = "No Tax";
            } else if (income <= 500000) {
                tax = (income - 250000) * 0.05;
                slab = "5%";
            } else if (income <= 1000000) {
                tax = 12500 + (income - 500000) * 0.20;
                slab = "20%";
            } else {
                tax = 112500 + (income - 1000000) * 0.30;
                slab = "30%";
            }
            
            double cess = tax * 0.04;
            double totalTax = tax + cess;
            double afterTax = income - totalTax;
            
            String result = String.format("""
                Income Tax Calculation:
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Annual Income: $%,.2f
                Tax Slab: %s
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Base Tax: $%,.2f
                Health & Education Cess (4%%): $%,.2f
                Total Tax Payable: $%,.2f
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Income After Tax: $%,.2f
                """, income, slab, tax, cess, totalTax, afterTax);
            
            JOptionPane.showMessageDialog(this, result, "Tax Calculator", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyLoan() {
        String amountStr = JOptionPane.showInputDialog(this, "Loan Amount Requested:", "Loan Application", JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null) return;
        try {
            double loanAmount = Double.parseDouble(amountStr);
            String rateStr = JOptionPane.showInputDialog(this, "Interest Rate (% per year):", "Loan Application", JOptionPane.QUESTION_MESSAGE);
            if (rateStr == null) return;
            double interestRate = Double.parseDouble(rateStr);
            String tenureStr = JOptionPane.showInputDialog(this, "Loan Tenure (years):", "Loan Application", JOptionPane.QUESTION_MESSAGE);
            if (tenureStr == null) return;
            double tenureYears = Double.parseDouble(tenureStr);
            
            NeilOS.loanBalance = loanAmount;
            NeilOS.bankBalance += loanAmount;
            
            double totalInterest = loanAmount * interestRate * tenureYears / 100;
            double totalPayment = loanAmount + totalInterest;
            
            String result = String.format("""
                Loan Approved!
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Loan Amount: $%.2f
                Interest Rate: %.1f%%
                Tenure: %.1f years
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Total Interest: $%.2f
                Total Repayment: $%.2f
                Monthly EMI: $%.2f
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Amount credited to your account!
                New Balance: $%.2f
                """, loanAmount, interestRate, tenureYears, totalInterest, totalPayment, 
                totalPayment/(tenureYears*12), NeilOS.bankBalance);
            
            try {
                neilos.getDbManager().addTransaction("Loan Taken", loanAmount,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            updateBalance();
            JOptionPane.showMessageDialog(this, result, "Loan Approved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void repayLoan() {
        if (NeilOS.loanBalance <= 0) {
            JOptionPane.showMessageDialog(this, "No outstanding loan!", "Loan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String amountStr = JOptionPane.showInputDialog(this, 
            "Outstanding Loan: $" + String.format("%.2f", NeilOS.loanBalance) + 
            "\nRepayment Amount:", "Repay Loan", JOptionPane.QUESTION_MESSAGE);
        
        if (amountStr == null) return;
        try {
            double repayment = Double.parseDouble(amountStr);
            if (repayment > NeilOS.bankBalance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (repayment > NeilOS.loanBalance) {
                repayment = NeilOS.loanBalance;
            }
            
            NeilOS.bankBalance -= repayment;
            NeilOS.loanBalance -= repayment;
            
            try {
                neilos.getDbManager().addTransaction("Loan Repayment", repayment,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            updateBalance();
            JOptionPane.showMessageDialog(this,
                "Repaid: $" + String.format("%.2f", repayment) +
                "\nRemaining Loan: $" + String.format("%.2f", NeilOS.loanBalance) +
                "\nNew Balance: $" + String.format("%.2f", NeilOS.bankBalance),
                "Repayment", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewTransactions() {
        try {
            List<Transaction> transactions = neilos.getDbManager().getTransactions(20);
            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transactions found.", "Transactions", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Recent Transactions:\n");
            sb.append("━".repeat(40)).append("\n\n");
            
            for (Transaction t : transactions) {
                sb.append("Type: ").append(t.getType()).append("\n");
                sb.append("Amount: $").append(String.format("%.2f", t.getAmount())).append("\n");
                sb.append("Date: ").append(t.getDate()).append("\n");
                sb.append("━".repeat(40)).append("\n\n");
            }
            
            JOptionPane.showMessageDialog(this, sb.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBalance() {
        balanceLabel.setText("💰 Balance: $" + String.format("%,.2f", NeilOS.bankBalance));
        loanLabel.setText(NeilOS.loanBalance > 0 ? 
            "🏦 Outstanding Loan: $" + String.format("%,.2f", NeilOS.loanBalance) :
            "✅ No Outstanding Loans");
        loanLabel.setForeground(NeilOS.loanBalance > 0 ? Color.decode("#ef4444") : Color.decode("#10b981"));
    }
}