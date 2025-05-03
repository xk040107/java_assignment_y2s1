package com.mycompany.fooddeliverysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VendorManageItems extends JFrame {
    private String vendorName;

    public VendorManageItems(String vendorName) {
        this.vendorName = vendorName;

        setTitle("Manage Items - " + vendorName);
        setSize(1500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Main layout for the frame

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout()); // For vertical alignment of the Add button
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));
        leftPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Add Button
        JButton addButton = new JButton("Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setBackground(new Color(255, 165, 0)); // Orange background
        addButton.setForeground(Color.WHITE); // White text
        addButton.setFocusPainted(false);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(200, getHeight())); // Full vertical height
        addHoverEffect(addButton); // Add hover effect to "Add Item" button
        addButton.addActionListener(e -> openAddItemDialog());
        leftPanel.add(addButton, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST); // Add to the left side of the frame

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center alignment
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 60));
        bottomPanel.setBackground(new Color(220, 220, 220)); // Slightly darker gray background

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 165, 0)); // Orange background
        backButton.setForeground(Color.WHITE); // White text
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(120, 40));
        addHoverEffect(backButton); // Add hover effect to "Back" button
        backButton.addActionListener(e -> goBack());
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH); // Add to the bottom of the frame

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE); // Blank white background
        add(rightPanel, BorderLayout.CENTER);

        // Load vendor-specific menu items into the right panel
        loadVendorMenu();


        setVisible(true);
    }
    
private void loadVendorMenu() {
    // Create a JPanel with a GridLayout for 5 cards per row
    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new GridLayout(0, 5, 10, 10)); // Dynamic rows, 5 columns, 10px spacing
    menuPanel.setBackground(Color.WHITE);

    try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Menu.txt"))) {
        String line;
        boolean isCurrentVendor = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Vendor Name: ")) {
                String currentVendor = line.substring("Vendor Name: ".length());
                isCurrentVendor = currentVendor.equals(vendorName);
            } else if (isCurrentVendor && line.startsWith("Item Code: ")) {
                String itemCode = line.substring("Item Code: ".length());
                String filePath = reader.readLine().substring("Picture File Path: ".length());
                String foodName = reader.readLine().substring("Food Name: ".length());
                String foodDescription = reader.readLine().substring("Description: ".length());
                String foodPrice = reader.readLine().substring("Price: RM".length());

                // Create a food card for each item
                JPanel foodCard = createFoodCard(itemCode, filePath, foodName, foodDescription, foodPrice);
                menuPanel.add(foodCard);
            }
        }

        // Fill empty grid spaces to maintain 5 columns layout
        int itemCount = menuPanel.getComponentCount();
        int emptySpaces = 5 - (itemCount % 5);
        if (emptySpaces < 5) { // Only add placeholders if needed
            for (int i = 0; i < emptySpaces; i++) {
                JPanel placeholder = new JPanel();
                placeholder.setBackground(Color.WHITE);
                menuPanel.add(placeholder);
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading menu items.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Wrap the menuPanel in a JScrollPane
    JScrollPane scrollPane = new JScrollPane(menuPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

    // Remove any existing scroll pane
    for (Component component : getContentPane().getComponents()) {
        if (component instanceof JScrollPane) {
            remove(component);
        }
    }

    // Add the scroll pane to the center of the layout
    add(scrollPane, BorderLayout.CENTER);

    // Refresh the frame
    revalidate();
    repaint();
}



public class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + hgap * 2;
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }

    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}

