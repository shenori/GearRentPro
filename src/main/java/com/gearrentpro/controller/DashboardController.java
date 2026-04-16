package com.gearrentpro.controller;

import com.gearrentpro.Main;
import com.gearrentpro.entity.User;
import com.gearrentpro.service.AuthService;
import com.gearrentpro.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label welcomeLabel;
    @FXML private Label branchLabel;
    @FXML private Label statusLabel;
    @FXML private Menu branchMenu;

    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authService = new AuthService();
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName());
            
            if (currentUser.getBranchId() != null) {
                branchLabel.setText("Branch: " + currentUser.getBranchId());
            } else {
                branchLabel.setText("All Branches (Admin)");
            }
            
            // Configure menu visibility based on role
            configureMenuAccess(currentUser.getRole());
        }
    }

    private void configureMenuAccess(User.UserRole role) {
        switch (role) {
            case ADMIN:
                // Admin has access to everything
                break;
            case BRANCH_MANAGER:
                // Branch Manager cannot manage branches
                branchMenu.setVisible(false);
                break;
            case STAFF:
                // Staff has limited access
                branchMenu.setVisible(false);
                break;
        }
    }

    private void loadContent(String fxmlFile) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            statusLabel.setText("Loaded: " + fxmlFile);
        } catch (IOException e) {
            statusLabel.setText("Error loading: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        Main.switchScene("login.fxml", "Login");
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void openBranches() {
        loadContent("branches.fxml");
    }

    @FXML
    private void openCategories() {
        loadContent("categories.fxml");
    }

    @FXML
    private void openEquipment() {
        loadContent("equipment.fxml");
    }

    @FXML
    private void openCustomers() {
        loadContent("customers.fxml");
    }

    @FXML
    private void openMembership() {
        statusLabel.setText("Membership configuration - Coming in Week 3");
    }

    @FXML
    private void openReservations() {
        statusLabel.setText("Reservations - Coming in Week 3");
    }

    @FXML
    private void openRentals() {
        statusLabel.setText("Rentals - Coming in Week 4");
    }

    @FXML
    private void openReturns() {
        statusLabel.setText("Returns - Coming in Week 4");
    }

    @FXML
    private void openOverdue() {
        statusLabel.setText("Overdue Rentals - Coming in Week 4");
    }

    @FXML
    private void openRevenueReport() {
        statusLabel.setText("Revenue Report - Coming in Week 5");
    }

    @FXML
    private void openUtilizationReport() {
        statusLabel.setText("Utilization Report - Coming in Week 5");
    }
}