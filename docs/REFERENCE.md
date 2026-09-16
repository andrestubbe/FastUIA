# FastUIA API Reference

## Overview

FastUIA binds directly to the Windows `IUIAutomation` COM interface family via JNI, exposing a type-safe Java API backed by native COM element handles. All element handles are reference-counted COM pointers managed by the native layer. Call `release()` on `FastUIAElement` instances when they are no longer needed.

---

## Architecture

```
Java Application
        |
        | (JNI)
        v
fastuia.dll (C++/COM)
        |
        | (COM QueryInterface)
        v
IUIAutomation / IUIAutomationElement (Windows UIA subsystem)
        |
        v
Win32 Window Tree / Accessibility API
```

---

## FastUIA (Session Class)

The `FastUIA` class is the entry point. Instantiate once per session. It holds native event listener registrations.

### Element Retrieval

| Method | Return Type | Description |
|:---|:---|:---|
| `getFocusedElement()` | `FastUIAElement` | Returns the currently keyboard-focused UI element, or `null` if none. |
| `getRootElement()` | `FastUIAElement` | Returns the root desktop element (the Windows desktop itself). |
| `getElementFromPoint(int x, int y)` | `FastUIAElement` | Returns the topmost UI element at the given screen coordinates. |
| `fromHandle(long handle)` | `FastUIAElement` | Wraps an existing raw COM handle in a `FastUIAElement`. |

### Events

| Method | Description |
|:---|:---|
| `addFocusChangedListener(FocusChangedListener)` | Registers a native `IUIAutomationFocusChangedEventHandler` sink. Fires on every keyboard focus change. |
| `removeFocusChangedListener(FocusChangedListener)` | Removes a previously registered focus listener. |
| `addTextChangedListener(TextChangedListener)` | Registers a text-change event handler. |
| `addStructureChangedListener(StructureChangedListener)` | Registers a UI structure-change event handler (elements added or removed). |

### Utilities

| Method | Return Type | Description |
|:---|:---|:---|
| `setClickThrough(String title, boolean enabled)` | `void` | Sets or clears `WS_EX_TRANSPARENT` on a window matching the given title. |
| `release(long handle)` | `void` | Releases a raw native COM element handle. |

---

## FastUIAElement

A `FastUIAElement` wraps a native `IUIAutomationElement` COM pointer. The handle is valid as long as the underlying window exists. Always call `release()` when done.

### Identity & Properties

| Method | Return Type | Description |
|:---|:---|:---|
| `getName()` | `String` | Returns the accessible name (`UIA_NamePropertyId`). |
| `getControlType()` | `ControlType` | Returns the typed `ControlType` enum (Button, Edit, List, etc.). |
| `getBoundingRect()` | `Rect` | Returns the screen bounding rectangle as `Rect(x, y, width, height)`. |
| `getAutomationId()` | `String` | Returns the automation ID string (`UIA_AutomationIdPropertyId`). |
| `getFrameworkId()` | `String` | Returns the UI framework name: `"Win32"`, `"WPF"`, `"WinUI"`, etc. |
| `getProcessId()` | `int` | Returns the owning process ID. |
| `isValid()` | `boolean` | Returns whether the underlying COM handle is still valid. |

### Patterns

| Method | Check | Get / Set / Action |
|:---|:---|:---|
| **ValuePattern** | `supportsValue()` | `getValue()` / `setValue(String)` |
| **TextPattern** | `supportsText()` | `getSelection()` / `setSelection(String)` |
| **InvokePattern** | `supportsInvoke()` | `invoke()` |
| **ExpandCollapsePattern** | `supportsExpandCollapse()` | `expand()` / `collapse()` |
| **ScrollPattern** | `supportsScroll()` | `scroll(double h, double v)` |
| **SelectionPattern** | `supportsSelection()` | (query only) |
| **TogglePattern** | `supportsToggle()` | (query only) |
| **RangeValuePattern** | `supportsRangeValue()` | (query only) |
| **GridPattern** | `supportsGrid()` | (query only) |
| **GridItemPattern** | `supportsGridItem()` | (query only) |
| **SelectionItemPattern** | `supportsSelectionItem()` | (query only) |
| **WindowPattern** | `supportsWindow()` | (query only) |
| **LegacyIAccessible** | `supportsLegacyIAccessible()` | (query only) |

### Tree Traversal

| Method | Return Type | Description |
|:---|:---|:---|
| `getParent()` | `FastUIAElement` | Returns the parent element in the automation tree. |
| `getFirstChild()` | `FastUIAElement` | Returns the first child element. |
| `getNextSibling()` | `FastUIAElement` | Returns the next sibling at the same tree level. |
| `getPreviousSibling()` | `FastUIAElement` | Returns the previous sibling at the same tree level. |

### Lifecycle

| Method | Description |
|:---|:---|
| `release()` | Releases the underlying native COM `IUIAutomationElement` pointer. |
| `handle()` | Returns the raw long COM pointer value. |

---

## ControlType Enum

Maps Windows UIA `UIA_ControlTypeId` constants to Java enum values:

`Button`, `Calendar`, `CheckBox`, `ComboBox`, `Edit`, `Hyperlink`, `Image`, `ListItem`, `List`, `Menu`, `MenuBar`, `MenuItem`, `ProgressBar`, `RadioButton`, `ScrollBar`, `Slider`, `Spinner`, `StatusBar`, `Tab`, `TabItem`, `Text`, `ToolBar`, `ToolTip`, `Tree`, `TreeItem`, `Custom`, `Group`, `Thumb`, `DataGrid`, `DataItem`, `Document`, `SplitButton`, `Window`, `Pane`, `Header`, `HeaderItem`, `Table`, `TitleBar`, `Separator`, `SemanticZoom`, `AppBar`, `Unknown`

---

## Rect

A plain value class returned by `getBoundingRect()`:

```java
public record-like class Rect {
    public final int x, y, width, height;
}
```

---

## Event Listener Interfaces

```java
@FunctionalInterface
public interface FocusChangedListener {
    void onFocusChanged(FastUIAElement element);
}

@FunctionalInterface
public interface TextChangedListener {
    void onTextChanged(FastUIAElement element, String newText);
}

@FunctionalInterface
public interface StructureChangedListener {
    void onStructureChanged(FastUIAElement element, int changeType);
}
```

---

## Platform Support

| Platform | Architecture | Status | Subsystem |
|:---|:---:|:---:|:---|
| **Windows 10 / 11** | x64 | ✅ Fully Supported | `IUIAutomation` COM via native Win32 JNI |
| **Linux** | x64 / AArch64 | 🚧 Planned | AT-SPI2 via D-Bus |
| **macOS** | Apple Silicon / x64 | 🚧 Planned | Accessibility API (`AXUIElement`) |

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