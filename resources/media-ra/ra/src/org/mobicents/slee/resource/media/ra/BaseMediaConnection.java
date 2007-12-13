/*
 * BaseConnection.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ra;

import javax.media.protocol.DataSource;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class BaseMediaConnection implements MediaConnection {
    
    private DataSource outputStream;
    private DataSource inputStream;
    
    private MediaContext mediaContext;
    
    /** Creates a new instance of BaseConnection */
    public BaseMediaConnection() {
    }
    
    public DataSource getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(DataSource dataSource) {
        this.inputStream = dataSource;
    }
    
    public DataSource getOutputStream() {
        return outputStream;
    }
    
    public void setOutputStream(DataSource dataSource) {
        System.out.print("************ SET DATASOURCE **********");
        this.outputStream = dataSource;
    }
    
    public MediaContext getMediaContext() {
        return mediaContext;
    }
    
    public void setMediaContext(MediaContext mediaContext) {
        this.mediaContext = mediaContext;
    }
}
