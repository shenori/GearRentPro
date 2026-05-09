// =================== CustomerDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Customer;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getString("customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setNic(rs.getString("nic"));
        c.setContact(rs.getString("contact"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setMembershipLevel(rs.getString("membership_level"));
        return c;
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM customer");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Customer findById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, customerId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public boolean save(Customer c) throws SQLException {
        String sql = "INSERT INTO customer VALUES (?,?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getCustomerId());
        ps.setString(2, c.getFullName());
        ps.setString(3, c.getNic());
        ps.setString(4, c.getContact());
        ps.setString(5, c.getEmail());
        ps.setString(6, c.getAddress());
        ps.setString(7, c.getMembershipLevel());
        return ps.executeUpdate() > 0;
    }

    public boolean update(Customer c) throws SQLException {
        String sql = "UPDATE customer SET full_name=?, nic=?, contact=?, email=?, address=?, membership_level=? WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getFullName());
        ps.setString(2, c.getNic());
        ps.setString(3, c.getContact());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getAddress());
        ps.setString(6, c.getMembershipLevel());
        ps.setString(7, c.getCustomerId());
        return ps.executeUpdate() > 0;
    }

    public boolean delete(String customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, customerId);
        return ps.executeUpdate() > 0;
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next()) {
            int num = Integer.parseInt(rs.getString("customer_id").substring(2)) + 1;
            return String.format("CU%03d", num);
        }
        return "CU001";
    }
}