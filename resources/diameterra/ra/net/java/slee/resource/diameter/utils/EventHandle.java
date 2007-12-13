package net.java.slee.resource.diameter.utils;

public class EventHandle {

    private boolean request=true;
    private int commandCode=-1;
    
    
    
    public EventHandle(boolean req,int commCode)
    {
        request=req;
        commandCode=commCode;
    }
    
    public boolean equals(Object o)
    {
        
        if( !(o instanceof EventHandle))
            return false;
        
        EventHandle EH=(EventHandle)o;
        if(EH.request!=this.request)
            return false;
        if(EH.commandCode!=this.commandCode)
            return false;
        
        return true;
    }
    public int hashCode()
    {
        int tmp=(request?0:1);
        //IT SHOULD BE ENOUGH 
        return commandCode*10+tmp;
    }
    public String toString()
    {
        return super.toString()+"(CODE:"+commandCode+"--REQ: "+request+")";
    }
}
