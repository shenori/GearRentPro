package com.gearrentpro.controller;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Equipment.EquipmentStatus;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.service.CategoryService;
import com.gearrentpro.service.EquipmentService;
import com.gearrentpro.service.EquipmentService.ValidationException;
import com.gearrentpro.util.SessionManager;

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

public class EquipmentController implements Initializable {

    // Table components
    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TableColumn<Equipment, String> colId;
    @FXML private TableColumn<Equipment, String> colCategory;
    @FXML private TableColumn<Equipment, String> colBranch;
    @FXML private TableColumn<Equipment, String> colBrand;
    @FXML private TableColumn<Equipment, String> colModel;
    @FXML private TableColumn<Equipment, Integer> colYear;
    @FXML private TableColumn<Equipment, BigDecimal> colPrice;
    @FXML private TableColumn<Equipment, BigDecimal> colDeposit;
    @FXML private TableColumn<Equipment, EquipmentStatus> colStatus;
    @FXML private Label lblRecordCount;

    // Filter components
    @FXML private ComboBox<Branch> cmbFilterBranch;
    @FXML private ComboBox<Category> cmbFilterCategory;
    @FXML private ComboBox<EquipmentStatus> cmbFilterStatus;
    @FXML private TextField txtSearch;

    // Form components
    @FXML private Label formTitle;
    @FXML private TextField txtEquipmentId;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private ComboBox<Branch> cmbBranch;
    @FXML private TextField txtBrand;
    @FXML private TextField txtModel;
    @FXML private TextField txtYear;
    @FXML private TextField txtPrice;
    @FXML private TextField txtDeposit;
    @FXML private ComboBox<EquipmentStatus> cmbStatus;
    @FXML private TextArea txtDescription;
    @FXML private Label lblError;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private EquipmentService equipmentService;
    private BranchService branchService;
    private CategoryService categoryService;
    
    private ObservableList<Equipment> equipmentList;
    private ObservableList<Branch> branchList;
    private ObservableList<Category> categoryList;
    
    private Equipment selectedEquipment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        equipmentService = new EquipmentService();
        branchService = new BranchService();
        categoryService = new CategoryService();
        
        equipmentList = FXCollections.observableArrayList();
        branchList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadData();
        setupTableSelection();
        generateNextId();
        
