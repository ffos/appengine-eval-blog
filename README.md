# App engine - java

This is a blog using Objectify + Jersey + Giuce for appengine development.
It is still a work in progress, and was implemented to evaluate the google cloud platform for personal reasons.

Since the platform is google's app-engine, there are some limitations and necessary workarounds. To view those that I encountered, check [AppEngineWorkarounds.md](AppEngineWorkarounds.md)


## API calls sample
See [Sample.md](Sample.md)

## Running this app

To run this project. Do the following, replacing file-paths and other specific configuration where necessary:
* Have correct appengine settings in src/main/webapp/WEB-INF/appengine-web.xml
* Update 'appengine' plugin settings in build.gradle
* Install 'Google' eclipse plugin
* Import this project as a gradle project in eclipse
* Make sure your in your project properties:

  * Java compiler version is set to `1.7`
  * Build path -> Default output folder =  `appengine-java/build/exploded-app/WEB-INF/classes`
  * Build path -> Libraries has "App Engine SDK" added
  * Build path -> Order and Export has "App Engine SDK" BEFORE "Project and External Deps" (gradle deps)
  * Google -> AppEngine: Has "Use App Engine" Checked
  * Google -> AppEngine: Has "Use Data Nucleus" Unchecked
  * Google -> AppEngine -> Web Application: Has "This project has a war directory" Checked
  * Google -> AppEngine -> Web Application: Has "War Directory" = build/exploded-app
  * Google -> AppEngine -> Web Application: Has "Launch and Deploy from this Directory" Checked

* In "Run Configurations..." Create a new run configuration with these settings:
  * Main Tab:
    * Main class:	com.google.appengine.tools.development.DevAppServerMain
  * Server Tab:
    * Run Built In Server: 	Checked
    *	Port: 			8888

  * Arguments Tab:
    * Use the -XstartOn...SWT	Checked
    * Program Arguments (replace path):	`--port=8888 /path/to/project/build/exploded-app`
    * VM Arguments (replace path):		`-Xmx512m -javaagent:/Users/biswa/.p2/pool/plugins/com.google.appengine.eclipse.sdkbundle_1.9.34/appengine-java-sdk-1.9.34/lib/agent/appengine-agent.jar -XstartOnFirstThread -Xbootclasspath/p:/Users/biswa/.p2/pool/plugins/com.google.appengine.eclipse.sdkbundle_1.9.34/appengine-java-sdk-1.9.34/lib/override/appengine-dev-jdk-overrides.jar`


# How to run locally
* Make changes, then from the command line run `gradle build`
  * This will create folder 'build' and generate 'exploded-app' there and dump contents there
* Use the run configuration to start the dev server
* Use gradle test task to run tests 

# How to deploy
* Run `gradle appengineUpdate`, and when prompted for password, log-into google, copy the token and paste it on the command line (Standard In of the gradle process in console)



# Gradle plugin
From: https://github.com/GoogleCloudPlatform/gradle-appengine-plugin

# Deploying manually
See: https://cloud.google.com/appengine/docs/java/tools/uploadinganapp


## Issues with AppEngine
* See: [AppEngineWorkarounds.md](AppEngineWorkarounds.md)




