package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;


public class LoginPage extends javax.swing.JFrame {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        initialize();
    }
    
    private void initialize() {
        // Load the custom Marienda-Black font
        Font meriendaFont = loadCustomFont("src/main/resources/Font/Merienda-Black.ttf");

        // Create the main frame
        frame = new JFrame("Food Delivery System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 750);  // Updated to 1300x700
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Main panel with background color
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        // Left panel for the image (full height coverage without distortion)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        try {
            // Load the image and resize it proportionally
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File("src/main/resources/pic/LoginPage.png")));
            Image img = imageIcon.getImage();  
            int windowHeight = frame.getHeight();
            int imageWidth = img.getWidth(null);
            int imageHeight = img.getHeight(null);

            // Calculate the new width while keeping aspect ratio
            int newWidth = (int) ((double) imageWidth / imageHeight * windowHeight);

            // Resize the image to cover the full height and proportional width
            Image scaledImage = img.getScaledInstance(newWidth, windowHeight, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(imageIcon);
            leftPanel.add(imageLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right panel for the welcome message and login form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Welcome Label (Modern style, updated font)
        JLabel welcomeLabel = new JLabel("Welcome to APU", SwingConstants.CENTER);
        welcomeLabel.setFont(meriendaFont.deriveFont(Font.BOLD, 40)); // Using the custom font
        welcomeLabel.setForeground(new Color(50, 50, 50)); // Dark gray
        welcomeLabel.setBorder(new EmptyBorder(150, 0, 20, 0)); // Increased top margin to move it down
        rightPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Center Panel for login form
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        rightPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label and TextField
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(meriendaFont.deriveFont(Font.PLAIN, 18)); // Using the custom font
        usernameLabel.setForeground(new Color(70, 70, 70)); // Darker gray
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(meriendaFont.deriveFont(Font.PLAIN, 16)); // Using the custom font
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(usernameField, gbc);

        // Password Label and PasswordField
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(meriendaFont.deriveFont(Font.PLAIN, 18)); // Using the custom font
        passwordLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(meriendaFont.deriveFont(Font.PLAIN, 16)); // Using the custom font
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(passwordField, gbc);

        // Buttons Panel (Vertical Arrangement)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Grid with 4 rows and spacing
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(buttonPanel, gbc);

        JButton loginButton = createModernButton("Login");
        JButton clearButton = createModernButton("Clear");
        JButton forgetPasswordButton = createModernButton("Forget Password");
        JButton exitButton = createModernButton("Exit");

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(forgetPasswordButton);
        buttonPanel.add(exitButton);

        // Add button functionality
        addListeners(clearButton, loginButton, forgetPasswordButton, exitButton);

        // Show frame
        frame.setVisible(true);
    }

    private Font loadCustomFont(String path) {
        try {
            // Load the font from the file
            return Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(Font.PLAIN, 16); // Set default size
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Segoe UI", Font.PLAIN, 16); // Fallback font
        }
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 0, 0)); // Black
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void addListeners(JButton clearButton, JButton loginButton, JButton forgetPasswordButton, JButton exitButton) {
        // Clear button functionality
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameField.setText("");
                passwordField.setText("");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check admin credentials first
                if (checkAdminCredentials(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login Successful! Welcome Admin!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Close the current login page
                    new AdminMain().setVisible(true); // Open AdminMain
                    return;
                }

                // Check manager credentials
                if (checkManagerCredentials(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login Successful! Welcome Manager!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    new ManagerMain().setVisible(true); // Redirect to ManagerMain
                    return;
                }
                
                // Check user credentials
                String role = validateLogin(username, password);
                if (role != null) {
                    // Display a success message based on the user's role
                    JOptionPane.showMessageDialog(frame, "Login Successful! Welcome " + role + "!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Redirect to specific pages based on role
                    if (role.equals("Vendor")) {
                        frame.dispose(); // Close the current login page
                        new VendorMain(username).setVisible(true); // Pass the username as the vendor name
                    } else if (role.equals("Customer")) {
                        frame.dispose(); // Close the current login page
                        new CustomerMainPage(username).setVisible(true); // Pass the username as the customer name
                    } else if (role.equals("Runner")) {
                        frame.dispose(); // Close the current login page
                        new RunnerMain(username).setVisible(true); // Pass the username as the runner name
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                }
            }
        });



        // Forget Password button functionality
        forgetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Please contact admin for password recovery.", "Forget Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Exit button functionality
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private String validateLogin(String username, String password) {
        try {
            File file = new File("src/main/resources/txtfile/Users.txt");
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            String storedUsername = null;
            String storedPassword = null;
            String role = null;

            while ((line = reader.readLine()) != null) {
                // Skip divider lines
                if (line.trim().equals("-----------------------")) {
                    continue;
                }

                // Parse fields
                if (line.startsWith("Username:")) {
                    storedUsername = line.split(":", 2)[1].trim();
                } else if (line.startsWith("Password:")) {
                    storedPassword = line.split(":", 2)[1].trim();
                } else if (line.startsWith("Role:")) {
                    role = line.split(":", 2)[1].trim();
                }

                // When all fields are collected, validate the user
                if (storedUsername != null && storedPassword != null && role != null) {
                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        reader.close();
                        return role; // Return role if credentials match
                    }

                    // Reset fields for the next user
                    storedUsername = null;
                    storedPassword = null;
                    role = null;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Return null if no match is found
    }

    private boolean checkAdminCredentials(String username, String password) {
        try {
            File file = new File("src/main/resources/txtfile/Admins.txt");
            if (!file.exists()) {
                System.out.println("Admin file not found: " + file.getAbsolutePath());
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // Read each line of the Admin.txt file
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    String[] credentials = line.split(",");
                    String adminUsername = credentials[0].trim();
                    String adminPassword = credentials[1].trim();

                    // Compare the provided username and password with the current line
                    if (username.equals(adminUsername) && password.equals(adminPassword)) {
                        reader.close();
                        return true;  // Return true if credentials match
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;  // Return false if no match is found
    }

    private boolean checkManagerCredentials(String username, String password) {
    // Hardcoded Manager Credentials
    return username.equals("m") && password.equals("manager123");
    }
    

    public static void main(String[] args) {
        new LoginPage();
    }
}
