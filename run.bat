@echo off

REM ===== Java setup =====
set JAVA_HOME=C:\Users\Kevin Meng\.jdks\openjdk-25.0.1
set PATH=%JAVA_HOME%\bin;%PATH%

REM ===== Move to project directory =====
cd /d %~dp0

REM ===== Log start =====
echo =============================== >> logs\run.log
echo START File Downloader %DATE% %TIME% >> logs\run.log

REM ===== Run application =====
java -jar target\ibkr_xmlFileDownloader-0.0.1-SNAPSHOT.jar >> logs\run.log 2>&1

REM ===== Capture exit code =====
set EXIT_CODE=%errorlevel%

REM ===== Failure handling =====
if %EXIT_CODE% neq 0 (
    echo ERROR: Downloader failed with exit code %EXIT_CODE% >> logs\run.log
    echo END Downloader FAILED %DATE% %TIME% >> logs\run.log
    exit /b %EXIT_CODE%
)

REM ===== Success =====
echo SUCCESS: Downloader completed successfully >> logs\run.log
echo END Downloader SUCCESS %DATE% %TIME% >> logs\run.log

exit /b 0
