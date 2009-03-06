package org.mobicents.media.server.impl.jmx.enp.ivr;

import javax.naming.NamingException;
import org.mobicents.media.server.impl.jmx.enp.ann.AnnTrunkManagementMBean;

public interface IVRTrunkManagementMBean extends AnnTrunkManagementMBean {

	public String getRecordDir();

	public void setRecordDir(String recordDir) throws NamingException;

	public String getMediaType();

	public void setMediaType(String mediaType);

	public String getDtmfMode();

	public void setDtmfMode(String mode) throws NamingException;
}
