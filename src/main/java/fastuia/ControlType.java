package fastuia;

/**
 * UI Automation Control Types.
 * Maps UIA_CONTROLTYPE_ID values to a typed enum.
 */
public enum ControlType {
    UNKNOWN(0),
    BUTTON(50000),
    CALENDAR(50001),
    CHECK_BOX(50002),
    COMBO_BOX(50003),
    EDIT(50004),
    HYPERLINK(50005),
    IMAGE(50006),
    LIST_ITEM(50007),
    LIST(50008),
    MENU(50009),
    MENU_ITEM(50010),
    PROGRESS_BAR(50011),
    RADIO_BUTTON(50012),
    SCROLL_BAR(50013),
    SLIDER(50015),
    SPINNER(50016),
    STATUS_BAR(50017),
    TAB(50018),
    TAB_ITEM(50019),
    TEXT(50020),
    TOOL_BAR(50021),
    TOOL_TIP(50022),
    TREE(50023),
    TREE_ITEM(50024),
    CUSTOM(50025),
    GROUP(50026),
    THUMB(50027),
    DATA_GRID(50028),
    DATA_ITEM(50029),
    DOCUMENT(50030),
    SPLIT_BUTTON(50031),
    WINDOW(50032),
    PANE(50033),
    HEADER(50034),
    HEADER_ITEM(50035),
    TABLE(50036),
    TITLE_BAR(50037),
    SEPARATOR(50038),
    SEMANTIC_ZOOM(50039),
    APP_BAR(50040);

    private final int uiaId;

    ControlType(int uiaId) {
        this.uiaId = uiaId;
    }

    public int getUiaId() {
        return uiaId;
    }

    /**
     * Resolve a UIA_CONTROLTYPE_ID to the corresponding enum value.
     * @param uiaId The raw UIA control type id
     * @return The matching ControlType, or UNKNOWN
     */
    public static ControlType fromUiaId(int uiaId) {
        for (ControlType type : values()) {
            if (type.uiaId == uiaId) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
