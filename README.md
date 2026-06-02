# FastUIA v0.1.0 [ALPHA] — Native Windows UI Automation API for Java

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastUIA/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

**⚡ High-performance, native Windows UI Automation (UIA) for Java.**

FastUIA provides **real-time native UI Automation** for Java applications without the overhead of heavy frameworks.

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

---

## Table of Contents

- [Key Features](#key-features)
- [Installation](#installation)
- [Try the Demo](#try-the-demo)
- [API Reference](#api-reference)
- [Platform Support](#platform-support)
- [Building from Source](#building-from-source)
- [License](#license)
- [Related Projects](#related-projects)

---

```java
// Quick Start — Example

import fastuia.FastUIA;
import fastuia.FastUIAElement;

public class Demo {
    public static void main(String[] args) {
        FastUIA uia = new FastUIA();
        // Get focused element
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

- **🚀 Native Performance** — Direct UI Automation API access via JNI.
- **⚡ Zero Overhead** — No polling, purely event-driven callbacks.
- **📦 Zero Dependencies** — Just requires Java 17+ and Windows.
- **🎯 Object-Oriented** — Clean, type-safe API for elements and patterns.

---

## Performance

FastJava modules require **two** dependencies: the module itself, and `FastCore` (which handles the cross-platform
native library extraction).

### Maven (JitPack)

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<!-- 1. The FastUIA Module -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastuia</artifactId>
    <version>0.1.0</version>
</dependency>

<!-- 2. FastCore (Required for native loading) -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastcore</artifactId>
    <version>0.1.0</version>
</dependency>
</dependencies>
```

### Gradle (JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastuia:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

---

## API Reference (FastUIAElement)

| Method                                    | Description                                   |
|-------------------------------------------|-----------------------------------------------|
| `getName()`                               | Get the name of the UI element.               |
| `getControlType()`                        | Get the `ControlType` enum value.             |
| `getValue()` / `setValue(String)`         | Access the `ValuePattern` if supported.       |
| `getSelection()` / `setSelection(String)` | Access the `TextPattern` if supported.        |
| `invoke()`                                | Trigger the default action (`InvokePattern`). |
| `expand()` / `collapse()`                 | Control `ExpandCollapsePattern`.              |
| `getBoundingRect()`                       | Get the `Rect` (x, y, w, h).                  |
| `getParent()` / `getFirstChild()`         | Navigate the UI tree.                         |
| `supportsValue()` / `supportsText()`      | Check for pattern support.                    |

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHIE.md](docs/PHILOSOPHIE.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) - Binary file indexing with mmap support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live file monitoring
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*



