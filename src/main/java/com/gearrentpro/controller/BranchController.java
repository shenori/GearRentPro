package com.gearrentpro.controller;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.service.BranchService.ValidationException;
import com.gearrentpro.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class BranchController implements Initializable {

    @FXML private TableView<Branch> branchTable;
    @FXML private TableColumn<Branch, String> colId;
    @FXML private TableColumn<Branch, String> colName;
    @FXML private TableColumn<Branch, String> colAddress;
    @FXML private TableColumn<Branch, String> colContact;
    @FXML private TableColumn<Branch, Boolean> colStatus;

    @FXML private Label formTitle;
    @FXML private TextField txtBranchId;
    @FXML private TextField txtName;
    @FXML private TextArea txtAddress;
    @FXML private TextField txtContact;
    @FXML private CheckBox chkActive;
    @FXML private Label lblError;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private BranchService branchService;
    private ObservableList<Branch> branchList;
    private Branch selectedBranch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        branchService = new BranchService();
        branchList = FXCollections.observableArrayList();

        // Check if user is admin
        if (!SessionManager.getInstance().isAdmin()) {
            showError("Access denied. Admin privileges required.");
            disableAllControls();
            return;
        }

        setupTable();
        loadBranches();
        setupTableSelection();
        generateNextId();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        
        // Custom cell factory for status column
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setCellFactory(col -> new TableCell<Branch, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(active ? "Active" : "Inactive");
                    setStyle(active ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });

        branchTable.setItems(branchList);
    }

    private void setupTableSelection() {
        branchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectBranch(newSelection);
                }
            }
        );
    }

    private void loadBranches() {
        try {
            branchList.clear();
            branchList.addAll(branchService.getAllBranchesIncludingInactive());
        } catch (SQLException e) {
            showError("Error loading branches: " + e.getMessage());
        }
    }

    private void generateNextId() {
        try {
            txtBranchId.setText(branchService.generateNextId());
        } catch (SQLException e) {
            showError("Error generating ID: " + e.getMessage());
        }
    }

    private void selectBranch(Branch branch) {
        selectedBranch = branch;
        
        txtBranchId.setText(branch.getBranchId());
        txtName.setText(branch.getName());
        txtAddress.setText(branch.getAddress());
        txtContact.setText(branch.getContact());
        chkActive.setSelected(branch.isActive());

        // Switch to edit mode
        formTitle.setText("Edit Branch");
        btnSave.setVisible(false);
        btnSave.setManaged(false);
        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);

        clearError();
    }

    @FXML
    private void handleAddBranch() {
        handleClear();
    }

    @FXML
    private void handleSave() {
        clearError();

        Branch branch = new Branch();
        branch.setBranchId(txtBranchId.getText());
        branch.setName(txtName.getText().trim());
        branch.setAddress(txtAddress.getText().trim());
        branch.setContact(txtContact.getText().trim());
        branch.setActive(chkActive.isSelected());

        try {
            branchService.saveBranch(branch);
            showSuccess("Branch saved successfully!");
            loadBranches();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedBranch == null) {
            showError("No branch selected");
            return;
        }

        clearError();

        selectedBranch.setName(txtName.getText().trim());
        selectedBranch.setAddress(txtAddress.getText().trim());
        selectedBranch.setContact(txtContact.getText().trim());
        selectedBranch.setActive(chkActive.isSelected());

        try {
            branchService.updateBranch(selectedBranch);
            showSuccess("Branch updated successfully!");
            loadBranches();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedBranch == null) {
            showError("No branch selected");
            return;
        }

        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Branch");
        alert.setContentText("Are you sure you want to delete branch: " + selectedBranch.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                branchService.deleteBranch(selectedBranch.getBranchId());
                showSuccess("Branch deleted successfully!");
                loadBranches();
                handleClear();
            } catch (ValidationException e) {
                showError(e.getMessage());
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedBranch = null;
        
        generateNextId();
        txtName.clear();
        txtAddress.clear();
        txtContact.clear();
        chkActive.setSelected(true);

        // Switch to add mode
        formTitle.setText("Add New Branch");
        btnSave.setVisible(true);
        btnSave.setManaged(true);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        branchTable.getSelectionModel().clearSelection();
        clearError();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: green;");
    }

    private void clearError() {
        lblError.setText("");
    }

    private void disableAllControls() {
        txtName.setDisable(true);
        txtAddress.setDisable(true);
        txtContact.setDisable(true);
        chkActive.setDisable(true);
        btnSave.setDisable(true);
    }
}