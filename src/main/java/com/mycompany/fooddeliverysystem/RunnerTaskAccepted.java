
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
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

public class RunnerTaskAccepted extends javax.swing.JPanel {

    private String RunnerName;
    private TaskHistory taskHistoryPanel; 
    private RevenueDashboard RevenueDashboard;
    
    public RunnerTaskAccepted(String RunnerName, TaskHistory taskHistoryPanel, RevenueDashboard RevenueDashboard) {
        initComponents();
        this.RunnerName = RunnerName;
        this.taskHistoryPanel = taskHistoryPanel;
        this.RevenueDashboard = RevenueDashboard;
        mainPanel.setPreferredSize(new Dimension(900, 200));
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        jScrollPanel.setPreferredSize(new Dimension(1000, 1000));
        loadAndDisplayAcceptedOrders();
    }
    
    private void loadAndDisplayAcceptedOrders() {
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
            // Now filter and display the accepted orders for the specific runner
            for (Map<String, String> filteredOrder : orders) {
                if ("Accepted".equals(filteredOrder.get("RunnerStatus")) && RunnerName.equals(filteredOrder.get("AllocatedRunner"))) {
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
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createOrderCard(Map<String, String> order) {
        // Same implementation as in ViewTaskPanel
        // Reuse the method to display order details with vendor image
        // Add vendor image logic if necessary
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1000, 150));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        detailsPanel.add(new JLabel("Order ID: " + order.get("OrderID")));
        detailsPanel.add(new JLabel("Customer Name: " + order.get("CustomerName")));
        detailsPanel.add(new JLabel("Vendor Name: " + order.get("VendorName")));
        detailsPanel.add(new JLabel("Item Description: " + order.get("ItemDescription")));
        detailsPanel.add(new JLabel("Order Method: " + order.get("OrderMethod")));
        detailsPanel.add(new JLabel("Total Price: RM" + order.get("TotalPrice")));
        detailsPanel.add(new JLabel("Address: " + order.get("Address")));

        card.add(detailsPanel, BorderLayout.CENTER);


        // Panel for the "Completed" button (right)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton completedButton = new JButton("Completed");

        // Add ActionListener to handle "Completed" button click
        completedButton.addActionListener(e -> {
            System.out.println("Completed order ID: " + order.get("OrderID"));
            JOptionPane.showMessageDialog(this, "Order Completed!", "Order Completed", JOptionPane.INFORMATION_MESSAGE);
            String notificationId = generateUniqueId("NOTIFICATION");
            String title = "Order Delivered";
            String detail = "Your order with Order ID: " + order.get("OrderID") + " has been delivered.";
            String datetime = java.time.LocalDateTime.now().toString();
            writeNotificationToFile(notificationId, title, order.get("CustomerName"), detail, datetime);
            markOrderAsCompleted(order.get("OrderID"), datetime);
            taskHistoryPanel.refreshTable();
            RevenueDashboard.reloadRevenue();
        });

        buttonPanel.add(completedButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }
    
    private String generateUniqueId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
    
    private void writeNotificationToFile(String notificationId, String title, String customerName, String detail, String dateTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Notification.txt", true))) {
            writer.write("Notification ID: " + notificationId + "\n");
            writer.write("Title: "+ title+ "\n");
            writer.write("Customer Name: " + customerName + "\n");
            writer.write("Notification Detail: " + detail + "\n");
            writer.write("Datetime: " + dateTime + "\n");
            writer.write("----------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void markOrderAsCompleted(String orderId, String datetime) {
        File filename = new File("src/main/resources/txtfile/Order.txt");
        StringBuilder updatedContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isOrderToUpdate = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ") && line.contains(orderId)) {
                    updatedContent.append(line).append("\n");
                    isOrderToUpdate = true;

                    // Continue reading order details until "----------"
                    while ((line = br.readLine()) != null && !line.equals("----------")) {
                        if (isOrderToUpdate && line.startsWith("Runner Status: ")) {
                            updatedContent.append("Runner Status: Completed\n");  // Mark Runner Status as Completed

                            // Add the Date Completed
                            updatedContent.append("Date Completed: ").append(datetime).append("\n");

                            // Reset flag after updating
                            isOrderToUpdate = false;
                        } else {
                            updatedContent.append(line).append("\n");
                        }
                    }
                    updatedContent.append("----------\n");  // End of the order block
                } else {
                    updatedContent.append(line).append("\n");
                }
            }

            // Write the updated content back to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
                bw.write(updatedContent.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Refresh the panel after marking the order as completed
        reloadOrders();
    }


    public void reloadOrders() {
        mainPanel.removeAll(); // Clear existing cards
        loadAndDisplayAcceptedOrders(); // Reload accepted orders
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

        setMaximumSize(new java.awt.Dimension(1000, 400));
        setMinimumSize(new java.awt.Dimension(1000, 400));
        setPreferredSize(new java.awt.Dimension(1000, 400));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

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
