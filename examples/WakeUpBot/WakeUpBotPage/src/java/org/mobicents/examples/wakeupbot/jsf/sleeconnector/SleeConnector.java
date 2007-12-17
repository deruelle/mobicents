/*
 * SleeConnector.java
 *
 * Created on 25 luty 2006, 09:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.examples.wakeupbot.jsf.sleeconnector;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.EventTypeID;
import javax.slee.UnrecognizedActivityException;
import javax.slee.UnrecognizedEventException;
import javax.slee.connection.*;
import javax.slee.resource.ResourceException;
import org.mobicents.slee.connector.server.*;
import org.mobicents.examples.wakeupbot.events.WakeUpRequestEvent;
/**
 *
 * @author Administrator
 */
public class SleeConnector {
    private SleeConnection connection=null;
    private RemoteSleeService service=null;
    /** Creates a new instance of SleeConnector */
    public SleeConnector() {
    }
    public void sendData(long differenceTimieMillis,String UID) {
        
        // if(connection==null) {
        try{
            Properties properties = new Properties();
            //JNDI lookup properties
            properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
            String tmpIP=getIpFromProps();
            if(tmpIP==null)
                tmpIP="192.168.1.100";
            
            properties.put("java.naming.provider.url", "jnp://"+tmpIP+":1099");
            InitialContext ctx=new InitialContext(properties);
            
            //SleeConnectionFactory factory =(SleeConnectionFactory)ctx.lookup("java:comp/env/slee/MySleeConnectionFactory");
            // Obtain a connection to the SLEE from the factory
            //SleeConnection connection = factory.getConnection();
            service=(RemoteSleeService)ctx.lookup("/SleeService");
        }catch(NamingException ne) {
            ne.printStackTrace();
        }catch(Exception E) {
            E.printStackTrace();
        }
        // }
        if(service!=null) {
            try {
                // Locate the event type, same data that WakeUpRequest-event-jar.xml contains
                EventTypeID requestType = service.getEventTypeID("org.mobicents.examples.wakeupbot.events.WakeUpRequestEvent","mobicents","0.1");
                WakeUpRequestEvent request=new WakeUpRequestEvent();
                Date date=Calendar.getInstance().getTime();
                date.setTime(date.getTime()+differenceTimieMillis);
                request.setUID(UID);
                request.setTimeMillisDifference(differenceTimieMillis);
                request.setDate(date);
                // Fire an asynchronous event
                ExternalActivityHandle handle = service.createActivityHandle();
                Address address=new Address(AddressPlan.SMTP,UID);
                service.fireEvent(request, requestType, handle, address);
                
                //connection.endExternalActivity(handle);
            }catch(RemoteException RE) {
                System.out.println("REMOTE EXCEPTION!!!");
                RE.printStackTrace();
            }
        }       
    }
    private String getIpFromProps()
    {
        Properties props=new Properties();
		try
		{
			props.load(getClass().getResourceAsStream("slee-connector.properties"));
                        return props.getProperty("org.mobicents.examples.jndi.ip");
                }catch(IOException IOE)
                {
                    System.out.println("FAILED TO LOAD PROPERTIES FILE: slee-connector.properties!!!");
                }
        return null;
    }
}
