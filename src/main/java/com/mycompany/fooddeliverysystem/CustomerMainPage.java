package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CustomerMainPage extends javax.swing.JFrame {
    private String customerName;

    public CustomerMainPage(String customerName) {
    this.customerName = customerName;
    initComponents();
    Username.setText(customerName); // Set the username on the JLabel
    loadUserBalance(); // Load and display the balance
    loadUserDefaultAddress(); // Load and display the default address
    setupCustomerBackground(); // Set up the vendor cards and "View Cart" button
    this.setLocationRelativeTo(null);
    }

    
    private void setupCustomerBackground() {
        CustomerBackground.setLayout(new BorderLayout());

        // Create a "View Cart" button at the top
        GradientButton viewCartButton = new GradientButton("View Cart");
        viewCartButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewCartButton.setPreferredSize(new Dimension(CustomerBackground.getWidth(), 50));
        viewCartButton.addActionListener(e -> viewCart()); // Calls the defined method

        // Add the button to the top of the CustomerBackground panel
        CustomerBackground.add(viewCartButton, BorderLayout.NORTH);

        // Create a scrollable panel for displaying vendor cards
        JPanel vendorCardsPanel = new JPanel();
        vendorCardsPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, dynamic rows
        vendorCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load vendor cards dynamically
        loadVendorCards(vendorCardsPanel);

        // Wrap the vendorCardsPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(vendorCardsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add the scroll pane to the center of the CustomerBackground panel
        CustomerBackground.add(scrollPane, BorderLayout.CENTER);
    }


class CartItem {
    private String cartId; // Unique Cart ID
    private String vendorName;
    private String foodName;
    private int quantity;
    private double pricePerItem;
    private String picturePath;

    public CartItem(String cartId, String vendorName, String foodName, int quantity, double pricePerItem, String picturePath) {
        this.cartId = cartId;
        this.vendorName = vendorName;
        this.foodName = foodName;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.picturePath = picturePath;
    }

    public String getCartId() {
        return cartId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public double getTotalPrice() {
        return quantity * pricePerItem;
    }

    public String getPicturePath() {
        return picturePath;
    }
}


private void viewCart() {
    JFrame cartFrame = new JFrame("View Cart");
    cartFrame.setSize(1000, 800);
    cartFrame.setLayout(new BorderLayout());
    cartFrame.setLocationRelativeTo(null);

    JPanel cartPanel = new JPanel();
    cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(cartPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    // Read cart data
    Map<String, List<CartItem>> cartItemsByVendor = loadCartData();

    // Wrap totalCartPrice in a single-element array to make it effectively final
    final double[] totalCartPrice = {0.0};

    // Create a new Map to store updated cart data for deletion purposes
    Map<String, List<CartItem>> updatedCartItemsByVendor = new HashMap<>(cartItemsByVendor);

    for (String vendorName : cartItemsByVendor.keySet()) {
        JPanel vendorPanel = new JPanel(new BorderLayout());
        vendorPanel.setBorder(BorderFactory.createTitledBorder(vendorName));

        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        double vendorTotalPrice = 0.0;

        for (CartItem item : cartItemsByVendor.get(vendorName)) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel pictureLabel = new JLabel();
            pictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                ImageIcon foodImage = new ImageIcon(item.getPicturePath());
                if (foodImage.getIconWidth() > 0) {
                    Image scaledImage = foodImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    pictureLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    pictureLabel.setText("No Image");
                }
            } catch (Exception e) {
                pictureLabel.setText("No Image");
            }

            JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
            detailsPanel.add(new JLabel("Food: " + item.getFoodName()));
            detailsPanel.add(new JLabel("Quantity: " + item.getQuantity()));
            detailsPanel.add(new JLabel("Price per Item: RM" + String.format("%.2f", item.getPricePerItem())));
            detailsPanel.add(new JLabel("Total Price: RM" + String.format("%.2f", item.getTotalPrice())));

            // Add delete button for this item
            JButton deleteButton = new JButton("Delete");
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBackground(Color.RED);
            deleteButton.addActionListener(e -> {
                // Remove the item from the cart using its Cart ID
                String cartId = item.getCartId(); // Assume `CartItem` has a `getCartId()` method
                deleteItemFromCart(cartId);

                // Refresh the Cart view
                JOptionPane.showMessageDialog(cartFrame, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cartFrame.dispose();
                viewCart(); // Reload the Cart
            });


            // Add components to the item panel
            JPanel actionPanel = new JPanel(new BorderLayout());
            actionPanel.add(detailsPanel, BorderLayout.CENTER);
            actionPanel.add(deleteButton, BorderLayout.EAST);

            itemPanel.add(pictureLabel, BorderLayout.WEST);
            itemPanel.add(actionPanel, BorderLayout.CENTER);
            itemsPanel.add(itemPanel);

            vendorTotalPrice += item.getTotalPrice();
        }

        vendorPanel.add(itemsPanel, BorderLayout.CENTER);
        cartPanel.add(vendorPanel);
        totalCartPrice[0] += vendorTotalPrice;
    }

    JLabel totalPriceLabel = new JLabel("Total Price: RM" + String.format("%.2f", totalCartPrice[0]));
    JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    totalPanel.add(totalPriceLabel);

    // Selection panel for Dine-In, Take Away, and Delivery
    JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
    JRadioButton dineInButton = new JRadioButton("Dine-In");
    JRadioButton takeAwayButton = new JRadioButton("Take Away");
    JRadioButton deliveryButton = new JRadioButton("Delivery");

    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(dineInButton);
    buttonGroup.add(takeAwayButton);
    buttonGroup.add(deliveryButton);

    selectionPanel.add(dineInButton);
    selectionPanel.add(takeAwayButton);
    selectionPanel.add(deliveryButton);

    // Address selection panel
    JPanel addressPanel = new JPanel(new FlowLayout());
    JLabel addressLabel = new JLabel("Choose Address:");
    JComboBox<String> addressComboBox = new JComboBox<>();
    addressComboBox.addItem("Default Address");
    addressComboBox.addItem("Home Address");
    addressComboBox.addItem("Work Address");
    addressComboBox.addItem("Other Address");
    addressPanel.add(addressLabel);
    addressPanel.add(addressComboBox);
    addressPanel.setVisible(false);

    deliveryButton.addActionListener(e -> {
    addressPanel.setVisible(true); // Show the address selection panel for delivery

    // Calculate the delivery fee based on the number of vendors
    double deliveryFee = cartItemsByVendor.size() * 5.0; // RM5 per vendor

    // Update the total price to include the delivery fee
    double newTotal = totalCartPrice[0] + deliveryFee;

    // Update the total price label
    totalPriceLabel.setText("Total Price: RM" + String.format("%.2f", newTotal));

    // Notify the user about the delivery fee
    JOptionPane.showMessageDialog(cartFrame, 
        "A delivery fee of RM" + String.format("%.2f", deliveryFee) + 
        " has been applied for " + cartItemsByVendor.size() + " vendor(s).", 
        "Delivery Fee", 
        JOptionPane.INFORMATION_MESSAGE);
});


    dineInButton.addActionListener(e -> {
        addressPanel.setVisible(false);
        totalPriceLabel.setText("Total Price: RM" + String.format("%.2f", totalCartPrice[0]));
    });

    takeAwayButton.addActionListener(e -> {
        addressPanel.setVisible(false);
        totalPriceLabel.setText("Total Price: RM" + String.format("%.2f", totalCartPrice[0]));
    });

    // Pay button with increased size
    JButton payButton = new JButton("Pay");
    payButton.setPreferredSize(new Dimension(200, 60)); // Larger button size
    payButton.setFont(new Font("Arial", Font.BOLD, 20)); // Bigger font
    payButton.setBackground(new Color(255, 140, 0));
    payButton.setForeground(Color.WHITE);
    payButton.setFocusPainted(false);
payButton.addActionListener(e -> {
    // Ensure a valid option is selected
    if (!dineInButton.isSelected() && !takeAwayButton.isSelected() && !deliveryButton.isSelected()) {
        JOptionPane.showMessageDialog(cartFrame, "Please select an option before proceeding to pay.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Fetch the customer balance
    double customerBalance = getUserBalance(customerName);

    // Calculate delivery fee based on the number of vendors if "Delivery" is selected
    double deliveryFee = 0.0;
    if (deliveryButton.isSelected()) {
        deliveryFee = cartItemsByVendor.size() * 5.0; // RM5 per vendor
        JOptionPane.showMessageDialog(cartFrame, "A delivery fee of RM" + String.format("%.2f", deliveryFee) + " has been applied for " + cartItemsByVendor.size() + " vendor(s).", "Delivery Fee", JOptionPane.INFORMATION_MESSAGE);
    }

    // Calculate the total price including delivery fee (if applicable)
    double totalPrice = totalCartPrice[0] + deliveryFee;

    // Check if the customer has sufficient balance
    if (customerBalance < totalPrice) {
        JOptionPane.showMessageDialog(cartFrame, "Insufficient balance to proceed with payment.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Deduct the total amount (including delivery fee) from the customer's balance
    updateUserBalance(customerName, customerBalance - totalPrice);

    // Determine the order method and selected address (for Delivery only)
    String selectedOrderMethod = dineInButton.isSelected() ? "Dine-In" : takeAwayButton.isSelected() ? "Take Away" : "Delivery";
    String selectedAddress = deliveryButton.isSelected() ? addressComboBox.getSelectedItem().toString() : "N/A";

    // Generate a single Transaction ID for all orders in this transaction
    String transactionId = generateUniqueId("TRANSACTION");

    // Generate and save orders for each vendor
    for (String vendorName : cartItemsByVendor.keySet()) {
        List<CartItem> items = cartItemsByVendor.get(vendorName);
        String orderId = generateUniqueId("ORDER");
        String vendorItemDescription = generateItemDescription(items);
        writeOrderToFile(orderId, transactionId, customerName, vendorName, vendorItemDescription, selectedOrderMethod, calculateVendorTotal(items), selectedAddress);
    }

    StringBuilder transactionItemDescription = new StringBuilder();
    for (String vendorName : cartItemsByVendor.keySet()) {
        List<CartItem> items = cartItemsByVendor.get(vendorName);
        for (CartItem item : items) {
            transactionItemDescription.append(item.getFoodName())
                                      .append(" x ")
                                      .append(item.getQuantity())
                                      .append(", ");
        }
    }
    // Remove trailing comma and space for transaction description
    if (transactionItemDescription.length() > 0) {
        transactionItemDescription.setLength(transactionItemDescription.length() - 2);
    }


    writeTransactionToFile(transactionId, customerName, transactionItemDescription.toString(), totalPrice, LocalDateTime.now().toString());

    // Update Cart.txt to mark items as completed
    updateCartStatusToCompleted(customerName, cartItemsByVendor);

    // Send payment notification
    String notificationId = generateUniqueId("NOTIFICATION");
    String paymentDetail = "Payment of RM" + String.format("%.2f", totalPrice) + " (including RM" + String.format("%.2f", deliveryFee) + " delivery fee) completed successfully.";
    writeNotificationToFile(notificationId, customerName, paymentDetail, LocalDateTime.now().toString());

    // Display success message
    JOptionPane.showMessageDialog(cartFrame, "Payment completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    cartFrame.dispose(); // Close the cart frame
});


    // Main action panel using GridBagLayout for flexibility
    JPanel actionPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = 0;
    actionPanel.add(selectionPanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    actionPanel.add(addressPanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    actionPanel.add(payButton, gbc);

    cartFrame.add(scrollPane, BorderLayout.CENTER);
    cartFrame.add(totalPanel, BorderLayout.NORTH);
    cartFrame.add(actionPanel, BorderLayout.SOUTH);

    cartFrame.setVisible(true);
}

private double calculateVendorTotal(List<CartItem> items) {
    return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
}


private double getUserBalance(String customerName) {
    String filePath = "src/main/resources/txtfile/Users.txt";
    double balance = 0.0;

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        boolean userFound = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                userFound = true; // User found
            } else if (userFound && line.startsWith("Credit: RM")) {
                String balanceString = line.substring(10).trim(); // Extract balance
                balance = Double.parseDouble(balanceString);
                break; // Exit loop after finding the balance
            }
        }

        if (!userFound) {
            throw new IOException("User not found: " + customerName);
        }

    } catch (IOException e) {
        System.err.println("Error reading user data: " + e.getMessage());
        e.printStackTrace();
    }

    return balance;
}

private void updateUserBalance(String customerName, double newBalance) {
    String filePath = "src/main/resources/txtfile/Users.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        boolean userFound = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                userFound = true;
                updatedContent.append(line).append("\n");
            } else if (userFound && line.startsWith("Credit: RM")) {
                updatedContent.append("Credit: RM").append(String.format("%.2f", newBalance)).append("\n");
                userFound = false; // Reset userFound flag after updating balance
            } else {
                updatedContent.append(line).append("\n");
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading user data for update: " + e.getMessage());
        e.printStackTrace();
    }

    // Write updated content back to the file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(updatedContent.toString());
    } catch (IOException e) {
        System.err.println("Error writing updated user data: " + e.getMessage());
        e.printStackTrace();
    }
}

private void updateCartStatusToCompleted(String customerName, Map<String, List<CartItem>> cartItemsByVendor) {
    String cartFilePath = "src/main/resources/txtfile/Cart.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(cartFilePath))) {
        String line;
        String currentVendorName = null;
        String currentCustomerName = null;
        StringBuilder currentItemBlock = new StringBuilder();
        boolean isRelevantBlock = false;

        while ((line = reader.readLine()) != null) {
            // Start of a new block (CART ID or Vendor Name)
            if (line.startsWith("CART") || line.startsWith("Vendor Name: ")) {
                if (currentItemBlock.length() > 0) {
                    // Append the current block before starting a new one
                    updatedContent.append(currentItemBlock);
                }
                // Reset the current block
                currentItemBlock.setLength(0);
                isRelevantBlock = false; // Reset relevance flag
            }

            // Add the line to the current block
            currentItemBlock.append(line).append("\n");

            if (line.startsWith("Vendor Name: ")) {
                currentVendorName = line.substring(13).trim();
            } else if (line.startsWith("Customer Name: ")) {
                currentCustomerName = line.substring(15).trim();
            } else if (line.startsWith("Status: ")) {
                // Check if this block matches the criteria
                if (currentCustomerName != null && currentCustomerName.equals(customerName) &&
                        currentVendorName != null && cartItemsByVendor.containsKey(currentVendorName)) {
                    isRelevantBlock = true;
                }
            } else if (line.equals("----------") && isRelevantBlock) {
                // If the block is relevant, update the status before the separator
                String updatedBlock = currentItemBlock.toString().replaceFirst("(?m)^Status: .*", "Status: Payment Completed");
                updatedContent.append(updatedBlock);
                currentItemBlock.setLength(0); // Clear the block after appending
                isRelevantBlock = false; // Reset relevance flag
            }
        }

        // Append the last block if any
        if (currentItemBlock.length() > 0) {
            updatedContent.append(currentItemBlock);
        }

    } catch (IOException e) {
        System.err.println("Error reading Cart.txt: " + e.getMessage());
        e.printStackTrace();
    }

    // Write the updated content back to Cart.txt
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFilePath))) {
        writer.write(updatedContent.toString());
    } catch (IOException e) {
        System.err.println("Error writing to Cart.txt: " + e.getMessage());
        e.printStackTrace();
    }
}





