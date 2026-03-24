package com.coresync.controllers;

import java.net.URL;

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
            // Call the PHP API via AuthService
            String responseBody = AuthService.authenticate(empId, password);

            if (responseBody.contains("\"status\":\"success\"")) {
                messageLabel.setStyle("-fx-text-fill: green;");
                
                // 1. Save Logged In ID to Session
                UserSession.setLoggedInEmployeeId(empId);
                
                // 2. Extract the employee's name from the JSON response and save it
                String empName = "Employee";
                if (responseBody.contains("\"name\"")) {
                    // Splits the JSON string to grab just the name value
                    empName = responseBody.split("\"name\":\"")[1].split("\"")[0];
                }
                UserSession.setFullName(empName);
                
                // 3. Route to the correct dashboard based on role
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

    // Helper method for smooth fade-in transitions and error diagnostics
    private void loadSceneWithTransition(String fxmlPath, ActionEvent event) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            
            // Safety Check: Did Maven actually build the FXML file?
            if (resource == null) {
                messageLabel.setText("Missing File: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Set up the new scene dynamically to fit the FXML layout
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();

            // Animate it fading in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            // Print the error to the terminal AND the UI so you know exactly what crashed
            e.printStackTrace(); 
            if (e.getCause() != null) {
                messageLabel.setText("UI Crash: " + e.getCause().toString());
            } else {
                messageLabel.setText("UI Crash: " + e.getMessage());
            }
        }
    }
}