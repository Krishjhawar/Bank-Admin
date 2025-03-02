import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StaffDashboard extends JFrame {

    private JTextField searchField;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public StaffDashboard() {
        setTitle("Staff Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Staff Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchCustomer);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.CENTER);

        // Customer Table (updated column order and included address)
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Account Number");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Address");
        tableModel.addColumn("Account Status");

        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel();
        JButton viewTransactionsButton = new JButton("View Transactions");
        viewTransactionsButton.addActionListener(this::viewTransactions);
        JButton editCustomerButton = new JButton("Edit Customer Details");
        editCustomerButton.addActionListener(this::editCustomer);
        JButton freezeAccountButton = new JButton("Freeze Account");
        freezeAccountButton.addActionListener(this::freezeAccount);
        actionPanel.add(viewTransactionsButton);
        actionPanel.add(editCustomerButton);
        actionPanel.add(freezeAccountButton);
        panel.add(actionPanel, BorderLayout.EAST);

        // Add panel to frame
        add(panel);

        // Load customer data
        loadCustomerData();
    }

    private void loadCustomerData() {
        String sql = "SELECT * FROM bank_user";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0); // Clear the table before loading new data

            while (rs.next()) {
                Object[] row = new Object[6];  // Number of columns after removal of Customer ID
                row[0] = rs.getString("account_number");  // Account number is now first
                row[1] = rs.getString("name");
                row[2] = rs.getString("email");
                row[3] = rs.getString("phone");
                row[4] = rs.getString("address");  // Address column added
                row[5] = rs.getInt("frozen") == 1 ? "Frozen" : "Active";  // Account status column
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomer(ActionEvent e) {
        String searchQuery = searchField.getText();
        String sql = "SELECT * FROM bank_user WHERE account_number LIKE ? OR name LIKE ? OR email LIKE ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            stmt.setString(3, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);  // Clear previous data

            while (rs.next()) {
                Object[] row = new Object[6];  // Ensure the array size matches the column count
                row[0] = rs.getString("account_number");
                row[1] = rs.getString("name");
                row[2] = rs.getString("email");
                row[3] = rs.getString("phone");
                row[4] = rs.getString("address");  // Address column included
                row[5] = rs.getInt("frozen") == 1 ? "Frozen" : "Active";  // Account status
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching customers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewTransactions(ActionEvent e) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String accountNumber = (String) customerTable.getValueAt(selectedRow, 0); // Account number is now at index 0
            System.out.println("Selected Account Number: " + accountNumber);
            showTransactionHistory(accountNumber);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to view transactions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTransactionHistory(String accountNumber) {
        String sql = "SELECT * FROM transactions WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel transactionTableModel = new DefaultTableModel();
            transactionTableModel.addColumn("Transaction ID");
            transactionTableModel.addColumn("Transaction Type");
            transactionTableModel.addColumn("Amount");
            transactionTableModel.addColumn("Date");

            boolean hasTransactions = false;  // Flag to check if there are any transactions
            while (rs.next()) {
                Object[] transactionRow = new Object[4];  // Number of columns in transaction table
                transactionRow[0] = rs.getInt("transaction_id");
                transactionRow[1] = rs.getString("transaction_type");
                transactionRow[2] = rs.getDouble("amount");
                transactionRow[3] = rs.getTimestamp("transaction_date");
                transactionTableModel.addRow(transactionRow);
                hasTransactions = true;
            }

            if (!hasTransactions) {
                JOptionPane.showMessageDialog(this, "No transactions found for account: " + accountNumber, "No Transactions", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTable transactionTable = new JTable(transactionTableModel);
                JScrollPane scrollPane = new JScrollPane(transactionTable);
                JOptionPane.showMessageDialog(this, scrollPane, "Transaction History for Account: " + accountNumber, JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving transactions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCustomer(ActionEvent e) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String accountNumber = (String) customerTable.getValueAt(selectedRow, 0);

            // Create a combo box to select which field to edit
            String[] fields = {"Name", "Phone", "Address", "Email"};
            JComboBox<String> fieldComboBox = new JComboBox<>(fields);
            JTextField newFieldValue = new JTextField(20);

            JPanel panel = new JPanel();
            panel.add(new JLabel("Select Field to Edit:"));
            panel.add(fieldComboBox);
            panel.add(new JLabel("Enter New Data:"));
            panel.add(newFieldValue);

            int option = JOptionPane.showConfirmDialog(this, panel, "Edit Customer Details", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String selectedField = (String) fieldComboBox.getSelectedItem();
                String newValue = newFieldValue.getText().trim();
                
                if (!newValue.isEmpty()) {
                    updateCustomerDetails(accountNumber, selectedField, newValue);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid value for the selected field.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomerDetails(String accountNumber, String selectedField, String newValue) {
        String sql = "UPDATE bank_user SET " + selectedField.toLowerCase() + " = ? WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newValue);
            stmt.setString(2, accountNumber);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, selectedField + " updated successfully.");
                loadCustomerData();  // Reload customer data
            } else {
                JOptionPane.showMessageDialog(this, "Error updating customer details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void freezeAccount(ActionEvent e) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String accountNumber = (String) customerTable.getValueAt(selectedRow, 0);

            String sql = "UPDATE bank_user SET frozen = 1 WHERE account_number = ?";
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, accountNumber);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Account frozen successfully.");
                    loadCustomerData();  // Reload customer data
                } else {
                    JOptionPane.showMessageDialog(this, "Error freezing account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error freezing account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to freeze.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StaffDashboard().setVisible(true);
        });
    }
}