private String generateUniqueId(String prefix) {
    return prefix + System.currentTimeMillis();
}

private void writeTransactionToFile(String transactionId, String customerName, 
                                    String itemDescription, double totalAmount, 
                                    String dateTime) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Transaction.txt", true))) {
        writer.write("Transaction ID: " + transactionId + "\n");
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Item Description: " + itemDescription + "\n");
        writer.write("Transaction Type: Payment\n"); // Add Transaction Type
        writer.write("Total Amount: -RM" + String.format("%.2f", totalAmount) + "\n"); // Add - before RM
        writer.write("Datetime: " + dateTime + "\n");
        writer.write("----------\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


private void writeNotificationToFile(String notificationId, String customerName, String detail, String dateTime) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Notification.txt", true))) {
        // Append the notification for the payment
        writer.write("Notification ID: " + notificationId + "\n");
        writer.write("Title: Order Successfully" + "\n");
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Notification Detail: " + detail + "\n");
        writer.write("Datetime: " + dateTime + "\n");
        writer.write("----------\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private String generateItemDescription(List<CartItem> items) {
    StringBuilder description = new StringBuilder();
    for (CartItem item : items) {
        description.append(item.getFoodName()).append(" x ").append(item.getQuantity()).append(", ");
    }
    // Remove the trailing comma and space
    if (description.length() > 0) {
        description.setLength(description.length() - 2);
    }
    return description.toString();
}

private String generateItemDescriptionWithVendor(Map<String, List<CartItem>> cartItemsByVendor) {
    StringBuilder description = new StringBuilder();
    for (String vendorName : cartItemsByVendor.keySet()) {
        List<CartItem> items = cartItemsByVendor.get(vendorName);
        for (CartItem item : items) {
            description.append(vendorName).append(": ") // Include the vendor name
                       .append(item.getFoodName()).append(" x ").append(item.getQuantity()).append(", ");
        }
    }
    // Remove the trailing comma and space
    if (description.length() > 0) {
        description.setLength(description.length() - 2);
    }
    return description.toString();
}



private void deleteItemFromCart(String cartId) {
    String cartFilePath = "src/main/resources/txtfile/Cart.txt";
    StringBuilder updatedContent = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(cartFilePath))) {
        String line;
        boolean isRelevantBlock = false;

        while ((line = reader.readLine()) != null) {
            // Start of a new cart block
            if (line.startsWith("CART")) {
                isRelevantBlock = line.equals(cartId);
            }

            // If this block is not the one to delete, add it to updatedContent
            if (!isRelevantBlock) {
                updatedContent.append(line).append("\n");
            }

            // End of a block
            if (line.equals("----------")) {
                isRelevantBlock = false; // Reset flag
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error reading cart file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Write updated content back to the file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFilePath))) {
        writer.write(updatedContent.toString());
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error writing to cart file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}



