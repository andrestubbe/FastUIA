@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo [FastUIA] Running Demo (via JitPack)...
cd examples\Demo
call mvn compile exec:java -Dexec.mainClass=${exec.mainClass}
cd ..\..
pause
