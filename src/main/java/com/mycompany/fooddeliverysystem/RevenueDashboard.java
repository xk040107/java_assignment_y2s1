package com.mycompany.fooddeliverysystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;


public class RevenueDashboard extends javax.swing.JPanel {

    private String RunnerName;
    private static final double REVENUE_PER_ORDER = 5.0;
    private static final String ORDER_FILE = "src/main/resources/txtfile/Order.txt";
    
    public RevenueDashboard(String RunnerName) {
        initComponents();
        this.RunnerName = RunnerName;
        loadOrderAndRevenue();
        displayWeeklyChart();
        Loadbtn.addActionListener(evt -> loadChartBasedOnPeriod());
    }
    
    private void loadChartBasedOnPeriod() {
    String selectedPeriod = (String) periodcombobox.getSelectedItem();
    
    // Handle the chart display logic for different periods
    switch (selectedPeriod) {
        case "Daily":
            displayWeeklyChart();
            break;
        case "Monthly":
            displayMonthlyChart();
            break;
        case "Yearly":
            displayYearlyChart();
            break;
        default:
            break;
    }
}
    
private void displayWeeklyChart() {
    CategoryDataset dataset = createWeeklyRevenueDataset();  // Get the dataset

    // Create the chart
    JFreeChart chart = ChartFactory.createBarChart(
        "Weekly Revenue",     // Chart Title
        "Date",               // X-Axis Label
        "Revenue (RM)",       // Y-Axis Label
        dataset,              // Dataset
        PlotOrientation.VERTICAL, // Bar chart orientation
        false,                // Include Legend
        true,                 // Tooltips
        false                 // URLs
    );

    // Customize the chart (optional)
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setRangeGridlinePaint(Color.BLACK);

    // Create a ChartPanel and add it to the UI
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(600, 450));

    // Clear previous chart and update UI
    chartpanel.removeAll();
    chartpanel.setLayout(new BorderLayout());
    chartpanel.add(chartPanel, BorderLayout.CENTER);
    chartpanel.revalidate();
    chartpanel.repaint();
}


private void displayMonthlyChart() {
    // Create dataset
    CategoryDataset dataset = createMonthlyRevenueDataset();

    // Create bar chart
    JFreeChart chart = ChartFactory.createBarChart(
        "Monthly Revenue",  // Chart title
        "Month",            // X-Axis Label
        "Revenue (RM)",     // Y-Axis Label
        dataset,            // Dataset
        PlotOrientation.VERTICAL,  // Bar chart orientation
        false,              // Hide legend
        true,               // Show tooltips
        false               // No URLs
    );

    // Get plot and configure X-Axis
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    CategoryAxis xAxis = plot.getDomainAxis();
    
    // Format X-Axis labels as "Jan 25", "Feb 25", etc.
    xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Rotate labels for better visibility

    // Customize Y-Axis (Revenue)
    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // Create and update ChartPanel
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(800, 600));

    chartpanel.removeAll();
    chartpanel.setLayout(new BorderLayout());
    chartpanel.add(chartPanel, BorderLayout.CENTER);
    chartpanel.revalidate();
    chartpanel.repaint();
}



private void displayYearlyChart() {
    CategoryDataset dataset = createYearlyRevenueDataset();

    JFreeChart chart = ChartFactory.createBarChart(
        "Yearly Revenue",  // Chart title
        "Year",            // X-Axis Label
        "Revenue (RM)",    // Y-Axis Label
        dataset,           // Dataset
        PlotOrientation.VERTICAL,
        false,             // No legend needed
        true,              // Show tooltips
        false              // No URLs
    );

    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);  // Set bar color

    // **Dynamically Set Y-Axis Range**
    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // Get max revenue to scale Y-Axis properly
    double maxRevenue = 0;
    for (int i = 0; i < dataset.getColumnCount(); i++) {
        Number value = dataset.getValue(0, i);
        if (value != null) {
            maxRevenue = Math.max(maxRevenue, value.doubleValue());
        }
    }

    // Set Y-Axis range with a buffer
    if (maxRevenue > 0) {
        yAxis.setRange(0, maxRevenue + 100); // Add buffer for better visualization
    }

    // Create & Update Chart Panel
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

    chartpanel.removeAll();
    chartpanel.setLayout(new BorderLayout());
    chartpanel.add(chartPanel, BorderLayout.CENTER);
    chartpanel.revalidate();
    chartpanel.repaint();
}

