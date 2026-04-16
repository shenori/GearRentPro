package com.gearrentpro.dao;

import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Customer.MembershipLevel;
import com.gearrentpro.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public String generateNextId() throws SQLException {
        String sql = "SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("customer_id");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("C%03d", num);
            }
            return "C001";
        }
    }

    public boolean save(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, name, nic_passport, contact_no, " +
                     "email, address, membership_level, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getNicPassport());
            pstmt.setString(4, customer.getContactNo());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getAddress());
            pstmt.setString(7, customer.getMembershipLevel().name());
            pstmt.setBoolean(8, customer.isActive());

            return pstmt.executeUpdate() > 0;
        }
    }

    public Customer findById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public Customer findByNIC(String nicPassport) throws SQLException {
        String sql = "SELECT * FROM customers WHERE nic_passport = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nicPassport);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE is_active = true ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public List<Customer> findAllIncludingInactive() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public List<Customer> search(String keyword, MembershipLevel level) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM customers WHERE is_active = true "
        );
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (LOWER(name) LIKE ? OR LOWER(nic_passport) LIKE ? OR contact_no LIKE ? OR customer_id LIKE ?) ");
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
        }
        if (level != null) {
            sql.append("AND membership_level = ? ");
            params.add(level.name());
        }
        
        sql.append("ORDER BY name");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, nic_passport = ?, contact_no = ?, " +
                     "email = ?, address = ?, membership_level = ?, is_active = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getNicPassport());
            pstmt.setString(3, customer.getContactNo());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getMembershipLevel().name());
            pstmt.setBoolean(7, customer.isActive());
            pstmt.setString(8, customer.getCustomerId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deactivate(String customerId) throws SQLException {
        String sql = "UPDATE customers SET is_active = false WHERE customer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean existsByNIC(String nicPassport, String excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE nic_passport = ? AND customer_id != ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nicPassport);
            pstmt.setString(2, excludeId != null ? excludeId : "");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Get total active deposit for a customer
    public BigDecimal getTotalActiveDeposit(String customerId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(security_deposit), 0) as total_deposit " +
                     "FROM rentals WHERE customer_id = ? AND rental_status IN ('ACTIVE', 'OVERDUE')";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_deposit");
            }
        }
        return BigDecimal.ZERO;
    }

    // Check if customer has active rentals
    public boolean hasActiveRentals(String customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rentals WHERE customer_id = ? AND rental_status IN ('ACTIVE', 'OVERDUE')";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getString("customer_id"));
        customer.setName(rs.getString("name"));
        customer.setNicPassport(rs.getString("nic_passport"));
        customer.setContactNo(rs.getString("contact_no"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        customer.setMembershipLevel(MembershipLevel.valueOf(rs.getString("membership_level")));
        customer.setActive(rs.getBoolean("is_active"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return customer;
    }
}