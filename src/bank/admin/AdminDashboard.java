import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;


public class AdminDashboard extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addBankCustomerButton, addUserButton, removeUserButton, removeBankCustomerButton, editUserButton, editBankCustomerButton;
    private JTextField searchField;
    private JButton searchButton, adminStaffButton, bankCustomerButton;
    private JButton viewTransactionsButton; 
    private JButton viewCustomerDetailsButton;
private JButton freezeButton;
private JButton unfreezeButton;

            
public AdminDashboard() { 
    setTitle("Admin Dashboard");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel(new BorderLayout());
    add(panel);

    // Create left and right panels for admin/staff and bank customer buttons
    JPanel leftButtonsPanel = new JPanel(new GridLayout(6, 1, 10, 10));  // Increased GridLayout to accommodate the new button
    JPanel rightButtonsPanel = new JPanel(new GridLayout(4, 1, 10, 10));

    panel.add(leftButtonsPanel, BorderLayout.WEST);
    panel.add(rightButtonsPanel, BorderLayout.EAST);

    // Add Bank Customer Buttons to Right Panel
    addBankCustomerButton = new JButton("Add Bank Customer");
    removeBankCustomerButton = new JButton("Remove Bank Customer");
    editBankCustomerButton = new JButton("Edit Bank Customer");
    bankCustomerButton = new JButton("Bank Customer Actions");

    rightButtonsPanel.add(addBankCustomerButton);
    rightButtonsPanel.add(removeBankCustomerButton);
    rightButtonsPanel.add(editBankCustomerButton);
    rightButtonsPanel.add(bankCustomerButton);

    addBankCustomerButton.addActionListener(e -> addBankCustomer());
    removeBankCustomerButton.addActionListener(e -> showBankCustomerRemovalPanel());
    editBankCustomerButton.addActionListener(e -> editBankCustomer());
    bankCustomerButton.addActionListener(e -> showBankCustomerList());

    // Add Admin/Staff Buttons to Left Panel
    addUserButton = new JButton("Add Admin/Staff");
    removeUserButton = new JButton("Remove Admin/Staff");
    editUserButton = new JButton("Edit Admin/Staff");
    adminStaffButton = new JButton("Admin/Staff Actions");

    leftButtonsPanel.add(addUserButton);
    leftButtonsPanel.add(removeUserButton);
    leftButtonsPanel.add(editUserButton);
    leftButtonsPanel.add(adminStaffButton);

    addUserButton.addActionListener(e -> addNewUser());
    removeUserButton.addActionListener(e -> showUserRemovalPanel());
    editUserButton.addActionListener(e -> editUser());
    adminStaffButton.addActionListener(e -> showAdminStaffList());

    // Add View Customer Details Button to Left Panel (separate from transactions)
    viewCustomerDetailsButton = new JButton("View Customer Details");
    leftButtonsPanel.add(viewCustomerDetailsButton);

    // Add ActionListener for View Customer Details button
    viewCustomerDetailsButton.addActionListener(e -> viewCustomerDetails());

     freezeButton = new JButton("Freeze Account");
        unfreezeButton = new JButton("Unfreeze Account");
    // Freeze/Unfreeze Account Buttons
    freezeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String accountNumber = JOptionPane.showInputDialog(AdminDashboard.this, "Enter Account Number to Freeze:");
            if (accountNumber != null && !accountNumber.isEmpty()) {
                freezeAccount(accountNumber); // Freeze account in the database
            } else {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Account number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    // Action listener for unfreezing the account
    unfreezeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String accountNumber = JOptionPane.showInputDialog(AdminDashboard.this, "Enter Account Number to Unfreeze:");
            if (accountNumber != null && !accountNumber.isEmpty()) {
                unfreezeAccount(accountNumber); // Unfreeze account in the database
            } else {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Account number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    // Add Freeze/Unfreeze buttons to the panel
    JPanel freezeUnfreezePanel = new JPanel(new GridLayout(1, 2, 10, 10));
    freezeUnfreezePanel.add(freezeButton);
    freezeUnfreezePanel.add(unfreezeButton);
    panel.add(freezeUnfreezePanel, BorderLayout.NORTH);

    // Search Panel at Bottom
    JPanel searchPanel = new JPanel();
    searchField = new JTextField(20);
    searchButton = new JButton("Search");
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);
    panel.add(searchPanel, BorderLayout.SOUTH);

    // Table for displaying users
    tableModel = new DefaultTableModel();
    userTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(userTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    fetchUsers();  // Show Admin/Staff users initially

    // Initialize the viewTransactionsButton
    viewTransactionsButton = new JButton("View Transactions");

    // Add the button to the appropriate panel (leftButtonsPanel in this case)
    leftButtonsPanel.add(viewTransactionsButton);

    // Add ActionListener for viewTransactionsButton
    viewTransactionsButton.addActionListener(e -> {
        generateRandomTransactions(10); // Generate 10 random transactions
        showTransactionsList();});
viewTransactionsButton.addActionListener(e -> {
    String accountNumber = JOptionPane.showInputDialog(AdminDashboard.this, "Enter Account Number to View Transactions:");
    if (accountNumber != null && !accountNumber.isEmpty()) {
        showTransactionsForCustomer(accountNumber);
    } else {
        JOptionPane.showMessageDialog(AdminDashboard.this, "Account number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});
    // Search button listener
    searchButton.addActionListener(e -> searchCustomer());
}


    private void showTransactionsList() {
    fetchTransactions();
    toggleButtonsForTransactions();
}

// Toggle button visibility based on the role (admin or staff)
    private void toggleButtonsForTransactions() {
    addBankCustomerButton.setEnabled(false);
    removeBankCustomerButton.setEnabled(false);
    editBankCustomerButton.setEnabled(false);
    viewTransactionsButton.setEnabled(true);

    addUserButton.setEnabled(false);
    removeUserButton.setEnabled(false);
    editUserButton.setEnabled(false);
}
    // Toggle to show Admin/Staff
    private void showAdminStaffList() {
        fetchUsers();
        toggleButtonsForAdminStaff();
    }

    // Toggle to show Bank Customers
    private void showBankCustomerList() {
        fetchBankCustomers();
        toggleButtonsForBankCustomer();
    }

    private void toggleButtonsForAdminStaff() {
        addBankCustomerButton.setEnabled(false);
        removeBankCustomerButton.setEnabled(false);
        editBankCustomerButton.setEnabled(false);

        addUserButton.setEnabled(true);
        removeUserButton.setEnabled(true);
        editUserButton.setEnabled(true);
    }

    private void toggleButtonsForBankCustomer() {
        addBankCustomerButton.setEnabled(true);
        removeBankCustomerButton.setEnabled(true);
        editBankCustomerButton.setEnabled(true);

        addUserButton.setEnabled(false);
        removeUserButton.setEnabled(false);
        editUserButton.setEnabled(false);
    }
    
private void viewCustomerDetails() {
    String accountNumber = JOptionPane.showInputDialog(this, "Enter Account Number:");

    if (accountNumber != null && !accountNumber.isEmpty()) {
        // Query the customer details based on the account number
        String sql = "SELECT * FROM bank_user WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                boolean isFrozen = rs.getBoolean("frozen");
                int warningCount = rs.getInt("warning_count"); // Fetch warning count from the database

                // Display customer details
                String customerDetails = String.format(
                    "Name: %s\nEmail: %s\nPhone: %s\nAddress: %s\nAccount Type: %s\nBalance: %.2f INR\nAccount Frozen: %s",
                    name, email, phone, address, accountType, balance, isFrozen ? "Yes" : "No");

                JOptionPane.showMessageDialog(this, customerDetails, "Customer Details", JOptionPane.INFORMATION_MESSAGE);

                // Handle account freeze status
                if (isFrozen) {
                    JOptionPane.showMessageDialog(this, "This account is frozen. No transactions can be performed.", "Account Frozen", JOptionPane.WARNING_MESSAGE);
                    unfreezeButton.setEnabled(true);  // Enable Unfreeze button if the account is frozen
                    freezeButton.setEnabled(false);  // Disable Freeze button if the account is already frozen
                } else {
                    freezeButton.setEnabled(true);   // Enable Freeze button if the account is not frozen
                    unfreezeButton.setEnabled(false); // Disable Unfreeze button if the account is not frozen
                }

                // Check warning count and display relevant message
                if (warningCount > 0) {
                    JOptionPane.showMessageDialog(this, "This customer has " + warningCount + " warning(s).", "Customer Warnings", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No warnings for this customer.", "No Warnings", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No customer found with that account number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving customer details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Account number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Freeze or Unfreeze Account
private void freezeAccount(String accountNumber) {
    String sql = "UPDATE bank_user SET frozen = TRUE WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, accountNumber);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Account has been frozen.", "Account Frozen", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error freezing account.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Unfreeze Account
private void unfreezeAccount(String accountNumber) {
    String sql = "UPDATE bank_user SET frozen = FALSE WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, accountNumber);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Account has been unfrozen.", "Account Unfrozen", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error unfreezing account.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Update Warning Count in Database
private void updateWarningCount(String accountNumber, int warningCount) {
    String sql = "UPDATE bank_user SET warning_count = ? WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, warningCount);
        stmt.setString(2, accountNumber);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating warning count.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Transaction Handling (Check Freeze Status before Transaction)
private void processTransaction(String accountNumber, double amount) {
    // Check if account is frozen before allowing a transaction
    String sql = "SELECT frozen FROM bank_user WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, accountNumber);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            boolean isFrozen = rs.getBoolean("frozen");
            if (isFrozen) {
                JOptionPane.showMessageDialog(this, "Transaction cannot be processed. The account is frozen.", "Transaction Error", JOptionPane.ERROR_MESSAGE);
                return; // Prevent transaction if account is frozen
            }
        }

        // Proceed with transaction if account is not frozen
        String transactionSql = "INSERT INTO transactions (account_number, amount, transaction_type) VALUES (?, ?, ?)";
        try (PreparedStatement transactionStmt = conn.prepareStatement(transactionSql)) {
            transactionStmt.setString(1, accountNumber);
            transactionStmt.setDouble(2, amount);
            transactionStmt.setString(3, amount > 0 ? "Deposit" : "Withdrawal");
            transactionStmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaction processed successfully.", "Transaction Successful", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error processing transaction.", "Transaction Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void showCustomerDetails(String accountNumber) {
    String sql = "SELECT * FROM bank_user WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, accountNumber);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // Fetch account details
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");

                // Create a dialog to display the account details
                String accountDetails = String.format(
                    "Account Number: %s\nName: %s\nEmail: %s\nPhone: %s\nAddress: %s\nAccount Type: %s\nBalance: %.2f",
                    accountNumber, name, email, phone, address, accountType, balance
                );

                // Show the details in a message dialog
                JOptionPane.showMessageDialog(this, accountDetails);

                // Now fetch and display the transactions related to this account
                fetchTransactionsForCustomer(accountNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching customer details.");
    }
}

private void fetchTransactionsForCustomer(String accountNumber) {
    // Fetch transactions for the customer
    String sql = "SELECT * FROM transactions WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, accountNumber);
        try (ResultSet rs = stmt.executeQuery()) {
            StringBuilder transactions = new StringBuilder("Transactions for Account Number: " + accountNumber + "\n");

            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("transaction_date");
                String type = rs.getString("transaction_type");

                transactions.append(String.format("Transaction ID: %d | Amount: %.2f | Date: %s | Type: %s\n",
                        transactionId, amount, date, type));
            }

            // Show the transaction details
            JOptionPane.showMessageDialog(this, transactions.toString());
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching transactions.");
    }
}
private void showTransactionsForCustomer(String accountNumber) {
    // Adjusted SQL query to exclude description column if it doesn't exist
    String sql = "SELECT transaction_id, transaction_type, amount, transaction_date FROM transactions WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, accountNumber);
        ResultSet rs = stmt.executeQuery();

        // Prepare table model for displaying transactions
        DefaultTableModel transactionTableModel = new DefaultTableModel();
        transactionTableModel.addColumn("Transaction ID");
        transactionTableModel.addColumn("Transaction Type");
        transactionTableModel.addColumn("Amount (INR)");
        transactionTableModel.addColumn("Date");

        // Add rows to the table model
        while (rs.next()) {
            int transactionId = rs.getInt("transaction_id");
            String transactionType = rs.getString("transaction_type");
            double amount = rs.getDouble("amount");
            Timestamp transactionDate = rs.getTimestamp("transaction_date");

            // Add a row without 'description' column
            transactionTableModel.addRow(new Object[]{
                transactionId, transactionType, amount, transactionDate
            });
        }

        // Create a JTable and display it in a dialog
        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        JOptionPane.showMessageDialog(this, scrollPane, "Transaction History for Account: " + accountNumber, JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error retrieving transactions.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    // New Method: Edit Bank Customer
    private void editBankCustomer() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String accountNumber = (String) tableModel.getValueAt(selectedRow, 0);

            String[] options = {"Name", "Email", "Phone", "Address"};
            String fieldToEdit = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose a field to edit:",
                    "Edit Field",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (fieldToEdit != null) {
                String newValue = JOptionPane.showInputDialog("Enter new " + fieldToEdit + ":");

                String sql = "UPDATE bank_user SET " + fieldToEdit.toLowerCase() + "=? WHERE account_number=?";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, newValue);
                    stmt.setString(2, accountNumber);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, fieldToEdit + " updated successfully.");
                    fetchBankCustomers();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bank customer to edit.");
        }
    }

    // New Method: Edit Admin/Staff
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String newName = JOptionPane.showInputDialog("Enter new name:");
            String newEmail = JOptionPane.showInputDialog("Enter new email:");
            String newPhone = JOptionPane.showInputDialog("Enter new phone:");
            String newAddress = JOptionPane.showInputDialog("Enter new address:");

            String sql = "UPDATE user SET name=?, email=?, phone=?, address=? WHERE user_id=?";
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newName);
                stmt.setString(2, newEmail);
                stmt.setString(3, newPhone);
                stmt.setString(4, newAddress);
                stmt.setInt(5, userId);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Admin/staff details updated successfully.");
                fetchUsers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void fetchUsers() {
        // Set columns for user data
        tableModel.setColumnIdentifiers(new Object[]{"User ID", "Name", "Email", "Phone", "Role", "Address"});
        
        String sql = "SELECT * FROM user WHERE role IN ('admin', 'staff')";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear previous data in the table
            tableModel.setRowCount(0);

            // Populate table with user data
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("role");
                String address = rs.getString("address");

                // Add the user data to the table model
                tableModel.addRow(new Object[]{userId, name, email, phone, role, address});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch Bank Customers
    private void fetchBankCustomers() {
        // Set columns for bank customer data
        tableModel.setColumnIdentifiers(new Object[]{"Account Number", "Name", "Email", "Phone", "Account Type", "Balance", "Address"});
        
        String sql = "SELECT * FROM bank_user";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear previous data in the table
            tableModel.setRowCount(0);

            // Populate table with bank customer data
            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                String address = rs.getString("address");

                // Add the bank customer data to the table model
                tableModel.addRow(new Object[]{accountNumber, name, email, phone, accountType, balance, address});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  private void fetchTransactions() {
    // Set columns for transaction data
    tableModel.setColumnIdentifiers(new Object[]{"Transaction ID", "Account Number", "Amount", "Date", "Type"});

    // Query to retrieve all transactions (no need for account number input)
    String sql = "SELECT * FROM transactions";  

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        // Clear previous data in the table
        tableModel.setRowCount(0);

        // Populate the table with transaction data
        while (rs.next()) {
            int transactionId = rs.getInt("transaction_id");
            String accountNumber = rs.getString("account_number");
            double amount = rs.getDouble("amount");
            Date date = rs.getDate("transaction_date");
            String type = rs.getString("transaction_type");

            // Add the transaction data to the table model
            tableModel.addRow(new Object[]{transactionId, accountNumber, amount, date, type});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Check if the current user is an admin
private boolean isAdmin() {
    int userId = getLoggedInUserId();  // Get the logged-in user's ID
    String sql = "SELECT role FROM user WHERE user_id = ?";
    
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, userId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String role = rs.getString("role");
                return "admin".equals(role);  // Return true if the role is admin
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;  // Default to false if no role is found
}

// Get the logged-in user's ID
private int getLoggedInUserId() {
    // In a real application, you would retrieve this from the session or authentication context
    // For now, returning a hardcoded value (replace with actual login logic)
    return 1;  // Placeholder: replace with actual logic to get the logged-in user's ID
}


    private void addBankCustomer() {
    String name = JOptionPane.showInputDialog("Enter Name:");
    String email = JOptionPane.showInputDialog("Enter Email:");
    String phone = JOptionPane.showInputDialog("Enter Phone:");
    String address = JOptionPane.showInputDialog("Enter Address:");

    // Creating an array of bank account types
    String[] accountTypes = {"Checking", "Savings", "Business", "Salary", "Current"};
    JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);

    // Show the combo box dialog to select account type
    int option = JOptionPane.showOptionDialog(
            this, accountTypeComboBox, "Select Account Type", JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE, null, null, null);

    // If the user selects a valid account type
    if (option == JOptionPane.OK_OPTION) {
        String selectedAccountType = (String) accountTypeComboBox.getSelectedItem();
        double initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter Initial Balance:"));

        // Random Account Number Generation
        String accountNumber = generateRandomAccountNumber();

        // Step 1: Insert into the 'bank_user' table directly
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO bank_user (account_number, account_type, balance, name, email, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            insertStmt.setString(1, accountNumber);
            insertStmt.setString(2, selectedAccountType);
            insertStmt.setDouble(3, initialBalance);
            insertStmt.setString(4, name);
            insertStmt.setString(5, email);
            insertStmt.setString(6, phone);
            insertStmt.setString(7, address);

            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bank customer added successfully.");
            fetchBankCustomers();  // Refresh the bank customers list after adding
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding to bank_user table.");
        }
    }
}

// Random account number generation method
private String generateRandomAccountNumber() {
    int randomAccountNumber = (int) (Math.random() * 1000000); // Generating a random number for account number
    return String.format("%06d", randomAccountNumber); // Format as a 6-digit number
}


    // Add a new User (Admin/Staff)
    private void addNewUser() {
        String name = JOptionPane.showInputDialog("Enter Name:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String phone = JOptionPane.showInputDialog("Enter Phone:");
        String address = JOptionPane.showInputDialog("Enter Address:");
        String role = JOptionPane.showInputDialog("Enter Role (admin/staff):");

        // Insert into the 'user' table
        String sql = "INSERT INTO user (name, email, phone, address, role, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setString(5, role);
            stmt.setString(6, "defaultpassword"); // Default password

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "New Admin/Staff added successfully.");
            fetchUsers();  // Refresh user list after adding
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding Admin/Staff.");
        }
    }

    // Show User Removal Panel
    private void showUserRemovalPanel() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String userName = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove Admin/Staff: " + userName + "?",
                    "Remove User",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                removeUser(userId);  // Call the removeUser method with the user ID
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a Admin/Staff to remove.");
        }
    }

    private void removeUser(int userId) {
        String deleteUserSql = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Admin/Staff removed successfully.");
                fetchUsers();  // Refresh user list after removal
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing Admin/Staff.");
        }
    }

    // Show Bank Customer Removal Panel
    private void showBankCustomerRemovalPanel() {
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow != -1) {
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 0); // Get account number
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove bank customer with Account Number: " + accountNumber + "?",
                "Remove Bank Customer",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            removeBankCustomer(accountNumber);  // Call the removeBankCustomer method with the account number
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a bank customer to remove.");
    }
}


   private void removeBankCustomer(String accountNumber) {
    String deleteBankUserSql = "DELETE FROM bank_user WHERE account_number = ?";
    String deleteUserSql = "DELETE FROM user WHERE user_id = (SELECT user_id FROM bank_user WHERE account_number = ?)";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement bankStmt = conn.prepareStatement(deleteBankUserSql);
         PreparedStatement userStmt = conn.prepareStatement(deleteUserSql)) {

        // First delete from 'bank_user' table
        bankStmt.setString(1, accountNumber);
        int bankRowsAffected = bankStmt.executeUpdate();

        if (bankRowsAffected > 0) {
            // Then delete from 'user' table after deleting the bank user
            userStmt.setString(1, accountNumber);
            int userRowsAffected = userStmt.executeUpdate();

            if (userRowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Bank customer and associated user removed successfully.");
                fetchBankCustomers();  // Refresh the bank customer list after removal
            } else {
                JOptionPane.showMessageDialog(this, "Bank customer has been removed, refresh!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error removing bank customer.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error removing bank customer.");
    }
}
    private void searchCustomer() {
    String searchQuery = JOptionPane.showInputDialog(this, "Enter Account Number or Name to Search:");

    if (searchQuery != null && !searchQuery.isEmpty()) {
        // Define a SQL query to search for customers by account number or name
        String sql = "SELECT * FROM bank_user WHERE account_number LIKE ? OR name LIKE ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Use wildcards for LIKE query to allow partial matches
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            
            ResultSet rs = stmt.executeQuery();
            
            // Prepare table model for displaying customers
            DefaultTableModel customerTableModel = new DefaultTableModel();
            customerTableModel.addColumn("Customer ID");
            customerTableModel.addColumn("Name");
            customerTableModel.addColumn("Email");
            customerTableModel.addColumn("Phone");
            customerTableModel.addColumn("Account Type");
            customerTableModel.addColumn("Balance");
            customerTableModel.addColumn("Account Number");

            // Add rows to the table model based on search results
            while (rs.next()) {
                int customerId = rs.getInt("bank_user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                String accountNumber = rs.getString("account_number");

                customerTableModel.addRow(new Object[]{
                        customerId, name, email, phone, accountType, balance, accountNumber
                });
            }

            // Create a JTable to display the results
            JTable customerTable = new JTable(customerTableModel);
            JScrollPane scrollPane = new JScrollPane(customerTable);
            JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving customer data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Search query cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void generateRandomTransactions(int count) {
    List<String> accountNumbers = new ArrayList<>();

    // Fetch account numbers from bank_user table
    String fetchAccountsSQL = "SELECT account_number FROM bank_user";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(fetchAccountsSQL)) {

        while (rs.next()) {
            accountNumbers.add(rs.getString("account_number"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    Random random = new Random();

    String insertTransactionSQL = "INSERT INTO transactions (account_number, amount, transaction_date, transaction_type) VALUES (?, ?, NOW(), ?)";
    String updateBalanceSQL = "UPDATE bank_user SET balance = balance + ? WHERE account_number = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "krish248650.");
         PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionSQL);
         PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceSQL)) {

        for (int i = 0; i < count; i++) {
            String accountNumber = accountNumbers.get(random.nextInt(accountNumbers.size()));
            double amount = 100 + (5000 - 100) * random.nextDouble(); // Amount between 100 and 5000
            String type = random.nextBoolean() ? "deposit" : "withdrawal";

            insertTransactionStmt.setString(1, accountNumber);
            insertTransactionStmt.setDouble(2, amount);
            insertTransactionStmt.setString(3, type);
            insertTransactionStmt.executeUpdate();

            // Update balance based on transaction type
            double balanceChange = type.equals("deposit") ? amount : -amount;
            updateBalanceStmt.setDouble(1, balanceChange);
            updateBalanceStmt.setString(2, accountNumber);
            updateBalanceStmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(null, "Showing all transactions, just a second");
        fetchBankCustomers(); // Refresh the customer list to reflect the updated balances
    } catch (SQLException e) {
        e.printStackTrace();
    }
}   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}
