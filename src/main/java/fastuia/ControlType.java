package fastuia;

/**
 * UI Automation Control Types.
 * Minimal set for FastUIA as requested.
 */
public enum ControlType {
    BUTTON(50000),
    EDIT(50004),
    DOCUMENT(50030),
    LIST(50008),
    LIST_ITEM(50007),
    WINDOW(50032),
    MENU_ITEM(50010),
    TREE(50023),
    TREE_ITEM(50024),
    CHECKBOX(50002),
    RADIO_BUTTON(50012),
    COMBO_BOX(50003),
    TAB(50018),
    TAB_ITEM(50019),
    UNKNOWN(0);

    private final int uiaId;

    ControlType(int uiaId) {
        this.uiaId = uiaId;
    }

    public int getUiaId() {
        return uiaId;
    }

    public static ControlType fromUiaId(int uiaId) {
        for (ControlType type : values()) {
            if (type.uiaId == uiaId) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
