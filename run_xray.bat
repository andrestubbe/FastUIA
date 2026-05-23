@echo off
echo [FastUIA] Starting Desktop X-Ray Demo (1:1 Alignment)...
cd examples\Demo
call mvn compile exec:java -Dexec.mainClass="fastuia.UIADesktopXRay"
cd ..\..
pause
