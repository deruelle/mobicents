/*
 * File Name     : MessageHandler.java
 *
 * The Java Call Control RA
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

package org.mobicents.jcc.sip;

import javax.csapi.cc.jcc.JccConnection;
import javax.csapi.cc.jcc.JccEvent;

import javax.sip.message.Request;
import javax.sip.message.Response;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class MessageHandler implements Runnable {
    
    private JccConnectionImpl connection;
    private RequestEvent requestEvent;
    private ResponseEvent responseEvent;
    
    /** Creates a new instance of MessageHandler */
    public MessageHandler(JccConnectionImpl connection, RequestEvent requestEvent) {
        this.connection = connection;
        this.requestEvent = requestEvent;
        this.responseEvent = null;
    }

    public MessageHandler(JccConnectionImpl connection, ResponseEvent responseEvent) {
        this.connection = connection;
        this.requestEvent = null;
        this.responseEvent = responseEvent;
    }
    
    private void processRequest() {
        Request request = requestEvent.getRequest();
        String method = request.getMethod();
        if (method.equals(Request.BYE)) {
            connection.ok(requestEvent.getServerTransaction(), request, null);
            connection.isComplete = true;
            connection.setState(JccConnection.DISCONNECTED, JccEvent.CAUSE_NORMAL);
        } else if (method.equals(Request.CANCEL)) {
            connection.isComplete = true;
            connection.setState(JccConnection.FAILED, JccEvent.CAUSE_CALL_CANCELLED);
        }
    }
    
    private void processResponse() {
        Response response = responseEvent.getResponse();
        switch (response.getStatusCode()) {
            case Response.TRYING :
                connection.startDialog();
                break;
            case Response.RINGING :
            case Response.SESSION_PROGRESS :
                connection.setState(JccConnection.ALERTING, JccEvent.CAUSE_NORMAL);
                break;
            case Response.OK :
                switch (connection.getState()) {
                    case JccConnection.ALERTING :
                        connection.ack();
                        connection.remoteSdp = new String(response.getRawContent());
                        connection.setState(JccConnection.CONNECTED, JccEvent.CAUSE_NORMAL);
                        break;
                    case JccConnection.DISCONNECTED :
                        connection.releaseComplete();
                        break;
                        
                }
                break;
            case Response.BUSY_HERE :
                connection.setState(JccConnection.FAILED, JccEvent.CAUSE_BUSY);
                break;
        }
    }
    
    public void run() {
        if (requestEvent != null) {
            processRequest();
        } else if (responseEvent != null) {
            processResponse();
        }
    }
    
    public String toString() {
        return requestEvent != null ? requestEvent.getRequest().getMethod() : 
            responseEvent.getResponse().getReasonPhrase();
    }
}
