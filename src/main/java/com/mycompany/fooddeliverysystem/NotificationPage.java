
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class NotificationPage extends javax.swing.JFrame {
private static String customerName;

 
    public NotificationPage(String customerName) {
        this.customerName = customerName;
        initComponents();
        Username.setText(customerName); // Set the username on the JLabel
        initNotificationBar();
        loadUserBalance(); // Load and display the balance
        loadUserDefaultAddress(); // Load and display the default address
         this.setLocationRelativeTo(null);
    }
    

private void initNotificationBar() {
    // Create a JScrollPane to make the notification bar scrollable
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    // Ensure the scrollPane is fixed to the size of notificationBar
    scrollPane.setPreferredSize(notificationBar.getSize());
    scrollPane.setMinimumSize(notificationBar.getSize());
    scrollPane.setMaximumSize(notificationBar.getSize());

    // Create a JPanel to hold the notification buttons
    JPanel notificationPanel = new JPanel();
    notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));

    // Load notifications and add buttons dynamically
    List<Notification> notifications = loadNotifications();
    Dimension buttonSize = new Dimension(notificationBar.getWidth(), 60); // Adjust height for multi-line text
    List<JButton> notificationButtons = new ArrayList<>(); // Keep track of all buttons

    for (Notification notification : notifications) {
        // Format datetime to display only date and time
        String formattedDatetime = formatDatetime(notification.getDatetime());

        // Use HTML for multi-line text in the button
        String buttonText = "<html><div style='text-align: center;'><b>" + notification.getTitle() + "</b><br>" + formattedDatetime + "</div></html>";
        JButton notificationButton = new JButton(buttonText);

        // Center-align the text horizontally and vertically
        notificationButton.setHorizontalAlignment(SwingConstants.CENTER);
        notificationButton.setVerticalAlignment(SwingConstants.CENTER);

        // Fix the size of the button to fit the width of the notification bar
        notificationButton.setPreferredSize(buttonSize);
        notificationButton.setMinimumSize(buttonSize);
        notificationButton.setMaximumSize(buttonSize);

        // Set default button color to white and add a border
        notificationButton.setBackground(Color.WHITE);
        notificationButton.setOpaque(true); // To ensure the background color is applied
        notificationButton.setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // Add light gray border

        // Add an action listener to display details and handle color changes
        notificationButton.addActionListener(e -> {
            // Change all buttons back to white and reset their border
            for (JButton button : notificationButtons) {
                button.setBackground(Color.WHITE);
                button.setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // Reset border to default
            }
            // Change the clicked button to orange and add a highlighted border
            notificationButton.setBackground(Color.ORANGE);
            notificationButton.setBorder(new LineBorder(Color.ORANGE, 2));

            // Display the notification details
            displayNotificationDetails(notification);
        });

        // Add the button to the panel and list
        notificationPanel.add(notificationButton);
        notificationButtons.add(notificationButton);
    }

    // Add the notification panel to the scroll pane
    scrollPane.setViewportView(notificationPanel);

    // Increase scroll speed
    scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Set scroll speed (20 pixels per tick)

    // Add the scroll pane to the notificationBar panel in the JFrame
    notificationBar.setLayout(new BorderLayout());
    notificationBar.add(scrollPane, BorderLayout.CENTER);
}

// Helper method to format datetime
private String formatDatetime(String isoDatetime) {
    // Parse the ISO-8601 datetime string
    LocalDateTime dateTime = LocalDateTime.parse(isoDatetime);

    // Define the desired output format (dd/MM/yyyy HH:mm)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Format and return the datetime
    return dateTime.format(formatter);
}



private void displayNotificationDetails(Notification notification) {
    // Clear the notificationDetails panel
    notificationDetails.removeAll();

    // Create a JPanel for the notification details (this will be scrollable)
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

    // Create labels for the notification title and date & time
    JLabel titleLabel = new JLabel("<html><b>Title:</b> " + notification.getTitle() + "</html>");
    JLabel dateTimeLabel = new JLabel("<html><b>Date & Time:</b> " + formatDatetime(notification.getDatetime()) + "</html>");

    // Create a JTextArea for the details with word wrapping and scrolling
    JTextArea detailTextArea = new JTextArea(notification.getDetail());
    detailTextArea.setLineWrap(true); // Enable line wrapping
    detailTextArea.setWrapStyleWord(true); // Wrap by word, not character
    detailTextArea.setEditable(false); // Make the text area read-only
    detailTextArea.setCaretPosition(0); // Ensure the text starts at the top

    // Add the JTextArea to a JScrollPane
    JScrollPane detailScrollPane = new JScrollPane(detailTextArea);
    detailScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    detailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    detailScrollPane.setPreferredSize(new Dimension(notificationDetails.getWidth(), 100)); // Adjust height as needed

    // Add components to the details panel
    detailsPanel.add(titleLabel);
    detailsPanel.add(Box.createVerticalStrut(10)); // Add spacing between elements
    detailsPanel.add(dateTimeLabel);
    detailsPanel.add(Box.createVerticalStrut(10)); // Add spacing between elements
    detailsPanel.add(new JLabel("<html><b>Details:</b></html>"));
    detailsPanel.add(detailScrollPane); // Add the scrollable text area

    // Add the details panel to a JScrollPane to make the whole section scrollable
    JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
    detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    // Fix the size of the details scroll pane to match notificationDetails
    detailsScrollPane.setPreferredSize(notificationDetails.getSize());
    detailsScrollPane.setMinimumSize(notificationDetails.getSize());
    detailsScrollPane.setMaximumSize(notificationDetails.getSize());

    // Add the scrollable details panel to the notificationDetails panel
    notificationDetails.setLayout(new BorderLayout());
    notificationDetails.add(detailsScrollPane, BorderLayout.CENTER);

    // Refresh the notificationDetails panel
    notificationDetails.revalidate();
    notificationDetails.repaint();
}


