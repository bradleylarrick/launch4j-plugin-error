# Site Report Errors caused by launchj4-maven-plugin

This simple project illustrates the reports error caused by the **launch4j-maven-plugin**.
Executing the command:
```
mvn clean test site
```
will generate the expected project reports via the **maven-project-info-reports-plugin**,
including the **Plugin Management** and **Plugins** reports listing all the plugins defined
for the project.

However, running the command:
```
mvn clean package site
```
which executes the **launch4j-maven-plugin**, results in **Plugin Management** and **Plugins**
reports that show all the plugins with the **GroupId**, **ArtifactId** and **Version** of this
project.
