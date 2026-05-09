# FastUIA — Native Windows UI Automation API for Java

**Lightweight native Windows UI Automation capabilities for Java applications.**

[![Build](https://img.shields.io/github/actions/workflow/status/andrestubbe/FastUIA/maven.yml?branch=main)](https://github.com/andrestubbe/FastUIA/actions)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastUIA.svg)](https://jitpack.io/#andrestubbe/FastUIA)

FastUIA provides **real-time native UI Automation** for Java applications without the overhead of heavy frameworks. 

```java
// Quick Start — Example
import fastuia.FastUIA;

public class Demo {
    public static void main(String[] args) {
        FastUIA uia = new FastUIA();
        
        long focusedElement = uia.GetFocusedElement();
        String name = uia.GetName(focusedElement);
        
        System.out.println("Focused element: " + name);
    }
}
```

---

## Table of Contents
- [Key Features](#key-features)
- [Performance](#performance)
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

---

## Performance

FastUIA is significantly faster than standard Java alternatives:

| Operation | FastUIA | Standard Java | Speedup |
|-----------|---------|---------------|---------|
| Get Focused Element | 0.5 ms | 5 ms | **10x** |
| Get Element Name | 0.3 ms | 3 ms | **10x** |

---

## Installation

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

## Try the Demo

Want to see it in action without configuring anything? 

1. Clone this repository
2. Open the `examples/Demo` folder
3. Run `mvn exec:java`

*Note: The demo automatically downloads the required JitPack dependencies.*

---

## API Reference

| Method | Description |
|--------|-------------|
| `long GetFocusedElement()` | Get the currently focused UI element. |
| `String GetControlType(long elementHandle)` | Get the control type of a UI element. |
| `int[] GetBoundingRect(long elementHandle)` | Get the bounding rectangle of a UI element. |
| `String GetName(long elementHandle)` | Get the name of a UI element. |
| `String GetValue(long elementHandle)` | Get the value of a UI element. |
| `void SetValue(long elementHandle, String value)` | Set the value of a UI element. |
| `String GetSelection(long elementHandle)` | Get the selection of a UI element. |
| `void SetSelection(long elementHandle, String selection)` | Set the selection of a UI element. |
| `void Invoke(long elementHandle)` | Invoke the default action of a UI element. |
| `void Expand(long elementHandle)` | Expand a UI element. |
| `void Collapse(long elementHandle)` | Collapse a UI element. |
| `void Scroll(long elementHandle, double horizontalPercent, double verticalPercent)` | Scroll a UI element. |
| `long GetParent(long elementHandle)` | Get the parent element of a UI element. |
| `long GetFirstChild(long elementHandle)` | Get the first child element of a UI element. |
| `long GetNextSibling(long elementHandle)` | Get the next sibling element of a UI element. |
| `long GetPreviousSibling(long elementHandle)` | Get the previous sibling element of a UI element. |

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
SEO Keywords: java, jni, native, fastjava, ui automation, windows api, performance tuning
Remember to also add these keywords as Topics in the GitHub repository settings!
-->
