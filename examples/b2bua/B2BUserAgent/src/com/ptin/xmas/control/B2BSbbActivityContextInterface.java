/*
 * Created on 4/Out/2005
 *
 * 
 */
package com.ptin.xmas.control;

import java.util.Hashtable;
import javax.slee.facilities.TimerID;

import com.ptin.xmas.control.common.interfaces.B2BCallController;

/**
 * @author Luis Teixeira
 *
 * 
 */

public interface B2BSbbActivityContextInterface extends javax.slee.ActivityContextInterface {
    public Hashtable getInCallsList();
    public void setInCallsList(Hashtable inCallsList);
    
    public B2BCallController getB2BCallController();
    public void setB2BCallController(B2BCallController b2bCallController);
}
