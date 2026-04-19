package com.coresync.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.coresync.services.ApiConfig;
import com.coresync.services.UserSession;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminController {

    @FXML private VBox dashboardVBox;
    @FXML private VBox addEmployeeForm;
    @FXML private VBox manageEmployeesVBox;
    @FXML private VBox leaveManagementVBox;
    @FXML private VBox settingsVBox;
    @FXML private VBox payrollVBox;

    @FXML private HBox dashboardBtn;
    @FXML private HBox addEmployeeBtn;
    @FXML private HBox manageEmployeesBtn;
    @FXML private HBox leaveManagementBtn;
    @FXML private HBox settingsBtn;
    @FXML private HBox payrollBtn;
    
    @FXML private HBox quickActionAddEmployee;
    @FXML private HBox quickActionLeave;

    @FXML private TextField empIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField salaryField;
    @FXML private PasswordField passField;
    @FXML private Label statusLabel;

    @FXML private ComboBox<String> monthComboBox;
    @FXML private Label payrollStatusLabel;
    @FXML private TableView<PayrollData> payrollTable;
    @FXML private TableColumn<PayrollData, String> payEmpIdCol, payNameCol, payMonthCol, payBaseCol, payDedCol, payNetCol, payDateCol;
    private ObservableList<PayrollData> payrollList = FXCollections.observableArrayList();

    @FXML private TableView<EmployeeData> empTable;
    @FXML private TableColumn<EmployeeData, String> empIdCol;
    @FXML private TableColumn<EmployeeData, String> empNameCol;
    @FXML private TableColumn<EmployeeData, String> empRoleCol;
    @FXML private TableColumn<EmployeeData, String> empSalaryCol;
    private ObservableList<EmployeeData> empList = FXCollections.observableArrayList();

    @FXML private TableView<LeaveData> leaveTable;
    @FXML private TableColumn<LeaveData, String> leaveIdCol;
    @FXML private TableColumn<LeaveData, String> leaveEmpIdCol;
    @FXML private TableColumn<LeaveData, String> leaveStartCol;
    @FXML private TableColumn<LeaveData, String> leaveEndCol;
    @FXML private TableColumn<LeaveData, String> leaveReasonCol;
    @FXML private Label leaveAdminStatusLabel;
    private ObservableList<LeaveData> leaveList = FXCollections.observableArrayList();

    @FXML private PasswordField oldPassField;
    @FXML private PasswordField newPassField;
    @FXML private Label passStatusLabel;

    public static class PayrollData {
        private final SimpleStringProperty empId, name, month, base, ded, net, date;
        public PayrollData(String e, String n, String m, String b, String d, String nt, String dt) {
            this.empId = new SimpleStringProperty(e); this.name = new SimpleStringProperty(n); this.month = new SimpleStringProperty(m);
            this.base = new SimpleStringProperty(b); this.ded = new SimpleStringProperty(d); this.net = new SimpleStringProperty(nt); this.date = new SimpleStringProperty(dt);
        }
        public String getEmpId() { return empId.get(); } public String getName() { return name.get(); } public String getMonth() { return month.get(); }
        public String getBase() { return base.get(); } public String getDed() { return ded.get(); } public String getNet() { return net.get(); } public String getDate() { return date.get(); }
    }

    public static class EmployeeData {
        private final SimpleStringProperty id, name, role, salary;
        public EmployeeData(String id, String name, String role, String salary) {
            this.id = new SimpleStringProperty(id); this.name = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role); this.salary = new SimpleStringProperty(salary);
        }
        public String getId() { return id.get(); } public String getName() { return name.get(); }
        public String getRole() { return role.get(); } public String getSalary() { return salary.get(); }
    }

    public static class LeaveData {
        private final SimpleStringProperty id, empId, start, end, reason;
        public LeaveData(String id, String empId, String start, String end, String reason) {
            this.id = new SimpleStringProperty(id); this.empId = new SimpleStringProperty(empId);
            this.start = new SimpleStringProperty(start); this.end = new SimpleStringProperty(end);
            this.reason = new SimpleStringProperty(reason);
        }
        public String getId() { return id.get(); } public String getEmpId() { return empId.get(); }
        public String getStart() { return start.get(); } public String getEnd() { return end.get(); }
        public String getReason() { return reason.get(); }
    }

    @FXML
    public void initialize() {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("EMPLOYEE", "HR Admin");
            roleComboBox.getSelectionModel().selectFirst();
        }
        
        if (monthComboBox != null) {
            monthComboBox.getItems().addAll("January 2026", "February 2026", "March 2026", "April 2026", "May 2026");
            monthComboBox.getSelectionModel().selectFirst();
            
            payEmpIdCol.setCellValueFactory(new PropertyValueFactory<>("empId")); payNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            payMonthCol.setCellValueFactory(new PropertyValueFactory<>("month")); payBaseCol.setCellValueFactory(new PropertyValueFactory<>("base"));
            payDedCol.setCellValueFactory(new PropertyValueFactory<>("ded")); payNetCol.setCellValueFactory(new PropertyValueFactory<>("net"));
            payDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            payrollTable.setItems(payrollList);
        }
        
        setupSmoothScale(quickActionAddEmployee);
        setupSmoothScale(quickActionLeave);
        
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        empRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        empSalaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        empTable.setItems(empList);

        leaveIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        leaveEmpIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));
        leaveStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        leaveEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        leaveReasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        leaveTable.setItems(leaveList);

        showDashboard();
    }

    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return "0";
            int valStart = keyIndex + searchKey.length();
            while (valStart < json.length() && (json.charAt(valStart) == ' ' || json.charAt(valStart) == '\n')) valStart++;
            if (json.charAt(valStart) == '"') {
                valStart++;
                int valEnd = json.indexOf("\"", valStart);
                return json.substring(valStart, valEnd);
            } else {
                int valEnd = valStart;
                while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}') valEnd++;
                return json.substring(valStart, valEnd).trim();
            }
        } catch (Exception e) { return "0"; }
    }

    private void loadEmployeesData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("admin_api.php?action=get_employees"))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    empList.clear();
                    int start = response.indexOf("["); int end = response.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = response.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String id = extractJsonValue(item, "employee_id");
                            if (!id.equals("0")) empList.add(new EmployeeData(id, extractJsonValue(item, "full_name"), extractJsonValue(item, "role"), extractJsonValue(item, "base_salary"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    private void loadPendingLeaves() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("admin_api.php?action=get_pending_leaves"))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    leaveList.clear();
                    int start = response.indexOf("["); int end = response.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = response.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String id = extractJsonValue(item, "id");
                            if (!id.equals("0")) leaveList.add(new LeaveData(id, extractJsonValue(item, "employee_id"), extractJsonValue(item, "start_date"), extractJsonValue(item, "end_date"), extractJsonValue(item, "reason"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    private void loadPayrollData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("payroll_api.php?action=get_all"))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(res -> Platform.runLater(() -> {
                if(res.contains("\"status\":\"success\"")) {
                    payrollList.clear();
                    int start = res.indexOf("["); int end = res.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = res.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String id = extractJsonValue(item, "employee_id");
                            if (!id.equals("0")) payrollList.add(new PayrollData(id, extractJsonValue(item, "full_name"), extractJsonValue(item, "salary_month"), extractJsonValue(item, "base_salary"), extractJsonValue(item, "deductions"), extractJsonValue(item, "net_salary"), extractJsonValue(item, "payment_date"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    @FXML protected void handleApproveLeave() { updateLeaveStatus("Approved"); }
    @FXML protected void handleRejectLeave() { updateLeaveStatus("Rejected"); }

    private void updateLeaveStatus(String status) {
        LeaveData selected = leaveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            leaveAdminStatusLabel.setText("Select a request."); leaveAdminStatusLabel.setStyle("-fx-text-fill: red;"); return;
        }
        try {
            String jsonPayload = String.format("{\"action\":\"update_leave\", \"leave_id\":\"%s\", \"status\":\"%s\"}", selected.getId(), status);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("admin_api.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> Platform.runLater(() -> {
                leaveAdminStatusLabel.setText("Request " + status + "!"); leaveAdminStatusLabel.setStyle("-fx-text-fill: green;");
                loadPendingLeaves();
            }));
        } catch (Exception e) {}
    }

    @FXML protected void handleRegisterEmployee(ActionEvent event) {
        String id = empIdField.getText(), name = nameField.getText(), role = roleComboBox.getValue(), salary = salaryField.getText(), pass = passField.getText();
        if (id.isEmpty() || name.isEmpty() || salary.isEmpty() || pass.isEmpty()) { statusLabel.setText("All fields required."); return; }
        try {
            String jsonInput = String.format("{\"employee_id\":\"%s\", \"full_name\":\"%s\", \"role\":\"%s\", \"base_salary\":\"%s\", \"password\":\"%s\"}", id, name, role, salary, pass);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("register.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonInput)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(responseBody -> Platform.runLater(() -> {
                if (responseBody.contains("\"status\":\"success\"")) {
                    statusLabel.setStyle("-fx-text-fill: green;"); statusLabel.setText("Employee registered!");
                    empIdField.clear(); nameField.clear(); salaryField.clear(); passField.clear();
                } else { statusLabel.setStyle("-fx-text-fill: red;"); statusLabel.setText("Failed to register."); }
            }));
        } catch (Exception e) {}
    }

    @FXML protected void handleRunPayroll() {
        String month = monthComboBox.getValue();
        try {
            String json = String.format("{\"action\":\"generate\", \"month\":\"%s\"}", month);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("payroll_api.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
            client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(res -> Platform.runLater(() -> {
                if(res.contains("\"status\":\"success\"")) {
                    payrollStatusLabel.setStyle("-fx-text-fill: green;"); payrollStatusLabel.setText("Payroll Generated for " + month + "!");
                    loadPayrollData();
                } else {
                    String msg = extractJsonValue(res, "message");
                    payrollStatusLabel.setStyle("-fx-text-fill: red;"); payrollStatusLabel.setText(msg.equals("0") ? "Failed." : msg);
                }
            }));
        } catch (Exception e) {}
    }

    @FXML protected void handleChangePassword() {
        String oldP = oldPassField.getText(), newP = newPassField.getText(), empId = UserSession.getLoggedInEmployeeId();
        if(oldP.isEmpty() || newP.isEmpty()) { passStatusLabel.setText("Fill all fields."); return; }
        try {
            String jsonPayload = String.format("{\"employee_id\":\"%s\", \"old_password\":\"%s\", \"new_password\":\"%s\"}", empId, oldP, newP);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("change_password.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(res -> Platform.runLater(() -> {
                if(res.contains("\"status\":\"success\"")) {
                    passStatusLabel.setStyle("-fx-text-fill: green;"); passStatusLabel.setText("Password Updated!");
                    oldPassField.clear(); newPassField.clear();
                } else { passStatusLabel.setStyle("-fx-text-fill: red;"); passStatusLabel.setText("Incorrect old password."); }
            }));
        } catch (Exception e) {}
    }

    private void hideAllViews() {
        if (dashboardVBox != null) dashboardVBox.setVisible(false);
        if (addEmployeeForm != null) addEmployeeForm.setVisible(false);
        if (manageEmployeesVBox != null) manageEmployeesVBox.setVisible(false);
        if (leaveManagementVBox != null) leaveManagementVBox.setVisible(false);
        if (settingsVBox != null) settingsVBox.setVisible(false);
        if (payrollVBox != null) payrollVBox.setVisible(false);
    }
    
    private void setActiveSidebarButton(HBox activeBtn) {
        HBox[] allBtns = {dashboardBtn, addEmployeeBtn, manageEmployeesBtn, leaveManagementBtn, payrollBtn, settingsBtn};
        for (HBox btn : allBtns) { if (btn != null) btn.getStyleClass().remove("sidebar-btn-active"); }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("sidebar-btn-active")) { activeBtn.getStyleClass().add("sidebar-btn-active"); }
    }
    
    private void setupSmoothScale(javafx.scene.Node node) {
        if (node == null) return;
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node); scaleIn.setToX(1.03); scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node); scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        node.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.playFromStart(); }); node.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.playFromStart(); });
    }

    @FXML protected void showDashboard() { setActiveSidebarButton(dashboardBtn); hideAllViews(); dashboardVBox.setVisible(true); }
    @FXML protected void showAddEmployeeForm() { setActiveSidebarButton(addEmployeeBtn); hideAllViews(); addEmployeeForm.setVisible(true); }
    @FXML protected void showManageEmployees() { setActiveSidebarButton(manageEmployeesBtn); hideAllViews(); manageEmployeesVBox.setVisible(true); loadEmployeesData(); }
    @FXML protected void showLeaveManagement() { setActiveSidebarButton(leaveManagementBtn); hideAllViews(); leaveManagementVBox.setVisible(true); loadPendingLeaves(); }
    @FXML protected void showSettings() { setActiveSidebarButton(settingsBtn); hideAllViews(); settingsVBox.setVisible(true); }
    @FXML protected void showPayroll() { setActiveSidebarButton(payrollBtn); hideAllViews(); if (payrollVBox != null) { payrollVBox.setVisible(true); loadPayrollData(); } }

    @FXML protected void handleLogout(ActionEvent event) { performLogout(); }
    @FXML protected void handleLogoutAsMouseEvent(MouseEvent event) { performLogout(); }
    private void performLogout() {
        UserSession.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/coresync/login.fxml"));
            Stage stage = (dashboardVBox != null && dashboardVBox.getScene() != null) ? (Stage) dashboardVBox.getScene().getWindow() : null;
            if (stage != null) {
                stage.setScene(new Scene(root, 800, 500)); stage.centerOnScreen();
                FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
                fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();
            }
        } catch (Exception e) {}
    }
}