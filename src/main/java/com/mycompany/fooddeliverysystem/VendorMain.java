package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class VendorMain extends JFrame {
    private JFrame handleOrdersFrame;
    private String vendorName;
    private JFrame updateOrderStatusFrame;


    public VendorMain(String vendorName) {
        this.vendorName = vendorName;

        // Set Look-and-Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Vendor Panel");
        setSize(1500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load custom font
        Font meriendaFont = loadCustomFont("src/main/resources/Font/Merienda-Black.ttf");

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
        JLabel titleLabel = new JLabel("Welcome, " + vendorName + "!");
        titleLabel.setFont(meriendaFont); // Use the custom font
        titleLabel.setForeground(Color.BLACK); // Change text color to black
        titleBackgroundPanel.add(titleLabel);

        titlePanel.add(titleBackgroundPanel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH); // Place title at the top

        // Create the profile picture panel (left side)
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridBagLayout()); // Use GridBagLayout for precise positioning
        profilePanel.setOpaque(false);
        profilePanel.setPreferredSize(new Dimension(500, 600)); // Set fixed width for profile panel

        // Profile picture square (default is blank)
        JLabel profilePicLabel = new JLabel();
        profilePicLabel.setPreferredSize(new Dimension(180, 180)); // Ensure the profile picture is a fixed 150x150 square
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Border around the profile picture
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        loadProfilePicture(vendorName, profilePicLabel);
        // Set constraints for profile picture placement
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  // Position in the grid (left side)
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 40, 10, 20); // Add margin around the profile picture
        profilePanel.add(profilePicLabel, gbc);

        // Profile picture upload button
        JButton uploadButton = new JButton("Upload Profile Picture");
        uploadButton.addActionListener(e -> uploadProfilePicture(profilePicLabel)); // Set action to upload image
        gbc.gridy = 1; // Move the button below the profile picture
        profilePanel.add(uploadButton, gbc);

        // Add profile panel to the left of the button panel
        backgroundPanel.add(profilePanel, BorderLayout.WEST);

        // Create button panel for action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout to stack buttons vertically
        buttonPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(130, 0, 0, 500)); // Adjust the left margin to move the buttons left


        // Buttons
        JButton manageItemsButton = createModernButton("Manage Items");
        manageItemsButton.addActionListener(e -> openManageItemsPage());
        addHoverEffect(manageItemsButton);

        JButton handleOrdersButton = createModernButton("Handle Orders");
        handleOrdersButton.addActionListener(e -> openHandleOrdersPage());
        addHoverEffect(handleOrdersButton);

        JButton updateOrderStatusButton = createModernButton("Update Order Status");
        updateOrderStatusButton.addActionListener(e -> openUpdateOrderStatusPage());
        addHoverEffect(updateOrderStatusButton);

        JButton checkOrderHistoryButton = createModernButton("Order History");
        checkOrderHistoryButton.addActionListener(e -> openOrderHistoryPage());
        addHoverEffect(checkOrderHistoryButton);

        JButton readReviewsButton = createModernButton("Read Customer Reviews");
        readReviewsButton.addActionListener(e -> openReviewsPage());
        addHoverEffect(readReviewsButton);

        JButton revenueDashboardButton = createModernButton("Revenue Dashboard");
        revenueDashboardButton.addActionListener(e -> openRevenueDashboardPage());
        addHoverEffect(revenueDashboardButton);

        JButton logoutButton = createModernButton("Logout");
        logoutButton.addActionListener(e -> logout());
        addHoverEffect(logoutButton);

        // Add buttons to the button panel
        buttonPanel.add(manageItemsButton);
        buttonPanel.add(handleOrdersButton);
        buttonPanel.add(updateOrderStatusButton);
        buttonPanel.add(checkOrderHistoryButton);
        buttonPanel.add(readReviewsButton);
        buttonPanel.add(revenueDashboardButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the background panel
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // Place buttons in the center

        setVisible(true);
    }
    
    private Font loadCustomFont(String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
            return font.deriveFont(36f); // Set font size to 36
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, 36); // Fallback to Arial if the custom font is not found
        }
    }


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

    private void uploadProfilePicture(JLabel profilePicLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Load the image and set it to the profile picture label
                ImageIcon profileImage = new ImageIcon(selectedFile.getAbsolutePath());
                Image img = profileImage.getImage();
                Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Scale the image to fit the square
                profilePicLabel.setIcon(new ImageIcon(scaledImg)); // Set the image to the label

                // Save the image file path
                saveProfilePicturePath(vendorName, selectedFile.getAbsolutePath());

                // Show a success dialog with the uploaded profile picture
                ImageIcon scaledIcon = new ImageIcon(scaledImg);
                JOptionPane.showMessageDialog(
                    this,
                    "Profile picture uploaded successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE,
                    scaledIcon
                );
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    private void saveProfilePicturePath(String username, String filePath) {
        File originalFile = new File("src/main/resources/txtfile/VendorInfo.txt");
        StringBuilder updatedContent = new StringBuilder();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                updatedContent.append(line).append(System.lineSeparator());
                if (line.equals("Username: " + username)) {
                    userFound = true;
                    updatedContent.append("ProfilePic: ").append(filePath).append(System.lineSeparator());
                    reader.readLine(); // Skip the next line as it's the old profile pic
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading profile data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add new user if not found
        if (!userFound) {
            updatedContent.append("Username: ").append(username).append(System.lineSeparator());
            updatedContent.append("ProfilePic: ").append(filePath).append(System.lineSeparator());
        }

        // Write the updated content back to the same file
        try (FileWriter writer = new FileWriter(originalFile)) {
            writer.write(updatedContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    private void loadProfilePicture(String username, JLabel profilePicLabel) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/VendorInfo.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Username: " + username)) {
                    String profilePicLine = reader.readLine();
                    if (profilePicLine != null && profilePicLine.startsWith("ProfilePic: ")) {
                        String filePath = profilePicLine.substring("ProfilePic: ".length());
                        File imageFile = new File(filePath);
                        if (imageFile.exists()) {
                            ImageIcon profileImage = new ImageIcon(filePath);
                            Image img = profileImage.getImage();
                            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                            profilePicLabel.setIcon(new ImageIcon(scaledImg));
                        } else {
                            JOptionPane.showMessageDialog(this, "Profile picture file not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile picture", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void openManageItemsPage() {
    System.out.println("Opening Manage Items Page for: " + vendorName);
    new VendorManageItems(vendorName).setVisible(true); // Pass vendorName to the VendorManageItems class
    dispose(); // Close the current window if needed
}


private void openHandleOrdersPage() {
    if (handleOrdersFrame != null) {
        handleOrdersFrame.dispose(); // Close the previous instance if it exists
    }

    handleOrdersFrame = new JFrame("Handle Orders");
    handleOrdersFrame.setSize(850, 800);
    handleOrdersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    handleOrdersFrame.setLayout(new BorderLayout());
    handleOrdersFrame.setLocationRelativeTo(null);
    
    // Outer panel with FlowLayout to keep order cards at the top
    JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    outerPanel.setBackground(Color.WHITE);

    // Orders panel with fixed size cards stacked vertically
    JPanel ordersPanel = new JPanel();
    ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
    ordersPanel.setBackground(Color.WHITE);

    // Scroll pane to contain the outer panel
    JScrollPane scrollPane = new JScrollPane(outerPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll speed adjustment

    // Read and display orders
    String ordersFilePath = "src/main/resources/txtfile/Order.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(ordersFilePath))) {
        String line;
        String orderId = null, transactionId = null, customerName = null, vendorName = null, 
               itemDescription = null, orderMethod = null, totalPrice = null, address = null, orderStatus = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) orderId = line.substring(10).trim();
            else if (line.startsWith("Transaction ID: ")) transactionId = line.substring(16).trim();
            else if (line.startsWith("Customer Name: ")) customerName = line.substring(15).trim();
            else if (line.startsWith("Vendor Name: ")) vendorName = line.substring(13).trim();
            else if (line.startsWith("Item Description: ")) itemDescription = line.substring(18).trim();
            else if (line.startsWith("Order Method: ")) orderMethod = line.substring(14).trim();
            else if (line.startsWith("Total Price: ")) totalPrice = line.substring(13).trim();
            else if (line.startsWith("Address: ")) address = line.substring(9).trim();
            else if (line.startsWith("Order Status: ")) orderStatus = line.substring(14).trim();
            else if (line.equals("----------")) {
                // Add order card if vendor matches and status is pending
                if (vendorName != null && vendorName.equals(this.vendorName) && "Pending".equals(orderStatus)) {
                    JPanel orderCard = createOrderCard(orderId, transactionId, customerName, itemDescription, orderMethod, totalPrice, address);
                    ordersPanel.add(orderCard);
                }
                // Reset variables for next order
                orderId = transactionId = customerName = vendorName = itemDescription = orderMethod = totalPrice = address = orderStatus = null;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading orders file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Add the orders panel to the outer panel
    outerPanel.add(ordersPanel);

    handleOrdersFrame.add(scrollPane, BorderLayout.CENTER);
    handleOrdersFrame.setVisible(true);
}



private JPanel createOrderCard(String orderId, String transactionId, String customerName, String itemDescription, String orderMethod, String totalPrice, String address) {
    JPanel orderCard = new JPanel();
    orderCard.setLayout(new BorderLayout());
    orderCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    orderCard.setBackground(Color.WHITE);
    orderCard.setPreferredSize(new Dimension(800, 200)); // Adjust the size of the order card

    // Vendor Profile Image and Description Panel
    JPanel itemPanel = new JPanel();
    itemPanel.setLayout(new BorderLayout());
    JLabel vendorImageLabel = new JLabel();
    vendorImageLabel.setPreferredSize(new Dimension(150, 150));
    vendorImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // Fetch the vendor profile picture
    String vendorImagePath = findVendorProfilePicture(this.vendorName);

    if (vendorImagePath != null) {
        ImageIcon vendorImage = new ImageIcon(vendorImagePath);
        Image scaledImage = vendorImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        vendorImageLabel.setIcon(new ImageIcon(scaledImage));
    } else {
        vendorImageLabel.setText("No Image");
    }
    itemPanel.add(vendorImageLabel, BorderLayout.WEST);

    // Order Details
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    detailsPanel.add(new JLabel("Order ID: " + orderId));
    detailsPanel.add(new JLabel("Transaction ID: " + transactionId));
    detailsPanel.add(new JLabel("Customer: " + customerName));
    detailsPanel.add(new JLabel("Items: " + itemDescription));
    detailsPanel.add(new JLabel("Order Method: " + orderMethod));
    detailsPanel.add(new JLabel("Total Price: " + totalPrice));

    if ("Delivery".equalsIgnoreCase(orderMethod)) {
        detailsPanel.add(new JLabel("Address: " + address));
    }

    itemPanel.add(detailsPanel, BorderLayout.CENTER);

    // Buttons Panel
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton acceptButton = new JButton("Accept Order");
    acceptButton.setBackground(new Color(0, 204, 102));
    acceptButton.setForeground(Color.WHITE);
    acceptButton.addActionListener(e -> {
    try {
        // Update the order status in Order.txt
        if ("Delivery".equalsIgnoreCase(orderMethod)) {
            updateOrderStatusWithDelivery(orderId, orderMethod);
        } else {
            updateOrderStatus(orderId, orderMethod);
        }

        // Send notification to the customer
        String notificationId = generateUniqueId("NOTIFICATION");
        String title = "Order Accepted";
        String detail = "Your order with Order ID: " + orderId + " has been accepted.";
        String datetime = java.time.LocalDateTime.now().toString();
        writeNotificationToFile(notificationId, title, customerName, detail, datetime);

        // Show success message
        JOptionPane.showMessageDialog(this, "Order Accepted: " + orderId, "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh the Handle Orders page
        handleOrdersFrame.dispose(); // Close the Handle Orders window
        openHandleOrdersPage();
    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error accepting order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
});

    JButton rejectButton = new JButton("Reject Order");
    rejectButton.setBackground(new Color(255, 77, 77));
    rejectButton.setForeground(Color.WHITE);
    rejectButton.addActionListener(e -> {
    try {
        // Directly handle the rejection logic for delivery or non-delivery orders
        if ("Delivery".equalsIgnoreCase(orderMethod)) {
            handleDeliveryOrderRejection(orderId, transactionId, customerName, totalPrice);
        } else {
            handleNonDeliveryOrderRejection(orderId, customerName, totalPrice);
        }

        // Show success message
        JOptionPane.showMessageDialog(this, "Order Rejected: " + orderId, "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh the Handle Orders page
        handleOrdersFrame.dispose();
        openHandleOrdersPage();
    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error rejecting order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
});


    buttonsPanel.add(acceptButton);
    buttonsPanel.add(rejectButton);

    orderCard.add(itemPanel, BorderLayout.CENTER);
    orderCard.add(buttonsPanel, BorderLayout.SOUTH);

    return orderCard;
}

private void handleNonDeliveryOrderRejection(String orderId, String customerName, String totalPrice) throws IOException {
    // Update order status in Order.txt
    updateOrderStatusToRejected(orderId);

    // Refund the total price to the customer
    double refundAmount = Double.parseDouble(totalPrice.replace("RM", "").trim());
    refundToCustomer(customerName, refundAmount);

    // Write transaction record
    String transactionId = generateUniqueId("TRANSACTION");
    writeRejectedTransactionToFile(transactionId, customerName, orderId, refundAmount);

    // Send notification
    String notificationId = generateUniqueId("NOTIFICATION");
    String title = "Order Rejected by Vendor";
    String detail = "Your order with Order ID: " + orderId + " has been rejected. A refund of RM" 
                    + String.format("%.2f", refundAmount) + " has been processed.";
    String datetime = java.time.LocalDateTime.now().toString();
    writeNotificationToFile(notificationId, title, customerName, detail, datetime);
}

private void handleDeliveryOrderRejection(String orderId, String transactionId, String customerName, String totalPrice) throws IOException {
    // Update order status in Order.txt
    updateOrderStatusToRejected(orderId);

    // Refund calculation: total price of the order + RM5 delivery fee
    double refundAmount = Double.parseDouble(totalPrice.replace("RM", "").trim()) + 5.0;

    // Refund the total amount to the customer
    refundToCustomer(customerName, refundAmount);

    // Write transaction record
    String newTransactionId = generateUniqueId("TRANSACTION");
    writeRejectedTransactionToFile(newTransactionId, customerName, orderId, refundAmount);

    // Send notification
    String notificationId = generateUniqueId("NOTIFICATION");
    String title = "Order Rejected";
    String detail = "Your delivery order with Order ID: " + orderId + " has been rejected. A refund of RM" 
                    + String.format("%.2f", refundAmount) + " (including RM5 delivery fee) has been processed.";
    String datetime = java.time.LocalDateTime.now().toString();
    writeNotificationToFile(notificationId, title, customerName, detail, datetime);
}

private void writeRejectedTransactionToFile(String transactionId, String customerName, String orderId, double refundAmount) {
    String transactionFilePath = "src/main/resources/txtfile/Transaction.txt";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionFilePath, true))) {
        writer.write("Transaction ID: " + transactionId + "\n");
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Order ID: " + orderId + "\n");
        writer.write("Transaction Type: Refund\n");
        writer.write("Refund Amount: RM" + String.format("%.2f", refundAmount) + "\n");
        writer.write("Datetime: " + java.time.LocalDateTime.now() + "\n");
        writer.write("----------\n");
    } catch (IOException e) {
        System.err.println("Error writing to Transaction.txt: " + e.getMessage());
        e.printStackTrace();
    }
}


private void updateOrderStatusToRejected(String orderId) throws IOException {
    String orderFilePath = "src/main/resources/txtfile/Order.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(orderFilePath))) {
        String line;
        boolean isInOrderBlock = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) {
                if (line.substring(10).equals(orderId)) {
                    isInOrderBlock = true;
                }
            }

            if (isInOrderBlock && line.startsWith("Order Status: ")) {
                updatedContent.append("Order Status: Rejected\n"); // Update status to Rejected
                isInOrderBlock = false;
                continue;
            }

            updatedContent.append(line).append("\n");
        }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFilePath))) {
        writer.write(updatedContent.toString());
    }
}

private void refundToCustomer(String customerName, double refundAmount) throws IOException {
    String userFilePath = "src/main/resources/txtfile/Users.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
        String line;
        boolean isUserFound = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).trim().equals(customerName)) {
                isUserFound = true;
                updatedContent.append(line).append("\n");
            } else if (isUserFound && line.startsWith("Credit: RM")) {
                double currentBalance = Double.parseDouble(line.substring(10).trim());
                double newBalance = currentBalance + refundAmount;
                updatedContent.append("Credit: RM").append(String.format("%.2f", newBalance)).append("\n");
                isUserFound = false; // Reset user found flag
            } else {
                updatedContent.append(line).append("\n");
            }
        }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFilePath))) {
        writer.write(updatedContent.toString());
    }
}