private JPanel createFoodCard(String itemCode, String filePath, String foodName, String foodDescription, String foodPrice) {
    JPanel card = new JPanel();
    card.setPreferredSize(new Dimension(240, 420)); // Fixed card size
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2)); // Orange border
    card.setLayout(new BorderLayout(5, 5)); // Use BorderLayout for better positioning

    // Top Panel for Item Code
    JLabel itemCodeLabel = new JLabel(itemCode, SwingConstants.CENTER);
    itemCodeLabel.setFont(new Font("Arial", Font.BOLD, 12));
    itemCodeLabel.setForeground(Color.GRAY);
    card.add(itemCodeLabel, BorderLayout.NORTH);

    // Center Panel for Image, Name, and Description
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(Color.WHITE);

    // Food Image
    JLabel foodImageLabel = new JLabel();
    foodImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    foodImageLabel.setVerticalAlignment(SwingConstants.CENTER);
    foodImageLabel.setPreferredSize(new Dimension(200, 200)); // Fixed square size for the image

    try {
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(filePath);
            Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            foodImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            foodImageLabel.setText("No Image");
            foodImageLabel.setForeground(Color.GRAY);
        }
    } catch (Exception e) {
        foodImageLabel.setText("Error Loading Image");
        foodImageLabel.setForeground(Color.RED);
    }
    centerPanel.add(foodImageLabel, BorderLayout.NORTH);

    // Food Name
    JLabel nameLabel = new JLabel("<html><center>" + foodName + "</center></html>", SwingConstants.CENTER);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    nameLabel.setForeground(Color.BLACK);
    centerPanel.add(nameLabel, BorderLayout.CENTER);

    // Food Description with Scroll Pane
    JTextArea descriptionArea = new JTextArea(foodDescription);
    descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
    descriptionArea.setForeground(Color.DARK_GRAY);
    descriptionArea.setWrapStyleWord(true);
    descriptionArea.setLineWrap(true);
    descriptionArea.setEditable(false);
    descriptionArea.setBackground(Color.WHITE);

    JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
    descriptionScrollPane.setPreferredSize(new Dimension(200, 60)); // Fixed height
    descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    descriptionScrollPane.getVerticalScrollBar().setUnitIncrement(10); // Smooth scrolling
    descriptionScrollPane.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 1)); // Orange border for the scroll pane
    centerPanel.add(descriptionScrollPane, BorderLayout.SOUTH);
    
    customizeScrollPane(descriptionScrollPane);

    card.add(centerPanel, BorderLayout.CENTER);

    // Bottom Panel for Price and Buttons
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(Color.WHITE);

    // Food Price
    JLabel priceLabel = new JLabel("Price: RM " + foodPrice, SwingConstants.CENTER);
    priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
    priceLabel.setForeground(Color.BLACK);
    bottomPanel.add(priceLabel, BorderLayout.NORTH);

    // Buttons
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
    buttonPanel.setBackground(Color.WHITE);

    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    setButtonStyle(editButton);
    setButtonStyle(deleteButton);

    editButton.addActionListener(e -> openEditItemDialog(itemCode, filePath, foodName, foodDescription, foodPrice));
    deleteButton.addActionListener(e -> deleteItem(vendorName, itemCode));

    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);

    bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Add spacing below buttons
    JPanel spacer = new JPanel();
    spacer.setPreferredSize(new Dimension(200, 10)); // Add spacing
    spacer.setBackground(Color.WHITE);
    bottomPanel.add(spacer, BorderLayout.CENTER);

    card.add(bottomPanel, BorderLayout.SOUTH);

    return card;
}

