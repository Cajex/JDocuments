echo Starte Java...

if exist target\jdocuments-server-1.0-SNAPSHOT-jar-with-dependencies.jar (
    echo JAR-Datei vorhanden
) else (
    echo JAR-Datei fehlt!
    exit /b 1
)
start "" java -jar target\jdocuments-server-1.0-SNAPSHOT-jar-with-dependencies.jar