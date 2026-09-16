# FastUIA Roadmap

**Vision**: To provide the fastest possible native primitives for Windows UI Automation in Java by aggressively bypassing the COM marshaling overhead of standard frameworks.

---

## v0.1.0: Initial Release (Current)

- [x] **Core Native Engine**: JNI bindings to `IUIAutomation` and `IUIAutomationElement` COM interfaces.
- [x] **Element Retrieval**: `getFocusedElement()`, `getRootElement()`, `getElementFromPoint(x, y)`.
- [x] **Element Properties**: `getName()`, `getControlType()`, `getBoundingRect()`, `getAutomationId()`, `getFrameworkId()`, `getProcessId()`.
- [x] **Pattern Support**: `ValuePattern`, `TextPattern`, `InvokePattern`, `ExpandCollapsePattern`, `ScrollPattern`, and 8 further pattern checks.
- [x] **Tree Traversal**: `getParent()`, `getFirstChild()`, `getNextSibling()`, `getPreviousSibling()`.
- [x] **Event System**: `FocusChangedListener`, `TextChangedListener`, `StructureChangedListener` backed by native COM event sinks.
- [x] **FastCore Integration**: Automated native DLL extraction and loading via `FastCore.loadLibrary`.
- [x] **FastJava Blueprint Standards**: README, Reference, Philosophy, Roadmap, Changelog, JMH Benchmark.

---

## v0.2.0: Advanced Automation

- [ ] **Condition-Based Search**: `findFirst(condition)`, `findAll(condition)` backed by native `IUIAutomationCondition`.
- [ ] **Extended Pattern Actions**: Full `TogglePattern`, `RangeValuePattern.setValue()`, `SelectionItemPattern.select()`.
- [ ] **Window Pattern**: `close()`, `minimize()`, `maximize()` via `IUIAutomationWindowPattern`.
- [ ] **Caching Mode**: Optional element caching via `IUIAutomationCacheRequest` for bulk property reads.

---

## v0.5.0: Ecosystem Integration

- [ ] **FastOverlay Bridge**: Direct `getBoundingRect()` to `FastOverlay` bounding-box rendering in one call for live UI inspection.
- [ ] **FastRobot Bridge**: Shortcut methods to click center-of-bounding-rect via `FastRobot.mouseMove + click`.
- [ ] **FastAgent Tool Executor**: Expose UIA actions as structured tool calls for LLM-driven agentic desktop automation.

---

## v1.0.0: Production Hardening

- [ ] **Cross-Platform**: AT-SPI2 (Linux) and `AXUIElement` (macOS) backend implementations.
- [ ] **Full Stability Audit**: Long-run stress testing under concurrent element access.
- [ ] **Extended ControlType Coverage**: Full mapping of all 50+ Windows UIA ControlType constants.

---

**Focus**: Performance is the USP. We optimize where Java stops.