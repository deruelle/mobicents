/**
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.mobicents.examples.convergeddemo.seam.action;

import java.math.BigDecimal;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.slee.EventTypeID;
import javax.slee.connection.ExternalActivityHandle;
import javax.slee.connection.SleeConnection;
import javax.slee.connection.SleeConnectionFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.connector.server.RemoteSleeService;
import org.mobicents.slee.service.events.CustomEvent;

/**
 * A MDB3 EJB example.
 * 
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 * @version <tt>$Revision: 2868 $</tt>
 * 
 * $Id: EJB3MDBExample.java 2868 2007-07-10 20:22:16Z timfox $
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/A"),
		@ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10") })
public class EJB3MDBExample implements MessageListener {
	private static Logger logger = Logger.getLogger(EJB3MDBExample.class);

	public void onMessage(Message m) {
		businessLogic(m);
	}

	private void businessLogic(Message m) {
		Connection conn = null;
		Session session = null;
		RemoteSleeService service = null;

		try {
			TextMessage tm = (TextMessage) m;

			String text = tm.getText();
			logger.info("message " + text + " received");

			String[] mssArr = text.split(",");
			for (int i = 0; i < mssArr.length; i++) {
				logger.info(mssArr[i]);
			}

			logger.info("************************** EJB3MDBExample -> Start **************************");

			InitialContext ctx = new InitialContext();
			SleeConnectionFactory factory = (SleeConnectionFactory) ctx.lookup("java:/MobicentsConnectionFactory");

			SleeConnection conn1 = null;
			conn1 = factory.getConnection();

			// BigDecimal amount = BigDecimal.valueOf(45l, 0) ;
			// amount = 45.66;

			long orderId = Long.parseLong(mssArr[0]);

			BigDecimal amount = new BigDecimal(mssArr[1]);

			CustomEvent customEvent = new CustomEvent(orderId, amount, mssArr[2], mssArr[3], mssArr[4]);

			EventTypeID requestType = conn1.getEventTypeID("org.mobicents.slee.service.sfdemo.ORDER_PLACED",
					"org.mobicents", "1.0");

			// Fire an asynchronous event
			ExternalActivityHandle handle = conn1.createActivityHandle();

			conn1.fireEvent(customEvent, requestType, handle, null);

			logger.info("************************** MDBExample -> End **************************");

		} catch (Exception e) {

			logger.error("The Message Driven Bean failed!", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.error("Could not close the connection!" , e);
				}
			}
		}
	}
}
