
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class CustomerOrderHistory extends javax.swing.JFrame {
    private static String customerName;

public CustomerOrderHistory(String customerName) {
    initComponents();
    this.customerName = customerName;
    Username.setText(customerName);
    loadUserBalance();
    loadUserDefaultAddress();
    displayOrderHistoryTable();
    this.setLocationRelativeTo(null);
}

private List<String[]> loadCompletedOrders() {
    List<String[]> completedOrders = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Order.txt"))) {
        String line;
        String[] orderData = new String[9]; // Adjust array size to include all columns
        boolean isCustomerOrder = false;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            if (line.startsWith("Order ID: ")) {
                orderData[0] = line.substring(10).trim();
            } else if (line.startsWith("Transaction ID: ")) {
                orderData[1] = line.substring(15).trim();
            } else if (line.startsWith("Customer Name: ")) {
                String orderCustomerName = line.substring(15).trim();
                isCustomerOrder = orderCustomerName.equalsIgnoreCase(customerName); // Match customer name
            } else if (line.startsWith("Vendor Name: ")) {
                orderData[5] = line.substring(13).trim();
            } else if (line.startsWith("Item Description: ")) {
                orderData[3] = line.substring(18).trim();
            } else if (line.startsWith("Order Method: ")) {
                orderData[2] = line.substring(14).trim();
            } else if (line.startsWith("Total Price: ")) {
                orderData[4] = line.substring(13).trim();
            } else if (line.startsWith("Order Status: ")) {
                orderData[6] = line.substring(14).trim();
            } else if (line.startsWith("Runner Status: ")) {
                orderData[7] = line.substring(15).trim();
            } else if (line.startsWith("----------")) {
                if (isCustomerOrder && orderData[6] != null && orderData[6].equalsIgnoreCase("Completed")) {
                    if (orderData[2].equalsIgnoreCase("Dine-In") || orderData[2].equalsIgnoreCase("Take Away")) {
                        orderData[7] = "N/A";
                    }
                    completedOrders.add(orderData.clone());
                }
                Arrays.fill(orderData, null);
                isCustomerOrder = false; // Reset flag for the next order
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    return completedOrders;
}

// Custom TableCellEditor and Renderer for the "Actions" column
class ButtonEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private JPanel panel;
    private JButton reviewButton;
    private JButton reorderButton;
    private JTable table; // Add a reference to the table

    public ButtonEditorRenderer(JTable table) {
        this.table = table; // Initialize the table reference

        // Create panel with buttons
        panel = new JPanel(new GridLayout(1, 2, 10, 0)); // Horizontal layout for buttons
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

        reviewButton = new JButton("Review");
        reorderButton = new JButton("Reorder");

        // Set consistent size for buttons
        reviewButton.setPreferredSize(new java.awt.Dimension(100, 30));
        reorderButton.setPreferredSize(new java.awt.Dimension(100, 30));

        // Add buttons to the panel
        panel.add(reviewButton);
        panel.add(reorderButton);

        // Add action listeners for the buttons
        reviewButton.addActionListener(e -> {
            String orderId = (String) table.getValueAt(table.getSelectedRow(), 0); // Order ID
            orderReview(orderId);
        });

        reorderButton.addActionListener(e -> {
            String vendorName = (String) table.getValueAt(table.getSelectedRow(), 5); // Vendor Name
            String itemDescription = (String) table.getValueAt(table.getSelectedRow(), 3); // Item Description
            handleReorder(vendorName, customerName, itemDescription);
        });
    }
    
    

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return panel; // Return the panel with buttons for rendering
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel; // Return the panel with buttons for editing
    }

    @Override
    public Object getCellEditorValue() {
        return null; // No specific value needs to be returned
    }
}

// Action for "Review" button
    private void orderReview(String orderId) {
        OrderReviewForm orderReviewForm= new OrderReviewForm(this.customerName,orderId);
        orderReviewForm.setVisible(true);
        this.setVisible(false);
    }

