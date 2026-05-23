package fastuia;

import fasthotkey.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * UIADesktopXRayV2 — "The Actionable Blueprint"
 * Focuses on "What can this element DO?"
 */
public class UIADesktopXRayV2 extends JFrame {
    private static final String WINDOW_TITLE = "FastUIA Spotlight";
    private final FastUIA uia;
    private final SpotlightPanel spotlightPanel;

    private FastUIAElement currentHover = null;
    private FastUIAElement currentWindow = null;
    private List<ElementData> mapData = new ArrayList<>();
    private long lastMapWindowHandle = 0;
    private long lastMapTime = 0;
    private Point lockPoint = new Point(0, 0);

    private boolean showSpotlight = true;
    private boolean showWindow = true;
    private boolean showPath = true;
    private boolean showEngineering = true;
    private boolean showMap = true;
    
    private int depthOffset = 0; 

    public UIADesktopXRayV2() {
        super(WINDOW_TITLE);
        this.uia = new FastUIA();
        this.spotlightPanel = new SpotlightPanel();
        setUndecorated(true);
        setAlwaysOnTop(true);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        setBounds(gd.getDefaultConfiguration().getBounds());
        setBackground(new Color(0, 0, 0, 1));
        setLayout(new BorderLayout());
        add(spotlightPanel, BorderLayout.CENTER);
        setupHotkeys();
        SwingUtilities.invokeLater(() -> uia.setClickThrough(WINDOW_TITLE, true));
        startTrackingThread();
    }

