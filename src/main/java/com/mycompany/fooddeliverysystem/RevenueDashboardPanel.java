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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeriesCollection;

public class RevenueDashboardPanel extends javax.swing.JPanel {
    
    private static final String ORDER_FILE = "src/main/resources/txtfile/Order.txt";
    private JComboBox<String> vendorFilter;
    private MonitorRevenueDashboardPanel monitorPanel;
    private VendorRevenueDashboard revenueDashboard;
    private JPanel summaryPanel, chartsPanel;
    
    
    // Initialize with "Show All" (or any default vendor)
    public RevenueDashboardPanel() {
        // Main panel layout
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        vendorFilter = new JComboBox<>(getUniqueVendors());
        vendorFilter.setSelectedIndex(0);
        vendorFilter.addActionListener(e -> updateDashboard((String) vendorFilter.getSelectedItem()));
        vendorFilter.setPreferredSize(new Dimension(150, 30));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(vendorFilter);
        filterPanel.setBackground(Color.WHITE);
        add(filterPanel, BorderLayout.NORTH);

        String selectedVendor = vendorFilter.getSelectedItem().toString(); // Move after vendorFilter initialization
        revenueDashboard = new VendorRevenueDashboard(selectedVendor); // Fix instance creation
        
        summaryPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        summaryPanel.setBackground(Color.WHITE);
        add(summaryPanel, BorderLayout.CENTER);

        chartsPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        chartsPanel.setBackground(Color.WHITE);
        add(chartsPanel, BorderLayout.SOUTH);
        
        // Main panel with scrolling
        monitorPanel = new MonitorRevenueDashboardPanel();
        JScrollPane scrollPane = new JScrollPane(monitorPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Smooth scrolling
        add(scrollPane, BorderLayout.CENTER);

        updateDashboard(selectedVendor); // Call updateDashboard with selected vendor
    }

    // Method to update the dashboard when a vendor is selected
    private void updateDashboard(String selectedVendor) {
        revenueDashboard = new VendorRevenueDashboard(selectedVendor);
        monitorPanel.updateContent(revenueDashboard);
        revalidate();
        repaint();
    }
    
    // A panel that stores summary and charts inside a scrollable panel
class MonitorRevenueDashboardPanel extends JPanel {
    private JPanel summaryPanel, chartsPanel;
    private JLabel vendorNameLabel;

    public MonitorRevenueDashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        summaryPanel = new JPanel(new GridLayout(1, 5, 20, 20));
        summaryPanel.setBackground(Color.WHITE);

        chartsPanel = new JPanel(new GridLayout(0, 1, 20, 20)); // 1 chart per row
        chartsPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds padding around everything
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        contentPanel.add(chartsPanel, BorderLayout.CENTER);
        contentPanel.setBackground(Color.WHITE);
       
        add(contentPanel, BorderLayout.CENTER);
    }

    public void updateContent(VendorRevenueDashboard revenueDashboard) {
        setBackground(Color.WHITE);
        summaryPanel.removeAll();
        chartsPanel.removeAll();

        Map<String, Object> summaryData = revenueDashboard.getSummaryData();

        summaryPanel.add(createSummaryCard("Total Revenue", "RM " + String.format("%.2f", summaryData.get("totalRevenue")), 
                                            new Color(56, 116, 203), new Color(30, 80, 160))); // Blue gradient
        summaryPanel.add(createSummaryCard("Total Orders", String.valueOf(summaryData.get("totalOrders")), 
                                            new Color(246, 0, 2), new Color(180, 0, 0))); // Red gradient
        summaryPanel.add(createSummaryCard("Average Order Value", "RM " + String.format("%.2f", summaryData.get("averageOrderValue")), 
                                            new Color(45, 196, 193), new Color(20, 160, 160))); // Cyan gradient
        summaryPanel.add(createSummaryCard("Most Popular Item", (String) summaryData.get("mostPopularItem"), 
                                            new Color(255, 215, 0), new Color(200, 160, 0))); // Gold gradient
        summaryPanel.add(createSummaryCard("Most Used Order Method", (String) summaryData.get("mostUsedOrderMethod"), 
                                            new Color(156, 99, 214), new Color(120, 70, 180))); // Purple gradient

        chartsPanel.add(createChartCard("Revenue Over Time", revenueDashboard.getRevenueChart()));
        chartsPanel.add(createChartCard("Most Ordered Food", revenueDashboard.getMostOrderedFoodChart()));
        chartsPanel.add(createChartCard("Order Method Distribution", revenueDashboard.getOrderMethodChart()));

        revalidate();
        repaint();
    }

    // Helper method to create summary card with gradient background and rounded corners
    private JPanel createSummaryCard(String title, String value, Color startColor, Color endColor) {
    JPanel card = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, width, height, 20, 20); // Rounded corners with 20px radius
        }
    };

        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 120));
        card.setOpaque(false); // Allows gradient to render
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartCard(String title, Object dataset) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        //card.setPreferredSize(new Dimension(700, 600));
        card.setBorder(BorderFactory.createTitledBorder(title));

        JFreeChart chart = null;

        if (dataset instanceof TimeSeriesCollection) {
            chart = ChartFactory.createTimeSeriesChart(title, "Time", "Revenue", (TimeSeriesCollection) dataset);
        } else if (dataset instanceof DefaultCategoryDataset) {
            chart = ChartFactory.createBarChart(title, "Food Items", "Orders", (DefaultCategoryDataset) dataset);
        } else if (dataset instanceof DefaultPieDataset) {
            chart = ChartFactory.createPieChart(title, (DefaultPieDataset) dataset, true, true, false);
        }

        if (chart != null) {
            ChartPanel chartPanel = new ChartPanel(chart);
            card.add(chartPanel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Chart data unavailable", SwingConstants.CENTER);
            card.add(errorLabel, BorderLayout.CENTER);
        }

        return card;
    }
}
    
    private String[] getUniqueVendors() {
        Set<String> vendors = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Vendor Name: ")) {
                    vendors.add(line.substring(13).trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vendors.toArray(new String[0]);
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
