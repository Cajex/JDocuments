cd target || exit
mvn clean package
java -jar jdocuments-server-1.0-SNAPSHOT-jar-with-dependencies.jar