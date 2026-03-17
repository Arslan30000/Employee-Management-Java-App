package com.coresync.controllers;

import com.coresync.services.AuthService;
import com.coresync.services.UserSession;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField idField;
    @FXML private PasswordField passField;
    @FXML private Label messageLabel;

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String empId = idField.getText();
        String password = passField.getText();

        if (empId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Fields cannot be blank.");
            return;
        }

        try {
            String responseBody = AuthService.authenticate(empId, password);

            if (responseBody.contains("\"status\":\"success\"")) {
                messageLabel.setStyle("-fx-text-fill: green;");
                UserSession.setLoggedInEmployeeId(empId);
                
                if (responseBody.contains("\"role\":\"HR Admin\"")) {
                    messageLabel.setText("Authenticating Admin...");
                    loadSceneWithTransition("/com/coresync/admin_dashboard.fxml", event);
                } else {
                    messageLabel.setText("Authenticating Employee...");
                    loadSceneWithTransition("/com/coresync/employee_dashboard.fxml", event);
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: #d62828;");
                messageLabel.setText("Invalid Credentials");
                passField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Connection Error. Check XAMPP.");
        }
    }

    // Helper method for smooth fade-in transitions
    private void loadSceneWithTransition(String fxmlPath, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Set up the new scene
            Scene newScene = new Scene(root, 800, 500);
            stage.setScene(newScene);
            stage.centerOnScreen();

            // Animate it fading in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard UI.");
        }
    }
}