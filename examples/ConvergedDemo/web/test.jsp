<%@page contentType="text/html"
   import="java.util.*,javax.naming.*,
   java.math.BigDecimal,
   javax.slee.*,javax.slee.connection.*,
   org.mobicents.slee.connector.server.RemoteSleeService,
   org.mobicents.slee.service.events.CustomEvent"
%>
<html>
<head>
   <title>MBean Inspector</title>
   <link rel="stylesheet" href="style_master.css" type="text/css">
   <meta http-equiv="cache-control" content="no-cache">
</head>
<body>

<%
RemoteSleeService service=null;
System.out.println("************************** test.jsp -> Start **************************");
	try{
            Properties properties = new Properties();
            //JNDI lookup properties
            properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
            String tmpIP="127.0.0.1";
            
            properties.put("java.naming.provider.url", "jnp://"+tmpIP+":1099");
            InitialContext ctx=new InitialContext(properties);
		service=(RemoteSleeService)ctx.lookup("/SleeService");
		System.out.println("SLEE Object received = "+service);

		CustomEvent customEvent = new CustomEvent(100001, new BigDecimal(45.66), "Amit Bhayani", "sip:abhayani@192.168.0.101:5059");
		
		EventTypeID requestType = service.getEventTypeID("org.mobicents.slee.service.dvddemo.ORDER_PLACED","org.mobicents","1.0");

		// Fire an asynchronous event
                ExternalActivityHandle handle = service.createActivityHandle();

		service.fireEvent(customEvent, requestType, handle, null);

	}
	catch(Exception ex){
		System.out.println("Exception while lookup of SLEE");
		ex.printStackTrace();
	}
System.out.println("************************** test.jsp -> End **************************");
%>


Blah blah blah
</body>
</html>
