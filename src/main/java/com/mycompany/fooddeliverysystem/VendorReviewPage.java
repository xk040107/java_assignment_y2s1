package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VendorReviewPage extends JFrame {
    private String vendorName;

    public VendorReviewPage(String vendorName) {
        this.vendorName = vendorName;
        setTitle("Vendor Reviews - " + vendorName);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Title label at the top of the page
        JLabel titleLabel = new JLabel("Customer Reviews for " + vendorName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(255, 140, 0));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 50));

        // Create lists for Vendor and Order reviews.
        List<String[]> vendorReviews = new ArrayList<>();
        List<String[]> orderReviews = new ArrayList<>();
        loadReviews(vendorReviews, orderReviews);

        // --- Vendor Reviews Table ---
        String[] vendorColumns = {"Review ID", "Customer Name", "Remarks", "Datetime"};
        String[][] vendorTableData = vendorReviews.toArray(new String[0][0]);
        DefaultTableModel vendorModel = new DefaultTableModel(vendorTableData, vendorColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disallow editing
            }
        };
        JTable vendorTable = new JTable(vendorModel);
        vendorTable.setRowHeight(30);
        vendorTable.setFont(new Font("Arial", Font.PLAIN, 14));
        vendorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        vendorTable.getTableHeader().setBackground(new Color(255, 165, 0));
        vendorTable.getTableHeader().setForeground(Color.WHITE);

        TableColumnModel vendorColModel = vendorTable.getColumnModel();
        vendorColModel.getColumn(0).setPreferredWidth(200);
        vendorColModel.getColumn(1).setPreferredWidth(150);
        vendorColModel.getColumn(2).setPreferredWidth(400);
        vendorColModel.getColumn(3).setPreferredWidth(250);

        JScrollPane vendorScrollPane = new JScrollPane(vendorTable);
        vendorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // --- Order Reviews Table ---
        // Order reviews include extra columns: Order ID and Order Method.
        String[] orderColumns = {"Review ID", "Order ID", "Customer Name", "Order Method", "Remarks", "Datetime"};
        String[][] orderTableData = orderReviews.toArray(new String[0][0]);
        DefaultTableModel orderModel = new DefaultTableModel(orderTableData, orderColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disallow editing
            }
        };
        JTable orderTable = new JTable(orderModel);
        orderTable.setRowHeight(30);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        orderTable.getTableHeader().setBackground(new Color(255, 165, 0));
        orderTable.getTableHeader().setForeground(Color.WHITE);

        TableColumnModel orderColModel = orderTable.getColumnModel();
        orderColModel.getColumn(0).setPreferredWidth(200);
        orderColModel.getColumn(1).setPreferredWidth(200);
        orderColModel.getColumn(2).setPreferredWidth(150);
        orderColModel.getColumn(3).setPreferredWidth(150);
        orderColModel.getColumn(4).setPreferredWidth(400);
        orderColModel.getColumn(5).setPreferredWidth(250);

        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // --- Tabbed Pane for Reviews ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Vendor Reviews", vendorScrollPane);
        tabbedPane.addTab("Order Reviews", orderScrollPane);

        // --- Back Button ---
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 140, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> openVendorMainPage());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(backButton);

        // --- Main Panel Layout ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Reads the Review.txt file and populates the vendorReviews and orderReviews lists.
     * Only reviews with a matching Vendor Name are added.
     */
    private void loadReviews(List<String[]> vendorReviews, List<String[]> orderReviews) {
        File reviewFile = new File("src/main/resources/txtfile/Review.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFile))) {
            String line;
            // Initialize fields for review details.
            String reviewID = "";
            String orderID = "";
            String reviewType = "";
            String customerName = "";
            String reviewVendorName = "";
            String orderMethod = "";
            String remarks = "";
            String datetime = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Review ID: ")) {
                    reviewID = line.substring(11).trim();
                } else if (line.startsWith("Order ID: ")) {
                    orderID = line.substring(10).trim();
                } else if (line.startsWith("Review Type: ")) {
                    reviewType = line.substring(13).trim();
                } else if (line.startsWith("Customer Name: ")) {
                    customerName = line.substring(15).trim();
                } else if (line.startsWith("Vendor Name: ")) {
                    reviewVendorName = line.substring(13).trim();
                } else if (line.startsWith("Order Method: ")) {
                    orderMethod = line.substring(14).trim();
                } else if (line.startsWith("Remarks: ")) {
                    remarks = line.substring(9).trim();
                } else if (line.startsWith("Datetime: ")) {
                    datetime = line.substring(10).trim();
                } else if (line.startsWith("----------")) {
                    // End of a review block. Only add if the review's vendor matches.
                    if (reviewVendorName.equalsIgnoreCase(vendorName)) {
                        if ("Vendor".equalsIgnoreCase(reviewType)) {
                            vendorReviews.add(new String[]{reviewID, customerName, remarks, datetime});
                        } else if ("Order".equalsIgnoreCase(reviewType)) {
                            orderReviews.add(new String[]{reviewID, orderID, customerName, orderMethod, remarks, datetime});
                        }
                    }
                    // Reset all fields for the next review block.
                    reviewID = "";
                    orderID = "";
                    reviewType = "";
                    customerName = "";
                    reviewVendorName = "";
                    orderMethod = "";
                    remarks = "";
                    datetime = "";
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading review data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the Vendor Main Page.
     */
    private void openVendorMainPage() {
        // Replace this with your actual navigation logic.
        new VendorMain(vendorName).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendorReviewPage("Arest Cafe").setVisible(true));
    }
}
