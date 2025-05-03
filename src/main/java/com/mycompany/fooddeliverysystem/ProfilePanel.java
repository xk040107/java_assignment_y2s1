package com.mycompany.fooddeliverysystem;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ProfilePanel extends javax.swing.JPanel {
    
    private String RunnerName;
    public ProfilePanel(String RunnerName) {
        initComponents();
        this.RunnerName = RunnerName;
        loadProfilePicture(RunnerName);
        displayRunnerProfile(RunnerName);

    }
        private void uploadProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Load the image and set it to the profile picture label
                ImageIcon profileImage = new ImageIcon(selectedFile.getAbsolutePath());
                Image img = profileImage.getImage();
                Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Scale the image to fit the square
                profilepiclbl.setIcon(new ImageIcon(scaledImg)); // Set the image to the label

                // Save the image file path
                saveProfilePicturePath(RunnerName, selectedFile.getAbsolutePath());

                // Show a success dialog with the uploaded profile picture
                ImageIcon scaledIcon = new ImageIcon(scaledImg);
                JOptionPane.showMessageDialog(
                    this,
                    "Profile picture uploaded successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE,
                    scaledIcon
                );
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    private void saveProfilePicturePath(String RunnerName, String filePath) {
        File originalFile = new File("src/main/resources/txtfile/RunnerInfo.txt");
        StringBuilder updatedContent = new StringBuilder();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                updatedContent.append(line).append(System.lineSeparator());
                if (line.equals("Username: " + RunnerName)) {
                    userFound = true;
                    updatedContent.append("ProfilePic: ").append(filePath).append(System.lineSeparator());
                    reader.readLine(); // Skip the next line as it's the old profile pic
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading profile data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add new user if not found
        if (!userFound) {
            updatedContent.append("Username: ").append(RunnerName).append(System.lineSeparator());
            updatedContent.append("ProfilePic: ").append(filePath).append(System.lineSeparator());
        }

        // Write the updated content back to the same file
        try (FileWriter writer = new FileWriter(originalFile)) {
            writer.write(updatedContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    private void loadProfilePicture(String RunnerName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/RunnerInfo.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Username: " + RunnerName)) {
                    String profilePicLine = reader.readLine();
                    if (profilePicLine != null && profilePicLine.startsWith("ProfilePic: ")) {
                        String filePath = profilePicLine.substring("ProfilePic: ".length());
                        File imageFile = new File(filePath);
                        if (imageFile.exists()) {
                            ImageIcon profileImage = new ImageIcon(filePath);
                            Image img = profileImage.getImage();
                            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                            profilepiclbl.setIcon(new ImageIcon(scaledImg));
                        } else {
                            JOptionPane.showMessageDialog(this, "Profile picture file not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile picture", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayRunnerProfile(String runnerName) {
        File filename = new File("src/main/resources/txtfile/Users.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String userCode = "";
            String email = "";
            String dob = "";

            boolean isMatchFound = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("User Code: ")) {
                    userCode = line.split(": ")[1].trim();
                } else if (line.startsWith("Username: ")) {
                    String currentUsername = line.split(": ")[1].trim();
                    if (currentUsername.equals(runnerName)) {
                        isMatchFound = true; // Confirm the match
                    }
                } else if (isMatchFound) {
                    if (line.startsWith("Email: ")) {
                        email = line.split(": ")[1].trim();
                    } else if (line.startsWith("Date of Birth: ")) {
                        dob = line.split(": ")[1].trim();
                    } else if (line.startsWith("-----------------------")) {
                        break; // Stop after completing the current user's details
                    }
                }
            }

            // Update labels based on whether a match was found
            if (isMatchFound) {
                lblUsernameProfile.setText("Username: " + runnerName);
                lblUserCodeProfile.setText("User Code: " + userCode);
                lblEmailProfile.setText("Email: " + email);
                lblDOBProfile.setText("Date of Birth: " + dob);
            } else {
                lblUsernameProfile.setText("Username: Not found");
                lblUserCodeProfile.setText("User Code: N/A");
                lblEmailProfile.setText("Email: N/A");
                lblDOBProfile.setText("Date of Birth: N/A");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        profilePanel = new javax.swing.JPanel();
        profilepiclbl = new javax.swing.JLabel();
        btnUploadProfilePic = new javax.swing.JButton();
        lblUsernameProfile = new javax.swing.JLabel();
        lblUserCodeProfile = new javax.swing.JLabel();
        lblEmailProfile = new javax.swing.JLabel();
        lblDOBProfile = new javax.swing.JLabel();

        setBackground(new java.awt.Color(192, 222, 242));
        setMaximumSize(new java.awt.Dimension(1010, 490));
        setMinimumSize(new java.awt.Dimension(1010, 490));
        setPreferredSize(new java.awt.Dimension(1010, 490));

        profilePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        profilePanel.setPreferredSize(new java.awt.Dimension(180, 180));
        profilePanel.setLayout(new java.awt.GridBagLayout());

        profilepiclbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 210;
        gridBagConstraints.gridheight = 228;
        profilePanel.add(profilepiclbl, gridBagConstraints);

        btnUploadProfilePic.setBackground(new java.awt.Color(18, 76, 154));
        btnUploadProfilePic.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        btnUploadProfilePic.setForeground(new java.awt.Color(255, 255, 255));
        btnUploadProfilePic.setText("Upload Profile Pic");
        btnUploadProfilePic.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnUploadProfilePic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadProfilePicActionPerformed(evt);
            }
        });

        lblUsernameProfile.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lblUsernameProfile.setForeground(new java.awt.Color(18, 76, 154));

        lblUserCodeProfile.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lblUserCodeProfile.setForeground(new java.awt.Color(18, 76, 154));
        lblUserCodeProfile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        lblEmailProfile.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lblEmailProfile.setForeground(new java.awt.Color(18, 76, 154));

        lblDOBProfile.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lblDOBProfile.setForeground(new java.awt.Color(18, 76, 154));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(profilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(166, 166, 166)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblEmailProfile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUsernameProfile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDOBProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserCodeProfile, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(btnUploadProfilePic, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(244, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUsernameProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUserCodeProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(lblEmailProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDOBProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnUploadProfilePic, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadProfilePicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadProfilePicActionPerformed
        uploadProfilePicture();
    }//GEN-LAST:event_btnUploadProfilePicActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUploadProfilePic;
    private javax.swing.JLabel lblDOBProfile;
    private javax.swing.JLabel lblEmailProfile;
    private javax.swing.JLabel lblUserCodeProfile;
    private javax.swing.JLabel lblUsernameProfile;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JLabel profilepiclbl;
    // End of variables declaration//GEN-END:variables
}
