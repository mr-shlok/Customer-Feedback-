import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminFrame extends JFrame {

    private final Color pageColor = new Color(248, 248, 248);
    private final Color gridColor = new Color(210, 210, 210);
    private final Color headerColor = new Color(239, 239, 239);
    private final Color textColor = new Color(45, 45, 45);

    private JTable table;
    private DefaultTableModel model;

    public AdminFrame() {
        setTitle("Admin Feedback Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        createUI();
        loadFeedback();
    }

    private void createUI() {
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(pageColor);
        background.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 28));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(35, 32, 22, 32));
        card.setPreferredSize(new Dimension(1050, 470));

        // Dashboard heading
        JLabel title = new JLabel(
                "Admin Feedback Dashboard",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 38));
        title.setForeground(textColor);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(15, 0, 4, 0));
        titlePanel.add(title, BorderLayout.CENTER);

        card.add(titlePanel, BorderLayout.NORTH);

        // Feedback table
        model = new DefaultTableModel(
                new Object[]{"ID", "Feedback", "Date", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 19));
        table.setForeground(textColor);
        table.setRowHeight(86);
        table.setShowGrid(true);
        table.setGridColor(gridColor);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setSelectionBackground(new Color(235, 242, 250));
        table.setSelectionForeground(textColor);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 20)
        );
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 60));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(58);
        table.getColumnModel().getColumn(0).setMinWidth(50);

        table.getColumnModel().getColumn(1).setPreferredWidth(520);
        table.getColumnModel().getColumn(1).setMinWidth(280);

        table.getColumnModel().getColumn(2).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setMinWidth(110);

        table.getColumnModel().getColumn(3).setPreferredWidth(270);
        table.getColumnModel().getColumn(3).setMinWidth(255);

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0)
                .setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2)
                .setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(1)
                .setCellRenderer(new FeedbackRenderer());

        table.getColumnModel().getColumn(3)
                .setCellRenderer(new ActionRenderer());

        table.getColumnModel().getColumn(3)
                .setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(gridColor));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // IMPORTANT: reserve enough space even when the table is empty
        scrollPane.setPreferredSize(new Dimension(980, 245));
        scrollPane.setMinimumSize(new Dimension(700, 180));

        card.add(scrollPane, BorderLayout.CENTER);

        // Log Out link
        JButton logoutButton = new JButton("<html><u>Log Out</u></html>");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 19));
        logoutButton.setForeground(new Color(45, 45, 180));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        logoutButton.addActionListener(e -> {
            dispose();
            new Main().setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(logoutButton);

        card.add(bottomPanel, BorderLayout.SOUTH);

        background.add(card);
        setContentPane(background);
    }

    private void loadFeedback() {
        try {
            JsonNode response = ApiClient.get("/admin/feedback");

            model.setRowCount(0);

            if (response == null || !response.isArray()) {
                JOptionPane.showMessageDialog(
                        this,
                        "The server did not return a feedback list.",
                        "Response Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            for (JsonNode feedback : response) {
                long id = feedback.path("id").asLong();
                String message = feedback.path("message").asText("");
                String date = formatDate(
                        feedback.path("createdAt").asText("")
                );

                model.addRow(new Object[]{
                        id, message, date, "Actions"
                });
            }

            table.revalidate();
            table.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load feedback.\n"
                            + "Make sure the Spring Boot backend is running.\n\n"
                            + ex.getMessage(),
                    "Loading Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String formatDate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        try {
            return LocalDateTime.parse(value).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );
        } catch (Exception ex) {
            return value.length() >= 10
                    ? value.substring(0, 10)
                    : value;
        }
    }

    private long getFeedbackId(int row) {
        return ((Number) model.getValueAt(row, 0)).longValue();
    }

    private String getFeedbackMessage(int row) {
        Object value = model.getValueAt(row, 1);
        return value == null ? "" : value.toString();
    }

    // VIEW
    private void viewFeedback(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return;
        }

        JTextArea area = new JTextArea(getFeedbackMessage(row), 8, 35);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Feedback Details - ID " + getFeedbackId(row),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // EDIT
    private void editFeedback(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return;
        }

        long id = getFeedbackId(row);

        JTextArea area = new JTextArea(getFeedbackMessage(row), 8, 35);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        int result = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(area),
                "Edit Feedback - ID " + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String updatedMessage = area.getText().trim();

        if (updatedMessage.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Feedback cannot be empty."
            );
            return;
        }

        try {
            ApiClient.put(
                    "/admin/feedback/" + id,
                    ApiClient.data("message", updatedMessage)
            );

            JOptionPane.showMessageDialog(
                    this, "Feedback updated successfully."
            );
            loadFeedback();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update feedback.\n" + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // DELETE
    private void deleteFeedback(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return;
        }

        long id = getFeedbackId(row);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete feedback ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ApiClient.delete("/admin/feedback/" + id);

            JOptionPane.showMessageDialog(
                    this, "Feedback deleted successfully."
            );
            loadFeedback();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete feedback.\n" + ex.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class FeedbackRenderer extends DefaultTableCellRenderer {
        FeedbackRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(8, 14, 8, 10));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
            );

            setText(value == null ? "" : value.toString());
            setToolTipText(value == null ? null : value.toString());
            setFont(new Font("Arial", Font.PLAIN, 18));
            setForeground(textColor);

            if (!selected) {
                setBackground(Color.WHITE);
            }

            return this;
        }
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3, 0)
        );
        panel.setBackground(Color.WHITE);
        panel.add(createActionButton("View"));
        panel.add(createActionButton("Edit"));
        panel.add(createActionButton("Delete"));
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(
                text.equals("Delete") ? 90 : 75, 42
        ));
        button.setFocusPainted(false);
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(205, 205, 205)
                ),
                new EmptyBorder(3, 5, 3, 5)
        ));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        return button;
    }

    private class ActionRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            return createActionPanel();
        }
    }

    private class ActionEditor extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3, 0)
        );

        private int editingRow;

        ActionEditor() {
            panel.setBackground(Color.WHITE);

            JButton view = createActionButton("View");
            JButton edit = createActionButton("Edit");
            JButton delete = createActionButton("Delete");

            view.addActionListener(e -> {
                int row = editingRow;
                fireEditingStopped();
                viewFeedback(row);
            });

            edit.addActionListener(e -> {
                int row = editingRow;
                fireEditingStopped();
                editFeedback(row);
            });

            delete.addActionListener(e -> {
                int row = editingRow;
                fireEditingStopped();
                deleteFeedback(row);
            });

            panel.add(view);
            panel.add(edit);
            panel.add(delete);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean selected,
                int row, int column) {
            editingRow = table.convertRowIndexToModel(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    // Rounded white dashboard card
    private static class RoundedPanel extends JPanel {
        RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 18, 18
            );

            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}