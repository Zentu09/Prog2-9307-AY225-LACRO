import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Top10GamesReport extends JFrame {

    private JTextField pathField;
    private JTable table;
    private DefaultTableModel model;

    public Top10GamesReport() {
        super("Top 10 Games by Total Sales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ── TOP PANEL: file selection ───────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        pathField = new JTextField("No file selected", 50);
        pathField.setEditable(false);
        pathField.setFont(new Font("Consolas", Font.PLAIN, 14));
        pathField.setBackground(new Color(245, 245, 245));

        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseButton.addActionListener(this::openNativeFileChooser);

        JButton loadButton = new JButton("Load & Show Top 10");
        loadButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadButton.addActionListener(this::loadFile);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(browseButton);
        buttonPanel.add(loadButton);

        topPanel.add(new JLabel(" CSV File:"), BorderLayout.WEST);
        topPanel.add(pathField, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // ── TABLE ────────────────────────────────────────────────────────────
        String[] columns = {"Rank", "Title", "Platform", "Genre", "Publisher", "Total Sales (M)"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(55);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Top 10 Games by Global Sales"));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openNativeFileChooser(ActionEvent e) {
        FileDialog dialog = new FileDialog(this, "Select Video Game Sales CSV File", FileDialog.LOAD);
        dialog.setDirectory(null);                    // start at default/recent location
        dialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".csv"));
        dialog.setVisible(true);

        String fileName = dialog.getFile();
        String directory = dialog.getDirectory();

        if (fileName != null && directory != null) {
            File selected = new File(directory, fileName);
            pathField.setText(selected.getAbsolutePath());
        }
    }

    private void loadFile(ActionEvent e) {
        model.setRowCount(0);  // clear table

        String path = pathField.getText().trim();
        if (path.equals("No file selected") || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a CSV file first.", "No file", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            JOptionPane.showMessageDialog(this, "Selected file not found or is not a file.", "Invalid file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Read CSV ────────────────────────────────────────────────────────
        Map<String, GameAggregate> aggregates = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();  // skip header
            if (header == null) throw new IOException("Empty file");

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 8) continue;

                try {
                    String title     = cols[1].trim().replace("\"", "");
                    String platform  = cols[2].trim().replace("\"", "");
                    String genre     = cols[3].trim().replace("\"", "");
                    String publisher = cols[5].trim().replace("\"", "");
                    String salesStr  = cols[7].trim().replace("\"", "");

                    double sales = Double.parseDouble(salesStr);

                    GameAggregate agg = aggregates.computeIfAbsent(title,
                        k -> new GameAggregate(title, platform, genre, publisher));

                    agg.totalSales += sales;
                } catch (NumberFormatException ignored) {
                    // skip bad rows
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file:\n" + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Sort descending by total sales ──────────────────────────────────
        java.util.List<GameAggregate> list = new ArrayList<>(aggregates.values());
        list.sort((a, b) -> Double.compare(b.totalSales, a.totalSales));

        // ── Fill table with top 10 ──────────────────────────────────────────
        int rank = 1;
        for (int i = 0; i < Math.min(10, list.size()); i++) {
            GameAggregate g = list.get(i);
            model.addRow(new Object[]{
                rank++,
                g.title,
                g.platform,
                g.genre,
                g.publisher,
                String.format("%.2f M", g.totalSales)
            });
        }

        if (aggregates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid sales data found.", "No data", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Helper class ────────────────────────────────────────────────────────
    static class GameAggregate {
        String title;
        String platform;
        String genre;
        String publisher;
        double totalSales = 0.0;

        GameAggregate(String t, String p, String g, String pub) {
            title = t;
            platform = p;
            genre = g;
            publisher = pub;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Top10GamesReport::new);
    }
}