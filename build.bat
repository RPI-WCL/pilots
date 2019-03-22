@echo off
set DIST=.\classes
set ROOT=%CD%
set CLASSPATH=.;%DIST%;.\lib\jcommon-1.0.17.jar;.\lib\jfreechart-1.0.14.jar;.\lib\json-java.jar;
set VERSION=0.4.1

echo PILOTS Build Script (v%VERSION%)
echo Please make sure the current directory is in your CLASSPATH
echo Current Path is %ROOT%
if EXIST %DIST% (
	echo Removing old classes directory...
	rd /s /q %DIST%
)

echo Making dir: %DIST%...
mkdir %DIST%

echo package pilots; > .\pilots\Version.java
echo public class Version { public static final String ver = "%VERSION%"; } >> .\pilots\Version.java
echo "Compiling pilots..."


setlocal EnableDelayedExpansion
(for /f "delims=" %%f in ('dir /b /s *.java ^| findstr /v "example test"') do (set "F=%%f" & set "F=!F:\=\\!" & echo "!F!")) > gen_javasrc.txt

javac -cp %CLASSPATH% -Xlint:none -d %DIST% @gen_javasrc.txt
if EXIST gen_javasrc.txt (
	del /q gen_javasrc.txt
)

echo "Generating jar file..."

cd %DIST%
(for /f "delims=" %%f in ('dir *.class /b/s') do (set "F=%%f" & set "F=!F:\=\\!" & set "F=!F: =\ !" & echo "!F!")) > gen_javasrc.txt
jar cf "%ROOT%\lib\pilots.jar" @gen_javasrc.txt
if EXIST gen_javasrc.txt (
	del /q gen_javasrc.txt
)
cd %ROOT%
endlocal
echo "Finished!"

