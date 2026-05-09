// =================== ReservationDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Reservation;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getString("reservation_id"));
        r.setEquipmentId(rs.getString("equipment_id"));
        r.setCustomerId(rs.getString("customer_id"));
        r.setBranchId(rs.getString("branch_id"));
        r.setStartDate(rs.getDate("start_date").toLocalDate());
        r.setEndDate(rs.getDate("end_date").toLocalDate());
        r.setStatus(rs.getString("status"));
        try { r.setCustomerName(rs.getString("customer_name")); } catch (SQLException e) {}
        try { r.setEquipmentName(rs.getString("equipment_name")); } catch (SQLException e) {}
        try { r.setBranchName(rs.getString("branch_name")); } catch (SQLException e) {}
        return r;
    }

    public List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM reservation r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Reservation> findByBranch(String branchId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM reservation r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Reservation findById(String reservationId) throws SQLException {
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM reservation r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.reservation_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, reservationId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public boolean save(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservation VALUES (?,?,?,?,?,?,'Active',NOW())";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, r.getReservationId());
        ps.setString(2, r.getEquipmentId());
        ps.setString(3, r.getCustomerId());
        ps.setString(4, r.getBranchId());
        ps.setDate(5, Date.valueOf(r.getStartDate()));
        ps.setDate(6, Date.valueOf(r.getEndDate()));
        return ps.executeUpdate() > 0;
    }

    public boolean cancel(String reservationId) throws SQLException {
        String sql = "UPDATE reservation SET status='Cancelled' WHERE reservation_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, reservationId);
        return ps.executeUpdate() > 0;
    }

    public boolean hasOverlap(String equipmentId, LocalDate start, LocalDate end, String excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservation WHERE equipment_id=? AND status='Active' AND NOT (end_date < ? OR start_date > ?) AND reservation_id != IFNULL(?,reservation_id)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, equipmentId);
        ps.setDate(2, Date.valueOf(start));
        ps.setDate(3, Date.valueOf(end));
        ps.setString(4, excludeId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
        return false;
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT reservation_id FROM reservation ORDER BY reservation_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next()) {
            int num = Integer.parseInt(rs.getString("reservation_id").substring(2)) + 1;
            return String.format("RS%03d", num);
        }
        return "RS001";
    }
}