private Map<String, List<CartItem>> loadCartData() {
    String cartFile = "src/main/resources/txtfile/Cart.txt";
    Map<String, List<CartItem>> cartItemsByVendor = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(cartFile))) {
        String line;
        String cartId = null;
        String vendorName = null;
        String customerName = null;
        String foodName = null;
        int quantity = 0;
        double pricePerItem = 0.0;
        String picturePath = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("CART")) {
                cartId = line; // Store the Cart ID
            } else if (line.startsWith("Vendor Name: ")) {
                vendorName = line.substring(13).trim();
            } else if (line.startsWith("Customer Name: ")) {
                customerName = line.substring(15).trim();
            } else if (line.startsWith("Food Name: ")) {
                foodName = line.substring(11).trim();
            } else if (line.startsWith("Quantity: ")) {
                quantity = Integer.parseInt(line.substring(10).trim());
            } else if (line.startsWith("Price per Item: RM")) {
                pricePerItem = Double.parseDouble(line.substring(18).trim());
            } else if (line.startsWith("Status: ") && line.substring(8).equalsIgnoreCase("Pending")) {
                if (customerName.equals(this.customerName)) {
                    picturePath = findPictureForFood(foodName); // Use the updated method
                    CartItem item = new CartItem(cartId, vendorName, foodName, quantity, pricePerItem, picturePath);
                    cartItemsByVendor.computeIfAbsent(vendorName, k -> new ArrayList<>()).add(item);
                }
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading cart data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    return cartItemsByVendor;
}

