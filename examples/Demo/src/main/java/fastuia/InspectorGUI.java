package fastuia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * FastUIA Inspector GUI — Ultra-Stable Version.
 * Native calls are strictly separated from UI thread to avoid Race Conditions.
 */
public class InspectorGUI extends JFrame {

    private final FastUIA uia;
    private final JLabel lblProgram = new JLabel("PROGRAM: Searching...");
    private final JLabel lblElement = new JLabel("ELEMENT: Searching...");
    private final JLabel lblType = new JLabel("TYPE: UNKNOWN");
    private final JLabel lblRect = new JLabel("0, 0 [0x0]");
    
    private final PatternIndicator[] indicators = {
        new PatternIndicator("Value"),
        new PatternIndicator("Text"),
        new PatternIndicator("Invoke"),
        new PatternIndicator("Scroll"),
        new PatternIndicator("Expand"),
        new PatternIndicator("Selection")
    };

    private final Color COLOR_BG = Color.BLACK;
    private final Color COLOR_ACCENT = Color.CYAN;
    private final Color COLOR_TEXT = Color.WHITE;

    private FastUIAElement lastExternalElement = null;

    public InspectorGUI() {
        this.uia = new FastUIA();
        setupUI();
        setupSystemTray();
        startUpdateThread();
    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) return;
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image iconImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics g = iconImage.getGraphics();
            g.setColor(COLOR_ACCENT);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
            PopupMenu popup = new PopupMenu();
            MenuItem itemShow = new MenuItem("Open Inspector");
            itemShow.addActionListener(e -> { setVisible(true); setExtendedState(JFrame.NORMAL); });
            MenuItem itemExit = new MenuItem("Exit");
            itemExit.addActionListener(e -> System.exit(0));
            popup.add(itemShow);
            popup.addSeparator();
            popup.add(itemExit);
            TrayIcon trayIcon = new TrayIcon(iconImage, "FastUIA Inspector", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> setVisible(true));
            tray.add(trayIcon);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupUI() {
        setTitle("FastUIA Inspector (Stable)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 750);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JPanel header = new JPanel(new GridLayout(3, 1, 0, 5));
        header.setBackground(COLOR_BG);
        lblProgram.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblProgram.setForeground(Color.YELLOW);
        lblElement.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblElement.setForeground(COLOR_ACCENT);
        lblType.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblType.setForeground(Color.LIGHT_GRAY);
        header.add(lblProgram);
        header.add(lblElement);
        header.add(lblType);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_BG);
        content.add(createSection("GEOMETRY (Screen Coordinates)", lblRect));
        content.add(Box.createVerticalStrut(30));
        content.add(createPatternSection());
        mainPanel.add(content, BorderLayout.CENTER);

        JButton btnAction = new JButton("INTERACT (Modify Focused Element)");
        btnAction.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAction.setBackground(COLOR_ACCENT);
        btnAction.setForeground(Color.BLACK);
        btnAction.setPreferredSize(new Dimension(0, 50));
        btnAction.addActionListener(e -> performAction());
        mainPanel.add(btnAction, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 20));
        p.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        JLabel t = new JLabel(" " + title);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(COLOR_ACCENT);
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        valueLabel.setForeground(COLOR_TEXT);
        valueLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(t, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JPanel createPatternSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);
        JLabel t = new JLabel("SUPPORTED PATTERNS");
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setForeground(COLOR_ACCENT);
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        p.add(t, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setBackground(COLOR_BG);
        for (PatternIndicator pi : indicators) grid.add(pi);
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private void startUpdateThread() {
        Thread t = new Thread(() -> {
            FastUIAElement currentElement = null;
            while (true) {
                try {
                    FastUIAElement nextElement = uia.getFocusedElement();
                    if (nextElement != null && nextElement.isValid()) {
                        String name = nextElement.getName();
                        if (name != null && name.contains("FastUIA Inspector")) {
                            nextElement.release();
                        } else {
                            if (currentElement == null || currentElement.handle() != nextElement.handle()) {
                                if (currentElement != null) currentElement.release();
                                currentElement = nextElement;
                                lastExternalElement = currentElement;
                                
                                // READ DATA IN BACKGROUND THREAD
                                UIData data = extractData(currentElement);
                                SwingUtilities.invokeLater(() -> applyData(data));
                            } else {
                                nextElement.release();
                            }
                        }
                    }
                    Thread.sleep(200);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private UIData extractData(FastUIAElement el) {
        String name = el.getName();
        String type = el.getControlType().toString();
        Rect r = el.getBoundingRect();
        
        String program = "Unknown";
        FastUIAElement p = el;
        for(int i=0; i<10; i++) {
            if (p.getControlType() == ControlType.WINDOW) {
                program = p.getName();
                if (p != el) p.release();
                break;
            }
            FastUIAElement next = p.getParent();
            if (next == null) break;
            if (p != el) p.release();
            p = next;
        }
        
        boolean[] patterns = {
            el.supportsValue(), el.supportsText(), el.supportsInvoke(),
            el.supportsScroll(), el.supportsExpandCollapse(), el.supportsSelection()
        };
        
        return new UIData(program, name, type, r, patterns);
    }

    private void applyData(UIData d) {
        lblProgram.setText("PROGRAM: " + d.program.toUpperCase());
        lblElement.setText("ELEMENT: " + (d.name == null || d.name.isEmpty() ? "<No Name>" : d.name.toUpperCase()));
        lblType.setText("TYPE:    " + d.type);
        lblRect.setText(d.rect.x() + ", " + d.rect.y() + " [" + d.rect.width() + "x" + d.rect.height() + "]");
        for(int i=0; i<6; i++) indicators[i].setActive(d.patterns[i]);
    }

    private void performAction() {
        if (lastExternalElement != null && lastExternalElement.isValid()) {
            if (lastExternalElement.supportsValue()) {
                lastExternalElement.setValue(lastExternalElement.getValue() + " [FastUIA]");
            } else if (lastExternalElement.supportsInvoke()) {
                lastExternalElement.invoke();
            }
        }
    }

    private static class UIData {
        String program, name, type;
        Rect rect;
        boolean[] patterns;
        UIData(String p, String n, String t, Rect r, boolean[] pats) {
            this.program = p; this.name = n; this.type = t; this.rect = r; this.patterns = pats;
        }
    }

    private class PatternIndicator extends JPanel {
        private final JLabel label;
        private boolean active = false;
        public PatternIndicator(String name) {
            setLayout(new BorderLayout());
            setBackground(new Color(20, 20, 20));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            label = new JLabel(name, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(Color.GRAY);
            add(label, BorderLayout.CENTER);
        }
        public void setActive(boolean active) {
            this.active = active;
            label.setForeground(active ? COLOR_ACCENT : Color.GRAY);
            setBorder(BorderFactory.createLineBorder(active ? COLOR_ACCENT : Color.DARK_GRAY, 1));
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> new InspectorGUI().setVisible(true));
    }
}
