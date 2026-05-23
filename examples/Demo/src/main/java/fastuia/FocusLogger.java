package fastuia;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FocusLogger — VERSION 2.0 (EVENT-DRIVEN)
 * This version uses NATIVE EVENTS and has a DEDUPLICATION filter.
 */
public class FocusLogger {

    private static String lastOutput = "";

    public static void main(String[] args) {
        FastUIA uia = new FastUIA();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        System.out.println("==================================================");
        System.out.println("   FastUIA Focus Logger — EVENT-DRIVEN MODE v2    ");
        System.out.println("   (Reagiert sofort auf Windows-Events)           ");
        System.out.println("==================================================\n");

        uia.addFocusChangedListener(el -> {
            try {
                if (el == null || !el.isValid()) return;

                String name = el.getName();
                String type = el.getControlType().toString();
                
                // Find Program Name (Parent Window)
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

                String currentOutput = program + "|" + name + "|" + type;
                
                // DEDUPLICATION: Only print if something actually changed
                if (!currentOutput.equals(lastOutput)) {
                    String timestamp = LocalTime.now().format(timeFormatter);
                    System.out.printf("[%s] PROGRAM: %-20s | ELEMENT: %-20s | TYPE: %s\n", 
                                      timestamp, program.toUpperCase(), 
                                      (name == null || name.isEmpty() ? "<NONE>" : name.toUpperCase()), 
                                      type);
                    lastOutput = currentOutput;
                }
                
                el.release();
                
            } catch (Exception e) {
                // Silently handle callback errors
            }
        });

        // Keep main thread alive
        while (true) {
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
    }
}
