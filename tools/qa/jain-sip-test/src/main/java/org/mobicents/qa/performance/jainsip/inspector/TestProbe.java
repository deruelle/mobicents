package org.mobicents.qa.performance.jainsip.inspector;

public interface TestProbe {
    
    public void dialogCreated();
    
    public void dialogTerminated();
    
    public void dialogTerminatedUnexpectedly();
    
}
