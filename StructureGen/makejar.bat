javac @arguments.txt src/*.java
jar cfm Structure.jar src/Manifest.txt *.class src src/Manifest.txt *.bat
del *.class
java -jar -Xmx200m Structure.jar --append
pause