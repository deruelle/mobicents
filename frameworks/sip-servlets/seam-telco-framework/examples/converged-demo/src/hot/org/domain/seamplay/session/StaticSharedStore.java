package org.domain.seamplay.session;

import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;

import org.ajax4jsf.event.PushEventListener;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("sharedStore")
@Scope(ScopeType.STATELESS)
public class StaticSharedStore {
	public static LinkedList<String> messages = new LinkedList<String>();
	private static LinkedList<PushEventListener> listeners = new LinkedList<PushEventListener>();
	
	public void addListener(EventListener eventListener) {
		listeners.add((PushEventListener) eventListener);
	}
	
	public LinkedList<String> getMessages() {
		return messages;
	}
	
	public void reset() {
		messages = new LinkedList<String>();
	}
	
	/*
	 * This method causes the update in the Web Page through Server Push.
	 * It just notifies the subscribed listeners. The listeners come from
	 * the a4j:push control everytime someone loads the page from a broswer.
	 * 
	 * We start a new thread everytime because otherwise the Seam contexts
	 * will be propagated in the Web Pages, which will cause confusion in
	 * multiuser enviroments where the Web Page would have session-speific
	 * data (the session is part of the the Seam context).
	 */
	public static void makeDirty() {
		for(final PushEventListener listener : listeners) {
			new Thread() {
				public void run() {
					listener.onEvent(new EventObject(this));
				}
			}.start();
		}
	}
	
}
