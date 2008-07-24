package org.mobicents.qa.performance.jainsip.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.qa.performance.jainsip.inspector.TestInspector;

public class TestInspectorTestCase {

    private TestInspector testSubject;

    @Before
    public void init() {
	this.testSubject = new TestInspector();
    }

    @Test
    public void testDialogCreated() {
	testSubject.dialogCreated();
	if (testSubject.getCreatedDialogCount() != 1) {
	    fail("Error on dialog created count: != 1");
	}
    }

    @Test
    public void testDialogTerminated() {
	testSubject.dialogTerminated();
	if (testSubject.getTerminatedDialogCount() != 1) {
	    fail("Error on dialog trminated count: != 1");
	}
    }

    @Test
    public void testDialogTerminatedUnexpectedly() {
	testSubject.dialogTerminatedUnexpectedly();
	if (testSubject.getTerminatedUnexpectedlyDialogCount() != 1) {
	    fail("Error on dialog unexpectedly count: != 1");
	}
    }
    
    @Test
    public void testResetDialogCreated() {
	testSubject.dialogCreated();
	if (testSubject.getAndResetCreatedDialogCount() != 1) {
	    fail("Error on dialog created count: != 1");
	}
	testSubject.dialogCreated();
	if (testSubject.getAndResetCreatedDialogCount() != 1) {
	    fail("Error on dialog created count: != 1");
	}
	if (testSubject.getAndResetCreatedDialogCount() != 0) {
	    fail("Error on dialog created count: != 0");
	}
	if (testSubject.getCreatedDialogCount() != 2) {
	    fail("Error on dialog created count: != 2");
	}
    }

    @After
    public void tearDown() {
	testSubject = null;
    }

}
