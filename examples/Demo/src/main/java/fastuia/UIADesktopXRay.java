package fastuia;

import fasthotkey.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * UIADesktopXRay — A 1:1 Desktop Overlay (V5).
 * - Entire screen structure traversal.
 * - Labels for Focused (Cyan) and Hovered (Magenta/White) elements.
 * - Global Hotkeys via FastHotkey (Arrow UP/DOWN for depth, ESC to exit).
 * - System Tray to exit.
 */
public class UIADesktopXRay extends JFrame {
    private static final String WINDOW_TITLE = "FastUIA X-Ray Overlay";
    private final FastUIA uia;
    private final XRayPanel xRayPanel;
    private FastUIAElement currentFocus = null;
    private FastUIAElement currentHover = null;
    private volatile int maxDepth = 3;

    public UIADesktopXRay() {
        super(WINDOW_TITLE);
        this.uia = new FastUIA();
        this.xRayPanel = new XRayPanel();
        xRayPanel.setOpaque(false);

        setUndecorated(true);
        setAlwaysOnTop(true);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
        setBounds(screenBounds);
        
        setBackground(new Color(0, 0, 0, 1)); 
        setLayout(new BorderLayout());
        add(xRayPanel, BorderLayout.CENTER);

        System.out.println("Window created: " + screenBounds);

        setupSystemTray();
        setupHotkeys();

        SwingUtilities.invokeLater(() -> {
            try {
                uia.setClickThrough(WINDOW_TITLE, true);
            } catch (UnsatisfiedLinkError e) {
                System.err.println("Warning: nativeSetClickThrough not found.");
            }
        });

        xRayPanel.addMouseWheelListener(e -> {
            maxDepth = Math.max(1, Math.min(10, maxDepth - e.getWheelRotation()));
        });

        startTracking();
    }

    private void setupHotkeys() {
        try {
            // Loading directly from user's build folder to avoid cluttering pom.xml
            String libPath = "C:/Users/andre/Documents/2026-04-28-Work-FastJava/FastHotkey/build/fasthotkey.dll";
            FastHotkey.loadLibrary(libPath);
            
            // Register Global Keys (Aggressive mode ensures they work regardless of focus)
            FastHotkey.register(1, 0, KeyCodes.VK_ESCAPE, id -> System.exit(0), HotkeyMode.AGGRESSIVE);
            FastHotkey.register(2, 0, KeyCodes.VK_UP, id -> maxDepth = Math.min(10, maxDepth + 1), HotkeyMode.AGGRESSIVE);
            FastHotkey.register(3, 0, KeyCodes.VK_DOWN, id -> maxDepth = Math.max(1, maxDepth - 1), HotkeyMode.AGGRESSIVE);
            
            FastHotkey.start();
            System.out.println("Global Hotkeys registered: ESC (Exit), UP/DOWN (Depth)");
        } catch (Exception e) {
            System.err.println("Failed to initialize FastHotkey: " + e.getMessage());
        }
    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) return;
        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = icon.createGraphics();
            g.setColor(Color.CYAN);
            g.fillOval(0, 0, 16, 16);
            g.dispose();
            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit X-Ray");
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(exitItem);
            TrayIcon trayIcon = new TrayIcon(icon, "FastUIA X-Ray", popup);
            trayIcon.addActionListener(e -> System.exit(0));
            tray.add(trayIcon);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startTracking() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    // 1. Focus Tracking
                    FastUIAElement focus = uia.getFocusedElement();
                    if (focus != null && focus.isValid()) {
                        if (currentFocus == null || currentFocus.handle() != focus.handle()) {
                            if (currentFocus != null) currentFocus.release();
                            currentFocus = focus;
                        } else {
                            focus.release();
                        }
                    }

                    // 2. Hover Tracking
                    PointerInfo pi = MouseInfo.getPointerInfo();
                    if (pi != null) {
                        Point p = pi.getLocation();
                        FastUIAElement hover = uia.getElementFromPoint(p.x, p.y);
                        if (hover != null && hover.isValid()) {
                            if (currentHover == null || currentHover.handle() != hover.handle()) {
                                if (currentHover != null) currentHover.release();
                                currentHover = hover;
                                System.out.println("Hovering: " + currentHover.getName() + " [" + currentHover.getControlType() + "]");
                            } else {
                                hover.release();
                            }
                        }
                    }

