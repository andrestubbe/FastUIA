package fastuia;

import fastcore.FastCore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FastUIA Main API Class.
 * Native Windows UI Automation capabilities exposed via JNI.
 */
public final class FastUIA {

    // Load the native library once upon class initialization
    static {
        FastCore.loadLibrary("fastuia");
    }

    private final List<FocusChangedListener> focusListeners = new CopyOnWriteArrayList<>();
    private final List<TextChangedListener> textListeners = new CopyOnWriteArrayList<>();
    private final List<StructureChangedListener> structureListeners = new CopyOnWriteArrayList<>();

    // --- Core ---

    /**
     * Get the currently focused UI element as a typed wrapper.
     */
    public FastUIAElement getFocusedElement() {
        long handle = GetFocusedElement();
        return handle != 0 ? new FastUIAElement(handle, this) : null;
    }

    /**
     * Wrap an existing native handle into a typed element.
     */
    public FastUIAElement fromHandle(long handle) {
        return handle != 0 ? new FastUIAElement(handle, this) : null;
    }

    // --- Events ---

    public void addFocusChangedListener(FocusChangedListener l) {
        focusListeners.add(l);
    }

    public void removeFocusChangedListener(FocusChangedListener l) {
        focusListeners.remove(l);
    }

    public void addTextChangedListener(TextChangedListener l) {
        textListeners.add(l);
    }

    public void removeTextChangedListener(TextChangedListener l) {
        textListeners.remove(l);
    }

    public void addStructureChangedListener(StructureChangedListener l) {
        structureListeners.add(l);
    }

    public void removeStructureChangedListener(StructureChangedListener l) {
        structureListeners.remove(l);
    }

    // Internal callback from native event thread
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

    // --- Native Methods ---

    public native long GetFocusedElement();
    public native int GetControlType(long elementHandle);

    public ControlType GetControlTypeAsEnum(long elementHandle) {
        return ControlType.fromUiaId(GetControlType(elementHandle));
    }

    public native String GetName(long elementHandle);
    public native String GetValue(long elementHandle);
    public native void SetValue(long elementHandle, String value);
    public native String GetSelection(long elementHandle);
    public native void SetSelection(long elementHandle, String selection);
    public native void Invoke(long elementHandle);
    public native void Expand(long elementHandle);
    public native void Collapse(long elementHandle);
    public native void Scroll(long elementHandle, double horizontalPercent, double verticalPercent);
    public native long GetParent(long elementHandle);
    public native long GetFirstChild(long elementHandle);
    public native long GetNextSibling(long elementHandle);
    public native long GetPreviousSibling(long elementHandle);

    // Geometry
    public native int[] GetBoundingRectRaw(long elementHandle);

    public Rect GetBoundingRect(long elementHandle) {
        int[] raw = GetBoundingRectRaw(elementHandle);
        if (raw != null && raw.length == 4) {
            return new Rect(raw[0], raw[1], raw[2], raw[3]);
        }
        return null;
    }

    // Validation
    public native boolean IsValid(long elementHandle);

    // Pattern support checks
    public native boolean SupportsValue(long elementHandle);
    public native boolean SupportsInvoke(long elementHandle);
    public native boolean SupportsExpandCollapse(long elementHandle);
    public native boolean SupportsScroll(long elementHandle);
    public native boolean SupportsSelection(long elementHandle);
    public native boolean SupportsText(long elementHandle);
    public native boolean SupportsWindow(long elementHandle);
    public native boolean SupportsLegacyIAccessible(long elementHandle);
    public native boolean SupportsToggle(long elementHandle);
    public native boolean SupportsRangeValue(long elementHandle);

    // Event registration (native hooks)
    public native void StartFocusTracking();
    public native void StopFocusTracking();

}
