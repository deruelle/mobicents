package org.jboss.mobicents.seam;

import java.math.BigDecimal;

import javax.naming.InitialContext;
import javax.slee.EventTypeID;
import javax.slee.connection.ExternalActivityHandle;
import javax.slee.connection.SleeConnection;
import javax.slee.connection.SleeConnectionFactory;
import javax.naming.*;
import javax.slee.*;
import javax.slee.connection.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import org.mobicents.slee.connector.server.RemoteSleeService;
import org.mobicents.slee.service.events.CustomEvent;

@Name("orderApprovalAction")
public class OrderApprovalAction {
	
	@In
	String customerfullname;

	@In
	String cutomerphone;

	@In
	BigDecimal amount;

	@In
	Long orderId;
	
	@In
	ExternalActivityHandle adminExternalActivityHandle;
	
	public void fireOrderApprovedEvent() {

		System.out
				.println("*************** Fire ORDER_APPROVED  ***************************");
		System.out.println("First Name = " + customerfullname);
		System.out.println("Phone = " + cutomerphone);
		System.out.println("orderId = " + orderId);
		
		try {

			InitialContext ic = new InitialContext();

			SleeConnectionFactory factory = (SleeConnectionFactory) ic
					.lookup("java:/MobicentsConnectionFactory");

			SleeConnection conn1 = null;
			conn1 = factory.getConnection();

			//ExternalActivityHandle handle = conn1.createActivityHandle();

			EventTypeID requestType = conn1.getEventTypeID(
					"org.mobicents.slee.service.dvddemo.ORDER_APPROVED",
					"org.mobicents", "1.0");
			CustomEvent customEvent = new CustomEvent(orderId, amount,
					customerfullname, cutomerphone);

			conn1.fireEvent(customEvent, requestType, adminExternalActivityHandle, null);
			conn1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void fireOrderRejectedEvent() {
		System.out
				.println("*************** Fire ORDER_REJECTED  ***************************");
		System.out.println("First Name = " + customerfullname);
		System.out.println("Phone = " + cutomerphone);
		System.out.println("orderId = " + orderId);
		System.out.println("adminExternalActivityHandle = " + adminExternalActivityHandle);

		try {

			InitialContext ic = new InitialContext();

			SleeConnectionFactory factory = (SleeConnectionFactory) ic
					.lookup("java:/MobicentsConnectionFactory");

			SleeConnection conn1 = null;
			conn1 = factory.getConnection();

			//ExternalActivityHandle handle = conn1.createActivityHandle();

			EventTypeID requestType = conn1.getEventTypeID(
					"org.mobicents.slee.service.dvddemo.ORDER_REJECTED",
					"org.mobicents", "1.0");
			CustomEvent customEvent = new CustomEvent(orderId, amount,
					customerfullname, cutomerphone);

			conn1.fireEvent(customEvent, requestType, adminExternalActivityHandle, null);
			conn1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
