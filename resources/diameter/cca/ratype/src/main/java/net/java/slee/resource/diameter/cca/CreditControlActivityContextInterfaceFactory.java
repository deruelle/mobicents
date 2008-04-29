package net.java.slee.resource.diameter.cca;

import javax.slee.ActivityContextInterface;

public interface CreditControlActivityContextInterfaceFactory {

	public ActivityContextInterface getActivityContextInterface(CreditControlClientSession cccs);
	public ActivityContextInterface getActivityContextInterface(CreditControlServerSession ccss);
}
