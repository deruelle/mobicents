/**
 * Start time:18:05:42 2008-11-27<br>
 * Project: mobicents-media-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.media.server.impl;

import org.mobicents.media.server.local.management.WorkDataGatherer;

/**
 * Start time:18:05:42 2008-11-27<br>
 * Project: mobicents-media-server-core<br>
 * 
 * Simple implementation to avoid writing the same code over and over
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public abstract class AbstractWorkDataGatherer implements WorkDataGatherer {

	
	protected WorkDataGatherer dataSink=null;
	
	
	
	public WorkDataGatherer getWorkDataSink() {
		return dataSink;
	}

	public void setWorkDataSink(WorkDataGatherer dataSink) {
		this.dataSink = dataSink;
	}

	
	
	public boolean isGatherStats() {
		return this.dataSink!=null;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.local.management.WorkDataGatherer#octetsReceived(int)
	 */
	public void octetsReceived(int count) {
		WorkDataGatherer local=this.dataSink;
		if(local!=null)
		{
			try
			{
				local.octetsReceived(count);
			}
			catch(NullPointerException e)
			{
				//this shouldnt happen
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.local.management.WorkDataGatherer#octetsSent(int)
	 */
	public void octetsSent(int count) {
		WorkDataGatherer local=this.dataSink;
		if(local!=null)
		{
			try
			{
				local.octetsSent(count);
			}
			catch(NullPointerException e)
			{
				//this shouldnt happen
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.local.management.WorkDataGatherer#packetsLost(int)
	 */
	public void packetsLost(int count) {
		WorkDataGatherer local=this.dataSink;
		if(local!=null)
		{
			try
			{
				local.packetsLost(count);
			}
			catch(NullPointerException e)
			{
				//this shouldnt happen
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.local.management.WorkDataGatherer#packetsReceived(int)
	 */
	public void packetsReceived(int count) {
		WorkDataGatherer local=this.dataSink;
		if(local!=null)
		{
			try
			{
				local.packetsReceived(count);
			}
			catch(NullPointerException e)
			{
				//this shouldnt happen
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.media.server.local.management.WorkDataGatherer#packetsSents(int)
	 */
	public void packetsSent(int count) {
		WorkDataGatherer local=this.dataSink;
		if(local!=null)
		{
			try
			{
				local.packetsSent(count);
			}
			catch(NullPointerException e)
			{
				//this shouldnt happen
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public void interArrivalJitter(int value) {
		
			WorkDataGatherer local=this.dataSink;
			if(local!=null)
			{
				try
				{
					local.interArrivalJitter(value);
				}
				catch(NullPointerException e)
				{
					//this shouldnt happen
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		
		
	}
	
	
	

}
