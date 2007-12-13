package test;
import static dk.i1.diameter.ProtocolConstants.DI_AUTH_SESSION_STATE_NO_STALE_MAINTAINED;
import static dk.i1.diameter.ProtocolConstants.DI_AUTH_SESSION_STATE_STALE_MAINTAINED;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import net.java.slee.resource.diameter.utils.EventHandle;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.InvalidAVPLengthException;
import dk.i1.diameter.Message;
import dk.i1.diameter.ProtocolConstants;
import dk.i1.diameter.node.Capability;
import dk.i1.diameter.node.InvalidSettingException;
import dk.i1.diameter.node.NodeManager;
import dk.i1.diameter.node.NodeSettings;
public class Test {
    private static String hostID="eclipso.mobicents.org";
    private static String realm="mobicents.org";
    private static int port=3868;
    public Test(NodeSettings ns)
    {
       
    }
    public Test(){}
   public static void main(String[] arg) throws IOException
   {
       /*
     Capability cap=new Capability();
     cap.addAuthApp(ProtocolConstants.DIAMETER_APPLICATION_COMMON);
     cap.addAcctApp(ProtocolConstants.DIAMETER_APPLICATION_COMMON);

     NodeSettings node_settings;
        try {
            node_settings  = new NodeSettings(
                    hostID, realm,
                99999, //vendor-id
                cap,
                port,
                "cc_test_server", 0x01000000);
        } catch (InvalidSettingException e) {
            System.out.println(e.toString());
            return;
        }
        
        Test t=new Test(node_settings);
        t.start();
        System.out.println("Hit enter to terminate server");
        System.in.read();
        
        t.stop();
        */
      /* 
       HashMap map=new HashMap();
       map.put("STRING1","VALUE1");
       map.put("STRING2","VALUE2");
       map.put("STRING3","VALUE3");
       map.put("STRING4","VALUE4");
       
       
       
       Set set=map.entrySet();
       
       Iterator it = set.iterator();
       while(it.hasNext())
       {
           Map.Entry ent=(Entry) it.next();
           
       }
       
       doMe(new Integer(1));*/
       
       
       HashMap<EventHandle,String> map=new HashMap<EventHandle,String>();
       map.put(new EventHandle(false,1),"ALA");
       map.put(new EventHandle(true,1),"KALA");
       
       System.out.println(map);
       
       String tmp="307R";
       System.out.println(tmp.substring(0,tmp.length()-1));
       
       //new Test().doMe();
       int authSessionState=0;
       System.out.println(DI_AUTH_SESSION_STATE_NO_STALE_MAINTAINED);
       System.out.println(DI_AUTH_SESSION_STATE_STALE_MAINTAINED);
       System.out.println(authSessionState!=DI_AUTH_SESSION_STATE_NO_STALE_MAINTAINED);
       System.out.println(authSessionState!=DI_AUTH_SESSION_STATE_STALE_MAINTAINED);
       if(authSessionState!=DI_AUTH_SESSION_STATE_NO_STALE_MAINTAINED && authSessionState!=DI_AUTH_SESSION_STATE_STALE_MAINTAINED)
           throw new IllegalArgumentException("<><><> authSessionState MUST BE EQUAL TO \"dk.i1.diameter.ProtocolConstants.DI_AUTH_SESSION_STATE_NO_STALE_MAINTAINED\" OR \"dk.i1.diameter.ProtocolConstants.DI_AUTH_SESSION_STATE_STALE_MAINTAINED\" <><><>");
       
       
       AVP_Unsigned32 avp=new AVP_Unsigned32(ProtocolConstants.DI_VENDOR_ID,131244);
       Message msg=new Message();
       msg.add(avp);
       Message req=new Message();
       req.add(msg.find(ProtocolConstants.DI_VENDOR_ID));
       AVP plain=req.find(ProtocolConstants.DI_VENDOR_ID);
       avp=null;
       try {
        avp=new AVP_Unsigned32(plain);
    } catch (InvalidAVPLengthException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    System.out.println("====== "+avp.queryValue()+" =====");
   }
   
   
   public static void doMe(Number num)
   {
       System.out.println("DOING NUMBER");
   }
   public static void doMe(Integer inter)
   {
       System.out.println("DOING SUBCALSS");
   }
   public void doMe()
   {
       HashMap<EventHandle,String> associationMap=new HashMap<EventHandle,String>();
       Properties props=new Properties();
       try {
           Class cls=getClass();
           System.out.println(cls);
           InputStream str=cls.getResourceAsStream("MessagesMap.properties");
           System.out.println(str);
           props.load(str);
       } catch (IOException IOE) {
           System.out.println("^^^^   FAILED TO LOAD: MessageMap.properties ^^^^^");
           
       }
       Iterator it=props.keySet().iterator();
       while(it.hasNext())
       {
           String key=(String)it.next();
           boolean req=key.endsWith("R");
           int code=Integer.parseInt(key.substring(0,key.length()-1));
           associationMap.put(new EventHandle(req,code),props.getProperty(key));
       }
       System.out.println(associationMap);
   }
   
}
