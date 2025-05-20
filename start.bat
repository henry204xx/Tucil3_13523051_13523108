@echo off
setlocal enabledelayedexpansion

REM Directories
set SRC_DIR=src
set BIN_DIR=bin

REM 
if "%1"=="" goto :run
if "%1"=="all" goto :all
if "%1"=="run" goto :run
if "%1"=="clean" goto :clean
goto :invalid_target

:all
echo Compiling Java sources...
if not exist %BIN_DIR% mkdir %BIN_DIR%

REM Find all Java sources and compile them
set SOURCES=
for /r %SRC_DIR% %%f in (*.java) do (
    set SOURCES=!SOURCES! "%%f"
)
javac -d %BIN_DIR% %SOURCES%
if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    exit /b 1
)
echo Compilation successful.
goto :eof

:run
call :all
echo Running Animator...
java -cp %BIN_DIR% Animator
goto :eof

:clean
echo Cleaning compiled files...
if exist %BIN_DIR% (
    del /q /s %BIN_DIR%\*.class > nul 2>&1
    echo Clean completed.
) else (
    echo Nothing to clean.
)
goto :eof

:invalid_target
echo Invalid target: %1
echo Available targets: all, run, clean
exit /b 1

:eof
exit /b 0