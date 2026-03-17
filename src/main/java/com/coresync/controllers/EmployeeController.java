package com.coresync.controllers;

import com.coresync.services.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmployeeController {
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label roleLabel;
    @FXML private Label salaryLabel;
    @FXML private Label totalLeavesLabel;
    @FXML private Label leavesTakenLabel;
    @FXML private Label remainingLeavesLabel;
    @FXML private ProgressBar leaveProgressBar; 

    @FXML
    public void initialize() {
        String empId = UserSession.getLoggedInEmployeeId();
        if (empId != null) {
            fetchEmployeeData(empId);
        }
    }

    private void fetchEmployeeData(String empId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/payroll_api/get_employee.php?id=" + empId))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                  .thenApply(HttpResponse::body)
                  .thenAccept(response -> Platform.runLater(() -> parseAndDisplay(response)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseAndDisplay(String json) {
        if(json.contains("\"status\":\"success\"")) {
            nameLabel.setText("Name: " + extractValue(json, "full_name"));
            idLabel.setText("ID: " + extractValue(json, "employee_id"));
            roleLabel.setText("Role: " + extractValue(json, "role"));
            salaryLabel.setText("Base Salary: PKR " + extractValue(json, "base_salary"));
            
            double total = Double.parseDouble(extractValue(json, "total_leaves"));
            double taken = Double.parseDouble(extractValue(json, "leaves_taken"));
            double remaining = total - taken;
            
            totalLeavesLabel.setText("Total Allowed: " + (int)total);
            leavesTakenLabel.setText("Leaves Taken: " + (int)taken);
            remainingLeavesLabel.setText("Remaining: " + (int)remaining + " days");

            double progressRatio = (total > 0) ? (remaining / total) : 0;
            
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(leaveProgressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(leaveProgressBar.progressProperty(), progressRatio))
            );
            timeline.play();
        }
    }

    private String extractValue(String json, String key) {
        try {
            if (json.contains("\"" + key + "\":\"")) {
                return json.split("\"" + key + "\":\"")[1].split("\"")[0];
            } 
            else if (json.contains("\"" + key + "\":")) {
                String rawValue = json.split("\"" + key + "\":")[1].split("[,}]")[0].trim();
                
                if (rawValue.equals("null")) {
                    return "0";
                }
                return rawValue;
            }
        } catch (Exception e) {
            System.out.println("Error parsing key: " + key);
        }
        return "0"; 
    }

    @FXML
    protected void handleLogout(ActionEvent event) {
        UserSession.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/coresync/login.fxml"));
            Scene scene = new Scene(root, 800, 500);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}