private List<Notification> loadNotifications() {
    List<Notification> notifications = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Notification.txt"))) {
        String line;
        Notification notification = null;
        StringBuilder detailBuilder = null; // For building multiline details

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Notification ID: ")) {
                if (notification != null && notification.getCustomerName().equals(customerName)) {
                    // Save the previous notification
                    notifications.add(notification);
                }
                // Start a new notification
                notification = new Notification();
                notification.setId(line.substring(16).trim());
                detailBuilder = null; // Reset detail builder
            } else if (line.startsWith("Title: ")) {
                notification.setTitle(line.substring(7).trim());
            } else if (line.startsWith("Customer Name: ")) {
                notification.setCustomerName(line.substring(15).trim());
            } else if (line.startsWith("Notification Detail: ")) {
                detailBuilder = new StringBuilder(line.substring(20).trim()); // Start building details
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("Datetime: ")) {
                        notification.setDatetime(line.substring(10).trim());
                        break; // Stop reading details at Datetime
                    }
                    if (line.startsWith("----------")) {
                        break; // Stop if we hit the delimiter
                    }
                    // Append line to details if not a delimiter
                    detailBuilder.append("\n").append(line);
                }
                if (detailBuilder != null) {
                    notification.setDetail(detailBuilder.toString().trim());
                }
            } else if (line.startsWith("Datetime: ")) {
                notification.setDatetime(line.substring(10).trim());
                if (notification.getCustomerName().equals(customerName)) {
                    notifications.add(notification);
                }
                notification = null; // Reset for the next notification
            }
        }

        // Add the last notification if it exists
        if (notification != null && notification.getCustomerName().equals(customerName)) {
            notifications.add(notification);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading notification data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Sort notifications by datetime (latest first)
    notifications.sort((n1, n2) -> n2.getDatetime().compareTo(n1.getDatetime()));
    return notifications;
}




