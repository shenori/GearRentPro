// =================== CategoryService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.CategoryDAO;
import main.java.com.gearrentpro.entity.Category;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Category> getActiveCategories() throws SQLException {
        return categoryDAO.findAllActive();
    }

    public Category getCategoryById(String categoryId) throws SQLException {
        return categoryDAO.findById(categoryId);
    }

    public boolean addCategory(Category category) throws SQLException {
        if (category.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        return categoryDAO.save(category);
    }

    public boolean updateCategory(Category category) throws SQLException {
        return categoryDAO.update(category);
    }

    public boolean deleteCategory(String categoryId) throws SQLException {
        return categoryDAO.delete(categoryId);
    }

    public String generateNextId() throws SQLException {
        return categoryDAO.generateNextId();
    }
}