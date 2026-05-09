
// =================== BranchController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.Branch;
import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.service.BranchService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BranchController extends JFrame {

    private User loggedInUser;
    private BranchService branchService = new BranchService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtId, txtName, txtAddress, txtContact;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public BranchController(User user) {
        this.loggedInUser = user;
        setTitle("Manage Branches");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Branch Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId      = addFormRow(formPanel, gbc, 0, "Branch ID:", 10);
        txtName    = addFormRow(formPanel, gbc, 1, "Name:", 20);
        txtAddress = addFormRow(formPanel, gbc, 2, "Address:", 30);
        txtContact = addFormRow(formPanel, gbc, 3, "Contact:", 15);

        try { txtId.setText(branchService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd    = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Table
        String[] cols = {"ID", "Name", "Address", "Contact"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, new JScrollPane(table));
        split.setDividerLocation(230);
        add(split);

        // Events
        btnAdd.addActionListener(e -> addBranch());
        btnUpdate.addActionListener(e -> updateBranch());
        btnDelete.addActionListener(e -> deleteBranch());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });
    }

    private JTextField addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, int cols) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField field = new JTextField(cols);
        panel.add(field, gbc);
        return field;
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            for (Branch b : branchService.getAllBranches()) {
                tableModel.addRow(new Object[]{b.getBranchId(), b.getBranchName(), b.getAddress(), b.getContact()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void addBranch() {
        try {
            Branch b = new Branch(txtId.getText().trim(), txtName.getText().trim(),
                    txtAddress.getText().trim(), txtContact.getText().trim());
            branchService.addBranch(b);
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Branch added successfully.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateBranch() {
        if (table.getSelectedRow() < 0) { showError("Select a branch to update."); return; }
        try {
            Branch b = new Branch(txtId.getText().trim(), txtName.getText().trim(),
                    txtAddress.getText().trim(), txtContact.getText().trim());
            branchService.updateBranch(b);
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Branch updated.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteBranch() {
        if (table.getSelectedRow() < 0) { showError("Select a branch to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this branch?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                branchService.deleteBranch(txtId.getText().trim());
                loadTable(); clearForm();
                JOptionPane.showMessageDialog(this, "Branch deleted.");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtName.setText((String) tableModel.getValueAt(row, 1));
        txtAddress.setText((String) tableModel.getValueAt(row, 2));
        txtContact.setText((String) tableModel.getValueAt(row, 3));
    }

    private void clearForm() {
        try { txtId.setText(branchService.generateNextId()); } catch (SQLException ignored) {}
        txtName.setText(""); txtAddress.setText(""); txtContact.setText("");
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

