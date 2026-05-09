package fastuia;

import fastcore.FastCore;

/**
 * FastUIA Main API Class.
 * Native Windows UI Automation capabilities exposed via JNI.
 */
public class FastUIA {

    // Load the native library once upon class initialization
    static {
        FastCore.loadLibrary("fastuia");
    }

    /**
     * Get the currently focused UI element.
     * @return Handle to the focused element
     */
    public native long GetFocusedElement();

    /**
     * Get the control type of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Control type as string
     */
    public native String GetControlType(long elementHandle);

    /**
     * Get the bounding rectangle of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Bounding rectangle as [x, y, width, height]
     */
    public native int[] GetBoundingRect(long elementHandle);

    /**
     * Get the name of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Name of the element
     */
    public native String GetName(long elementHandle);

    /**
     * Get the value of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Value of the element
     */
    public native String GetValue(long elementHandle);

    /**
     * Set the value of a UI element.
     * @param elementHandle Handle to the UI element
     * @param value Value to set
     */
    public native void SetValue(long elementHandle, String value);

    /**
     * Get the selection of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Selection as string
     */
    public native String GetSelection(long elementHandle);

    /**
     * Set the selection of a UI element.
     * @param elementHandle Handle to the UI element
     * @param selection Selection to set
     */
    public native void SetSelection(long elementHandle, String selection);

    /**
     * Invoke the default action of a UI element.
     * @param elementHandle Handle to the UI element
     */
    public native void Invoke(long elementHandle);

    /**
     * Expand a UI element.
     * @param elementHandle Handle to the UI element
     */
    public native void Expand(long elementHandle);

    /**
     * Collapse a UI element.
     * @param elementHandle Handle to the UI element
     */
    public native void Collapse(long elementHandle);

    /**
     * Scroll a UI element.
     * @param elementHandle Handle to the UI element
     * @param horizontalPercent Horizontal scroll percentage (0-100)
     * @param verticalPercent Vertical scroll percentage (0-100)
     */
    public native void Scroll(long elementHandle, double horizontalPercent, double verticalPercent);

    /**
     * Get the parent element of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Handle to the parent element
     */
    public native long GetParent(long elementHandle);

    /**
     * Get the first child element of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Handle to the first child element
     */
    public native long GetFirstChild(long elementHandle);

    /**
     * Get the next sibling element of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Handle to the next sibling element
     */
    public native long GetNextSibling(long elementHandle);

    /**
     * Get the previous sibling element of a UI element.
     * @param elementHandle Handle to the UI element
     * @return Handle to the previous sibling element
     */
    public native long GetPreviousSibling(long elementHandle);

}
