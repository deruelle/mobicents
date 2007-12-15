Running the Example:

The code is tested to work with Mobicents 1.0.02.GA

REQUIREMENTS(deploy targets are not needed if deploy-all is called):
*set "jnpHost" variable in lib/build.xml so it is correct jb container bind address
*lib/smppra -> ant ra-deploy  = will deploy smpp ra.
there is posibiliy that some props in smppra/stage/ra-to/jar/**.xml file can be changed, if so
use package-ra target to repackage everything and than deploy.
*SMPPSim running with system_id and password (conf/*.props) set to values the same as in ra.xml file.
* ANT 1.7.0
CALL:
* make sure JBOSS_HOME is set and server is running.
ant deploy-all - this should deploy everything , refer to wiki
ant undeploy-all - this should undeploy everything - along with deps
ant deploy/undeploy - to play with only this service
ACCESS:
?
Also see:
http://wiki.java.net/bin/view/Communications/MobicentsSMPPRA


If this example is part of release use ant that has been shipped with mobicents. This concerns all ant calls.
/path_to_mobicents/tools/ant/ant -f /path_to_mobicents/examples/example/build.xml deploy

