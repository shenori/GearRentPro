// =================== CategoryController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.Category;
import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.service.CategoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class CategoryController extends JFrame {

    private User loggedInUser;
    private CategoryService categoryService = new CategoryService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtId, txtName, txtDesc, txtFactor, txtWeekend, txtLateFee;
    private JCheckBox chkActive;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public CategoryController(User user) {
        this.loggedInUser = user;
        setTitle("Manage Categories");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId      = addFormRow(formPanel, gbc, 0, "Category ID:");
        txtName    = addFormRow(formPanel, gbc, 1, "Name:");
        txtDesc    = addFormRow(formPanel, gbc, 2, "Description:");
        txtFactor  = addFormRow(formPanel, gbc, 3, "Base Price Factor:");
        txtWeekend = addFormRow(formPanel, gbc, 4, "Weekend Multiplier:");
        txtLateFee = addFormRow(formPanel, gbc, 5, "Late Fee/Day (LKR):");

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Active:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        chkActive = new JCheckBox(); chkActive.setSelected(true);
        formPanel.add(chkActive, gbc);

        try { txtId.setText(categoryService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd    = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        String[] cols = {"ID", "Name", "Description", "Factor", "Weekend", "Late Fee/Day", "Active"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, new JScrollPane(table));
        split.setDividerLocation(270);
        add(split);

        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
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
            for (Category c : categoryService.getAllCategories()) {
                tableModel.addRow(new Object[]{c.getCategoryId(), c.getCategoryName(), c.getDescription(),
                        c.getBasePriceFactor(), c.getWeekendMultiplier(), c.getLateFeePerDay(), c.isActive()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void addCategory() {
        try {
            Category c = buildCategory();
            categoryService.addCategory(c);
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Category added.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateCategory() {
        if (table.getSelectedRow() < 0) { showError("Select a category to update."); return; }
        try {
            categoryService.updateCategory(buildCategory());
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Category updated.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteCategory() {
        if (table.getSelectedRow() < 0) { showError("Select a category to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryService.deleteCategory(txtId.getText().trim());
                loadTable(); clearForm();
                JOptionPane.showMessageDialog(this, "Category deleted.");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private Category buildCategory() {
        Category c = new Category();
        c.setCategoryId(txtId.getText().trim());
        c.setCategoryName(txtName.getText().trim());
        c.setDescription(txtDesc.getText().trim());
        c.setBasePriceFactor(new BigDecimal(txtFactor.getText().trim()));
        c.setWeekendMultiplier(new BigDecimal(txtWeekend.getText().trim()));
        c.setLateFeePerDay(new BigDecimal(txtLateFee.getText().trim()));
        c.setActive(chkActive.isSelected());
        return c;
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtName.setText((String) tableModel.getValueAt(row, 1));
        txtDesc.setText((String) tableModel.getValueAt(row, 2));
        txtFactor.setText(tableModel.getValueAt(row, 3).toString());
        txtWeekend.setText(tableModel.getValueAt(row, 4).toString());
        txtLateFee.setText(tableModel.getValueAt(row, 5).toString());
        chkActive.setSelected((Boolean) tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        try { txtId.setText(categoryService.generateNextId()); } catch (SQLException ignored) {}
        txtName.setText(""); txtDesc.setText(""); txtFactor.setText("");
        txtWeekend.setText(""); txtLateFee.setText(""); chkActive.setSelected(true);
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}