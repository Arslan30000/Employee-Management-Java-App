import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame() {
        setTitle("CoreSync — HR Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel welcomeLabel = new JLabel("Welcome to the Secure Admin Area", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel);
    }
}