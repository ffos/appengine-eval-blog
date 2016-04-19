# App engine - java - blog

To run this project. Do the following.
* Run `gradle eclipse`
* Import gradle into eclipse
* Make sure your project properties is for java compiler version 1.7
* Make sure your project properties, java build path output is `appengine-java-blog/build/exploded-app/WEB-INF/classes`
* Create a new 'Google' plugin Run configuration with the following settings:
```
Main Tab:
	Main class:	com.google.appengine.tools.development.DevAppServerMain

Server Tab:
	Run Built In Server: 	Checked
	Port: 			8888

Arguments Tab:
	Use the -XstartOn...SWT	Checked
	Program Arguments:	--port=8888 /Users/biswa/code/code/eclipse/appengine-java-blog/build/exploded-app
	VM Arguments:		-Xmx512m -javaagent:/Users/biswa/.p2/pool/plugins/com.google.appengine.eclipse.sdkbundle_1.9.34/appengine-java-sdk-1.9.34/lib/agent/appengine-agent.jar -XstartOnFirstThread -Xbootclasspath/p:/Users/biswa/.p2/pool/plugins/com.google.appengine.eclipse.sdkbundle_1.9.34/appengine-java-sdk-1.9.34/lib/override/appengine-dev-jdk-overrides.jar
	

```
* Make changes, then from the command line do `gradle build`
** This will create folder 'build' and generate 'exploded-app' there
* Use the run configuration to start the dev server