private String generateUniqueId(String prefix) {
    return prefix + System.currentTimeMillis();
}
private String findVendorProfilePicture(String vendorName) {
    String vendorInfoFile = "src/main/resources/txtfile/VendorInfo.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(vendorInfoFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).trim().equals(vendorName)) {
                line = reader.readLine();
                if (line != null && line.startsWith("ProfilePic: ")) {
                    return line.substring(12).trim();
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return null; // Return null if no profile picture is found
}
private void updateOrderStatus(String orderId, String orderMethod) throws IOException {
    String orderFilePath = "src/main/resources/txtfile/Order.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(orderFilePath))) {
        String line;
        boolean isInOrderBlock = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) {
                if (line.substring(10).equals(orderId)) {
                    isInOrderBlock = true;
                }
            }

            if (isInOrderBlock && line.startsWith("Order Status: ")) {
                updatedContent.append("Order Status: Accepted\n"); // Update status to Accepted
                isInOrderBlock = false;
                continue;
            }

            updatedContent.append(line).append("\n");
        }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFilePath))) {
        writer.write(updatedContent.toString());
    }
}
private void updateOrderStatusWithDelivery(String orderId, String orderMethod) throws IOException {
    String orderFilePath = "src/main/resources/txtfile/Order.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(orderFilePath))) {
        String line;
        boolean isInOrderBlock = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) {
                if (line.substring(10).equals(orderId)) {
                    isInOrderBlock = true;
                }
            }

            if (isInOrderBlock && line.startsWith("Order Status: ")) {
                updatedContent.append("Order Status: Accepted\n"); // Update status to Accepted
                updatedContent.append("Allocated Runner: ").append(allocateRunner()).append("\n");
                updatedContent.append("Runner Status: Pending\n");
                isInOrderBlock = false;
                continue;
            }

            updatedContent.append(line).append("\n");
        }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFilePath))) {
        writer.write(updatedContent.toString());
    }
}

