#!/bin/bash.
#compilation de FW
javac -d Framework/build Framework/src/*.java

#exportation en jar
cd Framework/build
jar -cvf ../../fw.jar .
cd ../../

#copie vers le projet de test
cp fw.jar Test-Framework/WEB-INF/lib

#compilation du projet de test
cd Test-Framework/build
echo $classpath
export CLASSPATH=$CLASSPATH:/media/asinvs/96E7D7FBD2ED3C4E/S4/projets/M_Naina/Renato-Framework/fw.jar
echo $classpath
javac -d . ../src/*.java
cd ../../

#creation de l'archive war
jar -cvf Test-Framework.war Test-Framework/WEB-INF

#copie vers le deploiment
cp Test-Framework.war /home/asinvs/apps/apache-tomcat-8.5.82/webapps