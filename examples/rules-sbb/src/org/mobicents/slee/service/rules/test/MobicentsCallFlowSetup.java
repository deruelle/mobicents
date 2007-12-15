package org.mobicents.slee.service.rules.test;

import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.rule.Package;

public class MobicentsCallFlowSetup extends TestCase {

	private WorkingMemory wm = null;

	public void setUp() {
		final SpreadsheetCompiler converter = new SpreadsheetCompiler();
		final String drl = converter.compile("/MobicentsCallFlow.xls",
				InputType.XLS);

		System.out.println("drl file = " + drl);
		assertNotNull(drl);

		try {

			final PackageBuilder builder = new PackageBuilder();
			builder.addPackageFromDrl(new StringReader(drl));

			final Package pkg = builder.getPackage();

			System.out.println(pkg.getErrorSummary());
			assertNotNull(pkg);

			// assertEquals( 0,
			// builder.getErrors().length );

			System.out.println(builder.getErrors());

			// BUILD RULEBASE
			final RuleBase rb = RuleBaseFactory.newRuleBase();
			rb.addPackage(pkg);

			// NEW WORKING MEMORY
			wm = rb.newWorkingMemory();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	protected WorkingMemory getWorkingMemory(){
		return this.wm;
	}

	public void tearDown() {
		if(wm!=null){
			wm.dispose();			
		}

	}

}
