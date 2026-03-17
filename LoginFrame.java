import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFrame() {
    
        setTitle("CoreSync — Login");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JLabel headerLabel = new JLabel("CoreSync — Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(25, 60, 120));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setPreferredSize(new Dimension(450, 60));
        add(headerLabel, BorderLayout.NORTH);

    
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        formPanel.add(new JLabel("Employee ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField(); // Masks password automatically
        formPanel.add(passField);

        add(formPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(350, 40));
        loginButton.setBackground(new Color(25, 60, 120));
        loginButton.setForeground(Color.WHITE);
        
        loginButton.addActionListener(e -> authenticateUser());
        
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void authenticateUser() {
        String empId = idField.getText();
        String password = new String(passField.getPassword());

        if (empId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String jsonInput = String.format("{\"employee_id\":\"%s\", \"password\":\"%s\"}", empId, password);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/payroll_api/login.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (responseBody.contains("\"status\":\"success\"")) {
                this.dispose(); 

                if (responseBody.contains("\"role\":\"HR Admin\"")) {
                    new AdminDashboardFrame().setVisible(true);
                } else {
                    new EmployeeDashboardFrame().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server Connection Error. Is XAMPP running?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}