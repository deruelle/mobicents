package org.mobicents.media.server.impl.fft;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

public class DFTResourceManager extends BaseResourceManager {

	private static Logger logger = Logger.getLogger(DFTResourceManager.class);

	@Override
	public MediaResource getResource(BaseEndpoint endpoint,
			MediaResourceType type, Connection connection, Properties config)
			throws UnknownMediaResourceException {

		logger.info("---> ["+endpoint.getLocalName()+"] FETCH[" + type + "]");
		//new IllegalArgumentException("---> ["+endpoint.getLocalName()+"] FETCH[" + type + "]").printStackTrace();
		if (type == type.AUDIO_SINK) {
			// TODO: PUT
			return new DFTFilterResource(endpoint, connection, config);
		} else if (type == type.AUDIO_SOURCE) {
			return new DFTSineSource((DFTEndpointImpl) endpoint, connection,
					null);
		} else {
			return null;
		}

	}

}
