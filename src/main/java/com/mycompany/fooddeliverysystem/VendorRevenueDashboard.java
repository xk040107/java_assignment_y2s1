package com.mycompany.fooddeliverysystem;

import com.mycompany.fooddeliverysystem.CustomerOrderStatus.Order;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;

public class VendorRevenueDashboard extends JFrame {
    private String vendorName;
    private static final String ORDER_FILE = "src/main/resources/txtfile/Order.txt";
    
    // Manager fetch data
    public double totalRevenue;
    public int totalOrders;
    public double averageOrderValue;
    public String mostPopularItem;
    public String mostUsedOrderMethod;

    public TimeSeriesCollection revenueDataset;
    public DefaultCategoryDataset mostOrderedFoodDataset;
    public DefaultPieDataset orderMethodDataset;
    
    public VendorRevenueDashboard(String vendorName) {
        this.vendorName = vendorName;
        setTitle("Vendor Dashboard - " + vendorName);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 2)); // 2x2 grid for charts

        // Adding the charts
        add(createLineChartPanel(createRevenueDataset(), "Revenue Over Time", "Date", "Revenue (RM)"));
        add(createBarChartPanel(createMostOrderedFoodDataset(), "Most Ordered Foods", "Food Item", "Quantity Ordered"));
        add(createPieChartPanel(createOrderMethodDataset(), "Order Method Distribution"));
        add(createSummaryPanel());  // New summary panel
        
        //Manager fetch data
        loadSummaryData();
        loadChartData();
    }
    
    // Manager fetch data
    // Load summary data from Order.txt
    private void loadSummaryData() {
        totalRevenue = 0.0;
        totalOrders = 0;
        averageOrderValue = 0.0;
        mostPopularItem = "N/A";
        mostUsedOrderMethod = "N/A";

        Map<String, Integer> foodOrderCount = new HashMap<>();
        Map<String, Integer> orderMethodCount = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/txtfile/Order.txt"))) {
            String line;
            boolean isCorrectVendor = false;
            boolean isOrderCompleted = false;
            boolean isDelivery = false;
            boolean isRunnerCompleted = false;
            double currentOrderPrice = 0.0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Vendor Name: ") && line.substring(13).equals(vendorName)) {
                    isCorrectVendor = true;
                    isOrderCompleted = false;
                    isDelivery = false;
                    isRunnerCompleted = false;
                    currentOrderPrice = 0.0;
                }

                if (isCorrectVendor && line.startsWith("Order Method: ")) {
                    String method = line.substring(14).trim();
                    orderMethodCount.put(method, orderMethodCount.getOrDefault(method, 0) + 1);
                    if (method.equalsIgnoreCase("Delivery")) {
                        isDelivery = true;
                    }
                }

                if (isCorrectVendor && line.startsWith("Order Status: ") && line.substring(14).equalsIgnoreCase("Completed")) {
                    isOrderCompleted = true;
                }

                if (isDelivery && isCorrectVendor && line.startsWith("Runner Status: ") && line.substring(15).equalsIgnoreCase("Completed")) {
                    isRunnerCompleted = true;
                }

                if (isCorrectVendor && line.startsWith("Total Price: RM")) {
                    currentOrderPrice = Double.parseDouble(line.substring(15).trim());
                }

                if (isCorrectVendor && line.startsWith("Item Description: ")) {
                    String[] items = line.substring(18).split(",");
                    for (String item : items) {
                        String[] parts = item.trim().split(" x ");
                        if (parts.length == 2) {
                            String foodName = parts[0].trim();
                            int quantity = Integer.parseInt(parts[1].trim());
                            foodOrderCount.put(foodName, foodOrderCount.getOrDefault(foodName, 0) + quantity);
                        }
                    }
                }

                if (line.startsWith("----------")) {
                    if (isOrderCompleted && (!isDelivery || (isDelivery && isRunnerCompleted))) {
                        totalRevenue += currentOrderPrice;
                        totalOrders++;
                    }
                    isCorrectVendor = false;
                }
            }

            if (totalOrders > 0) {
                averageOrderValue = totalRevenue / totalOrders;
            }

            mostPopularItem = foodOrderCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            mostUsedOrderMethod = orderMethodCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Manager fetch data
    private void loadChartData() {
        revenueDataset = createRevenueDataset();
        mostOrderedFoodDataset = createMostOrderedFoodDataset();
        orderMethodDataset = createOrderMethodDataset();
    }

    // Manager fetch data
    public Map<String, Object> getSummaryData() {
        Map<String, Object> summaryData = new HashMap<>();
        summaryData.put("totalRevenue", totalRevenue);
        summaryData.put("totalOrders", totalOrders);
        summaryData.put("averageOrderValue", averageOrderValue);
        summaryData.put("mostPopularItem", mostPopularItem);
        summaryData.put("mostUsedOrderMethod", mostUsedOrderMethod);
        return summaryData;
    }

    // Manager fetch data
    public TimeSeriesCollection getRevenueChart() {
        return revenueDataset;
    }

    // Manager fetch data
    public DefaultCategoryDataset getMostOrderedFoodChart() {
        return mostOrderedFoodDataset;
    }

    // Manager fetch data
    public DefaultPieDataset getOrderMethodChart() {
        return orderMethodDataset;
    }

    /*** 1. Revenue Over Time (Line Chart) ***/
    private TimeSeriesCollection createRevenueDataset() {
        TimeSeries series = new TimeSeries("Revenue");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            boolean isCorrectVendor = false;
            double totalPrice = 0.0;
            Date dateCompleted = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Vendor Name: ") && line.substring(13).equals(vendorName)) {
                    isCorrectVendor = true;
                } else if (isCorrectVendor && line.startsWith("Total Price: RM")) {
                    totalPrice = Double.parseDouble(line.substring(15).trim());
                } else if (isCorrectVendor && line.startsWith("Date Completed: ")) {
                    String dateStr = line.substring(16, 26); // Extract date portion
                    dateCompleted = dateFormat.parse(dateStr);
                    series.addOrUpdate(new Day(dateCompleted), totalPrice);
                    isCorrectVendor = false;  // Reset for the next order
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    /*** 2. Most Ordered Food (Bar Chart) ***/
    private DefaultCategoryDataset createMostOrderedFoodDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> foodOrderCount = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            boolean isCorrectVendor = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Vendor Name: ") && line.substring(13).equals(vendorName)) {
                    isCorrectVendor = true;
                } else if (isCorrectVendor && line.startsWith("Item Description: ")) {
                    String[] items = line.substring(18).split(",");
                    for (String item : items) {
                        String[] parts = item.trim().split(" x ");
                        String foodName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());

                        foodOrderCount.put(foodName, foodOrderCount.getOrDefault(foodName, 0) + quantity);
                    }
                    isCorrectVendor = false;  // Reset after processing an order
                }
            }

            // Add data to dataset
            for (Map.Entry<String, Integer> entry : foodOrderCount.entrySet()) {
                dataset.addValue(entry.getValue(), "Orders", entry.getKey());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return dataset;
    }

    /*** 3. Order Method Distribution (Pie Chart) ***/
    private DefaultPieDataset createOrderMethodDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> orderMethodCount = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            boolean isCorrectVendor = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Vendor Name: ") && line.substring(13).equals(vendorName)) {
                    isCorrectVendor = true;
                } else if (isCorrectVendor && line.startsWith("Order Method: ")) {
                    String method = line.substring(14).trim();
                    orderMethodCount.put(method, orderMethodCount.getOrDefault(method, 0) + 1);
                    isCorrectVendor = false;  // Reset after processing an order
                }
            }

            // Add data to dataset
            for (Map.Entry<String, Integer> entry : orderMethodCount.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return dataset;
    }

    

    /*** Helper Method to Create Line Chart Panels ***/
