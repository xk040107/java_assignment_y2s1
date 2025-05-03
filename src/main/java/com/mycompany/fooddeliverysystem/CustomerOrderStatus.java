
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class CustomerOrderStatus extends javax.swing.JFrame {
    private static String customerName;
    private List<Order> orderList = new ArrayList<>();

    public CustomerOrderStatus(String customerName) {
        initComponents();
        this.customerName = customerName;
        Username.setText(customerName); // Set the username on the JLabel
        loadUserBalance(); // Load and display the balance
        loadUserDefaultAddress(); // Load and display the address
        loadOrders(); // Load orders from the file
        displayOrders(); // Display the filtered orders in the table
        this.setLocationRelativeTo(null);
    }

    // Method to load orders from the file
    private void loadOrders() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Order.txt"))) {
            String line;
            Order order = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Order ID: ")) {
                    if (order != null) {
                        orderList.add(order);
                    }
                    order = new Order();
                    order.setOrderID(line.substring(10).trim());
                } else if (line.startsWith("Transaction ID: ")) {
                    order.setTransactionID(line.substring(16).trim());
                } else if (line.startsWith("Customer Name: ")) {
                    order.setCustomerName(line.substring(15).trim());
                } else if (line.startsWith("Vendor Name: ")) {
                    order.setVendorName(line.substring(13).trim());
                } else if (line.startsWith("Item Description: ")) {
                    order.setItemDescription(line.substring(18).trim());
                } else if (line.startsWith("Order Method: ")) {
                    order.setOrderMethod(line.substring(14).trim());
                } else if (line.startsWith("Total Price: ")) {
                    order.setTotalPrice(line.substring(13).trim());
                } else if (line.startsWith("Address: ")) {
                    order.setAddress(line.substring(9).trim());
                } else if (line.startsWith("Order Status: ")) {
                    order.setOrderStatus(line.substring(14).trim());
                } else if (line.startsWith("Allocated Runner: ")) {
                    order.setAllocatedRunner(line.substring(18).trim());
                } else if (line.startsWith("Runner Status: ")) {
                    order.setRunnerStatus(line.substring(15).trim());
                } else if (line.isEmpty() && order != null) {
                    orderList.add(order);
                    order = null;
                }
            }

            if (order != null) {
                orderList.add(order);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayOrders() {
    // Filter orders based on customerName
    List<Order> filteredOrders = orderList.stream()
            .filter(order -> order.getCustomerName().equalsIgnoreCase(customerName)) // Match all orders of the customer
            .collect(Collectors.toList());

    // Reverse the order of the filtered orders (to display the most recent at the top)
    Collections.reverse(filteredOrders);

    // Clear the panel and prepare the table
    CustomerOrderStatus.removeAll();
    CustomerOrderStatus.setLayout(new BorderLayout());

    if (filteredOrders.isEmpty()) {
        // Display a message if no orders are found
        JLabel noDataLabel = new JLabel("No orders found for the customer.", SwingConstants.CENTER);
        CustomerOrderStatus.add(noDataLabel, BorderLayout.CENTER);
    } else {
        String[] columnNames = {"Order ID", "Item Description", "Order Method", "Total Price", "Order Status", "Runner Status", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Order order : filteredOrders) {
            String runnerStatus = order.getRunnerStatus() != null ? order.getRunnerStatus() : "-";

            tableModel.addRow(new Object[]{
                    order.getOrderID(),
                    "View", // Add the "View" button in the Item Description column
                    order.getOrderMethod(),
                    order.getTotalPrice(),
                    order.getOrderStatus(),
                    runnerStatus,
                    "Cancel"
            });
        }

        JTable orderTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 6; // Only the "Item Description" and "Action" columns are editable
            }
        };

        // Add the "View" button for the Item Description column
        orderTable.getColumnModel().getColumn(1).setCellRenderer(new ItemDescriptionRenderer());
        orderTable.getColumnModel().getColumn(1).setCellEditor(new ItemDescriptionEditor(new JCheckBox()));


        // Add Cancel button renderer and editor
        orderTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(CustomerOrderStatus.getWidth(), CustomerOrderStatus.getHeight()));

        CustomerOrderStatus.add(scrollPane, BorderLayout.CENTER);
    }

    CustomerOrderStatus.revalidate();
    CustomerOrderStatus.repaint();
}


