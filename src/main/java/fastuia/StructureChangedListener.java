package fastuia;

/**
 * Listener for structural changes in the UI Automation tree
 * (elements added, removed, reordered).
 */
@FunctionalInterface
public interface StructureChangedListener {
    void onStructureChanged(FastUIAElement element, int changeType);
}
