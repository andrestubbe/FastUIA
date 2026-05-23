package fastuia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * UIAMapOverlay — A borderless, semi-transparent HUD showing the UIA structure.
 * This demo visualizes the "Map" of UI elements surrounding the focused element.
 */
public class UIAMapOverlay extends JFrame {
    private final FastUIA uia;
    private final MapPanel mapPanel;
    private FastUIAElement currentElement = null;

    public UIAMapOverlay() {
        this.uia = new FastUIA();
        this.mapPanel = new MapPanel();

        // 1. Frame Setup: Undecorated & Transparent
        setUndecorated(true);
        setAlwaysOnTop(true);
        setSize(400, 320);
        setBackground(new Color(0, 0, 0, 0)); 
        setLayout(new BorderLayout());
        
        // Add Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel(" FASTUIA MAP OVERLAY", SwingConstants.LEFT);
        title.setForeground(Color.CYAN);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.add(title, BorderLayout.WEST);
        
        JButton btnClose = new JButton("×");
        btnClose.setForeground(Color.WHITE);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> System.exit(0));
        header.add(btnClose, BorderLayout.EAST);
        
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(header, BorderLayout.NORTH);
        container.add(mapPanel, BorderLayout.CENTER);
        
        add(container, BorderLayout.CENTER);

        // 2. Interactivity: Draggable
        Point mouseClick = new Point();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseClick.x = e.getX();
                mouseClick.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseClick.x, y - mouseClick.y);
            }
        });

        startTracking();
    }

    private void startTracking() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    FastUIAElement el = uia.getFocusedElement();
                    if (el != null && el.isValid()) {
                        // Avoid tracking our own window
                        String name = el.getName();
                        if (name != null && name.contains("FASTUIA MAP OVERLAY")) {
                            el.release();
                        } else if (currentElement == null || currentElement.handle() != el.handle()) {
                            if (currentElement != null) currentElement.release();
                            currentElement = el;
                            updateMap(currentElement);
                        } else {
                            el.release();
                        }
                    }
                    Thread.sleep(150); // Balanced update rate
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateMap(FastUIAElement focus) {
        List<ElementData> data = new ArrayList<>();
        
        // Get focused element bounds
        Rect focusRect = focus.getBoundingRect();
        data.add(new ElementData(focusRect, focus.getName(), focus.getControlType(), true));

        // Walk the tree to get siblings for context
        FastUIAElement parent = focus.getParent();
        if (parent != null) {
            FastUIAElement child = parent.getFirstChild();
            int limit = 50; // Safety limit
            while (child != null && limit-- > 0) {
                if (child.handle() != focus.handle()) {
                    data.add(new ElementData(child.getBoundingRect(), child.getName(), child.getControlType(), false));
                }
                FastUIAElement next = child.getNextSibling();
                child.release();
                child = next;
            }
            parent.release();
        }

        SwingUtilities.invokeLater(() -> mapPanel.setData(data, focusRect));
    }

    private static class ElementData {
        Rect rect;
        String name;
        ControlType type;
        boolean isFocused;

        ElementData(Rect rect, String name, ControlType type, boolean isFocused) {
            this.rect = rect; this.name = name; this.type = type; this.isFocused = isFocused;
        }
    }

    private class MapPanel extends JPanel {
        private List<ElementData> elements = new ArrayList<>();
        private Rect focusBounds = null;

        public void setData(List<ElementData> elements, Rect focusBounds) {
            this.elements = elements;
            this.focusBounds = focusBounds;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background with rounded corners
            g2.setColor(new Color(10, 10, 15, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.setColor(new Color(0, 255, 255, 80));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            if (focusBounds == null || elements.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Waiting for focus...", 20, 50);
                return;
            }

            // Calculate "Global" bounding box for the map to auto-scale
            int minX = focusBounds.x(), minY = focusBounds.y();
            int maxX = focusBounds.x() + focusBounds.width(), maxY = focusBounds.y() + focusBounds.height();
            
            for(ElementData ed : elements) {
                minX = Math.min(minX, ed.rect.x());
                minY = Math.min(minY, ed.rect.y());
                maxX = Math.max(maxX, ed.rect.x() + ed.rect.width());
                maxY = Math.max(maxY, ed.rect.y() + ed.rect.height());
            }

            int totalW = maxX - minX;
            int totalH = maxY - minY;
            
            // Padding
            int pad = 40;
            double scaleX = (double)(getWidth() - pad*2) / Math.max(1, totalW);
            double scaleY = (double)(getHeight() - pad*2) / Math.max(1, totalH);
            double scale = Math.min(scaleX, scaleY);
            
            // Center the map
            int offsetX = pad + (int)((getWidth() - pad*2 - totalW * scale) / 2);
            int offsetY = pad + (int)((getHeight() - pad*2 - totalH * scale) / 2);

            for (ElementData ed : elements) {
                int rx = (int) ((ed.rect.x() - minX) * scale) + offsetX;
                int ry = (int) ((ed.rect.y() - minY) * scale) + offsetY;
                int rw = (int) (ed.rect.width() * scale);
                int rh = (int) (ed.rect.height() * scale);

                if (ed.isFocused) {
                    g2.setColor(new Color(0, 255, 255));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRect(rx, ry, Math.max(2, rw), Math.max(2, rh));
                    
                    // Label
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    String label = (ed.name != null && !ed.name.isEmpty()) ? ed.name : ed.type.toString();
                    g2.drawString(label, rx, ry - 5);
                } else {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRect(rx, ry, Math.max(1, rw), Math.max(1, rh));
                }
            }
            
            // Scale indicator
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            g2.setColor(Color.GRAY);
            g2.drawString(String.format("Scale: %.2fx", scale), 10, getHeight() - 10);
        }
    }

    public static void main(String[] args) {
        // High-DPI support
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            UIAMapOverlay overlay = new UIAMapOverlay();
            overlay.setLocation(50, 50); // Start top-left
            overlay.setVisible(true);
        });
    }
}
