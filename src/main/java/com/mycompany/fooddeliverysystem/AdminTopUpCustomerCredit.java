package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTopUpCustomerCredit extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private static final String TOPUP_FILE = "C:\\JavaAsg\\FoodDeliverySystem\\src\\main\\resources\\txtfile\\TopUp.txt";
    private static final String USERS_FILE = "C:\\JavaAsg\\FoodDeliverySystem\\src\\main\\resources\\txtfile\\Users.txt";
    private static final String NOTIFICATION_FILE = "C:\\JavaAsg\\FoodDeliverySystem\\src\\main\\resources\\txtfile\\Notification.txt";

    public AdminTopUpCustomerCredit() {
        setTitle("Top Up Customer Credit");
        setSize(1500, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Label
        JLabel titleLabel = new JLabel("Manage Customer Top-Up Requests", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Top Up ID", "Customer Name", "Amount", "Datetime", "Action"}, 0);
        table = new JTable(tableModel);
        loadTopUpRequests();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton approveButton = createButton("Approve", Color.GREEN);
        JButton rejectButton = createButton("Reject", Color.RED);
        JButton backButton = createButton("Back", Color.BLACK);

        approveButton.addActionListener((ActionEvent e) -> processTopUpRequest(true));
        rejectButton.addActionListener((ActionEvent e) -> processTopUpRequest(false));
        backButton.addActionListener((ActionEvent e) -> goBack());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(400, 40));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        return button;
    }

    private void goBack() {
        SwingUtilities.invokeLater(() -> AdminMain.main(new String[]{}));
        dispose();
    }

    private void loadTopUpRequests() {
        File file = new File(TOPUP_FILE);
        if (!file.exists()) {
            System.out.println("File not found: " + TOPUP_FILE);
            return;
        }

        tableModel.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String topUpId = "", customerName = "", amount = "", datetime = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Top Up ID: ")) {
                    topUpId = line.substring(10).trim();
                } else if (line.startsWith("Customer Name: ")) {
                    customerName = line.substring(15).trim();
                } else if (line.startsWith("Amount: ")) {
                    amount = line.substring(8).trim();
                } else if (line.startsWith("Datetime: ")) {
                    datetime = line.substring(9).trim();
                    tableModel.addRow(new Object[]{topUpId, customerName, amount, datetime, "Pending"});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading TopUp file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processTopUpRequest(boolean isApprove) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a top-up request.");
            return;
        }

        String topUpId = (String) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        String amount = (String) tableModel.getValueAt(selectedRow, 2);
        String datetime = java.time.LocalDateTime.now().toString();

        if (isApprove) {
            boolean success = updateUserCredit(customerName, amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "Top-up approved for " + customerName + " (RM" + amount + ").", "Approval Successful", JOptionPane.INFORMATION_MESSAGE);
                sendNotification(customerName, "Top-Up Approved", "Your top-up of RM" + amount + " has been approved.", datetime);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update credit for " + customerName + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Top-up request rejected for " + customerName + ".", "Rejection Notice", JOptionPane.WARNING_MESSAGE);
            sendNotification(customerName, "Top-Up Rejected", "Your top-up request of RM" + amount + " has been rejected.", datetime);
        }

        boolean removed = removeTopUpRequest(topUpId);
        if (removed) {
            loadTopUpRequests();
        }
    }

    private boolean updateUserCredit(String customerName, String amount) {
        File file = new File(USERS_FILE);
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isTargetUser = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).trim().equalsIgnoreCase(customerName)) {
                    lines.add(line);
                    isTargetUser = true;
                } else if (isTargetUser && line.startsWith("Credit: RM")) {
                    double currentCredit = Double.parseDouble(line.substring(10).trim());
                    double topUpAmount = Double.parseDouble(amount);
                    double newCredit = currentCredit + topUpAmount;
                    lines.add("Credit: RM" + String.format("%.2f", newCredit));
                    updated = true;
                    isTargetUser = false;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return updated;
    }

    private void sendNotification(String customerName, String title, String detail, String datetime) {
        File notificationFile = new File(NOTIFICATION_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(notificationFile, true))) {
            String notificationID = "NOTIFICATION" + System.currentTimeMillis();
            writer.write("Notification ID: " + notificationID);
            writer.newLine();
            writer.write("Title: " + title);
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("Notification Detail: " + detail);
            writer.newLine();
            writer.write("Datetime: " + datetime);
            writer.newLine();
            writer.write("----------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminTopUpCustomerCredit().setVisible(true));
    }

    private boolean removeTopUpRequest(String topUpId) {
    File file = new File(TOPUP_FILE);
    List<String> lines = new ArrayList<>();
    boolean isRemoving = false;
    boolean removed = false;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Top Up ID: " + topUpId)) {
                isRemoving = true;
                removed = true;
            } else if (isRemoving && line.equals("----------")) {
                isRemoving = false;
                continue;
            }

            if (!isRemoving) {
                lines.add(line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    if (removed) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    return removed;
}

}
