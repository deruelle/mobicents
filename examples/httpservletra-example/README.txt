This example implements a simple service to demonstrate the use of Http-Servlet-RA


Running the example:

This example is composed of three parts 
	i)   /slee - which has the service (SBB)
	ii)  /tests - which has the unit test that makes use of HttpClient to send HttpRequest to RA
	iii) /web - which is a simple web application and used to send the HttpRequest to RA via browser
	
To deploy the example call
ant deploy-all
    - this deploys the example along with all dpes - http-servlet-ra

Wed :- 
After deployment point your browser to http://yourhost:8080/httpservletra/ 
Click on 'Post' or 'Get' and you should see the Response composed by SBB on your browser

Test:- 
To execute the HttpServletRATest JUnit Test call 'ant test' which will produce the test results on console. 
Call 'ant test-html' which will produce the HTMl output of test results in httpservletra-example/tests/reports
Junit Test uses httpclient (http://jakarta.apache.org/commons/httpclient/) to create HTTP Requests

The difference of Junit Test from Web is that JUnit Test is capable of testing all the Methods of HTTP 
for example PUT, OPTIONS, TRACE etc. While web page only submits GET and POST request

To stop and undeploy example application with dependencies
ant undeploy-all
      - this undeploys example application and http-servlet-ra as well
      
ant deploy/undeploy will only deploy/undeploy the example and will not touch the RA      
           	


Other Notes:
* You may need to set "jnpHost" variable in lib/build.xml so it points to the correct server bind address. 
    There is no need to do that if the server is running with default parameters.

* lib/http-servlet-ra -> ant ra-deploy  = will deploy http-servlet-ra - it pics up jboss bind address.
                         ant deploy-with-servlet = will deploy http-servlet-ra along with mobicents.war. mobicents.war is simple web application that acts as 
                                                   interface between the HttpServlet and RA
* ANT 1.7.0 is required

If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy


Make sure to set the system variable JBOSS_HOME to point to the JBoss AS instance where Mobicents is deployed. 
Also see: http://groups.google.com/group/mobicents-public/web/mobicents-http-servlet-ra

