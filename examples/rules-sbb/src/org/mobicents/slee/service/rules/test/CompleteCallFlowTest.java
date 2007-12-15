package org.mobicents.slee.service.rules.test;

import java.util.ArrayList;
import java.util.List;

import org.drools.WorkingMemory;
import org.mobicents.slee.resource.rules.ra.CallFactImpl;
import org.mobicents.slee.resource.rules.ra.RulesSessionImpl;
import org.mobicents.slee.resource.rules.ratype.CallFact;
import org.mobicents.slee.resource.rules.ratype.RulesSession;

public class CompleteCallFlowTest extends MobicentsCallFlowSetup {

	public void testWelcomeRepeatWithRulesActivity() {
		WorkingMemory wm = getWorkingMemory();
		RulesSession rulesActivity = new RulesSessionImpl(
				"/MobicentsCallFlow.xls", wm);

		// User Dials sip:08055555@nist.gov and our IVR Starts
		CallFact call1 = new CallFactImpl();
		call1.setFromUri("sip:abhayani@nist.gov");
		call1.setToUri("sip:08055555@nist.gov");

		List list = new ArrayList();
		list.add(call1);

		List result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("welcome", result.get(1));
		assertEquals("true", result.get(2));

		// If user didnot select the options 1,2,3,4 while playing welcome audio
		// rules will ask to replay the welcome audio
		call1.setSubMenu((String) result.get(1));

		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("welcome", result.get(1));
		assertEquals("true", result.get(2));

		// user dialed 1 test if we get existing customer wav audio
		call1.setDtmf("1");

		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("existingcustomer", result.get(1));
		assertEquals("true", result.get(2));

		// user dialed 2 test if we get products information wav audio
		call1.setDtmf("2");

		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("productsinformation", result.get(1));
		assertEquals("true", result.get(2));

		// user dialed 3 test if we get loss of card wav audio
		call1.setDtmf("3");

		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("forward", result.get(1));
		assertEquals("false", result.get(2));

		// user dialed 4 test if we get recording wav audio
		call1.setDtmf("4");

		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("recording", result.get(1));
		assertEquals("false", result.get(2));

		// Common Actions Test

		// User has dialed 4 while on welcome. recording audio file is already
		// played.
		// Test is we get RECORDING ACTION

		call1.setSubMenu("recording");
		// While playing the recording wav audio we have set receive dtm to be
		// false hence dtmf should be NULL
		call1.setDtmf("NULL");

		result = rulesActivity.executeRules(list);

		assertEquals(1, result.size());
		assertEquals("RECORDING", result.get(0));

		// User has dialed 0 to exit. We play hangup audio and fire HANGUP
		// ACTION
		call1.setDtmf("0");
		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("hangup", result.get(1));
		assertEquals("false", result.get(2));
		
		
		//Hangup wav audio is played. Test if HANGUP ACTION is fired
		call1.setDtmf("NULL");
		call1.setSubMenu("hangup");
		
		result = rulesActivity.executeRules(list);

		assertEquals(1, result.size());
		assertEquals("HANGUP", result.get(0));
		
		
		//User has dialed 9. Test if call gets forwarded
		call1.setDtmf("9");
		
		result = rulesActivity.executeRules(list);

		assertEquals(3, result.size());
		assertEquals("PLAY", result.get(0));
		assertEquals("forward", result.get(1));
		assertEquals("false", result.get(2));
		

	}

}
