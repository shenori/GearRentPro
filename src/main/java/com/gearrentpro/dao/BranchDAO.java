package com.gearrentpro.dao;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    // Generate next branch ID
    public String generateNextId() throws SQLException {
        String sql = "SELECT branch_id FROM branches ORDER BY branch_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("branch_id");
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("BR%03d", num);
            }
            return "BR001";
        }
    }

    // Create
    public boolean save(Branch branch) throws SQLException {
        String sql = "INSERT INTO branches (branch_id, name, address, contact, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branch.getBranchId());
            pstmt.setString(2, branch.getName());
            pstmt.setString(3, branch.getAddress());
            pstmt.setString(4, branch.getContact());
            pstmt.setBoolean(5, branch.isActive());

            return pstmt.executeUpdate() > 0;
        }
    }

    // Read by ID
    public Branch findById(String branchId) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branchId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBranch(rs);
            }
        }
        return null;
    }

    // Read all active
    public List<Branch> findAll() throws SQLException {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE is_active = true ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }
        }
        return branches;
    }

    // Read all (including inactive)
    public List<Branch> findAllIncludingInactive() throws SQLException {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }
        }
        return branches;
    }

    // Update
    public boolean update(Branch branch) throws SQLException {
        String sql = "UPDATE branches SET name = ?, address = ?, contact = ?, is_active = ? WHERE branch_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branch.getName());
            pstmt.setString(2, branch.getAddress());
            pstmt.setString(3, branch.getContact());
            pstmt.setBoolean(4, branch.isActive());
            pstmt.setString(5, branch.getBranchId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // Soft delete (deactivate)
    public boolean deactivate(String branchId) throws SQLException {
        String sql = "UPDATE branches SET is_active = false WHERE branch_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branchId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Check if branch has equipment
    public boolean hasEquipment(String branchId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM equipment WHERE branch_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, branchId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Check if branch name exists
    public boolean existsByName(String name, String excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM branches WHERE LOWER(name) = LOWER(?) AND branch_id != ?";

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

    private Branch mapResultSetToBranch(ResultSet rs) throws SQLException {
        Branch branch = new Branch();
        branch.setBranchId(rs.getString("branch_id"));
        branch.setName(rs.getString("name"));
        branch.setAddress(rs.getString("address"));
        branch.setContact(rs.getString("contact"));
        branch.setActive(rs.getBoolean("is_active"));
        branch.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return branch;
    }
}