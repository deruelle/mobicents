package org.mobicents.javax.media.mscontrol;

import java.io.Reader;
import java.net.URI;
import java.util.Properties;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.Configuration;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.resource.video.VideoLayout;

import org.mobicents.jsr309.mgcp.MgcpStackFactory;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MsControlFactoryImpl implements MsControlFactory {

	private Properties properties = null;
	private MgcpWrapper mgcpWrapper = null;

	public MsControlFactoryImpl(Properties properties) {
		this.properties = properties;

		MgcpStackFactory mgcpStackFactory = MgcpStackFactory.getInstance();
		this.mgcpWrapper = mgcpStackFactory.getMgcpStackProvider(properties);

		if (mgcpWrapper == null) {
			throw new RuntimeException("Could not create instance of MediaSessionFactory. Check the exception in logs");
		}

	}

	public MediaSession createMediaSession() {
		return new MediaSessionImpl(this.mgcpWrapper);
	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public VideoLayout createVideoLayout(String mimeType, Reader xmlDef) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig> C getMediaConfig(Class<C> configClass, Reader xmlDef) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public VideoLayout getPresetLayout(String type) throws MediaConfigException {
		return null;
	}

	public VideoLayout[] getPresetLayouts(int numberOfLiveRegions) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public MediaConfig getMediaConfig(Configuration<?> paramConfiguration) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public MediaConfig getMediaConfig(Reader paramReader) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public MediaObject getMediaObject(URI paramURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