                    updateXRay(currentFocus, currentHover);
                    Thread.sleep(150);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateXRay(FastUIAElement focus, FastUIAElement hover) {
        List<ElementData> data = new ArrayList<>();
        long focusH = (focus != null) ? focus.handle() : 0;
        long hoverH = (hover != null) ? hover.handle() : 0;

        FastUIAElement root = uia.getRootElement();
        if (root != null) {
            collectElements(root, focusH, hoverH, data, 0, 1000); 
            root.release();
        }
        
        // Always ensure hover is in the list, even if deeper than maxDepth
        if (hover != null && hover.isValid()) {
            boolean alreadyIn = false;
            for(ElementData ed : data) if(ed.isHovered) { alreadyIn = true; break; }
            if(!alreadyIn) {
                Rect r = hover.getBoundingRect();
                if(r.width() > 0 && r.height() > 0) {
                    data.add(new ElementData(r, hover.getName(), hover.getControlType(), false, true));
                }
            }
        }

        SwingUtilities.invokeLater(() -> xRayPanel.setData(data, maxDepth));
    }

    private void collectElements(FastUIAElement el, long focusH, long hoverH, List<ElementData> data, int depth, int limit) {
        if (data.size() >= limit || depth > maxDepth) return;
        
        Rect r = el.getBoundingRect();
        if (r.width() > 0 && r.height() > 0) {
            data.add(new ElementData(r, el.getName(), el.getControlType(), el.handle() == focusH, el.handle() == hoverH));
        }
        
        FastUIAElement child = el.getFirstChild();
        while (child != null) {
            collectElements(child, focusH, hoverH, data, depth + 1, limit);
            FastUIAElement next = child.getNextSibling();
            child.release();
            child = next;
        }
    }

    private static class ElementData {
        Rect rect;
        String name;
        ControlType type;
        boolean isFocused, isHovered;

        ElementData(Rect rect, String name, ControlType type, boolean isFocused, boolean isHovered) {
            this.rect = rect; this.name = name; this.type = type; this.isFocused = isFocused; this.isHovered = isHovered;
        }
    }

    private class XRayPanel extends JPanel {
        private List<ElementData> elements = new ArrayList<>();
        private int currentDepth = 0;

        public void setData(List<ElementData> elements, int depth) {
            this.elements = elements;
            this.currentDepth = depth;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.SrcOver);

            if (elements.isEmpty()) return;

            for (ElementData ed : elements) {
                int x = ed.rect.x();
                int y = ed.rect.y();
                int w = ed.rect.width();
                int h = ed.rect.height();

                if (ed.isFocused) {
                    g2.setColor(new Color(0, 255, 255, 30));
                    g2.fillRect(x, y, w, h);
                    g2.setColor(new Color(0, 255, 255, 255));
                    g2.setStroke(new BasicStroke(6.0f));
                    g2.drawRect(x, y, w, h);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                    String label = (ed.name != null && !ed.name.isEmpty()) ? ed.name : ed.type.toString();
                    g2.drawString(label, x + 10, y + 45);
                } else if (ed.isHovered) {
                    // Hovered: Solid Lime + Medium Text
                    g2.setColor(new Color(0, 255, 0, 40));
                    g2.fillRect(x, y, w, h);
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(4.0f));
                    g2.drawRect(x, y, w, h);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 24));
                    String label = (ed.name != null && !ed.name.isEmpty()) ? ed.name : ed.type.toString();
                    g2.drawString(label, x + 5, y - 10);
                    System.out.println("Debugging: Hover element " + label + " at " + x + "," + y);
                } else {
                    // Structure: Solid Magenta (Subtle but visible)
                    g2.setColor(new Color(255, 0, 255, 10));
                    g2.fillRect(x, y, w, h);
                    g2.setColor(new Color(255, 0, 255, 255));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRect(x, y, w, h);
                }
            }
            
            g2.setFont(new Font("Monospaced", Font.BOLD, 24));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString(String.format("DEPTH: %d | ELEMENTS: %d", currentDepth, elements.size()), 40, getHeight() - 60);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString("Arrows UP/DOWN: Adjust Depth | ESC: Exit | Cyan: Focus | White: Hover", 40, getHeight() - 35);
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            UIADesktopXRay xray = new UIADesktopXRay();
            xray.setVisible(true);
        });
    }
}
