@echo off
setlocal
set "GRADLE_VERSION=8.4"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "INSTALL_ROOT=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin\soft12"
set "GRADLE_HOME=%INSTALL_ROOT%\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"

if not exist "%GRADLE_BIN%" (
  if not exist "%INSTALL_ROOT%" mkdir "%INSTALL_ROOT%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $u='https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip'; $z='%INSTALL_ROOT%\gradle-%GRADLE_VERSION%-bin.zip'; Invoke-WebRequest -Uri $u -OutFile $z; Expand-Archive -LiteralPath $z -DestinationPath '%INSTALL_ROOT%' -Force; Remove-Item $z"
  if errorlevel 1 exit /b %errorlevel%
)
call "%GRADLE_BIN%" %*
exit /b %errorlevel%
