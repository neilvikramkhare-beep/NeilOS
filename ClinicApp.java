package com.neilos.apps;

import com.neilos.NeilOS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clinic management system
 */
public class ClinicApp extends JPanel {
    private NeilOS neilos;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    
    public ClinicApp(NeilOS neilos) {
        this.neilos = neilos;
        setBackground(Color.decode("#101826"));
        setLayout(new BorderLayout());
        createUI();
        refreshTable();
    }
    
    private void createUI() {
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.decode("#0f172a"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton addBtn = new JButton("➕ Add Patient");
        addBtn.setBackground(Color.decode("#10b981"));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addPatient());
        topPanel.add(addBtn);
        
        JButton viewBtn = new JButton("📋 View Patients");
        viewBtn.setBackground(Color.decode("#3b82f6"));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        viewBtn.addActionListener(e -> viewPatients());
        topPanel.add(viewBtn);
        
        JButton dischargeBtn = new JButton("🏥 Discharge");
        dischargeBtn.setBackground(Color.decode("#ef4444"));
        dischargeBtn.setForeground(Color.WHITE);
        dischargeBtn.setFocusPainted(false);
        dischargeBtn.addActionListener(e -> dischargePatient());
        topPanel.add(dischargeBtn);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(Color.decode("#f59e0b"));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshTable());
        topPanel.add(refreshBtn);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Split pane with table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(Color.decode("#101826"));
        
        // Table
        String[] columns = {"Name", "Age", "Disease", "Date Added"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setBackground(Color.decode("#1e1e1e"));
        patientTable.setForeground(Color.WHITE);
        patientTable.setFont(new Font("Consolas", Font.PLAIN, 11));
        patientTable.getTableHeader().setBackground(Color.decode("#0f172a"));
        patientTable.getTableHeader().setForeground(Color.WHITE);
        patientTable.setSelectionBackground(Color.decode("#2d2d2d"));
        patientTable.setRowHeight(25);
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPatientDetails();
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(patientTable);
        splitPane.setTopComponent(tableScroll);
        
        // Details area
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.BLACK);
        detailsArea.setForeground(Color.decode("#00ff00"));
        detailsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        detailsArea.setText("Select a patient to view details.\n");
        
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBackground(Color.BLACK);
        splitPane.setBottomComponent(detailsScroll);
        
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void addPatient() {
        String name = JOptionPane.showInputDialog(this, "Patient Name:", "Add Patient", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.isEmpty()) return;
        
        String ageStr = JOptionPane.showInputDialog(this, "Age:", "Add Patient", JOptionPane.QUESTION_MESSAGE);
        if (ageStr == null || ageStr.isEmpty()) return;
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String disease = JOptionPane.showInputDialog(this, "Diagnosis:", "Add Patient", JOptionPane.QUESTION_MESSAGE);
        if (disease == null || disease.isEmpty()) return;
        
        Map<String, Object> patient = new java.util.HashMap<>();
        patient.put("name", name);
        patient.put("age", age);
        patient.put("disease", disease);
        patient.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        NeilOS.patients.add(patient);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewPatients() {
        if (NeilOS.patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients registered.", "Patients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("🏥 PATIENT RECORDS\n");
        sb.append("━".repeat(40)).append("\n\n");
        
        for (Map<String, Object> patient : NeilOS.patients) {
            sb.append("Name: ").append(patient.get("name")).append("\n");
            sb.append("Age: ").append(patient.get("age")).append("\n");
            sb.append("Disease: ").append(patient.get("disease")).append("\n");
            sb.append("Date: ").append(patient.get("date")).append("\n");
            sb.append("━".repeat(30)).append("\n\n");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Patient Records", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void dischargePatient() {
        String name = JOptionPane.showInputDialog(this, "Enter patient name to discharge:", 
            "Discharge Patient", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.isEmpty()) return;
        
        for (int i = 0; i < NeilOS.patients.size(); i++) {
            Map<String, Object> patient = NeilOS.patients.get(i);
            if (patient.get("name").toString().equalsIgnoreCase(name)) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Discharge " + name + "?", "Confirm Discharge", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    NeilOS.patients.remove(i);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Patient discharged: " + name, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this, "Patient not found!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            detailsArea.setText("Select a patient to view details.");
            return;
        }
        
        if (selectedRow >= NeilOS.patients.size()) return;
        Map<String, Object> patient = NeilOS.patients.get(selectedRow);
        
        StringBuilder details = new StringBuilder();
        details.append("📋 PATIENT DETAILS\n");
        details.append("━".repeat(40)).append("\n\n");
        details.append("Name: ").append(patient.get("name")).append("\n");
        details.append("Age: ").append(patient.get("age")).append("\n");
        details.append("Disease: ").append(patient.get("disease")).append("\n");
        details.append("Date Added: ").append(patient.get("date")).append("\n\n");
        details.append("━".repeat(30)).append("\n");
        details.append("💡 Click 'Discharge' to remove this patient.");
        
        detailsArea.setText(details.toString());
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Map<String, Object> patient : NeilOS.patients) {
            tableModel.addRow(new Object[]{
                patient.get("name"),
                patient.get("age"),
                patient.get("disease"),
                patient.get("date")
            });
        }
        
        if (patientTable.getSelectedRow() >= 0) {
            showPatientDetails();
        } else {
            detailsArea.setText("Select a patient to view details.");
        }
    }
}