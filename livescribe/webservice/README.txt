Livescribe 'WebService' Maven Archetype

This project is a Maven 'archetype' project.  It's purpose is to create a new
Maven 'archetype' that can be used to quickly generate "Web Service" projects.  

--------------------------------------------------
USAGE:

    $ newService.sh < groupId > < artifactId >"

       groupId - The Maven group ID of the new Web Service."
    artifactId - The Maven artifact ID of the new Web Service."

--------------------------------------------------

It will create a new directory named with the given 'artifactId' parameter.
In this directory, the Maven project structure will be created, which will
include:

src/main/java
src/main/java/com/livescribe/App.java		// Stub, needed for Maven archetype plugin to work.
src/main/resources
src/main/resources/app.properties
src/main/resources/application-context.xml
src/main/resources/application-servlet.xml
src/main/resources/log4j.xml
src/main/webapp
src/main/webapp/WEB-INF
src/main/webapp/WEB-INF/web.xml
src/test/java
src/test/java/BaseTest.java					//	Base class for all unit tests.
src/test/resources/log4j.xml
pom.xml										//	Common dependencies and plugins are included.

Some changes will need to be made to the POM and XML configuration files once
the project has been created.
