README.TXT for Resource Adaptor Framework RAFrame and BounceSbb
---------------------------------------------------------------

My configuration:
 Windows XP SP2
 JBoss 3.2.6 configured as found on www.mobicents.org
 Java SDK 1.5.0
 Ant
 
What you need to do:
 * Download Project_RAFrame.zip from 
   http://www.maretzke.com/pub/howtos/mobicents_ra/project_raframe.zip
   (or alternatively http://www.maretzke.de/com/pub/howtos/mobicents_ra/project_raframe.zip)
 
 * Download Project_RASbb.zip from
   http://www.maretzke.com/pub/howtos/mobicents_ra/project_rasbb.zip
   (or alternatively http://www.maretzke.de/com/pub/howtos/mobicents_ra/project_rasbb.zip)
 
 * Unzip the files
 
 * Change into Project_RAFrame directory and run "ant"
   In the directory "stage" should be two files (at least):
   	
   	1) raframe-ra-type.jar
   	
   	2) raframe-local-ra.jar
   
   These are the deployable units for the resource adaptor and the resource adaptor type.
   
 * Start JBoss 3.2.6 prior to installing the RA.
   Change into Project_RAFrame\bin and execute DeployRAFRA.bat 
   (I encountered some problems on my system and found mywin_DeployRAFRA.bat working ...)
   
   Now, the resource adaptor should be installed properly!
 
   Your screen might look like mine:

	E:\Programming\Project_RAFrame\bin>mywin_DeployRAFRA.bat
	"Deploying RAFrame RA type"
	DeployableUnitID[0]
	"Deploying RAFrame RA"
	DeployableUnitID[1]
	"Creating RAFrame RA entity, entity name=RAFrameRA"
	No response
	"Activating RAFrame RA entity, entity name=RAFrameRA"
	No response
	"Create RAFrame RA entity link, entity name=RAFrameRA"
	No response
	E:\Programming\Project_RAFrame\bin>
	
 * So far, so good. Now change into Project_RASbb directory and run "ant".
   In the directory "dist" there should occur a file named "bouncesbb-service.jar"
   This is the deployable unit for the service.
   
 * Change into Project_RASbb\bin and execute DeployBounceSbb.bat.
   (I encountered some problems on my system and found mywin_DeployBounceSbb.bat working ...)
   
   Now, the service should be installed properly!
   
   Your screen might look like mine:
   
	E:\Programming\Project_RASbb\bin>mywin_DeployBounceSbb.bat
	"Deploying Bounce Proxy Service"
	DeployableUnitID[2]
	"Activating Bounce Sbb Service"
	No response
	E:\Programming\Project_RASbb\bin>
	
 * Furthermore, JBoss console should not be overwhelmed with exceptions ... :)
 
 * Now, change back to Project_RAFrame\bin and execute swingClient.bat
 
   A small GUI should start up. You will see 6 buttons and two text areas. The bottom text area is
   for typing commands. The send button will send the typed text to the resource adaptor (which is
   basically nothing more than a TCP/IP server socket waiting for incoming messages).
   The six buttons help to construct valid commands faster.
   
   A valid command looks like: 
   	
   	100 INIT 
   	100 ANY
   	100 END
   	
   100 determines the ID of the "dialog" or session. The command INIT starts a session. ANY represents
   following commands and END finishes the whole session.
   
   Done!
   
   
   Please send any feedback, criticism or whatever you want to send ... back to me!