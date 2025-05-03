package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class ViewTaskPanel extends javax.swing.JPanel {

    private String RunnerName;
    private RunnerTaskAccepted RunnerTaskAccepted;
    

    public ViewTaskPanel(String RunnerName, RunnerTaskAccepted RunnerTaskAccepted) {
        initComponents();
        this.RunnerName = RunnerName;
        this.RunnerTaskAccepted = RunnerTaskAccepted;
        mainPanel.setPreferredSize(new Dimension(900, 200));
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        jScrollPanel.setPreferredSize(new Dimension(1000, 1000));
        loadAndDisplayOrders();
    }
    
    private void loadAndDisplayOrders() {
        // Define the File object for the order.txt file
        File filename = new File("src/main/resources/txtfile/Order.txt");

        List<Map<String, String>> orders = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Map<String, String> order = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ")) {
                    // If there's an existing order, add it to the list
                    if (order != null) {
                        orders.add(order);
                    }
                    // Start a new order map
                    order = new HashMap<>();
                    order.put("OrderID", line.substring(10).trim());
                }
                if (line.startsWith("Transaction ID: ")) order.put("TransactionID", line.substring(17).trim());
                if (line.startsWith("Customer Name: ")) order.put("CustomerName", line.substring(15).trim());
                if (line.startsWith("Vendor Name: ")) order.put("VendorName", line.substring(13).trim());
                if (line.startsWith("Item Description: ")) order.put("ItemDescription", line.substring(18).trim());
                if (line.startsWith("Order Method: ")) order.put("OrderMethod", line.substring(14).trim());
                if (line.startsWith("Total Price: RM")) order.put("TotalPrice", line.substring(15).trim());
                if (line.startsWith("Address: ")) order.put("Address", line.substring(9).trim());
                if (line.startsWith("Order Status: ")) order.put("OrderStatus", line.substring(14).trim());
                if (line.startsWith("Allocated Runner: ")) order.put("AllocatedRunner", line.substring(18).trim());
                if (line.startsWith("Runner Status: ")) order.put("RunnerStatus", line.substring(15).trim());

                // When we reach the end of an order block (indicated by "----------"), add it to the list
                if (line.equals("----------")) {
                    if (order != null) {
                        orders.add(order);
                        order = null;  // Reset for next order
                    }
                }
            }

            boolean hasPendingOrders = false;
            // Now filter and display the pending orders for the specific runner
            for (Map<String, String> filteredOrder : orders) {
                if ("Pending".equals(filteredOrder.get("RunnerStatus")) && RunnerName.equals(filteredOrder.get("AllocatedRunner"))) {
                    JPanel orderCard = createOrderCard(filteredOrder);
                    mainPanel.add(orderCard); // Add the order card to the mainPanel
                    hasPendingOrders = true;
                }
            }
            if (!hasPendingOrders) {
                JLabel noOrderLabel = new JLabel("Currently no order available. Waiting....");
                noOrderLabel.setFont(new Font("Arial", Font.BOLD, 16));
                mainPanel.add(noOrderLabel);
            }
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private JPanel createOrderCard(Map<String, String> order) {
        // Create a panel for the card with fixed size
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());  // Use BorderLayout to place components in specific regions
        card.setPreferredSize(new Dimension(1000, 150)); // Set a fixed size for each order card
        card.setMaximumSize(new Dimension(1000, 150)); // Ensure the card does not exceed this size
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Create a panel for the labels to hold the order details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));  // Vertical layout for labels
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Reduce padding for tighter space

        // Add labels with left alignment to the detailsPanel
        JLabel orderIdLabel = new JLabel("Order ID: " + order.get("OrderID"));
        orderIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(orderIdLabel);

        JLabel customerNameLabel = new JLabel("Customer Name: " + order.get("CustomerName"));
        customerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(customerNameLabel);

        JLabel vendorNameLabel = new JLabel("Vendor Name: " + order.get("VendorName"));
        vendorNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(vendorNameLabel);

        JLabel itemDescriptionLabel = new JLabel("Item Description: " + order.get("ItemDescription"));
        itemDescriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(itemDescriptionLabel);

        JLabel orderMethodLabel = new JLabel("Order Method: " + order.get("OrderMethod"));
        orderMethodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(orderMethodLabel);

        JLabel totalPriceLabel = new JLabel("Total Price: RM" + order.get("TotalPrice"));
        totalPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(totalPriceLabel);

        JLabel addressLabel = new JLabel("Address: " + order.get("Address"));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(addressLabel);

        // Add the detailsPanel to the card (left side)
        card.add(detailsPanel, BorderLayout.CENTER);

        // Create a panel for the Accept and Reject buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5)); 

        // Create buttons
        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");

        // Add ActionListener to buttons
        acceptButton.addActionListener(e -> {
            System.out.println("Accept button clicked!");
            String orderId = order.get("OrderID");
            String notificationId = generateUniqueId("NOTIFICATION");
            String title = "Order Accepted by Runner";
            String detail = "Delivery runner is delivering your order with Order ID: " + orderId + ".";
            String datetime = java.time.LocalDateTime.now().toString();
            writeNotificationToFile(notificationId, title, order.get("CustomerName"), detail, datetime);
            updateRunnerStatus(orderId, "Accepted");
            System.out.println("Accepted order ID: " + orderId);
        });


        rejectButton.addActionListener(e -> {
            try {
                String orderId = order.get("OrderID");
                String runnerName = RunnerName;  // Assuming RunnerName is available as a field
                String customerName = order.get("CustomerName");

                // Save the rejected order information to the RejectedOrders.txt
                saveRejectedOrderToFile(orderId, runnerName);

                // Update the order's allocated runner (find a new one)
                String newRunner = findNextAvailableRunner(orderId);
                System.out.println(newRunner);
                
                if (newRunner != null) {
                    // Reallocate the order to the new runner
                    System.out.println("New Runner found: " + newRunner);
                    updateAllocatedRunner(orderId, newRunner);

                    // Provide feedback to the user
                    JOptionPane.showMessageDialog(this, "Order successfully rejected!", "Order Reallocated", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    handleNoRunnerRejection(orderId, customerName);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error rejecting order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Add buttons to the buttonPanel
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        // Add the button panel to the card (right bottom)
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }
            
    private void handleNoRunnerRejection(String orderId, String customerName) throws IOException {
        updateOrderStatusNoRunner(orderId, customerName);
        updateUserCredit(customerName);
        String notificationId = generateUniqueId("NOTIFICATION");
        String title = "Order Rejected by Runners";
        String detail = "Your order with Order ID: " + orderId + " has been rejected. No runner available for delivery. A refund of RM5 has been processed. Please choose dine-in or take away.";
        String datetime = java.time.LocalDateTime.now().toString();
        writeNotificationToFile(notificationId, title, customerName, detail, datetime);
        
        // Write transaction record
        String transactionId = generateUniqueId("TRANSACTION");
        writeRejectedTransactionToFile(transactionId, customerName, orderId, 5);
    }
    
    private String generateUniqueId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
    
    private void writeNotificationToFile(String notificationId, String title, String customerName, String detail, String dateTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Notification.txt", true))) {
            writer.write("Notification ID: " + notificationId + "\n");
            writer.write("Title: "+ title +"\n");
            writer.write("Customer Name: " + customerName + "\n");
            writer.write("Notification Detail: " + detail + "\n");
            writer.write("Datetime: " + dateTime + "\n");
            writer.write("----------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    private void updateRunnerStatus(String orderId, String newStatus) {
        File filename = new File("src/main/resources/txtfile/Order.txt");
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isOrderToUpdate = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ") && line.contains(orderId)) {
                    isOrderToUpdate = true;
                }

                // Update the RunnerStatus if it's the relevant order
                if (isOrderToUpdate && line.startsWith("Runner Status: ")) {
                    fileContent.add("Runner Status: " + newStatus);
                    isOrderToUpdate = false; // Reset flag after updating
                } else {
                    fileContent.add(line); // Add unmodified line
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the updated content back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String contentLine : fileContent) {
                bw.write(contentLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(this, "Order Accepted Successfully!", "Order Accepted", JOptionPane.INFORMATION_MESSAGE);
        mainPanel.removeAll();
        loadAndDisplayOrders();
        mainPanel.revalidate();
        mainPanel.repaint();
        
        
        RunnerTaskAccepted.reloadOrders();
    }
    
    private void saveRejectedOrderToFile(String orderId, String runnerName) {
        File rejectedOrdersFile = new File("src/main/resources/txtfile/RejectedOrders.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rejectedOrdersFile, true))) {
            // Writing rejected order details to the file
            writer.write("Order ID: " + orderId + "\n");
            writer.write("Rejected by Runner: " + runnerName + "\n");
            writer.write("----------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private List<String> getRunnersFromUsersFile() {
        File userFile = new File("src/main/resources/txtfile/Users.txt");
        List<String> runners = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            String username = null;
            String role = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();  // Clean up any extra spaces or newlines

                if (line.startsWith("Username: ")) {
                    username = line.substring(10).trim();  // Extract the username
                }
                if (line.startsWith("Role: ")) {
                    role = line.substring(6).trim();  // Extract the role
                }

                // If both username and role are found, check if the role is "Runner" and add to the list
                if (username != null && role != null && "Runner".equalsIgnoreCase(role)) {
                    runners.add(username);
                    // Reset for the next user
                    username = null;
                    role = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return runners;
    }


    
    private List<String> getRejectedRunners(String orderId) {
        File rejectedOrdersFile = new File("src/main/resources/txtfile/RejectedOrders.txt");
        List<String> rejectedRunners = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rejectedOrdersFile))) {
            String line;
            String currentOrderId = null;
            String runnerName = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Order ID: ")) {
                    currentOrderId = line.substring(10).trim();
                }
                if (line.startsWith("Rejected by Runner: ")) {
                    runnerName = line.substring(20).trim();
                    if (currentOrderId != null && runnerName != null && currentOrderId.equals(orderId)) {
                        rejectedRunners.add(runnerName);  // Add the runner if the orderId matches
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rejectedRunners;
    }


   private void updateAllocatedRunner(String orderId, String newRunner) {
    System.out.println("Updating order ID: " + orderId + " with new runner: " + newRunner);
    File filename = new File("src/main/resources/txtfile/Order.txt");
    List<String> fileContent = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isOrderToUpdate = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ") && line.contains(orderId)) {
                    isOrderToUpdate = true;
                }

                // Update the RunnerStatus if it's the relevant order
                if (isOrderToUpdate && line.startsWith("Allocated Runner: ")) {
                    fileContent.add("Allocated Runner: " + newRunner);
                    isOrderToUpdate = false; // Reset flag after updating
                } else {
                    fileContent.add(line); // Add unmodified line
                }
            }

        } catch (IOException e) {
        e.printStackTrace();
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
        for (String contentLine : fileContent) {
            bw.write(contentLine);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    System.out.println("Order file updated.");
    mainPanel.removeAll();
    loadAndDisplayOrders(); // Re-load the orders after update
    mainPanel.revalidate(); // Revalidate to apply changes
    mainPanel.repaint(); // Repaint to reflect changes on the screen

    }






    
    private String findNextAvailableRunner(String orderId) {
        // Get the list of all runners
        List<String> runners = getRunnersFromUsersFile();  
        List<String> rejectedRunners = getRejectedRunners(orderId);  

        // Debugging the contents of both lists
        System.out.println("Runners: " + runners);
        System.out.println("Rejected Runners: " + rejectedRunners);

        // Find a runner who hasn't rejected any orders
        for (String runner : runners) {
            // If the runner is not in the rejected list, they're available
            if (!rejectedRunners.contains(runner)) {
                System.out.println("Found available runner: " + runner);
                return runner;  // Return the first available runner
            }
        }

        // No available runner found
        return null;
    }




private void updateOrderStatusNoRunner(String orderId, String customerName) {
    File filename = new File("src/main/resources/txtfile/Order.txt");
    List<String> fileContent = new ArrayList<>();
    boolean isOrderToUpdate = false;  // Flag to mark when the matching order block is found
    boolean isCustomerMatched = false;  // Flag to confirm customer match for the order
    boolean isOrderUpdated = false;  // Flag to track if the update is successful

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = br.readLine()) != null) {
            // When we find the start of an order block, check for the matching orderId
            if (line.startsWith("Order ID: ") && line.contains(orderId)) {
                isOrderToUpdate = true;  // Mark order block as matched
            }

            // If we are in the correct order block, check for the customer name
            if (isOrderToUpdate && line.startsWith("Customer Name: ") && line.contains(customerName)) {
                isCustomerMatched = true;  // Confirm that the customer name matches
            }

            // Update the fields only if both orderId and customerName match
            if (isOrderToUpdate && isCustomerMatched) {
                if (line.startsWith("Allocated Runner: ")) {
                    fileContent.add("Allocated Runner: Rejected");  // Indicating no runner available
                } else if (line.startsWith("Runner Status: ")) {
                    fileContent.add("Runner Status: Rejected");  // Indicating the runner status is rejected
                    isOrderUpdated = true;  // Mark the order as updated
                } else {
                    fileContent.add(line);  // Add any other lines as they are
                }
            } else {
                fileContent.add(line);  // Add the line as is if we are not in the matching order block
            }

            // If the order block is fully processed, stop further checks until the next block starts
            if (isOrderToUpdate && line.startsWith("----------")) {
                isOrderToUpdate = false;  // Reset flag for the next order block
                isCustomerMatched = false;  // Reset customer match flag
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Write the updated content back to the file
    if (isOrderUpdated) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String contentLine : fileContent) {
                bw.write(contentLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Notify that the order status has been updated
        JOptionPane.showMessageDialog(this, "Order status updated successfully!", "Order Updated", JOptionPane.INFORMATION_MESSAGE);
        mainPanel.removeAll();
        loadAndDisplayOrders();
        mainPanel.revalidate();
        mainPanel.repaint();
    } else {
        JOptionPane.showMessageDialog(this, "Error updating the order status. Order not found or customer mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}



private boolean updateUserCredit(String customerName) {
    String userFilePath = "src/main/resources/txtfile/Users.txt";
    StringBuilder updatedContent = new StringBuilder();
    boolean isUserFound = false;

    try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Username: ") && line.substring(10).trim().equals(customerName)) {
                isUserFound = true;
                updatedContent.append(line).append("\n");
            } else if (isUserFound && line.startsWith("Credit: RM")) {
                double currentBalance = Double.parseDouble(line.substring(10).trim());
                double newBalance = currentBalance + 5.00;  // Refund RM5
                updatedContent.append("Credit: RM").append(String.format("%.2f", newBalance)).append("\n");
                isUserFound = false;
            } else {
                updatedContent.append(line).append("\n");
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error reading Users.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFilePath))) {
        writer.write(updatedContent.toString());
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error writing to Users.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    mainPanel.removeAll();
    loadAndDisplayOrders();
    mainPanel.revalidate();
    mainPanel.repaint();
    return true;
}

public void reloadOrders() {
        mainPanel.removeAll(); // Clear existing cards
        loadAndDisplayOrders(); // Reload accepted orders
        mainPanel.revalidate(); // Refresh the panel
        mainPanel.repaint();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPanel = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(192, 222, 242));
        setMaximumSize(new java.awt.Dimension(1000, 400));
        setMinimumSize(new java.awt.Dimension(1000, 400));
        setPreferredSize(new java.awt.Dimension(1000, 400));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.setBackground(new java.awt.Color(192, 222, 242));
        mainPanel.setPreferredSize(new java.awt.Dimension(200000, 200000));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPanel.setViewportView(mainPanel);

        add(jScrollPanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPanel;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
