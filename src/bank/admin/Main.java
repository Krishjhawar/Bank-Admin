import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Start the application by launching the login form
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