        // Restrict branch selection based on role
        if (!SessionManager.getInstance().isAdmin()) {
            String userBranchId = SessionManager.getInstance().getCurrentBranchId();
            cmbBranch.setDisable(true);
            cmbFilterBranch.setDisable(true);
            // Set the user's branch and filter by it
            for (Branch b : branchList) {
                if (b.getBranchId().equals(userBranchId)) {
                    cmbBranch.setValue(b);
                    cmbFilterBranch.setValue(b);
                    break;
                }
            }
            handleSearch(); // Apply the filter
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("purchaseYear"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("dailyBasePrice"));
        colDeposit.setCellValueFactory(new PropertyValueFactory<>("securityDeposit"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Custom cell factory for status with colors
        colStatus.setCellFactory(col -> new TableCell<Equipment, EquipmentStatus>() {
            @Override
            protected void updateItem(EquipmentStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.getDisplayName());
                    switch (status) {
                        case AVAILABLE -> setStyle("-fx-text-fill: green;");
                        case RESERVED -> setStyle("-fx-text-fill: orange;");
                        case RENTED -> setStyle("-fx-text-fill: blue;");
                        case UNDER_MAINTENANCE -> setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });

        equipmentTable.setItems(equipmentList);
    }

    private void setupComboBoxes() {
        // Branch combo box converter
        StringConverter<Branch> branchConverter = new StringConverter<>() {
            @Override
            public String toString(Branch branch) {
                return branch == null ? "" : branch.getName();
            }
            @Override
            public Branch fromString(String string) {
                return null;
            }
        };
        
        cmbBranch.setConverter(branchConverter);
        cmbFilterBranch.setConverter(branchConverter);
        
        // Category combo box converter
        StringConverter<Category> categoryConverter = new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }
            @Override
            public Category fromString(String string) {
                return null;
            }
        };
        
        cmbCategory.setConverter(categoryConverter);
        cmbFilterCategory.setConverter(categoryConverter);
        
        // Status combo boxes
        cmbStatus.setItems(FXCollections.observableArrayList(EquipmentStatus.values()));
        cmbStatus.setValue(EquipmentStatus.AVAILABLE);
        
        ObservableList<EquipmentStatus> filterStatusList = FXCollections.observableArrayList();
        filterStatusList.add(null); // For "All Status" option
        filterStatusList.addAll(Arrays.asList(EquipmentStatus.values()));
        cmbFilterStatus.setItems(filterStatusList);
        
        cmbFilterStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(EquipmentStatus status) {
                return status == null ? "All Status" : status.getDisplayName();
            }
            @Override
            public EquipmentStatus fromString(String string) {
                return null;
            }
        });
    }

    private void loadData() {
        try {
            // Load branches
            branchList.clear();
            branchList.addAll(branchService.getAllBranches());
            cmbBranch.setItems(branchList);
            
            ObservableList<Branch> filterBranchList = FXCollections.observableArrayList();
            filterBranchList.add(null); // For "All Branches" option
            filterBranchList.addAll(branchList);
            cmbFilterBranch.setItems(filterBranchList);
            
            // Load categories
            categoryList.clear();
            categoryList.addAll(categoryService.getAllCategories());
            cmbCategory.setItems(categoryList);
            
            ObservableList<Category> filterCategoryList = FXCollections.observableArrayList();
            filterCategoryList.add(null); // For "All Categories" option
            filterCategoryList.addAll(categoryList);
            cmbFilterCategory.setItems(filterCategoryList);
            
            // Load equipment
            loadEquipment();
            
        } catch (SQLException e) {
            showError("Error loading data: " + e.getMessage());
        }
    }

    private void loadEquipment() {
        try {
            equipmentList.clear();
            equipmentList.addAll(equipmentService.getAllEquipment());
            updateRecordCount();
        } catch (SQLException e) {
            showError("Error loading equipment: " + e.getMessage());
        }
    }

    private void setupTableSelection() {
        equipmentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectEquipment(newSelection);
                }
            }
        );
    }

    private void generateNextId() {
        try {
            txtEquipmentId.setText(equipmentService.generateNextId());
        } catch (SQLException e) {
            showError("Error generating ID: " + e.getMessage());
        }
    }

    private void selectEquipment(Equipment equipment) {
        selectedEquipment = equipment;

        txtEquipmentId.setText(equipment.getEquipmentId());
        
        // Set category
        for (Category c : categoryList) {
            if (c.getCategoryId().equals(equipment.getCategoryId())) {
                cmbCategory.setValue(c);
                break;
            }
        }
        
        // Set branch
        for (Branch b : branchList) {
            if (b.getBranchId().equals(equipment.getBranchId())) {
                cmbBranch.setValue(b);
                break;
            }
        }
        
        txtBrand.setText(equipment.getBrand());
        txtModel.setText(equipment.getModel());
        txtYear.setText(String.valueOf(equipment.getPurchaseYear()));
        txtPrice.setText(equipment.getDailyBasePrice().toString());
        txtDeposit.setText(equipment.getSecurityDeposit().toString());
        cmbStatus.setValue(equipment.getStatus());
        txtDescription.setText(equipment.getDescription());

        formTitle.setText("Edit Equipment");
        btnSave.setVisible(false);
        btnSave.setManaged(false);
        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);

        clearError();
    }

    @FXML
    private void handleAddEquipment() {
        handleClear();
    }

    @FXML
    private void handleSearch() {
        try {
            Branch selectedBranch = cmbFilterBranch.getValue();
            Category selectedCategory = cmbFilterCategory.getValue();
            EquipmentStatus selectedStatus = cmbFilterStatus.getValue();
            String keyword = txtSearch.getText().trim();

            equipmentList.clear();
            equipmentList.addAll(equipmentService.searchEquipment(
                selectedBranch != null ? selectedBranch.getBranchId() : null,
                selectedCategory != null ? selectedCategory.getCategoryId() : null,
                selectedStatus,
                keyword.isEmpty() ? null : keyword
            ));
            updateRecordCount();
        } catch (SQLException e) {
            showError("Error searching: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilters() {
        cmbFilterBranch.setValue(null);
        cmbFilterCategory.setValue(null);
        cmbFilterStatus.setValue(null);
        txtSearch.clear();
        loadEquipment();
    }

    @FXML
    private void handleSave() {
        clearError();

        try {
            Equipment equipment = buildEquipmentFromForm();
            equipmentService.saveEquipment(equipment);
            showSuccess("Equipment saved successfully!");
            loadEquipment();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEquipment == null) {
            showError("No equipment selected");
            return;
        }

        clearError();

        try {
            updateEquipmentFromForm(selectedEquipment);
            equipmentService.updateEquipment(selectedEquipment);
            showSuccess("Equipment updated successfully!");
            loadEquipment();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEquipment == null) {
            showError("No equipment selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Equipment");
        alert.setContentText("Are you sure you want to delete: " + selectedEquipment.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                equipmentService.deleteEquipment(selectedEquipment.getEquipmentId());
                showSuccess("Equipment deleted successfully!");
                loadEquipment();
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
        selectedEquipment = null;

        generateNextId();
        cmbCategory.setValue(null);
        if (SessionManager.getInstance().isAdmin()) {
            cmbBranch.setValue(null);
        }
        txtBrand.clear();
        txtModel.clear();
        txtYear.clear();
        txtPrice.clear();
        txtDeposit.clear();
        cmbStatus.setValue(EquipmentStatus.AVAILABLE);
        txtDescription.clear();

        formTitle.setText("Add New Equipment");
        btnSave.setVisible(true);
        btnSave.setManaged(true);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        equipmentTable.getSelectionModel().clearSelection();
        clearError();
    }

    private Equipment buildEquipmentFromForm() {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(txtEquipmentId.getText());
        updateEquipmentFromForm(equipment);
        return equipment;
    }

    private void updateEquipmentFromForm(Equipment equipment) {
        Category selectedCategory = cmbCategory.getValue();
        Branch selectedBranch = cmbBranch.getValue();
        
        if (selectedCategory != null) {
            equipment.setCategoryId(selectedCategory.getCategoryId());
        }
        if (selectedBranch != null) {
            equipment.setBranchId(selectedBranch.getBranchId());
        }
        
        equipment.setBrand(txtBrand.getText().trim());
        equipment.setModel(txtModel.getText().trim());
        equipment.setPurchaseYear(Integer.parseInt(txtYear.getText().trim()));
        equipment.setDailyBasePrice(new BigDecimal(txtPrice.getText().trim()));
        equipment.setSecurityDeposit(new BigDecimal(txtDeposit.getText().trim()));
        equipment.setStatus(cmbStatus.getValue());
        equipment.setDescription(txtDescription.getText().trim());
    }

    private void updateRecordCount() {
        lblRecordCount.setText(equipmentList.size() + " records found");
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