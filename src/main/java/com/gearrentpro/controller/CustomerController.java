// =================== CustomerController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.Customer;
import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.service.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class CustomerController extends JFrame {

    private User loggedInUser;
    private CustomerService customerService = new CustomerService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtId, txtName, txtNic, txtContact, txtEmail, txtAddress;
    private JComboBox<String> cmbMembership;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public CustomerController(User user) {
        this.loggedInUser = user;
        setTitle("Manage Customers");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId      = addFormRow(formPanel, gbc, 0, "Customer ID:");
        txtName    = addFormRow(formPanel, gbc, 1, "Full Name:");
        txtNic     = addFormRow(formPanel, gbc, 2, "NIC / Passport:");
        txtContact = addFormRow(formPanel, gbc, 3, "Contact No:");
        txtEmail   = addFormRow(formPanel, gbc, 4, "Email:");
        txtAddress = addFormRow(formPanel, gbc, 5, "Address:");

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Membership:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbMembership = new JComboBox<>(new String[]{"Regular", "Silver", "Gold"});
        formPanel.add(cmbMembership, gbc);

        try { txtId.setText(customerService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd    = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        String[] cols = {"ID", "Name", "NIC", "Contact", "Email", "Address", "Membership"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, new JScrollPane(table));
        split.setDividerLocation(290);
        add(split);

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });
    }

    private JTextField addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField field = new JTextField(20);
        panel.add(field, gbc);
        return field;
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            for (Customer c : customerService.getAllCustomers()) {
                tableModel.addRow(new Object[]{c.getCustomerId(), c.getFullName(), c.getNic(),
                        c.getContact(), c.getEmail(), c.getAddress(), c.getMembershipLevel()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void addCustomer() {
        try {
            customerService.addCustomer(buildCustomer());
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Customer added.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateCustomer() {
        if (table.getSelectedRow() < 0) { showError("Select a customer to update."); return; }
        try {
            customerService.updateCustomer(buildCustomer());
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Customer updated.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteCustomer() {
        if (table.getSelectedRow() < 0) { showError("Select a customer to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteCustomer(txtId.getText().trim());
                loadTable(); clearForm();
                JOptionPane.showMessageDialog(this, "Customer deleted.");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private Customer buildCustomer() {
        Customer c = new Customer();
        c.setCustomerId(txtId.getText().trim());
        c.setFullName(txtName.getText().trim());
        c.setNic(txtNic.getText().trim());
        c.setContact(txtContact.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        c.setMembershipLevel((String) cmbMembership.getSelectedItem());
        return c;
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtName.setText((String) tableModel.getValueAt(row, 1));
        txtNic.setText((String) tableModel.getValueAt(row, 2));
        txtContact.setText((String) tableModel.getValueAt(row, 3));
        txtEmail.setText((String) tableModel.getValueAt(row, 4));
        txtAddress.setText((String) tableModel.getValueAt(row, 5));
        cmbMembership.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        try { txtId.setText(customerService.generateNextId()); } catch (SQLException ignored) {}
        txtName.setText(""); txtNic.setText(""); txtContact.setText("");
        txtEmail.setText(""); txtAddress.setText(""); cmbMembership.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}


