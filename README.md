# FastUIA — Native Windows UI Automation API for Java

**High-performance, native Windows UI Automation (UIA) for Java.**

[![Build](https://img.shields.io/github/actions/workflow/status/andrestubbe/FastUIA/maven.yml?branch=main)](https://github.com/andrestubbe/FastUIA/actions)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastUIA.svg)](https://jitpack.io/#andrestubbe/FastUIA)

FastUIA provides **real-time native UI Automation** for Java applications without the overhead of heavy frameworks. 

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

## Key Features

- **🚀 Native Performance** — Direct UI Automation API access via JNI.
- **⚡ Zero Overhead** — No polling, purely event-driven callbacks.
- **📦 Zero Dependencies** — Just requires Java 17+ and Windows.
- **🎯 Object-Oriented** — Clean, type-safe API for elements and patterns.

---

## Performance

FastJava modules require **two** dependencies: the module itself, and `FastCore` (which handles the cross-platform native library extraction).

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

| Method | Description |
|--------|-------------|
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

## Platform Support

| Platform | Status |
|----------|--------|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux | 🚧 Planned |
| macOS | 🚧 Planned |

---

## Building from Source

For detailed instructions on compiling the C++ JNI code and building the Maven FatJAR, see [COMPILE.md](COMPILE.md).

---

## License
MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java
- [FastWindow](https://github.com/andrestubbe/FastWindow) — High‑performance window management
- [FastRobot](https://github.com/andrestubbe/FastRobot) — Native input simulation

---
**Made with ⚡ by Andre Stubbe**

<!-- 
GitHub About: High-performance, native Windows UI Automation (UIA) for Java. Lightweight, event-driven, and architected for real-time system tools, overlays, and automation.
Topics: java, jni, native, windows-api, ui-automation, uia, performance, low-latency, fastjava, automation-framework
-->