// Notification class to hold notification details (unchanged)
class Notification {
    private String id;
    private String title;
    private String customerName;
    private String detail;
    private String datetime;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }
}

    
    // Method to load the balance for the logged-in user
    private void loadUserBalance() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            boolean isUserFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Credit: RM")) {
                    String credit = line.substring(7); // Extract the balance value
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
        Username = new javax.swing.JLabel();
        notificationbtn = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        TopUpbtn2 = new javax.swing.JButton();
        Balance = new javax.swing.JLabel();
        logoutbtn = new javax.swing.JButton();
        addressButton = new javax.swing.JButton();
        Address = new javax.swing.JLabel();
        CustomerBackground = new javax.swing.JPanel();
        notificationBar = new javax.swing.JPanel();
        notificationDetails = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        customerFunctionBackground = new javax.swing.JPanel();
        transactionHistorybtn = new javax.swing.JButton();
        orderHistorybtn = new javax.swing.JButton();
        orderStatusbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Balancebtn4 = new javax.swing.JButton();
        menuPageButton = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        feedbackButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        Username.setText("Username ");

        notificationbtn.setText("Notification");
        notificationbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationbtnActionPerformed(evt);
            }
        });

        logoLabel.setText("LOGO");

        TopUpbtn2.setText("TOP UP");
        TopUpbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TopUpbtn2ActionPerformed(evt);
            }
        });

        Balance.setText("Balance");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TopUpbtn2)
                .addGap(18, 18, 18)
                .addComponent(Balance, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TopUpbtn2)
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82)
                .addComponent(addressButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Address, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationbtn)
                .addGap(4, 4, 4)
                .addComponent(logoutbtn)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addressButton)
                        .addComponent(Address))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(notificationbtn)
                        .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(logoutbtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(13, 13, 13))
        );

        CustomerBackground.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout notificationBarLayout = new javax.swing.GroupLayout(notificationBar);
        notificationBar.setLayout(notificationBarLayout);
        notificationBarLayout.setHorizontalGroup(
            notificationBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );
        notificationBarLayout.setVerticalGroup(
            notificationBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 536, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout notificationDetailsLayout = new javax.swing.GroupLayout(notificationDetails);
        notificationDetails.setLayout(notificationDetailsLayout);
        notificationDetailsLayout.setHorizontalGroup(
            notificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
        );
        notificationDetailsLayout.setVerticalGroup(
            notificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Notification");

        javax.swing.GroupLayout CustomerBackgroundLayout = new javax.swing.GroupLayout(CustomerBackground);
        CustomerBackground.setLayout(CustomerBackgroundLayout);
        CustomerBackgroundLayout.setHorizontalGroup(
            CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notificationBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CustomerBackgroundLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(262, 262, 262))
                    .addGroup(CustomerBackgroundLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(notificationDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))))
        );
        CustomerBackgroundLayout.setVerticalGroup(
            CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerBackgroundLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(CustomerBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(CustomerBackgroundLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(notificationDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(notificationBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        transactionHistorybtn.setText("Transaction History");
        transactionHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionHistorybtnActionPerformed(evt);
            }
        });

        orderHistorybtn.setText("Order History");
        orderHistorybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderHistorybtnActionPerformed(evt);
            }
        });

        orderStatusbtn.setText("Order Status");
        orderStatusbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderStatusbtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Feature");

        Balancebtn4.setText("Balance");
        Balancebtn4.setMaximumSize(new java.awt.Dimension(96, 23));
        Balancebtn4.setMinimumSize(new java.awt.Dimension(96, 23));
        Balancebtn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Balancebtn4ActionPerformed(evt);
            }
        });

        menuPageButton.setText("Menu Page");
        menuPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPageButtonActionPerformed(evt);
            }
        });

        jButton12.setText("Exit");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
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
            .addComponent(Balancebtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(menuPageButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(orderHistorybtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(78, 78, 78))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerFunctionBackgroundLayout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
            .addComponent(feedbackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        customerFunctionBackgroundLayout.setVerticalGroup(
            customerFunctionBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerFunctionBackgroundLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(menuPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(transactionHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(orderStatusbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Balancebtn4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(orderHistorybtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(jButton12)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CustomerBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(customerFunctionBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CustomerBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void notificationbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationbtnActionPerformed
        //initialize Notification Page
        NotificationPage notificationPage= new NotificationPage(customerName);
        notificationPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_notificationbtnActionPerformed

    private void TopUpbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TopUpbtn2ActionPerformed
        //initialize balance page
        CustomerBalancePage customerBalancePage= new CustomerBalancePage(customerName);
        customerBalancePage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_TopUpbtn2ActionPerformed

    private void logoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutbtnActionPerformed
        //initialize login page
        dispose();
        // Open the login page
        new LoginPage(); // Replace this with the actual class name for your login page
    }//GEN-LAST:event_logoutbtnActionPerformed

    private void addressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressButtonActionPerformed
        //initialize Address Page
        CustomerAddressPage customerAddressPage= new CustomerAddressPage(customerName);
        customerAddressPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_addressButtonActionPerformed

    private void transactionHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionHistorybtnActionPerformed
        //initialize Transaction page
        CustomerTransactionHistory customerTransactionPage= new CustomerTransactionHistory(customerName);
        customerTransactionPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_transactionHistorybtnActionPerformed

    private void orderHistorybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderHistorybtnActionPerformed
        //initialize Order history page
        CustomerOrderHistory customerOrderHistory= new CustomerOrderHistory(customerName);
        customerOrderHistory.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderHistorybtnActionPerformed

    private void orderStatusbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderStatusbtnActionPerformed
        //initialize Transaction page
        CustomerOrderStatus customerOrderStatusPage= new CustomerOrderStatus(customerName);
        customerOrderStatusPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_orderStatusbtnActionPerformed

    private void Balancebtn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Balancebtn4ActionPerformed
        //initialize balance page
        CustomerBalancePage customerPage= new CustomerBalancePage(customerName);
        customerPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_Balancebtn4ActionPerformed

    private void menuPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPageButtonActionPerformed
        //initialize Maim page
        CustomerMainPage customerMainPage= new CustomerMainPage(customerName);
        customerMainPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_menuPageButtonActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        //exit system
        JOptionPane.showMessageDialog(this,"Thank you for using the system!! Have a good day!!!!");
        java.lang.System.exit(0);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void feedbackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feedbackButtonActionPerformed
        //intialize feedback page
        FeedbackPage feedback= new FeedbackPage(customerName);
        feedback.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_feedbackButtonActionPerformed

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
            java.util.logging.Logger.getLogger(NotificationPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NotificationPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NotificationPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NotificationPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NotificationPage(customerName).setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JLabel Balance;
    private javax.swing.JButton Balancebtn4;
    private javax.swing.JPanel CustomerBackground;
    private javax.swing.JButton TopUpbtn2;
    private javax.swing.JLabel Username;
    private javax.swing.JButton addressButton;
    private javax.swing.JPanel customerFunctionBackground;
    private javax.swing.JButton feedbackButton;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton logoutbtn;
    private javax.swing.JButton menuPageButton;
    private javax.swing.JPanel notificationBar;
    private javax.swing.JPanel notificationDetails;
    private javax.swing.JButton notificationbtn;
    private javax.swing.JButton orderHistorybtn;
    private javax.swing.JButton orderStatusbtn;
    private javax.swing.JButton transactionHistorybtn;
    // End of variables declaration//GEN-END:variables
}
