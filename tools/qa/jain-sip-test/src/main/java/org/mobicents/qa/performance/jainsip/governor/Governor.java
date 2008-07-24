package org.mobicents.qa.performance.jainsip.governor;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;

public interface Governor {

    public TestObserver getObserver();
    
    public void endTest();

}
