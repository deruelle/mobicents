package org.mobicents.slee.service.rules.test;

import java.util.ArrayList;
import java.util.List;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.mobicents.slee.resource.rules.ra.CallFactImpl;
import org.mobicents.slee.resource.rules.ra.RulesSessionImpl;
import org.mobicents.slee.resource.rules.ratype.CallFact;
import org.mobicents.slee.resource.rules.ratype.RulesSession;

public class IndividualRulesUnitTest extends MobicentsCallFlowSetup {
	
	public void testWelcome() throws Exception {

		List result = new ArrayList();
		WorkingMemory wm = getWorkingMemory();

		wm.setGlobal("result", result);

		CallFact call1 = new CallFactImpl();
		call1.setFromUri("sip:abhayani@nist.gov");
		call1.setToUri("sip:08055555@nist.gov");

		// ASSERT AND FIRE
		wm.assertObject(call1);

		wm.fireAllRules();

		assertEquals(3, result.size());

		String action = (String) result.get(0);
		String value = (String) result.get(1);

		assertEquals("PLAY", action);
		assertEquals("welcome", value);

		// Testing modifyObject of workingMemory

		result = (List) wm.getGlobal("result");
		result.clear();

		wm.setGlobal("result", result);

		call1.setSubMenu(value);

		FactHandle callFactHandel = wm.getFactHandle(call1);

		if (callFactHandel != null) {
			wm.modifyObject(callFactHandel, call1);
		}

		// wm.assertObject(call1);
		wm.fireAllRules();

		action = (String) result.get(0);
		value = (String) result.get(1);

		assertEquals("PLAY", action);
		assertEquals("welcome", value);

	}

	public void testWelcomeRepeat() throws Exception {

		List result = new ArrayList();
		
		WorkingMemory wm = getWorkingMemory();

		wm.setGlobal("result", result);

		CallFact call1 = new CallFactImpl();
		call1.setFromUri("sip:abhayani@nist.gov");
		call1.setToUri("sip:08055555@nist.gov");
		call1.setDtmf("NULL");

		call1.setSubMenu("welcome");

		// ASSERT AND FIRE
		wm.assertObject(call1);

		wm.fireAllRules();

		assertEquals(3, result.size());

		System.out.println(result.get(0));
		System.out.println(result.get(1));

	}

	public void testExistingCustomer() throws Exception {

		List result = new ArrayList();
		
		WorkingMemory wm = getWorkingMemory();

		wm.setGlobal("result", result);

		CallFact call1 = new CallFactImpl();
		call1.setFromUri("sip:abhayani@nist.gov");
		call1.setToUri("sip:08055555@nist.gov");
		call1.setSubMenu("welcome");
		call1.setDtmf("101");

		// ASSERT AND FIRE
		wm.assertObject(call1);

		wm.fireAllRules();

		assertEquals(3, result.size());

		System.out.println(result.get(0));
		System.out.println(result.get(1));
		
		boolean dtmf = Boolean.parseBoolean((String)result.get(2));
		
		System.out.println(dtmf);

	}
}
