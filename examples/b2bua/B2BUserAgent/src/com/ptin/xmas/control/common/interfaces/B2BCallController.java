/*
 * Created on 20/Out/2005
 *
 * 
 */
package com.ptin.xmas.control.common.interfaces;

/**
 * @author Luis Teixeira
 *
 * 
 */

public interface B2BCallController {   
    public void hangup(String callId);
    public void forward(String sourceUri, String destinationUri, String finalToUri, String domain);
    public String getTo(String callId);
    public String getFrom(String callId);
    public void reject(String callId);
}
