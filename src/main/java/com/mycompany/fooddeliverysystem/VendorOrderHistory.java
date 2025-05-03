package com.mycompany.fooddeliverysystem;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
 
public class VendorOrderHistory extends JFrame {
    private String vendorName;
    private JPanel orderHistoryPanel;
    private JButton backButton;
    
    // New member variables for filtering functionality
    private JTable orderTable;
    private List<String[]> allCompletedOrders;
    private final String[] columnNames = {
        "Order ID", "Transaction ID", "Item Description", "Order Method",
        "Total Price", "Order Status", "Allocated Runner", "Runner Status", "Date Completed"
    };

    public VendorOrderHistory(String vendorName) {
        this.vendorName = vendorName;
        setTitle("Vendor Order History - " + vendorName);
        setSize(1500, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Order History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(255, 140, 0));
        titleLabel.setPreferredSize(new Dimension(getWidth(), 50));

        // Order History Panel
        orderHistoryPanel = new JPanel(new BorderLayout());
        orderHistoryPanel.setBackground(Color.WHITE);
        displayOrderHistoryTable();

        // Back Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 140, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> openVendorMainPage());
        buttonPanel.add(backButton);

        // Add Components
        add(titleLabel, BorderLayout.NORTH);
        add(orderHistoryPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void displayOrderHistoryTable() {
        // Load all orders once and store them for filtering
        allCompletedOrders = loadCompletedOrders();
        // Initially, show all orders
        List<String[]> filteredOrders = new ArrayList<>(allCompletedOrders);

        String[][] tableData = filteredOrders.toArray(new String[0][0]);

        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Make "Item Description" column editable
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        orderTable.setRowHeight(40);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        orderTable.getTableHeader().setBackground(new Color(255, 165, 0));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setGridColor(Color.LIGHT_GRAY);
        orderTable.setSelectionBackground(new Color(255, 200, 100));
        orderTable.setSelectionForeground(Color.BLACK);
        

        // In displayOrderHistoryTable(), after creating the table and before adding the scrollPane:
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Order ID column longer
        columnModel.getColumn(1).setPreferredWidth(160); // Transaction ID column longer
        columnModel.getColumn(2).setPreferredWidth(150); // Item Description column shorter


        // Set Custom Renderer & Editor for "Item Description"
        orderTable.getColumn("Item Description").setCellRenderer(new ItemDescriptionRenderer());
        orderTable.getColumn("Item Description").setCellEditor(new ItemDescriptionEditor(orderTable));

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        String[] filterOptions = {"All Orders", "Daily", "Monthly", "Quarterly"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterComboBox.addActionListener(e -> {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            updateOrderTable(selectedFilter);
        });
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add filter panel and table to orderHistoryPanel
        orderHistoryPanel.removeAll();
        orderHistoryPanel.add(filterPanel, BorderLayout.NORTH);
        orderHistoryPanel.add(scrollPane, BorderLayout.CENTER);
        orderHistoryPanel.revalidate();
        orderHistoryPanel.repaint();
    }

    private void updateOrderTable(String filterType) {
    List<String[]> filteredOrders = new ArrayList<>();
    LocalDate today = LocalDate.now();
    for (String[] order : allCompletedOrders) {
        String dateStr = order[8];
        // If date is invalid or missing, include only in "All Orders" view
        if (dateStr == null || dateStr.equals("-") || dateStr.isEmpty()) {
            if (filterType.equals("All Orders")) {
                filteredOrders.add(order);
            }
            continue;
        }
        try {
            // Parse the full date-time and extract the date part.
            LocalDateTime orderDateTime = LocalDateTime.parse(dateStr);
            LocalDate orderDate = orderDateTime.toLocalDate();
            boolean matches = false;
            switch (filterType) {
                case "Daily":
                    matches = orderDate.equals(today);
                    break;
                case "Monthly":
                    matches = (orderDate.getYear() == today.getYear() && orderDate.getMonth() == today.getMonth());
                    break;
                case "Quarterly":
                    int orderQuarter = (orderDate.getMonthValue() - 1) / 3 + 1;
                    int todayQuarter = (today.getMonthValue() - 1) / 3 + 1;
                    matches = (orderDate.getYear() == today.getYear() && orderQuarter == todayQuarter);
                    break;
                case "All Orders":
                default:
                    matches = true;
                    break;
            }
            if (matches) {
                filteredOrders.add(order);
            }
        } catch (Exception ex) {
            // If date parsing fails, include in "All Orders" view
            if (filterType.equals("All Orders")) {
                filteredOrders.add(order);
            }
        }
    }
    // Update table model with filtered orders
    String[][] tableData = filteredOrders.toArray(new String[0][0]);
    DefaultTableModel newModel = new DefaultTableModel(tableData, columnNames) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }
    };
    

    orderTable.setModel(newModel);
    // Reapply custom renderer & editor for "Item Description"
    orderTable.getColumn("Item Description").setCellRenderer(new ItemDescriptionRenderer());
    orderTable.getColumn("Item Description").setCellEditor(new ItemDescriptionEditor(orderTable));

            TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Order ID column longer
        columnModel.getColumn(1).setPreferredWidth(160); // Transaction ID column longer
        columnModel.getColumn(2).setPreferredWidth(150); // Item Description column shorter
    }


    private void openVendorMainPage() {
        new VendorMain(vendorName).setVisible(true);
        this.dispose();
    }

    class ItemDescriptionRenderer extends JButton implements TableCellRenderer {
        public ItemDescriptionRenderer() {
            setOpaque(true);
            setBackground(Color.ORANGE);
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View");
            return this;
        }
    }

    private List<String[]> loadCompletedOrders() {
        List<String[]> completedOrders = new ArrayList<>();
        List<String[]> tempOrders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Order.txt"))) {
            String line;
            String[] orderData = new String[9];
            boolean isVendorOrder = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Order ID: ")) {
                    orderData[0] = line.substring(10).trim();
                } else if (line.startsWith("Transaction ID: ")) {
                    orderData[1] = line.substring(15).trim();
                } else if (line.startsWith("Vendor Name: ")) {
                    isVendorOrder = line.substring(13).trim().equalsIgnoreCase(vendorName);
                } else if (line.startsWith("Item Description: ")) {
                    orderData[2] = line.substring(18).trim();
                } else if (line.startsWith("Order Method: ")) {
                    orderData[3] = line.substring(14).trim();
                } else if (line.startsWith("Total Price: ")) {
                    orderData[4] = line.substring(13).trim();
                } else if (line.startsWith("Order Status: ")) {
                    orderData[5] = line.substring(14).trim();
                } else if (line.startsWith("Allocated Runner: ")) {
                    orderData[6] = line.substring(18).trim();
                } else if (line.startsWith("Runner Status: ")) {
                    orderData[7] = line.substring(15).trim();
                } else if (line.startsWith("Date Completed: ")) {
                    orderData[8] = line.substring(15).trim();
                } else if (line.startsWith("----------")) {
                    if (isVendorOrder && orderData[5].equalsIgnoreCase("Completed")) {
                        if (orderData[3].equalsIgnoreCase("Dine-In") || orderData[3].equalsIgnoreCase("Take Away")) {
                            orderData[6] = "-";
                            orderData[7] = "-";
                        }
                        if (orderData[8] == null) {
                            orderData[8] = "-";
                        }
                        tempOrders.add(orderData.clone());
                    }
                    Arrays.fill(orderData, null);
                    isVendorOrder = false;
                }
            }

