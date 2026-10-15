import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.fasterxml.jackson.databind.JsonNode;

public class Main extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);

    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    private final JTextField regName = new JTextField();
    private final JTextField email = new JTextField();
    private final JPasswordField regPass = new JPasswordField();
    private final JPasswordField confirm = new JPasswordField();

    private static final Color BG = new Color(248, 248, 248);
    private static final Color BORDER = new Color(195, 195, 195);
    private static final Color BUTTON = new Color(220, 220, 220);
    private static final Color TEXT = new Color(75, 75, 75);

    public Main() {
        setTitle("Customer Feedback");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 500);
        setMinimumSize(new Dimension(480, 450));
        setLocationRelativeTo(null);

        JPanel login = createCard("Login");
        login.add(field(username, "Username"));
        login.add(Box.createVerticalStrut(14));
        login.add(field(password, "Password"));
        login.add(Box.createVerticalStrut(12));

        JButton loginButton = createButton("Log In");
        login.add(loginButton);
        login.add(Box.createVerticalStrut(20));

        JLabel signupLink = createLink(
            "Don't have an account? <u>Sign Up</u>"
        );
        login.add(signupLink);

        JPanel register = createCard("Register");
        register.add(field(regName, "Username"));
        register.add(Box.createVerticalStrut(12));
        register.add(field(email, "Email"));
        register.add(Box.createVerticalStrut(12));
        register.add(field(regPass, "Password"));
        register.add(Box.createVerticalStrut(12));
        register.add(field(confirm, "Confirm Password"));
        register.add(Box.createVerticalStrut(12));

        JButton registerButton = createButton("Sign Up");
        register.add(registerButton);
        register.add(Box.createVerticalStrut(18));

        JLabel loginLink = createLink(
            "Already have an account? <u>Log In</u>"
        );
        register.add(loginLink);

        pages.add(login, "login");
        pages.add(register, "register");

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);
        root.add(pages);
        setContentPane(root);

        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(pages, "register");
            }
        });

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(pages, "login");
            }
        });

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> registerUser());

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    private JPanel createCard(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 215, 215)),
            new EmptyBorder(36, 32, 34, 32)
        ));

        panel.setPreferredSize(new Dimension(
            440, title.equals("Login") ? 330 : 440
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(32));
        return panel;
    }

    private JPanel field(JComponent component, String placeholder) {
        component.setFont(new Font("Arial", Font.PLAIN, 16));
        component.setForeground(Color.GRAY);
        component.setBackground(Color.WHITE);

        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));

        component.setPreferredSize(new Dimension(380, 48));
        component.setMinimumSize(new Dimension(250, 48));
        component.setMaximumSize(new Dimension(380, 48));

        if (component instanceof JTextField) {
            JTextField f = (JTextField) component;
            f.setText(placeholder);

            f.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (f.getText().equals(placeholder)) {
                        f.setText("");
                        f.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) {
                        f.setText(placeholder);
                        f.setForeground(Color.GRAY);
                    }
                }
            });

        } else if (component instanceof JPasswordField) {
            JPasswordField f = (JPasswordField) component;
            f.setEchoChar((char) 0);
            f.setText(placeholder);

            f.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (new String(f.getPassword()).equals(placeholder)) {
                        f.setText("");
                        f.setEchoChar('\u2022');
                        f.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (f.getPassword().length == 0) {
                        f.setEchoChar((char) 0);
                        f.setText(placeholder);
                        f.setForeground(Color.GRAY);
                    }
                }
            });
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(380, 48));
        wrapper.add(component, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(TEXT);
        button.setBackground(BUTTON);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(380, 48));
        button.setMaximumSize(new Dimension(380, 48));
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setCursor(Cursor.getPredefinedCursor(
            Cursor.HAND_CURSOR
        ));
        return button;
    }

    private JLabel createLink(String text) {
        JLabel label = new JLabel(
            "<html>" + text + "</html>",
            SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setCursor(Cursor.getPredefinedCursor(
            Cursor.HAND_CURSOR
        ));
        return label;
    }

    private String textValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        return value.equals(placeholder) ? "" : value;
    }

    private String passwordValue(
            JPasswordField field, String placeholder) {
        String value = new String(field.getPassword());
        return value.equals(placeholder) ? "" : value;
    }

    private void loginUser() {
        String name = textValue(username, "Username");
        String pass = passwordValue(password, "Password");

        if (name.isEmpty() || pass.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        try {
            JsonNode user = ApiClient.post("/login",
                ApiClient.data(
                    "username", name,
                    "password", pass
                )
            );

            dispose();

            if ("ADMIN".equals(user.path("role").asText())) {
                new AdminFrame().setVisible(true);
            } else {
                new FeedbackFrame(
                    user.path("id").asLong(),
                    user.path("username").asText()
                ).setVisible(true);
            }

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void registerUser() {
        String name = textValue(regName, "Username");
        String mail = textValue(email, "Email");
        String pass = passwordValue(regPass, "Password");
        String confirmPass = passwordValue(
            confirm, "Confirm Password"
        );

        if (name.isEmpty() || mail.isEmpty()
                || pass.isEmpty() || confirmPass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (pass.length() < 6) {
            showError("Password must contain at least 6 characters.");
            return;
        }

        if (!pass.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            ApiClient.post("/register",
                ApiClient.data(
                    "username", name,
                    "email", mail,
                    "password", pass
                )
            );

            JOptionPane.showMessageDialog(this,
                "Registration successful! Please log in.");

            regName.setText("Username");
            regName.setForeground(Color.GRAY);
            email.setText("Email");
            email.setForeground(Color.GRAY);

            resetPasswordPlaceholder(regPass, "Password");
            resetPasswordPlaceholder(
                confirm, "Confirm Password"
            );

            cardLayout.show(pages, "login");

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void resetPasswordPlaceholder(
            JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setEchoChar((char) 0);
        field.setForeground(Color.GRAY);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message == null ? "Something went wrong." : message,
            "Customer Feedback",
            JOptionPane.ERROR_MESSAGE);
    }

    // Required by AdminFrame and FeedbackFrame
    public static void run(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                e.getMessage() == null
                    ? "Something went wrong."
                    : e.getMessage(),
                "Customer Feedback",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}