private void displayOrderHistoryTable() {
    // Load completed orders
    List<String[]> completedOrders = loadCompletedOrders();

    // Define column headers (Address removed)
    String[] columnNames = {
        "Order ID", "Transaction ID", "Order Method", "Item Description", 
        "Total Price", "Vendor Name", "Order Status", "Runner Status", "Actions"
    };

    // Convert the list of completed orders to a 2D array
    String[][] tableData = completedOrders.toArray(new String[0][0]);

    // Create the table model
    DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
        @Override
       public boolean isCellEditable(int row, int column) {
           return column == 3 || column == 8;  // Make both Item Description and Actions columns editable
       }
    };

    JTable table = new JTable(tableModel);
    
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    // Set custom row height for better spacing
    table.setRowHeight(40); // Increase the row height to 40px for better visibility

    // Set the custom renderer and editor for the "Actions" column
    table.getColumn("Actions").setCellRenderer(new ButtonEditorRenderer(table));
    table.getColumn("Actions").setCellEditor(new ButtonEditorRenderer(table));

    // Add renderer and editor for "Item Description" column (index 3)
    table.getColumn("Item Description").setCellRenderer(new ItemDescriptionRenderer());
    table.getColumn("Item Description").setCellEditor(new ItemDescriptionEditor(table));

    // Set fixed column sizes (adjusted widths for better visibility)
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(120); // Order ID
    columnModel.getColumn(1).setPreferredWidth(180); // Transaction ID
    columnModel.getColumn(2).setPreferredWidth(110); // Order Method
    columnModel.getColumn(3).setPreferredWidth(250); // Item Description
    columnModel.getColumn(4).setPreferredWidth(90); // Total Price
    columnModel.getColumn(5).setPreferredWidth(140); // Vendor Name
    columnModel.getColumn(6).setPreferredWidth(120); // Order Status
    columnModel.getColumn(7).setPreferredWidth(120); // Runner Status
    columnModel.getColumn(8).setPreferredWidth(250); // Actions (buttons)

    // Ensure table is scrollable
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    // Make the table fit the size of the orderHistoryPanel
    scrollPane.setPreferredSize(orderHistoryPanel.getSize());
    scrollPane.setMaximumSize(orderHistoryPanel.getSize());
    scrollPane.setMinimumSize(orderHistoryPanel.getSize());

    // Add the table to the panel
    orderHistoryPanel.setLayout(new BorderLayout());
    orderHistoryPanel.add(scrollPane, BorderLayout.CENTER);

    // Refresh the panel to display the table
    orderHistoryPanel.revalidate();
    orderHistoryPanel.repaint();
}

// Renderer for "Item Description" column
class ItemDescriptionRenderer extends JButton implements TableCellRenderer {
    public ItemDescriptionRenderer() {
        setOpaque(true);
        setBackground(Color.ORANGE); // Set the button background to orange
        setForeground(Color.WHITE);  // Set the text color to white
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText("View"); // Always show "View" text
        return this;
    }
}

class ItemDescriptionEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;
    private JTable table;
    private int selectedRow;
    private String itemDescription;  // Store item description

    public ItemDescriptionEditor(JTable table) {
        this.table = table;
        button = new JButton("View");
        button.setOpaque(true);
        button.setBackground(Color.ORANGE);
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> {
            handleViewButtonClick();  // Handle click first
            fireEditingStopped();  // Then stop editing after the action
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.selectedRow = row;
        this.itemDescription = (String) table.getValueAt(row, 3);  // Get actual item description from the table
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return itemDescription;  // Return the actual item description, not "View"
    }

    private void handleViewButtonClick() {
        String vendorName = (String) table.getValueAt(selectedRow, 5);  // Get Vendor Name
        displayOrderItems(vendorName, itemDescription);  // Display items using the stored description
    }
}



