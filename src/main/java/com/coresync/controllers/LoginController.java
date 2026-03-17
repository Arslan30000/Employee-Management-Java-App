package com.coresync.controllers;

import com.coresync.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
                
                if (responseBody.contains("\"role\":\"HR Admin\"")) {
                    messageLabel.setText("Login Success! Routing to Admin...");
                    // TODO: Load Admin Dashboard
                } else {
                    messageLabel.setText("Login Success! Routing to Employee...");
                    // TODO: Load Employee Dashboard
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Invalid Credentials");
                passField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Server Error. Is XAMPP running?");
        }
    }
}