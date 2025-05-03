/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author USER
 */
public class ManagerMain extends javax.swing.JFrame {
    private JPanel navigationPanel, contentPanel;
    private JButton dashboardBtn, performanceBtn, customerComplaintsBtn, itemListBtn, logoutBtn;
    private CardLayout cardLayout;
    
    /**
     * Creates new form ManagerMain
     */
    public ManagerMain() {
        initComponents();
        // Set JFrame properties
        setTitle("Manager Panel");
        setSize(1500, 750);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize content panel here
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
    
         // Initialize panels
        initNavigationPanel();
        initHeaderPanel();
                
        // Add panels to frame
        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        setVisible(true);

    }   
    
    // Create a header panel to contain the title and logout button
    private void initHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(1250, 70));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Logout Button
        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.LIGHT_GRAY);
        logoutBtn.setForeground(Color.BLACK);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(2, 3 , 2, 3)); // Spacing
        logoutBtn.setPreferredSize(new Dimension(110, 20));

        // Add action listener for logout
        logoutBtn.addActionListener(e -> logout());

        // Add components to the header panel
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        // Add the header panel to the frame
        add(headerPanel, BorderLayout.NORTH);
    }

    // Logout Function
    private void logout() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            javax.swing.JOptionPane.showMessageDialog(this, "Logout successful!", "Logout", javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // Close ManagerMain and open LoginPage
            this.dispose();  // Close current window
            new LoginPage().setVisible(true);  // Open LoginPage
        }
    }
   
    // Navigation Panel Setup
    private void initNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.BLACK);
        navigationPanel.setPreferredSize(new Dimension(250, 750));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS)); // Vertical layout

    // Load Logo Image
    JLabel logoLabel = new JLabel();
    try {
        // Assuming the logo is in the "resources" folder or project root
        ImageIcon logoIcon = new ImageIcon("apuLogo.png"); // Update path as needed
        java.awt.Image img = logoIcon.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(img)); // Set scaled icon
    } catch (Exception e) {
        System.out.println("Logo image not found: " + e.getMessage());
    }
    
    logoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); // Center the logo horizontally
    logoLabel.setPreferredSize(new Dimension(150, 150));
    logoLabel.setMaximumSize(new Dimension(150, 150)); // Prevent excessive resizing
    navigationPanel.add(logoLabel); // Add the logo to the top of the panel

    // Add spacing between logo and buttons
    navigationPanel.add(Box.createVerticalStrut(20)); // 20px space
    
        // Create buttons
        dashboardBtn = createNavButton("Dashboard");
        performanceBtn = createNavButton("Performance");
        customerComplaintsBtn = createNavButton("Customer Complaints");
        itemListBtn = createNavButton("Item List");

        // Set button preferred size
        Dimension buttonSize = new Dimension(250, 60);
        dashboardBtn.setPreferredSize(buttonSize);
        performanceBtn.setPreferredSize(buttonSize);
        customerComplaintsBtn.setPreferredSize(buttonSize);
        itemListBtn.setPreferredSize(buttonSize);

        // Add buttons to the panel
        navigationPanel.add(dashboardBtn);
        navigationPanel.add(performanceBtn);
        navigationPanel.add(customerComplaintsBtn);
        navigationPanel.add(itemListBtn);

        // Add button listeners
        dashboardBtn.addActionListener(e -> switchPanel("Dashboard"));
        performanceBtn.addActionListener(e -> switchPanel("Performance"));
        customerComplaintsBtn.addActionListener(e -> switchPanel("Customer Complaints"));
        itemListBtn.addActionListener(e -> switchPanel("Item List"));

        // Add content panels
        contentPanel.add(new RevenueDashboardPanel(), "Dashboard");
        contentPanel.add(new DeliveryRunnerPerformance(), "Performance");
        contentPanel.add(new CustomerComplaintsPanel(), "Customer Complaints");
        contentPanel.add(new VendorItemsPanel(), "Item List");

        // Show the default panel
        cardLayout.show(contentPanel, "Dashboard");

        setVisible(true);

    }
    
    private JButton createNavButton(String title) {
        JButton btn = new JButton(title);
        btn.setBackground(new Color(56, 116, 203));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));

        // Enforce button size
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setMaximumSize(new Dimension(250, 50)); // Critical for BoxLayout
        btn.setAlignmentX(JButton.CENTER_ALIGNMENT); // Center-align button

        return btn;
    }
    
    private void switchPanel(String title) {
        cardLayout.show(contentPanel, title);
        titleLabel.setText(title); 
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusCycleRoot(false);
        setSize(new java.awt.Dimension(1500, 750));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1175, 1175, 1175)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1254, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(1194, 1194, 1194)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1195, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(933, 933, 933)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(399, 399, 399)
                    .addComponent(lblLogo)
                    .addContainerGap(569, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
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
            java.util.logging.Logger.getLogger(ManagerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManagerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManagerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManagerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManagerMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