private void displayOrderItems(String vendorName, String itemDescription) {
    String[] items = itemDescription.split(",\\s*");  // Split items by comma

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBackground(Color.WHITE);

    for (String item : items) {
        // Extract quantity and food name
        String quantity = item.replaceAll(".*x (\\d+)", "$1");
        String foodName = item.replaceAll(" x \\d+", "").trim();

        // Get picture path from the menu
        String imagePath = getItemPicturePath(vendorName, foodName);

        // Create panel for each item
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
        ));
        itemPanel.setBackground(Color.WHITE);

        // Load and display image
        JLabel imageLabel;
        if (imagePath != null) {
            ImageIcon itemImage = new ImageIcon(imagePath);
            Image scaledImage = itemImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            imageLabel = new JLabel("Image Not Found");
            imageLabel.setPreferredSize(new Dimension(150, 150));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 3)); // Orange border
        itemPanel.add(imageLabel, BorderLayout.WEST);

        // Display food name and quantity
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("<html><b>" + foodName + "</b></html>");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel quantityLabel = new JLabel("Quantity: " + quantity);
        quantityLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        quantityLabel.setForeground(Color.DARK_GRAY);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(quantityLabel);

        itemPanel.add(textPanel, BorderLayout.CENTER);

        mainPanel.add(itemPanel);
    }

    JScrollPane scrollPane = new JScrollPane(mainPanel);
    scrollPane.setPreferredSize(new Dimension(600, 500));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Order Items",
            JOptionPane.INFORMATION_MESSAGE
    );
}

