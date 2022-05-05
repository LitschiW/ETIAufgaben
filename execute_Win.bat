@echo off
::===============================================================
:: This file will look for a jre 8 installation on your system. 
:: The search is based on your %JAVA_HOME% variable.
:: Once found it will start the Generator.
:: Notice, that we have to use javaw.exe to prevent a console from poping up.
::===============================================================

set JAR_TARGET=%~dp0GeneratorUI.jar

if "%OS%"=="Windows_NT" setlocal

if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%

if "%JAVA_HOME%"=="%JAVA_HOME:1.8=%" (
	echo derp
    goto findJavaEightFromJavaHome
)
set JAVA_EXE=%JAVA_HOME%/bin/javaw.exe

if exist "%JAVA_EXE%" goto execute
echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:findJavaEightFromJavaHome
set JAVA_HOME2=%JAVA_HOME%\..\
for /f "delims=" %%a in ('dir /ad /b "%JAVA_HOME2%*1.8*"') do @set VAR=%%a

set JAVAEIGHT_HOME=%JAVA_HOME2%%VAR%
set JAVA_EXE=%JAVAEIGHT_HOME%\bin\javaw.exe 

if exist "%JAVA_EXE%" goto execute
echo.
echo ERROR: JAVA_HOME is set but there was no Java8 jre/jdk found in %JAVA_HOME2%
echo.
echo Make sure to install a version of Java8 in the same directory level as your main java version.
echo (this happens automatically with the default installation)
goto fail

:execute
echo.
WHERE pdflatex >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
	echo Warning! pdflatex was not found. Please install MikTex on this System.
	echo "Otherwise this program will not run properly!"
	echo.
)
echo Starting Generator...
start "" "%JAVA_EXE%" -jar "%JAR_TARGET%"
goto mainEnd

:fail
exit 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal