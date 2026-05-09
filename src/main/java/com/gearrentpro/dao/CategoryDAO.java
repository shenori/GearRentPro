// =================== CategoryDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Category;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getString("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setDescription(rs.getString("description"));
        c.setBasePriceFactor(rs.getBigDecimal("base_price_factor"));
        c.setWeekendMultiplier(rs.getBigDecimal("weekend_multiplier"));
        c.setLateFeePerDay(rs.getBigDecimal("late_fee_per_day"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Category> findAllActive() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE is_active=TRUE";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Category findById(String categoryId) throws SQLException {
        String sql = "SELECT * FROM category WHERE category_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, categoryId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public boolean save(Category c) throws SQLException {
        String sql = "INSERT INTO category VALUES (?,?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getCategoryId());
        ps.setString(2, c.getCategoryName());
        ps.setString(3, c.getDescription());
        ps.setBigDecimal(4, c.getBasePriceFactor());
        ps.setBigDecimal(5, c.getWeekendMultiplier());
        ps.setBigDecimal(6, c.getLateFeePerDay());
        ps.setBoolean(7, c.isActive());
        return ps.executeUpdate() > 0;
    }

    public boolean update(Category c) throws SQLException {
        String sql = "UPDATE category SET category_name=?, description=?, base_price_factor=?, weekend_multiplier=?, late_fee_per_day=?, is_active=? WHERE category_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getCategoryName());
        ps.setString(2, c.getDescription());
        ps.setBigDecimal(3, c.getBasePriceFactor());
        ps.setBigDecimal(4, c.getWeekendMultiplier());
        ps.setBigDecimal(5, c.getLateFeePerDay());
        ps.setBoolean(6, c.isActive());
        ps.setString(7, c.getCategoryId());
        return ps.executeUpdate() > 0;
    }

    public boolean delete(String categoryId) throws SQLException {
        String sql = "DELETE FROM category WHERE category_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, categoryId);
        return ps.executeUpdate() > 0;
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT category_id FROM category ORDER BY category_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next()) {
            int num = Integer.parseInt(rs.getString("category_id").substring(1)) + 1;
            return String.format("C%03d", num);
        }
        return "C001";
    }
}