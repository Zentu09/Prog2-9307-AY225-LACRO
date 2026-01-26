import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;

public class GradeCalculatorGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeCalculatorGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Prelim Grade Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(880, 620);
        frame.setLocationRelativeTo(null);

        // Main background
        JPanel contentPane = new JPanel(new BorderLayout(12, 12));
        contentPane.setBackground(Color.decode("#D3F0EA"));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        frame.setContentPane(contentPane);

        // Title
        JLabel title = new JLabel("Required Prelim Exam Grade Calculator", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.decode("#67ABAB"));
        title.setBorder(new EmptyBorder(10, 0, 20, 0));
        frame.add(title, BorderLayout.NORTH);

        // ── Side-by-side layout ───────────────────────────────────────────────
        JPanel splitPane = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPane.setOpaque(false);
        frame.add(splitPane, BorderLayout.CENTER);

        // Left: Inputs
        JPanel leftPanel = createInputPanel();
        splitPane.add(leftPanel);

        // Right: Result
        JPanel rightPanel = createResultPanel();
        splitPane.add(rightPanel);

        // Buttons (bottom, centered)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        buttonPanel.setOpaque(false);

        JButton calcBtn = new JButton("Calculate");
        JButton resetBtn = new JButton("Reset All");

        styleButton(calcBtn, (Color.decode("#54b7c9")), (Color.decode("#429fb0")));
        styleButton(resetBtn, (Color.decode("#ffa411")), (Color.decode("#c27800")));

        buttonPanel.add(calcBtn);
        buttonPanel.add(resetBtn);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Store references for the listener
        JTextField[] fields = (JTextField[]) leftPanel.getClientProperty("fields");
        JLabel[] errorLabels = (JLabel[]) leftPanel.getClientProperty("errors");
        JEditorPane resultPane = (JEditorPane) rightPanel.getClientProperty("resultPane");

        // Calculate logic
        calcBtn.addActionListener(e -> {
            for (JLabel err : errorLabels) err.setText(" ");

            try {
                int lab1 = getValidGrade(fields[0].getText().trim(), "Lab 1", errorLabels[0], 0, 100);
                int lab2 = getValidGrade(fields[1].getText().trim(), "Lab 2", errorLabels[1], 0, 100);
                int lab3 = getValidGrade(fields[2].getText().trim(), "Lab 3", errorLabels[2], 0, 100);
                int absences = getValidGrade(fields[3].getText().trim(), "Absences", errorLabels[3], 0, 4);

                StringBuilder sb = new StringBuilder();

                if (absences >= 4) {
                    sb.append("<font color='red' size='+1'><b>CLASS FAILED</b></font><br>");
                    sb.append("Too many absences (≥ 4).<br>");
                    sb.append("You cannot pass this subject.");
                } else {
                    int attPoints = 100 - (absences * 10);
                    double labAvg = (lab1 + lab2 + lab3) / 3.0;
                    double classStanding = (attPoints * 0.40) + (labAvg * 0.60);

                    long minExam = Math.round((75.0 - (classStanding * 0.30)) / 0.70);
                    long maxExam = Math.round((100.0 - (classStanding * 0.30)) / 0.70);

                    

                    Color minColor = (minExam <= 60) ? new Color(0, 140, 0) :
                                     (minExam <= 80) ? new Color(220, 140, 0) :
                                     new Color(200, 40, 40);

                    // Highlighted boxes for key results
                    sb.append("<b>Inputted Lab Scores:</b><br>");
                    sb.append("• Lab 1: ").append(lab1).append("<br>");
                    sb.append("• Lab 2: ").append(lab2).append("<br>");
                    sb.append("• Lab 3: ").append(lab3).append("<br><br>");
                    // ── Breakdown boxes ────────────────────────────────────────
                    sb.append("<div style='display: flex; gap: 16px; margin: 20px 0; flex-wrap: wrap;'>");

                    // Lab Average box
                    sb.append("<div style='flex: 1; min-width: 180px; border: 2px solid #4a90e2; background-color: #f0f7ff; padding: 12px; border-radius: 10px; text-align: center;'>");
                    sb.append("<div style='font-weight: bold; color: #4a90e2; margin-bottom: 6px;'>Lab Average</div>");
                    sb.append("<div style='font-size: 1.5em; font-weight: bold; color: #2c5282;'>").append(String.format("%.1f", labAvg)).append("</div>");
                    sb.append("</div>");

                    // Attendance Score box
                    sb.append("<div style='flex: 1; min-width: 180px; border: 2px solid #48bb78; background-color: #f0fff4; padding: 12px; border-radius: 10px; text-align: center;'>");
                    sb.append("<div style='font-weight: bold; color: #48bb78; margin-bottom: 6px;'>Attendance Score</div>");
                    sb.append("<div style='font-size: 1.5em; font-weight: bold; color: #276749;'>").append(attPoints).append(" / 100</div>");
                    sb.append("</div>");

                    // Class Standing box
                    sb.append("<div style='flex: 1; min-width: 180px; border: 2px solid #ed8936; background-color: #fffaf0; padding: 12px; border-radius: 10px; text-align: center;'>");
                    sb.append("<div style='font-weight: bold; color: #ed8936; margin-bottom: 6px;'>Class Standing</div>");
                    sb.append("<div style='font-size: 1.5em; font-weight: bold; color: #c05621;'>").append(String.format("%.1f", classStanding)).append("</div>");
                    sb.append("</div>");

                    sb.append("</div><br>");

                    sb.append("<div style='border: 2px solid ").append(colorToHex(minColor))
                    .append("; background-color: ").append(colorToHexWithAlpha(minColor, 0.12))
                    .append("; padding: 16px; border-radius: 10px; margin: 20px 0; text-align: center;'>");

                    sb.append("<div style='font-weight: bold; font-size: 1.3em; margin-bottom: 8px;'>");
                    sb.append("Required Prelim Exam Score<br>to reach final grade 75");
                    sb.append("</div>");

                    sb.append("<div style='font-size: 2.4em; font-weight: bold; color: ").append(colorToHex(minColor)).append(";'>");
                    sb.append(minExam);
                    sb.append("</div>");

                    sb.append("<div style='font-size: 1.1em; color: #555; margin-top: 8px;'>");
                    sb.append("(This is the minimum score you need in the prelim exam<br>");
                    sb.append("so your final grade becomes at least 75)");
                    sb.append("</div>");

                    sb.append("</div><br>");

                    if (maxExam > 100) {
                        sb.append("<div style='border: 2px solid #c00000; background-color: #ffebee; padding: 10px; border-radius: 8px; margin: 8px 0;'>");
                        sb.append("<b style='font-size:1.2em; color:#c00000;'>Cannot reach 100</b><br>");
                        sb.append("<span style='font-size:1.1em;'>Even if you have perfect exam score, your grade will fall short</span>");
                        sb.append("</div>");
                    } else {
                        sb.append("<div style='border: 2px solid #006600; background-color: #e8f5e9; padding: 10px; border-radius: 8px; margin: 8px 0;'>");
                        sb.append("<b style='font-size:1.2em;'>To reach 100:</b><br>");
                        sb.append("<span style='font-size:1.1em;'>You just need to ace your exam to get 100!!</span>");
                        
                        sb.append("</div>");
                    }

                    // Feedback line
                    if (classStanding >= 90) {
                        sb.append("<br><font color='#006600'>Excellent standing! You're in a very strong position.</font>");
                    } else if (classStanding >= 75) {
                        sb.append("<br><font color='#885500'>Solid standing — a good exam can get you to a high grade.</font>");
                    } else if (classStanding >= 60) {
                        sb.append("<br><font color='#aa5500'>Moderate standing — you need a strong performance in the exam.</font>");
                    } else {
                        sb.append("<br><font color='#cc0000'>Low class standing — focus on doing very well in the prelim exam.</font>");
                    }
                }

                resultPane.setContentType("text/html");
                String html = "<html><body style='font-family:Segoe UI; font-size:15pt; line-height:1.5; padding:10px;'>"
                            + sb.toString()
                            + "</body></html>";
                resultPane.setText(html);
                resultPane.setCaretPosition(0);

            } catch (IllegalArgumentException ex) {
                resultPane.setContentType("text/html");
                resultPane.setText("<html><body style='color:#b40000; padding:10px; font-size:15pt;'><b>Please correct the highlighted fields.</b></body></html>");
            }
        });

        resetBtn.addActionListener(e -> {
            for (JTextField f : fields) f.setText("");
            for (JLabel err : errorLabels) err.setText(" ");
            resultPane.setText("");
        });

        frame.setVisible(true);
    }

    private static JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(160, 180, 200)),
                        " Input Grades & Attendance ",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(60, 90, 140)),
                new EmptyBorder(15, 20, 15, 20)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel[] labels = {
                new JLabel("Lab Work 1:"),
                new JLabel("Lab Work 2:"),
                new JLabel("Lab Work 3:"),
                new JLabel("Absences (0–4):")
        };

        JTextField[] fields = new JTextField[4];
        JLabel[] errors = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            labels[i].setFont(new Font("Segoe UI", Font.PLAIN, 15));
            labels[i].setForeground(new Color(50, 60, 80));

            fields[i] = new JTextField(16);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));

            errors[i] = new JLabel(" ");
            errors[i].setForeground(new Color(200, 40, 40));
            errors[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));

            gbc.gridx = 0; gbc.gridy = i;
            form.add(labels[i], gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            form.add(fields[i], gbc);

            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            form.add(errors[i], gbc);
        }

        panel.add(form, BorderLayout.NORTH);
        panel.putClientProperty("fields", fields);
        panel.putClientProperty("errors", errors);
        return panel;
    }

    private static JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(160, 180, 200)),
                        " Results ",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(60, 90, 140)),
                new EmptyBorder(15, 15, 15, 15)));

        JEditorPane resultPane = new JEditorPane();
        resultPane.setEditable(false);
        resultPane.setContentType("text/html");
        resultPane.setBackground(new Color(253, 251, 240));
        resultPane.setForeground(new Color(30, 40, 60));
        resultPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        resultPane.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        panel.putClientProperty("resultPane", resultPane);
        return panel;
    }

    private static void styleButton(JButton btn, Color bg, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 48));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg);   }
        });
    }

    private static String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String colorToHexWithAlpha(Color c, double alpha) {
        int a = (int) (alpha * 255);
        return String.format("#%02x%02x%02x%02x", a, c.getRed(), c.getGreen(), c.getBlue());
    }

    private static int getValidGrade(String input, String fieldName, JLabel errorLabel, int min, int max)
            throws IllegalArgumentException {
        if (input.isBlank()) {
            errorLabel.setText("Required");
            throw new IllegalArgumentException();
        }
        try {
            int value = Integer.parseInt(input);
            if (value < min || value > max) {
                errorLabel.setText("Must be " + min + "–" + max);
                throw new IllegalArgumentException();
            }
            return value;
        } catch (NumberFormatException e) {
            errorLabel.setText("Enter a number");
            throw e;
        }
    }
}