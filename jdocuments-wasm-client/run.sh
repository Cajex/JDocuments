#!/bin/bash
set -e

echo "Baue WebAssembly Client..."
wasm-pack build --release --target web -d dist

echo "Kopiere statische Dateien..."
cp index.html dist/index.html
cp style.css dist/style.css

echo "Baue Server mit Maven..."
cd ../jdocuments-server

# shellcheck disable=SC2155
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "JAVA_HOME=$JAVA_HOME"

mvn -Didea.version=2025.2 \
    -Djansi.passthrough=true \
    -Dstyle.color=always \
    clean package

echo "▶ Starte Server..."
cd target
exec java -jar jdocuments-server-1.0-SNAPSHOT-jar-with-dependencies.jar