cls
@echo off
REM compilation de FW
cd Framework\build

javac -parameters -d . ..\src\*.java

REM exportation
jar -cvf ..\..\fw.jar .
cd ..\..\