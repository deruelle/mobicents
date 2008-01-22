/*
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
package org.mobicents.media.server.impl.conference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.jmf.splitter.MediaSplitter;

/**
 *
 * @author Oleg Kulikov
 */
public class LocalSplitter {

    private String id;
    private Map streams = Collections.synchronizedMap(new HashMap());
    private MediaSplitter splitter = new MediaSplitter();
    private Logger logger = Logger.getLogger(LocalSplitter.class);

    public LocalSplitter(String id) {
        this.id = id;
    }

    public void setInputStream(PushBufferStream pushStream) {
        splitter.setInputStream(pushStream);
    }

    public PushBufferStream newBranch(String id) {
        PushBufferStream branch = splitter.newBranch();
        streams.put(id, branch);
        logger.info("id=" + this.id + ", created new branch for connection id = " +
                id + ", branches=" + splitter.getSize());
        return branch;
    }

    public PushBufferStream remove(String id) {
        PushBufferStream pushStream = (PushBufferStream) streams.remove(id);
        if (pushStream != null) {
            splitter.closeBranch(pushStream);
            logger.info("id=" + this.id + ", removed branch for connection id = " +
                    id + ", branches=" + splitter.getSize());
        }
        return pushStream;
    }

    public void close() {
        splitter.close();
    }

    public String toString() {
        return "LocalSplitter[" + id + "]";
    }
}