private void customizeScrollPane(JScrollPane scrollPane) {
    // Customize the vertical scroll bar
    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(255, 165, 0); // Orange color for the thumb
            this.trackColor = new Color(255, 235, 204); // Light orange for the track
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton button = super.createDecreaseButton(orientation);
            button.setBackground(new Color(255, 165, 0)); // Orange button
            button.setForeground(Color.WHITE); // White arrow
            return button;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton button = super.createIncreaseButton(orientation);
            button.setBackground(new Color(255, 165, 0)); // Orange button
            button.setForeground(Color.WHITE); // White arrow
            return button;
        }
    });
}



    private void openEditItemDialog(String itemCode, String filePath, String foodName, String foodDescription, String foodPrice) {
        // Create a dialog for editing items
        JDialog editItemDialog = new JDialog(this, "Edit Item", true);
        editItemDialog.setSize(800, 600);
        editItemDialog.setLayout(new BorderLayout(10, 10)); // Top-level layout
        editItemDialog.setLocationRelativeTo(this);
        editItemDialog.getContentPane().setBackground(Color.WHITE); // White background

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 165, 0)); // Orange
        titlePanel.setPreferredSize(new Dimension(editItemDialog.getWidth(), 50));
        JLabel titleLabel = new JLabel("Edit Item");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Form Panel (Center)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout()); // GridBag for flexible layout
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between elements
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Upload Picture Button
        JLabel picLabel = new JLabel(filePath, JLabel.CENTER); // Pre-fill with file path
        picLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        picLabel.setForeground(Color.BLACK);

        JButton uploadButton = new JButton("Change Picture");
        styleButton(uploadButton);

        // Panel for Image Preview
        JPanel imagePreviewPanel = new JPanel();
        imagePreviewPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Center the image
        JLabel imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(150, 150));  // Set preferred size for image preview
        imagePreviewPanel.add(imagePreviewLabel);

        // Pre-load the image preview
        ImageIcon icon = new ImageIcon(filePath);
        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        imagePreviewLabel.setIcon(new ImageIcon(image));

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String newFilePath = selectedFile.getAbsolutePath(); // Get absolute path

                picLabel.setText(newFilePath); // Update the label text directly with the file path

                // Update image preview
                try {
                    ImageIcon newIcon = new ImageIcon(newFilePath);
                    Image newImage = newIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(newImage));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Unable to load image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Food Name
        JLabel nameLabel = new JLabel("Food Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField nameField = new JTextField(foodName, 20); // Pre-fill with current food name

        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField descField = new JTextField(foodDescription, 20); // Pre-fill with current description

        // Price
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField priceField = new JTextField(foodPrice, 20); // Pre-fill with current price

        // Save and Cancel Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> saveEditedItem(vendorName, itemCode, picLabel.getText(), nameField.getText(), descField.getText(), priceField.getText(), editItemDialog));    cancelButton.addActionListener(e -> editItemDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to the Form Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(uploadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(picLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(imagePreviewPanel, gbc);  // Centered image preview panel

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Add components to Dialog
        editItemDialog.add(titlePanel, BorderLayout.NORTH);
        editItemDialog.add(formPanel, BorderLayout.CENTER);

        editItemDialog.setVisible(true);
    }

    private void saveEditedItem(String vendorName, String itemCode, String filePath, String name, String description, String price, JDialog dialog) {
        if (filePath.equals("No File Selected") || name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double priceValue = Double.parseDouble(price);

            // Read the file and update the specific item
            File inputFile = new File("src/main/resources/txtfile/Menu.txt");
            File tempFile = new File("src/main/resources/txtfile/Menu_temp.txt");

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String line;
                boolean isCurrentVendor = false;
                boolean isItemUpdated = false;

                while ((line = reader.readLine()) != null) {
                    // Check for vendor name
                    if (line.startsWith("Vendor Name: ")) {
                        isCurrentVendor = line.substring("Vendor Name: ".length()).equals(vendorName);
                        writer.write(line + System.lineSeparator()); // Write the vendor name as-is
                    } 
                    // Check for item code
                    else if (isCurrentVendor && line.startsWith("Item Code: ")) {
                        String currentItemCode = line.substring("Item Code: ".length());
                        if (currentItemCode.equals(itemCode)) {
                            // Found the item to update; write the updated details
                            writer.write(line + System.lineSeparator()); // Write the item code
                            writer.write("Picture File Path: " + filePath + System.lineSeparator());
                            writer.write("Food Name: " + name + System.lineSeparator());
                            writer.write("Description: " + description + System.lineSeparator());
                            writer.write("Price: RM" + priceValue + System.lineSeparator());
                            writer.write("-----------------------" + System.lineSeparator());
                            isItemUpdated = true;

                            // Skip the old lines for the current item
                            for (int i = 0; i < 5; i++) reader.readLine();
                        } else {
                            // Write the existing item code if not being updated
                            writer.write(line + System.lineSeparator());
                        }
                    } else {
                        // Write all other lines as-is
                        writer.write(line + System.lineSeparator());
                    }
                }

                if (!isItemUpdated) {
                    JOptionPane.showMessageDialog(dialog, "Item not found for editing!", "Error", JOptionPane.ERROR_MESSAGE);
                    tempFile.delete();
                    return;
                }
            }

            // Replace the old file with the updated one
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                JOptionPane.showMessageDialog(dialog, "Error updating the file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(dialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();

            // Refresh the vendor menu after editing
            refreshVendorMenu();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(dialog, "Price must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(dialog, "Error saving the edited item. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }



    private void refreshVendorMenu() {
        // Get the right panel (the menu area)
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                // Remove the scroll pane (menu panel)
                getContentPane().remove(component);
            }
        }

        // Reload the menu items
        loadVendorMenu();

        // Revalidate and repaint the frame to apply changes
        revalidate();
        repaint();
    }


    // Helper method to style buttons with fixed size and uniform style
    private void setButtonStyle(JButton button) {
        button.setPreferredSize(new Dimension(90, 30)); // Fixed size for buttons
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(255, 165, 0)); // Orange background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 140, 0)); // Darker orange on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 165, 0)); // Reset to original color
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 165, 0)); // Orange
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Set fixed size to avoid shrinking or expanding
        Dimension buttonSize = button.getPreferredSize();
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setPreferredSize(buttonSize);

        Color hoverBackground = new Color(255, 140, 0); // Slightly darker orange

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBackground);
                // Add border without affecting button size
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 165, 0));
                button.setBorder(null); // Remove border on exit
            }
        });
    }

