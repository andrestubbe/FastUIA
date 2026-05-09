package fastuia;

/**
 * Listener for text change events on UI Automation elements.
 */
@FunctionalInterface
public interface TextChangedListener {
    void onTextChanged(FastUIAElement element, String newText);
}