private ChartPanel createLineChartPanel(TimeSeriesCollection dataset, String title, String xAxisLabel, String yAxisLabel) {
    JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset,
            true,
            true,
            false
    );

    // Customize the plot to handle date formatting
    var plot = chart.getXYPlot();

    // Create a DateAxis to handle the x-axis
    var dateAxis = new DateAxis(xAxisLabel);
    dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy")); // Format as 'Jan-2024'

    // Rotate the tick labels if they are still overlapping
    dateAxis.setVerticalTickLabels(true); // Makes the month labels vertical

    plot.setDomainAxis(dateAxis);

    return new ChartPanel(chart);
}


    /*** Helper Method to Create Bar Chart Panels ***/
private ChartPanel createBarChartPanel(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
    JFreeChart chart = ChartFactory.createBarChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset
    );

    // Enable item labels (show quantity above bars)
    var plot = chart.getCategoryPlot();
    var renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
    renderer.setDefaultItemLabelsVisible(true); // Show labels above bars

    // Rotate category (X-axis) labels for better readability
    var domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(
            org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4) // 45-degree rotation
    );

    return new ChartPanel(chart);
}


    /*** Helper Method to Create Pie Chart Panels ***/
    private ChartPanel createPieChartPanel(DefaultPieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );
        return new ChartPanel(chart);
    }

