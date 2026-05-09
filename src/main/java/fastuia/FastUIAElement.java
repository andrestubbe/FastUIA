package fastuia;

/**
 * Wrapper around a native UIA element handle.
 * Provides type-safe, object-oriented access to UI Automation elements.
 */
public class FastUIAElement {

    final long handle;
    final FastUIA api;

    FastUIAElement(long handle, FastUIA api) {
        this.handle = handle;
        this.api = api;
    }

    /**
     * Check if this element handle is still valid.
     */
    public boolean isValid() {
        return api.IsValid(handle);
    }

    /**
     * Get the control type of this element.
     */
    public ControlType getControlType() {
        return api.GetControlTypeAsEnum(handle);
    }

    /**
     * Get the bounding rectangle of this element.
     * @return [x, y, width, height] or null
     */
    public int[] getBoundingRect() {
        return api.GetBoundingRect(handle);
    }

    /**
     * Get the name of this element.
     */
    public String getName() {
        return api.GetName(handle);
    }

    /**
     * Get the value of this element.
     */
    public String getValue() {
        return api.GetValue(handle);
    }

    /**
     * Set the value of this element.
     */
    public void setValue(String value) {
        api.SetValue(handle, value);
    }

    /**
     * Get the selection of this element.
     */
    public String getSelection() {
        return api.GetSelection(handle);
    }

    /**
     * Set the selection of this element.
     */
    public void setSelection(String selection) {
        api.SetSelection(handle, selection);
    }

    /**
     * Invoke the default action of this element.
     */
    public void invoke() {
        api.Invoke(handle);
    }

    /**
     * Expand this element.
     */
    public void expand() {
        api.Expand(handle);
    }

    /**
     * Collapse this element.
     */
    public void collapse() {
        api.Collapse(handle);
    }

    /**
     * Scroll this element.
     */
    public void scroll(double horizontalPercent, double verticalPercent) {
        api.Scroll(handle, horizontalPercent, verticalPercent);
    }

    /**
     * Get the parent element.
     */
    public FastUIAElement getParent() {
        long parent = api.GetParent(handle);
        return parent != 0 ? new FastUIAElement(parent, api) : null;
    }

    /**
     * Get the first child element.
     */
    public FastUIAElement getFirstChild() {
        long child = api.GetFirstChild(handle);
        return child != 0 ? new FastUIAElement(child, api) : null;
    }

    /**
     * Get the next sibling element.
     */
    public FastUIAElement getNextSibling() {
        long sibling = api.GetNextSibling(handle);
        return sibling != 0 ? new FastUIAElement(sibling, api) : null;
    }

    /**
     * Get the previous sibling element.
     */
    public FastUIAElement getPreviousSibling() {
        long sibling = api.GetPreviousSibling(handle);
        return sibling != 0 ? new FastUIAElement(sibling, api) : null;
    }

    // Pattern support checks

    public boolean supportsValue() {
        return api.SupportsValue(handle);
    }

    public boolean supportsInvoke() {
        return api.SupportsInvoke(handle);
    }

    public boolean supportsExpandCollapse() {
        return api.SupportsExpandCollapse(handle);
    }

    public boolean supportsScroll() {
        return api.SupportsScroll(handle);
    }

    public boolean supportsSelection() {
        return api.SupportsSelection(handle);
    }

    @Override
    public String toString() {
        String name = getName();
        ControlType type = getControlType();
        return String.format("FastUIAElement[handle=%d, type=%s, name=%s]", handle, type, name);
    }
}
