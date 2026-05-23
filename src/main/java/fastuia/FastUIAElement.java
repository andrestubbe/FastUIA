package fastuia;

/**
 * Wrapper around a native UIA element handle.
 * Provides type-safe, object-oriented access to UI Automation elements.
 */
public final class FastUIAElement {

    private long handle;
    private final FastUIA api;

    FastUIAElement(long handle, FastUIA api) {
        this.handle = handle;
        this.api = api;
    }

    public void release() {
        if (handle != 0) {
            api.release(handle);
            handle = 0;
        }
    }

    public long handle() {
        return handle;
    }

    /**
     * Check if this element handle is still valid.
     */
    public boolean isValid() {
        return api.isValid(handle);
    }

    /**
     * Get the control type of this element.
     */
    public ControlType getControlType() {
        return api.getControlType(handle);
    }

    /**
     * Get the bounding rectangle of this element.
     */
    public Rect getBoundingRect() {
        return api.getBoundingRect(handle);
    }

    /**
     * Get the name of this element.
     */
    public String getName() {
        return api.getName(handle);
    }

    /**
     * Get the value of this element.
     */
    public String getValue() {
        return api.getValue(handle);
    }

    /**
     * Set the value of this element.
     */
    public void setValue(String value) {
        api.setValue(handle, value);
    }

    /**
     * Get the selection of this element.
     */
    public String getSelection() {
        return api.getSelection(handle);
    }

    /**
     * Set the selection of this element.
     */
    public void setSelection(String selection) {
        api.setSelection(handle, selection);
    }

    /**
     * Invoke the default action of this element.
     */
    public void invoke() {
        api.invoke(handle);
    }

    /**
     * Expand this element.
     */
    public void expand() {
        api.expand(handle);
    }

    /**
     * Collapse this element.
     */
    public void collapse() {
        api.collapse(handle);
    }

    /**
     * Scroll this element.
     */
    public void scroll(double horizontalPercent, double verticalPercent) {
        api.scroll(handle, horizontalPercent, verticalPercent);
    }

    /**
     * Get the parent element.
     */
    public FastUIAElement getParent() {
        long parent = api.getParent(handle);
        return parent != 0 ? new FastUIAElement(parent, api) : null;
    }

    /**
     * Get the first child element.
     */
    public FastUIAElement getFirstChild() {
        long child = api.getFirstChild(handle);
        return child != 0 ? new FastUIAElement(child, api) : null;
    }

    /**
     * Get the next sibling element.
     */
    public FastUIAElement getNextSibling() {
        long sibling = api.getNextSibling(handle);
        return sibling != 0 ? new FastUIAElement(sibling, api) : null;
    }

    /**
     * Get the previous sibling element.
     */
    public FastUIAElement getPreviousSibling() {
        long sibling = api.getPreviousSibling(handle);
        return sibling != 0 ? new FastUIAElement(sibling, api) : null;
    }

    public String getFrameworkId() {
        return api.getFrameworkId(handle);
    }

    public String getAutomationId() {
        return api.getAutomationId(handle);
    }

    public int getProcessId() {
        return api.getProcessId(handle);
    }

    // Pattern support checks

    public boolean supportsValue() {
        return api.supportsValue(handle);
    }

    public boolean supportsInvoke() {
        return api.supportsInvoke(handle);
    }

    public boolean supportsExpandCollapse() {
        return api.supportsExpandCollapse(handle);
    }

    public boolean supportsScroll() {
        return api.supportsScroll(handle);
    }

    public boolean supportsSelection() {
        return api.supportsSelection(handle);
    }

    public boolean supportsText() {
        return api.supportsText(handle);
    }

    public boolean supportsWindow() {
        return api.supportsWindow(handle);
    }

    public boolean supportsLegacyIAccessible() {
        return api.supportsLegacyIAccessible(handle);
    }

    public boolean supportsToggle() {
        return api.supportsToggle(handle);
    }

    public boolean supportsRangeValue() {
        return api.supportsRangeValue(handle);
    }

    public boolean supportsGrid() {
        return api.supportsGrid(handle);
    }

    public boolean supportsGridItem() {
        return api.supportsGridItem(handle);
    }

    public boolean supportsSelectionItem() {
        return api.supportsSelectionItem(handle);
    }

    /**
     * Helper for FastOverlay to identify text-entry fields.
     */
    public boolean isTextField() {
        return supportsText() || supportsValue();
    }

    @Override
    public String toString() {
        String name = getName();
        ControlType type = getControlType();
        return String.format("FastUIAElement[handle=%d, type=%s, name=%s]", handle, type, name);
    }
}
