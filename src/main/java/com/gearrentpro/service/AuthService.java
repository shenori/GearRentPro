// =================== AuthService.java ===================
package main.java.com.gearrentpro.service;

import main.java.com.gearrentpro.dao.UserDAO;
import main.java.com.gearrentpro.entity.User;
import main.java.com.gearrentpro.util.SessionManager;
import java.sql.SQLException;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            SessionManager.getInstance().setLoggedInUser(user);
            return user;
        }
        return null;
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}
