package com.coresync.controllers;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import com.coresync.services.ApiConfig;
import com.coresync.services.UserSession;
import javafx.scene.layout.Region;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.print.PrinterJob;

public class EmployeeController implements Initializable {

    @FXML private Label userNameLabel;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchBar; 
    
    @FXML private StackPane mainContentArea;
    @FXML private VBox dashboardVBox;
    @FXML private VBox attendanceVBox;
    @FXML private VBox leaveRequestVBox;
    @FXML private VBox holidaysVBox;
    @FXML private VBox settingsVBox;
    @FXML private VBox salaryVBox;

    @FXML private HBox dashboardBtn;
    @FXML private HBox attendanceBtn;
    @FXML private HBox leaveBtn;
    @FXML private HBox holidaysBtn;
    @FXML private HBox settingsBtn;
    @FXML private HBox salaryBtn;

    @FXML private HBox cardTotal;
    @FXML private HBox cardTaken;
    @FXML private HBox cardRemaining;
    @FXML private Label totalLeavesLabel;
    @FXML private Label takenLeavesLabel;
    @FXML private Label remainingLeavesLabel;

    @FXML private Label attendanceStatusLabel;
    @FXML private Button clockInOutBtn;
    @FXML private Label timeInLabel;
    @FXML private Label timeOutLabel;
    private boolean isClockedIn = false;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea leaveReasonArea;
    @FXML private Label leaveStatusLabel;

    @FXML private TableView<LeaveHistory> leaveHistoryTable;
    @FXML private TableColumn<LeaveHistory, String> historyStartCol;
    @FXML private TableColumn<LeaveHistory, String> historyEndCol;
    @FXML private TableColumn<LeaveHistory, String> historyReasonCol;
    @FXML private TableColumn<LeaveHistory, String> historyStatusCol;
    private ObservableList<LeaveHistory> historyList = FXCollections.observableArrayList();

    @FXML private TableView<Holiday> holidaysTable;
    @FXML private TableColumn<Holiday, String> holidayNameCol;
    @FXML private TableColumn<Holiday, String> holidayDateCol;
    @FXML private TableColumn<Holiday, String> holidayTypeCol;
    private ObservableList<Holiday> masterHolidayList = FXCollections.observableArrayList();
    private FilteredList<Holiday> filteredHolidays;

    @FXML private TableView<PayslipData> salaryTable;
    @FXML private TableColumn<PayslipData, String> salMonthCol, salBaseCol, salDedCol, salNetCol, salDateCol;
    @FXML private Label salaryStatusLabel;
    private ObservableList<PayslipData> payslipList = FXCollections.observableArrayList();

    @FXML private PasswordField oldPassField;
    @FXML private PasswordField newPassField;
    @FXML private Label passStatusLabel;

    public static class PayslipData {
        private final SimpleStringProperty month, base, ded, net, date;
        public PayslipData(String m, String b, String d, String nt, String dt) {
            this.month = new SimpleStringProperty(m); this.base = new SimpleStringProperty(b); this.ded = new SimpleStringProperty(d);
            this.net = new SimpleStringProperty(nt); this.date = new SimpleStringProperty(dt);
        }
        public String getMonth() { return month.get(); } public String getBase() { return base.get(); }
        public String getDed() { return ded.get(); } public String getNet() { return net.get(); } public String getDate() { return date.get(); }
    }

    public static class Holiday {
        private final SimpleStringProperty name, date, type;
        public Holiday(String name, String date, String type) {
            this.name = new SimpleStringProperty(name); this.date = new SimpleStringProperty(date); this.type = new SimpleStringProperty(type);
        }
        public String getName() { return name.get(); } public String getDate() { return date.get(); } public String getType() { return type.get(); }
    }

    public static class LeaveHistory {
        private final SimpleStringProperty start, end, reason, status;
        public LeaveHistory(String start, String end, String reason, String status) {
            this.start = new SimpleStringProperty(start); this.end = new SimpleStringProperty(end);
            this.reason = new SimpleStringProperty(reason); this.status = new SimpleStringProperty(status);
        }
        public String getStart() { return start.get(); } public String getEnd() { return end.get(); }
        public String getReason() { return reason.get(); } public String getStatus() { return status.get(); }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String empName = UserSession.getFullName(); 
        if(empName == null || empName.isEmpty()) empName = "Employee";
        
