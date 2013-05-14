export CLASSPATH=./classes:$CLASSPATH 
DIST=./classes
VERSION=0.2

echo "PILOTS Build Script v0.1"
echo "Please make sure the current directory is in your CLASSPATH"
echo ""

if [ -d $DIST ]; then
		rm -rf $DIST
fi
echo "Making dir: "$DIST
mkdir $DIST

echo "Compiling pilots/compiler"
javac -Xlint:none -g -d $DIST `find pilots/compiler/ | grep "java$"`

echo "Compiling pilots/runtime"
javac -Xlint:none -g -d $DIST `find pilots/runtime/ | grep "java$"`

echo "Compiling pilots/util"
javac -Xlint:none -g -d $DIST `find pilots/util/ | grep "java$"`

echo "Compiling pilots/examples"
javac -Xlint:none -g -d $DIST `find pilots/examples/ | grep "java$"`


echo "Generating jar file..."
cd $DIST
jar cf ../lib/pilots.jar `find pilots/compiler/` `find pilots/runtime/` `find pilots/util/` `find pilots/examples/`
cd ..

echo "Finished!"