private class ItemDescriptionEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable table;

    public ItemDescriptionEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(Color.ORANGE); // Set the button background to orange
        button.setForeground(Color.WHITE); // Set the text color to white
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        label = (value == null) ? "View" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

@Override
public Object getCellEditorValue() {
    if (isPushed) {
        String orderID = table.getValueAt(selectedRow, 0).toString(); // Get the Order ID
        Order order = orderList.stream()
                .filter(o -> o.getOrderID().equals(orderID))
                .findFirst()
                .orElse(null);

        if (order != null) {
            // Split the item description by commas to get individual items
            String[] items = order.getItemDescription().split(",\\s*");

            // Create a panel with a vertical layout to display each item and its image
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.WHITE); // Set background color for better contrast

            for (String item : items) {
                // Extract quantity (e.g., "x 1") and remove it from the food name
                String quantity = item.replaceAll(".*x (\\d+)", "$1");
                String foodName = item.replaceAll(" x \\d+", "").trim();

                // Fetch the image path using the vendor name and food name
                String imagePath = getItemPicturePath(order.getVendorName(), foodName);

                // Create a panel for each item with padding and border
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding around each item
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1) // Light gray border for separation
                ));
                itemPanel.setBackground(Color.WHITE);

                // Load and add the image with an orange border
                JLabel imageLabel;
                if (imagePath != null) {
                    ImageIcon itemImage = new ImageIcon(imagePath);
                    Image scaledImage = itemImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel = new JLabel(new ImageIcon(scaledImage));
                } else {
                    imageLabel = new JLabel("Image Not Found");
                    imageLabel.setPreferredSize(new Dimension(150, 150));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                }
                imageLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 3)); // Orange border
                itemPanel.add(imageLabel, BorderLayout.WEST);

                // Add the item name and quantity
                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(Color.WHITE);
                textPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding inside text panel

                JLabel nameLabel = new JLabel("<html><b>" + foodName + "</b></html>");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

                JLabel quantityLabel = new JLabel("Quantity: " + quantity);
                quantityLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                quantityLabel.setForeground(Color.DARK_GRAY);

                textPanel.add(nameLabel);
                textPanel.add(Box.createVerticalStrut(5)); // Space between name and quantity
                textPanel.add(quantityLabel);

                itemPanel.add(textPanel, BorderLayout.CENTER);

                // Add each item panel to the main panel
                mainPanel.add(itemPanel);
            }

            // Show the pop-up window with all items and their images
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setPreferredSize(new Dimension(600, 500));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JOptionPane.showMessageDialog(
                    null,
                    scrollPane,
                    "Order Items for Order ID: " + orderID,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    isPushed = false;
    return label;
}

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}

private String getItemPicturePath(String vendorName, String foodName) {
    String menuFilePath = "src/main/resources/txtfile/Menu.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(menuFilePath))) {
        String line;
        boolean isMatchingVendor = false;
        String currentPicturePath = null; // Store the picture path temporarily

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Vendor Name: ") && line.substring(13).trim().equalsIgnoreCase(vendorName)) {
                isMatchingVendor = true;
            } else if (isMatchingVendor && line.startsWith("Picture File Path: ")) {
                currentPicturePath = line.substring(19).trim(); // Store the picture file path temporarily
            } else if (isMatchingVendor && line.startsWith("Food Name: ") && line.substring(11).trim().equalsIgnoreCase(foodName)) {
                // When food name matches, return the stored picture path
                return currentPicturePath;
            } else if (line.equals("-----------------------")) {
                isMatchingVendor = false;  // Reset for the next vendor
                currentPicturePath = null; // Clear picture path for the next item
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;  // If no image is found
}

