@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "M2_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3"
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;C:\Windows\System32;C:\Windows;C:\Windows\System32\WindowsPowerShell\v1.0;C:\Program Files\Git\cmd;%PATH%"
if exist "C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe" (
    C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe %*
) else (
    cmd.exe /c %*
)
