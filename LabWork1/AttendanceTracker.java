import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Attendance Tracker Application
 * Includes a Proceed button that activates when required fields are filled
 */
public class AttendanceTracker {

    public static void main(String[] args) {

        // Create the main frame
        JFrame frame = new JFrame("Attendance Tracker");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel courseLabel = new JLabel("Course / Year:");
        JLabel timeLabel = new JLabel("Time In:");
        JLabel signatureLabel = new JLabel("E-Signature:");

        // Text fields
        JTextField nameField = new JTextField();
        JTextField courseField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField signatureField = new JTextField();

        // Proceed button
        JButton proceedButton = new JButton("Proceed");
        proceedButton.setEnabled(false); // Disabled by default

        // Obtain system date and time
        String timeIn = LocalDateTime.now().toString();
        timeField.setText(timeIn);
        timeField.setEditable(false);

        // Generate E-Signature
        String eSignature = UUID.randomUUID().toString();
        signatureField.setText(eSignature);
        signatureField.setEditable(false);

        // Listener to check if both fields are filled
        DocumentListener inputChecker = new DocumentListener() {
            public void checkFields() {
                boolean isFilled = !nameField.getText().trim().isEmpty()
                                && !courseField.getText().trim().isEmpty();
                proceedButton.setEnabled(isFilled);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }
        };

        // Attach listener to text fields
        nameField.getDocument().addDocumentListener(inputChecker);
        courseField.getDocument().addDocumentListener(inputChecker);

        // Proceed button action
        proceedButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                frame,
                "Attendance Recorded Successfully!\n\n"
                + "Name: " + nameField.getText() + "\n"
                + "Course/Year: " + courseField.getText() + "\n"
                + "Time In: " + timeField.getText() + "\n"
                + "UID: " + signatureField.getText(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Add components to panel
        panel.add(nameLabel);
        panel.add(nameField);

        panel.add(courseLabel);
        panel.add(courseField);

        panel.add(timeLabel);
        panel.add(timeField);

        panel.add(signatureLabel);
        panel.add(signatureField);

        panel.add(new JLabel()); // Empty space
        panel.add(proceedButton);

        // Add panel to frame
        frame.add(panel);

        // Show the window
        frame.setVisible(true);
    }
}
