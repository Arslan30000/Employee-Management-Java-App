import javax.swing.*;
import java.awt.*;

public class EmployeeDashboardFrame extends JFrame {
    public EmployeeDashboardFrame() {
        setTitle("CoreSync — Employee Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        JLabel welcomeLabel = new JLabel("Welcome to the Employee Area", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel);
    }
}