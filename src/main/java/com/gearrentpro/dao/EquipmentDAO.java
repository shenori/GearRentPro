package com.gearrentpro.dao;

import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Equipment.EquipmentStatus;
import com.gearrentpro.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public String generateNextId() throws SQLException {
        String sql = "SELECT equipment_id FROM equipment ORDER BY equipment_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("equipment_id");
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("EQ%03d", num);
            }
            return "EQ001";
        }
    }

    public boolean save(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment (equipment_id, category_id, branch_id, brand, model, " +
                     "purchase_year, daily_base_price, security_deposit, status, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getEquipmentId());
            pstmt.setString(2, equipment.getCategoryId());
            pstmt.setString(3, equipment.getBranchId());
            pstmt.setString(4, equipment.getBrand());
            pstmt.setString(5, equipment.getModel());
            pstmt.setInt(6, equipment.getPurchaseYear());
            pstmt.setBigDecimal(7, equipment.getDailyBasePrice());
            pstmt.setBigDecimal(8, equipment.getSecurityDeposit());
            pstmt.setString(9, equipment.getStatus().name());
            pstmt.setString(10, equipment.getDescription());

            return pstmt.executeUpdate() > 0;
        }
    }

    public Equipment findById(String equipmentId) throws SQLException {
        String sql = "SELECT e.*, c.name as category_name, b.name as branch_name " +
                     "FROM equipment e " +
                     "LEFT JOIN categories c ON e.category_id = c.category_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.equipment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEquipment(rs);
            }
        }
        return null;
    }

    public List<Equipment> findAll() throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT e.*, c.name as category_name, b.name as branch_name " +
                     "FROM equipment e " +
                     "LEFT JOIN categories c ON e.category_id = c.category_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "ORDER BY e.equipment_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        }
        return equipmentList;
    }

    public List<Equipment> findByBranch(String branchId) throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT e.*, c.name as category_name, b.name as branch_name " +
                     "FROM equipment e " +
                     "LEFT JOIN categories c ON e.category_id = c.category_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.branch_id = ? " +
                     "ORDER BY e.equipment_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branchId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        }
        return equipmentList;
    }

    // Search with filters
    public List<Equipment> search(String branchId, String categoryId, 
                                   EquipmentStatus status, String keyword) throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, c.name as category_name, b.name as branch_name " +
            "FROM equipment e " +
            "LEFT JOIN categories c ON e.category_id = c.category_id " +
            "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        if (branchId != null && !branchId.isEmpty()) {
            sql.append("AND e.branch_id = ? ");
            params.add(branchId);
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (status != null) {
            sql.append("AND e.status = ? ");
            params.add(status.name());
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (LOWER(e.brand) LIKE ? OR LOWER(e.model) LIKE ? OR e.equipment_id LIKE ?) ");
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
        }
        
        sql.append("ORDER BY e.equipment_id");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        }
        return equipmentList;
    }

    // Find available equipment for rental
    public List<Equipment> findAvailableByBranch(String branchId) throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT e.*, c.name as category_name, b.name as branch_name " +
                     "FROM equipment e " +
                     "LEFT JOIN categories c ON e.category_id = c.category_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.branch_id = ? AND e.status = 'AVAILABLE' " +
                     "ORDER BY c.name, e.brand, e.model";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branchId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        }
        return equipmentList;
    }

    public boolean update(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipment SET category_id = ?, branch_id = ?, brand = ?, model = ?, " +
                     "purchase_year = ?, daily_base_price = ?, security_deposit = ?, status = ?, " +
                     "description = ? WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getCategoryId());
            pstmt.setString(2, equipment.getBranchId());
            pstmt.setString(3, equipment.getBrand());
            pstmt.setString(4, equipment.getModel());
            pstmt.setInt(5, equipment.getPurchaseYear());
            pstmt.setBigDecimal(6, equipment.getDailyBasePrice());
            pstmt.setBigDecimal(7, equipment.getSecurityDeposit());
            pstmt.setString(8, equipment.getStatus().name());
            pstmt.setString(9, equipment.getDescription());
            pstmt.setString(10, equipment.getEquipmentId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String equipmentId, EquipmentStatus status) throws SQLException {
        String sql = "UPDATE equipment SET status = ? WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            pstmt.setString(2, equipmentId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String equipmentId) throws SQLException {
        String sql = "DELETE FROM equipment WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean hasActiveRentals(String equipmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rentals WHERE equipment_id = ? AND rental_status IN ('ACTIVE', 'OVERDUE')";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(rs.getString("equipment_id"));
        equipment.setCategoryId(rs.getString("category_id"));
        equipment.setBranchId(rs.getString("branch_id"));
        equipment.setBrand(rs.getString("brand"));
        equipment.setModel(rs.getString("model"));
        equipment.setPurchaseYear(rs.getInt("purchase_year"));
        equipment.setDailyBasePrice(rs.getBigDecimal("daily_base_price"));
        equipment.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        equipment.setStatus(EquipmentStatus.valueOf(rs.getString("status")));
        equipment.setDescription(rs.getString("description"));
        equipment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Joined columns
        equipment.setCategoryName(rs.getString("category_name"));
        equipment.setBranchName(rs.getString("branch_name"));
        
        return equipment;
    }
}