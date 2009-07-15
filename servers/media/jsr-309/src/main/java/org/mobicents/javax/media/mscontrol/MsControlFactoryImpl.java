package org.mobicents.javax.media.mscontrol;

import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.media.mscontrol.Configuration;
import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.MediaObject;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.resource.enums.EventTypeEnum;
import javax.media.mscontrol.resource.enums.ParameterEnum;
import javax.media.mscontrol.resource.video.VideoLayout;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUMgcpEvent;
import org.mobicents.jain.protocol.ip.mgcp.pkg.AUPackage;
import org.mobicents.javax.media.mscontrol.mediagroup.PlayerEventDetectorFactory;
import org.mobicents.javax.media.mscontrol.mediagroup.signals.SignalDetectorEventDetectorFactory;
import org.mobicents.javax.media.mscontrol.resource.ExtendedParameter;
import org.mobicents.jsr309.mgcp.MgcpStackFactory;
import org.mobicents.jsr309.mgcp.MgcpWrapper;
import org.xml.sax.SAXException;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MsControlFactoryImpl implements MsControlFactory {

	private static final Logger logger = Logger.getLogger(MsControlFactoryImpl.class);

	private Properties properties = null;
	private MgcpWrapper mgcpWrapper = null;

	private List<Integer> list = new ArrayList<Integer>();

	private XMLParser parser = new XMLParser();

	protected static Map<Configuration, MediaConfigImpl> configVsMediaConfigMap = new HashMap<Configuration, MediaConfigImpl>();

	public MsControlFactoryImpl(Properties properties) {
		this.properties = properties;

		MgcpStackFactory mgcpStackFactory = MgcpStackFactory.getInstance();
		this.mgcpWrapper = mgcpStackFactory.getMgcpStackProvider(properties);

		if (mgcpWrapper == null) {
			throw new RuntimeException("Could not create instance of MediaSessionFactory. Check the exception in logs");
		}

		// NC.BASIC
		configVsMediaConfigMap.put(NetworkConnection.BASIC, this.createNetConnBasic());

		// MG.PPLAYER
		configVsMediaConfigMap.put(MediaGroup.PLAYER, this.createMedGrpPlayer());

		// MG.PLAYER_SIGNALDETECTOR
		configVsMediaConfigMap.put(MediaGroup.PLAYER_SIGNALDETECTOR, this.createMedGrpPlayerSignDete());

		// MG.SIGNALDETECTOR
		configVsMediaConfigMap.put(MediaGroup.SIGNALDETECTOR, this.createMedGrpSignDete());

		// MG.PLAYER_RECORDER_SIGNALDETECTOR
		configVsMediaConfigMap.put(MediaGroup.PLAYER_RECORDER_SIGNALDETECTOR, this.createMedGrpPlaRecSigDet());

		// MMX.AUDIO
		configVsMediaConfigMap.put(MediaMixer.AUDIO, this.createMedMixAud());
	}

	private MediaConfigImpl createMedMixAud() {
		MediaConfigImpl ncMediaConf = new MediaConfigImpl(mgcpWrapper);

		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/cnf/$");
		paraImpl.put(ParameterEnum.MAX_PORTS, "5");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		ncMediaConf.setSupportedFeatures(suppFeatures);

		ncMediaConf.setParameters(paraImpl);

		return ncMediaConf;
	}

	private MediaConfigImpl createNetConnBasic() {
		MediaConfigImpl ncMediaConf = new MediaConfigImpl(mgcpWrapper);
		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/packetrelay/$");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		ncMediaConf.setSupportedFeatures(suppFeatures);

		ncMediaConf.setParameters(paraImpl);

		return ncMediaConf;
	}

	private List<DefaultEventGeneratorFactory> getPlayerEventGeneratorFactoryList() {
		List<DefaultEventGeneratorFactory> generatorList = new ArrayList<DefaultEventGeneratorFactory>();

		DefaultEventGeneratorFactory ann = new DefaultEventGeneratorFactory(PackageName.Announcement.toString(),
				MgcpEvent.ann.getName(), true);
		generatorList.add(ann);

		return generatorList;
	}

	private List<DefaultEventGeneratorFactory> getRecEveGenFacList() {
		List<DefaultEventGeneratorFactory> generatorList = new ArrayList<DefaultEventGeneratorFactory>();

		DefaultEventGeneratorFactory ann = new DefaultEventGeneratorFactory(AUPackage.AU.toString(), AUMgcpEvent.aupr
				.getName(), true);
		generatorList.add(ann);

		return generatorList;
	}

	private List<PlayerEventDetectorFactory> getPlayerEventDetectorFactoryList() {
		List<PlayerEventDetectorFactory> detectorList = new ArrayList<PlayerEventDetectorFactory>();

		PlayerEventDetectorFactory oc = new PlayerEventDetectorFactory(PackageName.Announcement.toString(),
				MgcpEvent.oc.getName(), true, EventTypeEnum.PLAY_COMPLETED);
		detectorList.add(oc);

		PlayerEventDetectorFactory of = new PlayerEventDetectorFactory(PackageName.Announcement.toString(),
				MgcpEvent.of.getName(), true, EventTypeEnum.PLAY_COMPLETED);
		detectorList.add(of);

		return detectorList;
	}

	private MediaConfigImpl createMedGrpPlayer() {
		MediaConfigImpl ncMediaConf = new MediaConfigImpl(mgcpWrapper);
		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/IVR/$");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		ncMediaConf.setSupportedFeatures(suppFeatures);

		ncMediaConf.setParameters(paraImpl);

		ncMediaConf.setPlayerGeneFactList(getPlayerEventGeneratorFactoryList());
		ncMediaConf.setPlayerDetFactList(getPlayerEventDetectorFactoryList());
		ncMediaConf.setPlayer(true);

		return ncMediaConf;
	}

	private MediaConfigImpl createMedGrpSignDete() {
		MediaConfigImpl mgMediaConf = new MediaConfigImpl(mgcpWrapper);
		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/IVR/$");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		mgMediaConf.setSupportedFeatures(suppFeatures);

		mgMediaConf.setParameters(paraImpl);

		// Signal Detector
		// mgMediaConf.setSigDeteEveGeneFactList(getSigDetEveGenFacList());
		mgMediaConf.setSigDeteEveDetFactList(getSigDetEveDetFacList());
		mgMediaConf.setSignaldetector(true);

		return mgMediaConf;

	}

	private List<DefaultEventGeneratorFactory> getSigDetEveGenFacList() {
		List<DefaultEventGeneratorFactory> sigDeteEveGenList = new ArrayList<DefaultEventGeneratorFactory>();

		DefaultEventGeneratorFactory dtmf0 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf0.getName(), false);
		sigDeteEveGenList.add(dtmf0);

		DefaultEventGeneratorFactory dtmf1 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf1.getName(), false);
		sigDeteEveGenList.add(dtmf1);

		DefaultEventGeneratorFactory dtmf2 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf2.getName(), false);
		sigDeteEveGenList.add(dtmf2);

		DefaultEventGeneratorFactory dtmf3 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf3.getName(), false);
		sigDeteEveGenList.add(dtmf3);

		DefaultEventGeneratorFactory dtmf4 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf4.getName(), false);
		sigDeteEveGenList.add(dtmf4);

		DefaultEventGeneratorFactory dtmf5 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf5.getName(), false);
		sigDeteEveGenList.add(dtmf5);

		DefaultEventGeneratorFactory dtmf6 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf6.getName(), false);
		sigDeteEveGenList.add(dtmf6);

		DefaultEventGeneratorFactory dtmf7 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf7.getName(), false);
		sigDeteEveGenList.add(dtmf7);

		DefaultEventGeneratorFactory dtmf8 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf8.getName(), false);
		sigDeteEveGenList.add(dtmf8);

		DefaultEventGeneratorFactory dtmf9 = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmf9.getName(), false);
		sigDeteEveGenList.add(dtmf9);

		DefaultEventGeneratorFactory dtmfA = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfA.getName(), false);
		sigDeteEveGenList.add(dtmfA);

		DefaultEventGeneratorFactory dtmfB = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfB.getName(), false);
		sigDeteEveGenList.add(dtmfB);

		DefaultEventGeneratorFactory dtmfC = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfC.getName(), false);
		sigDeteEveGenList.add(dtmfC);

		DefaultEventGeneratorFactory dtmfD = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfD.getName(), false);
		sigDeteEveGenList.add(dtmfD);

		DefaultEventGeneratorFactory dtmfStar = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfStar.getName(), false);
		sigDeteEveGenList.add(dtmfStar);

		DefaultEventGeneratorFactory dtmfHash = new DefaultEventGeneratorFactory(PackageName.Dtmf.toString(),
				MgcpEvent.dtmfHash.getName(), false);
		sigDeteEveGenList.add(dtmfHash);

		return sigDeteEveGenList;

	}

	private List<SignalDetectorEventDetectorFactory> getSigDetEveDetFacList() {
		List<SignalDetectorEventDetectorFactory> sigDeteEveDetList = new ArrayList<SignalDetectorEventDetectorFactory>();

		SignalDetectorEventDetectorFactory dtmf0Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf0.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf0Eve);

		SignalDetectorEventDetectorFactory dtmf1Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf1.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf1Eve);

		SignalDetectorEventDetectorFactory dtmf2Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf2.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf2Eve);

		SignalDetectorEventDetectorFactory dtmf3Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf3.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf3Eve);

		SignalDetectorEventDetectorFactory dtmf4Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf4.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf4Eve);

		SignalDetectorEventDetectorFactory dtmf5Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf5.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf5Eve);

		SignalDetectorEventDetectorFactory dtmf6Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf6.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf6Eve);

		SignalDetectorEventDetectorFactory dtmf7Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf7.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf7Eve);

		SignalDetectorEventDetectorFactory dtmf8Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf8.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf8Eve);

		SignalDetectorEventDetectorFactory dtmf9Eve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmf9.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmf9Eve);

		SignalDetectorEventDetectorFactory dtmfAEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfA.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfAEve);

		SignalDetectorEventDetectorFactory dtmfBEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfB.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfBEve);

		SignalDetectorEventDetectorFactory dtmfCEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfC.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfCEve);

		SignalDetectorEventDetectorFactory dtmfDEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfD.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfDEve);

		SignalDetectorEventDetectorFactory dtmfStarEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfStar.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfStarEve);

		SignalDetectorEventDetectorFactory dtmfHashEve = new SignalDetectorEventDetectorFactory(PackageName.Dtmf
				.toString(), MgcpEvent.dtmfHash.getName(), false, EventTypeEnum.SIGNAL_DETECTED);
		sigDeteEveDetList.add(dtmfHashEve);

		return sigDeteEveDetList;
	}

	private MediaConfigImpl createMedGrpPlayerSignDete() {
		MediaConfigImpl mgMediaConf = new MediaConfigImpl(mgcpWrapper);
		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/IVR/$");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		mgMediaConf.setSupportedFeatures(suppFeatures);
		
		mgMediaConf.setParameters(paraImpl);

		// Player
		mgMediaConf.setPlayerGeneFactList(getPlayerEventGeneratorFactoryList());
		mgMediaConf.setPlayerDetFactList(getPlayerEventDetectorFactoryList());
		mgMediaConf.setPlayer(true);

		// Signal Detector
		mgMediaConf.setSigDeteEveGeneFactList(getSigDetEveGenFacList());
		mgMediaConf.setSigDeteEveDetFactList(getSigDetEveDetFacList());
		mgMediaConf.setSignaldetector(true);

		return mgMediaConf;
	}

	private MediaConfigImpl createMedGrpPlaRecSigDet() {
		MediaConfigImpl mgMediaConf = new MediaConfigImpl(mgcpWrapper);;
		ParametersImpl paraImpl = new ParametersImpl();
		paraImpl.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/mobicents/media/IVR/$");

		SupportedFeaturesImpl suppFeatures = new SupportedFeaturesImpl();
		suppFeatures.setParameter(paraImpl.keySet());
		mgMediaConf.setSupportedFeatures(suppFeatures);

		mgMediaConf.setParameters(paraImpl);

		// Player
		mgMediaConf.setPlayerGeneFactList(getPlayerEventGeneratorFactoryList());
		mgMediaConf.setPlayerDetFactList(getPlayerEventDetectorFactoryList());
		mgMediaConf.setPlayer(true);

		// Signal Detector
		mgMediaConf.setSigDeteEveGeneFactList(getSigDetEveGenFacList());
		mgMediaConf.setSigDeteEveDetFactList(getSigDetEveDetFacList());
		mgMediaConf.setSignaldetector(true);

		// Recorder
		mgMediaConf.setRecorderGeneFactList(getRecEveGenFacList());
		mgMediaConf.setRecorder(true);

		return mgMediaConf;
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
		return configVsMediaConfigMap.get(paramConfiguration);
	}

	public MediaConfig getMediaConfig(Reader paramReader) throws MediaConfigException {
		int c;
		MediaConfigImpl config = null;

		try {
			while ((c = paramReader.read()) != -1) {
				list.add(c);
			}

			byte[] b = new byte[list.size()];
			int count = 0;
			for (int i : list) {
				b[count] = (byte) i;
				count++;
			}

			list.clear();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
			config = parser.parse(null, inputStream);
		} catch (IOException e) {
			logger.error(e);
			throw new MediaConfigException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			logger.error(e);
			throw new MediaConfigException(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e);
			throw new MediaConfigException(e.getMessage(), e);
		}

		return config;
	}

	public MediaObject getMediaObject(URI paramURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
