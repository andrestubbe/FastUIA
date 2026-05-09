package fastuia;

import fastuia.*;

/**
 * FastUIA Context Explorer — Native Context Explorer Demo.
 * Showcases real-time focus tracking and interaction with UI elements.
 */
public class ContextDemo {

    public static void main(String[] args) throws Exception {

        System.out.println("[DEBUG] Initializing FastUIA...");
        FastUIA uia = new FastUIA();
        FastUIAElement last = null;

        System.out.println("==================================================");
        System.out.println("   FastUIA Context Explorer — Running...        ");
        System.out.println("   (Focus on any window to see its structure)   ");
        System.out.println("==================================================\n");

        while (true) {
            FastUIAElement el = uia.getFocusedElement();
            
            if (el == null) {
                // Silent wait if nothing is focused (e.g. desktop transition)
                Thread.sleep(200);
                continue;
            }

            if (!el.isValid()) {
                System.out.println("[DEBUG] Focused element handle " + el.handle() + " is invalid.");
                Thread.sleep(200);
                continue;
            }

            // Display info only when focus changes
            if (last == null || el.handle() != last.handle()) {

                System.out.println("\n[EVENT] Focus Changed -> Handle: " + el.handle());
                System.out.println("--------------------------------------------------");

                // 1. Identity
                String name = el.getName();
                ControlType type = el.getControlType();
                System.out.println("[DATA] Name: " + (name != null ? "\"" + name + "\"" : "<null>"));
                System.out.println("[DATA] Type: " + type);

                // 2. Geometry
                Rect r = el.getBoundingRect();
                if (r != null) {
                    System.out.println("[DATA] Rect: " + r.x() + "," + r.y() +
                                       " [" + r.width() + "x" + r.height() + "]");
                } else {
                    System.out.println("[DATA] Rect: <null>");
                }

                // 3. Capabilities
                System.out.println("[CHECK] Pattern Support:");
                System.out.println("  - Value:          " + el.supportsValue());
                System.out.println("  - Text:           " + el.supportsText());
                System.out.println("  - Invoke:         " + el.supportsInvoke());
                System.out.println("  - ExpandCollapse: " + el.supportsExpandCollapse());
                System.out.println("  - Scroll:         " + el.supportsScroll());
                System.out.println("  - isTextField:    " + el.isTextField());

                // 4. Action (Demo: Interact with text fields)
                if (el.supportsValue()) {
                    String current = el.getValue();
                    System.out.println("[DATA] Current Value: " + (current != null ? "\"" + current + "\"" : "<null>"));
                    
                    if (current != null && !current.contains("[FastUIA]")) {
                        String updated = current + " [FastUIA]";
                        System.out.println("[ACTION] Setting new value: \"" + updated + "\"");
                        el.setValue(updated);
                        System.out.println("[SUCCESS] Value updated.");
                    }
                }

                if (el.supportsInvoke()) {
                    System.out.println("[INFO] Element is invokable (Button/Link).");
                }

                last = el;
            }

            Thread.sleep(150);
        }
    }
}
