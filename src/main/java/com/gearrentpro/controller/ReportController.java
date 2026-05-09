// =================== ReportController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.*;
import main.java.com.gearrentpro.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ReportController extends JFrame {

    private User loggedInUser;
    private RentalService rentalService = new RentalService();
    private EquipmentService equipmentService = new EquipmentService();
    private BranchService branchService = new BranchService();

    private JTabbedPane tabbedPane;

    // Revenue Report
    private JTextField txtRevFromDate, txtRevToDate;
    private JComboBox<String> cmbRevBranch;
    private DefaultTableModel revenueTableModel;

    // Utilization Report
    private JTextField txtUtilFromDate, txtUtilToDate;
    private JComboBox<String> cmbUtilBranch;
    private DefaultTableModel utilizationTableModel;

    public ReportController(User user) {
        this.loggedInUser = user;
        setTitle("Reports");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Branch-wise Revenue", buildRevenuePanel());
        tabbedPane.add("Equipment Utilization", buildUtilizationPanel());
        add(tabbedPane);
    }

    private JPanel buildRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("From (yyyy-MM-dd):"));
        txtRevFromDate = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 12);
        filterPanel.add(txtRevFromDate);
        filterPanel.add(new JLabel("To:"));
        txtRevToDate = new JTextField(LocalDate.now().toString(), 12);
        filterPanel.add(txtRevToDate);
        filterPanel.add(new JLabel("Branch:"));
        cmbRevBranch = new JComboBox<>();
        cmbRevBranch.addItem("ALL - All Branches");
        loadBranchCombo(cmbRevBranch);
        if (!"Admin".equals(loggedInUser.getRole())) {
            cmbRevBranch.setEnabled(false);
        }
        filterPanel.add(cmbRevBranch);
        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(e -> generateRevenueReport());
        filterPanel.add(btnGenerate);

        String[] cols = {"Branch", "Total Rentals", "Rental Income (LKR)", "Late Fees (LKR)", "Damage Charges (LKR)", "Total Revenue (LKR)"};
        revenueTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable revenueTable = new JTable(revenueTableModel);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(revenueTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildUtilizationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("From (yyyy-MM-dd):"));
        txtUtilFromDate = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 12);
        filterPanel.add(txtUtilFromDate);
        filterPanel.add(new JLabel("To:"));
        txtUtilToDate = new JTextField(LocalDate.now().toString(), 12);
        filterPanel.add(txtUtilToDate);
        filterPanel.add(new JLabel("Branch:"));
        cmbUtilBranch = new JComboBox<>();
        loadBranchCombo(cmbUtilBranch);
        if (!"Admin".equals(loggedInUser.getRole()) && loggedInUser.getBranchId() != null) {
            cmbUtilBranch.setEnabled(false);
            try {
                Branch b = branchService.getBranchById(loggedInUser.getBranchId());
                if (b != null) cmbUtilBranch.addItem(b.getBranchId() + " - " + b.getBranchName());
            } catch (SQLException ignored) {}
        }
        filterPanel.add(cmbUtilBranch);
        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(e -> generateUtilizationReport());
        filterPanel.add(btnGenerate);

        String[] cols = {"Equipment ID", "Brand/Model", "Category", "Total Days in Period", "Days Rented", "Utilization %"};
        utilizationTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable utilizationTable = new JTable(utilizationTableModel);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(utilizationTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadBranchCombo(JComboBox<String> combo) {
        try {
            for (Branch b : branchService.getAllBranches())
                combo.addItem(b.getBranchId() + " - " + b.getBranchName());
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void generateRevenueReport() {
        try {
            LocalDate from = LocalDate.parse(txtRevFromDate.getText().trim());
            LocalDate to   = LocalDate.parse(txtRevToDate.getText().trim());

            List<Rental> allRentals = rentalService.getAllRentals();
            String selectedBranch = (String) cmbRevBranch.getSelectedItem();

            // Group by branch
            Map<String, List<Rental>> branchMap = new LinkedHashMap<>();
            for (Rental r : allRentals) {
                LocalDate start = r.getStartDate();
                if (start.isBefore(from) || start.isAfter(to)) continue;
                if (selectedBranch != null && !selectedBranch.startsWith("ALL")) {
                    String brId = selectedBranch.split(" - ")[0];
                    if (!brId.equals(r.getBranchId())) continue;
                }
                branchMap.computeIfAbsent(r.getBranchName() != null ? r.getBranchName() : r.getBranchId(),
                        k -> new ArrayList<>()).add(r);
            }

            revenueTableModel.setRowCount(0);
            for (Map.Entry<String, List<Rental>> entry : branchMap.entrySet()) {
                String branch = entry.getKey();
                List<Rental> rentals = entry.getValue();
                BigDecimal rentalIncome = rentals.stream().map(Rental::getRentalAmount)
                        .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lateFees = rentals.stream().map(Rental::getLateFee)
                        .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal damageCharges = rentals.stream().map(Rental::getDamageCharge)
                        .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal total = rentalIncome.add(lateFees).add(damageCharges);
                revenueTableModel.addRow(new Object[]{branch, rentals.size(),
                        String.format("%.2f", rentalIncome),
                        String.format("%.2f", lateFees),
                        String.format("%.2f", damageCharges),
                        String.format("%.2f", total)});
            }

            if (revenueTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No rentals found for the selected period/branch.");
            }
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use yyyy-MM-dd.");
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void generateUtilizationReport() {
        try {
            LocalDate from = LocalDate.parse(txtUtilFromDate.getText().trim());
            LocalDate to   = LocalDate.parse(txtUtilToDate.getText().trim());
            long totalDays = from.until(to).getDays() + 1;

            if (cmbUtilBranch.getSelectedItem() == null) { showError("Select a branch."); return; }
            String branchId = ((String) cmbUtilBranch.getSelectedItem()).split(" - ")[0];

            List<Equipment> equipment = equipmentService.getEquipmentByBranch(branchId);
            List<Rental> rentals = rentalService.getRentalsByBranch(branchId);

            utilizationTableModel.setRowCount(0);
            for (Equipment eq : equipment) {
                long daysRented = 0;
                for (Rental r : rentals) {
                    if (!eq.getEquipmentId().equals(r.getEquipmentId())) continue;
                    if ("Cancelled".equals(r.getRentalStatus())) continue;
                    LocalDate rStart = r.getStartDate().isBefore(from) ? from : r.getStartDate();
                    LocalDate rEnd   = r.getEndDate().isAfter(to) ? to : r.getEndDate();
                    if (!rEnd.isBefore(rStart)) {
                        daysRented += rStart.until(rEnd).getDays() + 1;
                    }
                }
                double utilPct = totalDays > 0 ? (daysRented * 100.0 / totalDays) : 0;
                utilizationTableModel.addRow(new Object[]{
                        eq.getEquipmentId(),
                        eq.getBrand() + " " + eq.getModel(),
                        eq.getCategoryName(),
                        totalDays,
                        daysRented,
                        String.format("%.1f%%", Math.min(utilPct, 100.0))
                });
            }
            if (utilizationTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No equipment found for selected branch.");
            }
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use yyyy-MM-dd.");
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
