package org.mobicents.slee.resource.sip.wrappers;

import java.util.TimerTask;

import javax.sip.DialogState;

import org.mobicents.slee.resource.sip.SipActivityHandle;
import org.mobicents.slee.resource.sip.SipResourceAdaptor;

/**
 * This class acts as dialog idle timeout timer, when it is run dialog should be
 * considered dead - no requests in and out within this dialog for certain time.
 * 
 * @author B. Baranowski
 * @author eduardomartins
 * 
 */
public class DialogTimeoutTimerTask extends TimerTask {

	private SipResourceAdaptor sipRA;
	private DialogWrapper dialog;

	public DialogTimeoutTimerTask(DialogWrapper dialog, SipResourceAdaptor sipResourceAdaptor) {
		this.dialog = dialog;
		this.sipRA = sipResourceAdaptor;
	}

	public void run() {
		try {
			// if there is still a non terminated dialog that is an ra activity ...
			if (dialog != null && sipRA != null
					&& dialog.getState() != DialogState.TERMINATED
					&& sipRA.getActivities().containsKey(
							new SipActivityHandle(dialog.getDialogId()))) {
				// ... terminate it
				dialog.delete();
			}
		}
		catch (Exception e) {
			// don't let exceptions escape, will terminate timer
		}
	}

	@Override
	public boolean cancel() {
		dialog = null;
		sipRA = null;
		return super.cancel();
	}
}
