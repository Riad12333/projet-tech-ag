package projet;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class auc extends JFrame {
    public static auc instance; // Static reference for centralized logging
    private JTextField nb;
    private JTextField durée;
    private JTextField start;
    private JTextField reserv;
    private JTextArea seller;
    private JPanel mainpannel;
    private JButton button1;
    private JTextArea buyer1;
    private JTextArea buyer3;

    // Premium Color Palette
    private static final Color BG_DARK = new Color(18, 18, 22);
    private static final Color BG_CARD = new Color(30, 30, 38);
    private static final Color ACCENT_BLUE = new Color(0, 122, 255);
    private static final Color ACCENT_BLUE_HOVER = new Color(0, 94, 203);
    private static final Color TEXT_PRIMARY = new Color(245, 245, 247);
    private static final Color TEXT_MUTED = new Color(160, 160, 168);
    private static final Color BORDER_COLOR = new Color(48, 48, 56);

    public auc() {
        instance = this; // Initialize singleton
        setTitle(" A Control Center (JADE)");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null); // Center the window

        // Set look and feel to system if possible, but keep custom colors
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Main Panel (Vertical Layout)
        mainpannel = new JPanel();
        mainpannel.setLayout(new BorderLayout());
        mainpannel.setBackground(BG_DARK);
        mainpannel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header Panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 35, 126), getWidth(), 0,
                        new Color(49, 27, 146));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(900, 75));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("A AUCTION CONTROL CENTER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("JADE Multi-Agent Platform Simulation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(197, 202, 233));

        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(subtitleLabel);
        headerPanel.add(titleTextPanel, BorderLayout.WEST);

        mainpannel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Panel (Controls & Consoles)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Controls Panel (Inputs Card)
        JPanel inputsCard = new JPanel(new GridBagLayout());
        inputsCard.setBackground(BG_CARD);
        inputsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Init Form Fields with Custom Styles
        nb = createStyledField("3");
        durée = createStyledField("10");
        start = createStyledField("100");
        reserv = createStyledField("200");

        // Add inputs to GridBagLayout
        addLabelAndField(inputsCard, "Nb Buyers:", nb, gbc, 0);
        addLabelAndField(inputsCard, "Duration (ms):", durée, gbc, 1);
        addLabelAndField(inputsCard, "Start Price ($):", start, gbc, 2);
        addLabelAndField(inputsCard, "Reserve Price ($):", reserv, gbc, 3);

        // Styled Button
        button1 = new JButton("Start Auction Session");
        button1.setBackground(ACCENT_BLUE);
        button1.setForeground(Color.WHITE);
        button1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button1.setFocusPainted(false);
        button1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button1.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_BLUE.darker(), 1, true),
                new EmptyBorder(8, 20, 8, 20)));
        button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button1.setBackground(ACCENT_BLUE_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button1.setBackground(ACCENT_BLUE);
            }
        });

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        inputsCard.add(button1, gbc);

        centerPanel.add(inputsCard, BorderLayout.NORTH);

        // 3. Consoles Display Panel
        JPanel textPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        textPanel.setOpaque(false);

        seller = createStyledConsole("Seller Agent Operations");
        buyer1 = createStyledConsole("Buyers Action Logs");
        buyer3 = createStyledConsole("Live Transactions & Broadcasts");

        textPanel.add(createConsoleCard("SELLER CONTROLLER", seller, new Color(255, 171, 0)));
        textPanel.add(createConsoleCard("ALL BUYERS ACTIVITY", buyer1, new Color(0, 230, 118)));
        textPanel.add(createConsoleCard("BROADCASTS & WINNERS", buyer3, new Color(0, 229, 255)));

        centerPanel.add(textPanel, BorderLayout.CENTER);
        mainpannel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainpannel);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewComponent();
            }
        });
    }

    private JTextField createStyledField(String defaultVal) {
        JTextField field = new JTextField(defaultVal, 5);
        field.setBackground(BG_DARK);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc,
            int column) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_MUTED);

        gbc.gridx = column;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        panel.add(label, gbc);

        gbc.gridy = 1;
        panel.add(field, gbc);
    }

    private JTextArea createStyledConsole(String placeholder) {
        JTextArea console = new JTextArea();
        console.setBackground(new Color(15, 15, 18));
        console.setForeground(new Color(220, 220, 225));
        console.setCaretColor(Color.WHITE);
        console.setFont(new Font("Consolas", Font.PLAIN, 12));
        console.setEditable(false);
        console.setText(placeholder + "\n---------------------\n");
        return console;
    }

    private JPanel createConsoleCard(String title, JTextArea console, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(accentColor);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(console);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scroll.setBackground(new Color(15, 15, 18));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void addNewComponent() {
        int nbn = Integer.parseInt(nb.getText().trim());
        int duré = Integer.parseInt(durée.getText().trim());
        int starte = Integer.parseInt(start.getText().trim());
        int reserve = Integer.parseInt(reserv.getText().trim());

        try {
            Runtime rt = Runtime.instance();
            ProfileImpl p = new ProfileImpl();
            p.setParameter(Profile.LOCAL_HOST, "localhost");
            p.setParameter(Profile.LOCAL_PORT, "1099");
            p.setParameter(Profile.GUI, "true");
            p.setParameter(Profile.SERVICES, "jade.core.messaging.TopicManagementService");
            ContainerController mc = rt.createMainContainer(p);
            AgentController ag1;

            Random r = new Random();
            for (int i = 1; i <= nbn; i++) {
                int ag = r.nextInt(5000) + starte;
                String h = ag + "";
                Object[] arg = { h };
                String name = "Agent" + i;
                ag1 = mc.createNewAgent(name, buyers.class.getName(), arg);
                ag1.start();
            }
            Object[] arg1 = { nb.getText(), durée.getText(), start.getText(), reserv.getText() };
            ag1 = mc.createNewAgent("seller", projet.seller.class.getName(), arg1);
            ag1.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void buyer1txt(String text) {
        buyer1.append(text + "\n");
        buyer1.setCaretPosition(buyer1.getDocument().getLength());
    }

    public void buyer2txt(String text) {
        buyer3.append(text + "\n");
        buyer3.setCaretPosition(buyer3.getDocument().getLength());
    }

    public void sellertxt(String text) {
        seller.append(text + "\n");
        seller.setCaretPosition(seller.getDocument().getLength());
    }
}
