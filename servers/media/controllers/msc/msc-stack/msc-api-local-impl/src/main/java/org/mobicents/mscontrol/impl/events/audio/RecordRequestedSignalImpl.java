/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.mscontrol.impl.events.audio;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.audio.RecordRequestedSignal;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.audio.MsRecordRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.mscontrol.impl.events.BaseRequestedSignal;

/**
 *
 * @author Oleg Kulikov
 */
public class RecordRequestedSignalImpl extends BaseRequestedSignal implements MsRecordRequestedSignal {

    private String file;
    private int recordTime = 60;

    public MsEventIdentifier getID() {
        return MsAudio.RECORD;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
	public int getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int timeInSec) {
		this.recordTime = timeInSec;		
	}    

    @Override
    public RequestedSignal convert() {
        EventFactory factory = new EventFactory();
        RecordRequestedSignal signal = (RecordRequestedSignal) factory.createRequestedSignal(
                MsAudio.RECORD.getPackageName(), MsAudio.RECORD.getEventName());
        signal.setFile(file);
        signal.setRecordTime(recordTime);
        return signal;
    }


}