    private void setupHotkeys() {
        try {
            String libPath = "C:/Users/andre/Documents/2026-04-28-Work-FastJava/FastHotkey/build/fasthotkey.dll";
            FastHotkey.loadLibrary(libPath);
            FastHotkey.register(1, 0, KeyCodes.VK_ESCAPE, id -> System.exit(0), HotkeyMode.AGGRESSIVE);
            FastHotkey.register(2, 0, KeyCodes.VK_1, id -> showSpotlight = !showSpotlight, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(3, 0, KeyCodes.VK_2, id -> showWindow = !showWindow, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(4, 0, KeyCodes.VK_3, id -> showPath = !showPath, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(9, 0, KeyCodes.VK_6, id -> showEngineering = !showEngineering, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(10, 0, KeyCodes.VK_7, id -> showMap = !showMap, HotkeyMode.AGGRESSIVE);
            
            // Interaction Hotkeys
            FastHotkey.register(5, 0, KeyCodes.VK_4, id -> { if(currentHover!=null) currentHover.invoke(); }, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(6, 0, KeyCodes.VK_5, id -> { if(currentHover!=null) currentHover.setValue("TEST"); }, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(11, 0, KeyCodes.VK_8, id -> { if(currentHover!=null) currentHover.scroll(0, 50); }, HotkeyMode.AGGRESSIVE);

            FastHotkey.register(7, 0, KeyCodes.VK_UP, id -> depthOffset++, HotkeyMode.AGGRESSIVE);
            FastHotkey.register(8, 0, KeyCodes.VK_DOWN, id -> depthOffset = Math.max(0, depthOffset - 1), HotkeyMode.AGGRESSIVE);
            FastHotkey.start();
        } catch (Exception e) {}
    }

    private void startTrackingThread() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    PointerInfo pi = MouseInfo.getPointerInfo();
                    if (pi != null) {
                        Point p = pi.getLocation();
                        if (p.distance(lockPoint) > 10 || currentHover == null) {
                            FastUIAElement hover = uia.getElementFromPoint(p.x, p.y);
                            if (hover != null && hover.isValid()) {
                                if (currentHover == null || currentHover.handle() != hover.handle()) {
                                    if (currentHover != null) currentHover.release();
                                    currentHover = hover; depthOffset = 0; lockPoint = p;
                                } else { hover.release(); }
                            }
                        }
                        if (currentHover != null && currentHover.isValid()) {
                            List<ElementData> path = new ArrayList<>();
                            FastUIAElement cur = currentHover;
                            FastUIAElement parent = cur.getParent();
                            FastUIAElement window = null;
                            while (parent != null) {
                                if (cur.getControlType() == ControlType.WINDOW) { window = cur; break; }
                                path.add(new ElementData(cur));
                                FastUIAElement next = parent.getParent();
                                if (cur != currentHover) cur.release();
                                cur = parent; parent = next;
                            }
                            if (window != null) {
                                if (currentWindow == null || currentWindow.handle() != window.handle()) {
                                    if (currentWindow != null) currentWindow.release();
                                    currentWindow = window;
                                } else { window.release(); }
                            }
                            if (showMap && currentWindow != null) {
                                long now = System.currentTimeMillis();
                                if (currentWindow.handle() != lastMapWindowHandle || (now - lastMapTime) > 2000) {
                                    List<ElementData> newMap = new ArrayList<>();
                                    scanChildrenRecursive(currentWindow, newMap, 0, 300);
                                    this.mapData = newMap; lastMapWindowHandle = currentWindow.handle(); lastMapTime = now;
                                }
                            } else { mapData.clear(); }
                            Snapshot s = new Snapshot();
                            if (showSpotlight && !path.isEmpty()) s.hover = path.get(Math.min(depthOffset, path.size()-1));
                            else if (showSpotlight && currentHover != null) s.hover = new ElementData(currentHover);
                            if (showWindow && currentWindow != null) s.window = new ElementData(currentWindow);
                            if (showPath) {
                                List<ElementData> drawPath = new ArrayList<>(path);
                                if (!drawPath.isEmpty() && depthOffset < drawPath.size()) drawPath.remove(depthOffset);
                                Collections.reverse(drawPath); s.path = drawPath;
                            }
                            if (showMap) s.map = new ArrayList<>(mapData);
                            s.showEng = showEngineering; s.showMap = showMap; s.showPath = showPath; s.showWindow = showWindow; s.showSpotlight = showSpotlight;
                            SwingUtilities.invokeLater(() -> spotlightPanel.update(s));
                        }
                    }
                    Thread.sleep(8); 
                } catch (Exception e) {}
            }
        });
        t.setDaemon(true); t.start();
    }

    private void scanChildrenRecursive(FastUIAElement root, List<ElementData> list, int depth, int max) {
        if (list.size() >= max || depth > 4) return;
        FastUIAElement child = root.getFirstChild();
        while (child != null) {
            list.add(new ElementData(child));
            scanChildrenRecursive(child, list, depth + 1, max);
            FastUIAElement next = child.getNextSibling();
            child.release(); child = next;
            if (list.size() >= max) break;
        }
    }

    private static class ElementData {
        Rect rect; String name, type, frameworkId, automationId, processName; int pid;
        boolean canInvoke, canValue, canScroll;
        ElementData(FastUIAElement el) {
            this.rect = el.getBoundingRect(); this.name = el.getName();
            this.type = el.getControlType().toString();
            this.canInvoke = el.supportsInvoke(); this.canValue = el.supportsValue(); this.canScroll = el.supportsScroll();
            this.frameworkId = el.getFrameworkId(); this.automationId = el.getAutomationId(); this.pid = el.getProcessId();
            Optional<ProcessHandle> ph = ProcessHandle.of(this.pid);
            this.processName = ph.map(h -> h.info().command().orElse("Unknown")).map(cmd -> {
                                    int lastIdx = cmd.lastIndexOf('\\');
                                    return (lastIdx != -1) ? cmd.substring(lastIdx + 1) : cmd;
                                }).orElse("Unknown");
        }
    }

    private static class Snapshot {
        ElementData hover, window;
        List<ElementData> path = new ArrayList<>(), map = new ArrayList<>();
        boolean showEng, showMap, showPath, showWindow, showSpotlight;
    }

    private class SpotlightPanel extends JPanel {
        private Snapshot lastSnapshot = null;
        private final Font fontWindow = new Font("Monospaced", Font.BOLD, 32);
        private final Font fontElement = new Font("Monospaced", Font.BOLD, 22);
        private final Font fontLegend = new Font("Monospaced", Font.BOLD, 14);
        private final BasicStroke strokeWindow = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 10}, 0);
        private final BasicStroke strokeHover = new BasicStroke(3.0f), strokeMap = new BasicStroke(1.5f), strokePath = new BasicStroke(1.0f);
        private final Color colorLabelBg = new Color(0, 0, 0, 220), colorMap = new Color(100, 100, 100, 80), colorPath = new Color(200, 200, 200, 80);
        private final Color colorHover = Color.WHITE, colorHoverFill = new Color(255, 255, 255, 15);

