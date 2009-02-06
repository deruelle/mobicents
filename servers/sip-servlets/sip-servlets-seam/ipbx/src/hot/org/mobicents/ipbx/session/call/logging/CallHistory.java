package org.mobicents.ipbx.session.call.logging;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.mobicents.ipbx.entity.Contact;
import org.mobicents.ipbx.entity.History;
import org.mobicents.ipbx.entity.User;
import org.mobicents.ipbx.session.DataLoader;
import org.mobicents.ipbx.session.call.model.CallStateManager;
import org.mobicents.ipbx.session.util.DateUtil;

@Name("callHistory")
@Scope(ScopeType.STATELESS)
@Transactional
public class CallHistory {
	@In EntityManagerFactory ipbxEntityManagerFactory;
	@In SipSession sipSession;
	@In DataLoader dataLoader;
	@In CallStateManager callStateManager;
	@In EntityManager sipEntityManager;
	@In(scope=ScopeType.SESSION, required=false) @Out(scope=ScopeType.SESSION, required=false) List historyCache;
	
	@Observer("RESPONSE")
	public void response(SipServletResponse response) {
		int status = response.getStatus();
		if(status >= 300) {
			String message = "Call to " + response.getTo().getURI() +
			" has failed with status code " + status;
			addHistory(message);
		} else if (status >= 200) {
			String method = response.getRequest().getMethod();
			if(method.equalsIgnoreCase("INVITE")) {
				String message = "Call to " + response.getTo().getURI() +
				" has succeeded with status code " + status;
				addHistory(message);
			} else if(method.equalsIgnoreCase("BYE")) {
				String message = "Call with " + response.getTo().getURI() +
				" has ended with status code " + status;
				addHistory(message);
			}
		}
	}
	
	@Observer("incomingCall")
	public void acceptedCall(SipServletRequest request) {
		addHistory("Incoming call from " + request.getFrom().getURI());
	}
	
	@Observer("rejectedCall")
	public void rejectedCall(String from, String to) {
		// TODO
	}
	
	public void addHistory(String message) {
		// The database logging is deisabled for now and is replaced by this in-memory logging
		try {
			User user = (User) sipSession.getAttribute("user");
			if(user == null) return;
			if(DataLoader.history.get(user.getName()) == null) {
				DataLoader.history.put(user.getName(), new LinkedList<History>());
			}
			History history = new History();
			history.setMessage(message);
			history.setTimestamp(DateUtil.now());
			history.setUser(user);
			DataLoader.history.get(user.getName()).add(history);

			callStateManager.getCurrentState(user.getName()).makeHistoryDirty();
		} catch(Exception e) {
			// if something fails here we don't care because loggig is secondary function
			e.printStackTrace();
		}
		/*
		try {
			EntityManager em = sipEntityManager;//ipbxEntityManagerFactory.createEntityManager();
			em.flush();
			User user = (User) sipSession.getAttribute("user");
			user = (User) em.createQuery(
			"SELECT user FROM User user where user.id=:uid")
			.setParameter("uid", user.getId()).getSingleResult();
			em.lock(user, LockModeType.READ);
			History history = new History();
			history.setMessage(message);
			history.setTimestamp(DateUtil.now());
			history.setUser(user);
			if(user.getHistory() == null) {
				user.setHistory(new HashSet<History>());
			}
			user.getHistory().add(history);
			em.persist(history);
			
			callStateManager.getCurrentState(user.getName()).makeHistoryDirty();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	

}
