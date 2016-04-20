# App Engine Workarounds

The following were the workarounds introduced to play nice with appengine

1. Based on identical issue faced as described here: http://stackoverflow.com/questions/31354363/google-appengine-with-jersey-2-1x-works-fine-in-dev-server-but-not-in-appengine?lq=1, which was caused by attempting to inject HttpServletRequest a jersey container request filter, the solution was as referred in stackoverflow to create an HTTP filter, and set necessary attributes from there. The approach taken in this code is to create that filter, inject a bean as a request attribute, and access it in ContainerRequestFilter using requestContext.getProperty(name)

1. Since app-engine doesn't allow temporary file creation, and Jersey multipart feature by default creates temp files, we need to tell jersey to disable temp file creation. We do this by:
  1. Creating resource: `src/main/resources/jersey-multipart-config.properties`
  2. Set property: `jersey.config.multipart.bufferThreshold=-1`

1. Jersey specific 4XX errors are shown as container HTML errors instead of json. They are fixed by adding the following lines to `web.xml`:
```
<init-param>
  <param-name>jersey.config.server.response.setStatusOverSendError</param-name>
  <param-value>true</param-value>
</init-param>         
```
1. Bean validator has to be a specific version, along with a different implementation of Unified Expression Language (EL). The following diff shows the fixed dependencies:

```
+## Issues with AppEngine
+* https://code.google.com/p/googleappengine/issues/detail?id=9634
+
+


diff --git a/build.gradle b/build.gradle
index e5d772d..a3e6e60 100644
--- a/build.gradle
+++ b/build.gradle
@@ -53,9 +53,13 @@ dependencies{
   providedCompile "javax.servlet:servlet-api:2.5"

   runtime "org.ow2.asm:asm:4.2"
-  runtime "org.hibernate:hibernate-validator:5.1.3.Final"
+  compile "org.hibernate:hibernate-validator:4.3.1.Final"
   runtime "javax.el:javax.el-api:2.2.5"
-  runtime "org.glassfish.web:javax.el:2.2.6"
+  runtime "de.odysseus.juel:juel-api:2.2.7"
+  runtime "de.odysseus.juel:juel-impl:2.2.7"
+  runtime "de.odysseus.juel:juel-spi:2.2.7"
+
+//  runtime "org.glassfish.web:javax.el:2.2.6"
```

1. Freemarker template cannot be used. Even though a solution is specified:
	* http://www.mkyong.com/google-app-engine/javax-swing-tree-treenode-is-a-restricted-class/
		* This does not work because of reasons in https://solveme.wordpress.com/2009/12/25/freemarker-sucks-it-has-dependency-on-javax-swing/

1. We must remember to create class-path based template object factory (in this case in Mustache)