        public void update(Snapshot s) { this.lastSnapshot = s; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.Clear); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.SrcOver);
            if (lastSnapshot == null) return;
            if (lastSnapshot.showMap) drawMap(g2);
            if (lastSnapshot.showPath) drawPath(g2);
            if (lastSnapshot.showWindow && lastSnapshot.window != null) drawWindowBoundary(g2);
            if (lastSnapshot.showSpotlight && lastSnapshot.hover != null) drawElementSpotlight(g2);
            drawLegend(g2);
        }

        private void drawLegend(Graphics2D g2) {
            String[] items = { "ONE: SPOT", "TWO: WIN", "THREE: PATH", "SIX: ENG", "SEVEN: MAP" };
            g2.setFont(fontLegend); g2.setColor(colorLabelBg);
            int lx = getWidth() - 150, ly = 20;
            g2.fillRect(lx, ly, 130, items.length * 20 + 20);
            g2.setColor(Color.WHITE);
            for (int i = 0; i < items.length; i++) g2.drawString(items[i], lx + 10, ly + 25 + (i * 20));
        }

        private void drawMap(Graphics2D g2) {
            g2.setStroke(strokeMap); g2.setColor(colorMap);
            for (ElementData ed : lastSnapshot.map) if (ed.rect != null) g2.drawRect(ed.rect.x(), ed.rect.y(), ed.rect.width(), ed.rect.height());
        }

        private void drawPath(Graphics2D g2) {
            g2.setStroke(strokePath); g2.setColor(colorPath);
            for (ElementData ed : lastSnapshot.path) if (ed.rect != null) g2.drawRect(ed.rect.x(), ed.rect.y(), ed.rect.width(), ed.rect.height());
        }

        private void drawWindowBoundary(Graphics2D g2) {
            Rect r = lastSnapshot.window.rect; if (r == null) return;
            g2.setColor(new Color(255, 255, 255, 100)); g2.setStroke(strokeWindow); g2.drawRect(r.x(), r.y(), r.width(), r.height());
            g2.setFont(fontWindow); String label = lastSnapshot.window.name;
            if (label != null && !label.isEmpty()) {
                int tw = g2.getFontMetrics().stringWidth(label);
                g2.setColor(colorLabelBg); g2.fillRect(r.x(), r.y() - 40, tw + 15, 40);
                g2.setColor(Color.WHITE); g2.drawString(label, r.x() + 7, r.y() - 10);
            }
        }

        private void drawElementSpotlight(Graphics2D g2) {
            Rect r = lastSnapshot.hover.rect; if (r == null) return;
            g2.setColor(colorHoverFill); g2.fillRect(r.x(), r.y(), r.width(), r.height());
            g2.setColor(colorHover); g2.setStroke(strokeHover); g2.drawRect(r.x(), r.y(), r.width(), r.height());
            
            g2.setFont(fontElement);
            String name = (lastSnapshot.hover.name != null && !lastSnapshot.hover.name.isEmpty()) ? lastSnapshot.hover.name : lastSnapshot.hover.type;
            
            // Build Functional Report
            StringBuilder functions = new StringBuilder(" | TYPE: ").append(lastSnapshot.hover.type);
            if (lastSnapshot.hover.canInvoke) functions.append(" | FOUR: PRESS");
            if (lastSnapshot.hover.canValue)  functions.append(" | FIVE: EDIT");
            if (lastSnapshot.hover.canScroll) functions.append(" | EIGHT: SCROLL");
            
            String fullLabel = name + functions.toString();
            int tw = g2.getFontMetrics().stringWidth(fullLabel);
            g2.setColor(colorLabelBg); g2.fillRect(r.x(), r.y() - 35, tw + 15, 35);
            g2.setColor(Color.WHITE); g2.drawString(fullLabel, r.x() + 7, r.y() - 10);
            
            if (lastSnapshot.showEng) drawEngInfo(g2, lastSnapshot.hover, r.x(), r.y() + r.height() + 5);
        }

        private void drawEngInfo(Graphics2D g2, ElementData ed, int x, int y) {
            String[] lines = { "FRAMEWORK: " + ed.frameworkId.toUpperCase(), "PROCESS: " + ed.processName.toUpperCase() + " [" + ed.pid + "]", "AUTO_ID: " + (ed.automationId.isEmpty() ? "NONE" : ed.automationId) };
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12)); g2.setColor(colorLabelBg); g2.fillRect(x, y, 320, lines.length * 18 + 10);
            g2.setColor(new Color(200,200,200)); for (int i = 0; i < lines.length; i++) g2.drawString(lines[i], x + 10, y + 20 + (i * 18));
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> new UIADesktopXRayV2().setVisible(true));
    }
}
