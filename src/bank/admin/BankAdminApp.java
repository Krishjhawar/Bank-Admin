import javax.swing.*;
import java.sql.*;

public class BankAdminApp {

    private void addBankCustomer() {
        String name = JOptionPane.showInputDialog("Enter Name:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String phone = JOptionPane.showInputDialog("Enter Phone:");
        String address = JOptionPane.showInputDialog("Enter Address:");
        
        // Account type selection
        String[] accountTypes = {"checking", "savings"};
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);
        int option = JOptionPane.showOptionDialog(
                null, accountTypeComboBox, "Select Account Type", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        
        if (option == JOptionPane.OK_OPTION) {
            String selectedAccountType = (String) accountTypeComboBox.getSelectedItem();
            
            try {
                double initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter Initial Balance:"));
            
                // SQL to insert into user table
                String userSql = "INSERT INTO user (name, email, phone, address, role, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
                     PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    
                    userStmt.setString(1, name);
                    userStmt.setString(2, email);
                    userStmt.setString(3, phone);
                    userStmt.setString(4, address);
                    userStmt.setString(5, "customer"); // Default role
                    userStmt.setString(6, "defaultpassword"); // Default password

                    int rowsAffected = userStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet rs = userStmt.getGeneratedKeys();
                        if (rs.next()) {
                            int userId = rs.getInt(1);

                            // Insert into the `bank_user` table
                            String bankUserSql = "INSERT INTO bank_user (name, email, phone, address, account_type, balance, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement bankStmt = conn.prepareStatement(bankUserSql)) {
                                bankStmt.setString(1, name);
                                bankStmt.setString(2, email);
                                bankStmt.setString(3, phone);
                                bankStmt.setString(4, address);
                                bankStmt.setString(5, selectedAccountType);
                                bankStmt.setDouble(6, initialBalance);
                                bankStmt.setInt(7, userId);

                                bankStmt.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Bank customer added successfully.");
                                fetchUsers(); // Refresh user list after adding
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, "Error adding to bank_user table: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error adding to user table: No rows affected.");
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error adding to user table: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid balance input. Please enter a valid number.");
            }
        }
    }
    private void removeBankCustomer() {
    String userIdInput = JOptionPane.showInputDialog("Enter User ID to remove:");
    try {
        int userId = Integer.parseInt(userIdInput); // Ensure user ID is an integer

        String deleteUserSql = "DELETE FROM user WHERE user_id = ?";
        String deleteBankUserSql = "DELETE FROM bank_user WHERE user_id = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql);
             PreparedStatement deleteBankUserStmt = conn.prepareStatement(deleteBankUserSql)) {

            // Start a transaction
            conn.setAutoCommit(false);

            // Delete from the bank_user table
            deleteBankUserStmt.setInt(1, userId);
            int bankUserDeleted = deleteBankUserStmt.executeUpdate();

            if (bankUserDeleted > 0) {
                // If the bank_user record is deleted, delete from the user table
                deleteUserStmt.setInt(1, userId);
                int userDeleted = deleteUserStmt.executeUpdate();

                if (userDeleted > 0) {
                    // Commit transaction if both deletions succeeded
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Bank customer removed successfully.");
                    fetchUsers(); // Refresh the user list
                } else {
                    conn.rollback(); // Rollback if user deletion failed
                    JOptionPane.showMessageDialog(null, "Error: User record not found in the user table.");
                }
            } else {
                conn.rollback(); // Rollback if bank_user deletion failed
                JOptionPane.showMessageDialog(null, "Error: Bank customer record not found in the bank_user table.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error while removing bank customer: " + e.getMessage());
            e.printStackTrace();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid User ID. Please enter a numeric value.");
    }
}


    // Stub method for fetchUsers to simulate refreshing user list
    private void fetchUsers() {
        // Placeholder for code to refresh and display the updated user list in the app
    }
    
    public static void main(String[] args) {
        BankAdminApp app = new BankAdminApp();
        app.addBankCustomer();
    }
}