private String getItemPicturePath(String vendorName, String foodName) {
    File menuFile = new File("src/main/resources/txtfile/Menu.txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(menuFile))) {
        String line;
        boolean isMatchingVendor = false;
        String currentPicturePath = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Vendor Name: ") && line.substring(13).trim().equalsIgnoreCase(vendorName.trim())) {
                isMatchingVendor = true;
            } else if (isMatchingVendor && line.startsWith("Picture File Path: ")) {
                currentPicturePath = line.substring(19).trim();  // Store picture path
            } else if (isMatchingVendor && line.startsWith("Food Name: ")) {
                String menuFoodName = line.substring(11).trim();
                if (menuFoodName.equalsIgnoreCase(foodName.trim())) {
                    return currentPicturePath;  // Return the stored picture path
                }
            } else if (line.startsWith("-----------------------")) {
                isMatchingVendor = false;  // Reset for the next vendor
                currentPicturePath = null;  // Reset picture path for next item
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading menu data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    return null;  // Return null if no image is found
}

private void handleReorder(String vendorName, String customerName, String itemDescription) {
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reorder these items?", "Reorder Confirmation", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        String[] items = itemDescription.split(",");
        File cartFile = new File("src/main/resources/txtfile/Cart.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFile, true))) {
            long baseTimestamp = System.currentTimeMillis();  // Base timestamp for the first item
            
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                String[] itemParts = item.split("x");
                
                if (itemParts.length < 2) {
                    JOptionPane.showMessageDialog(this, "Invalid item format: " + item, "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                
                String foodName = itemParts[0].trim();
                int quantity;
                
                try {
                    quantity = Integer.parseInt(itemParts[1].trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity for item: " + foodName, "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Fetch the price from Menu.txt
                double pricePerItem = fetchPriceFromMenu(vendorName, foodName);
                if (pricePerItem == 0.0) {
                    JOptionPane.showMessageDialog(this, "Price for item '" + foodName + "' not found in menu.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                double totalPrice = pricePerItem * quantity;

                // Generate unique Cart ID ensuring it remains 13 digits
                long cartIDNumber = baseTimestamp + i;  // Ensure uniqueness by incrementing
                String cartID = "CART" + cartIDNumber;  // This will always have 13 digits after 'CART'

                // Write the cart entry to cart.txt
                writer.write(cartID);
                writer.newLine();
                writer.write("Vendor Name: " + vendorName);
                writer.newLine();
                writer.write("Customer Name: " + customerName);
                writer.newLine();
                writer.write("Food Name: " + foodName);
                writer.newLine();
                writer.write("Quantity: " + quantity);
                writer.newLine();
                writer.write("Price per Item: RM" + String.format("%.2f", pricePerItem));
                writer.newLine();
                writer.write("Total Price: RM" + String.format("%.2f", totalPrice));
                writer.newLine();
                writer.write("Status: Pending");
                writer.newLine();
                writer.write("----------");
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Items successfully added to cart!", "Reorder Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to cart file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}



private double fetchPriceFromMenu(String vendorName, String foodName) {
    File menuFile = new File("src/main/resources/txtfile/Menu.txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(menuFile))) {
        String line;
        boolean isCorrectVendor = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Vendor Name: ") && line.substring(13).trim().equalsIgnoreCase(vendorName.trim())) {
                isCorrectVendor = true;
            } else if (isCorrectVendor && line.startsWith("Food Name: ")) {
                // Normalize strings for comparison
                String menuFoodName = line.substring(11).trim().toLowerCase();
                if (menuFoodName.equals(foodName.trim().toLowerCase())) {
                    // Extract the price
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Price: RM")) {
                            return Double.parseDouble(line.substring(9).trim());
                        }
                    }
                }
            } else if (line.startsWith("-----------------------")) {
                isCorrectVendor = false; // Reset for the next vendor block
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading menu data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    return 0.0; // Default price if not found
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

        jPanel1 = new javax.swing.JPanel();
        customerFunctionBackground = new javax.swing.JPanel();
        orderStatusbtn = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        transactionHistorybtn = new javax.swing.JButton();
        menuPagebtn = new javax.swing.JButton();
        Balancebtn4 = new javax.swing.JButton();
        feedbackButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        orderHistoryPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        Username = new javax.swing.JLabel();
        notificationbtn1 = new javax.swing.JButton();
        logoLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        TopUpbtn = new javax.swing.JButton();
        Balance = new javax.swing.JLabel();
        logoutbtn = new javax.swing.JButton();
        addressButton = new javax.swing.JButton();
        Address = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        customerFunctionBackground.setBackground(new java.awt.Color(102, 255, 255));

        orderStatusbtn.setText("Order Status");
        orderStatusbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderStatusbtnActionPerformed(evt);
            }
        });

        jButton12.setText("Exit");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel4.setText("Feature");

        transactionHistorybtn.setText("Transaction History");
        transactionHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionHistorybtnActionPerformed(evt);
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
            .addComponent(transactionHistorybtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(orderStatusbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(78, 78, 78))
            .addComponent(menuPagebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Balancebtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
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
                .addComponent(transactionHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(orderStatusbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Balancebtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton12)
                .addGap(62, 62, 62))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setText("Order History");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout orderHistoryPanelLayout = new javax.swing.GroupLayout(orderHistoryPanel);
        orderHistoryPanel.setLayout(orderHistoryPanelLayout);
        orderHistoryPanelLayout.setHorizontalGroup(
            orderHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1114, Short.MAX_VALUE)
        );
        orderHistoryPanelLayout.setVerticalGroup(
            orderHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 442, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(orderHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(469, 469, 469)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        Username.setText("Username ");

        notificationbtn1.setText("Notification");
        notificationbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationbtn1ActionPerformed(evt);
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
                .addComponent(Balance, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(71, 71, 71)
                .addComponent(addressButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Address, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationbtn1)
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
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(addressButton)
                                .addComponent(Address))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(notificationbtn1)
                                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(logoutbtn))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void orderStatusbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderStatusbtnActionPerformed
        //initialize Transaction page
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
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutbtnActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        //exit system
       JOptionPane.showMessageDialog(this,"Thank you for using the system!! Have a good day!!!!");
       java.lang.System.exit(0);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void transactionHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionHistorybtnActionPerformed
        //initialize Transaction page
        CustomerTransactionHistory customerTransactionPage= new CustomerTransactionHistory(customerName);
        customerTransactionPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_transactionHistorybtnActionPerformed

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

    private void notificationbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationbtn1ActionPerformed
        //initialize notification Page
        NotificationPage notificationPage= new NotificationPage(customerName);
        notificationPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_notificationbtn1ActionPerformed

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
            java.util.logging.Logger.getLogger(CustomerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerOrderHistory(customerName).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JLabel Balance;
    private javax.swing.JButton Balancebtn4;
    private javax.swing.JButton TopUpbtn;
    private javax.swing.JLabel Username;
    private javax.swing.JButton addressButton;
    private javax.swing.JPanel customerFunctionBackground;
    private javax.swing.JButton feedbackButton;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel logoLabel1;
    private javax.swing.JButton logoutbtn;
    private javax.swing.JButton menuPagebtn;
    private javax.swing.JButton notificationbtn1;
    private javax.swing.JPanel orderHistoryPanel;
    private javax.swing.JButton orderStatusbtn;
    private javax.swing.JButton transactionHistorybtn;
    // End of variables declaration//GEN-END:variables
}
