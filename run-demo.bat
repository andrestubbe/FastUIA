@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo [1/3] Building FastUIA library...
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 ( echo [ERROR] Build failed. & pause & exit /b 1 )

echo [2/3] Compiling Demo...
cd examples\Demo
call mvn compile -q
if %errorlevel% neq 0 ( echo [ERROR] Demo compile failed. & pause & exit /b 1 )

echo [3/3] Running Demo...
call mvn exec:java -Dexec.mainClass=fastuia.Demo -q

cd ..\..
pause
