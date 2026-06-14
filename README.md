# FastUIA 0.1.0 [ALPHA-2026-06-14] — Native Windows UI Automation API for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastUIA/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastUIA)

**⚡ High-performance, native Windows UI Automation (UIA) for Java.**

FastUIA provides **real-time native UI Automation** for Java applications without the overhead of heavy frameworks.

[![FastUIA Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

---

## Table of Contents

- [Key Features](#key-features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [API Reference](#api-reference)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Quick Start

```java
import fastuia.FastUIA;
import fastuia.FastUIAElement;

public class Demo {
    public static void main(String[] args) {
        FastUIA uia = new FastUIA();
        FastUIAElement el = uia.getFocusedElement();
        if (el != null) {
            System.out.println("Focused: " + el.getName());
            System.out.println("Type:    " + el.getControlType());
            if (el.supportsValue()) {
                System.out.println("Value: " + el.getValue());
            }
        }
    }
}
```

---

## Key Features

- ⚡ **Native Performance** — Direct UI Automation API access via JNI, no COM overhead.
- 🎯 **Zero Overhead** — No polling, purely event-driven callbacks.
- 🔗 **Zero Dependencies** — Just requires Java 17+ and Windows.
- 🧩 **Object-Oriented** — Clean, type-safe API for elements and automation patterns.

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
    <!-- FastUIA Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastUIA</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
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

Download the latest JARs directly to add them to your classpath:

1. 📦 **[fastuia-0.1.0.jar](https://github.com/andrestubbe/FastUIA/releases/download/0.1.0/fastuia-0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.

---

## API Reference

| Method | Description |
|-------------------------------------------|-----------------------------------------------|
| `getName()` | Get the name of the UI element. |
| `getControlType()` | Get the `ControlType` enum value. |
| `getValue()` / `setValue(String)` | Access the `ValuePattern` if supported. |
| `getSelection()` / `setSelection(String)` | Access the `TextPattern` if supported. |
| `invoke()` | Trigger the default action (`InvokePattern`). |
| `expand()` / `collapse()` | Control `ExpandCollapsePattern`. |
| `getBoundingRect()` | Get the `Rect` (x, y, w, h). |
| `getParent()` / `getFirstChild()` | Navigate the UI tree. |
| `supportsValue()` / `supportsText()` | Check for pattern support. |

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions and method reference.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status             |
|---------------|--------------------|
| Windows 10/11 | ✅ Fully Supported  |
| Linux         | 🚧 Planned         |
| macOS         | 🚧 Planned         |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore) — Unified JNI Loader and Platform Abstraction
- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) — Binary File Indexing with mmap Support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) — Prefix Trie, N-Gram Index, and Ranking Engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) — USN Journal-based Live File Monitoring

---
**Part of the FastJava Ecosystem** — *Making the JVM faster. ⚡*
