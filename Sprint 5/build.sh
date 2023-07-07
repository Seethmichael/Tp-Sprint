#!/bin/bash.
#compilation de FW
javac -d Framework/build Framework/src/*.java

#exportation en jar
cd Framework/build
jar -cvf ../../fw.jar .
cd ../../

#copie vers le projet de test
cp fw.jar Test/WEB-INF/lib

#compilation du projet de test
cd Test/build
echo $classpath
export CLASSPATH=$CLASSPATH:C:/Users/Seth Michael/Desktop/Sprint Michael/Sprint 5/Test
echo $classpath
javac -d . ../src/*.java
cd ../../

#creation de l'archive war
jar -cvf Test.war Test/WEB-INF

#copie vers le deploiment
cp Test.war C:/Program Files/Apache Software Foundation/Tomcat 10.0/webapps