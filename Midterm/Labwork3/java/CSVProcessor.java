import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

public class CSVProcessor extends JFrame {

    // These are the things the user can see and interact with on the screen
    private JTextField pathField;        // Text box that shows the selected file path
    private JComboBox<String> columnCombo; // Dropdown menu to choose which column to analyze
    private JTextField maxRowsField;     // Text box where user can type how many rows to show
    private JTextArea outputArea;        // Big box where all the results are displayed

    // This is the constructor - it runs when the program starts
    public CSVProcessor() {
        super("CSV Dataset Processor");   // Set the title of the window
        setSize(780, 580);                // Make the window a nice size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the program when window is closed
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout to arrange components nicely

        // ==================== FILE SELECTION PANEL ====================
        // This panel is at the top and lets the user choose their CSV file
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filePanel.add(new JLabel("CSV File:"));   // Label so user knows what this is for

        pathField = new JTextField(45);   // Text field to show the file path
        pathField.setEditable(false);     // User cannot type here, only we set the text
        filePanel.add(pathField);

        JButton browseButton = new JButton("Browse CSV");  // Button to open file chooser
        browseButton.addActionListener(this::openNativeFileChooser); // When clicked, run the method below
        filePanel.add(browseButton);

        // ==================== CONTROL PANEL ====================
        // This panel has the column selector and number of rows option
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        controlPanel.add(new JLabel("Select Column:"));   // Label for column dropdown
        columnCombo = new JComboBox<>();                  // Dropdown menu for columns
        controlPanel.add(columnCombo);

        controlPanel.add(new JLabel("Show first rows:")); // Label for rows input
        maxRowsField = new JTextField("25", 5);           // Default value is 25
        controlPanel.add(maxRowsField);

        // ==================== NORTH PANEL (Top Section) ====================
        // Combines the file panel and control panel together at the top
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(filePanel);
        northPanel.add(controlPanel);

        // ==================== OUTPUT AREA ====================
        // This is the big area in the middle where results will appear
        outputArea = new JTextArea();
        outputArea.setEditable(false);                    // User cannot edit the results
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13)); // Nice monospace font
        JScrollPane scrollPane = new JScrollPane(outputArea);     // Makes it scrollable

        // Big button at the bottom to start processing
        JButton processButton = new JButton("Process CSV");
        processButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        processButton.addActionListener(e -> processCSV()); // When clicked, run processCSV()

        // ==================== ADD EVERYTHING TO THE WINDOW ====================
        add(northPanel, BorderLayout.NORTH);    // Top part
        add(scrollPane, BorderLayout.CENTER);   // Middle - results
        add(processButton, BorderLayout.SOUTH); // Bottom - process button

        setLocationRelativeTo(null);  // Center the window on screen
        setVisible(true);             // Show the window
    }

    // ====================== OPEN FILE CHOOSER ======================
    // This method runs when the user clicks the "Browse CSV" button
    private void openNativeFileChooser(ActionEvent e) {
        // Open the normal file dialog (Windows/Mac style)
        FileDialog dialog = new FileDialog(this, "Select CSV File", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".csv")); // Only show .csv files
        dialog.setVisible(true);

        String fileName = dialog.getFile();
        String directory = dialog.getDirectory();

        // If user actually selected a file
        if (fileName != null && directory != null) {
            File selectedFile = new File(directory, fileName);
            String filePath = selectedFile.getAbsolutePath();
            
            pathField.setText(filePath);        // Show the full path in the text box
            loadColumnHeaders(filePath);        // Read and load column names into dropdown
        }
    }

    // ====================== LOAD COLUMN NAMES ======================
    // This reads the CSV file and puts the column names in the dropdown
    // It skips the first 6 lines because they are metadata (not real data)
    private void loadColumnHeaders(String filePath) {
        columnCombo.removeAllItems();   // Clear old items first

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Skip the first 6 lines (metadata/header info we don't need)
            for (int i = 0; i < 6; i++) {
                if (br.readLine() == null) break;
            }

            String headerLine = br.readLine();  // This line should have the column names
            if (headerLine == null || headerLine.trim().isEmpty()) {
                columnCombo.addItem("Column 0");  // Fallback if no headers found
                return;
            }

            String[] headers = splitCsvLine(headerLine);  // Split the line into columns
            boolean hasValidColumns = false;

            // Add each valid column name to the dropdown
            for (int i = 0; i < headers.length; i++) {
                String cleaned = headers[i].trim();

                // Skip empty columns and the weird "Column1" placeholder
                if (cleaned.isEmpty() || cleaned.equalsIgnoreCase("Column1")) {
                    continue;
                }

                columnCombo.addItem(cleaned);   // Add nice column name to dropdown
                hasValidColumns = true;
            }

            // If no good columns were found, add a default one
            if (!hasValidColumns) {
                columnCombo.addItem("Column 0");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error reading headers:\n" + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            columnCombo.addItem("Column 0");  // Fallback option
        }
    }

    // ====================== PROCESS THE CSV FILE ======================
    // This is the main method that runs when user clicks "Process CSV"
    private void processCSV() {
        String filePath = pathField.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a CSV file first.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (columnCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No columns loaded.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get which column the user selected from the dropdown
        int selectedComboIndex = columnCombo.getSelectedIndex();
        if (selectedComboIndex == -1) selectedComboIndex = 0;

        // Get how many rows the user wants to see (default 25)
        int maxRows = 25;
        try {
            maxRows = Integer.parseInt(maxRowsField.getText().trim());
            if (maxRows < 1) maxRows = 25;
        } catch (Exception ignored) {}

        // Variables to store our results
        int validRows = 0;
        List<String> extractedValues = new ArrayList<>();
        Set<String> uniqueValues = new HashSet<>();

        StringBuilder results = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Skip the first 6 metadata lines again
            for (int i = 0; i < 6; i++) br.readLine();

            String headerLine = br.readLine();
            String[] headers = splitCsvLine(headerLine);

            // Figure out the real column index (because we skipped empty columns)
            List<Integer> realIndices = new ArrayList<>();
            for (int i = 0; i < headers.length; i++) {
                String cleaned = headers[i].trim();
                if (!cleaned.isEmpty() && !cleaned.equalsIgnoreCase("Column1")) {
                    realIndices.add(i);
                }
            }

            int actualColumnIndex = selectedComboIndex < realIndices.size() 
                    ? realIndices.get(selectedComboIndex) : 0;

            String columnName = actualColumnIndex < headers.length 
                    ? headers[actualColumnIndex].trim() : "Column " + actualColumnIndex;

            String line;
            // Read every remaining line in the file
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] columns = splitCsvLine(line);
                if (actualColumnIndex >= columns.length) continue;

                String value = columns[actualColumnIndex].trim();
                if (value.isEmpty()) continue;

                validRows++;
                extractedValues.add(value);
                uniqueValues.add(value);   // Automatically removes duplicates
            }

            // ==================== BUILD THE RESULT TEXT ====================
            results.append("=== CSV Summary ===\n");
            results.append("Selected Column: ").append(columnName).append("\n");
            results.append("Total Valid Rows: ").append(validRows).append("\n");
            results.append("Unique Values Count: ").append(uniqueValues.size()).append("\n\n");

            results.append("First ").append(Math.min(maxRows, extractedValues.size()))
                   .append(" extracted values:\n");

            // Show only the first X values (as requested by user)
            for (int i = 0; i < extractedValues.size() && i < maxRows; i++) {
                results.append((i + 1)).append(". ").append(extractedValues.get(i)).append("\n");
            }
            if (extractedValues.size() > maxRows) {
                results.append("... (showing first ").append(maxRows).append(" values)\n");
            }

            results.append("\nUnique Values:\n");
            int idx = 1;
            for (String val : uniqueValues) {
                results.append(idx++).append(". ").append(val).append("\n");
            }

        } catch (IOException ex) {
            results.append("Error reading file: ").append(ex.getMessage());
        }

        // Finally, show everything in the big text area
        outputArea.setText(results.toString());
    }

    // ====================== HELPER METHOD: SPLIT CSV LINE ======================
    // This method correctly splits a CSV line even if there are commas inside quotes
    // Example: "Hello, World",123,"Test, with comma"  → splits properly
    private static String[] splitCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');  // Handle escaped quotes
                    i++;
                } else {
                    inQuotes = !inQuotes; // Toggle quote mode
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());  // Add the last value
        return values.toArray(new String[0]);
    }

    // Main method - starts the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CSVProcessor::new); // Run on Event Dispatch Thread (safe for GUI)
    }
}