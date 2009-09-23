package org.mobicents.ipbx.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.mobicents.ipbx.entity.User;
import org.mobicents.ipbx.session.call.model.CurrentWorkspaceState;
import org.mobicents.ipbx.session.call.model.WorkspaceStateManager;

@Name("chatAction")
public class ChatAction {
	String text;
	@In User user;
	
	public void addText() {
		CurrentWorkspaceState cws = WorkspaceStateManager.instance().getWorkspace(user.getName());
		cws.getConference().addChat(user.getName(), text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