private class ItemDescriptionRenderer extends JButton implements TableCellRenderer {
    public ItemDescriptionRenderer() {
        setOpaque(true);
        setBackground(Color.ORANGE); // Set the button background to orange
        setForeground(Color.WHITE);  // Set the text color to white
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "View" : value.toString());
        return this;
    }
}


private class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "Cancel" : value.toString());
        setBackground(Color.RED); // Set the button background to red
        setForeground(Color.WHITE); // Set the text color to white
        return this;
    }
}


  private class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(Color.RED); // Set the button background to red
        button.setForeground(Color.WHITE); // Set the text color to white
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        label = (value == null) ? "Cancel" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

@Override
public Object getCellEditorValue() {
    if (isPushed) {
        String orderID = table.getValueAt(selectedRow, 0).toString(); // Order ID
        String itemDescription = table.getValueAt(selectedRow, 1).toString(); // Item Description
        String orderMethod = table.getValueAt(selectedRow, 2).toString(); // Order Method
        String orderStatus = table.getValueAt(selectedRow, 4).toString(); // Order Status
        String totalPrice = table.getValueAt(selectedRow, 3).toString(); // Total Price
        String runnerStatus = ""; // Initialize runner status

        // Fetch runner status if the column exists
        try {
            runnerStatus = table.getValueAt(selectedRow, 5).toString(); // Runner Status
        } catch (ArrayIndexOutOfBoundsException e) {
            // If Runner Status is not available, do nothing
        }

        if (orderMethod.equalsIgnoreCase("Delivery") && orderStatus.equalsIgnoreCase("Pending")) {
            // Confirm cancellation
            int confirm = JOptionPane.showConfirmDialog(
                    null, // Centered on the screen
                    "Are you sure you want to cancel Order ID: " + orderID + "?",
                    "Cancel Order",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Update the Order.txt file
                updateOrderStatus(orderID, "Cancelled");

                // Calculate refund amount (Total Price + RM5 delivery fee)
                double refundAmount = Double.parseDouble(totalPrice.replace("RM", "")) + 5;

                // Write a notification in Notification.txt
                writeNotification(orderID, refundAmount);

                // Write a refund transaction in Transaction.txt
                writeRefundTransaction(orderID, itemDescription, refundAmount);

                // Update the user's credit in Users.txt
                updateUserCredit(customerName, refundAmount);

                // Show success message
                JOptionPane.showMessageDialog(
                        null,
                        "Order ID: " + orderID + " has been canceled. A refund of RM" + refundAmount + " has been processed.",
                        "Cancel Order",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Update the table to reflect the canceled status
                table.setValueAt("Cancelled", selectedRow, 4);
            }
        } else if (orderMethod.equalsIgnoreCase("Delivery") && runnerStatus.equalsIgnoreCase("Rejected")) {
            // Allow changing the order method
            String[] options = {"Dine-In", "Takeaway"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Runner rejected. Please select a new order method for Order ID: " + orderID,
                    "Change Order Method",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice != -1) {
                String newMethod = options[choice];

                // Update Order.txt
                changeOrderMethod(orderID, newMethod);

                // Write notification
                writeChangeMethodNotification(orderID, newMethod);

                // Show success message
                JOptionPane.showMessageDialog(
                        null,
                        "Order ID: " + orderID + " has been changed to " + newMethod ,
                        "Change Order Method",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Update table to reflect the new method
                table.setValueAt(newMethod, selectedRow, 2);
            }
        } else {
            // Action not allowed for non-eligible orders
            JOptionPane.showMessageDialog(
                    null,
                    "This action is not allowed for the selected order.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    isPushed = false;
    return label;
}

// Change Order Method for Runner Status = Rejected
private void changeOrderMethod(String orderID, String newMethod) {
    File orderFile = new File("src/main/resources/txtfile/Order.txt");
    File tempFile = new File("src/main/resources/txtfile/Order_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(orderFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isMatchingOrder = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Order ID: ") && line.substring(10).trim().equals(orderID)) {
                isMatchingOrder = true;
                writer.write(line); // Write the order ID line as-is
                writer.newLine();
            } else if (isMatchingOrder && line.startsWith("Order Method: ")) {
                writer.write("Order Method: " + newMethod); // Update the order method
                writer.newLine();
            } else if (isMatchingOrder && line.startsWith("Allocated Runner: ")) {
                // Skip writing this line to remove it
            } else if (isMatchingOrder && line.startsWith("Runner Status: ")) {
                // Skip writing this line to remove it
                isMatchingOrder = false; // Reset matching flag
            } else {
                writer.write(line); // Write all other lines as-is
                writer.newLine();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Replace the old file with the updated one
    if (!orderFile.delete() || !tempFile.renameTo(orderFile)) {
        JOptionPane.showMessageDialog(
                null,
                "Error updating order method in Order.txt.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


// Write a notification to Notification.txt for the order method change
private void writeChangeMethodNotification(String orderID, String newMethod) {
    File notificationsFile = new File("src/main/resources/txtfile/Notification.txt");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(notificationsFile, true))) {
        String notificationID = "NOTIFICATION" + System.currentTimeMillis();
        String datetime = java.time.LocalDateTime.now().toString();

        writer.write("Notification ID: " + notificationID);
        writer.newLine();
        writer.write("Title: Order Method Changed");
        writer.newLine();
        writer.write("Customer Name: " + customerName);
        writer.newLine();
        writer.write("Notification Detail: Your order with Order ID: " + orderID +
                " has been successfully changed to " + newMethod );
        writer.newLine();
        writer.write("Datetime: " + datetime);
        writer.newLine();
        writer.write("----------");
        writer.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Update the user's credit in Users.txt
private void updateUserCredit(String username, double refundAmount) {
    File usersFile = new File("src/main/resources/txtfile/Users.txt");
    File tempFile = new File("src/main/resources/txtfile/Users_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(usersFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isMatchingUser = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).equals(username)) {
                isMatchingUser = true;
                writer.write(line);
                writer.newLine();
            } else if (isMatchingUser && line.startsWith("Credit: RM")) {
                double currentCredit = Double.parseDouble(line.substring(10).trim());
                double updatedCredit = currentCredit + refundAmount;
                writer.write("Credit: RM" + String.format("%.2f", updatedCredit)); // Format to 2 decimal places
                writer.newLine();
                isMatchingUser = false;
            } else {
                writer.write(line);
                writer.newLine();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Replace the old file with the updated one
    if (!usersFile.delete() || !tempFile.renameTo(usersFile)) {
        JOptionPane.showMessageDialog(
                null,
                "Error updating user credit in Users.txt.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    // Update the order status in Order.txt
    private void updateOrderStatus(String orderID, String newStatus) {
        File orderFile = new File("src/main/resources/txtfile/Order.txt");
        File tempFile = new File("src/main/resources/txtfile/Order_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean isMatchingOrder = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Order ID: ") && line.substring(10).trim().equals(orderID)) {
                    isMatchingOrder = true;
                }

                if (isMatchingOrder && line.startsWith("Order Status: ")) {
                    writer.write("Order Status: " + newStatus);
                    writer.newLine();
                    isMatchingOrder = false;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace the old file with the updated one
        if (!orderFile.delete() || !tempFile.renameTo(orderFile)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error updating order status in Order.txt.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Write a notification to Notification.txt
    private void writeNotification(String orderID, double refundAmount) {
        File notificationsFile = new File("src/main/resources/txtfile/Notification.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(notificationsFile, true))) {
            String notificationID = "NOTIFICATION" + System.currentTimeMillis();
            String datetime = java.time.LocalDateTime.now().toString();

            writer.write("Notification ID: " + notificationID);
            writer.newLine();
            writer.write("Title: Order Cancelled");
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("Notification Detail: Your order with Order ID: " + orderID +
                    " has been canceled. A refund of RM" + refundAmount + " (including RM5.00 delivery fee) has been processed.");
            writer.newLine();
            writer.write("Datetime: " + datetime);
            writer.newLine();
            writer.write("----------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write a refund transaction to Transaction.txt
    private void writeRefundTransaction(String orderID, String itemDescription, double refundAmount) {
        File transactionsFile = new File("src/main/resources/txtfile/Transaction.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFile, true))) {
            String transactionID = "TRANSACTION" + System.currentTimeMillis();
            String datetime = java.time.LocalDateTime.now().toString();

            writer.write("Transaction ID: " + transactionID);
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("Order ID: " + orderID);
            writer.newLine();
            writer.write("Transaction Type: Refund");
            writer.newLine();
            writer.write("Refund Amount: RM" + refundAmount);
            writer.newLine();
            writer.write("Datetime: " + datetime);
            writer.newLine();
            writer.write("----------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


    private void loadUserBalance() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            boolean isUserFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Credit: ")) {
                    String credit = line.substring(8); // Extract the balance value
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CustomerOrderStatus = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        Username = new javax.swing.JLabel();
        notificationbtn = new javax.swing.JButton();
        logoLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        TopUpbtn = new javax.swing.JButton();
        Balance = new javax.swing.JLabel();
        logoutbtn = new javax.swing.JButton();
        addressButton = new javax.swing.JButton();
        Address = new javax.swing.JLabel();
        customerFunctionBackground = new javax.swing.JPanel();
        transactionHistorybtn = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        orderHistorybtn = new javax.swing.JButton();
        menuPagebtn = new javax.swing.JButton();
        Balancebtn4 = new javax.swing.JButton();
        feedbackButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setText("Order Status");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(15, 15, 15))
        );

        CustomerOrderStatus.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout CustomerOrderStatusLayout = new javax.swing.GroupLayout(CustomerOrderStatus);
        CustomerOrderStatus.setLayout(CustomerOrderStatusLayout);
        CustomerOrderStatusLayout.setHorizontalGroup(
            CustomerOrderStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1126, Short.MAX_VALUE)
        );
        CustomerOrderStatusLayout.setVerticalGroup(
            CustomerOrderStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CustomerOrderStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(497, 497, 497))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CustomerOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        Username.setText("Username ");

        notificationbtn.setText("Notification");
        notificationbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationbtnActionPerformed(evt);
            }
        });

        logoLabel1.setText("LOGO");

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
                .addComponent(Balance, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(logoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(addressButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Address)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationbtn)
                .addGap(4, 4, 4)
                .addComponent(logoutbtn)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(13, 13, 13))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(notificationbtn)
                            .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logoutbtn)
                            .addComponent(Address)
                            .addComponent(addressButton)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        customerFunctionBackground.setBackground(new java.awt.Color(102, 255, 255));

        transactionHistorybtn.setText("Transaction History");
        transactionHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionHistorybtnActionPerformed(evt);
            }
        });

        jButton12.setText("Exit");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel4.setText("Feature");

        orderHistorybtn.setText("Order History");
        orderHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderHistorybtnActionPerformed(evt);
            }
        });

        menuPagebtn.setText("Menu Page");
        menuPagebtn.setMaximumSize(new java.awt.Dimension(96, 23));
        menuPagebtn.setMinimumSize(new java.awt.Dimension(96, 23));
        menuPagebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPagebtnActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout customerFunctionBackgroundLayout = new javax.swing.GroupLayout(customerFunctionBackground);
        customerFunctionBackground.setLayout(customerFunctionBackgroundLayout);
        customerFunctionBackgroundLayout.setHorizontalGroup(
            customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderHistorybtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(transactionHistorybtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(78, 78, 78))
            .addComponent(menuPagebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Balancebtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .addComponent(feedbackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        customerFunctionBackgroundLayout.setVerticalGroup(
            customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(menuPagebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(orderHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(transactionHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Balancebtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton12)
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(customerFunctionBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TopUpbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TopUpbtnActionPerformed
        //initialize balance page
        CustomerBalancePage customerBalancePage= new CustomerBalancePage(customerName);
        customerBalancePage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_TopUpbtnActionPerformed

    private void logoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutbtnActionPerformed
        //initialize login page
        new LoginPage();
        this.dispose();
    }//GEN-LAST:event_logoutbtnActionPerformed

    private void transactionHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionHistorybtnActionPerformed
        //initialize Transaction page
        CustomerTransactionHistory customerTransactionPage= new CustomerTransactionHistory(customerName);
        customerTransactionPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_transactionHistorybtnActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        //exit system
        JOptionPane.showMessageDialog(this,"Thank you for using the system!! Have a good day!!!!");
        java.lang.System.exit(0);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void orderHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderHistorybtnActionPerformed
        //initialize Order history page
        CustomerOrderHistory customerOrderPage= new CustomerOrderHistory(customerName);
        customerOrderPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderHistorybtnActionPerformed

    private void menuPagebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPagebtnActionPerformed
        //initialize Maim page
        CustomerMainPage customerMainPage= new CustomerMainPage(customerName);
        customerMainPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_menuPagebtnActionPerformed

    private void Balancebtn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Balancebtn4ActionPerformed
        //initialize balance page
        CustomerBalancePage customerBalancePage= new CustomerBalancePage(customerName);
        customerBalancePage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_Balancebtn4ActionPerformed

    private void addressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressButtonActionPerformed
        //initialize Address Page
        CustomerAddressPage customerAddressPage= new CustomerAddressPage(customerName);
        customerAddressPage.setVisible(true);
    }//GEN-LAST:event_addressButtonActionPerformed

    private void feedbackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feedbackButtonActionPerformed
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerOrderStatus(customerName).setVisible(true);
            }
        });
    }
    // Order class to represent each order
    class Order {
        private String orderID;
        private String transactionID;
        private String customerName;
        private String vendorName;
        private String itemDescription;
        private String orderMethod;
        private String totalPrice;
        private String address;
        private String orderStatus;
        private String allocatedRunner;
        private String runnerStatus;

        // Getters and setters
        public String getOrderID() { return orderID; }
        public void setOrderID(String orderID) { this.orderID = orderID; }
        public String getTransactionID() { return transactionID; }
        public void setTransactionID(String transactionID) { this.transactionID = transactionID; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getVendorName() { return vendorName; }
        public void setVendorName(String vendorName) { this.vendorName = vendorName; }
        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
        public String getOrderMethod() { return orderMethod; }
        public void setOrderMethod(String orderMethod) { this.orderMethod = orderMethod; }
        public String getTotalPrice() { return totalPrice; }
        public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public String getAllocatedRunner() { return allocatedRunner; }
        public void setAllocatedRunner(String allocatedRunner) { this.allocatedRunner = allocatedRunner; }
        public String getRunnerStatus() { return runnerStatus; }
        public void setRunnerStatus(String runnerStatus) { this.runnerStatus = runnerStatus; }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JLabel Balance;
    private javax.swing.JButton Balancebtn4;
    private javax.swing.JPanel CustomerOrderStatus;
    private javax.swing.JButton TopUpbtn;
    private javax.swing.JLabel Username;
    private javax.swing.JButton addressButton;
    private javax.swing.JPanel customerFunctionBackground;
    private javax.swing.JButton feedbackButton;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel logoLabel1;
    private javax.swing.JButton logoutbtn;
    private javax.swing.JButton menuPagebtn;
    private javax.swing.JButton notificationbtn;
    private javax.swing.JButton orderHistorybtn;
    private javax.swing.JButton transactionHistorybtn;
    // End of variables declaration//GEN-END:variables
}
