package com.coresync.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.coresync.services.UserSession;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EmployeeController implements Initializable {

    @FXML private Label userNameLabel;
    @FXML private Label welcomeLabel;
    
    // US-03 Leave Balance Labels
    @FXML private Label totalLeavesLabel;
    @FXML private Label takenLeavesLabel;
    @FXML private Label remainingLeavesLabel;
    
    // Main View Areas
    @FXML private StackPane mainContentArea;
    @FXML private VBox dashboardVBox;
    @FXML private VBox attendanceVBox; // New Attendance View

    // Sidebar Buttons
    @FXML private HBox dashboardBtn;
    @FXML private HBox attendanceBtn;
    @FXML private HBox leaveBtn;
    @FXML private HBox salaryBtn;

    // Stat Cards
    @FXML private HBox cardTotal;
    @FXML private HBox cardTaken;
    @FXML private HBox cardRemaining;

    // Attendance View Elements
    @FXML private Label attendanceStatusLabel;
    @FXML private Button clockInOutBtn;
    @FXML private Label timeInLabel;
    @FXML private Label timeOutLabel;
    
    private boolean isClockedIn = false; // Tracks clock status

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Retrieve logged-in user details from UserSession
        String empName = UserSession.getFullName(); 
        if(empName == null || empName.isEmpty()) empName = "Employee";
        
        userNameLabel.setText(empName);
        welcomeLabel.setText("Welcome back, " + empName + "!");
        
        // Setup Hover Animations for the Stat Cards
        setupSmoothScale(cardTotal);
        setupSmoothScale(cardTaken);
        setupSmoothScale(cardRemaining);
        
        // Load Data and Set Default View
        loadLeaveData();
        showDashboard(); 
    }

    // --- Hover Animation Logic ---
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

    // --- Sidebar Active State Logic ---
    private void setActiveSidebarButton(HBox activeBtn) {
        HBox[] allBtns = {dashboardBtn, attendanceBtn, leaveBtn, salaryBtn};
        for (HBox btn : allBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-btn-active");
            }
        }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("sidebar-btn-active")) {
            activeBtn.getStyleClass().add("sidebar-btn-active");
        }
    }

    // --- Data Loading (US-03) ---
    private void loadLeaveData() {
        int totalAllowed = 20;
        int leavesTaken = 4;
        int remaining = totalAllowed - leavesTaken;

        totalLeavesLabel.setText(String.valueOf(totalAllowed) + " Days");
        takenLeavesLabel.setText(String.valueOf(leavesTaken) + " Days");
        remainingLeavesLabel.setText(String.valueOf(remaining) + " Days");
    }

    // --- Navigation Actions ---
    @FXML
    protected void showDashboard() {
        setActiveSidebarButton(dashboardBtn);
        if (attendanceVBox != null) attendanceVBox.setVisible(false);
        if (dashboardVBox != null) dashboardVBox.setVisible(true);
    }

    @FXML
    protected void showAttendance() {
        setActiveSidebarButton(attendanceBtn);
        if (dashboardVBox != null) dashboardVBox.setVisible(false);
        if (attendanceVBox != null) attendanceVBox.setVisible(true);
    }

    @FXML
    private void showFeatureNotImplemented() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("This feature will be implemented later");
        alert.showAndWait();
    }

    // --- Attendance Clock Logic ---
    @FXML
    private void handleClockToggle() {
        // Get the current time dynamically
        String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

        if (!isClockedIn) {
            // Clock In Action
            isClockedIn = true;
            attendanceStatusLabel.setText("🟢 On Duty");
            attendanceStatusLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #16A34A;"); // Green
            clockInOutBtn.setText("Clock Out");
            clockInOutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 16; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"); // Red button
            timeInLabel.setText(currentTime);
            timeOutLabel.setText("--:-- --"); // Reset time out
        } else {
            // Clock Out Action
            isClockedIn = false;
            attendanceStatusLabel.setText("🔴 Off Duty");
            attendanceStatusLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #64748B;"); // Grey
            clockInOutBtn.setText("Clock In");
            clockInOutBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 16; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"); // Blue button
            timeOutLabel.setText(currentTime);
        }
    }

    // --- Logout Logic ---
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
            URL resource = getClass().getResource("/com/coresync/login.fxml");
            if (resource == null) {
                System.out.println("Error: Cannot find login.fxml");
                return;
            }
            Parent root = FXMLLoader.load(resource);
            
            Stage stage = null;
            if (dashboardVBox != null && dashboardVBox.getScene() != null) {
                stage = (Stage) dashboardVBox.getScene().getWindow();
            }
            
            if (stage != null) {
                Scene newScene = new Scene(root, 800, 500);
                stage.setScene(newScene);
                stage.centerOnScreen();
    
                FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}