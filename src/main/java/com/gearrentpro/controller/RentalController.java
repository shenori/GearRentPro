// =================== RentalController.java ===================
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
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class RentalController extends JFrame {

    private User loggedInUser;
    private RentalService rentalService = new RentalService();
    private EquipmentService equipmentService = new EquipmentService();
    private CustomerService customerService = new CustomerService();
    private BranchService branchService = new BranchService();
    private PricingService pricingService = new PricingService();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtId, txtStartDate, txtEndDate;
    private JComboBox<String> cmbBranch, cmbEquipment, cmbCustomer, cmbPayment, cmbStatusFilter;
    private JLabel lblRentalAmt, lblMemDisc, lblLongDisc, lblDeposit, lblFinal;
    private JButton btnCreate, btnReturn, btnRefresh, btnCalculate;
    private boolean overdueOnly;

    public RentalController(User user) {
        this(user, false);
    }

    public RentalController(User user, boolean overdueOnly) {
        this.loggedInUser = user;
        this.overdueOnly = overdueOnly;
        setTitle(overdueOnly ? "Overdue Rentals" : "Manage Rentals");
        setSize(1100, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Rental"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Rental ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Rental ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtId = new JTextField(10);
        try { txtId.setText(rentalService.generateNextId()); txtId.setEditable(false); }
        catch (SQLException ignored) {}
        formPanel.add(txtId, gbc);

        // Row 1: Branch
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbBranch = new JComboBox<>();
        loadBranchCombo();
        if (!"Admin".equals(loggedInUser.getRole())) cmbBranch.setEnabled(false);
        formPanel.add(cmbBranch, gbc);

        // Row 2: Equipment
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Equipment (Available):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbEquipment = new JComboBox<>();
        formPanel.add(cmbEquipment, gbc);
        cmbBranch.addActionListener(e -> loadEquipmentCombo());
        loadEquipmentCombo();

        // Row 3: Customer
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbCustomer = new JComboBox<>();
        loadCustomerCombo();
        formPanel.add(cmbCustomer, gbc);

        // Row 4: Start Date
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtStartDate = new JTextField(LocalDate.now().toString());
        formPanel.add(txtStartDate, gbc);

        // Row 5: End Date
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEndDate = new JTextField(LocalDate.now().plusDays(3).toString());
        formPanel.add(txtEndDate, gbc);

        // Row 6: Payment Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbPayment = new JComboBox<>(new String[]{"Paid", "Partially Paid", "Unpaid"});
        formPanel.add(cmbPayment, gbc);

        // Row 7: Pricing summary labels
        JPanel pricingPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        pricingPanel.setBorder(BorderFactory.createTitledBorder("Pricing Summary"));
        lblRentalAmt = new JLabel("Rental: -"); lblMemDisc = new JLabel("Mem Disc: -");
        lblLongDisc = new JLabel("Long Disc: -"); lblDeposit = new JLabel("Deposit: -");
        lblFinal = new JLabel("Final: -"); lblFinal.setFont(lblFinal.getFont().deriveFont(Font.BOLD));
        pricingPanel.add(lblRentalAmt); pricingPanel.add(lblMemDisc); pricingPanel.add(lblLongDisc);
        pricingPanel.add(lblDeposit); pricingPanel.add(lblFinal);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(pricingPanel, gbc);

        // Row 8: Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnCalculate = new JButton("Calculate");
        btnCreate    = new JButton("Create Rental");
        btnReturn    = new JButton("Process Return");
        btnRefresh   = new JButton("Refresh");
        btnPanel.add(btnCalculate); btnPanel.add(btnCreate); btnPanel.add(btnReturn); btnPanel.add(btnRefresh);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by Status:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Active", "Returned", "Overdue", "Cancelled"});
        if (overdueOnly) cmbStatusFilter.setSelectedItem("Overdue");
        filterPanel.add(cmbStatusFilter);
        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> loadTable());
        filterPanel.add(btnFilter);

        // Table
        String[] cols = {"ID", "Equipment", "Customer", "Branch", "Start", "End", "Return Date",
                "Rental Amt", "Deposit", "Mem Disc", "Long Disc", "Late Fee", "Damage", "Final Amt", "Payment", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        split.setDividerLocation(330);
        add(split);

        btnCalculate.addActionListener(e -> calculatePricing());
        btnCreate.addActionListener(e -> createRental());
        btnReturn.addActionListener(e -> processReturn());
        btnRefresh.addActionListener(e -> loadTable());
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
            List<Rental> list;
            String statusFilter = (String) cmbStatusFilter.getSelectedItem();
            if (overdueOnly || "Overdue".equals(statusFilter)) {
                list = rentalService.getOverdueRentals();
            } else if ("All".equals(statusFilter)) {
                list = "Admin".equals(loggedInUser.getRole()) ? rentalService.getAllRentals()
                        : rentalService.getRentalsByBranch(loggedInUser.getBranchId());
            } else {
                list = "Admin".equals(loggedInUser.getRole()) ? rentalService.getAllRentals()
                        : rentalService.getRentalsByBranch(loggedInUser.getBranchId());
                list = list.stream().filter(r -> statusFilter.equals(r.getRentalStatus())).collect(java.util.stream.Collectors.toList());
            }
            for (Rental r : list) {
                tableModel.addRow(new Object[]{r.getRentalId(), r.getEquipmentName(), r.getCustomerName(),
                        r.getBranchName(), r.getStartDate(), r.getEndDate(), r.getActualReturnDate(),
                        r.getRentalAmount(), r.getSecurityDeposit(), r.getMembershipDiscount(),
                        r.getLongRentalDiscount(), r.getLateFee(), r.getDamageCharge(),
                        r.getFinalAmount(), r.getPaymentStatus(), r.getRentalStatus()});
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void calculatePricing() {
        try {
            if (cmbEquipment.getSelectedItem() == null || cmbCustomer.getSelectedItem() == null) {
                showError("Select equipment and customer first."); return;
            }
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end   = LocalDate.parse(txtEndDate.getText().trim());
            String eqId = ((String) cmbEquipment.getSelectedItem()).split(" - ")[0];
            String cuId = ((String) cmbCustomer.getSelectedItem()).split(" - ")[0];

            Equipment eq = equipmentService.getEquipmentById(eqId);
            Customer  cu = customerService.getCustomerById(cuId);
            long days    = start.until(end).getDays() + 1;

            BigDecimal rentalAmt    = pricingService.calculateRentalAmount(eq.getCategoryId(), eq.getDailyBasePrice(), start, end);
            BigDecimal memDiscount  = pricingService.calculateMembershipDiscount(cu.getMembershipLevel(), rentalAmt);
            BigDecimal longDiscount = pricingService.calculateLongRentalDiscount(days, rentalAmt);
            BigDecimal finalAmt     = rentalAmt.subtract(memDiscount).subtract(longDiscount).add(eq.getSecurityDeposit());

            lblRentalAmt.setText(String.format("Rental: %.2f", rentalAmt));
            lblMemDisc.setText(String.format("Mem Disc: %.2f", memDiscount));
            lblLongDisc.setText(String.format("Long Disc: %.2f", longDiscount));
            lblDeposit.setText(String.format("Deposit: %.2f", eq.getSecurityDeposit()));
            lblFinal.setText(String.format("Final: %.2f", finalAmt));
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void createRental() {
        try {
            if (cmbEquipment.getSelectedItem() == null || cmbCustomer.getSelectedItem() == null) {
                showError("Select equipment and customer."); return;
            }
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            LocalDate end   = LocalDate.parse(txtEndDate.getText().trim());
            String eqId = ((String) cmbEquipment.getSelectedItem()).split(" - ")[0];
            String cuId = ((String) cmbCustomer.getSelectedItem()).split(" - ")[0];
            String brId = ((String) cmbBranch.getSelectedItem()).split(" - ")[0];

            Equipment eq = equipmentService.getEquipmentById(eqId);
            Customer  cu = customerService.getCustomerById(cuId);
            long days    = start.until(end).getDays() + 1;

            BigDecimal rentalAmt    = pricingService.calculateRentalAmount(eq.getCategoryId(), eq.getDailyBasePrice(), start, end);
            BigDecimal memDiscount  = pricingService.calculateMembershipDiscount(cu.getMembershipLevel(), rentalAmt);
            BigDecimal longDiscount = pricingService.calculateLongRentalDiscount(days, rentalAmt);
            BigDecimal finalAmt     = rentalAmt.subtract(memDiscount).subtract(longDiscount).add(eq.getSecurityDeposit());

            Rental rental = new Rental();
            rental.setRentalId(txtId.getText().trim());
            rental.setEquipmentId(eqId);
            rental.setCustomerId(cuId);
            rental.setBranchId(brId);
            rental.setStartDate(start);
            rental.setEndDate(end);
            rental.setRentalAmount(rentalAmt);
            rental.setSecurityDeposit(eq.getSecurityDeposit());
            rental.setMembershipDiscount(memDiscount);
            rental.setLongRentalDiscount(longDiscount);
            rental.setLateFee(BigDecimal.ZERO);
            rental.setDamageCharge(BigDecimal.ZERO);
            rental.setFinalAmount(finalAmt);
            rental.setPaymentStatus((String) cmbPayment.getSelectedItem());
            rental.setRentalStatus("Active");

            rentalService.createRental(rental);
            loadTable(); resetForm();
            JOptionPane.showMessageDialog(this, "Rental created. Final Amount: LKR " + finalAmt);
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void processReturn() {
        if (table.getSelectedRow() < 0) { showError("Select a rental to process return."); return; }
        String status = (String) tableModel.getValueAt(table.getSelectedRow(), 15);
        if ("Returned".equals(status) || "Cancelled".equals(status)) {
            showError("This rental is already " + status + "."); return;
        }

        String rentalId = (String) tableModel.getValueAt(table.getSelectedRow(), 0);

        JTextField txtReturnDate   = new JTextField(LocalDate.now().toString());
        JTextField txtDamageCharge = new JTextField("0.00");
        JTextField txtDamageDesc   = new JTextField();
        JComboBox<String> cmbPaymentStatus = new JComboBox<>(new String[]{"Paid", "Partially Paid", "Unpaid"});

        JPanel returnPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        returnPanel.add(new JLabel("Return Date (yyyy-MM-dd):")); returnPanel.add(txtReturnDate);
        returnPanel.add(new JLabel("Damage Charge (LKR):")); returnPanel.add(txtDamageCharge);
        returnPanel.add(new JLabel("Damage Description:"));   returnPanel.add(txtDamageDesc);
        returnPanel.add(new JLabel("Payment Status:"));        returnPanel.add(cmbPaymentStatus);

        int result = JOptionPane.showConfirmDialog(this, returnPanel, "Process Return for " + rentalId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            LocalDate returnDate = LocalDate.parse(txtReturnDate.getText().trim());
            Rental rental = rentalService.getRentalById(rentalId);

            Equipment eq = equipmentService.getEquipmentById(rental.getEquipmentId());
            BigDecimal lateFee     = pricingService.calculateLateFee(eq.getCategoryId(), rental.getEndDate(), returnDate);
            BigDecimal damageCharge = new BigDecimal(txtDamageCharge.getText().trim());

            rental.setActualReturnDate(returnDate);
            rental.setLateFee(lateFee);
            rental.setDamageCharge(damageCharge);
            rental.setDamageDescription(txtDamageDesc.getText().trim());
            rental.setPaymentStatus((String) cmbPaymentStatus.getSelectedItem());

            rentalService.processReturn(rental);

            BigDecimal totalCharges = lateFee.add(damageCharge);
            BigDecimal deposit      = rental.getSecurityDeposit();
            BigDecimal diff         = deposit.subtract(totalCharges);
            String settlementMsg;
            if (diff.compareTo(BigDecimal.ZERO) >= 0) {
                settlementMsg = String.format("Return processed.\nLate Fee: LKR %.2f\nDamage: LKR %.2f\nDeposit Refund: LKR %.2f", lateFee, damageCharge, diff);
            } else {
                settlementMsg = String.format("Return processed.\nLate Fee: LKR %.2f\nDamage: LKR %.2f\nAdditional Amount Due: LKR %.2f", lateFee, damageCharge, diff.abs());
            }
            loadTable(); resetForm();
            JOptionPane.showMessageDialog(this, settlementMsg);
        } catch (DateTimeParseException ex) {
            showError("Invalid return date format. Use yyyy-MM-dd.");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void resetForm() {
        try { txtId.setText(rentalService.generateNextId()); } catch (SQLException ignored) {}
        txtStartDate.setText(LocalDate.now().toString());
        txtEndDate.setText(LocalDate.now().plusDays(3).toString());
        lblRentalAmt.setText("Rental: -"); lblMemDisc.setText("Mem Disc: -");
        lblLongDisc.setText("Long Disc: -"); lblDeposit.setText("Deposit: -"); lblFinal.setText("Final: -");
        loadEquipmentCombo();
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
