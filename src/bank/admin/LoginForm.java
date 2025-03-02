import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Login");
        setSize(300, 200); // Original frame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel to contain the buttons for admin and staff login
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10)); // 3 rows, 1 column, 10px gap
        add(panel);

        JLabel welcomeLabel = new JLabel("Welcome to the Admin/Staff Panel", JLabel.CENTER);
        panel.add(welcomeLabel);

        // Creating buttons for admin and staff login
        JButton adminButton = new JButton("Admin Panel");
        JButton staffButton = new JButton("Staff Panel");

        panel.add(adminButton);
        panel.add(staffButton);

        // Action for Admin Button
        adminButton.addActionListener(e -> {
            dispose(); // Close the login form
            showLoginForm("admin"); // Open login form for admin authentication
        });

        // Action for Staff Button
        staffButton.addActionListener(e -> {
            dispose(); // Close the login form
            showLoginForm("staff"); // Open login form for staff authentication
        });
    }

    private void showLoginForm(String role) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 170); // Adjusted frame size
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        // Panel with GridBagLayout to hold the username and password fields with reduced distance
        JPanel panel = new JPanel(new GridBagLayout());
        loginFrame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Small insets to reduce the gap
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15); // Adjusted width of the text field
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15); // Adjusted width of the password field
        panel.add(passwordField, gbc);

        // Center-aligned login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center-align button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30)); // Adjust button size as needed
        buttonPanel.add(loginButton);

        loginFrame.add(buttonPanel, BorderLayout.SOUTH); // Add button panel at the bottom of the frame

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate user based on the role (admin or staff)
            AuthService authService = new AuthService();
            if (authService.authenticateUser(username, password)) {
                String userRole = authService.getUserRole(username);

                // Proceed to the correct dashboard based on the role
                if (userRole != null) {
                    loginFrame.dispose(); // Close the login form
                    if ("admin".equals(userRole)) {
                        JOptionPane.showMessageDialog(null, "Welcome to Admin Dashboard");
                        new AdminDashboard().setVisible(true); // Open Admin Dashboard
                    } else if ("staff".equals(userRole)) {
                        JOptionPane.showMessageDialog(null, "Welcome to Staff Dashboard");
                        new StaffDashboard().setVisible(true); // Open Staff Dashboard
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Role mismatch or invalid credentials");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials");
            }
        });

        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}