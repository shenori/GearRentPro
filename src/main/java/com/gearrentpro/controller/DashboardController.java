// =================== DashboardController.java ===================
package main.java.com.gearrentpro.controller;

import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.service.AuthService;
import main.java.com.gearrentpro.service.RentalService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DashboardController extends JFrame {

    private User loggedInUser;
    private AuthService authService = new AuthService();

    public DashboardController(User user) {
        this.loggedInUser = user;
        setTitle("GearRent Pro - Dashboard [" + user.getRole() + ": " + user.getFullName() + "]");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        updateOverdueRentals();
    }

    private void initUI() {
        JMenuBar menuBar = new JMenuBar();

        // ---- Admin-only menus ----
        if ("Admin".equals(loggedInUser.getRole())) {
            JMenu menuBranch = new JMenu("Branches");
            JMenuItem miBranchManage = new JMenuItem("Manage Branches");
            miBranchManage.addActionListener(e -> new BranchController(loggedInUser).setVisible(true));
            menuBranch.add(miBranchManage);
            menuBar.add(menuBranch);

            JMenu menuMembership = new JMenu("Membership");
            JMenuItem miMembership = new JMenuItem("Membership Config");
            miMembership.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Membership configuration: Regular (0%), Silver (5%), Gold (10%)\nEdit in MembershipConfig.java"));
            menuMembership.add(miMembership);
            menuBar.add(menuMembership);
        }

        // ---- Admin + Branch Manager ----
        if ("Admin".equals(loggedInUser.getRole()) || "Branch Manager".equals(loggedInUser.getRole())) {
            JMenu menuCategory = new JMenu("Categories");
            JMenuItem miCategory = new JMenuItem("Manage Categories");
            miCategory.addActionListener(e -> new CategoryController(loggedInUser).setVisible(true));
            menuCategory.add(miCategory);
            menuBar.add(menuCategory);
        }

        // ---- Equipment ----
        JMenu menuEquipment = new JMenu("Equipment");
        JMenuItem miEquipment = new JMenuItem("Manage Equipment");
        miEquipment.addActionListener(e -> new EquipmentController(loggedInUser).setVisible(true));
        menuEquipment.add(miEquipment);
        menuBar.add(menuEquipment);

        // ---- Customers ----
        JMenu menuCustomer = new JMenu("Customers");
        JMenuItem miCustomer = new JMenuItem("Manage Customers");
        miCustomer.addActionListener(e -> new CustomerController(loggedInUser).setVisible(true));
        menuCustomer.add(miCustomer);
        menuBar.add(menuCustomer);

        // ---- Reservations ----
        JMenu menuReservation = new JMenu("Reservations");
        JMenuItem miReservation = new JMenuItem("Manage Reservations");
        miReservation.addActionListener(e -> new ReservationController(loggedInUser).setVisible(true));
        menuReservation.add(miReservation);
        menuBar.add(menuReservation);

        // ---- Rentals ----
        JMenu menuRental = new JMenu("Rentals");
        JMenuItem miRental = new JMenuItem("Manage Rentals");
        miRental.addActionListener(e -> new RentalController(loggedInUser).setVisible(true));
        JMenuItem miOverdue = new JMenuItem("Overdue Rentals");
        miOverdue.addActionListener(e -> new RentalController(loggedInUser, true).setVisible(true));
        menuRental.add(miRental);
        menuRental.add(miOverdue);
        menuBar.add(menuRental);

        // ---- Reports ----
        JMenu menuReport = new JMenu("Reports");
        JMenuItem miReport = new JMenuItem("View Reports");
        miReport.addActionListener(e -> new ReportController(loggedInUser).setVisible(true));
        menuReport.add(miReport);
        menuBar.add(menuReport);

        // ---- Logout ----
        JMenu menuLogout = new JMenu("Account");
        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.addActionListener(e -> {
            authService.logout();
            dispose();
            new LoginController().setVisible(true);
        });
        menuLogout.add(miLogout);
        menuBar.add(menuLogout);

        setJMenuBar(menuBar);

        // ---- Center Panel ----
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 248, 255));

        JLabel welcome = new JLabel("Welcome, " + loggedInUser.getFullName() + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setForeground(new Color(0, 80, 160));

        JLabel roleLabel = new JLabel("Role: " + loggedInUser.getRole() +
                (loggedInUser.getBranchName() != null ? "  |  Branch: " + loggedInUser.getBranchName() : ""),
                SwingConstants.CENTER);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(Color.DARK_GRAY);

        JLabel hint = new JLabel("Use the menu bar above to navigate.", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 0, 10, 0);
        centerPanel.add(welcome, gbc);
        gbc.gridy = 1;
        centerPanel.add(roleLabel, gbc);
        gbc.gridy = 2;
        centerPanel.add(hint, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void updateOverdueRentals() {
        try {
            new RentalService().updateOverdueRentals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