private String findPictureForFood(String foodName) {
    String menuFile = "src/main/resources/txtfile/Menu.txt";
    String picturePath = null;
    boolean foundFood = false; // Indicates whether the food name matches in the current block

    try (BufferedReader reader = new BufferedReader(new FileReader(menuFile))) {
        String line;
        String currentPicturePath = null; // Stores the current item's picture path

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Picture File Path: ")) {
                currentPicturePath = line.substring(18).trim(); // Extract the picture path
            }

            if (line.startsWith("Food Name: ")) {
                String currentFoodName = line.substring(11).trim(); // Extract the food name

                // Check if the current food name matches the requested one
                if (currentFoodName.equals(foodName)) {
                    picturePath = currentPicturePath; // Assign the corresponding picture path
                    foundFood = true;
                    break; // Exit the loop since we've found the food
                }
            }
        }

        if (!foundFood) {
            System.out.println("No picture found for food: " + foodName);
        }
    } catch (IOException e) {
        System.err.println("Error reading Menu.txt file.");
        e.printStackTrace();
    }

    return picturePath;
}

    
  private void loadVendorCards(JPanel vendorCardsPanel) {
    String vendorInfoFile = "src/main/resources/txtfile/VendorInfo.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(vendorInfoFile))) {
        String line;
        String username = null;
        String profilePicPath = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ")) {
                username = line.substring(10).trim();
            } else if (line.startsWith("ProfilePic: ")) {
                profilePicPath = line.substring(12).trim();

                // Create a vendor card and add it to the panel
                JPanel vendorCard = createVendorCard(username, profilePicPath);
                vendorCardsPanel.add(vendorCard);

                // Reset variables for the next vendor
                username = null;
                profilePicPath = null;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading vendor data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private JPanel createVendorCard(String username, String profilePicPath) {
    JPanel card = new JPanel();
    card.setPreferredSize(new Dimension(280, 300)); // Adjust card size for larger picture
    card.setLayout(new BorderLayout());
    card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

    // Profile picture
    JLabel profilePicLabel = new JLabel();
    profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
    try {
        ImageIcon profilePic = new ImageIcon(profilePicPath);
        Image scaledImage = profilePic.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Larger picture
        profilePicLabel.setIcon(new ImageIcon(scaledImage));
    } catch (Exception e) {
        profilePicLabel.setText("No Image");
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // Vendor name
    JLabel nameLabel = new JLabel(username, SwingConstants.CENTER);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

    // Buttons
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
    GradientButton viewMenuButton = new GradientButton("View Menu");
    GradientButton reviewButton = new GradientButton("Review");

    // Add action listeners for buttons
    viewMenuButton.addActionListener(e -> viewMenu(username));
    reviewButton.addActionListener(e -> reviewVendor(username));

    buttonPanel.add(viewMenuButton);
    buttonPanel.add(reviewButton);

    // Add components to the card
    card.add(profilePicLabel, BorderLayout.NORTH);
    card.add(nameLabel, BorderLayout.CENTER);
    card.add(buttonPanel, BorderLayout.SOUTH);

    return card;
}



// Method to handle "View Menu" action
private void viewMenu(String vendorName) {
    try {
        // Clear CustomerBackground and prepare for menu display
        CustomerBackground.removeAll();
        CustomerBackground.setLayout(new BorderLayout());

        // Create a scrollable panel for menu items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns for menu items
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load menu items for the selected vendor
        loadMenuItems(menuPanel, vendorName);

        // Wrap the menuPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Force scroll pane to occupy the full space of CustomerBackground
        scrollPane.setPreferredSize(new Dimension(CustomerBackground.getWidth(), CustomerBackground.getHeight()));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        // Add the scroll pane to CustomerBackground
        CustomerBackground.add(scrollPane, BorderLayout.CENTER);
        // Add a "Back to Vendors" button at the bottom
        GradientButton backButton = new GradientButton("Back to Vendors");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(CustomerBackground.getWidth(), 40));
        backButton.addActionListener(e -> backToVendors()); // Call the backToVendors method


        // Add the button to the bottom of CustomerBackground
        CustomerBackground.add(backButton, BorderLayout.SOUTH);
        
        CustomerBackground.revalidate(); // Refresh the panel to apply changes
        CustomerBackground.repaint();   // Ensure the UI updates correctly
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error displaying menu for: " + vendorName, "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void backToVendors() {
    // Clear the CustomerBackground panel
    CustomerBackground.removeAll();

    // Reset the vendor cards view
    setupCustomerBackground();

    // Refresh the panel
    CustomerBackground.revalidate();
    CustomerBackground.repaint();
}

private void loadMenuItems(JPanel menuPanel, String vendorName) {
    String menuFile = "src/main/resources/txtfile/Menu.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(menuFile))) {
        String line;
        String currentVendorName = null;
        String itemCode = null;
        String picturePath = null;
        String foodName = null;
        String description = null;
        String price = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Vendor Name: ")) {
                currentVendorName = line.substring(13).trim();
            } else if (currentVendorName != null && currentVendorName.equals(vendorName)) {
                if (line.startsWith("Item Code: ")) {
                    itemCode = line.substring(11).trim();
                } else if (line.startsWith("Picture File Path: ")) {
                    picturePath = line.substring(18).trim();
                } else if (line.startsWith("Food Name: ")) {
                    foodName = line.substring(11).trim();
                } else if (line.startsWith("Description: ")) {
                    description = line.substring(13).trim();
                } else if (line.startsWith("Price:")) {
                    price = line.substring(7).trim();

                    // Create a food card and add it to the menu panel
                    JPanel foodCard = createMenuCard(itemCode, picturePath, foodName, description, price, vendorName);
                    menuPanel.add(foodCard);

                    // Reset variables for the next food item
                    itemCode = null;
                    picturePath = null;
                    foodName = null;
                    description = null;
                    price = null;
                }
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading menu data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private JPanel createMenuCard(String itemCode, String picturePath, String foodName, String description, String price, String vendorName) {
    JPanel card = new JPanel();
    card.setPreferredSize(new Dimension(380, 250)); // Adjust card size for better spacing
    card.setLayout(new BorderLayout());
    card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

    // Picture
    JLabel pictureLabel = new JLabel();
    pictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
    try {
        ImageIcon foodImage = new ImageIcon(picturePath);
        Image scaledImage = foodImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        pictureLabel.setIcon(new ImageIcon(scaledImage));
    } catch (Exception e) {
        pictureLabel.setText("No Image");
        pictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // Food name and description
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BorderLayout());
    detailsPanel.setBackground(Color.WHITE);

    // Food Name
    JTextArea foodNameArea = new JTextArea(foodName);
    foodNameArea.setFont(new Font("Arial", Font.BOLD, 16));
    foodNameArea.setLineWrap(true); // Enable line wrapping for long names
    foodNameArea.setWrapStyleWord(true); // Wrap words rather than breaking them
    foodNameArea.setEditable(false); // Make it read-only
    foodNameArea.setOpaque(false); // Match background to parent
    foodNameArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

    // Description
    JTextArea descriptionArea = new JTextArea(description);
    descriptionArea.setLineWrap(true); // Enable line wrapping
    descriptionArea.setWrapStyleWord(true); // Wrap by word
    descriptionArea.setEditable(false); // Make it read-only
    descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
    descriptionArea.setOpaque(false); // Match background to parent
    descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

    // Price
    JLabel priceLabel = new JLabel("Price:" + price, SwingConstants.LEFT);
    priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
    priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

    // Add components to the details panel
    detailsPanel.add(foodNameArea, BorderLayout.NORTH);
    detailsPanel.add(descriptionArea, BorderLayout.CENTER);
    detailsPanel.add(priceLabel, BorderLayout.SOUTH);

    // "Select Item" Button
    GradientButton selectItemButton = new GradientButton("Select Item");
    selectItemButton.setFont(new Font("Arial", Font.BOLD, 14));
    selectItemButton.addActionListener(e -> showQuantityPopup(itemCode, foodName, price, vendorName)); // Define the popup logic

    // Panel for details and button
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(detailsPanel, BorderLayout.CENTER);
    bottomPanel.add(selectItemButton, BorderLayout.EAST); // Add the button to the right of the details

    // Add picture and bottom panel to the card
    card.add(pictureLabel, BorderLayout.WEST);
    card.add(bottomPanel, BorderLayout.CENTER);

    return card;
}


private void showQuantityPopup(String itemCode, String foodName, String price, String vendorName) {
    // Create the dialog
    JDialog dialog = new JDialog(this, "Select Item", true);
    dialog.setSize(400, 300);
    dialog.setLayout(new BorderLayout());

    // Create the main panel for content
    JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

    // Display item information
    panel.add(new JLabel("Item Code:"));
    panel.add(new JLabel(itemCode));
    panel.add(new JLabel("Food Name:"));
    panel.add(new JLabel(foodName));
    panel.add(new JLabel("Price per Item:"));
    panel.add(new JLabel(price));
    
    

    // Quantity selection
    panel.add(new JLabel("Quantity:"));
    JPanel quantityPanel = new JPanel(new BorderLayout());

    // Gradient buttons for Increase and Decrease
    GradientButton decreaseButton = new GradientButton("-");
    GradientButton increaseButton = new GradientButton("+");
    JTextField quantityField = new JTextField("1", 5); // Default quantity is 1
    quantityField.setHorizontalAlignment(JTextField.CENTER);
    quantityField.setEditable(false);

    decreaseButton.addActionListener(e -> {
        int quantity = Integer.parseInt(quantityField.getText());
        if (quantity > 1) {
            quantityField.setText(String.valueOf(quantity - 1));
        }
    });

    increaseButton.addActionListener(e -> {
        int quantity = Integer.parseInt(quantityField.getText());
        quantityField.setText(String.valueOf(quantity + 1));
    });

    quantityPanel.add(decreaseButton, BorderLayout.WEST);
    quantityPanel.add(quantityField, BorderLayout.CENTER);
    quantityPanel.add(increaseButton, BorderLayout.EAST);
    panel.add(quantityPanel);

    dialog.add(panel, BorderLayout.CENTER);

    // Add "Add to Cart" button
    GradientButton addToCartButton = new GradientButton("Add to Cart");
    addToCartButton.addActionListener(e -> {
        // Logic for adding to cart
        String cleanedPrice = price.replace("RM", "").trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double unitPrice = Double.parseDouble(cleanedPrice); // Parse cleaned price
        double totalPrice = unitPrice * quantity;

        // Save to Cart.txt
        saveToCartFile(itemCode, vendorName, customerName, foodName, quantity, unitPrice, totalPrice);

        JOptionPane.showMessageDialog(this, "Added " + quantity + " of " + foodName + " to the cart!");
        dialog.dispose(); // Close the dialog after adding to cart
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addToCartButton);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setLocationRelativeTo(this); // Center the dialog on the parent frame
    dialog.setVisible(true);
}





private void saveToCartFile(String itemCode, String vendorName, String customerName, String foodName, int quantity, double unitPrice, double totalPrice) {
    String cartFilePath = "src/main/resources/txtfile/Cart.txt";
    String cartId = generateCartId(); // Generate a unique cart ID
    String status = "Pending";

    try (java.io.FileWriter writer = new java.io.FileWriter(cartFilePath, true)) {
        writer.write(cartId + "\n");
        writer.write("Vendor Name: " + vendorName + "\n");
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Food Name: " + foodName + "\n");
        writer.write("Quantity: " + quantity + "\n");
        writer.write("Price per Item: RM" + String.format("%.2f", unitPrice) + "\n");
        writer.write("Total Price: RM" + String.format("%.2f", totalPrice) + "\n");
        writer.write("Status: " + status + "\n");
        writer.write("----------\n"); // Separator for cart entries
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving to cart file.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private String generateCartId() {
    return "CART" + System.currentTimeMillis(); // Generate a unique ID using timestamp
}

class GradientButton extends JButton {
    private boolean isPressed = false;

    public GradientButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE); // Set text color

        // Add mouse press/release listeners for the clicking effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Define gradient colors for default and pressed states
        Color gradientStart = isPressed ? new Color(255, 80, 0) : new Color(255, 140, 0);
        Color gradientEnd = isPressed ? new Color(200, 50, 0) : new Color(255, 69, 0);

        // Draw the gradient background
        GradientPaint gradientPaint = new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add border effect
        Color borderColor = isPressed ? new Color(150, 0, 0) : new Color(200, 100, 0);
        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        super.paintComponent(g);
    }
}



    // Action for "Review" button
    private void reviewVendor(String vendorName) {
        System.out.println("Customer Name: " + customerName);
        System.out.println("Vendor Name: " + vendorName);
        
        ReviewPage reviewPage= new ReviewPage(this.customerName,vendorName);
        reviewPage.setVisible(true);
        this.setVisible(false);
    }
   
    // Method to load the balance for the logged-in user
    private void loadUserBalance() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            boolean isUserFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Credit: RM")) {
                    String credit = line.substring(7); // Extract the balance value
                    Balance.setText(credit); // Display the balance
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
    }
}    

    // Method to load the default Address for the logged-in user
    private void loadUserDefaultAddress() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            boolean isUserFound = false;
            String defaultAddressType = null;
            
            //find default address type in the Users.txt file
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Default Address: ")) {
                     defaultAddressType = line.substring(16).trim(); // Extract the Home address or work address value                    break;
                    break;
                    }
                }
            // if there is no default address select, it should display home address instead
            if(defaultAddressType == null){
                while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Home Address: ")) {
                     String HomeAddress = line.substring(14).trim(); // Extract the Home address value
                     Address.setText(HomeAddress); // Display Home Address
                     break;
                    }
                }
            }
             // Second pass: Find the actual address value for the default type
            reader.close(); // Close and reopen the reader
            
        try (BufferedReader reader2 = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            isUserFound = false;
            while ((line = reader2.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith(defaultAddressType + ": ")) {
                    String resolvedAddress = line.substring(defaultAddressType.length() + 2).trim(); // Extract the actual address value
                    Address.setText(resolvedAddress); // Set the JLabel with the resolved address
                    return;
                }
            }
        }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
    }
}

