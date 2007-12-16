package org.mobicents.slee.resource;

/** Used only be JUnit tests */
public class TestThreadUtil {

    private TestThreadUtil() {
    }

    /**
     * Standard pause used in multiple tests. For example, a test involving a
     * executor needs a delay before some check is done, use this standard
     * delay.
     */
    public static void pause() {
        try {
            // 
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
