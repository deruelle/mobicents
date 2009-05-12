/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.testsuite.general.ann;

import org.mobicents.media.server.testsuite.general.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
            AbstractCall call = new AnnCall(this, super.callDisplay.getFileURL());
            super.timeGuard.schedule(new AnnCallTimeOutTask(call), super.callDisplay.getCallDuration(), TimeUnit.MILLISECONDS);
            return call;
        } catch (IOException ex) {
            Logger.getLogger(AnnouncementTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    protected class AnnCallTimeOutTask implements Runnable
    {
        private AbstractCall call;

        public AnnCallTimeOutTask(AbstractCall call)
        {
            this.call = call;
        }
        public void run() {
            call.timeOut();
        }
        
        
    }
}
