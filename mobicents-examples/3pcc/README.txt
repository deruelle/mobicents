This example application consists of a sip service (3pcc_sip_slee) and a web app (3pcc_web) that integrates with the sip service.

The fastest way to run the (web) application is to:

First start JBoss (which must have Mobicents with the sip RA installed)
Then issue ant deploy-3pcc_web_app-(hot) (in the 3pcc project)

Note that you need to set the JBOSS_HOME environment variable for the ant targets to work.

Point you browser to http://localhost:8080/thirdpcc 

See READMEs in respective project for details.
 