package com.coresync.controllers;

import com.coresync.services.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AdminController {

    @FXML private VBox dashboardVBox;
    @FXML private VBox addEmployeeForm;
    @FXML private TextField empIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField salaryField;
    @FXML private PasswordField passField;
    @FXML private Label statusLabel;
    
    @FXML private HBox dashboardBtn;
    @FXML private HBox addEmployeeBtn;
    @FXML private HBox manageEmployeesBtn;
    @FXML private HBox leaveManagementBtn;
    @FXML private HBox payrollManagementBtn;
    
    @FXML private HBox quickActionAddEmployee;
    @FXML private HBox quickActionLeave;
    @FXML private HBox quickActionPayroll;

    @FXML
    public void initialize() {
        // Populate the dropdown menu
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("EMPLOYEE", "HR Admin");
            roleComboBox.getSelectionModel().selectFirst();
        }
        
        setupSmoothScale(quickActionAddEmployee);
        setupSmoothScale(quickActionLeave);
        setupSmoothScale(quickActionPayroll);
        
        setActiveSidebarButton(dashboardBtn);
    }
    
    private void setupSmoothScale(javafx.scene.Node node) {
        if (node == null) return;
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
        node.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });
        node.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    private void setActiveSidebarButton(HBox activeBtn) {
        HBox[] allBtns = {dashboardBtn, addEmployeeBtn, manageEmployeesBtn, leaveManagementBtn, payrollManagementBtn};
        for (HBox btn : allBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-btn-active");
            }
        }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("sidebar-btn-active")) {
            activeBtn.getStyleClass().add("sidebar-btn-active");
        }
    }

    @FXML
    protected void showAddEmployeeForm() {
        setActiveSidebarButton(addEmployeeBtn);
        if (dashboardVBox != null) dashboardVBox.setVisible(false);
        if (addEmployeeForm != null) addEmployeeForm.setVisible(true);
    }

    @FXML
    protected void showDashboard() {
        setActiveSidebarButton(dashboardBtn);
        if (addEmployeeForm != null) addEmployeeForm.setVisible(false);
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
        performLogout();
    }
    
    @FXML
    protected void handleLogoutAsMouseEvent(MouseEvent event) {
        performLogout();
    }
    
    private void performLogout() {
        UserSession.clear();
        try {
            // Check if login.fxml exists and load it
            java.net.URL resource = getClass().getResource("/com/coresync/login.fxml");
            if (resource == null) return;
            Parent root = FXMLLoader.load(resource);
            
            // Get current stage from any node
            Stage stage = null;
            if (dashboardVBox != null && dashboardVBox.getScene() != null) {
                stage = (Stage) dashboardVBox.getScene().getWindow();
            }
            
            if (stage != null) {
                Scene newScene = new Scene(root, 800, 500);
                stage.setScene(newScene);
                stage.centerOnScreen();
    
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(800), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
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