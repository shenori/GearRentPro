package com.gearrentpro.service;

import com.gearrentpro.dao.CategoryDAO;
import com.gearrentpro.entity.Category;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public String generateNextId() throws SQLException {
        return categoryDAO.generateNextId();
    }

    public void saveCategory(Category category) throws SQLException, ValidationException {
        validateCategory(category);

        if (categoryDAO.existsByName(category.getName(), null)) {
            throw new ValidationException("Category with this name already exists");
        }

        if (category.getCategoryId() == null || category.getCategoryId().isEmpty()) {
            category.setCategoryId(categoryDAO.generateNextId());
        }

        if (!categoryDAO.save(category)) {
            throw new SQLException("Failed to save category");
        }
    }

    public void updateCategory(Category category) throws SQLException, ValidationException {
        validateCategory(category);

        if (categoryDAO.existsByName(category.getName(), category.getCategoryId())) {
            throw new ValidationException("Another category with this name already exists");
        }

        if (!categoryDAO.update(category)) {
            throw new SQLException("Failed to update category");
        }
    }

    public void deleteCategory(String categoryId) throws SQLException, ValidationException {
        if (categoryDAO.hasEquipment(categoryId)) {
            throw new ValidationException("Cannot delete category with existing equipment");
        }

        if (!categoryDAO.deactivate(categoryId)) {
            throw new SQLException("Failed to delete category");
        }
    }

    public Category getCategoryById(String categoryId) throws SQLException {
        return categoryDAO.findById(categoryId);
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Category> getAllCategoriesIncludingInactive() throws SQLException {
        return categoryDAO.findAllIncludingInactive();
    }

    private void validateCategory(Category category) throws ValidationException {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
        if (category.getName().length() > 50) {
            throw new ValidationException("Category name must be less than 50 characters");
        }
        if (category.getBasePriceFactor() == null || 
            category.getBasePriceFactor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Base price factor must be greater than 0");
        }
        if (category.getBasePriceFactor().compareTo(new BigDecimal("10")) > 0) {
            throw new ValidationException("Base price factor cannot exceed 10");
        }
        if (category.getWeekendMultiplier() == null || 
            category.getWeekendMultiplier().compareTo(BigDecimal.ONE) < 0) {
            throw new ValidationException("Weekend multiplier must be at least 1.0");
        }
        if (category.getWeekendMultiplier().compareTo(new BigDecimal("5")) > 0) {
            throw new ValidationException("Weekend multiplier cannot exceed 5.0");
        }
        if (category.getDefaultLateFeePerDay() != null && 
            category.getDefaultLateFeePerDay().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Late fee cannot be negative");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}