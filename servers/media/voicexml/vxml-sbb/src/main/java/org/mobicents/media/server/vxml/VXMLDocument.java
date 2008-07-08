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

package org.mobicents.media.server.vxml;

import java.io.InputStream;
import org.mobicents.mscontrol.MsConnection;

/**
 * Represents VoiceXML document.
 * 
 * A VoiceXML document (or a set of related documents called an application) 
 * forms a conversational finite state machine. The user is always in one 
 * conversational state, or dialog, at a time. Each dialog determines the next 
 * dialog to transition to. Transitions are specified using URIs, which define 
 * the next document and dialog to use. If a URI does not refer to a document, 
 * the current document is assumed. If it does not refer to a dialog, 
 * the first dialog in the document is assumed. Execution is terminated when 
 * a dialog does not specify a successor, or if it has an element that 
 * explicitly exits the conversation.
 *
 *
 * @author Oleg Kulikov
 */
public interface VXMLDocument extends VXMLItem {
    /**
     * Starts executing of the specified document.
     * 
     * @param url the url to the voice xml document.
     * @param endpoint the endpoint which will interpret document.
     */
    public void start(String url, String endpoint);
    
    /**
     * Forwards interpreter to the specified dialog identifier.
     * 
     * @param dialogID the identifier of the dialog
     */
    public void forward(String dialogID);
    
    /**
     * Associated connection reference.
     * 
     * @return the connection reference.
     */
    public MsConnection getConnection();
    
    /**
     * Reference to base URI of the current document.
     * 
     * @return the base URI
     */
    public String getBaseURL();
    
    public void load(String uri, InputStream is);
}
