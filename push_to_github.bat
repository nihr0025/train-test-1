@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ===================================================
echo TrainMate Automation Framework - Git Push Automation
echo ===================================================

:: Ensure Git is in PATH if newly installed
where git >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Git\cmd\git.exe" (
        set "PATH=%PATH%;C:\Program Files\Git\cmd"
        echo Added C:\Program Files\Git\cmd to current session PATH.
    ) else if exist "C:\Program Files\Git\bin\git.exe" (
        set "PATH=%PATH%;C:\Program Files\Git\bin"
        echo Added C:\Program Files\Git\bin to current session PATH.
    ) else if exist "%LOCALAPPDATA%\Programs\Git\cmd\git.exe" (
        set "PATH=%PATH%;%LOCALAPPDATA%\Programs\Git\cmd"
        echo Added %LOCALAPPDATA%\Programs\Git\cmd to current session PATH.
    ) else (
        echo [ERROR] Git executable could not be found. Please restart your terminal or verify Git installation.
        pause
        exit /b 1
    )
)

echo [1/6] Verifying Git Version:
git --version
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Git execution failed.
    pause
    exit /b 1
)

echo.
echo [2/6] Checking Git Repository Status:
if not exist ".git" (
    echo Initializing new Git repository...
    git init
    git branch -M main
) else (
    echo Existing .git repository detected.
)

echo.
echo [3/6] Configuring Remote:
git remote get-url origin >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Remote 'origin' already exists:
    git remote -v
) else (
    echo Adding remote origin https://github.com/nihr0025/trainmate-test.git
    git remote add origin https://github.com/nihr0025/trainmate-test.git
)

echo.
echo [4/6] Staging Project Files (excluding secrets and build artifacts):
git add pom.xml testng.xml README.md .gitignore src/
git status -s

echo.
echo [5/6] Committing changes:
git commit -m "Fix TrainMate UI automation workflow failures, improve BasePage diagnostics, and synchronize page objects"

echo.
echo [6/6] Safely syncing and pushing to remote (no force push):
git pull origin main --rebase >nul 2>&1
git push -u origin main

if %ERRORLEVEL% equ 0 (
    echo.
    echo ===================================================
    echo SUCCESS: Project successfully pushed to GitHub!
    echo URL: https://github.com/nihr0025/trainmate-test
    echo ===================================================
) else (
    echo.
    echo [NOTICE] Push returned code %ERRORLEVEL%. If GitHub requires authentication or a token, follow the browser/credential prompt.
)

echo.
pause