private void writeOrderToFile(String orderId, String transactionId, String customerName, 
                              String vendorName, String itemDescription, 
                              String orderMethod, double totalPrice, String selectedAddressType) {
    String address = null;

    // Fetch the address only for Delivery
    if ("Delivery".equalsIgnoreCase(orderMethod)) {
        address = getAddressForUser(customerName, selectedAddressType); // Fetch the address
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Order.txt", true))) {
        writer.write("Order ID: " + orderId + "\n");
        writer.write("Transaction ID: " + transactionId + "\n"); // Add Transaction ID
        writer.write("Customer Name: " + customerName + "\n");
        writer.write("Vendor Name: " + vendorName + "\n");
        writer.write("Item Description: " + itemDescription + "\n");
        writer.write("Order Method: " + orderMethod + "\n");
        writer.write("Total Price: RM" + String.format("%.2f", totalPrice) + "\n");

        // Conditionally include the address for Delivery orders
        if ("Delivery".equalsIgnoreCase(orderMethod)) {
            writer.write("Address: " + address + "\n");
        }

        writer.write("Order Status: Pending\n");
        writer.write("----------\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}



private String getAddressForUser(String customerName, String selectedAddressType) {
    String usersFilePath = "src/main/resources/txtfile/Users.txt";
    String defaultAddressType = null; // To store the "Default Address" type
    String address = null; // To store the final resolved address

    try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
        String line;
        boolean isUserFound = false;

        // First Pass: Find the Default Address type
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).trim().equals(customerName)) {
                isUserFound = true; // Found the user
            } else if (isUserFound && line.startsWith("Default Address: ")) {
                defaultAddressType = line.substring(16).trim(); // Extract "Work Address", "Home Address", etc.
                break; // No need to read further
            }
        }

        if (!isUserFound || defaultAddressType == null) {
            throw new IOException("Default Address not found for user: " + customerName);
        }

        // If selected address is "Default Address," use the default type
        if ("Default Address".equals(selectedAddressType)) {
            selectedAddressType = defaultAddressType;
        }

        // Second Pass: Find the address based on the selected address type
        try (BufferedReader secondReader = new BufferedReader(new FileReader(usersFilePath))) {
            isUserFound = false; // Reset for the second pass

            while ((line = secondReader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).trim().equals(customerName)) {
                    isUserFound = true; // Found the user
                } else if (isUserFound && line.startsWith(selectedAddressType + ": ")) {
                    address = line.substring(selectedAddressType.length() + 2).trim(); // Extract the actual address
                    break; // Found the address, stop searching
                } else if (isUserFound && line.isEmpty()) {
                    break; // End of the user's block
                }
            }
        }

        if (address == null) {
            throw new IOException("Address not found for user: " + customerName + " with address type: " + selectedAddressType);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    return address;
}



    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        CustomerBackground = new javax.swing.JPanel();
        customerFunctionBackground = new javax.swing.JPanel();
        transactionHistorybtn = new javax.swing.JButton();
        orderHistorybtn = new javax.swing.JButton();
        orderStatusbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Balancebtn4 = new javax.swing.JButton();
        feedbackButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        Username = new javax.swing.JLabel();
        notificationbtn = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        TopUpbtn = new javax.swing.JButton();
        Balance = new javax.swing.JLabel();
        logoutbtn = new javax.swing.JButton();
        addressButton = new javax.swing.JButton();
        Address = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1500, 750));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        CustomerBackground.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout CustomerBackgroundLayout = new javax.swing.GroupLayout(CustomerBackground);
        CustomerBackground.setLayout(CustomerBackgroundLayout);
        CustomerBackgroundLayout.setHorizontalGroup(
            CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 838, Short.MAX_VALUE)
        );
        CustomerBackgroundLayout.setVerticalGroup(
            CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 721, Short.MAX_VALUE)
        );

        transactionHistorybtn.setText("Transaction History");
        transactionHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionHistorybtnActionPerformed(evt);
            }
        });

        orderHistorybtn.setText("Order History");
        orderHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderHistorybtnActionPerformed(evt);
            }
        });

        orderStatusbtn.setText("Order Status");
        orderStatusbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderStatusbtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Feature");

        Balancebtn4.setText("Balance");
        Balancebtn4.setMaximumSize(new java.awt.Dimension(96, 23));
        Balancebtn4.setMinimumSize(new java.awt.Dimension(96, 23));
        Balancebtn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Balancebtn4ActionPerformed(evt);
            }
        });

        feedbackButton.setText("Feedback");
        feedbackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                feedbackButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout customerFunctionBackgroundLayout = new javax.swing.GroupLayout(customerFunctionBackground);
        customerFunctionBackground.setLayout(customerFunctionBackgroundLayout);
        customerFunctionBackgroundLayout.setHorizontalGroup(
            customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderHistorybtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(transactionHistorybtn, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
            .addComponent(orderStatusbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Balancebtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(feedbackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(78, 78, 78))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
        );
        customerFunctionBackgroundLayout.setVerticalGroup(
            customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(orderHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(transactionHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(orderStatusbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Balancebtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                .addComponent(exitButton)
                .addGap(46, 46, 46))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        Username.setText("Username ");

        notificationbtn.setText("Notification");
        notificationbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationbtnActionPerformed(evt);
            }
        });

        logoLabel.setText("LOGO");

        TopUpbtn.setText("TOP UP");
        TopUpbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TopUpbtnActionPerformed(evt);
            }
        });

        Balance.setText("Balance");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TopUpbtn)
                .addGap(18, 18, 18)
                .addComponent(Balance, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TopUpbtn)
                    .addComponent(Balance))
                .addContainerGap())
        );

        logoutbtn.setText("Logout");
        logoutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutbtnActionPerformed(evt);
            }
        });

        addressButton.setText("Address");
        addressButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressButtonActionPerformed(evt);
            }
        });

        Address.setText("jLabel5");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82)
                .addComponent(addressButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Address, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationbtn)
                .addGap(4, 4, 4)
                .addComponent(logoutbtn)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addressButton)
                        .addComponent(Address))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(notificationbtn)
                        .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(logoutbtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CustomerBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(customerFunctionBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(CustomerBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void orderHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderHistorybtnActionPerformed
        //initialize Order history page
        CustomerOrderHistory customerOrderHistory= new CustomerOrderHistory(customerName);
        customerOrderHistory.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderHistorybtnActionPerformed

    private void Balancebtn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Balancebtn4ActionPerformed
        //initialize balance page
        CustomerBalancePage customerPage= new CustomerBalancePage(customerName);
        customerPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_Balancebtn4ActionPerformed

    private void TopUpbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TopUpbtnActionPerformed
        //initialize balance page
        CustomerBalancePage customerBalancePage= new CustomerBalancePage(customerName);
        customerBalancePage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_TopUpbtnActionPerformed

    private void logoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutbtnActionPerformed
        //initialize login page
        dispose();
        // Open the login page
        new LoginPage(); // Replace this with the actual class name for your login page
    }//GEN-LAST:event_logoutbtnActionPerformed

    private void transactionHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionHistorybtnActionPerformed
        //initialize Transaction page
        CustomerTransactionHistory customerTransactionPage= new CustomerTransactionHistory(customerName);
        customerTransactionPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_transactionHistorybtnActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        //exit system
       JOptionPane.showMessageDialog(this,"Thank you for using the system!! Have a good day!!!!");
       java.lang.System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void orderStatusbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderStatusbtnActionPerformed
        //initialize Transaction page
        CustomerOrderStatus customerOrderStatusPage= new CustomerOrderStatus(customerName);
        customerOrderStatusPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderStatusbtnActionPerformed

    private void addressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressButtonActionPerformed
        //initialize Address Page
        CustomerAddressPage customerAddressPage= new CustomerAddressPage(customerName);
        customerAddressPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_addressButtonActionPerformed

    private void feedbackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feedbackButtonActionPerformed
        //initialize Feedback Page
        FeedbackPage feedbackPage= new FeedbackPage(customerName);
        feedbackPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_feedbackButtonActionPerformed

    private void notificationbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationbtnActionPerformed
        //initialize notification Page
        NotificationPage notificationPage= new NotificationPage(customerName);
        notificationPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_notificationbtnActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String username = "DefaultUser"; // Replace "DefaultUser" with actual username or a fetched value
                new CustomerMainPage(username).setVisible(true);
                
            }
        });
    }
    
    public CustomerMainPage() {
    this("Guest"); // Default username
    
}


    
    class BackgroundPanel extends javax.swing.JFrame{
    private Image CustomerBackground;
    private Image customerFunctionBackground;
    
    public BackgroundPanel(){
        //load the background image from a file path (ensure the path is correct)
        try{
            CustomerBackground = new ImageIcon("src/main/resources/pic/LoginPage.png").getImage();
            customerFunctionBackground = new ImageIcon("src/main/resources/pic/LoginPage.png").getImage();
        }catch (Exception e){
        e.printStackTrace();
        }
    }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JLabel Balance;
    private javax.swing.JButton Balancebtn4;
    private javax.swing.JPanel CustomerBackground;
    private javax.swing.JButton TopUpbtn;
    private javax.swing.JLabel Username;
    private javax.swing.JButton addressButton;
    private javax.swing.JPanel customerFunctionBackground;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton feedbackButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton logoutbtn;
    private javax.swing.JButton notificationbtn;
    private javax.swing.JButton orderHistorybtn;
    private javax.swing.JButton orderStatusbtn;
    private javax.swing.JButton transactionHistorybtn;
    // End of variables declaration//GEN-END:variables
}