        userNameLabel.setText(empName);
        welcomeLabel.setText("Welcome back, " + empName + "!");
        
        setupSmoothScale(cardTotal);
        setupSmoothScale(cardTaken);
        setupSmoothScale(cardRemaining);
        
        holidayNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        holidayDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        holidayTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        filteredHolidays = new FilteredList<>(masterHolidayList, p -> true);
        holidaysTable.setItems(filteredHolidays);

        historyStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        historyEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        historyReasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        historyStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        leaveHistoryTable.setItems(historyList);

        if (salaryTable != null) {
            salMonthCol.setCellValueFactory(new PropertyValueFactory<>("month")); salBaseCol.setCellValueFactory(new PropertyValueFactory<>("base"));
            salDedCol.setCellValueFactory(new PropertyValueFactory<>("ded")); salNetCol.setCellValueFactory(new PropertyValueFactory<>("net"));
            salDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            salaryTable.setItems(payslipList);
        }

        loadLeaveData();
        loadHolidaysData();
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
                valStart++; int valEnd = json.indexOf("\"", valStart); return json.substring(valStart, valEnd);
            } else {
                int valEnd = valStart;
                while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}') valEnd++;
                return json.substring(valStart, valEnd).trim();
            }
        } catch (Exception e) { return "0"; }
    }

    private void loadHolidaysData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("get_holidays.php"))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    masterHolidayList.clear();
                    int start = response.indexOf("["); int end = response.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = response.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String name = extractJsonValue(item, "holiday_name");
                            if (!name.equals("0")) masterHolidayList.add(new Holiday(name, extractJsonValue(item, "holiday_date"), extractJsonValue(item, "type"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    private void loadLeaveData() {
        String empId = UserSession.getLoggedInEmployeeId();
        if (empId == null) return;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("get_employee.php?id=" + empId))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if(response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    try {
                        int total = Integer.parseInt(extractJsonValue(response, "total_leaves"));
                        int taken = Integer.parseInt(extractJsonValue(response, "leaves_taken"));
                        totalLeavesLabel.setText(total + " Days"); takenLeavesLabel.setText(taken + " Days"); remainingLeavesLabel.setText((total - taken) + " Days");
                    } catch (Exception e) {}
                }
            }));
        } catch (Exception e) {}
    }

    private void loadLeaveHistory() {
        String empId = UserSession.getLoggedInEmployeeId();
        if (empId == null) return;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("leave_action.php?id=" + empId))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    historyList.clear();
                    int start = response.indexOf("["); int end = response.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = response.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String startDate = extractJsonValue(item, "start_date");
                            if (!startDate.equals("0")) historyList.add(new LeaveHistory(startDate, extractJsonValue(item, "end_date"), extractJsonValue(item, "reason"), extractJsonValue(item, "status"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    private void loadSalaryData() {
        String empId = UserSession.getLoggedInEmployeeId();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("payroll_api.php?action=get_mine&id=" + empId))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(res -> Platform.runLater(() -> {
                if(res.contains("\"status\":\"success\"")) {
                    payslipList.clear();
                    int start = res.indexOf("["); int end = res.lastIndexOf("]");
                    if (start != -1 && end != -1 && start < end) {
                        String[] items = res.substring(start + 1, end).split("\\},\\{");
                        for (String item : items) {
                            String month = extractJsonValue(item, "salary_month");
                            if (!month.equals("0")) payslipList.add(new PayslipData(month, extractJsonValue(item, "base_salary"), extractJsonValue(item, "deductions"), extractJsonValue(item, "net_salary"), extractJsonValue(item, "payment_date"))); 
                        }
                    }
                }
            }));
        } catch (Exception e) {}
    }

    @FXML
    protected void handlePrintPayslip() {
        PayslipData selected = salaryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (salaryStatusLabel != null) {
                salaryStatusLabel.setText("Please select a payslip first.");
                salaryStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
            return;
        }
        if (salaryStatusLabel != null) salaryStatusLabel.setText("");

        // Build the official document UI
        VBox document = new VBox(20);
        document.setPadding(new Insets(50));
        document.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2;");

        // Header
        Label companyName = new Label("CoreSync Inc.");
        companyName.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label title = new Label("Official Monthly Payslip");
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #64748B;");
        
        // Fake Bank Details
        Label bankRef = new Label("Corporate Bank Transfer Ref: PK34 CORE 9832 0000 1234\nDate of Issue: " + selected.getDate());
        bankRef.setStyle("-fx-font-size: 12; -fx-text-fill: #94A3B8;");

        VBox header = new VBox(5, companyName, title, bankRef);
        header.setAlignment(Pos.CENTER);

        Line sep1 = new Line(0, 0, 500, 0);
        sep1.setStroke(Color.valueOf("#E2E8F0"));

        // Employee Info
        GridPane empGrid = new GridPane();
        empGrid.setHgap(30); empGrid.setVgap(15);
        
        Label empNameLbl = new Label("Employee Name:"); empNameLbl.setStyle("-fx-font-weight: bold;");
        Label empIdLbl = new Label("Employee ID:"); empIdLbl.setStyle("-fx-font-weight: bold;");
        Label monthLbl = new Label("Salary Month:"); monthLbl.setStyle("-fx-font-weight: bold;");

        empGrid.addRow(0, empNameLbl, new Label(userNameLabel.getText()));
        empGrid.addRow(1, empIdLbl, new Label(UserSession.getLoggedInEmployeeId()));
        empGrid.addRow(2, monthLbl, new Label(selected.getMonth()));

        Line sep2 = new Line(0, 0, 500, 0);
        sep2.setStroke(Color.valueOf("#E2E8F0"));

        // Financials
        GridPane finGrid = new GridPane();
        finGrid.setHgap(50); finGrid.setVgap(15);

        Label baseLbl = new Label("Base Salary:"); baseLbl.setStyle("-fx-font-weight: bold;");
        Label dedLbl = new Label("Deductions:"); dedLbl.setStyle("-fx-font-weight: bold;");
        Label netLbl = new Label("Net Transfer Amount:"); netLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label netValue = new Label("PKR " + selected.getNet());
        netValue.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #16A34A;");

        finGrid.addRow(0, baseLbl, new Label("PKR " + selected.getBase()));
        finGrid.addRow(1, dedLbl, new Label("- PKR " + selected.getDed()));
        finGrid.addRow(2, netLbl, netValue);

        // Footer
        Label footer = new Label("This is a computer generated document and does not require a physical signature.");
        footer.setStyle("-fx-font-size: 11; -fx-text-fill: #94A3B8;");
        VBox footerView = new VBox(footer); footerView.setAlignment(Pos.CENTER);

        document.getChildren().addAll(header, sep1, empGrid, sep2, finGrid, new Region(), footerView);

        // Show Print Window
        Stage printStage = new Stage();
        printStage.setTitle("Print Payslip");
        printStage.initModality(Modality.APPLICATION_MODAL);

        Button confirmPrintBtn = new Button("Send to Printer");
        confirmPrintBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-font-size: 14; -fx-cursor: hand;");
        
        confirmPrintBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceed = job.showPrintDialog(printStage);
                if (proceed) {
                    boolean printed = job.printPage(document);
                    if (printed) job.endJob();
                }
            } else {
                salaryStatusLabel.setText("No printer detected.");
                salaryStatusLabel.setStyle("-fx-text-fill: red;");
            }
            printStage.close();
        });

        VBox root = new VBox(20, document, confirmPrintBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F8FAFC;");

        printStage.setScene(new Scene(root, 650, 650));
        printStage.show();
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = searchBar.getText();
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredHolidays.setPredicate(holiday -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredHolidays.setPredicate(holiday -> {
                return holiday.getName().toLowerCase().contains(lowerCaseFilter) ||
                       holiday.getType().toLowerCase().contains(lowerCaseFilter) ||
                       holiday.getDate().toLowerCase().contains(lowerCaseFilter);
            });
        }
        if (holidaysVBox != null && !holidaysVBox.isVisible() && !searchText.trim().isEmpty()) showHolidays();
    }

    private void setupSmoothScale(javafx.scene.Node node) {
        if (node == null) return;
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node); scaleIn.setToX(1.03); scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node); scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        node.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.playFromStart(); }); node.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.playFromStart(); });
    }

    private void hideAllViews() {
        if (dashboardVBox != null) dashboardVBox.setVisible(false);
        if (attendanceVBox != null) attendanceVBox.setVisible(false);
        if (leaveRequestVBox != null) leaveRequestVBox.setVisible(false);
        if (holidaysVBox != null) holidaysVBox.setVisible(false);
        if (settingsVBox != null) settingsVBox.setVisible(false);
        if (salaryVBox != null) salaryVBox.setVisible(false);
    }

    private void setActiveSidebarButton(HBox activeBtn) {
        HBox[] allBtns = {dashboardBtn, attendanceBtn, leaveBtn, holidaysBtn, settingsBtn, salaryBtn};
        for (HBox btn : allBtns) { if (btn != null) btn.getStyleClass().remove("sidebar-btn-active"); }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("sidebar-btn-active")) { activeBtn.getStyleClass().add("sidebar-btn-active"); }
    }

    @FXML protected void showDashboard() { setActiveSidebarButton(dashboardBtn); hideAllViews(); if (dashboardVBox != null) dashboardVBox.setVisible(true); }
    @FXML protected void showAttendance() { setActiveSidebarButton(attendanceBtn); hideAllViews(); if (attendanceVBox != null) attendanceVBox.setVisible(true); }
    @FXML protected void showLeaveRequest() { setActiveSidebarButton(leaveBtn); hideAllViews(); if (leaveRequestVBox != null) leaveRequestVBox.setVisible(true); loadLeaveHistory(); }
    @FXML protected void showHolidays() { setActiveSidebarButton(holidaysBtn); hideAllViews(); if (holidaysVBox != null) holidaysVBox.setVisible(true); }
    @FXML protected void showSettings() { setActiveSidebarButton(settingsBtn); hideAllViews(); if (settingsVBox != null) settingsVBox.setVisible(true); }
    @FXML protected void showSalary() { setActiveSidebarButton(salaryBtn); hideAllViews(); if (salaryVBox != null) { salaryVBox.setVisible(true); loadSalaryData(); } }

    @FXML
    private void handleClockToggle() {
        String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        String empId = UserSession.getLoggedInEmployeeId();
        String action = !isClockedIn ? "in" : "out";

        if (!isClockedIn) {
            isClockedIn = true;
            attendanceStatusLabel.setText("🟢 On Duty"); attendanceStatusLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
            clockInOutBtn.setText("Clock Out"); clockInOutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 16; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
            timeInLabel.setText(currentTime); timeOutLabel.setText("--:-- --");
        } else {
            isClockedIn = false;
            attendanceStatusLabel.setText("🔴 Off Duty"); attendanceStatusLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            clockInOutBtn.setText("Clock In"); clockInOutBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 16; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
            timeOutLabel.setText(currentTime);
        }
        try {
            String jsonPayload = String.format("{\"employee_id\":\"%s\", \"action\":\"%s\"}", empId, action);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("clock_action.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {}
    }

    @FXML
    protected void handleSubmitLeaveRequest(ActionEvent event) {
        String empId = UserSession.getLoggedInEmployeeId();
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null || leaveReasonArea.getText().trim().isEmpty()) {
            leaveStatusLabel.setText("Please fill out all fields."); leaveStatusLabel.setStyle("-fx-text-fill: #d62828; -fx-font-weight: bold;"); return;
        }
        try {
            String jsonPayload = String.format("{\"employee_id\":\"%s\", \"start_date\":\"%s\", \"end_date\":\"%s\", \"reason\":\"%s\"}", empId, startDatePicker.getValue().toString(), endDatePicker.getValue().toString(), leaveReasonArea.getText().trim().replace("\"", "\\\"").replace("\n", " "));
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiConfig.endpoint("leave_action.php"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(response -> Platform.runLater(() -> {
                if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                    leaveStatusLabel.setText("Request submitted successfully!"); leaveStatusLabel.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                    startDatePicker.setValue(null); endDatePicker.setValue(null); leaveReasonArea.clear();
                    loadLeaveHistory(); 
                } else {
                    leaveStatusLabel.setText("Failed to submit request."); leaveStatusLabel.setStyle("-fx-text-fill: #d62828; -fx-font-weight: bold;");
                }
            }));
        } catch (Exception e) {}
    }

    @FXML
    protected void handleChangePassword() {
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