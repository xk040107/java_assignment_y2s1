
package com.mycompany.fooddeliverysystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class CustomerAddressPage extends javax.swing.JFrame {
    private static String customerName;
    
    public CustomerAddressPage(String customerName) {
        this.customerName = customerName;
        initComponents();
        Username.setText(customerName); // Set the username on the JLabel
        displayinstruction();
        loadUserAddress();
        this.setLocationRelativeTo(null);

    }    
    
    //Method to display instructions for user
    private void displayinstruction(){
        JOptionPane.showMessageDialog(this, "You can change the default address here used for delivery purpose", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
     // Method to load the Addresses for the logged-in user
    private void loadUserAddress() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Users.txt"))) {
            String line;
            boolean isUserFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ") && line.substring(10).equals(customerName)) {
                    isUserFound = true;
                } else if (isUserFound && line.startsWith("Home Address:")) {
                    String HomeAddress = line.substring(14); // Extract the Home address value
                    homeAddress.setText(HomeAddress); // Display Home Address
                } else if (isUserFound && line.startsWith("Work Address:")) {
                    String WorkAddress = line.substring(14); // Extract the work address value
                    workAddress.setText(WorkAddress); // Display Work Address
                } else if (isUserFound && line.startsWith("Other Address:")) {
                    String OtherAddress = line.substring(15); // Extract the other address value
                    otherAddress.setText(OtherAddress); // Display Other Address
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
    }
}    
    
    //update default address in Users.txt 
    private boolean updateDefaultAddressInFile(String username, String newDefaultAddress) {
    File inputFile = new File("src/main/resources/txtfile/Users.txt");
    File tempFile = new File("src/main/resources/txtfile/Users_temp.txt");
    boolean isUpdated = false;

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        boolean isUserFound = false;

        while ((line = reader.readLine()) != null) {
            // Check if the line contains the Username
            if (line.startsWith("Username: ") && line.substring(10).equals(username)) {
                isUserFound = true;
                writer.write(line + System.lineSeparator()); // Write the Username line
            } else if (isUserFound && line.startsWith("Default Address:")) {
                // Update the Default Address
                writer.write("Default Address: " + newDefaultAddress + System.lineSeparator());
                isUpdated = true;
                isUserFound = false; // Reset the flag after updating
            } else {
                writer.write(line + System.lineSeparator()); // Write other lines as is
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }

    // Replace the original file with the updated file
    if (isUpdated && inputFile.delete()) {
        tempFile.renameTo(inputFile);
    } else {
        tempFile.delete();
    }

    return isUpdated;
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        homeSetAsDefault = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        homeAddress = new javax.swing.JLabel();
        homeAsDefault = new javax.swing.JButton();
        editHomeButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        WorkSetAsDefault = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        workAddress = new javax.swing.JLabel();
        workAsDefault = new javax.swing.JButton();
        editWorkButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        otherSetAsDefault = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        otherAddress = new javax.swing.JLabel();
        editOtherAddressButton = new javax.swing.JButton();
        otherAsDefault = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        Username = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        homeSetAsDefault.setBackground(new java.awt.Color(204, 204, 255));

        jLabel4.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel4.setText("HOME ADDRESS:");

        homeAddress.setText("jLabel1");

        homeAsDefault.setText("Set as default");
        homeAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeAsDefaultActionPerformed(evt);
            }
        });

        editHomeButton.setText("Edit");
        editHomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editHomeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout homeSetAsDefaultLayout = new javax.swing.GroupLayout(homeSetAsDefault);
        homeSetAsDefault.setLayout(homeSetAsDefaultLayout);
        homeSetAsDefaultLayout.setHorizontalGroup(
            homeSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeSetAsDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(homeAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(homeSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(homeAsDefault, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(editHomeButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        homeSetAsDefaultLayout.setVerticalGroup(
            homeSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeSetAsDefaultLayout.createSequentialGroup()
                .addGroup(homeSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(homeSetAsDefaultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(homeAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(homeSetAsDefaultLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(homeAsDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editHomeButton)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeSetAsDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeSetAsDefault, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        WorkSetAsDefault.setBackground(new java.awt.Color(204, 204, 255));

        jLabel5.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel5.setText("Work Address:");

        workAddress.setText("jLabel2");

        workAsDefault.setText("Set as default");
        workAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workAsDefaultActionPerformed(evt);
            }
        });

        editWorkButton.setText("Edit");
        editWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editWorkButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WorkSetAsDefaultLayout = new javax.swing.GroupLayout(WorkSetAsDefault);
        WorkSetAsDefault.setLayout(WorkSetAsDefaultLayout);
        WorkSetAsDefaultLayout.setHorizontalGroup(
            WorkSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkSetAsDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(WorkSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(workAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addGroup(WorkSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(workAsDefault, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(editWorkButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        WorkSetAsDefaultLayout.setVerticalGroup(
            WorkSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkSetAsDefaultLayout.createSequentialGroup()
                .addGroup(WorkSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkSetAsDefaultLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(WorkSetAsDefaultLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(workAsDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editWorkButton)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WorkSetAsDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WorkSetAsDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        otherSetAsDefault.setBackground(new java.awt.Color(204, 204, 255));

        jLabel6.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel6.setText("Other address:");

        otherAddress.setText("jLabel3");

        editOtherAddressButton.setText("Edit");
        editOtherAddressButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editOtherAddressButtonActionPerformed(evt);
            }
        });

        otherAsDefault.setText("Set as default");
        otherAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherAsDefaultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout otherSetAsDefaultLayout = new javax.swing.GroupLayout(otherSetAsDefault);
        otherSetAsDefault.setLayout(otherSetAsDefaultLayout);
        otherSetAsDefaultLayout.setHorizontalGroup(
            otherSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherSetAsDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(otherAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
                .addGroup(otherSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editOtherAddressButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(otherAsDefault, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        otherSetAsDefaultLayout.setVerticalGroup(
            otherSetAsDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherSetAsDefaultLayout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(otherAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, otherSetAsDefaultLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(otherAsDefault)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editOtherAddressButton)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(otherSetAsDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(otherSetAsDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Address");

        Username.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Username.setText("Username");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(backButton)
                .addGap(227, 227, 227))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(158, 158, 158))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(backButton)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editHomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editHomeButtonActionPerformed
        //initialize Edit Home Address Page
        EditAddressPage editAddressPage= new EditAddressPage(customerName);
        editAddressPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_editHomeButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        //initialize  Customer Main Page
        CustomerMainPage customerMainPage= new CustomerMainPage(customerName);
        customerMainPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_backButtonActionPerformed

    private void editWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editWorkButtonActionPerformed
        //initialize Edit Work Address Page
        EditWorkAddressPage editWorkAddressPage= new EditWorkAddressPage(customerName);
        editWorkAddressPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_editWorkButtonActionPerformed

    private void editOtherAddressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editOtherAddressButtonActionPerformed
        // initialize Edit Other Address Page
        EditOtherAddressPage editOtherAddressPage= new EditOtherAddressPage(customerName);
        editOtherAddressPage.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_editOtherAddressButtonActionPerformed

    private void homeAsDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeAsDefaultActionPerformed
    homeAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeAsDefaultActionPerformed(evt);
            }
        });                                    
    // Change colors
    homeSetAsDefault.setBackground(new java.awt.Color(0, 255, 0)); // Green
    WorkSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish
    otherSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish

    // Update the "Default Address" in the file
    if (updateDefaultAddressInFile(customerName, "Home Address")) {
        JOptionPane.showMessageDialog(this, "Home Address set as default successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Error setting Home Address as default.", "Error", JOptionPane.ERROR_MESSAGE);
    }  
    }//GEN-LAST:event_homeAsDefaultActionPerformed

    private void workAsDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workAsDefaultActionPerformed
    workAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            workAsDefaultActionPerformed(evt);
            }
        });
    // Change colors
    WorkSetAsDefault.setBackground(new java.awt.Color(0, 255, 0)); // Green
    homeSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish
    otherSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish

    // Update the "Default Address" in the file
    if (updateDefaultAddressInFile(customerName, "Work Address")) {
        JOptionPane.showMessageDialog(this, "Work Address set as default successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Error setting Work Address as default.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_workAsDefaultActionPerformed

    private void otherAsDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherAsDefaultActionPerformed
    otherAsDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            otherAsDefaultActionPerformed(evt);
            }
        });
    // Change colors
    otherSetAsDefault.setBackground(new java.awt.Color(0, 255, 0)); // Green
    homeSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish
    WorkSetAsDefault.setBackground(new java.awt.Color(204,204,255)); // dark blue ish

    // Update the "Default Address" in the file
    if (updateDefaultAddressInFile(customerName, "Other Address")) {
        JOptionPane.showMessageDialog(this, "Other Address set as default successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Error setting Other Address as default.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_otherAsDefaultActionPerformed

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
            java.util.logging.Logger.getLogger(CustomerAddressPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerAddressPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerAddressPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerAddressPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerAddressPage(customerName).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Username;
    private javax.swing.JPanel WorkSetAsDefault;
    private javax.swing.JButton backButton;
    private javax.swing.JButton editHomeButton;
    private javax.swing.JButton editOtherAddressButton;
    private javax.swing.JButton editWorkButton;
    private javax.swing.JLabel homeAddress;
    private javax.swing.JButton homeAsDefault;
    private javax.swing.JPanel homeSetAsDefault;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel otherAddress;
    private javax.swing.JButton otherAsDefault;
    private javax.swing.JPanel otherSetAsDefault;
    private javax.swing.JLabel workAddress;
    private javax.swing.JButton workAsDefault;
    // End of variables declaration//GEN-END:variables
}
