@echo off
cls

REM classpath 
set CLASSPATH=%CLASSPATH%;C:\Users\Seth Michael\Desktop\Sprint Michael\Sprint 13\fw.jar

REM Déclaration des variables de chemin
REM chemin du projet
set "project=C:\Users\Seth Michael\Desktop\Sprint Michael\Sprint 13\Test"

REM nom du projet dans le deploiement
set "name=TFW"

REM chemin du .jar du framework
set "fw=C:\Users\Seth Michael\Desktop\Sprint Michael\Sprint 13\fw.jar"

REM chemin vers le repertoire webapps de tomcat
set "deploy=C:\Program Files\Apache Software Foundation\Tomcat 10.0\webapps"

REM Création du répertoire temp
mkdir temp

REM Copie du projet dans le répertoire temp
xcopy "%project%" temp /E /C /I /Q /H /R /K /Y


REM Changement du répertoire courant vers temp
cd temp
echo cd

REM Copie du fichier FW.jar dans le répertoire lib du projet
copy "%fw%" "WEB-INF\lib"

REM Compilation des fichiers de src vers le dossier classes avec l'option parameters
javac   -d WEB-INF\classes src\*.java

REM Exportation du projet en un fichier .war
rd /s /q src
jar -cvf "%name%".war *

REM Copie du fichier .war dans le répertoire webapps de Tomcat
copy "%name%".war "%deploy%"

REM Suppression du répertoire temp
cd ../
rd /s /q temp

echo Deploiement effectue.


