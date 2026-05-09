package fastuia;

import fastuia.FastUIA;

/**
 * Basic Hello World Demo for FastUIA.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastUIA Demo ===");
        
        FastUIA uia = new FastUIA();
        
        System.out.println("Getting focused element...");
        long focusedElement = uia.GetFocusedElement();
        
        if (focusedElement != 0) {
            String name = uia.GetName(focusedElement);
            String controlType = uia.GetControlType(focusedElement);
            
            System.out.println("Focused element name: " + name);
            System.out.println("Control type: " + controlType);
            
            int[] rect = uia.GetBoundingRect(focusedElement);
            if (rect != null) {
                System.out.println("Bounding rect: x=" + rect[0] + ", y=" + rect[1] + ", w=" + rect[2] + ", h=" + rect[3]);
            }
        } else {
            System.out.println("No focused element found.");
        }
        
        System.out.println("=== Demo Complete ===");
    }
}
