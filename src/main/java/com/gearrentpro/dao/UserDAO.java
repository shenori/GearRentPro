// =================== UserDAO.java ===================
package main.java.com.gearrentpro.dao;

import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, b.branch_name FROM user u LEFT JOIN branch b ON u.branch_id = b.branch_id WHERE u.username = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setRole(rs.getString("role"));
            user.setBranchId(rs.getString("branch_id"));
            user.setBranchName(rs.getString("branch_name"));
            return user;
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, b.branch_name FROM user u LEFT JOIN branch b ON u.branch_id = b.branch_id";
        Connection conn = DBConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setRole(rs.getString("role"));
            user.setBranchId(rs.getString("branch_id"));
            user.setBranchName(rs.getString("branch_name"));
            list.add(user);
        }
        return list;
    }

    public boolean save(User user) throws SQLException {
        String sql = "INSERT INTO user VALUES (?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getUserId());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getFullName());
        ps.setString(5, user.getRole());
        ps.setString(6, user.getBranchId());
        return ps.executeUpdate() > 0;
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE user SET username=?, password=?, full_name=?, role=?, branch_id=? WHERE user_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getBranchId());
        ps.setString(6, user.getUserId());
        return ps.executeUpdate() > 0;
    }
}