/*** 4. Summary Panel ***/
private JPanel createSummaryPanel() {
    JPanel summaryPanel = new JPanel();
    summaryPanel.setLayout(new GridLayout(5, 1, 10, 10));  // 5 rows, 1 column with spacing
    summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

    double totalRevenue = 0.0;
    int totalOrders = 0;
    double averageOrderValue = 0.0;
    String mostPopularItem = "N/A";
    String mostUsedOrderMethod = "N/A";

    Map<String, Integer> foodOrderCount = new HashMap<>();
    Map<String, Integer> orderMethodCount = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
        String line;
        boolean isCorrectVendor = false;
        boolean isOrderCompleted = false;
        boolean isDelivery = false;
        boolean isRunnerCompleted = false;
        double currentOrderPrice = 0.0;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Check for the correct vendor
            if (line.startsWith("Vendor Name: ") && line.substring(13).equals(vendorName)) {
                isCorrectVendor = true;
                isOrderCompleted = false;
                isDelivery = false;
                isRunnerCompleted = false;
                currentOrderPrice = 0.0;
            }

            // Track order method
            if (isCorrectVendor && line.startsWith("Order Method: ")) {
                String method = line.substring(14).trim();
                orderMethodCount.put(method, orderMethodCount.getOrDefault(method, 0) + 1);
                if (method.equalsIgnoreCase("Delivery")) {
                    isDelivery = true;
                }
            }

            // Check if the order is completed
            if (isCorrectVendor && line.startsWith("Order Status: ") && line.substring(14).equalsIgnoreCase("Completed")) {
                isOrderCompleted = true;
            }

            // For delivery, check if runner has completed
            if (isDelivery && isCorrectVendor && line.startsWith("Runner Status: ") && line.substring(15).equalsIgnoreCase("Completed")) {
                isRunnerCompleted = true;
            }

            // Get the total price
            if (isCorrectVendor && line.startsWith("Total Price: RM")) {
                currentOrderPrice = Double.parseDouble(line.substring(15).trim());
            }

            // Add item descriptions to count popular items
            if (isCorrectVendor && line.startsWith("Item Description: ")) {
                String[] items = line.substring(18).split(",");
                for (String item : items) {
                    String[] parts = item.trim().split(" x ");
                    if (parts.length == 2) {
                        String foodName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        foodOrderCount.put(foodName, foodOrderCount.getOrDefault(foodName, 0) + quantity);
                    }
                }
            }

            // If the order is fully completed, add it to totals
            if (line.startsWith("----------")) {
                if (isOrderCompleted && (!isDelivery || (isDelivery && isRunnerCompleted))) {
                    totalRevenue += currentOrderPrice;
                    totalOrders++;
                }
                isCorrectVendor = false; // Reset for next order
            }
        }

        // Calculate average order value
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue / totalOrders;
        }

        // Find the most popular item
        mostPopularItem = foodOrderCount.entrySet()
                            .stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse("N/A");

        // Find the most used order method
        mostUsedOrderMethod = orderMethodCount.entrySet()
                                .stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("N/A");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading order data.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Add metrics to the summary panel with better formatting
    summaryPanel.add(createSummaryLabel("Total Revenue: RM " + String.format("%.2f", totalRevenue)));
    summaryPanel.add(createSummaryLabel("Total Orders: " + totalOrders));
    summaryPanel.add(createSummaryLabel("Average Order Value: RM " + String.format("%.2f", averageOrderValue)));
    summaryPanel.add(createSummaryLabel("Most Popular Item: " + mostPopularItem));
    summaryPanel.add(createSummaryLabel("Most Used Order Method: " + mostUsedOrderMethod));

    return summaryPanel;
}

/*** Helper Method to Format Labels ***/
private JLabel createSummaryLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", Font.BOLD, 16));
    label.setHorizontalAlignment(SwingConstants.LEFT);
    return label;
}



    /*** Main Method to Launch Dashboard ***/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VendorRevenueDashboard("Arest Cafe").setVisible(true);  // Replace with desired vendor name
        });
    }
} 