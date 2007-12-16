
package org.mobicents.slee.resource.parlay.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.csapi.fw.fw_application.service_agreement.IpAppServiceAgreementManagement;
import org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagement;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.slee.resource.parlay.fw.ServiceAndToken;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.application.SABean;
import org.mobicents.slee.resource.parlay.fw.application.SABeanException;
import org.mobicents.slee.resource.parlay.fw.application.SABeanListener;

/**
 *
 **/
public class SABeanStub implements java.io.Serializable, SABean{
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(SABeanStub.class);

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#setServiceAgreementManagement(org.csapi.fw.fw_application.service_agreement.IpServiceAgreementManagement)
     */
    public void setServiceAgreementManagement(IpServiceAgreementManagement serviceAgreement) {
        
            logger.debug("setServiceAgreementManagement(IpServiceAgreementManagement) - start");

        
            logger.debug("setServiceAgreementManagement(IpServiceAgreementManagement) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#getServiceAgreementManagement()
     */
    public IpServiceAgreementManagement getServiceAgreementManagement() {
        
            logger.debug("getServiceAgreementManagement() - start");

            logger.debug("getServiceAgreementManagement() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#getAppServiceAgreementManagement()
     */
    public IpAppServiceAgreementManagement getAppServiceAgreementManagement() {
        
            logger.debug("getAppServiceAgreementManagement() - start");

            logger.debug("getAppServiceAgreementManagement() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#terminateServiceAgreement(java.lang.String, java.lang.String)
     */
    public void terminateServiceAgreement(String serviceToken, String terminationText) throws SABeanException {
        
            logger.debug("terminateServiceAgreement(String, String) - start");

        
            logger.debug("terminateServiceAgreement(String, String) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#selectAndSignServiceAgreement(java.lang.String, java.lang.String)
     */
    public ServiceAndToken selectAndSignServiceAgreement(String serviceID, String agreementText) throws SABeanException {
        
            logger.debug("selectAndSignServiceAgreement(String, String) - start");
        

        ServiceAndToken returnServiceAndToken = new ServiceAndToken(
            new IpServiceStub(),
            "ui123");
        
            logger.debug("selectAndSignServiceAgreement(String, String) - end");
        
        return returnServiceAndToken;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#getServiceAgreementMonitor()
     */
    public Object getServiceAgreementMonitor() {
        
            logger.debug("getServiceAgreementMonitor() - start");
        
            logger.debug("getServiceAgreementMonitor() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#removeSigningAlgorithm(java.lang.String)
     */
    public String removeSigningAlgorithm(String serviceToken) {
        
            logger.debug("removeSigningAlgorithm(String) - start");

            logger.debug("removeSigningAlgorithm(String) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#putServiceTokenSigningAlgorithm(java.lang.String, java.lang.String)
     */
    public void putServiceTokenSigningAlgorithm(String serviceToken, String signingAlgorithm) {
        
            logger.debug
                ("putServiceTokenSigningAlgorithm(String, String) - start");
        
        
            logger.debug("putServiceTokenSigningAlgorithm(String, String) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#setIsAgreementSigned(boolean)
     */
    public void setIsAgreementSigned(boolean value) {
        
            logger.debug("setIsAgreementSigned(boolean) - start");

        
            logger.debug("setIsAgreementSigned(boolean) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#verifyDigitalSignature(java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    public boolean verifyDigitalSignature(String text, String serviceToken, String signingAlgorithm, byte[] digitalSignature) {
        
            logger.debug("verifyDigitalSignature(String, String, String, byte[]) - start");

            logger.debug("verifyDigitalSignature(String, String, String, byte[]) - end");
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#generateDigitalSignature(java.lang.String, java.lang.String, java.lang.String)
     */
    public byte[] generateDigitalSignature(String text, String serviceToken, String signingAlgorithm) {
        
            logger.debug("generateDigitalSignature(String, String, String) - start");
        
            logger.debug("generateDigitalSignature(String, String, String) - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#cleanup()
     */
    public void cleanup() {
        
           logger.debug("cleanup() - start");
        
            logger.debug("cleanup() - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#getTSMBean()
     */
    public TSMBean getTSMBean() {
        
            logger.debug("getTSMBean() - start");

            logger.debug("getTSMBean() - end");
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABeanInterface#fireTerminateServiceAgreement(org.mobicents.slee.resource.parlay.fw.application.TerminateServiceAgreementEvent)
     */
    public void fireTerminateServiceAgreement(TerminateServiceAgreementEvent e) {
        
            logger.debug("fireTerminateServiceAgreement(TerminateServiceAgreementEvent) - start");

        
            logger.debug("fireTerminateServiceAgreement(TerminateServiceAgreementEvent) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABean#addSABeanListener(org.mobicents.slee.resource.parlay.fw.application.SABeanListener)
     */
    public void addSABeanListener(SABeanListener listener) {
        
            logger.debug("addSABeanListener(SABeanListener) - start");
        
       
        
        
            logger.debug("addSABeanListener(SABeanListener) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABean#removeSABeanListener(org.mobicents.slee.resource.parlay.fw.application.SABeanListener)
     */
    public void removeSABeanListener(SABeanListener listener) {
        
            logger.debug("removeSABeanListener(SABeanListener) - start");
        

        //  Auto-generated method stub
        
        
            logger.debug("removeSABeanListener(SABeanListener) - end");
        
    }

    /* (non-Javadoc)
     * @see org.mobicents.slee.resource.parlay.fw.application.SABean#initialise()
     */
    public void initialise() throws SABeanException {
        //  Auto-generated method stub
        
    }

}
