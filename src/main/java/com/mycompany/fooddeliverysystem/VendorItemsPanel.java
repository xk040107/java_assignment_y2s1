/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

class FoodItem {
    String vendorName, itemCode, imagePath, foodName, description;
    double price;

    public FoodItem(String vendorName, String itemCode, String imagePath, String foodName, String description, double price) {
        this.vendorName = vendorName;
        this.itemCode = itemCode;
        this.imagePath = imagePath;
        this.foodName = foodName;
        this.description = description;
        this.price = price;
    }
    
}   
public class VendorItemsPanel extends javax.swing.JPanel {
    private List<FoodItem> items = new ArrayList<>();
    private JPanel itemsPanel;
    private JComboBox<String> vendorFilter;
    private JScrollPane scrollPane;
    
    public VendorItemsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 30));
        loadMenuItems();

        vendorFilter = new JComboBox<>(getUniqueVendors());
        vendorFilter.insertItemAt("Show All", 0);
        vendorFilter.setSelectedIndex(0);
        vendorFilter.addActionListener(e -> filterItemsByVendor((String) vendorFilter.getSelectedItem()));
        vendorFilter.setPreferredSize(new Dimension(150, 30));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(vendorFilter);
        add(filterPanel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);

        displayItems(items);
    }
    
    private void loadMenuItems() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Menu.txt"))) {
            String line, vendor = "", itemCode = "", imagePath = "", foodName = "", description = "";
            double price = 0.0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Vendor Name:")) vendor = line.split(": ")[1];
                else if (line.startsWith("Item Code:")) itemCode = line.split(": ")[1];
                else if (line.startsWith("Picture File Path:")) imagePath = line.split(": ")[1];
                else if (line.startsWith("Food Name:")) foodName = line.split(": ")[1];
                else if (line.startsWith("Description:")) description = line.split(": ")[1];
                else if (line.startsWith("Price:")) price = Double.parseDouble(line.split("RM")[1]);
                else if (line.equals("-----------------------")) {
                    items.add(new FoodItem(vendor, itemCode, imagePath, foodName, description, price));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getUniqueVendors() {
        List<String> vendors = items.stream().map(i -> i.vendorName).distinct().collect(Collectors.toList());
        return vendors.toArray(new String[0]);
    }

    private void filterItemsByVendor(String vendor) {
        List<FoodItem> filteredItems = vendor.equals("Show All") ? items : items.stream()
                .filter(i -> i.vendorName.equals(vendor))
                .collect(Collectors.toList());
        displayItems(filteredItems);
    }

    private void displayItems(List<FoodItem> itemList) {
        itemsPanel.removeAll();
        for (FoodItem item : itemList) {
            itemsPanel.add(createItemCard(item));
            itemsPanel.add(Box.createVerticalStrut(15)); // Add spacing between cards
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0)); // Reset scroll position
    }

    private JPanel createItemCard(FoodItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(700, 240));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        JLabel itemCodeLabel = new JLabel(item.itemCode, SwingConstants.CENTER);
        itemCodeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        itemCodeLabel.setForeground(Color.GRAY);
        itemCodeLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 5, 10));
        
        JLabel foodImageLabel = new JLabel();
        foodImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        foodImageLabel.setPreferredSize(new Dimension(200, 200));
        foodImageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        try {
            ImageIcon icon = new ImageIcon(item.imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            foodImageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            foodImageLabel.setText("No Image");
            foodImageLabel.setForeground(Color.GRAY);
        }

        imagePanel.add(itemCodeLabel, BorderLayout.NORTH);
        imagePanel.add(foodImageLabel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel nameLabel = new JLabel(item.foodName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(30, 15, 10, 15));
        
        JLabel descriptionLabel = new JLabel("<html><p style='width:620px;'>" + item.description + "</p></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel priceLabel = new JLabel("Price: RM " + item.price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        infoPanel.add(nameLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(priceLabel);

        // Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteItem(item));

        // Add to Card
        card.add(imagePanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(deleteButton, BorderLayout.EAST);

        return card;
    }

    private void deleteItem(FoodItem item) {
        int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete " + item.foodName + "?",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        items.remove(item);
        updateMenuFile();

        JOptionPane.showMessageDialog(
            this,
            "Item '" + item.foodName + "' has been deleted successfully!",
            "Deleted",
            JOptionPane.INFORMATION_MESSAGE
        );

        filterItemsByVendor((String) vendorFilter.getSelectedItem());
    }
    }

    private void updateMenuFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/txtfile/Menu.txt"))) {
            for (FoodItem item : items) {
                writer.write("Vendor Name: " + item.vendorName + "\n");
                writer.write("Item Code: " + item.itemCode + "\n");
                writer.write("Picture File Path: " + item.imagePath + "\n");
                writer.write("Food Name: " + item.foodName + "\n");
                writer.write("Description: " + item.description + "\n");
                writer.write("Price: RM" + item.price + "\n");
                writer.write("-----------------------\n");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
