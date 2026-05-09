package fastuia;

/**
 * Listener for focus change events in the UI Automation tree.
 */
@FunctionalInterface
public interface FocusChangedListener {
    void onFocusChanged(FastUIAElement element);
}
