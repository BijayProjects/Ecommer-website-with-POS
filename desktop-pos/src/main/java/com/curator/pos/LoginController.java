package com.curator.pos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT password, is_staff, is_superuser FROM auth_user WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                boolean isSuperuser = rs.getInt("is_superuser") == 1;
                
                if (!isSuperuser) {
                    statusLabel.setText("Access Denied: Only Admins can log in.");
                    return;
                }

                if (verifyDjangoPassword(password, storedHash)) {
                    DatabaseManager.initDatabase();
                    SessionManager.setSession(username, isSuperuser);
                    DatabaseManager.logUserAction(username, "LOGIN");
                    switchToDashboard();
                } else {
                    statusLabel.setText("Invalid username or password.");
                }
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Auth Error: " + e.getMessage());
        }
    }

    private boolean verifyDjangoPassword(String password, String djangoHash) {
        try {
            // Django format: pbkdf2_sha256$iterations$salt$hash
            String[] parts = djangoHash.split("\\$");
            if (parts.length != 4) return false;

            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String expectedHashBase64 = parts[3];

            // PBKDF2 with HMAC-SHA256
            // Key length is 256 bits (32 bytes)
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            
            String actualHashBase64 = Base64.getEncoder().encodeToString(hash);
            return expectedHashBase64.equals(actualHashBase64);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void switchToDashboard() throws Exception {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        stage.setScene(new Scene(root, 1200, 800));
        stage.centerOnScreen();
    }
}
