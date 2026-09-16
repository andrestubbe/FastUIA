# FastUIA 0.1.0 [ALPHA-2026-06-14]: Native Windows UI Automation API for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastUIA/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.0-green.svg)](https://jitpack.io/#andrestubbe/FastUIA)

---

**⚡ Direct COM-level Windows UI Automation (UIA) access, zero-copy element queries, and event-driven focus tracking for Java.**

**FastUIA** provides low-latency native UI Automation for Java by communicating directly with the Windows `IUIAutomation` COM interfaces via JNI, bypassing the marshaling overhead of standard frameworks. Designed for AI agents, RPA bots, and desktop automation systems that need to read and interact with UI elements at millisecond speed using **[FastCore](https://github.com/andrestubbe/FastCore)** for seamless native library loading.

[**Watch Showcase Demo (YouTube)**](https://youtu.be/CK4PaWDHq8w)

[![FastUIA Showcase](docs/screenshot.png)](https://youtu.be/CK4PaWDHq8w)

---

## Quick Start

```java
import fastuia.FastUIA;
import fastuia.FastUIAElement;

public class Demo {
    public static void main(String[] args) {
        FastUIA uia = new FastUIA();

        // 1. Query the currently focused UI element
        FastUIAElement el = uia.getFocusedElement();
        if (el != null) {
            System.out.println("Focused: " + el.getName());
            System.out.println("Type:    " + el.getControlType());
            if (el.supportsValue()) {
                System.out.println("Value: " + el.getValue());
            }
        }

        // 2. Get element at specific screen coordinates
        FastUIAElement atPoint = uia.getElementFromPoint(960, 540);
        if (atPoint != null) {
            System.out.println("At (960,540): " + atPoint.getName());
        }

        // 3. Traverse the UI tree
        FastUIAElement root = uia.getRootElement();
        FastUIAElement first = root != null ? root.getFirstChild() : null;
        if (first != null) {
            System.out.println("First desktop child: " + first.getName());
        }
    }
}
```

---

## Table of Contents

- [Why FastUIA?](#why-fastuia)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [API Quick Reference](#api-quick-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [Related Projects](#related-projects)
- [License](#license)

---

## Why FastUIA?

Standard Java UI automation stacks (Microsoft's own `UIAutomationClient`, third-party wrappers like WinAppDriver, Selenium) introduce multiple abstraction layers that severely degrade performance for tight automation loops:

1. **COM Marshaling Overhead**: Every UIA call crosses process boundaries through heavyweight COM proxy stubs, adding 1-5 ms of latency per element query in framework wrappers.
2. **No Direct Event Dispatch**: Standard Java UIA wrappers poll element state or piggyback on slow managed event queues. True event-driven focus and structure change callbacks require native COM sinks.
3. **Heap Allocation on Every Query**: Wrapper APIs typically box all COM results into managed objects before returning them to Java, causing heap churn and GC pauses inside automation hot paths.

**FastUIA** bypasses all of these by binding directly to `IUIAutomation` COM interfaces via JNI:

| Feature | Standard Java UIA Wrappers | FastUIA |
|:---|:---|:---|
| **Element Access Latency** | 1-5 ms per call (proxy COM marshaling) | Sub-millisecond direct `IUIAutomation` COM query |
| **Screen-Point Lookup** | Framework polling or slow HitTest | Direct `ElementFromPoint` via native COM call |
| **Focus Events** | Polling or heavy event listener overhead | Native `IUIAutomationFocusChangedEventHandler` sink |
| **Tree Traversal** | Object allocation per node | Zero-copy handle-based traversal |
| **Heap Pressure** | Boxed COM results on every call | `GetPrimitiveArrayCritical` pinning, no heap churn |
| **Pattern Support** | Generic pattern factory with reflection | Typed native pattern check per interface |

---

## Key Features

- ⚡ **Direct COM-Level Access**: JNI binds directly to `IUIAutomation` and `IUIAutomationElement` COM interfaces without managed marshaling proxies.
- 🎯 **Zero-Copy Element Queries**: Bounding rectangles and primitive properties are transferred via `GetPrimitiveArrayCritical` pinning with no intermediate object allocation.
- 📡 **Event-Driven Callbacks**: Register native `FocusChangedListener`, `TextChangedListener`, and `StructureChangedListener` sinks backed by real Windows UIA event handlers.
- 🌳 **Full Tree Traversal**: Navigate the full desktop UI automation tree via `getParent()`, `getFirstChild()`, `getNextSibling()`, `getPreviousSibling()` with handle-level zero allocation.
- 🧩 **Pattern Introspection**: Typed per-pattern support checks (`supportsValue()`, `supportsInvoke()`, `supportsExpandCollapse()`, `supportsScroll()`, and 9 more) backed by native COM `QueryInterface`.
- 🔗 **FastCore Integration**: Automated zero-dependency native DLL extraction and loading via `FastCore.loadLibrary`.

---

## Real-World Use Cases

- 🤖 **Autonomous AI Desktop Agents**: Read the focused element, control type, and bounding rectangle at each step of an AI agent loop to build a real-time UI state model for LLM-driven desktop automation.
- 🕵️ **RPA & Workflow Automation**: Drive enterprise desktop applications (SAP, thick-client ERP, legacy WinForms) by querying and interacting with UI elements at native COM speed.
- 🧪 **High-Speed UI Testing**: Poll and validate element states in tight integration test loops without the 5-15 ms overhead of COM-proxied automation frameworks.
- 🖥️ **Live UI Inspection & Overlay**: Pair FastUIA element queries with **[FastOverlay](https://github.com/andrestubbe/FastOverlay)** to draw real-time bounding boxes around focused or hovered elements.

---

## API Quick Reference

### FastUIA (Session)

| Method | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `getFocusedElement()` | `FastUIAElement` | Returns the currently keyboard-focused UI element. | [Reference](docs/REFERENCE.md) |
| `getRootElement()` | `FastUIAElement` | Returns the root desktop automation element. | [Reference](docs/REFERENCE.md) |
| `getElementFromPoint(x, y)` | `FastUIAElement` | Returns the element at the given screen coordinates. | [Reference](docs/REFERENCE.md) |
| `setClickThrough(title, enabled)` | `void` | Toggles Win32 click-through on a window by title. | [Reference](docs/REFERENCE.md) |
| `addFocusChangedListener(l)` | `void` | Registers a native focus-change event sink. | [Reference](docs/REFERENCE.md) |
| `addTextChangedListener(l)` | `void` | Registers a native text-change event sink. | [Reference](docs/REFERENCE.md) |
| `addStructureChangedListener(l)` | `void` | Registers a native UI structure-change event sink. | [Reference](docs/REFERENCE.md) |

### FastUIAElement

| Method | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `getName()` | `String` | Returns the accessible name of the element. | [Reference](docs/REFERENCE.md) |
| `getControlType()` | `ControlType` | Returns the typed `ControlType` enum value. | [Reference](docs/REFERENCE.md) |
| `getBoundingRect()` | `Rect` | Returns the screen bounding rectangle (x, y, w, h). | [Reference](docs/REFERENCE.md) |
| `getValue()` / `setValue(s)` | `String` / `void` | Gets or sets the element's `ValuePattern` text. | [Reference](docs/REFERENCE.md) |
| `getSelection()` / `setSelection(s)` | `String` / `void` | Gets or sets the element's `TextPattern` selection. | [Reference](docs/REFERENCE.md) |
| `invoke()` | `void` | Triggers the element's default action via `InvokePattern`. | [Reference](docs/REFERENCE.md) |
| `expand()` / `collapse()` | `void` | Controls `ExpandCollapsePattern` state. | [Reference](docs/REFERENCE.md) |
| `scroll(h, v)` | `void` | Scrolls element by horizontal and vertical percent. | [Reference](docs/REFERENCE.md) |
| `getParent()` / `getFirstChild()` | `FastUIAElement` | Navigates the UI automation tree. | [Reference](docs/REFERENCE.md) |
| `getNextSibling()` / `getPreviousSibling()` | `FastUIAElement` | Sibling-level tree traversal. | [Reference](docs/REFERENCE.md) |
| `getAutomationId()` | `String` | Returns the automation ID string of the element. | [Reference](docs/REFERENCE.md) |
| `getFrameworkId()` | `String` | Returns the UI framework name (e.g. `"Win32"`, `"WPF"`). | [Reference](docs/REFERENCE.md) |
| `getProcessId()` | `int` | Returns the owning process ID. | [Reference](docs/REFERENCE.md) |
| `supportsValue()` / `supportsInvoke()` | `boolean` | Pattern support introspection via native `QueryInterface`. | [Reference](docs/REFERENCE.md) |
| `release()` | `void` | Releases the underlying native COM element handle. | [Reference](docs/REFERENCE.md) |

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Focus Inspector Demo** | [Demo.java](examples/Demo/src/main/java/fastuia/Demo.java) | `run-demo.bat` | Queries the focused element, control type, bounding rect, and pattern support at each keystroke. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastuia/benchmark/Benchmark.java) | `run-benchmark.bat` | JMH throughput measurements for element queries, tree traversal, and pattern introspection. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastUIA - Native Windows UI Automation -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastUIA</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastCore - Required Native JNI Loader -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastUIA:0.1.0'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the release JARs directly from GitHub Releases:

1. 📦 **[FastUIA-0.1.0.jar](https://github.com/andrestubbe/FastUIA/releases/tag/0.1.0)** (Core UI Automation Library)
2. ⚙️ **[FastCore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/tag/0.1.0)** (Mandatory Native Loader)

> [!IMPORTANT]
> Both JARs must be included in your classpath for the JNI calls to function correctly.

---

## Documentation

- **[COMPILE.md](docs/COMPILE.md)**: Full native compilation guide (MSVC C++17 build chain + JNI setup).
- **[REFERENCE.md](docs/REFERENCE.md)**: Comprehensive API specification, COM architecture, and event system.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Engineering rationale for direct COM-level UI Automation binding.
- **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestones, pattern expansion, and cross-platform support.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Complete version history and release notes.

---

## Platform Support

| Platform | Architecture | Status | Driver / Subsystem |
|:---|:---:|:---:|:---|
| **Windows 10 / 11** | x64 | ✅ Fully Supported | `IUIAutomation` COM via native Win32 JNI |
| **Linux** | x64 / AArch64 | 🚧 Planned | AT-SPI2 / Atspi via D-Bus |
| **macOS** | Apple Silicon / x64 | 🚧 Planned | Accessibility API (`AXUIElement`) |

---

## Related Projects

- **[`FastCore`](https://github.com/andrestubbe/FastCore)**: Native Library Loader & JNI Utilities for Java
- **[`FastOverlay`](https://github.com/andrestubbe/FastOverlay)**: High-Performance Native DirectComposition Transparent Overlay API for Java
- **[`FastRobot`](https://github.com/andrestubbe/FastRobot)**: Low-Latency Native Input & Bot Automation Substrate
- **[`FastScreen`](https://github.com/andrestubbe/FastScreen)**: High-Speed DXGI Screen Capture Engine (240-2000 FPS)
- **[`FastWindow`](https://github.com/andrestubbe/FastWindow)**: Native Win32 Window Management & Styling Substrate
- **[`FastKeyboard`](https://github.com/andrestubbe/FastKeyboard)**: Ultra-Fast Native RawInput Keyboard Engine

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀
