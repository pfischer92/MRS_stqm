This project contains the sources of and is the common base for both SWC and STQM.

Some remarks about running build in Jenkins with JDK11 (and in Docker)

# assuming Jenkins installed and configured with the default plugins

# install latest jdk 11
open bash shell
docker exec -u root -it <your Jenkins container ID> /bin/bash

install jdk11 manually or with a package manager
Export JAVA_HOME and set PATH

# configure Jenkins JDK
configure the JDK installation in Jenkins
uncheck the "automatic installation" and set the JAVA_HOME to the jdk installation dir

# Jenkins Plugings
Add Jacoco Plugin 

# updated POM file
1. JavaFX
JavaFX is no longer bundled with the JDK; add the JavaFX lib as a dependency to the POM file.
add JavaFX Controls and JavaFX FXML dependencies to POM file

2. Surefire plugin
Switch to new version and some extra configuration to avoid crash



