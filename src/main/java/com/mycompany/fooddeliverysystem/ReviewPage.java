package com.mycompany.fooddeliverysystem;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class ReviewPage extends javax.swing.JFrame {
    private static String customerName;
    private static String vendorName;
    private JPanel reviewContentPanel;
    private boolean isMyReviewView = false; // ✅ Declare the missing variable

    public ReviewPage(String customerName, String vendorName) {
        this.customerName = customerName;
        this.vendorName = vendorName;
        initComponents();
        displayReviewDetails();
        loadReviews(); // Load all reviews by default
        this.setLocationRelativeTo(null);
    }

    private void displayReviewDetails() {
        VendorName.setText(vendorName);
    }

    // Load all reviews for this vendor
    private void loadReviews() {
        isMyReviewView = false; // Show all reviews
        loadReviewsFromFile(null); // Passing null loads all reviews
    }

    // Load only the current user's reviews
    private void loadMyReviews() {
        isMyReviewView = true; // Show only user's reviews
        loadReviewsFromFile(customerName);
    }

    private void loadReviewsFromFile(String filterCustomer) {
        String filePath = "src/main/resources/txtfile/Review.txt";
        reviewContentPanel.removeAll();
        reviewContentPanel.setLayout(new BoxLayout(reviewContentPanel, BoxLayout.Y_AXIS));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentCustomerName = null;
            String currentVendorName = null;
            String remarks = null;
            String datetime = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer Name: ")) {
                    currentCustomerName = line.substring(15).trim();
                } else if (line.startsWith("Vendor Name: ")) {
                    currentVendorName = line.substring(13).trim();
                } else if (line.startsWith("Remarks: ")) {
                    remarks = line.substring(9).trim();
                } else if (line.startsWith("Datetime: ")) {
                    datetime = line.substring(10).trim();
                } else if (line.equals("----------")) {
                    if (vendorName.equals(currentVendorName) && (filterCustomer == null || filterCustomer.equals(currentCustomerName))) {
                        addReviewPanel(currentCustomerName, datetime, remarks);
                    }
                    currentCustomerName = null;
                    currentVendorName = null;
                    remarks = null;
                    datetime = null;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading reviews.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        reviewContentPanel.revalidate();
        reviewContentPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    private void addReviewPanel(String customerName, String datetime, String remarks) {
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BorderLayout());
        reviewPanel.setPreferredSize(new Dimension(600, 100));
        reviewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Top Panel for customer name and datetime
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel customerLabel = new JLabel("<html><b>" + customerName + "</b></html>");
        JLabel datetimeLabel = new JLabel("<html><i>" + datetime + "</i></html>");
        datetimeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        topPanel.add(customerLabel);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(datetimeLabel);

        // Remarks
        JLabel remarksLabel = new JLabel("<html><p>" + remarks + "</p></html>");
        remarksLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        remarksLabel.setVerticalAlignment(SwingConstants.TOP);

        // Add components to review panel
        reviewPanel.add(topPanel, BorderLayout.NORTH);
        reviewPanel.add(remarksLabel, BorderLayout.CENTER);

        reviewContentPanel.add(reviewPanel);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        VendorName = new javax.swing.JLabel();
        BackButton = new javax.swing.JButton();
        MyReviewButton = new javax.swing.JButton();
        WriteReviewButton = new javax.swing.JButton();
        reviewContentPanel = new JPanel();
        JScrollPane reviewScrollPane = new JScrollPane(reviewContentPanel);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Header Panel to align Vendor Name & Review Label
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        VendorName.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 36));
        VendorName.setText(vendorName);
        jLabel1.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 36));
        jLabel1.setText("REVIEW");
        headerPanel.add(VendorName);
        headerPanel.add(jLabel1);

        // Back Button
        BackButton.setText("BACK");
        BackButton.setPreferredSize(new Dimension(150, 40));
        BackButton.addActionListener(evt -> {
            CustomerMainPage customerMainPage = new CustomerMainPage(customerName);
            customerMainPage.setVisible(true);
            this.dispose();
        });

        // My Review Button (Shows only logged-in user's reviews)
        MyReviewButton.setText("My Review");
        MyReviewButton.setPreferredSize(new Dimension(150, 40));
        MyReviewButton.addActionListener(evt -> loadMyReviews());

        // Write Review Button (Placeholder action)
        WriteReviewButton.setText("Write Review");
        WriteReviewButton.setPreferredSize(new Dimension(150, 40));
        WriteReviewButton.addActionListener(evt -> writeReviewForm());

        // Scroll Panel
        reviewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        reviewScrollPane.setPreferredSize(new Dimension(700, 400));

        // Button Panel for Center Alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.add(BackButton);
        buttonPanel.add(MyReviewButton);
        buttonPanel.add(WriteReviewButton);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(headerPanel) // Header Panel for Vendor Name & REVIEW
                .addComponent(reviewScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buttonPanel) // Centered Button Panel
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(headerPanel) // Header Panel
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(reviewScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(buttonPanel) // Centered Button Panel
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);
        pack();
    }
    // Action for "Review" button
    private void writeReviewForm() {
        System.out.println("Customer Name: " + customerName);
        System.out.println("Vendor Name: " + vendorName);
        
        WriteReviewForm writeReviewForm= new WriteReviewForm(customerName,vendorName);
        writeReviewForm.setVisible(true);
        this.setVisible(false);
    }
   
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReviewPage("DefaultUser", "Arest Cafe").setVisible(true));
    }

    private javax.swing.JButton BackButton;
    private javax.swing.JButton MyReviewButton;
    private javax.swing.JButton WriteReviewButton;
    private javax.swing.JLabel VendorName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
}