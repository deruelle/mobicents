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

import javax.slee.SbbLocalObject;
import org.w3c.dom.Node;

/**
 * Form items are the elements that can be visited in the main loop of the form 
 * interpretation algorithm. 
 * 
 * Input items direct the FIA to gather a result for a specific element. 
 * When the FIA selects a control item, the control item may contain a block of 
 * procedural code to execute, or it may tell the FIA to set up the initial 
 * prompt-and-collect for a mixed initiative form.
 *
 *
 * @author Oleg Kulikov
 */
public interface VXMLItem extends SbbLocalObject {    
    /**
     * Executes specified node on specified endpoint.
     * 
     * @param root the reference to the SBB local object which handles document.
     * @param parent the reference to the SBB local object which handles parent node.
     * @param endpoint the endpoint to execute node
     * @param node the node to be executed.
     */
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node);
    
    /**
     * This method is called when a child node is performed.
     * 
     * @param userInput the input provided by user if present.
     */
    public void onCompleted(String userInput);
    
    /**
     * Gets specified variable.
     * 
     * @param the name of the variable.
     * @return specified varibale value.
     */
    public String getValue(String var);
    
}
