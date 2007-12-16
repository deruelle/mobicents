
package org.mobicents.slee.resource.parlay.fw;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.fw.application.SABeanListener;

/**
 *
 **/
public class SABeanListenerStub implements SABeanListener {
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory
        .getLog(SABeanListenerStub.class);

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanListener#terminateServiceAgreement(org.mobicents.slee.resource.parlay.fw.application.TerminateServiceAgreementEvent)
     */
    public void terminateServiceAgreement(TerminateServiceAgreementEvent event) {
       logger.debug("terminateServiceAgreement Received.");
        
    }

}
