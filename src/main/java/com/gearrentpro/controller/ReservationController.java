// =================== ReservationController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.*;
import main.java.com.gearrentpro.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ReservationController extends JFrame {

    private User loggedInUser;
    private ReservationService reservationService = new ReservationService();
    private EquipmentService equipmentService = new EquipmentService();
    private CustomerService customerService = new CustomerService();
    private BranchService branchService = new BranchService();
    private RentalService rentalService = new RentalService();
    private PricingService pricingService = new PricingService();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtId, txtStartDate, txtEndDate;
    private JComboBox<String> cmbBranch, cmbEquipment, cmbCustomer;
    private JButton btnCreate, btnCancel, btnConvert, btnRefresh;

    public ReservationController(User user) {
        this.loggedInUser = user;
        setTitle("Manage Reservations");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Reservation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Reservation ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Reservation ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtId = new JTextField(10);
        try { txtId.setText(reservationService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}
        formPanel.add(txtId, gbc);

        // Branch
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbBranch = new JComboBox<>();
        loadBranchCombo();
        if (!"Admin".equals(loggedInUser.getRole()) && loggedInUser.getBranchId() != null) {
            cmbBranch.setEnabled(false);
        }
        formPanel.add(cmbBranch, gbc);

        // Equipment
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Equipment (Available):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbEquipment = new JComboBox<>();
        formPanel.add(cmbEquipment, gbc);
        cmbBranch.addActionListener(e -> loadEquipmentCombo());
        loadEquipmentCombo();

        // Customer
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbCustomer = new JComboBox<>();
        loadCustomerCombo();
        formPanel.add(cmbCustomer, gbc);

        // Dates
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtStartDate = new JTextField(LocalDate.now().toString());
        formPanel.add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEndDate = new JTextField(LocalDate.now().plusDays(3).toString());
        formPanel.add(txtEndDate, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnCreate  = new JButton("Create Reservation");
        btnCancel  = new JButton("Cancel Reservation");
        btnConvert = new JButton("Convert to Rental");
        btnRefresh = new JButton("Refresh");
        btnPanel.add(btnCreate); btnPanel.add(btnCancel); btnPanel.add(btnConvert); btnPanel.add(btnRefresh);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Table
        String[] cols = {"ID", "Equipment", "Customer", "Branch", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, new JScrollPane(table));
        split.setDividerLocation(260);
        add(split);

        btnCreate.addActionListener(e -> createReservation());
        btnCancel.addActionListener(e -> cancelReservation());
        btnConvert.addActionListener(e -> convertToRental());
        btnRefresh.addActionListener(e -> loadTable());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateFormFromTable();
        });
    }

    private void loadBranchCombo() {
        try {
            if (!"Admin".equals(loggedInUser.getRole()) && loggedInUser.getBranchId() != null) {
                Branch b = branchService.getBranchById(loggedInUser.getBranchId());
                if (b != null) cmbBranch.addItem(b.getBranchId() + " - " + b.getBranchName());
            } else {
                for (Branch b : branchService.getAllBranches())
                    cmbBranch.addItem(b.getBranchId() + " - " + b.getBranchName());
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadEquipmentCombo() {
        cmbEquipment.removeAllItems();
        if (cmbBranch.getSelectedItem() == null) return;
        String branchId = ((String) cmbBranch.getSelectedItem()).split(" - ")[0];
        try {
            for (Equipment eq : equipmentService.getAvailableEquipment(branchId))
                cmbEquipment.addItem(eq.getEquipmentId() + " - " + eq.getBrand() + " " + eq.getModel());
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadCustomerCombo() {
        try {
            for (Customer c : customerService.getAllCustomers())
                cmbCustomer.addItem(c.getCustomerId() + " - " + c.getFullName());
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Reservation> list;
            if ("Admin".equals(loggedInUser.getRole())) {
                list = reservationService.getAllReservations();
            } else {
                list = reservationService.getReservationsByBranch(loggedInUser.getBranchId());
            }
            for (Reservation r : list) {
                tableModel.addRow(new Object[]{r.getReservationId(), r.getEquipmentName(),
                        r.getCustomerName(), r.getBranchName(), r.getStartDate(), r.getEndDate(), r.getStatus()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void createReservation() {
        try {
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end   = LocalDate.parse(txtEndDate.getText().trim());
            if (end.isBefore(start)) { showError("End date cannot be before start date."); return; }

            Reservation res = new Reservation();
            res.setReservationId(txtId.getText().trim());
            res.setEquipmentId(((String) cmbEquipment.getSelectedItem()).split(" - ")[0]);
            res.setCustomerId(((String) cmbCustomer.getSelectedItem()).split(" - ")[0]);
            res.setBranchId(((String) cmbBranch.getSelectedItem()).split(" - ")[0]);
            res.setStartDate(start);
            res.setEndDate(end);

            reservationService.createReservation(res);
            loadTable(); resetForm();
            JOptionPane.showMessageDialog(this, "Reservation created successfully.");
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void cancelReservation() {
        if (table.getSelectedRow() < 0) { showError("Select a reservation to cancel."); return; }
        String resId = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel reservation " + resId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reservationService.cancelReservation(resId);
                loadTable(); resetForm();
                JOptionPane.showMessageDialog(this, "Reservation cancelled.");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private void convertToRental() {
        if (table.getSelectedRow() < 0) { showError("Select an Active reservation to convert."); return; }
        String status = (String) tableModel.getValueAt(table.getSelectedRow(), 6);
        if (!"Active".equals(status)) { showError("Only Active reservations can be converted."); return; }

        String resId = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
        try {
            Reservation res = reservationService.getReservationById(resId);
            Equipment eq = equipmentService.getEquipmentById(res.getEquipmentId());
            Customer cu = customerService.getCustomerById(res.getCustomerId());

            long days = res.getStartDate().until(res.getEndDate()).getDays() + 1;
            java.math.BigDecimal rentalAmt = pricingService.calculateRentalAmount(
                    eq.getCategoryId(), eq.getDailyBasePrice(), res.getStartDate(), res.getEndDate());
            java.math.BigDecimal memDiscount = pricingService.calculateMembershipDiscount(cu.getMembershipLevel(), rentalAmt);
            java.math.BigDecimal longDiscount = pricingService.calculateLongRentalDiscount(days, rentalAmt);
            java.math.BigDecimal finalAmt = rentalAmt.subtract(memDiscount).subtract(longDiscount).add(eq.getSecurityDeposit());

            String msg = String.format("Convert Reservation %s to Rental?\n\nRental Amount: LKR %.2f\nMembership Discount: LKR %.2f\nLong Rental Discount: LKR %.2f\nSecurity Deposit: LKR %.2f\nFinal Payable: LKR %.2f",
                    resId, rentalAmt, memDiscount, longDiscount, eq.getSecurityDeposit(), finalAmt);

            int confirm = JOptionPane.showConfirmDialog(this, msg, "Convert to Rental", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String rentalId = rentalService.generateNextId();
                Rental rental = new Rental();
                rental.setRentalId(rentalId);
                rental.setEquipmentId(res.getEquipmentId());
                rental.setCustomerId(res.getCustomerId());
                rental.setBranchId(res.getBranchId());
                rental.setReservationId(resId);
                rental.setStartDate(res.getStartDate());
                rental.setEndDate(res.getEndDate());
                rental.setRentalAmount(rentalAmt);
                rental.setSecurityDeposit(eq.getSecurityDeposit());
                rental.setMembershipDiscount(memDiscount);
                rental.setLongRentalDiscount(longDiscount);
                rental.setLateFee(java.math.BigDecimal.ZERO);
                rental.setDamageCharge(java.math.BigDecimal.ZERO);
                rental.setFinalAmount(finalAmt);
                rental.setPaymentStatus("Paid");
                rental.setRentalStatus("Active");

                reservationService.cancelReservation(resId);
                rentalService.createRental(rental);
                loadTable();
                JOptionPane.showMessageDialog(this, "Converted to Rental ID: " + rentalId);
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        txtStartDate.setText(tableModel.getValueAt(row, 4).toString());
        txtEndDate.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void resetForm() {
        try { txtId.setText(reservationService.generateNextId()); } catch (SQLException ignored) {}
        txtStartDate.setText(LocalDate.now().toString());
        txtEndDate.setText(LocalDate.now().plusDays(3).toString());
        loadEquipmentCombo();
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
