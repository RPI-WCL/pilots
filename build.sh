echo "compiling pilots/compiler/*.java..."
javac -Xlint pilots/compiler/*.java

echo "compiling pilots/compiler/codegen/*.java..."
javac -Xlint pilots/compiler/codegen/*.java

echo "compiling pilots/compiler/parser/*.java..."
javac -Xlint pilots/compiler/parser/*.java

echo "compiling pilots/runtime/*.java..."
javac -Xlint pilots/runtime/*.java

echo "compiling pilots/tests/*.java..."
javac -Xlint -cp .:./lib/jcommon-1.0.17.jar:./lib/jfreechart-1.0.14.jar pilots/tests/*.java

echo "creating pilots.jar under ./lib..."
jar cf ./lib/pilots.jar `find pilots -name *.class`