private void writeNotificationToFile(String notificationId, String title, String customerName, String detail, String dateTime) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Notification.txt", true))) {
        writer.write("Notification ID: " + notificationId + "\n");
        writer.write("Title: "+ title + "\n");
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Notification Detail: " + detail + "\n");
        writer.write("Datetime: " + dateTime + "\n");
        writer.write("----------\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private String allocateRunner() {
    String usersFilePath = "src/main/resources/txtfile/Users.txt";
    java.util.List<String> runners = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
        String line;
        String currentUsername = null;

        while ((line = reader.readLine()) != null) {
            // Check for Username
            if (line.startsWith("Username: ")) {
                currentUsername = line.substring(10).trim();
            } 
            // Check for Role corresponding to the Username
            else if (line.startsWith("Role: ") && line.substring(6).equalsIgnoreCase("Runner")) {
                // Add the runner's username to the list
                if (currentUsername != null) {
                    runners.add(currentUsername);
                }
            }
        }

        if (runners.isEmpty()) {
            throw new IOException("No available runners found in Users.txt.");
        }

    } catch (IOException e) {
        System.err.println("Error reading Users.txt: " + e.getMessage());
        e.printStackTrace();
    }

    // Randomly select a runner from the list
    if (!runners.isEmpty()) {
        int randomIndex = new java.util.Random().nextInt(runners.size());
        return runners.get(randomIndex);
    }

    return null; // Fallback in case no runners are available
}


