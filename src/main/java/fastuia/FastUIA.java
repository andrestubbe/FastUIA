package fastuia;

import fastcore.FastCore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FastUIA Main API Class.
 * Native Windows UI Automation capabilities exposed via JNI.
 */
public final class FastUIA {

    static {
        FastCore.loadLibrary("fastuia");
    }

    private final List<FocusChangedListener> focusListeners = new CopyOnWriteArrayList<>();
    private final List<TextChangedListener> textListeners = new CopyOnWriteArrayList<>();
    private final List<StructureChangedListener> structureListeners = new CopyOnWriteArrayList<>();

    // --- Core ---

    public FastUIAElement getFocusedElement() {
        long handle = nativeGetFocusedElement();
        return handle != 0 ? new FastUIAElement(handle, this) : null;
    }

    public FastUIAElement fromHandle(long handle) {
        return handle != 0 ? new FastUIAElement(handle, this) : null;
    }

    public void release(long handle) {
        if (handle != 0) nativeRelease(handle);
    }

    // --- Events ---

    public void addFocusChangedListener(FocusChangedListener l) {
        if (focusListeners.isEmpty()) nativeRegisterFocusChanged();
        focusListeners.add(l);
    }

    public void removeFocusChangedListener(FocusChangedListener l) {
        focusListeners.remove(l);
    }

    public void addTextChangedListener(TextChangedListener l) {
        if (textListeners.isEmpty()) nativeRegisterTextChanged();
        textListeners.add(l);
    }

    public void removeTextChangedListener(TextChangedListener l) {
        textListeners.remove(l);
    }

    public void addStructureChangedListener(StructureChangedListener l) {
        if (structureListeners.isEmpty()) nativeRegisterStructureChanged();
        structureListeners.add(l);
    }

    public void removeStructureChangedListener(StructureChangedListener l) {
        structureListeners.remove(l);
    }

    // Internal callbacks from native event thread
    void notifyFocusChanged(long handle) {
        FastUIAElement el = fromHandle(handle);
        if (el != null) {
            for (FocusChangedListener l : focusListeners) {
                l.onFocusChanged(el);
            }
        }
    }

    void notifyTextChanged(long handle, String newText) {
        FastUIAElement el = fromHandle(handle);
        if (el != null) {
            for (TextChangedListener l : textListeners) {
                l.onTextChanged(el, newText);
            }
        }
    }

    void notifyStructureChanged(long handle, int changeType) {
        FastUIAElement el = fromHandle(handle);
        if (el != null) {
            for (StructureChangedListener l : structureListeners) {
                l.onStructureChanged(el, changeType);
            }
        }
    }

    // --- Native Methods (matching requested JNI signatures) ---

    // Core
    private native long nativeGetFocusedElement();
    private native boolean nativeIsValid(long handle);
    private native void nativeRelease(long handle);

    // Properties
    private native String nativeGetName(long handle);
    private native int nativeGetControlType(long handle);
    private native int[] nativeGetBoundingRect(long handle);

    // ValuePattern
    private native boolean nativeSupportsValue(long handle);
    private native String nativeGetValue(long handle);
    private native void nativeSetValue(long handle, String value);

    // TextPattern
    private native boolean nativeSupportsText(long handle);
    private native String nativeGetSelection(long handle);
    private native void nativeSetSelection(long handle, String text);

    // InvokePattern
    private native boolean nativeSupportsInvoke(long handle);
    private native void nativeInvoke(long handle);

    // ExpandCollapsePattern
    private native boolean nativeSupportsExpandCollapse(long handle);
    private native void nativeExpand(long handle);
    private native void nativeCollapse(long handle);

    // ScrollPattern
    private native boolean nativeSupportsScroll(long handle);
    private native void nativeScroll(long handle, double h, double v);

    private native boolean nativeSupportsSelection(long handle);
    private native boolean nativeSupportsWindow(long handle);
    private native boolean nativeSupportsLegacyIAccessible(long handle);
    private native boolean nativeSupportsToggle(long handle);
    private native boolean nativeSupportsRangeValue(long handle);
    private native boolean nativeSupportsGrid(long handle);
    private native boolean nativeSupportsGridItem(long handle);
    private native boolean nativeSupportsSelectionItem(long handle);

    // Tree Navigation
    private native long nativeGetParent(long handle);
    private native long nativeGetFirstChild(long handle);
    private native long nativeGetNextSibling(long handle);
    private native long nativeGetPreviousSibling(long handle);

    // Events
    private native void nativeRegisterFocusChanged();
    private native void nativeRegisterTextChanged();
    private native void nativeRegisterStructureChanged();

    // --- Internal Helpers for FastUIAElement ---

    boolean isValid(long handle) { return nativeIsValid(handle); }
    String getName(long handle) { return nativeGetName(handle); }
    ControlType getControlType(long handle) { return ControlType.fromUiaId(nativeGetControlType(handle)); }
    Rect getBoundingRect(long handle) {
        int[] r = nativeGetBoundingRect(handle);
        return (r != null && r.length == 4) ? new Rect(r[0], r[1], r[2], r[3]) : null;
    }

    boolean supportsValue(long handle) { return nativeSupportsValue(handle); }
    String getValue(long handle) { return nativeGetValue(handle); }
    void setValue(long handle, String value) { nativeSetValue(handle, value); }

    boolean supportsText(long handle) { return nativeSupportsText(handle); }
    String getSelection(long handle) { return nativeGetSelection(handle); }
    void setSelection(long handle, String text) { nativeSetSelection(handle, text); }

    boolean supportsInvoke(long handle) { return nativeSupportsInvoke(handle); }
    void invoke(long handle) { nativeInvoke(handle); }

    boolean supportsExpandCollapse(long handle) { return nativeSupportsExpandCollapse(handle); }
    void expand(long handle) { nativeExpand(handle); }
    void collapse(long handle) { nativeCollapse(handle); }

    boolean supportsScroll(long handle) { return nativeSupportsScroll(handle); }
    void scroll(long handle, double h, double v) { nativeScroll(handle, h, v); }

    boolean supportsSelection(long handle) { return nativeSupportsSelection(handle); }
    boolean supportsWindow(long handle) { return nativeSupportsWindow(handle); }
    boolean supportsLegacyIAccessible(long handle) { return nativeSupportsLegacyIAccessible(handle); }
    boolean supportsToggle(long handle) { return nativeSupportsToggle(handle); }
    boolean supportsRangeValue(long handle) { return nativeSupportsRangeValue(handle); }
    boolean supportsGrid(long handle) { return nativeSupportsGrid(handle); }
    boolean supportsGridItem(long handle) { return nativeSupportsGridItem(handle); }
    boolean supportsSelectionItem(long handle) { return nativeSupportsSelectionItem(handle); }

    long getParent(long handle) { return nativeGetParent(handle); }
    long getFirstChild(long handle) { return nativeGetFirstChild(handle); }
    long getNextSibling(long handle) { return nativeGetNextSibling(handle); }
    long getPreviousSibling(long handle) { return nativeGetPreviousSibling(handle); }
}
