package org.mobicents.media.server.impl.jmx.enp.ivr;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.enp.ivr.IVREndpointImpl;
import org.mobicents.media.server.impl.events.dtmf.DTMFMode;
import org.mobicents.media.server.impl.jmx.TrunkManagement;
import org.mobicents.media.server.spi.Endpoint;

public class IVRTrunkManagement extends TrunkManagement implements IVRTrunkManagementMBean {

    private transient Logger logger = Logger.getLogger(IVREndpointManagement.class);
    private String recordDir;
    private String mediaType;
    private String dtmfMode;
    

    /** Creates a new instance of IVREndpointManagement */
    public IVRTrunkManagement() {
    }

    public String getRecordDir() {
        return recordDir;
    }

    public void setRecordDir(String recordDir) throws NamingException {
        this.recordDir = recordDir;
		// if (this.getState() == STARTED) {
		// InitialContext ic = new InitialContext();
		// for (int i = 0; i < this.getChannels(); i++) {
		// IVREndpointImpl endpoint = (IVREndpointImpl)
		// ic.lookup(this.getJndiName() +"/" + i);
		// endpoint.setRecordDir(recordDir);
		// }
		//        }
    }

    public String getDtmfMode() {
        return dtmfMode;
    }

    public void setDtmfMode(String mode) throws NamingException {
        this.dtmfMode = mode;
		// if (this.getState() == STARTED) {
		// InitialContext ic = new InitialContext();
		// for (int i = 0; i < this.getChannels(); i++) {
		// IVREndpointImpl endpoint = (IVREndpointImpl)
		// ic.lookup(this.getJndiName() +"/" + i);
		// endpoint.setDtmfMode(DTMFMode.valueOf(dtmfMode));
		// }
		//        }
    }
    
    private int getModeCode(String modeName) {
        if (modeName.equalsIgnoreCase("INBAND")) {
            return 0;
        } else if (modeName.equalsIgnoreCase("RFC2833")) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public Endpoint createEndpoint(String name) throws Exception {
        IVREndpointImpl endpoint = new IVREndpointImpl(name);
        endpoint.setRecordDir(this.getRecordDir());
        endpoint.setDtmfMode(DTMFMode.valueOf(dtmfMode));
        return endpoint;
    }


}