private void openUpdateOrderStatusPage() {
    // Dispose the previous frame if it exists
    if (updateOrderStatusFrame != null) {
        updateOrderStatusFrame.dispose();
    }

    updateOrderStatusFrame = new JFrame("Update Order Status");
    updateOrderStatusFrame.setSize(850, 800);
    updateOrderStatusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    updateOrderStatusFrame.setLayout(new BorderLayout());
    updateOrderStatusFrame.setLocationRelativeTo(null);

    // Outer panel with FlowLayout to prevent stretching
    JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    outerPanel.setBackground(Color.WHITE);

    // Orders panel with BoxLayout for stacking cards
    JPanel ordersPanel = new JPanel();
    ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
    ordersPanel.setBackground(Color.WHITE);

    // Scroll pane to contain the outer panel
    JScrollPane scrollPane = new JScrollPane(outerPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Increase scroll speed

    // Read and display orders
    String ordersFilePath = "src/main/resources/txtfile/Order.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(ordersFilePath))) {
        String line;
        String orderId = null, transactionId = null, customerName = null, vendorName = null,
               itemDescription = null, orderMethod = null, totalPrice = null, address = null, orderStatus = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) orderId = line.substring(10).trim();
            else if (line.startsWith("Transaction ID: ")) transactionId = line.substring(16).trim();
            else if (line.startsWith("Customer Name: ")) customerName = line.substring(15).trim();
            else if (line.startsWith("Vendor Name: ")) vendorName = line.substring(13).trim();
            else if (line.startsWith("Item Description: ")) itemDescription = line.substring(18).trim();
            else if (line.startsWith("Order Method: ")) orderMethod = line.substring(14).trim();
            else if (line.startsWith("Total Price: ")) totalPrice = line.substring(13).trim();
            else if (line.startsWith("Address: ")) address = line.substring(9).trim();
            else if (line.startsWith("Order Status: ")) orderStatus = line.substring(14).trim();
            else if (line.equals("----------")) {
                // Display orders that are accepted and match the vendor
                if (vendorName != null && vendorName.equals(this.vendorName) && "Accepted".equals(orderStatus)) {
                    JPanel orderCard = createUpdateOrderCard(orderId, transactionId, customerName, itemDescription, orderMethod, totalPrice, address);
                    ordersPanel.add(orderCard);
                }
                // Reset variables for next order
                orderId = transactionId = customerName = vendorName = itemDescription = orderMethod = totalPrice = address = orderStatus = null;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading orders file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Add orders to the outer panel
    outerPanel.add(ordersPanel);

    updateOrderStatusFrame.add(scrollPane, BorderLayout.CENTER);
    updateOrderStatusFrame.setVisible(true);
}


    
    private JPanel createUpdateOrderCard(String orderId, String transactionId, String customerName, String itemDescription, String orderMethod, String totalPrice, String address) {
    JPanel orderCard = new JPanel();
    orderCard.setLayout(new BorderLayout());
    orderCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    orderCard.setBackground(Color.WHITE);
    orderCard.setPreferredSize(new Dimension(800, 200)); // Fixed card size

    // Order Details Panel
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    detailsPanel.add(new JLabel("Order ID: " + orderId));
    detailsPanel.add(new JLabel("Transaction ID: " + transactionId));
    detailsPanel.add(new JLabel("Customer: " + customerName));
    detailsPanel.add(new JLabel("Items: " + itemDescription));
    detailsPanel.add(new JLabel("Order Method: " + orderMethod));
    detailsPanel.add(new JLabel("Total Price: " + totalPrice));

    if ("Delivery".equalsIgnoreCase(orderMethod)) {
        detailsPanel.add(new JLabel("Address: " + address));
    }

    orderCard.add(detailsPanel, BorderLayout.CENTER);

    // Buttons Panel
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton markAsCompletedButton = new JButton("Mark as Completed");
    markAsCompletedButton.setBackground(new Color(0, 204, 102));
    markAsCompletedButton.setForeground(Color.WHITE);
    markAsCompletedButton.addActionListener(e -> {
        try {
            // Update the order status in Order.txt
            String completionDate = java.time.LocalDateTime.now().toString();
            markOrderAsCompleted(orderId, completionDate);

            // Send notification to the customer
            String notificationId = generateUniqueId("NOTIFICATION");
            String title = "Order Completed by Vendor";
            String detail = getPoliteNotificationDetail(orderMethod, orderId);
            String datetime = java.time.LocalDateTime.now().toString();
            writeNotificationToFile(notificationId, title, customerName, detail, datetime);

            // Show success message
            JOptionPane.showMessageDialog(this, "Order Marked as Completed: " + orderId, "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the Update Order Status page
            if (updateOrderStatusFrame != null) {
                updateOrderStatusFrame.dispose(); // Close the Update Order Status window
            }
            openUpdateOrderStatusPage(); // Open the page again
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error marking order as completed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    buttonsPanel.add(markAsCompletedButton);

    orderCard.add(buttonsPanel, BorderLayout.SOUTH);

    return orderCard;
}

private String getPoliteNotificationDetail(String orderMethod, String orderId) {
    switch (orderMethod.toLowerCase()) {
        case "dine-in":
            return "Your order with Order ID: " + orderId + " has been completed. Your meal is ready and waiting for you to enjoy at the restaurant. Thank you for dining with us!";
        case "take away":
            return "Your order with Order ID: " + orderId + " has been completed. Please collect your order at the counter. Thank you for choosing us!";
        case "delivery":
            return "Your order with Order ID: " + orderId + " has been completed. Kindly wait as our runner delivers your food to your address. Thank you for your patience and support!";
        default:
            return "Your order with Order ID: " + orderId + " has been completed. Thank you for ordering with us!";
    }
}


private void markOrderAsCompleted(String orderId, String completionDate) throws IOException {
    String orderFilePath = "src/main/resources/txtfile/Order.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(orderFilePath))) {
        String line;
        boolean isInOrderBlock = false;
        boolean isDeliveryOrder = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ")) {
                if (line.substring(10).equals(orderId)) {
                    isInOrderBlock = true;
                }
            }

            // Check the Order Method within the block
            if (isInOrderBlock && line.startsWith("Order Method: ")) {
                String orderMethod = line.substring(14).trim();
                if (orderMethod.equalsIgnoreCase("Delivery")) {
                    isDeliveryOrder = true; // Mark as delivery, no Date Completed will be added
                }
            }

            if (isInOrderBlock && line.startsWith("Order Status: ")) {
                updatedContent.append("Order Status: Completed\n"); // Update status to Completed
                // Add Date Completed only if it's not a Delivery order
                if (!isDeliveryOrder) {
                    updatedContent.append("Date Completed: ").append(completionDate).append("\n");
                }
                isInOrderBlock = false;
                isDeliveryOrder = false; // Reset for the next order
                continue;
            }

            updatedContent.append(line).append("\n");
        }
    }

    // Write the updated content back to the file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFilePath))) {
        writer.write(updatedContent.toString());
    }
}





    private void openOrderHistoryPage() {
        System.out.println("Opening Order History for: " + vendorName);
        new VendorOrderHistory(vendorName).setVisible(true);
        this.dispose();
    }

private void openReviewsPage() {
    new VendorReviewPage(vendorName).setVisible(true);
    // Optionally, dispose of the current window if needed
    this.dispose();
}

    private void openRevenueDashboardPage() {
        System.out.println("Opening Rvenue Dashboard for: " + vendorName);
        new VendorRevenueDashboard(vendorName).setVisible(true); 
    }

    private void logout() {
        System.out.println("Logging out...");
        // Dispose the current frame (close the VendorMain page)
        dispose();

        // Open the login page
        new LoginPage(); // Replace this with the actual class name for your login page
    }


    public static void main(String[] args) {
        new VendorMain("McDonald's"); // Replace with the actual vendor name
    }
}

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Load the background image from a file path (ensure the path is correct)
            try {
                backgroundImage = new ImageIcon("src/main/resources/pic/background1.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image scaled to fit the panel size
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
