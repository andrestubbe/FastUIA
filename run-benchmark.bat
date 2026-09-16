@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo [1/3] Building FastUIA library...
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 ( echo [ERROR] Build failed. & pause & exit /b 1 )

echo [2/3] Building JMH Benchmark uber-jar...
cd examples\Benchmark
call mvn clean package -q
if %errorlevel% neq 0 ( echo [ERROR] Benchmark build failed. & pause & exit /b 1 )

echo [3/3] Running JMH Benchmarks...
powershell -Command "Unblock-File -Path target\fastuia-benchmark.jar" 2>nul
java -jar target\fastuia-benchmark.jar

cd ..\..
pause
