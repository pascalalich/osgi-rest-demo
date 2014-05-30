Server-side OSGi demo offering a REST service
=============================================
Intro
-----
This project is a sample application created for the Eclipse DemoCamp Luna 2014 in Hamburg/Braunschweig.

https://wiki.eclipse.org/Eclipse_DemoCamps_Luna_2014/Hamburg
https://wiki.eclipse.org/Eclipse_DemoCamps_Luna_2014/Braunschweig

Let it run!
-----------
Prerequisites:
* Maven 3.1
* Java JDK 1.7
* Tomcat 7

Step by step:

1. Install *Eclipse IDE for Java EE Developers*, at least 3.8
2. Download/clone this project *osgi-rest-demo* to an arbitrary location
3. Import all projects into a new workspace
  1. File / Import / Existing Maven Projects
  2. Import from directory *osgi-rest-demo*
  3. Select all Maven projects found
  4. Press *Finish*
4. Make sure a Java 1.7 Environment is configured
5. Build the project on the command line via `mvn clean package`
6. Refresh projects in workspace
7. Right click on project *de.alichs.osgi.restdemo.webapp* / Run as / Run on Server
  * If not yet done, follow the instructions to configure your Tomcat 7
8. Launch [http://localhost:8080/de.alichs.osgi.restdemo.webapp/api](http://localhost:8080/de.alichs.osgi.restdemo.webapp/api) for investigating the REST API
9. Launch [http://localhost:8080/de.alichs.osgi.restdemo.webapp/system/console/](http://localhost:8080/de.alichs.osgi.restdemo.webapp/system/console/) for accessing the OSGi web console (User: admin, password: admin)

Credits
-------
* Pascal Alich, Software Engineer
