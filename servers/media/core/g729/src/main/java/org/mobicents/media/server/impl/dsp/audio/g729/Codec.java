/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.impl.dsp.audio.g729;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class Codec extends ServiceMBeanSupport implements CodecMBean {

    private String jndiName;
    private G729Factory codecFactory;
    private Logger logger = Logger.getLogger(Codec.class);
    
    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) throws NamingException {
        String oldName = this.jndiName;
        this.jndiName = jndiName;
        
        if (this.getState() == STARTED) {
            unbind(oldName);
            try {
                rebind();
            } catch (NamingException e) {
                NamingException ne = new NamingException("Failed to update JNDI name");
                ne.setRootCause(e);
                throw ne;
            }
        }
    }

    public Format[] getInputFormats() {
        return new AudioFormat[0];
    }

    public Format[] getOutputFormats() {
        return new AudioFormat[0];
    }

    @Override
    public void startService() throws Exception {
        logger.info("Deploying codec: G729");
        codecFactory = new G729Factory();
        rebind();
    }    
    
    @Override
    public void stopService() {
        unbind(jndiName);
        logger.info("Undeployed codec: G729 ");
    }
    
    private void rebind() throws NamingException {
        Context ctx = new InitialContext();
        String tokens[] = jndiName.split("/");
        
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].trim().length() > 0) {
                try {
                    ctx = (Context)ctx.lookup(tokens[i]);
                } catch (NamingException  e) {
                    ctx = ctx.createSubcontext(tokens[i]);
                }
            }
        }
        
        ctx.bind(tokens[tokens.length - 1], codecFactory);
        logger.info("Deployed codec: G729, jndi=" + this.jndiName);
    }
    
    /**
     * Unbounds object under specified name.
     *
     * @param jndiName the JNDI name of the object to be unbound.
     */
    private void unbind(String jndiName) {
        try {
            InitialContext initialContext = new InitialContext();
            initialContext.unbind(jndiName);
        } catch (NamingException e) {
            //ignore the error
        }
    }

}
