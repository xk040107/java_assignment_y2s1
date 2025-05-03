package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;


public class AdminGenerateReceipt extends JFrame {
    private JTextField searchField;
    private JButton searchButton, showAllButton, sendReceiptButton, backButton;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private File transactionsFile;
    private File notificationsFile;

    public AdminGenerateReceipt() {
        // Set up the JFrame
        setTitle("Admin - Generate Receipt");
        setSize(1500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        showAllButton = new JButton("Show All");
        sendReceiptButton = new JButton("Send Receipt");
        backButton = new JButton("Back");

        // Set button colors
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        showAllButton.setBackground(Color.BLACK);
        showAllButton.setForeground(Color.WHITE);
        sendReceiptButton.setBackground(Color.BLACK);
        sendReceiptButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);

        // Set up the table model
        String[] columnNames = {
            "Transaction ID", "Customer Name", "Item Description", "Transaction Type",
            "Total Amount", "Datetime"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionsTable = new JTable(tableModel);

        // Layout setup
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Customer:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        topPanel.add(searchPanel, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        leftPanel.add(sendReceiptButton);
        leftPanel.add(backButton);
        panel.add(leftPanel, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        // Files
        transactionsFile = new File("src/main/resources/txtfile/Transaction.txt");
        notificationsFile = new File("src/main/resources/txtfile/Notification.txt");

        // Load data
        showAllTransactions();

        // Add listeners
        searchButton.addActionListener(e -> searchTransactions());
        showAllButton.addActionListener(e -> showAllTransactions());
        sendReceiptButton.addActionListener(e -> sendReceipt());
        backButton.addActionListener(e -> goBack());
    }

    private void showAllTransactions() {
        tableModel.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            Map<String, String> transaction = new HashMap<>();
            boolean isReceiptGenerated = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("----------")) {
                    if (!transaction.isEmpty() &&
                        "Payment".equalsIgnoreCase(transaction.get("Transaction Type")) &&
                        !isReceiptGenerated) {
                        tableModel.addRow(new Object[]{
                            transaction.get("Transaction ID"),
                            transaction.get("Customer Name"),
                            transaction.getOrDefault("Item Description", ""),
                            transaction.get("Transaction Type"),
                            transaction.getOrDefault("Total Amount", ""),
                            transaction.get("Datetime")
                        });
                    }
                    transaction.clear();
                    isReceiptGenerated = false;
                } else if (line.startsWith("Receipt: Generated")) {
                    isReceiptGenerated = true;
                } else {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        transaction.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTransactions() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a customer name to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            Map<String, String> transaction = new HashMap<>();
            boolean isReceiptGenerated = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("----------")) {
                    if (!transaction.isEmpty() &&
                        transaction.get("Customer Name").equalsIgnoreCase(searchText) &&
                        "Payment".equalsIgnoreCase(transaction.get("Transaction Type")) &&
                        !isReceiptGenerated) {
                        tableModel.addRow(new Object[]{
                            transaction.get("Transaction ID"),
                            transaction.get("Customer Name"),
                            transaction.getOrDefault("Item Description", ""),
                            transaction.get("Transaction Type"),
                            transaction.getOrDefault("Total Amount", ""),
                            transaction.get("Datetime")
                        });
                    }
                    transaction.clear();
                    isReceiptGenerated = false;
                } else if (line.startsWith("Receipt: Generated")) {
                    isReceiptGenerated = true;
                } else {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        transaction.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching transactions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void sendReceipt() {
    int selectedRow = transactionsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "No transaction selected. Please select a transaction to send.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String transactionID = (String) tableModel.getValueAt(selectedRow, 0);
    String customerName = (String) tableModel.getValueAt(selectedRow, 1);
    String itemDescription = (String) tableModel.getValueAt(selectedRow, 2);
    String totalAmount = (String) tableModel.getValueAt(selectedRow, 4);
    String datetime = (String) tableModel.getValueAt(selectedRow, 5);

    // Write notification with receipt details
    sendReceiptNotification(transactionID, customerName, itemDescription, totalAmount, datetime);

    // Mark receipt as generated in Transaction.txt
    markReceiptGenerated(transactionID);

    // Show confirmation dialog
    JOptionPane.showMessageDialog(this, "Receipt for Transaction ID " + transactionID + " has been sent to the customer.", "Success", JOptionPane.INFORMATION_MESSAGE);

    // Remove the selected row from the table (since receipt is generated)
    tableModel.removeRow(selectedRow);
}


    private void markReceiptGenerated(String transactionID) {
    File tempFile = new File("src/main/resources/txtfile/Transaction_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isMatchingTransaction = false;

        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();

            if (line.startsWith("Transaction ID: ") && line.substring(15).trim().equals(transactionID)) {
                isMatchingTransaction = true;
            }

            // Add "Receipt: Generated" immediately after the Datetime field
            if (isMatchingTransaction && line.startsWith("Datetime: ")) {
                writer.write("Receipt: Generated");
                writer.newLine();
                isMatchingTransaction = false; // Reset for the next transaction
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error updating Transaction.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }

    // Replace the original file with the updated file
    if (!transactionsFile.delete() || !tempFile.renameTo(transactionsFile)) {
        JOptionPane.showMessageDialog(this, "Error replacing the transaction file.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}



private void sendReceiptNotification(String transactionID, String customerName, String itemDescription, String totalAmount, String datetime) {
    File notificationsFile = new File("src/main/resources/txtfile/Notification.txt");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(notificationsFile, true))) {
        String notificationID = "NOTIFICATION" + System.currentTimeMillis();
        String currentDatetime = java.time.LocalDateTime.now().toString();

        // Write notification header
        writer.write("Notification ID: " + notificationID);
        writer.newLine();
        writer.write("Title: Receipt Sent");
        writer.newLine();
        writer.write("Customer Name: " + customerName);
        writer.newLine();
        writer.write("Notification Detail: Your receipt is ready below.");
        writer.newLine();

        // Receipt details
        writer.write("==========================================");
        writer.newLine();
        writer.write("-                          APU FOODCOURT                          -");
        writer.newLine();
        writer.write("-       ** OFFICIAL TRANSACTION RECEIPT **      -");
        writer.newLine();
        writer.write("==========================================");
        writer.newLine();
        writer.write("Transaction ID   : " + transactionID);
        writer.newLine();
        writer.write("Customer Name    : " + customerName);
        writer.newLine();
        writer.write("Item Description : " + itemDescription);
        writer.newLine();
        writer.write("Total Amount     : " + totalAmount);
        writer.newLine();
        writer.write("Transaction Date : " + datetime);
        writer.newLine();
        writer.write("Receipt Status   : Generated");
        writer.newLine();
        writer.write("==========================================");
        writer.newLine();
        writer.write("-    Thank you for dining with us at APU Foodcourt!    -");
        writer.newLine();
        writer.write("We appreciate your support and hope to serve you again soon.");
        writer.newLine();
        writer.write("==========================================");
        writer.newLine();

        // Add a mock barcode (text-based representation)
        writer.write("|| || ||| || |||| ||| ||||| || ||| || ||||| || ||| || |||| ||| ||||| || ||| || |||||| || |||");
        writer.newLine();
        writer.write("|| ||||| ||| || |||| ||| ||| |||| || ||||| || ||| || |||| ||| ||||| || ||| || |||||| || |||");
        writer.newLine();
        writer.write("==========================================");
        writer.newLine();

        // Datetime and separator
        writer.write("Datetime: " + currentDatetime);
        writer.newLine();
        writer.write("----------");
        writer.newLine();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error writing receipt notification.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}





    private void goBack() {
        // Close the current window and go back to the previous screen
        SwingUtilities.invokeLater(() -> AdminMain.main(new String[]{}));
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminGenerateReceipt().setVisible(true));
    }
}
