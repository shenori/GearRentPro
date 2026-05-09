// =================== RentalDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Rental;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    private Rental mapRow(ResultSet rs) throws SQLException {
        Rental r = new Rental();
        r.setRentalId(rs.getString("rental_id"));
        r.setEquipmentId(rs.getString("equipment_id"));
        r.setCustomerId(rs.getString("customer_id"));
        r.setBranchId(rs.getString("branch_id"));
        r.setReservationId(rs.getString("reservation_id"));
        r.setStartDate(rs.getDate("start_date").toLocalDate());
        r.setEndDate(rs.getDate("end_date").toLocalDate());
        if (rs.getDate("actual_return_date") != null)
            r.setActualReturnDate(rs.getDate("actual_return_date").toLocalDate());
        r.setRentalAmount(rs.getBigDecimal("rental_amount"));
        r.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        r.setMembershipDiscount(rs.getBigDecimal("membership_discount"));
        r.setLongRentalDiscount(rs.getBigDecimal("long_rental_discount"));
        r.setLateFee(rs.getBigDecimal("late_fee"));
        r.setDamageCharge(rs.getBigDecimal("damage_charge"));
        r.setDamageDescription(rs.getString("damage_description"));
        r.setFinalAmount(rs.getBigDecimal("final_amount"));
        r.setPaymentStatus(rs.getString("payment_status"));
        r.setRentalStatus(rs.getString("rental_status"));
        try { r.setCustomerName(rs.getString("customer_name")); } catch (SQLException e) {}
        try { r.setEquipmentName(rs.getString("equipment_name")); } catch (SQLException e) {}
        try { r.setBranchName(rs.getString("branch_name")); } catch (SQLException e) {}
        return r;
    }

    public List<Rental> findAll() throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM rental r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Rental> findByBranch(String branchId) throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM rental r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Rental> findByStatus(String status) throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM rental r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.rental_status=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public List<Rental> findOverdue() throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM rental r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.rental_status='Overdue' OR (r.rental_status='Active' AND r.end_date < CURDATE())";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Rental findById(String rentalId) throws SQLException {
        String sql = "SELECT r.*, c.full_name as customer_name, CONCAT(e.brand,' ',e.model) as equipment_name, b.branch_name FROM rental r JOIN customer c ON r.customer_id=c.customer_id JOIN equipment e ON r.equipment_id=e.equipment_id JOIN branch b ON r.branch_id=b.branch_id WHERE r.rental_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, rentalId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public boolean save(Rental r) throws SQLException {
        String sql = "INSERT INTO rental (rental_id,equipment_id,customer_id,branch_id,reservation_id,start_date,end_date,rental_amount,security_deposit,membership_discount,long_rental_discount,late_fee,damage_charge,final_amount,payment_status,rental_status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, r.getRentalId());
        ps.setString(2, r.getEquipmentId());
        ps.setString(3, r.getCustomerId());
        ps.setString(4, r.getBranchId());
        ps.setString(5, r.getReservationId());
        ps.setDate(6, Date.valueOf(r.getStartDate()));
        ps.setDate(7, Date.valueOf(r.getEndDate()));
        ps.setBigDecimal(8, r.getRentalAmount());
        ps.setBigDecimal(9, r.getSecurityDeposit());
        ps.setBigDecimal(10, r.getMembershipDiscount());
        ps.setBigDecimal(11, r.getLongRentalDiscount());
        ps.setBigDecimal(12, r.getLateFee());
        ps.setBigDecimal(13, r.getDamageCharge());
        ps.setBigDecimal(14, r.getFinalAmount());
        ps.setString(15, r.getPaymentStatus());
        ps.setString(16, r.getRentalStatus());
        return ps.executeUpdate() > 0;
    }

    public boolean updateReturn(Rental r) throws SQLException {
        String sql = "UPDATE rental SET actual_return_date=?, late_fee=?, damage_charge=?, damage_description=?, rental_status=?, payment_status=? WHERE rental_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, r.getActualReturnDate() != null ? Date.valueOf(r.getActualReturnDate()) : null);
        ps.setBigDecimal(2, r.getLateFee());
        ps.setBigDecimal(3, r.getDamageCharge());
        ps.setString(4, r.getDamageDescription());
        ps.setString(5, "Returned");
        ps.setString(6, r.getPaymentStatus());
        ps.setString(7, r.getRentalId());
        return ps.executeUpdate() > 0;
    }

    public void markOverdue(LocalDate today) throws SQLException {
        String sql = "UPDATE rental SET rental_status='Overdue' WHERE rental_status='Active' AND end_date < ?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(today));
        ps.executeUpdate();
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT rental_id FROM rental ORDER BY rental_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next()) {
            int num = Integer.parseInt(rs.getString("rental_id").substring(1)) + 1;
            return String.format("R%03d", num);
        }
        return "R001";
    }
}


