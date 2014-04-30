set DIST=.\classes
set CLASSPATH=.;%DIST%;.\lib\jcommon-1.0.17.jar;.\lib\jfreechart-1.0.14.jar
set VERSION=0.2.4

echo "PILOTS Build Script (v%VERSION%)"
echo "Please make sure the current directory is in your CLASSPATH"
echo ""

if exist %DIST% (
	del /Q %DIST%
)

echo "Making dir: "%DIST%
mkdir %DIST%

echo package pilots; > .\pilots\Version.java
echo public class Version { public static final String ver = "%VERSION%"; } >> .\pilots\Version.java

echo "Compiling pilots/"
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\Version.java

echo "Compiling pilots/compiler"
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\compiler\codegen\*.java
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\compiler\parser\*.java
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\compiler\*.java

echo "Compiling pilots/runtime"
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\runtime\*.java
javac -cp %CLASSPATH% -Xlint:none -d %DIST% pilots\runtime\errsig\*.java

echo "Compiling pilots/util"
javac -cp %CLASSPATH% -Xlint:none -g -d %DIST% pilots\util\*.java

echo "Generating jar file..."
cd %DIST%
jar cf ..\lib\pilots.jar pilots\compiler\ pilots\runtime\ pilots\util\
cd ..

echo "Finished!"

