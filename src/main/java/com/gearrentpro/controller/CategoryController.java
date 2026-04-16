package com.gearrentpro.controller;

import com.gearrentpro.entity.Category;
import com.gearrentpro.service.CategoryService;
import com.gearrentpro.service.CategoryService.ValidationException;
import com.gearrentpro.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> colId;
    @FXML private TableColumn<Category, String> colName;
    @FXML private TableColumn<Category, String> colDescription;
    @FXML private TableColumn<Category, BigDecimal> colPriceFactor;
    @FXML private TableColumn<Category, BigDecimal> colWeekendMultiplier;
    @FXML private TableColumn<Category, BigDecimal> colLateFee;
    @FXML private TableColumn<Category, Boolean> colStatus;

    @FXML private Label formTitle;
    @FXML private TextField txtCategoryId;
    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtPriceFactor;
    @FXML private TextField txtWeekendMultiplier;
    @FXML private TextField txtLateFee;
    @FXML private CheckBox chkActive;
    @FXML private Label lblError;

    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private CategoryService categoryService;
    private ObservableList<Category> categoryList;
    private Category selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryService = new CategoryService();
        categoryList = FXCollections.observableArrayList();

        // Only Admin and Branch Manager can access
        if (!SessionManager.getInstance().isAdmin() && !SessionManager.getInstance().isBranchManager()) {
            showError("Access denied. Admin or Branch Manager privileges required.");
            disableAllControls();
            return;
        }

        setupTable();
        loadCategories();
        setupTableSelection();
        generateNextId();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPriceFactor.setCellValueFactory(new PropertyValueFactory<>("basePriceFactor"));
        colWeekendMultiplier.setCellValueFactory(new PropertyValueFactory<>("weekendMultiplier"));
        colLateFee.setCellValueFactory(new PropertyValueFactory<>("defaultLateFeePerDay"));
        
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setCellFactory(col -> new TableCell<Category, Boolean>() {
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

        categoryTable.setItems(categoryList);
    }

    private void setupTableSelection() {
        categoryTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectCategory(newSelection);
                }
            }
        );
    }

    private void loadCategories() {
        try {
            categoryList.clear();
            categoryList.addAll(categoryService.getAllCategoriesIncludingInactive());
        } catch (SQLException e) {
            showError("Error loading categories: " + e.getMessage());
        }
    }

    private void generateNextId() {
        try {
            txtCategoryId.setText(categoryService.generateNextId());
        } catch (SQLException e) {
            showError("Error generating ID: " + e.getMessage());
        }
    }

    private void selectCategory(Category category) {
        selectedCategory = category;

        txtCategoryId.setText(category.getCategoryId());
        txtName.setText(category.getName());
        txtDescription.setText(category.getDescription());
        txtPriceFactor.setText(category.getBasePriceFactor().toString());
        txtWeekendMultiplier.setText(category.getWeekendMultiplier().toString());
        txtLateFee.setText(category.getDefaultLateFeePerDay() != null ? 
                          category.getDefaultLateFeePerDay().toString() : "");
        chkActive.setSelected(category.isActive());

        formTitle.setText("Edit Category");
        btnSave.setVisible(false);
        btnSave.setManaged(false);
        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);

        clearError();
    }

    @FXML
    private void handleAddCategory() {
        handleClear();
    }

    @FXML
    private void handleSave() {
        clearError();

        try {
            Category category = buildCategoryFromForm();
            categoryService.saveCategory(category);
            showSuccess("Category saved successfully!");
            loadCategories();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values for factors and fees");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCategory == null) {
            showError("No category selected");
            return;
        }

        clearError();

        try {
            updateCategoryFromForm(selectedCategory);
            categoryService.updateCategory(selectedCategory);
            showSuccess("Category updated successfully!");
            loadCategories();
            handleClear();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values for factors and fees");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCategory == null) {
            showError("No category selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Category");
        alert.setContentText("Are you sure you want to delete category: " + selectedCategory.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.deleteCategory(selectedCategory.getCategoryId());
                showSuccess("Category deleted successfully!");
                loadCategories();
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
        selectedCategory = null;

        generateNextId();
        txtName.clear();
        txtDescription.clear();
        txtPriceFactor.setText("1.00");
        txtWeekendMultiplier.setText("1.00");
        txtLateFee.setText("500.00");
        chkActive.setSelected(true);

        formTitle.setText("Add New Category");
        btnSave.setVisible(true);
        btnSave.setManaged(true);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        categoryTable.getSelectionModel().clearSelection();
        clearError();
    }

    private Category buildCategoryFromForm() {
        Category category = new Category();
        category.setCategoryId(txtCategoryId.getText());
        updateCategoryFromForm(category);
        return category;
    }

    private void updateCategoryFromForm(Category category) {
        category.setName(txtName.getText().trim());
        category.setDescription(txtDescription.getText().trim());
        category.setBasePriceFactor(new BigDecimal(txtPriceFactor.getText().trim()));
        category.setWeekendMultiplier(new BigDecimal(txtWeekendMultiplier.getText().trim()));
        
        String lateFee = txtLateFee.getText().trim();
        if (!lateFee.isEmpty()) {
            category.setDefaultLateFeePerDay(new BigDecimal(lateFee));
        }
        
        category.setActive(chkActive.isSelected());
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
        txtDescription.setDisable(true);
        txtPriceFactor.setDisable(true);
        txtWeekendMultiplier.setDisable(true);
        txtLateFee.setDisable(true);
        chkActive.setDisable(true);
        btnSave.setDisable(true);
    }
}