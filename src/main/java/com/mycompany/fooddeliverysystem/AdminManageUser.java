package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;



public class AdminManageUser extends JFrame {
    private DefaultTableModel tableModel;
    private final String userDataFile = "Users.txt";
    private JTable userTable;
    
     public AdminManageUser() {
        super("Manage Users");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel userPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // Reduced gaps between buttons
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Minimal padding

        JButton addUserButton = createStyledButton("Add User");
        JButton editUserButton = createStyledButton("Edit User");
        JButton deleteUserButton = createStyledButton("Delete User");

        addUserButton.addActionListener(e -> addUser());
        editUserButton.addActionListener(e -> editUser());
        deleteUserButton.addActionListener(e -> deleteUser());

        userPanel.add(addUserButton);
        userPanel.add(editUserButton);
        userPanel.add(deleteUserButton);

        mainPanel.add(userPanel, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Removed vertical gap
        JTextField searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Search");
        JButton showAllButton = createStyledButton("Show All");

        searchButton.addActionListener(e -> searchUser(searchField.getText()));
        showAllButton.addActionListener(e -> showAllUsers());

        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 18));  // Change font size here
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        // Set no border and no margin for the search panel
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0)); // Minimal padding

        mainPanel.add(searchPanel, BorderLayout.NORTH); // Added search panel

        // Table Panel (Scroll pane)
        tableModel = new DefaultTableModel(new Object[]{"User Code", "Username", "Email", "Password", "Role", "Date of Birth"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Ensure no additional padding between search and table
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Placing the table directly under search

        // Back Button Panel
        JPanel backPanel = new JPanel();
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            // Navigate to the AdminMain page
            new AdminMain().setVisible(true);
            dispose(); // Close the current window
        });
        backPanel.add(backButton);
        mainPanel.add(backPanel, BorderLayout.SOUTH); // Place Back button at the bottom

        // Load user data into the table
        loadUserData();
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        return button;
    }
    
        // Method to validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Method to validate date format
    private boolean isValidDateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Check if a username already exists in the table
    private boolean isUsernameExists(String username) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (username.equals(tableModel.getValueAt(row, 1))) {
                return true;
            }
        }
        return false;
    }
    
    private String generateUserCode() {
        // Read existing user data from file to get the last user code
        int lastUserCode = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("User Code: ")) {
                    String code = line.substring(11);
                    if (code.startsWith("user")) {
                        try {
                            int userCodeNumber = Integer.parseInt(code.substring(4));
                            if (userCodeNumber > lastUserCode) {
                                lastUserCode = userCodeNumber;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore invalid user code formats
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Increment the last user code and generate a new one
        lastUserCode++;
        return "user" + String.format("%03d", lastUserCode);  // user001, user002, ...
    }
    
        // Method to check if email already exists
    private boolean isEmailExists(String email) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (email.equals(tableModel.getValueAt(row, 2))) {  // Email is in the 4th column (index 3)
                return true;
            }
        }
        return false;
    }
    
    // Method to search for users by username
    private void searchUser(String username) {
        if (username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel filteredModel = new DefaultTableModel(new Object[]{"User Code", "Username", "Email", "Password", "Role", "Date of Birth"}, 0);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String currentUsername = (String) tableModel.getValueAt(i, 1);
            if (currentUsername.toLowerCase().contains(username.toLowerCase())) {
                filteredModel.addRow(new Object[]{
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4),
                    tableModel.getValueAt(i, 5)
                });
            }
        }

        if (filteredModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No users found matching the name: " + username, "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        userTable.setModel(filteredModel);
    }

    // Method to reset and show all users
    private void showAllUsers() {
        userTable.setModel(tableModel);
    }


    private void addUser() {
        // Variables to hold user input and preserve data across validation attempts
        String username = "";
        String email = "";
        String password = "";
        String confirmPassword = "";
        String dob = "";
        String role = "Vendor";

        while (true) { // Loop until the user cancels or provides valid input
            JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
            JTextField usernameField = new JTextField(username);
            JTextField emailField = new JTextField(email);
            JPasswordField passwordField = new JPasswordField(password);
            JPasswordField confirmPasswordField = new JPasswordField(confirmPassword);
            JComboBox<String> rolesComboBox = new JComboBox<>(new String[]{"Vendor", "Customer", "Runner"});
            rolesComboBox.setSelectedItem(role);
            JTextField dobField = new JTextField(dob);

            inputPanel.add(new JLabel("Username:"));
            inputPanel.add(usernameField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailField);
            inputPanel.add(new JLabel("Password:"));
            inputPanel.add(passwordField);
            inputPanel.add(new JLabel("Confirm Password:"));
            inputPanel.add(confirmPasswordField);
            inputPanel.add(new JLabel("Roles:"));
            inputPanel.add(rolesComboBox);
            inputPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"));
            inputPanel.add(dobField);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add User", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                // Exit the loop if the user cancels
                return;
            }

            // Retrieve input from the fields
            username = usernameField.getText();
            email = emailField.getText();
            password = new String(passwordField.getPassword());
            confirmPassword = new String(confirmPasswordField.getPassword());
            dob = dobField.getText();
            role = (String) rolesComboBox.getSelectedItem();

            // Validation checks
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (password.length() < 7) {
                JOptionPane.showMessageDialog(this, "Password must be at least 7 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (isEmailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (isUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (!isValidDateFormat(dob)) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // If all validations pass, add the user
            String userCode = generateUserCode();
            Object[] rowData = {userCode, username, email, password, role, dob};
            tableModel.addRow(rowData);

            appendUserData();
            break; // Exit the loop after successful addition
        }
    }



    private void editUser() {
    // Ensure a row is selected
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "No user selected. Please select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Fetch initial user details
    String userCode = (String) tableModel.getValueAt(selectedRow, 0);
    String username = (String) tableModel.getValueAt(selectedRow, 1);
    String email = (String) tableModel.getValueAt(selectedRow, 2);
    String password = (String) tableModel.getValueAt(selectedRow, 3);
    String role = (String) tableModel.getValueAt(selectedRow, 4);
    String dob = (String) tableModel.getValueAt(selectedRow, 5);

    while (true) { // Loop until the user cancels or provides valid input
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField usernameField = new JTextField(username);
        JTextField emailField = new JTextField(email);
        JPasswordField passwordField = new JPasswordField(password);
        JComboBox<String> rolesComboBox = new JComboBox<>(new String[]{"Vendor", "Customer", "Runner"});
        rolesComboBox.setSelectedItem(role);
        JTextField dobField = new JTextField(dob);

        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Roles:"));
        inputPanel.add(rolesComboBox);
        inputPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"));
        inputPanel.add(dobField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // Exit the loop if the user cancels
        }

        // Retrieve input from the fields
        username = usernameField.getText();
        email = emailField.getText();
        password = new String(passwordField.getPassword());
        role = (String) rolesComboBox.getSelectedItem();
        dob = dobField.getText();

        // Validation checks
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (password.length() < 7) {
            JOptionPane.showMessageDialog(this, "Password must be at least 7 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (isEmailExists(email) && !email.equals(tableModel.getValueAt(selectedRow, 2))) {
            JOptionPane.showMessageDialog(this, "Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (!isValidDateFormat(dob)) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        if (isUsernameExists(username) && !username.equals(tableModel.getValueAt(selectedRow, 1))) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }

        // Update user data in the table
        tableModel.setValueAt(username, selectedRow, 1);
        tableModel.setValueAt(email, selectedRow, 2);
        tableModel.setValueAt(password, selectedRow, 3);
        tableModel.setValueAt(role, selectedRow, 4);
        tableModel.setValueAt(dob, selectedRow, 5);

        saveEditedData(userCode, username, email, password, role, dob); // Call the new save function
        JOptionPane.showMessageDialog(this, "User details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        break; // Exit the loop after successful update
    }
}

private void saveEditedData(String userCode, String username, String email, String password, String role, String dob) {
    File inputFile = new File("src/main/resources/txtfile/Users.txt");
    File tempFile = new File("src/main/resources/txtfile/Users_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isEditing = false;

        while ((line = reader.readLine()) != null) {
            // Check for the user to edit
            if (line.startsWith("User Code: ") && line.substring(11).equals(userCode)) {
                // Start editing this user
                isEditing = true;

                // Write updated details for the user
                writer.write("User Code: " + userCode);
                writer.newLine();
                writer.write("Username: " + username);
                writer.newLine();
                writer.write("Email: " + email);
                writer.newLine();
                writer.write("Password: " + password);
                writer.newLine();
                writer.write("Role: " + role);
                writer.newLine();
                writer.write("Date of Birth: " + dob);
                writer.newLine();

                continue; // Skip the rest of the user's original details
            }

            // Preserve the "Credit" field, if present
            if (isEditing && line.startsWith("Credit: ")) {
                writer.write(line);
                writer.newLine();
                isEditing = false; // Done editing after writing the "Credit" field
                continue;
            }

            // Reset editing flag upon encountering a separator
            if (line.startsWith("-----------------------")) {
                isEditing = false;
            }

            // Write all other lines as-is
            if (!isEditing) {
                writer.write(line);
                writer.newLine();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error saving edited data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Replace the original file with the updated one
    if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
        JOptionPane.showMessageDialog(this, "Error updating file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        System.out.println("successful");
    }
}






    private void deleteUser() {
    // Ensure a row is selected
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "No user selected. Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Confirm deletion
    int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the selected user?",
            "Delete Confirmation",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        // Get the user code of the selected row
        String userCode = (String) tableModel.getValueAt(selectedRow, 0);

        // Remove the selected row from the table
        tableModel.removeRow(selectedRow);

        // Rewrite the file without the deleted user
        removeUserFromFile(userCode);

        JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}



    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            String userCode = null, username = null, email = null, password = null, role = null, dob = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("User Code: ")) {
                    userCode = line.substring(11);
                } else if (line.startsWith("Username: ")) {
                    username = line.substring(10);
                } else if (line.startsWith("Email: ")) {
                    email = line.substring(7);
                } else if (line.startsWith("Password: ")) {
                    password = line.substring(10);
                } else if (line.startsWith("Role: ")) {
                    role = line.substring(6);
                } else if (line.startsWith("Date of Birth: ")) {
                    dob = line.substring(15);
                    // Add user data to the table
                    Object[] rowData = {userCode, username, email, password, role, dob};
                    tableModel.addRow(rowData);
                    // Reset variables for the next user
                    userCode = null;
                    username = null;
                    email = null;
                    password = null;
                    role = null;
                    dob = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
private void removeUserFromFile(String userCode) {
    File inputFile = new File("src/main/resources/txtfile/Users.txt");
    File tempFile = new File("src/main/resources/txtfile/Users_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isDeleting = false;

        while ((line = reader.readLine()) != null) {
            // Detect the start of the user's block to delete
            if (line.startsWith("User Code: ") && line.substring(11).equals(userCode)) {
                isDeleting = true; // Start skipping lines for this user
            }

            // Skip lines while deleting
            if (isDeleting) {
                if (line.startsWith("-----------------------")) {
                    isDeleting = false; // Stop skipping when block ends
                }
                continue; // Skip this line
            }

            // Write all other lines to the temp file
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error deleting user from file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Replace the original file with the updated file
    if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
        JOptionPane.showMessageDialog(this, "Error updating file after deletion. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        System.out.println("User successfully removed from file.");
    }
}

private void appendUserData() {
    File file = new File("src/main/resources/txtfile/Users.txt");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            // Check if the user already exists in the file
            if (isUserInFile((String) tableModel.getValueAt(row, 0))) {
                continue; // Skip existing users
            }

            // Write user details
            writer.write("User Code: " + tableModel.getValueAt(row, 0));
            writer.newLine();
            writer.write("Username: " + tableModel.getValueAt(row, 1));
            writer.newLine();
            writer.write("Email: " + tableModel.getValueAt(row, 2));
            writer.newLine();
            writer.write("Password: " + tableModel.getValueAt(row, 3));
            writer.newLine();
            writer.write("Role: " + tableModel.getValueAt(row, 4));
            writer.newLine();
            writer.write("Date of Birth: " + tableModel.getValueAt(row, 5));
            writer.newLine();

            // Add additional fields for Customer
            if ("Customer".equals(tableModel.getValueAt(row, 4))) {
                writer.write("Credit: RM0");
                writer.newLine();
                writer.write("Home Address: no address input");
                writer.newLine();
                writer.write("Work Address: no address input");
                writer.newLine();
                writer.write("Other Address: no address input");
                writer.newLine();
                writer.write("Default Address: Home Address");
                writer.newLine();
            }

            // Add the separator
            writer.write("-----------------------");
            writer.newLine();
        }

        JOptionPane.showMessageDialog(this, "User data appended successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error appending user data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Helper method to check if a user already exists in the file
private boolean isUserInFile(String userCode) {
    File file = new File("src/main/resources/txtfile/Users.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("User Code: " + userCode)) {
                return true; // User already exists
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return false;
}








    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminManageUser().setVisible(true));
    }
}