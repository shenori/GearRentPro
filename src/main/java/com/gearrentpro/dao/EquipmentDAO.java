// =================== EquipmentDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Equipment;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    private Equipment mapRow(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setEquipmentId(rs.getString("equipment_id"));
        e.setCategoryId(rs.getString("category_id"));
        e.setBranchId(rs.getString("branch_id"));
        e.setBrand(rs.getString("brand"));
        e.setModel(rs.getString("model"));
        e.setPurchaseYear(rs.getInt("purchase_year"));
        e.setDailyBasePrice(rs.getBigDecimal("daily_base_price"));
        e.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        e.setStatus(rs.getString("status"));
        try { e.setCategoryName(rs.getString("category_name")); } catch (SQLException ex) {}
        try { e.setBranchName(rs.getString("branch_name")); } catch (SQLException ex) {}
        return e;
    }

    public List<Equipment> findAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e JOIN category c ON e.category_id=c.category_id JOIN branch b ON e.branch_id=b.branch_id";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Equipment> findByBranch(String branchId) throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e JOIN category c ON e.category_id=c.category_id JOIN branch b ON e.branch_id=b.branch_id WHERE e.branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Equipment> findAvailableByBranch(String branchId) throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e JOIN category c ON e.category_id=c.category_id JOIN branch b ON e.branch_id=b.branch_id WHERE e.branch_id=? AND e.status='Available'";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Equipment findById(String equipmentId) throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e JOIN category c ON e.category_id=c.category_id JOIN branch b ON e.branch_id=b.branch_id WHERE e.equipment_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, equipmentId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public boolean save(Equipment e) throws SQLException {
        String sql = "INSERT INTO equipment VALUES (?,?,?,?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, e.getEquipmentId());
        ps.setString(2, e.getCategoryId());
        ps.setString(3, e.getBranchId());
        ps.setString(4, e.getBrand());
        ps.setString(5, e.getModel());
        ps.setInt(6, e.getPurchaseYear());
        ps.setBigDecimal(7, e.getDailyBasePrice());
        ps.setBigDecimal(8, e.getSecurityDeposit());
        ps.setString(9, e.getStatus());
        return ps.executeUpdate() > 0;
    }

    public boolean update(Equipment e) throws SQLException {
        String sql = "UPDATE equipment SET category_id=?, branch_id=?, brand=?, model=?, purchase_year=?, daily_base_price=?, security_deposit=?, status=? WHERE equipment_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, e.getCategoryId());
        ps.setString(2, e.getBranchId());
        ps.setString(3, e.getBrand());
        ps.setString(4, e.getModel());
        ps.setInt(5, e.getPurchaseYear());
        ps.setBigDecimal(6, e.getDailyBasePrice());
        ps.setBigDecimal(7, e.getSecurityDeposit());
        ps.setString(8, e.getStatus());
        ps.setString(9, e.getEquipmentId());
        return ps.executeUpdate() > 0;
    }

    public boolean delete(String equipmentId) throws SQLException {
        String sql = "DELETE FROM equipment WHERE equipment_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, equipmentId);
        return ps.executeUpdate() > 0;
    }

    public boolean updateStatus(String equipmentId, String status) throws SQLException {
        String sql = "UPDATE equipment SET status=? WHERE equipment_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, status);
        ps.setString(2, equipmentId);
        return ps.executeUpdate() > 0;
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT equipment_id FROM equipment ORDER BY equipment_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next()) {
            int num = Integer.parseInt(rs.getString("equipment_id").substring(1)) + 1;
            return String.format("E%03d", num);
        }
        return "E001";
    }
}
