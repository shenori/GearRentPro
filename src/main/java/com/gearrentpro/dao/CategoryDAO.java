package com.gearrentpro.dao;

import com.gearrentpro.entity.Category;
import com.gearrentpro.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public String generateNextId() throws SQLException {
        String sql = "SELECT category_id FROM categories ORDER BY category_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("category_id");
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("CAT%03d", num);
            }
            return "CAT001";
        }
    }

    public boolean save(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_id, name, description, base_price_factor, " +
                     "weekend_multiplier, default_late_fee_per_day, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getCategoryId());
            pstmt.setString(2, category.getName());
            pstmt.setString(3, category.getDescription());
            pstmt.setBigDecimal(4, category.getBasePriceFactor());
            pstmt.setBigDecimal(5, category.getWeekendMultiplier());
            pstmt.setBigDecimal(6, category.getDefaultLateFeePerDay());
            pstmt.setBoolean(7, category.isActive());

            return pstmt.executeUpdate() > 0;
        }
    }

    public Category findById(String categoryId) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        }
        return null;
    }

    public List<Category> findAll() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = true ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        return categories;
    }

    public List<Category> findAllIncludingInactive() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        return categories;
    }

    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ?, base_price_factor = ?, " +
                     "weekend_multiplier = ?, default_late_fee_per_day = ?, is_active = ? WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setBigDecimal(3, category.getBasePriceFactor());
            pstmt.setBigDecimal(4, category.getWeekendMultiplier());
            pstmt.setBigDecimal(5, category.getDefaultLateFeePerDay());
            pstmt.setBoolean(6, category.isActive());
            pstmt.setString(7, category.getCategoryId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deactivate(String categoryId) throws SQLException {
        String sql = "UPDATE categories SET is_active = false WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean hasEquipment(String categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM equipment WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean existsByName(String name, String excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?) AND category_id != ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, excludeId != null ? excludeId : "");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getString("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setBasePriceFactor(rs.getBigDecimal("base_price_factor"));
        category.setWeekendMultiplier(rs.getBigDecimal("weekend_multiplier"));
        category.setDefaultLateFeePerDay(rs.getBigDecimal("default_late_fee_per_day"));
        category.setActive(rs.getBoolean("is_active"));
        category.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return category;
    }
}