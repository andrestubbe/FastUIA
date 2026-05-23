@echo off
echo [FastUIA] Starting Map Overlay Demo...
cd examples\Demo
call mvn compile exec:java -Dexec.mainClass="fastuia.UIAMapOverlay"
cd ..\..
pause
