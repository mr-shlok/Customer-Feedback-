import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.fasterxml.jackson.databind.JsonNode;
public class FeedbackFrame extends JFrame {
    private final long userId;
    private final String username;
    private final JTextArea message = new JTextArea();
    private Long editingId = null;
    private final JButton submitButton = new JButton("Submit");
    private final Color BG = new Color(248, 248, 248);
    private final Color BORDER = new Color(215, 215, 215);
    private final Color BUTTON = new Color(220, 220, 220);
    private final Color TEXT = new Color(55, 55, 55);
    public FeedbackFrame(long id, String name) {
        userId = id;
        username = name;
        setTitle("Customer Feedback");
        setSize(760, 760);
        setMinimumSize(new Dimension(520, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 35, 40, 35));
        JLabel title = new JLabel("Feedback");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton addButton = makeButton("Add Feedback");
        JButton editButton = makeButton("Edit Feedback");
        JPanel buttons = new JPanel(new GridLayout(1, 2, 24, 0));
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        feedbackLabel.setForeground(TEXT);
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        message.setFont(new Font("Monospaced", Font.PLAIN, 19));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane scroll = new JScrollPane(message);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.setPreferredSize(new Dimension(600, 260));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        submitButton.setFont(new Font("Arial", Font.PLAIN, 21));
        submitButton.setBackground(BUTTON);
        submitButton.setForeground(TEXT);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createLineBorder(BORDER));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        submitButton.setPreferredSize(new Dimension(600, 62));
        card.add(title);
        card.add(Box.createVerticalStrut(48));
        card.add(buttons);
        card.add(Box.createVerticalStrut(48));
        card.add(feedbackLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(scroll);
        card.add(Box.createVerticalStrut(32));
        card.add(submitButton);
        root.add(card);
        setContentPane(root);
        addButton.addActionListener(e -> {
            editingId = null;
            message.setText("");
            submitButton.setText("Submit");
        });
        editButton.addActionListener(e -> editFeedback());
        submitButton.addActionListener(e -> saveFeedback());
        setVisible(true);
    }
    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 21));
        button.setBackground(BUTTON);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    private void editFeedback() {
        try {
            JsonNode list = ApiClient.get("/feedback/" + userId);
            if (!list.isArray() || list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No previous feedback found.");
                return;
            }
            DefaultListModel<JsonNode> model =
                new DefaultListModel<>();
            for (JsonNode item : list) {
                model.addElement(item);
            }
            JList<JsonNode> feedbackList = new JList<>(model);
            feedbackList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
            feedbackList.setCellRenderer((l, value, index,
                    selected, focus) -> {
                String text = "#" + value.path("id").asText()
                    + " - " + value.path("message").asText();
                JLabel label = new JLabel(text);
                label.setBorder(new EmptyBorder(8, 8, 8, 8));
                label.setOpaque(true);
                label.setBackground(selected
                    ? new Color(210, 220, 240) : Color.WHITE);
                return label;
            });
            int result = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(feedbackList),
                "Select Feedback to Edit",
                JOptionPane.OK_CANCEL_OPTION);
            JsonNode selected = feedbackList.getSelectedValue();
            if (result == JOptionPane.OK_OPTION && selected != null) {
                editingId = selected.path("id").asLong();
                message.setText(selected.path("message").asText());
                submitButton.setText("Update");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void saveFeedback() {
        String text = message.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your feedback.");
            return;
        }
        try {
            if (editingId == null) {
                ApiClient.post(
                    "/feedback/" + userId,
                    ApiClient.data("message", text));
            } else {
                ApiClient.put(
                    "/feedback/" + editingId + "/" + userId,
                    ApiClient.data("message", text));
            }
            JOptionPane.showMessageDialog(this,
                editingId == null
                    ? "Feedback submitted successfully."
                    : "Feedback updated successfully.");
            editingId = null;
            message.setText("");
            submitButton.setText("Submit");
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this,
            ex.getMessage() == null
                ? "An unexpected error occurred."
                : ex.getMessage(),
            "Customer Feedback",
            JOptionPane.ERROR_MESSAGE);
    }
}