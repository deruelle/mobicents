/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.testsuite.general.ann;

import org.mobicents.media.server.testsuite.general.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author baranowb
 */
public class AnnouncementTest extends AbstractTestCase{

    @Override
    public AbstractCall getNewCall() {
        try {

            return new AnnCall(this, super.callDisplay.getFileURL());
        } catch (IOException ex) {
            Logger.getLogger(AnnouncementTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
