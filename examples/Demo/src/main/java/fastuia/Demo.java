package fastuia;

/**
 * FastUIA Demo — showcases the object-oriented API.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastUIA Demo ===");
        
        FastUIA uia = new FastUIA();
        
        // Use the typed wrapper API
        FastUIAElement el = uia.getFocusedElement();
        
        if (el != null && el.isValid()) {
            System.out.println("Focused: " + el);
            System.out.println("Type:    " + el.getControlType());
            System.out.println("Name:    " + el.getName());
            System.out.println("Value:   " + el.getValue());
            
            int[] rect = el.getBoundingRect();
            if (rect != null) {
                System.out.printf("Rect:    x=%d, y=%d, w=%d, h=%d%n", rect[0], rect[1], rect[2], rect[3]);
            }
            
            // Pattern support checks
            System.out.println("Supports:");
            System.out.println("  Value:   " + el.supportsValue());
            System.out.println("  Invoke:  " + el.supportsInvoke());
            System.out.println("  Expand:  " + el.supportsExpandCollapse());
            System.out.println("  Scroll:  " + el.supportsScroll());
            System.out.println("  Select:  " + el.supportsSelection());
            
            // Traverse
            FastUIAElement parent = el.getParent();
            if (parent != null) {
                System.out.println("Parent:  " + parent.getName());
            }
        } else {
            System.out.println("No focused element found.");
        }
        
        System.out.println("=== Demo Complete ===");
    }
}