private CategoryDataset createWeeklyRevenueDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    // LinkedHashMap to store daily revenue in correct order
    Map<String, Double> dailyRevenue = new LinkedHashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
        String line;
        boolean isCorrectRunner = false;

        // Get the date range for the last 7 days (including today)
        Date endDate = cal.getTime(); // Today
        cal.add(Calendar.DAY_OF_YEAR, -6); // 6 days ago
        Date startDate = cal.getTime();

        // Initialize daily revenue map with 0 for each day in the range
        Calendar calForInit = (Calendar) cal.clone(); // Create a new Calendar instance for initialization
        while (!calForInit.getTime().before(startDate)) {
            String dateKey = dateFormat.format(calForInit.getTime());
            dailyRevenue.put(dateKey, 0.0); // Initialize with 0 revenue
            calForInit.add(Calendar.DAY_OF_YEAR, -1);
        }

        // Reset calendar to the correct start date
        cal.setTime(endDate);

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Allocated Runner: ") && line.substring(18).equals(RunnerName)) {
                isCorrectRunner = true;
            } else if (isCorrectRunner && line.startsWith("Date Completed: ")) {
                String dateStr = line.substring(16, 26);  // Extract date portion (yyyy-MM-dd)
                Date dateCompleted = dateFormat.parse(dateStr);

                // Check if the order's completed date is within the last 7 days
                if (!dateCompleted.before(startDate) && !dateCompleted.after(endDate)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateCompleted);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;  // Month is 1-based
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    // Format the date as "yyyy-MM-dd" for the category axis
                    String dateKey = String.format("%d-%02d-%02d", year, month, day);

                    // Add RM 5 revenue per order
                    dailyRevenue.put(dateKey, dailyRevenue.getOrDefault(dateKey, 0.0) + REVENUE_PER_ORDER);
                }

                isCorrectRunner = false; // Reset for next order
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading order data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Now, use the already initialized `cal` and `endDate` for adding data to the dataset
    for (int i = 0; i < 7; i++) {
        String dateKey = dateFormat.format(cal.getTime());
        dataset.addValue(dailyRevenue.get(dateKey), "Revenue", dateKey);
        cal.add(Calendar.DAY_OF_YEAR, -1);
    }

    return dataset;
}


private CategoryDataset createMonthlyRevenueDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // LinkedHashMap to store monthly revenue in correct order
    Map<String, Double> monthlyRevenue = new LinkedHashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
        String line;
        boolean isCorrectRunner = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Allocated Runner: ") && line.substring(18).equals(RunnerName)) {
                isCorrectRunner = true;
            } else if (isCorrectRunner && line.startsWith("Date Completed: ")) {
                String dateStr = line.substring(16, 26);  
                Date dateCompleted = dateFormat.parse(dateStr);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateCompleted);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;  // Month is 1-based

                // Format as "MMM yy" (e.g., "Jan 25")
                String period = new SimpleDateFormat("MMM yy").format(dateCompleted);

                // Add RM 5 revenue per order
                monthlyRevenue.put(period, monthlyRevenue.getOrDefault(period, 0.0) + REVENUE_PER_ORDER);
                
                isCorrectRunner = false; // Reset for next order
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading order data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Sort keys (months) in chronological order
    List<String> sortedKeys = new ArrayList<>(monthlyRevenue.keySet());
    Collections.sort(sortedKeys, (a, b) -> {
        try {
            return new SimpleDateFormat("MMM yy").parse(a).compareTo(new SimpleDateFormat("MMM yy").parse(b));
        } catch (ParseException e) {
            return 0;
        }
    });

    // Add sorted data to dataset
    for (String month : sortedKeys) {
        dataset.addValue(monthlyRevenue.get(month), "Revenue", month);
    }

    return dataset;
}


