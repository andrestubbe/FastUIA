package fastuia;

import fastuia.*;

/**
 * FastUIA Context Explorer — Native Context Explorer Demo.
 * Showcases real-time focus tracking and interaction with UI elements.
 */
public class ContextDemo {

    public static void main(String[] args) throws Exception {

        FastUIA uia = new FastUIA();
        FastUIAElement last = null;

        System.out.println("==================================================");
        System.out.println("   FastUIA Context Explorer — Running...        ");
        System.out.println("   (Focus on any window to see its structure)   ");
        System.out.println("==================================================\n");

        while (true) {

            FastUIAElement el = uia.getFocusedElement();
            if (el == null || !el.isValid()) {
                Thread.sleep(200);
                continue;
            }

            // Display info only when focus changes
            if (last == null || el.handle() != last.handle()) {

                System.out.println("--------------------------------------------------");

                // 1. Identity
                System.out.println("Element: " + el.getName());
                System.out.println("Typ:     " + el.getControlType());

                // 2. Geometry
                Rect r = el.getBoundingRect();
                if (r != null) {
                    System.out.println("Rect:    " + r.x() + "," + r.y() +
                                       "  [" + r.width() + "x" + r.height() + "]");
                }

                // 3. Capabilities
                System.out.println("Supports:");
                System.out.println("  Value:          " + el.supportsValue());
                System.out.println("  Text:           " + el.supportsText());
                System.out.println("  Invoke:         " + el.supportsInvoke());
                System.out.println("  ExpandCollapse: " + el.supportsExpandCollapse());
                System.out.println("  Scroll:         " + el.supportsScroll());
                System.out.println("  isTextField:    " + el.isTextField());

                // 4. Action (Demo: Interact with text fields)
                if (el.supportsValue()) {
                    String current = el.getValue();
                    if (current != null && !current.contains("[FastUIA]")) {
                        String updated = current + " [FastUIA]";
                        el.setValue(updated);
                        System.out.println("Action:  Text updated in native element.");
                    }
                }

                if (el.supportsInvoke()) {
                    System.out.println("Action:  (Button could be invoked via el.invoke())");
                }

                last = el;
            }

            Thread.sleep(150);
        }
    }
}
