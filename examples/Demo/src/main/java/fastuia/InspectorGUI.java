package fastuia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * FastUIA Inspector GUI — A premium visual interface for UI Automation.
 * Showcases the current system focus and element properties in real-time.
 */
public class InspectorGUI extends JFrame {

    private final FastUIA uia;
    private final JLabel lblName = new JLabel("Searching...");
    private final JLabel lblType = new JLabel("UNKNOWN");
    private final JLabel lblRect = new JLabel("0, 0 [0x0]");
    
    private final PatternIndicator[] indicators = {
        new PatternIndicator("Value"),
        new PatternIndicator("Text"),
        new PatternIndicator("Invoke"),
        new PatternIndicator("Scroll"),
        new PatternIndicator("Expand"),
        new PatternIndicator("Selection")
    };

    private final Color COLOR_BG = new Color(30, 30, 30);
    private final Color COLOR_ACCENT = new Color(0, 173, 181);
    private final Color COLOR_TEXT = new Color(238, 238, 238);
    private final Color COLOR_PANEL = new Color(45, 45, 45);

    public InspectorGUI() {
        this.uia = new FastUIA();
        setupUI();
        startUpdateThread();
    }

    private void setupUI() {
        setTitle("FastUIA Inspector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        lblName.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblName.setForeground(COLOR_ACCENT);
        lblType.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblType.setForeground(COLOR_TEXT.darker());
        
        header.add(lblName);
        header.add(lblType);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(10, 20, 20, 20));

        content.add(createSection("GEOMETRY", lblRect));
        content.add(Box.createVerticalStrut(20));
        content.add(createPatternSection());

        add(content, BorderLayout.CENTER);

        // Actions Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton btnAction = new JButton("INTERACT");
        btnAction.setBackground(COLOR_ACCENT);
        btnAction.setForeground(Color.BLACK);
        btnAction.setFocusPainted(false);
        btnAction.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnAction.addActionListener(e -> performAction());
        
        footer.add(btnAction);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.setMaximumSize(new Dimension(1000, 80));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(COLOR_TEXT.darker());
        
        valueLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        valueLabel.setForeground(COLOR_TEXT);

        p.add(t, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JPanel createPatternSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel t = new JLabel("PATTERNS");
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(COLOR_TEXT.darker());
        p.add(t, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setBackground(COLOR_PANEL);
        grid.setBorder(new EmptyBorder(10, 0, 0, 0));
        for (PatternIndicator pi : indicators) grid.add(pi);
        
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private void startUpdateThread() {
        new Thread(() -> {
            while (true) {
                try {
                    FastUIAElement el = uia.getFocusedElement();
                    if (el != null && el.isValid()) {
                        updateUI(el);
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateUI(FastUIAElement el) {
        SwingUtilities.invokeLater(() -> {
            String name = el.getName();
            lblName.setText(name == null || name.isEmpty() ? "<No Name>" : name);
            lblType.setText(el.getControlType().toString());
            
            Rect r = el.getBoundingRect();
            lblRect.setText(r.x() + ", " + r.y() + " [" + r.width() + "x" + r.height() + "]");

            indicators[0].setActive(el.supportsValue());
            indicators[1].setActive(el.supportsText());
            indicators[2].setActive(el.supportsInvoke());
            indicators[3].setActive(el.supportsScroll());
            indicators[4].setActive(el.supportsExpandCollapse());
            indicators[5].setActive(el.supportsSelection());
        });
    }

    private void performAction() {
        FastUIAElement el = uia.getFocusedElement();
        if (el != null && el.supportsValue()) {
            el.setValue(el.getValue() + " [Modified]");
        }
    }

    private class PatternIndicator extends JPanel {
        private final JLabel label;
        private final JPanel dot;
        private boolean active = false;

        public PatternIndicator(String name) {
            setLayout(new BorderLayout(8, 0));
            setBackground(COLOR_PANEL);
            
            dot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(active ? COLOR_ACCENT : Color.DARK_GRAY);
                    g2.fillOval(0, 0, 10, 10);
                }
            };
            dot.setPreferredSize(new Dimension(10, 10));
            dot.setBackground(COLOR_PANEL);

            label = new JLabel(name);
            label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            label.setForeground(COLOR_TEXT);

            add(dot, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new InspectorGUI().setVisible(true));
    }
}
