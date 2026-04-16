package main.java.com.gearrentpro.controller;

import com.gearrentpro.Main;
import com.gearrentpro.entity.User;
import com.gearrentpro.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    
    public LoginController() {
        this.authService = new AuthService();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        errorLabel.setText("");
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }
        
        try {
            User user = authService.login(username, password);
            
            if (user != null) {
                // Navigate to dashboard
                Main.switchScene("dashboard.fxml", "Dashboard");
            } else {
                errorLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}