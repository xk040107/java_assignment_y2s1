
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class CustomerTransactionHistory extends javax.swing.JFrame {
    private static String customerName;
    private List<Transaction> transactionList = new ArrayList<>(); // To store all transactions

    public CustomerTransactionHistory(String customerName) {
        initComponents();
        this.customerName = customerName;
        Username.setText(customerName); // Set the username on the JLabel
        loadUserBalance(); // Load and display the balance
        loadUserDefaultAddress(); // Load and display address
        loadTransactions(); // Load transactions from the file
        displayTransactions("Payment");
        this.setLocationRelativeTo(null);
    }

    // Method to load transactions from the file
    private void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Transaction.txt"))) {
            String line;
            Transaction transaction = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Transaction ID: ")) {
                    transaction = new Transaction();
                    transaction.setTransactionID(line.substring(16).trim());
                } else if (line.startsWith("Customer Name: ")) {
                    transaction.setCustomerName(line.substring(15).trim());
                } else if (line.startsWith("Item Description: ")) {
                    transaction.setItemDescription(line.substring(18).trim());
                } else if (line.startsWith("Order ID: ")) {
                    transaction.setOrderID(line.substring(10).trim());
                } else if (line.startsWith("Transaction Type: ")) {
                    transaction.setTransactionType(line.substring(18).trim());
                } else if (line.startsWith("Total Amount: ")) {
                    transaction.setTotalAmount(line.substring(14).trim());
                } else if (line.startsWith("Refund Amount: ")) {
                    transaction.setRefundAmount(line.substring(15).trim());
                } else if (line.startsWith("Datetime: ")) {
                    transaction.setDatetime(line.substring(10).trim());
                    transactionList.add(transaction); // Add transaction to the list
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading transaction data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayTransactions(String type) {
    // Filter transactions based on customerName and transaction type
    List<Transaction> filteredTransactions = transactionList.stream()
            .filter(t -> t.getCustomerName().equals(customerName) && t.getTransactionType().equals(type))
            .sorted(Comparator.comparing(Transaction::getDatetime).reversed()) // Sort by datetime descending
            .collect(Collectors.toList());

    // Clear the panel and prepare the table
    ViewTransactionPanel.removeAll();
    ViewTransactionPanel.setLayout(new BorderLayout());

    if (filteredTransactions.isEmpty()) {
        // Display a message if no transactions are found
        JLabel noDataLabel = new JLabel("No transactions found for " + type, SwingConstants.CENTER);
        ViewTransactionPanel.add(noDataLabel, BorderLayout.CENTER);
    } else {
        // Create a table to display the transactions
        String[] columnNames = {"Transaction ID", "Description/Order ID", "Amount", "Datetime"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Transaction t : filteredTransactions) {
            String description = t.getItemDescription() != null ? t.getItemDescription() : t.getOrderID();
            String amount = t.getTotalAmount() != null ? t.getTotalAmount() : t.getRefundAmount();
            String formattedDatetime = LocalDateTime.parse(t.getDatetime()).format(formatter);

            tableModel.addRow(new Object[]{t.getTransactionID(), description, amount, formattedDatetime});
        }

        JTable transactionTable = new JTable(tableModel);
        transactionTable.setEnabled(false); // Make table read-only
        transactionTable.setRowHeight(30); // Set row height for better visibility
        transactionTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Create a scroll pane for the table and set it to match the panel size
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Ensure the table and scroll pane take up the full size of ViewTransactionPanel
        scrollPane.setPreferredSize(ViewTransactionPanel.getSize());
        scrollPane.setMinimumSize(ViewTransactionPanel.getSize());
        scrollPane.setMaximumSize(ViewTransactionPanel.getSize());

        ViewTransactionPanel.add(scrollPane, BorderLayout.CENTER);
    }

    ViewTransactionPanel.revalidate();
    ViewTransactionPanel.repaint();
}

    
    
    // Method to load the balance for the logged-in user
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

        customerFunctionBackground = new javax.swing.JPanel();
        orderStatusbtn = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        orderHistorybtn = new javax.swing.JButton();
        menuPagebtn = new javax.swing.JButton();
        Balancebtn4 = new javax.swing.JButton();
        feedbackButton = new javax.swing.JButton();
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
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ViewTransactionPanel = new javax.swing.JPanel();
        paymentButton = new javax.swing.JButton();
        refundButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        customerFunctionBackground.setBackground(new java.awt.Color(102, 255, 255));

        orderStatusbtn.setText("Order Status");
        orderStatusbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderStatusbtnActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
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
            .addComponent(orderStatusbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(78, 78, 78))
            .addComponent(menuPagebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Balancebtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(feedbackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(orderStatusbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Balancebtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitButton)
                .addGap(31, 31, 31))
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
                .addComponent(Balance, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(94, 94, 94)
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

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setText("Transaction History");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(15, 15, 15))
        );

        ViewTransactionPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout ViewTransactionPanelLayout = new javax.swing.GroupLayout(ViewTransactionPanel);
        ViewTransactionPanel.setLayout(ViewTransactionPanelLayout);
        ViewTransactionPanelLayout.setHorizontalGroup(
            ViewTransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 763, Short.MAX_VALUE)
        );
        ViewTransactionPanelLayout.setVerticalGroup(
            ViewTransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 435, Short.MAX_VALUE)
        );

        paymentButton.setText("Payment");
        paymentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentButtonActionPerformed(evt);
            }
        });

        refundButton.setText("Refund");
        refundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refundButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(paymentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(refundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(330, 330, 330)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 336, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ViewTransactionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paymentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(ViewTransactionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void orderStatusbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderStatusbtnActionPerformed
        //initialize Order status page
        CustomerOrderStatus customerOrderStatusPage= new CustomerOrderStatus(customerName);
        customerOrderStatusPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderStatusbtnActionPerformed

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

    private void orderHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderHistorybtnActionPerformed
        //initialize Order history page
        CustomerOrderHistory customerOrderHistoryPage= new CustomerOrderHistory(customerName);
        customerOrderHistoryPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderHistorybtnActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        //exit system
       JOptionPane.showMessageDialog(this,"Thank you for using the system!! Have a good day!!!!");
       java.lang.System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

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
        //initialize Notification Page
        NotificationPage notificationPage= new NotificationPage(customerName);
        notificationPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_notificationbtnActionPerformed

    private void paymentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentButtonActionPerformed
        // TODO add your handling code here:
        displayTransactions("Payment");
    }//GEN-LAST:event_paymentButtonActionPerformed

    private void refundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refundButtonActionPerformed
        // TODO add your handling code here:
        displayTransactions("Refund");
    }//GEN-LAST:event_refundButtonActionPerformed

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
            java.util.logging.Logger.getLogger(CustomerTransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerTransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerTransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerTransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerTransactionHistory(customerName).setVisible(true);
            }
        });
    }
    // Transaction class to represent each transaction
    class Transaction {
        private String transactionID;
        private String customerName;
        private String itemDescription;
        private String orderID;
        private String transactionType;
        private String totalAmount;
        private String refundAmount;
        private String datetime;

        // Getters and setters
        public String getTransactionID() { return transactionID; }
        public void setTransactionID(String transactionID) { this.transactionID = transactionID; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
        public String getOrderID() { return orderID; }
        public void setOrderID(String orderID) { this.orderID = orderID; }
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
        public String getTotalAmount() { return totalAmount; }
        public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
        public String getRefundAmount() { return refundAmount; }
        public void setRefundAmount(String refundAmount) { this.refundAmount = refundAmount; }
        public String getDatetime() { return datetime; }
        public void setDatetime(String datetime) { this.datetime = datetime; }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JLabel Balance;
    private javax.swing.JButton Balancebtn4;
    private javax.swing.JButton TopUpbtn;
    private javax.swing.JLabel Username;
    private javax.swing.JPanel ViewTransactionPanel;
    private javax.swing.JButton addressButton;
    private javax.swing.JPanel customerFunctionBackground;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton feedbackButton;
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
    private javax.swing.JButton orderStatusbtn;
    private javax.swing.JButton paymentButton;
    private javax.swing.JButton refundButton;
    // End of variables declaration//GEN-END:variables
}
