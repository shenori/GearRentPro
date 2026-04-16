package com.gearrentpro.controller;

import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Customer.MembershipLevel;
import com.gearrentpro.service.CustomerService;
import com.gearrentpro.service.CustomerService.ValidationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    // Table components
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colNIC;
    @FXML private TableColumn<Customer, String> colContact;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, MembershipLevel> colMembership;
    @FXML private TableColumn<Customer, Boolean> colStatus;
    @FXML private Label lblRecordCount;
    @FXML private Label lblActiveDeposit;

    // Filter components
    @FXML private TextField txtSearch;
    @FXML private ComboBox<MembershipLevel> cmbFilterMembership;

    // Form components
    @FXML private Label formTitle;
    @FXML private TextField txtCustomerId;
    @FXML private TextField txtName;
    @FXML private TextField txtNIC;
    @FXML private TextField txtContact;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtAddress;
    @FXML private ComboBox<MembershipLevel> cmbMembership;
    @FXML private CheckBox chkActive;
    @FXML private Label lblError;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private CustomerService customerService;
    private ObservableList<Customer> customerList;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerService = new CustomerService();
        customerList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadCustomers();
        setupTableSelection();
        generateNextId();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nicPassport"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMembership.setCellValueFactory(new PropertyValueFactory<>("membershipLevel"));
        
        // Custom cell for membership with colors
        colMembership.setCellFactory(col -> new TableCell<Customer, MembershipLevel>() {
            @Override
            protected void updateItem(MembershipLevel level, boolean empty) {
                super.updateItem(level, empty);
                if (empty || level == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(level.getDisplayName());
                    switch (level) {
                        case GOLD -> setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                        case SILVER -> setStyle("-fx-text-fill: #C0C0C0; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });
        
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setCellFactory(col -> new TableCell<Customer, Boolean>() {
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

        customerTable.setItems(customerList);
    }

    private void setupComboBoxes() {
        // Membership combo box for form
        cmbMembership.setItems(FXCollections.observableArrayList(MembershipLevel.values()));
        cmbMembership.setValue(MembershipLevel.REGULAR);
        
        // Membership filter combo box
        ObservableList<MembershipLevel> filterList = FXCollections.observableArrayList();
        filterList.add(null); // For "All Levels" option
        filterList.addAll(Arrays.asList(MembershipLevel.values()));
        cmbFilterMembership.setItems(filterList);
        
        cmbFilterMembership.setConverter(new StringConverter<>() {
            @Override
            public String toString(MembershipLevel level) {
                return level == null ? "All Levels" : level.getDisplayName();
            }
            @Override
            public MembershipLevel fromString(String string) {
                return null;
            }
        });
    }

    private void loadCustomers() {
        try {
            customerList.clear();
            customerList.addAll(customerService.getAllCustomers());
            updateRecordCount();
        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    private void setupTableSelection() {
        customerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectCustomer(newSelection);
                }
            }
        );
    }

    private void generateNextId() {
        try {
            txtCustomerId.setText(customerService.generateNextId());
        } catch (SQLException e) {
            showError("Error generating ID: " + e.getMessage());
        }
    }

    private void selectCustomer(Customer customer) {
        selectedCustomer = customer;

        txtCustomerId.setText(customer.getCustomerId());
        txtName.setText(customer.getName());
        txtNIC.setText(customer.getNicPassport());
        txtContact.setText(customer.getContactNo());
        txtEmail.setText(customer.getEmail());
        txtAddress.setText(customer.getAddress());
        cmbMembership.setValue(customer.getMembershipLevel());
        chkActive.setSelected(customer.isActive());

        // Load active deposit info
        loadActiveDeposit(customer.getCustomerId());

        formTitle.setText("Edit Customer");
        btnSave.setVisible(false);
        btnSave.setManaged(false);
        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);

        clearError();
    }

    private void loadActiveDeposit(String customerId) {
        try {
            BigDecimal deposit = customerService.getTotalActiveDeposit(customerId);
            lblActiveDeposit.setText(String.format("Active Deposits: LKR %,.2f", deposit));
        } catch (SQLException e) {
            lblActiveDeposit.setText("Active Deposits: Error");
        }
    }

    @FXML
    private void handleAddCustomer() {
        handleClear();
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            MembershipLevel level = cmbFilterMembership.getValue();

            customerList.clear();
            customerList.addAll(customerService.searchCustomers(
                keyword.isEmpty() ? null : keyword,
                level
            ));
            updateRecordCount();
        } catch (SQLException e) {
            showError("Error searching: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cmbFilterMembership.setValue(null);
        loadCustomers();
    }

    @FXML
    private void handleSave() {
        clearError();

        try {
            Customer customer = buildCustomerFromForm();
            customerService.saveCustomer(customer);
            showSuccess("Customer saved successfully!");
            loadCustomers();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            showError("No customer selected");
            return;
        }

        clearError();

        try {
            updateCustomerFromForm(selectedCustomer);
            customerService.updateCustomer(selectedCustomer);
            showSuccess("Customer updated successfully!");
            loadCustomers();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            showError("No customer selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete customer: " + selectedCustomer.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerService.deleteCustomer(selectedCustomer.getCustomerId());
                showSuccess("Customer deleted successfully!");
                loadCustomers();
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
        selectedCustomer = null;

        generateNextId();
        txtName.clear();
        txtNIC.clear();
        txtContact.clear();
        txtEmail.clear();
        txtAddress.clear();
        cmbMembership.setValue(MembershipLevel.REGULAR);
        chkActive.setSelected(true);
        lblActiveDeposit.setText("Active Deposits: LKR 0.00");

        formTitle.setText("Add New Customer");
        btnSave.setVisible(true);
        btnSave.setManaged(true);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        customerTable.getSelectionModel().clearSelection();
        clearError();
    }

    @FXML
    private void handleViewHistory() {
        if (selectedCustomer == null) {
            showError("Please select a customer first");
            return;
        }
        
        // TODO: Open rental history dialog/screen
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rental History");
        alert.setHeaderText("Customer: " + selectedCustomer.getName());
        alert.setContentText("Rental history feature will be implemented in Week 3-4.");
        alert.showAndWait();
    }

    private Customer buildCustomerFromForm() {
        Customer customer = new Customer();
        customer.setCustomerId(txtCustomerId.getText());
        updateCustomerFromForm(customer);
        return customer;
    }

    private void updateCustomerFromForm(Customer customer) {
        customer.setName(txtName.getText().trim());
        customer.setNicPassport(txtNIC.getText().trim().toUpperCase());
        customer.setContactNo(txtContact.getText().trim());
        customer.setEmail(txtEmail.getText().trim());
        customer.setAddress(txtAddress.getText().trim());
        customer.setMembershipLevel(cmbMembership.getValue());
        customer.setActive(chkActive.isSelected());
    }

    private void updateRecordCount() {
        lblRecordCount.setText(customerList.size() + " records found");
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
}