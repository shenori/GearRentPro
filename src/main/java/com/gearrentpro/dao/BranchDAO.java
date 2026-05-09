// =================== BranchDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.Branch;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public List<Branch> findAll() throws SQLException {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branch";
        Connection conn = DBConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(new Branch(rs.getString("branch_id"), rs.getString("branch_name"),
                    rs.getString("address"), rs.getString("contact")));
        }
        return list;
    }

    public Branch findById(String branchId) throws SQLException {
        String sql = "SELECT * FROM branch WHERE branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Branch(rs.getString("branch_id"), rs.getString("branch_name"),
                    rs.getString("address"), rs.getString("contact"));
        }
        return null;
    }

    public boolean save(Branch branch) throws SQLException {
        String sql = "INSERT INTO branch VALUES (?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branch.getBranchId());
        ps.setString(2, branch.getBranchName());
        ps.setString(3, branch.getAddress());
        ps.setString(4, branch.getContact());
        return ps.executeUpdate() > 0;
    }

    public boolean update(Branch branch) throws SQLException {
        String sql = "UPDATE branch SET branch_name=?, address=?, contact=? WHERE branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branch.getBranchName());
        ps.setString(2, branch.getAddress());
        ps.setString(3, branch.getContact());
        ps.setString(4, branch.getBranchId());
        return ps.executeUpdate() > 0;
    }

    public boolean delete(String branchId) throws SQLException {
        String sql = "DELETE FROM branch WHERE branch_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, branchId);
        return ps.executeUpdate() > 0;
    }

    public String generateNextId() throws SQLException {
        String sql = "SELECT branch_id FROM branch ORDER BY branch_id DESC LIMIT 1";
        Connection conn = DBConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            String lastId = rs.getString("branch_id");
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("B%03d", num);
        }
        return "B001";
    }
}







