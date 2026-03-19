package com.coresync.controllers;

import com.coresync.services.UserSession;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AdminController {

    @FXML private GridPane dashboardGrid;
    @FXML private VBox dashboardVBox;
    @FXML private VBox addEmployeeForm;
    @FXML private TextField empIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField salaryField;
    @FXML private PasswordField passField;
    @FXML private Label statusLabel;
    
    @FXML private VBox addEmployeeCard;
    @FXML private VBox manageEmployeesCard;
    @FXML private VBox leaveManagementCard;
    @FXML private VBox payrollManagementCard;

    @FXML
    public void initialize() {
        // Populate the dropdown menu
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("EMPLOYEE", "HR Admin");
            roleComboBox.getSelectionModel().selectFirst();
        }
        
        setupCardHoverEffects(addEmployeeCard);
        setupCardHoverEffects(manageEmployeesCard);
        setupCardHoverEffects(leaveManagementCard);
        setupCardHoverEffects(payrollManagementCard);
    }
    
    private void setupCardHoverEffects(VBox card) {
        if (card != null) {
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #fcfcfc; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5); -fx-cursor: hand; -fx-border-color: #f77f00 transparent transparent transparent; -fx-border-width: 4 0 0 0;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 15, 0, 0, 5); -fx-cursor: hand; -fx-border-color: #f77f00 transparent transparent transparent; -fx-border-width: 4 0 0 0;"));
        }
    }

    @FXML
    protected void showAddEmployeeForm() {
        if (dashboardVBox != null) dashboardVBox.setVisible(false);
        if (dashboardGrid != null) dashboardGrid.setVisible(false);
        if (addEmployeeForm != null) addEmployeeForm.setVisible(true);
    }

    @FXML
    protected void showDashboard() {
        if (addEmployeeForm != null) addEmployeeForm.setVisible(false);
        if (dashboardGrid != null) dashboardGrid.setVisible(true);
        if (dashboardVBox != null) dashboardVBox.setVisible(true);
    }

    @FXML
    protected void showFeatureNotImplemented() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("This feature will be implemented later");
        alert.showAndWait();
    }

    @FXML
    protected void handleRegisterEmployee(ActionEvent event) {
        String id = empIdField.getText();
        String name = nameField.getText();
        String role = roleComboBox.getValue();
        String salary = salaryField.getText();
        String pass = passField.getText();

        // Validation
        if (id.isEmpty() || name.isEmpty() || salary.isEmpty() || pass.isEmpty()) {
            showError("All fields must be filled out.");
            return;
        }

        // Network Request to the PHP Backend
        try {
            String jsonInput = String.format(
                "{\"employee_id\":\"%s\", \"full_name\":\"%s\", \"role\":\"%s\", \"base_salary\":\"%s\", \"password\":\"%s\"}",
                id, name, role, salary, pass
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/payroll_api/register.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody.contains("\"status\":\"success\"")) {
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                statusLabel.setText("Employee successfully registered!");
                clearForm();
            } else {
                // Extracts the error message from the JSON
                String errorMsg = "Failed to register.";
                if (responseBody.contains("\"message\"")) {
                    errorMsg = responseBody.split("\"message\":\"")[1].split("\"")[0];
                }
                showError(errorMsg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Server error. Check XAMPP.");
        }
    }

    @FXML
    protected void handleLogout(ActionEvent event) {
        UserSession.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/coresync/login.fxml"));
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Set up the new scene
            Scene newScene = new Scene(root, 800, 500);
            stage.setScene(newScene);
            stage.centerOnScreen();

            // Animate fading back to login
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #d62828; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    private void clearForm() {
        empIdField.clear();
        nameField.clear();
        salaryField.clear();
        passField.clear();
        roleComboBox.getSelectionModel().selectFirst();
    }
}