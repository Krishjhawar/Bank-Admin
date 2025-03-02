import java.sql.*;

public class AuthService {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";  // Your database URL
    private static final String DB_USER = "root";  // Your database username
    private static final String DB_PASSWORD = "krish248650.";  // Your database password

    // Method to authenticate the user based on username (email) and password
    public boolean authenticateUser(String username, String password) {
    String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username);
        stmt.setString(2, password);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Authentication successful!");
                return true;
            } else {
                System.out.println("Invalid credentials.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error during authentication: " + e.getMessage());
    }
    return false;
}


    // Method to retrieve the role of a user (admin or staff)
    public String getUserRole(String username) {
        String sql = "SELECT role FROM user WHERE email = ?";
        
        // Establish the database connection using the provided URL, username, and password
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);  // Set the username (email)

            // Execute the query to fetch the user's role
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");  // Return the user's role (admin or staff)
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle SQL errors
        }
        return null;  // Return null if no role is found
    }
}