// Create the dataset for the yearly revenue
private CategoryDataset createYearlyRevenueDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    // Initialize variables to store the total number of orders for each year
    int totalOrders2024 = 0;
    int totalOrders2025 = 0;
    int totalOrders2026 = 0;

    // Read the order data
    try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
        String line;
        boolean isCorrectRunner = false;
        Date dateCompleted = null;

        // Read each line of the file
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Allocated Runner: ") && line.substring(18).equals(RunnerName)) {
                isCorrectRunner = true;
            } else if (isCorrectRunner && line.startsWith("Date Completed: ")) {
                String dateStr = line.substring(16, 26);  // Extract date portion
                dateCompleted = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

                // Get the year from the order's completion date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateCompleted);
                int yearCompleted = calendar.get(Calendar.YEAR);

                // Increment the total orders count based on the year
                if (yearCompleted == 2024) {
                    totalOrders2024++;
                } else if (yearCompleted == 2025) {
                    totalOrders2025++;
                } else if (yearCompleted == 2026) {
                    totalOrders2026++;
                }

                isCorrectRunner = false;  // Reset for the next order
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading order data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Add total revenue for each year (total orders * revenue per order)
    dataset.addValue(totalOrders2024 * REVENUE_PER_ORDER, "Revenue", "2024");
    dataset.addValue(totalOrders2025 * REVENUE_PER_ORDER, "Revenue", "2025");
    dataset.addValue(totalOrders2026 * REVENUE_PER_ORDER, "Revenue", "2026");

    return dataset;
}

    public void reloadRevenue() {
        loadOrderAndRevenue();
        createWeeklyRevenueDataset();
        displayWeeklyChart();
        createYearlyRevenueDataset();
        createMonthlyRevenueDataset();
        totalorderpanel.revalidate(); // Refresh
        totalorderpanel.repaint();
        totalrevenuepanel.revalidate(); // Refresh
        totalrevenuepanel.repaint();
    }
    
     private void loadOrderAndRevenue() {
        // Fetch the total number of orders for the specific runner
        int totalOrders = getTotalOrders(RunnerName);

        // Calculate the total revenue (each order is RM5)
        double totalRevenue = totalOrders * 5.0;

        // Update the labels with total orders and total revenue
        lbltotalorder.setText(String.valueOf(totalOrders));
        lbltotalrevenue.setText(String.format("RM %.2f", totalRevenue));

    }
     
    private int getTotalOrders(String runnerName) {
        int totalOrders = 0;
        File filename = new File("src/main/resources/txtfile/Order.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String allocatedRunner = null;
            String runnerStatus = null;

            while ((line = reader.readLine()) != null) {
                // Parse each block of order information
                if (line.trim().startsWith("Allocated Runner:")) {
                    allocatedRunner = line.split(":")[1].trim();
                }

                if (line.trim().startsWith("Runner Status:")) {
                    runnerStatus = line.split(":")[1].trim();
                }

                // Check if we have a complete block and both conditions are met
                if (allocatedRunner != null && runnerStatus != null) {
                    if (allocatedRunner.equalsIgnoreCase(runnerName) && runnerStatus.equalsIgnoreCase("Completed")) {
                        totalOrders++;
                    }

                    // Reset for next order block
                    allocatedRunner = null;
                    runnerStatus = null;
                }

                // Skip lines between orders (if any)
                if (line.trim().equals("----------")) {
                    allocatedRunner = null;
                    runnerStatus = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalOrders;
    }

    
    
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chartpanel = new javax.swing.JPanel();
        totalorderpanel = new javax.swing.JPanel();
        lbltotalorder = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        totalrevenuepanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbltotalrevenue = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        periodcombobox = new javax.swing.JComboBox<>();
        Loadbtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(192, 222, 242));
        setMaximumSize(new java.awt.Dimension(1010, 490));
        setMinimumSize(new java.awt.Dimension(1010, 490));
        setPreferredSize(new java.awt.Dimension(1010, 490));

        chartpanel.setLayout(new java.awt.BorderLayout());

        lbltotalorder.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbltotalorder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltotalorder.setText("0");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("Total Orders");

        javax.swing.GroupLayout totalorderpanelLayout = new javax.swing.GroupLayout(totalorderpanel);
        totalorderpanel.setLayout(totalorderpanelLayout);
        totalorderpanelLayout.setHorizontalGroup(
            totalorderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalorderpanelLayout.createSequentialGroup()
                .addGroup(totalorderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalorderpanelLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(totalorderpanelLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(lbltotalorder, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        totalorderpanelLayout.setVerticalGroup(
            totalorderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalorderpanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbltotalorder, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("Total Revenue");

        lbltotalrevenue.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbltotalrevenue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltotalrevenue.setText("RM 0");

        javax.swing.GroupLayout totalrevenuepanelLayout = new javax.swing.GroupLayout(totalrevenuepanel);
        totalrevenuepanel.setLayout(totalrevenuepanelLayout);
        totalrevenuepanelLayout.setHorizontalGroup(
            totalrevenuepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalrevenuepanelLayout.createSequentialGroup()
                .addGroup(totalrevenuepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalrevenuepanelLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel3))
                    .addGroup(totalrevenuepanelLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(lbltotalrevenue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        totalrevenuepanelLayout.setVerticalGroup(
            totalrevenuepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalrevenuepanelLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(lbltotalrevenue, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Sort by:");

        periodcombobox.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        periodcombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Daily", "Monthly", "Yearly" }));

        Loadbtn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Loadbtn.setText("Load");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(periodcombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(Loadbtn)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(periodcombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(Loadbtn)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalorderpanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalrevenuepanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(totalorderpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(totalrevenuepanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Loadbtn;
    private javax.swing.JPanel chartpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbltotalorder;
    private javax.swing.JLabel lbltotalrevenue;
    private javax.swing.JComboBox<String> periodcombobox;
    private javax.swing.JPanel totalorderpanel;
    private javax.swing.JPanel totalrevenuepanel;
    // End of variables declaration//GEN-END:variables
}
