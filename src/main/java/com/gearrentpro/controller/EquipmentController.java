//================== EquipmentController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.Branch;
import main.java.com.gearrentpro.entity.Category;
import main.java.com.gearrentpro.entity.Equipment;
import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.service.BranchService;
import main.java.com.gearrentpro.service.CategoryService;
import main.java.com.gearrentpro.service.EquipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class EquipmentController extends JFrame {

    private User loggedInUser;
    private EquipmentService equipmentService = new EquipmentService();
    private BranchService branchService = new BranchService();
    private CategoryService categoryService = new CategoryService();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtId, txtBrand, txtModel, txtYear, txtPrice, txtDeposit;
    private JComboBox<String> cmbCategory, cmbBranch, cmbStatus, cmbFilterBranch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnFilter;

    public EquipmentController(User user) {
        this.loggedInUser = user;
        setTitle("Manage Equipment");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Equipment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId      = addFormRow(formPanel, gbc, 0, "Equipment ID:");
        txtBrand   = addFormRow(formPanel, gbc, 1, "Brand:");
        txtModel   = addFormRow(formPanel, gbc, 2, "Model:");
        txtYear    = addFormRow(formPanel, gbc, 3, "Purchase Year:");
        txtPrice   = addFormRow(formPanel, gbc, 4, "Daily Base Price:");
        txtDeposit = addFormRow(formPanel, gbc, 5, "Security Deposit:");

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbCategory = new JComboBox<>();
        loadCategoryCombo();
        formPanel.add(cmbCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        formPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbBranch = new JComboBox<>();
        loadBranchCombo(cmbBranch);
        if (!"Admin".equals(loggedInUser.getRole())) {
            cmbBranch.setEnabled(false);
        }
        formPanel.add(cmbBranch, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Available", "Reserved", "Rented", "Under Maintenance"});
        formPanel.add(cmbStatus, gbc);

        try { txtId.setText(equipmentService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}

        // Set branch if not Admin
        if (!"Admin".equals(loggedInUser.getRole()) && loggedInUser.getBranchId() != null) {
            for (int i = 0; i < cmbBranch.getItemCount(); i++) {
                if (cmbBranch.getItemAt(i).startsWith(loggedInUser.getBranchId())) {
                    cmbBranch.setSelectedIndex(i); break;
                }
            }
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd    = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by Branch:"));
        cmbFilterBranch = new JComboBox<>();
        cmbFilterBranch.addItem("ALL - All Branches");
        loadBranchCombo(cmbFilterBranch);
        filterPanel.add(cmbFilterBranch);
        btnFilter = new JButton("Filter");
        filterPanel.add(btnFilter);

        // Table
        String[] cols = {"ID", "Brand", "Model", "Year", "Daily Price", "Deposit", "Category", "Branch", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        split.setDividerLocation(330);
        add(split);

        btnAdd.addActionListener(e -> addEquipment());
        btnUpdate.addActionListener(e -> updateEquipment());
        btnDelete.addActionListener(e -> deleteEquipment());
        btnClear.addActionListener(e -> clearForm());
        btnFilter.addActionListener(e -> loadTable());
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

    private void loadCategoryCombo() {
        try {
            for (Category c : categoryService.getActiveCategories()) {
                cmbCategory.addItem(c.getCategoryId() + " - " + c.getCategoryName());
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadBranchCombo(JComboBox<String> combo) {
        try {
            for (Branch b : branchService.getAllBranches()) {
                combo.addItem(b.getBranchId() + " - " + b.getBranchName());
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Equipment> list;
            String selected = (String) cmbFilterBranch.getSelectedItem();
            if (selected != null && !selected.startsWith("ALL")) {
                String branchId = selected.split(" - ")[0];
                list = equipmentService.getEquipmentByBranch(branchId);
            } else if (!"Admin".equals(loggedInUser.getRole()) && loggedInUser.getBranchId() != null) {
                list = equipmentService.getEquipmentByBranch(loggedInUser.getBranchId());
            } else {
                list = equipmentService.getAllEquipment();
            }
            for (Equipment e : list) {
                tableModel.addRow(new Object[]{e.getEquipmentId(), e.getBrand(), e.getModel(),
                        e.getPurchaseYear(), e.getDailyBasePrice(), e.getSecurityDeposit(),
                        e.getCategoryName(), e.getBranchName(), e.getStatus()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void addEquipment() {
        try {
            equipmentService.addEquipment(buildEquipment());
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Equipment added.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateEquipment() {
        if (table.getSelectedRow() < 0) { showError("Select equipment to update."); return; }
        try {
            equipmentService.updateEquipment(buildEquipment());
            loadTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Equipment updated.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteEquipment() {
        if (table.getSelectedRow() < 0) { showError("Select equipment to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this equipment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                equipmentService.deleteEquipment(txtId.getText().trim());
                loadTable(); clearForm();
                JOptionPane.showMessageDialog(this, "Equipment deleted.");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private Equipment buildEquipment() {
        Equipment e = new Equipment();
        e.setEquipmentId(txtId.getText().trim());
        e.setBrand(txtBrand.getText().trim());
        e.setModel(txtModel.getText().trim());
        e.setPurchaseYear(Integer.parseInt(txtYear.getText().trim()));
        e.setDailyBasePrice(new BigDecimal(txtPrice.getText().trim()));
        e.setSecurityDeposit(new BigDecimal(txtDeposit.getText().trim()));
        e.setCategoryId(((String) cmbCategory.getSelectedItem()).split(" - ")[0]);
        e.setBranchId(((String) cmbBranch.getSelectedItem()).split(" - ")[0]);
        e.setStatus((String) cmbStatus.getSelectedItem());
        return e;
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtBrand.setText((String) tableModel.getValueAt(row, 1));
        txtModel.setText((String) tableModel.getValueAt(row, 2));
        txtYear.setText(tableModel.getValueAt(row, 3).toString());
        txtPrice.setText(tableModel.getValueAt(row, 4).toString());
        txtDeposit.setText(tableModel.getValueAt(row, 5).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 8));
    }

    private void clearForm() {
        try { txtId.setText(equipmentService.generateNextId()); } catch (SQLException ignored) {}
        txtBrand.setText(""); txtModel.setText(""); txtYear.setText("");
        txtPrice.setText(""); txtDeposit.setText("");
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}