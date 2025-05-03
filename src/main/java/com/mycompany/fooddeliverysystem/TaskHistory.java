package com.mycompany.fooddeliverysystem;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TaskHistory extends javax.swing.JPanel {
    
    private String RunnerName;
    
     public TaskHistory(String RunnerName) {
        initComponents();
        this.RunnerName = RunnerName;

        historyTable.setBackground(new java.awt.Color(174,211,236));
        historyTable.getTableHeader().setBackground(new java.awt.Color(174,211,236)); 
        historyTable.getTableHeader().setForeground(Color.BLACK);
        historyTable.setRowHeight(40);
        historyTable.setIntercellSpacing(new Dimension(10, 10)); 
        
        javax.swing.table.TableColumnModel columnModel = historyTable.getColumnModel();
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            renderer.setBackground(new java.awt.Color(124,183,222));
            renderer.setFont(new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 18));
            renderer.setForeground(java.awt.Color.BLACK);
            renderer.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
            columnModel.getColumn(i).setHeaderRenderer(renderer);
        }
        loadAndDisplayTaskHistory();
    }
    
    private void loadAndDisplayTaskHistory() {
        File orderFile = new File("src/main/resources/txtfile/Order.txt");
        File reviewFile = new File("src/main/resources/txtfile/Review.txt");
        List<Map<String, String>> orders = new ArrayList<>();
        Map<String, String> reviews = loadReviews(reviewFile);

        try (BufferedReader br = new BufferedReader(new FileReader(orderFile))) {
            String line;
            Map<String, String> order = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ")) {
                    if (order != null) {
                        if ("Completed".equals(order.get("RunnerStatus")) && RunnerName.equals(order.get("AllocatedRunner"))) {
                            orders.add(order);
                        }
                    }
                    order = new HashMap<>();
                    order.put("OrderID", line.substring(10).trim());
                }
                if (line.startsWith("Customer Name: ")) order.put("CustomerName", line.substring(15).trim());
                if (line.startsWith("Vendor Name: ")) order.put("VendorName", line.substring(13).trim());
                if (line.startsWith("Date Completed: ")) order.put("DateCompleted", line.substring(16).trim());
                if (line.startsWith("Runner Status: ")) order.put("RunnerStatus", line.substring(15).trim());
                if (line.startsWith("Allocated Runner: ")) order.put("AllocatedRunner", line.substring(18).trim());

                if (line.equals("----------")) {
                    if (order != null) {
                        if ("Completed".equals(order.get("RunnerStatus")) && RunnerName.equals(order.get("AllocatedRunner"))) {
                            orders.add(order);
                        }
                        order = null;
                    }
                }
            }

            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0);

            for (Map<String, String> currentOrder : orders) {
                String orderID = currentOrder.get("OrderID");
                String review = reviews.getOrDefault(orderID, "No Review");
                model.addRow(new Object[] {
                    orderID,
                    currentOrder.get("CustomerName"),
                    currentOrder.get("VendorName"),
                    "RM 5", 
                    currentOrder.get("DateCompleted"),
                    review, 
                    currentOrder.get("RunnerStatus")
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading order data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, String> loadReviews(File reviewFile) {
        Map<String, String> reviews = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(reviewFile))) {
            String line;
            String orderID = null;
            String remarks = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Order ID: ")) {
                    orderID = line.substring(10).trim();
                }
                if (line.startsWith("Remarks: ")) {
                    remarks = line.substring(9).trim();
                }
                if (line.equals("----------")) {
                    if (orderID != null && remarks != null) {
                        reviews.put(orderID, remarks);
                    }
                    orderID = null;
                    remarks = null;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading review data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return reviews;
    }

    public void refreshTable() {
        loadAndDisplayTaskHistory();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(192, 222, 242));
        setMaximumSize(new java.awt.Dimension(1000, 400));
        setMinimumSize(new java.awt.Dimension(1000, 400));
        setPreferredSize(new java.awt.Dimension(1000, 400));

        jScrollPane1.setBackground(new java.awt.Color(174, 211, 236));
        jScrollPane1.setViewportBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jScrollPane1.setViewportView(historyTable);

        historyTable.setAutoCreateRowSorter(true);
        historyTable.setBackground(new java.awt.Color(174, 211, 236));
        historyTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        historyTable.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        historyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Customer Name", "Vendor Name", "Delivery Price", "DateTime", "Customer Review", "Status"
            }
        ));
        historyTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(historyTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable historyTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
