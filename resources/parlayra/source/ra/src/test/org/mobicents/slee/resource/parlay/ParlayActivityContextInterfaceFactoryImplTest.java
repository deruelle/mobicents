package org.mobicents.slee.resource.parlay;

import javax.slee.ActivityContextInterface;
import javax.slee.SLEEException;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.TransactionRolledbackLocalException;
import javax.slee.resource.ActivityHandle;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.TpCallActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpCallLegActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;
import org.mobicents.slee.runtime.ActivityContextFactory;

/**
 * 
 * Class Description for ParlayActivityContextInterfaceFactoryImplTest
 */
public class ParlayActivityContextInterfaceFactoryImplTest extends TestCase {

    ParlayActivityContextInterfaceFactoryImpl parlayActivityContextInterfaceFactoryImpl;

    static ActivityContextFactory activityContextFactory;

    /*
     * Class under test for ActivityContextInterface
     * getActivityContextInterface(TpServiceIdentifier)
     */
    public void testGetActivityContextInterfaceTpServiceIdentifier() {
        TestClass testClass = new TestClass(null, "ParlayRA");

        TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);

        ParlayServiceActivityHandle activityHandle = new ParlayServiceActivityHandle(
                serviceIdentifier);

        try {
            ActivityContextInterface result = testClass
                    .getActivityContextInterface(serviceIdentifier);

            assertEquals(activityHandle, testClass.handleReceived);

            assertEquals(testClass.activityContextInterface, result);
        }
        catch (Exception e) {
            fail();
        }
    }

    /*
     * Class under test for ActivityContextInterface
     * getActivityContextInterface(TpMultiPartyCallIdentifier)
     */
    public void testGetActivityContextInterfaceTpMultiPartyCallIdentifier() {
        TestClass testClass = new TestClass(null, "ParlayRA");

        TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                1, 1);

        TpMultiPartyCallActivityHandle activityHandle = new TpMultiPartyCallActivityHandle(
                callIdentifier);

        try {
            ActivityContextInterface result = testClass
                    .getActivityContextInterface(callIdentifier);

            assertEquals(activityHandle, testClass.handleReceived);

            assertEquals(testClass.activityContextInterface, result);
        }
        catch (Exception e) {
            fail();
        }
    }

    /*
     * Class under test for ActivityContextInterface
     * getActivityContextInterface(TpCallLegIdentifier)
     */
    public void testGetActivityContextInterfaceTpCallLegIdentifier() {
        TestClass testClass = new TestClass(null, "ParlayRA");

        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(1, 1);

        TpCallLegActivityHandle activityHandle = new TpCallLegActivityHandle(
                callLegIdentifier);

        try {
            ActivityContextInterface result = testClass
                    .getActivityContextInterface(callLegIdentifier);

            assertEquals(activityHandle, testClass.handleReceived);

            assertEquals(testClass.activityContextInterface, result);
        }
        catch (Exception e) {
            fail();
        }
    }

    public void testGetActivityContextInterfaceTpCallIdentifier() {
        TestClass testClass = new TestClass(null, "ParlayRA");

        TpCallIdentifier callIdentifier = new TpCallIdentifier(1, 1);

        TpCallActivityHandle activityHandle = new TpCallActivityHandle(
                callIdentifier);

        try {
            ActivityContextInterface result = testClass
                    .getActivityContextInterface(callIdentifier);

            assertEquals(activityHandle, testClass.handleReceived);

            assertEquals(testClass.activityContextInterface, result);
        }
        catch (Exception e) {
            fail();
        }
    }
    
    public void testGetJndiName() {
        TestClass testClass = new TestClass(null, "ParlayRA");

        assertEquals("java:slee/resources/" + "ParlayRA" + "/parlayacif",
                testClass.getJndiName());

    }

    /**
     * Extends class under test with utility methods for verifying results and
     * overrides methods that access the container which we aren't stubbing out
     * here.
     */
    private class TestClass extends ParlayActivityContextInterfaceFactoryImpl {
        /**
         * @param serviceContainer
         * @param name
         */
        public TestClass(SleeContainer serviceContainer, String name) {
            super(serviceContainer, name);
        }

        protected ActivityContextFactory getActivityContextFactory() {
            return ParlayActivityContextInterfaceFactoryImplTest.activityContextFactory;
        }

        public ActivityHandle handleReceived;

        public ActivityContextInterface activityContextInterface = new ActivityContextInterface() {
            public Object getActivity()
                    throws TransactionRequiredLocalException, SLEEException {
                return null;
            }

            public void attach(SbbLocalObject arg0)
                    throws NullPointerException,
                    TransactionRequiredLocalException,
                    TransactionRolledbackLocalException, SLEEException {

            }

            public void detach(SbbLocalObject arg0)
                    throws NullPointerException,
                    TransactionRequiredLocalException,
                    TransactionRolledbackLocalException, SLEEException {

            }

            public boolean isEnding() throws TransactionRequiredLocalException,
                    SLEEException {
                return false;
            }
        };

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.ParlayActivityContextInterfaceFactoryImpl#getActivityContextInterface(javax.slee.resource.ActivityHandle)
         */
        protected ActivityContextInterface getActivityContextInterface(
                ActivityHandle activityHandle) {
            handleReceived = activityHandle;
            return activityContextInterface;
        }

    }
}