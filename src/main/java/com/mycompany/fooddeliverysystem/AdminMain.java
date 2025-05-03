package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminMain extends JFrame {

    public AdminMain() {
        // Set Look-and-Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Admin Panel");
        setSize(1500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load custom font
        Font meriendaFont = loadCustomFont("src/main/resources/Font/Merienda-Black.ttf").deriveFont(48f); // Adjust the font size to 48

        // Set Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout()); // Use BorderLayout for positioning
        add(backgroundPanel);

        // Title Panel with Semi-Transparent White Background
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false); // Make the title panel transparent

        // Create a panel with a semi-transparent white background
        JPanel titleBackgroundPanel = new JPanel();
        titleBackgroundPanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent white background
        titleBackgroundPanel.setOpaque(true); // Make sure it is opaque so it shows the background color

        // Create the title label with the custom font
        JLabel titleLabel = new JLabel("Welcome, Admin!");
        titleLabel.setFont(meriendaFont); // Use the custom font
        titleLabel.setForeground(Color.BLACK); // Change text color to black
        titleBackgroundPanel.add(titleLabel);

        titlePanel.add(titleBackgroundPanel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH); // Place title at the top

        // Create button panel for action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout to stack buttons vertically
        buttonPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0)); // Adjust the left margin to move the buttons left

        // Buttons
        JButton manageUserButton = createModernButton("Manage Users");
        manageUserButton.addActionListener(e -> openManageUsersPage());
        addHoverEffect(manageUserButton);

        JButton topUpCreditButton = createModernButton("Top Up Customer Credit");
        topUpCreditButton.addActionListener(e -> openTopUpCreditPage());
        addHoverEffect(topUpCreditButton);

        JButton generateReceiptButton = createModernButton("Generate Receipt");
        generateReceiptButton.addActionListener(e -> openGenerateReceiptPage());
        addHoverEffect(generateReceiptButton);

        JButton logoutButton = createModernButton("Logout");
        logoutButton.addActionListener(e -> logout());
        addHoverEffect(logoutButton);

        // Add buttons to the button panel
        buttonPanel.add(manageUserButton);
        buttonPanel.add(topUpCreditButton);
        buttonPanel.add(generateReceiptButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the background panel
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // Place buttons in the center

        setVisible(true);
    }

    // Helper method to create modern-styled buttons
    private JButton createModernButton(String text) {
        JButton button = new JButton(text);

        // Set the background color to white
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK); // Text color set to black for contrast

        // Set font and button size
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(350, 50));  // Control the button size
        button.setMaximumSize(new Dimension(350, 50));

        // Center align the button in the panel
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add padding and a border to the button for better spacing
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Ensure the text stays centered inside the button
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        return button;
    }
    
    // Helper method for hover effect
    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change the background color when mouse enters
                button.setBackground(new Color(255, 204, 102)); // Light orange background on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert the background color when mouse exits
                button.setBackground(Color.WHITE); // Original white background
            }
        });
    }

    // Methods to open the corresponding pages
    private void openManageUsersPage() {
        // Open AdminManageUser window by calling its main method
        SwingUtilities.invokeLater(() -> AdminManageUser.main(new String[]{})); // Call the main method of AdminManageUser
        dispose(); // Optionally close the current AdminMain window
    }

    private void openTopUpCreditPage() {
        // Open Top Up Customer Credit page
        SwingUtilities.invokeLater(() -> AdminTopUpCustomerCredit.main(new String[]{}));
        dispose(); // Optionally close the current AdminMain window
    }

    private void openGenerateReceiptPage() {
        // Open Generate Receipt page
        SwingUtilities.invokeLater(() -> AdminGenerateReceipt.main(new String[]{}));
        dispose(); // Optionally close the current AdminMain window
    }

    private void logout() {
    // Logic for logout
    JOptionPane.showMessageDialog(this, "Logging out...");
    // Close the current AdminMain window
    dispose();
    // Open the login page by calling the main method of the LoginPage class
    SwingUtilities.invokeLater(() -> LoginPage.main(new String[]{})); // Replace LoginPage with your actual login page class name
}


    // Method to load custom font
    private Font loadCustomFont(String path) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 16); // Fallback font
        }
    }

    // Main method to launch AdminMain directly
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMain::new);
    }
}