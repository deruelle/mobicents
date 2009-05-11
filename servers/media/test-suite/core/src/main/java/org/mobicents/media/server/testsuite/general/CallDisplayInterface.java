/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.testsuite.general;

import java.io.File;
import java.util.Vector;
import javax.sdp.Attribute;

/**
 *
 * @author baranowb
 */
public interface CallDisplayInterface {

    //Some methods to get requried fields.
    /**
     * Get client address - this is our address
     * @return
     */
    public String getLocalAddress();
    /**
     * Get server bind adddress - JBoss bind/MMS
     * @return
     */
    public String getRemoteAddress();
    public int getLocalPort();
    public int getRemotePort();
    public int getCallDuration();
    public int getCPS();
    /**
     * Get codec Vector used to create SDP
     * @return
     */
    public Vector<Attribute> getCodec();

    public void updateCallView();
    public File getDefaultDataDumpDirectory();
    //Have to add this here, code which habndled that is gone by accident...
    public String getFileURL();
}
