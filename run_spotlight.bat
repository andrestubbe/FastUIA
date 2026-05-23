@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-25
set MAVEN_HOME=C:\Program Files\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo [1/2] Baking changes...
cd examples\Demo
call mvn -q compile -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Starting Spotlight...
call mvn -q exec:java "-Dexec.mainClass=fastuia.UIADesktopXRayV2"
cd ..\..