            for (int i = tempOrders.size() - 1; i >= 0; i--) {
                completedOrders.add(tempOrders.get(i));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return completedOrders;
    }

    class ItemDescriptionEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private JTable table;
        private int selectedRow;
        private String itemDescription;

        public ItemDescriptionEditor(JTable table) {
            this.table = table;
            button = new JButton("View");
            button.setOpaque(true);
            button.setBackground(Color.ORANGE);
            button.setForeground(Color.WHITE);

            button.addActionListener(e -> {
                handleViewButtonClick();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.selectedRow = row;
            this.itemDescription = (String) table.getValueAt(row, 2);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return itemDescription;
        }

        private void handleViewButtonClick() {
            String vendorName = VendorOrderHistory.this.vendorName;
            displayOrderItems(vendorName, itemDescription);
        }
    }

    private void displayOrderItems(String vendorName, String itemDescription) {
        String[] items = itemDescription.split(",\\s*");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        for (String item : items) {
            String quantity = item.replaceAll(".*x (\\d+)", "$1");
            String foodName = item.replaceAll(" x \\d+", "").trim();

            String imagePath = getItemPicturePath(vendorName, foodName);

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
            ));
            itemPanel.setBackground(Color.WHITE);

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
            imageLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 3));
            itemPanel.add(imageLabel, BorderLayout.WEST);

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

        JOptionPane.showMessageDialog(this, scrollPane, "Order Items", JOptionPane.INFORMATION_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendorOrderHistory("Thong Kee").setVisible(true));
    }
}
