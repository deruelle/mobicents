package org.mobicents.media.server.ctrl.mgcp.test.crcx;

import org.mobicents.media.server.ctrl.mgcp.test.CA;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 *
 */
public class CrcxTestCase extends MgcpMicrocontainerTest {
        private CA ca;

        public CrcxTestCase(String name) {
                super(name);
        } 

        public void testCRCX() throws Exception {

                ca = new CA(caProvider, (JainMgcpStackProviderImpl) mgcpServerStack.getMgcpProvider());
                ca.sendCreateConnection();

                waitForMessage();
        }

        public void tearDown() {
                try {
                        super.tearDown();
                } catch (Exception ex) {

                }
                this.ca.checkState();

        }

}
