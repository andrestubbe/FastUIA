package fastuia;

/**
 * Immutable bounding rectangle for UI Automation elements.
 */
public record Rect(int x, int y, int width, int height) {

    /**
     * Create from raw left/top/right/bottom values.
     */
    public static Rect fromLTRB(int left, int top, int right, int bottom) {
        return new Rect(left, top, right - left, bottom - top);
    }

    public int left()   { return x; }
    public int top()    { return y; }
    public int right()  { return x + width; }
    public int bottom() { return y + height; }

    public boolean contains(int px, int py) {
        return px >= x && px < x + width && py >= y && py < y + height;
    }

    @Override
    public String toString() {
        return String.format("Rect[x=%d, y=%d, w=%d, h=%d]", x, y, width, height);
    }
}
