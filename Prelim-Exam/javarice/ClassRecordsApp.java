// Programmer: Art Louie Lacro 9307
package javarice;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ClassRecordsApp extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtID, txtFI, txtLN,
            txtLW1, txtLW2, txtLW3,
            txtPrelim, txtAttendance;

    public ClassRecordsApp() {
        // Try to set Nimbus Look and Feel (best built-in modern-ish option)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fall back to default if Nimbus unavailable (rare)
            System.err.println("Nimbus not available, using default L&F");
        }

        UIManager.put("Panel.background",              new Color(0xf0, 0xf9, 0xfb));  // #f0f9fb
        UIManager.put("OptionPane.background",         new Color(0xf0, 0xf9, 0xfb));

        UIManager.put("Button.background",             new Color(75, 187, 210));     // #4bbbd2
        UIManager.put("Button.foreground",             Color.WHITE);
        UIManager.put("Button.focus",                  new Color(104, 198, 217));    // #68c6d9 hover feel

        UIManager.put("TextField.background",          Color.WHITE);
        UIManager.put("TextField.foreground",          new Color(21, 74, 85));       // #154a55
        UIManager.put("TextField.inactiveForeground",  new Color(80, 80, 80));

        UIManager.put("Table.background",              Color.WHITE);
        UIManager.put("Table.alternateRowColor",       new Color(245, 252, 254));   // very light teal tint
        UIManager.put("Table.selectionBackground",     new Color(104, 198, 217));    // #68c6d9
        UIManager.put("Table.selectionForeground",     Color.WHITE);
        UIManager.put("TableHeader.background",        new Color(165, 227, 214));   // #a5e3d6

        UIManager.put("TitledBorder.titleColor",       new Color(33, 109, 93));     // #216d5d
        UIManager.put("Label.foreground",              new Color(21, 74, 85));       // #154a55

        setTitle("Records - Art Louie Lacro 22-1190-438");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Light modern background
        getContentPane().setBackground(new Color(245, 248, 252));

        // ==================== TABLE ====================
        String[] columns = {
                "ID", "First Name", "Last Name",
                "Lab Work 1", "Lab Work 2", "Lab Work 3",
                "Prelim Exam", "Attendance"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;   // ← change to true (all cells editable)
                // or: return column != 0;   // if you want ID not editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(215, 220, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(220, 230, 245));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // ==================== INPUT PANEL ====================
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 190, 210)),
                        " Add New Record ",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        inputPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels & fields - more readable & aligned
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        addField(inputPanel, gbc, 0, 0, "Student ID:", txtID = new JTextField(8), labelFont, fieldFont);
        addField(inputPanel, gbc, 1, 0, "First Name:", txtFI = new JTextField(15), labelFont, fieldFont);
        addField(inputPanel, gbc, 2, 0, "Last Name:", txtLN = new JTextField(15), labelFont, fieldFont);

        addField(inputPanel, gbc, 0, 1, "Lab Work 1:", txtLW1 = new JTextField(6), labelFont, fieldFont);
        addField(inputPanel, gbc, 1, 1, "Lab Work 2:", txtLW2 = new JTextField(6), labelFont, fieldFont);
        addField(inputPanel, gbc, 2, 1, "Lab Work 3:", txtLW3 = new JTextField(6), labelFont, fieldFont);

        addField(inputPanel, gbc, 0, 2, "Prelim Exam:", txtPrelim = new JTextField(6), labelFont, fieldFont);
        addField(inputPanel, gbc, 1, 2, "Attendance:", txtAttendance = new JTextField(6), labelFont, fieldFont);

        // Buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(inputPanel.getBackground());

        JButton btnAdd = new JButton("Add Record");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnSave = new JButton("Save to CSV");

        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(140, 38));
        btnSave.addActionListener(e -> saveTableToCSV());
        buttonPanel.add(btnSave); 

        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(140, 38));
        btnDelete.setPreferredSize(new Dimension(160, 38));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.SOUTH);

        // ==================== ACTIONS ====================
        btnAdd.addActionListener(e -> addRecord());
        btnDelete.addActionListener(e -> deleteSelected());

        // Load data
        loadCSV();

        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int col, int row,
                          String labelText, JTextField field, Font lblFont, Font fldFont) {
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(lblFont);
        panel.add(lbl, gbc);

        gbc.gridx = col * 2 + 1;
        field.setFont(fldFont);
        field.setPreferredSize(new Dimension(140, 30));
        panel.add(field, gbc);
    }

    private void loadCSV() {
        String filePath = "MOCK_DATA.csv";
        try (BufferedReader br = new BufferedReader(
        new FileReader("Prelim-Exam/javarice/MOCK_DATA.csv"))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 8) {
                    model.addRow(data);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading CSV file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRecord() {
        JTextField[] allFields = {txtID, txtFI, txtLN, txtLW1, txtLW2, txtLW3, txtPrelim, txtAttendance};

        // Check empty
        for (JTextField f : allFields) {
            if (f.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Basic number check
        try {
            Double.parseDouble(txtLW1.getText().trim());
            Double.parseDouble(txtLW2.getText().trim());
            Double.parseDouble(txtLW3.getText().trim());
            Double.parseDouble(txtPrelim.getText().trim());
            Double.parseDouble(txtAttendance.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Grade fields must contain valid numbers.", "Invalid Grade", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.addRow(new Object[]{
                txtID.getText().trim(),
                txtFI.getText().trim(),
                txtLN.getText().trim(),
                txtLW1.getText().trim(),
                txtLW2.getText().trim(),
                txtLW3.getText().trim(),
                txtPrelim.getText().trim(),
                txtAttendance.getText().trim()
        });

        // Clear fields
        for (JTextField f : allFields) f.setText("");
        txtID.requestFocus();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this student record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }

    private void saveTableToCSV() {
        String filePath = "Prelim-Exam/javarice/MOCK_DATA.csv";   // ← use YOUR correct path here!
        // or simpler: "MOCK_DATA.csv" if you moved it

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            // Optional: write header
            pw.println("ID,First Name,Last Name,Lab Work 1,Lab Work 2,Lab Work 3,Prelim Exam,Attendance");

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    pw.print(value == null ? "" : value.toString());
                    if (col < model.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClassRecordsApp::new);
    }
}