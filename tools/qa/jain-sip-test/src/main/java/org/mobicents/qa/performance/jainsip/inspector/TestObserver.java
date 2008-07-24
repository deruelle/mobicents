package org.mobicents.qa.performance.jainsip.inspector;

public interface TestObserver {

    public int getCreatedDialogCount();
    
    public int getTerminatedDialogCount();
    
    public int getTerminatedUnexpectedlyDialogCount();
    

    public int getAndResetCreatedDialogCount();
    
    public int getAndResetTerminatedDialogCount();
    
    public int getAndResetTerminatedUnexpectedlyDialogCount();
    
}
