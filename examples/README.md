# FastUIA Examples

This folder contains standalone example projects to demonstrate and test the library.

## Demo

The `Demo` project provides a simple "Hello World" implementation.

To run it locally using the JAR you just built:
```bash
cd Demo
mvn compile exec:java
```

Or use the convenience script from the root:
```bash
run-demo.bat
```

## Benchmark

The `Benchmark` project compares the performance of the native FastUIA library against standard Java equivalents.

To run it:
```bash
cd Benchmark
mvn compile exec:java
```

Or use the convenience script from the root:
```bash
run-benchmark.bat
```

> **Note:** By default, the `pom.xml` files in these examples are configured to use JitPack dependencies. This allows external users to run them directly. For local development testing, you can uncomment the system scope dependency pointing to the local `target/` directory.
