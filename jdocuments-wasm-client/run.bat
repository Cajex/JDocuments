@echo off
setlocal enabledelayedexpansion

echo Baue WebAssembly Client...
wasm-pack build --release --target web -d dist
if errorlevel 1 exit /b 1

echo Kopiere statische Dateien...
robocopy resources dist\resources /E
copy /Y index.html dist\index.html
copy /Y style.css dist\style.css

echo Baue Server mit Maven...
cd ..\jdocuments-server

rem Falls Java 17 installiert ist, Pfad setzen (Beispiel für AdoptOpenJDK / Azul)
rem set JAVA_HOME=C:\Program Files\Java\jdk-17
echo JAVA_HOME=%JAVA_HOME%

mvn -Didea.version=2025.2 ^
    -Djansi.passthrough=true ^
    -Dstyle.color=always ^
    clean package
if errorlevel 1 exit /b 1

echo ▶ Starte Server...
cd target
java -jar jdocuments-server-1.0-SNAPSHOT-jar-with-dependencies.jar