private void openAddItemDialog() {
    // Create a dialog for adding items
    JDialog addItemDialog = new JDialog(this, "Add New Item", true);
    addItemDialog.setSize(800, 600);
    addItemDialog.setLayout(new BorderLayout(10, 10)); // Top-level layout
    addItemDialog.setLocationRelativeTo(this);
    addItemDialog.getContentPane().setBackground(Color.WHITE); // White background

    // Title Panel
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(new Color(255, 165, 0)); // Orange
    titlePanel.setPreferredSize(new Dimension(addItemDialog.getWidth(), 50));
    JLabel titleLabel = new JLabel("Add New Item");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setForeground(Color.WHITE);
    titlePanel.add(titleLabel);

    // Form Panel (Center)
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new GridBagLayout()); // GridBag for flexible layout
    formPanel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10); // Padding between elements
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Upload Picture Button
    JLabel picLabel = new JLabel("No File Selected", JLabel.CENTER);
    picLabel.setFont(new Font("Arial", Font.ITALIC, 14));
    picLabel.setForeground(Color.GRAY);

    JButton uploadButton = new JButton("Upload Picture");
    styleButton(uploadButton);

    // Panel for Image Preview
    JPanel imagePreviewPanel = new JPanel();
    imagePreviewPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center the image
    JLabel imagePreviewLabel = new JLabel();
    imagePreviewLabel.setPreferredSize(new Dimension(150, 150)); // Fixed size for the preview
    imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
    imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add border to define boundary
    imagePreviewPanel.add(imagePreviewLabel);

    uploadButton.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath(); // Get absolute path

            picLabel.setText(filePath); // Update the label text directly with the file path
            picLabel.setForeground(Color.BLACK);

            // Update image preview with scaling
            try {
                ImageIcon icon = new ImageIcon(filePath);
                Image image = icon.getImage();
                // Scale the image to fit within the fixed size (150x150)
                Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unable to load image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    // Food Name
    JLabel nameLabel = new JLabel("Food Name:");
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    JTextField nameField = new JTextField(20);

    // Description
    JLabel descLabel = new JLabel("Description:");
    descLabel.setFont(new Font("Arial", Font.BOLD, 14));
    JTextField descField = new JTextField(20);

    // Price
    JLabel priceLabel = new JLabel("Price:");
    priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
    JTextField priceField = new JTextField(20);

    // Save and Cancel Buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    styleButton(saveButton);
    styleButton(cancelButton);

    saveButton.addActionListener(e -> saveItem(vendorName, picLabel.getText(), nameField.getText(), descField.getText(), priceField.getText(), addItemDialog));
    cancelButton.addActionListener(e -> addItemDialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    // Add components to the Form Panel
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(uploadButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    formPanel.add(picLabel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    formPanel.add(imagePreviewPanel, gbc); // Centered image preview panel

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    formPanel.add(nameLabel, gbc);

    gbc.gridx = 1;
    formPanel.add(nameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    formPanel.add(descLabel, gbc);

    gbc.gridx = 1;
    formPanel.add(descField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    formPanel.add(priceLabel, gbc);

    gbc.gridx = 1;
    formPanel.add(priceField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;
    formPanel.add(buttonPanel, gbc);

    // Add components to Dialog
    addItemDialog.add(titlePanel, BorderLayout.NORTH);
    addItemDialog.add(formPanel, BorderLayout.CENTER);

    addItemDialog.setVisible(true);
}


    private void saveItem(String vendorName, String filePath, String name, String description, String price, JDialog dialog) {
        // Validate inputs
        if (filePath.equals("No File Selected") || name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double priceValue = Double.parseDouble(price);

            // Create the item details string with Vendor Name
            String itemDetails = "Vendor Name: " + vendorName + "\n" +
                                 "Item Code: " + generateItemCode() + "\n" +
                                 "Picture File Path: " + filePath + "\n" +
                                 "Food Name: " + name + "\n" +
                                 "Description: " + description + "\n" +
                                 "Price: RM" + priceValue + "\n" +
                                 "-----------------------";

            // Save data to Menu.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Menu.txt", true))) {
                writer.write(itemDetails);
                writer.newLine();
            }

            JOptionPane.showMessageDialog(dialog, "Item saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();

            // Refresh the vendor menu to display the new item
            refreshVendorMenu();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(dialog, "Price must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(dialog, "Error saving item. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
private void deleteItem(String vendorName, String itemCode) {
    try {
        StringBuilder updatedContent = new StringBuilder();
        boolean isCurrentVendor = false;
        boolean itemDeleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Menu.txt"))) {
            String line;
            StringBuilder vendorBlock = new StringBuilder(); // Holds the current vendor's block
            boolean hasRemainingItems = false; // Tracks if a vendor has other items

            while ((line = reader.readLine()) != null) {
                // Check for vendor name
                if (line.startsWith("Vendor Name: ")) {
                    // If we finish processing a vendor block, decide whether to keep it
                    if (isCurrentVendor) {
                        if (hasRemainingItems) {
                            updatedContent.append(vendorBlock);
                        }
                        vendorBlock.setLength(0); // Clear the vendor block
                    }

                    // Start a new vendor block
                    isCurrentVendor = line.substring("Vendor Name: ".length()).equals(vendorName);
                    hasRemainingItems = false;
                }

                if (isCurrentVendor) {
                    // Add lines to the vendor block
                    vendorBlock.append(line).append(System.lineSeparator());

                    // Check for item code
                    if (line.startsWith("Item Code: ")) {
                        String currentItemCode = line.substring("Item Code: ".length());
                        if (currentItemCode.equals(itemCode)) {
                            // Skip the item (effectively deleting it)
                            itemDeleted = true;

                            // Skip the next 5 lines (Picture, Food Name, Description, Price, Separator)
                            for (int i = 0; i < 5; i++) {
                                reader.readLine();
                            }
                            continue;
                        } else {
                            // Found another item for this vendor
                            hasRemainingItems = true;
                        }
                    }
                } else {
                    // Add non-current vendor lines directly to the updated content
                    updatedContent.append(line).append(System.lineSeparator());
                }
            }

            // Final check for the last vendor block
            if (isCurrentVendor && hasRemainingItems) {
                updatedContent.append(vendorBlock);
            }
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Menu.txt"))) {
            writer.write(updatedContent.toString());
        }

        if (itemDeleted) {
            JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshVendorMenu(); // Reload the menu to reflect changes
        } else {
            JOptionPane.showMessageDialog(this, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error deleting the item. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}






    // Helper method to generate a unique item code (could be based on a timestamp or counter)
    private String generateItemCode() {
        return "item" + System.currentTimeMillis();  // Simple example, generate unique ID based on current time
    }


        private void goBack() {
            dispose(); // Close this window
            new VendorMain(vendorName); // Open the VendorMain page
        }

        private void addHoverEffect(JButton button) {
            Color originalBackground = button.getBackground(); // Save original background color
            Color hoverBackground = new Color(255, 140, 0); // Slightly darker orange for hover

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverBackground); // Change background on hover
                    button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Add white border
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalBackground); // Reset to original background
                    button.setBorder(null); // Remove border
                }
            });
        }

        public static void main(String[] args) {
            new VendorManageItems("John's Bakery");
        }
    }
