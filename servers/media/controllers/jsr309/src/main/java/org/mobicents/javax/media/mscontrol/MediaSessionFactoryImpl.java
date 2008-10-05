package org.mobicents.javax.media.mscontrol;

import java.io.Reader;
import java.util.Properties;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MediaSessionFactory;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.resource.ConfigSymbol;
import javax.media.mscontrol.resource.Parameters;
import javax.media.mscontrol.resource.video.VideoLayout;

import org.mobicents.javax.media.mscontrol.resource.ParametersImpl;

public class MediaSessionFactoryImpl implements MediaSessionFactory {
	private Properties properties = null;

	public MediaSessionFactoryImpl() {

	}

	public MediaSessionFactoryImpl(Properties properties) {
		this.properties = properties;
	}

	public MediaSession createMediaSession() throws MscontrolException {
		return new MediaSessionImpl();
	}

	public MediaSession createMediaSession(Parameters p) throws MscontrolException {
		return new MediaSessionImpl(p);
	}

	public Parameters createParameters() {
		return new ParametersImpl();
	}

	public VideoLayout createVideoLayout(String arg0, Reader arg1) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig> C getMediaConfig(ConfigSymbol<C> arg0) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public <C extends MediaConfig> C getMediaConfig(Class<C> arg0, Reader arg1) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public VideoLayout getPresetLayout(String arg0) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

	public VideoLayout[] getPresetLayouts(int arg0) throws MediaConfigException {
		// TODO Auto-generated method stub
		return null;
	}

}
