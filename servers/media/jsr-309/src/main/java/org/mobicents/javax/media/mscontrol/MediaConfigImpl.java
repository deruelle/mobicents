package org.mobicents.javax.media.mscontrol;

import java.util.ArrayList;
import java.util.List;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.SupportedFeatures;
import javax.media.mscontrol.join.JoinableStream.StreamType;

import org.mobicents.javax.media.mscontrol.mediagroup.PlayerEventDetectorFactory;
import org.mobicents.javax.media.mscontrol.mediagroup.RecorderEventDetectorFactory;
import org.mobicents.javax.media.mscontrol.mediagroup.signals.SignalDetectorEventDetectorFactory;
import org.mobicents.jsr309.mgcp.MgcpWrapper;

public class MediaConfigImpl implements MediaConfig {

	private SupportedFeaturesImpl suppFeat = null;

	private MgcpWrapper mgcpWrapper = null;

	private List<DefaultEventGeneratorFactory> playerGeneFactList = null;
	private List<PlayerEventDetectorFactory> playerDetFactList = null;

	private List<DefaultEventGeneratorFactory> recorderGeneFactList = null;
	private List<RecorderEventDetectorFactory> recorderDetFactList = null;

	private List<DefaultEventGeneratorFactory> sigDeteEveGeneFactList = null;
	private List<SignalDetectorEventDetectorFactory> SigDeteEveDetFactList = null;

	private boolean recorder = false;
	private boolean player = false;
	private boolean signalgenerator = false;
	private boolean signaldetector = false;

	private ParametersImpl parameters = null;

	private XMLParser parser = null;

	public MediaConfigImpl(MgcpWrapper mgcpWrapper) {
		this.mgcpWrapper = mgcpWrapper;
		this.parser = new XMLParser();
	}

	public MediaConfig createCustomizedClone(Parameters argParams) throws MediaConfigException {

		ParametersImpl newParams = new ParametersImpl();
		newParams.putAll(this.parameters);

		// TODO : What about new Parameter which are not existing in this params? Ignore or add them as well? As of now
		// its ignored
		for (Parameter argP : argParams.keySet()) {
			for (Parameter p : newParams.keySet()) {
				if (p.equals(argP)) {
					newParams.put(p, argParams.get(p));
				}
			}
		}

		MediaConfigImpl configClone = new MediaConfigImpl(this.mgcpWrapper);
		SupportedFeaturesImpl supFeat = new SupportedFeaturesImpl();

		supFeat.setParameter(newParams.keySet());

		configClone.setParameters(newParams);

		if (this.playerGeneFactList != null) {
			List<DefaultEventGeneratorFactory> list = new ArrayList<DefaultEventGeneratorFactory>();
			list.addAll(this.playerGeneFactList);
			configClone.setPlayerGeneFactList(list);
			configClone.setPlayer(true);
		}

		if (this.playerDetFactList != null) {
			List<PlayerEventDetectorFactory> list = new ArrayList<PlayerEventDetectorFactory>();
			list.addAll(playerDetFactList);
			configClone.setPlayerDetFactList(list);
		}

		if (this.recorderGeneFactList != null) {
			List<DefaultEventGeneratorFactory> list = new ArrayList<DefaultEventGeneratorFactory>();
			list.addAll(this.recorderGeneFactList);
			configClone.setRecorderGeneFactList(list);
			configClone.setRecorder(true);
		}

		if (this.recorderDetFactList != null) {
			List<RecorderEventDetectorFactory> list = new ArrayList<RecorderEventDetectorFactory>();
			list.addAll(recorderDetFactList);
			configClone.setRecorderDetFactList(list);
		}

		if (this.sigDeteEveGeneFactList != null) {
			List<DefaultEventGeneratorFactory> list = new ArrayList<DefaultEventGeneratorFactory>();
			list.addAll(this.sigDeteEveGeneFactList);
			configClone.setSigDeteEveGeneFactList(list);
			configClone.setSignaldetector(true);
		}

		if (this.SigDeteEveDetFactList != null) {
			List<SignalDetectorEventDetectorFactory> list = new ArrayList<SignalDetectorEventDetectorFactory>();
			list.addAll(SigDeteEveDetFactList);
			configClone.setSigDeteEveDetFactList(list);
			configClone.setSignaldetector(true);
		}

		return configClone;
	}

	public SupportedFeatures getSupportedFeatures() {
		return suppFeat;
	}

	protected void setSupportedFeatures(SupportedFeatures suppFeat) {
		this.suppFeat = (SupportedFeaturesImpl) suppFeat;
	}

	public boolean hasStream(StreamType arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String marshall() {
		return this.parser.serialize(this);
	}

	public List<DefaultEventGeneratorFactory> getPlayerGeneFactList() {
		return playerGeneFactList;
	}

	protected void setPlayerGeneFactList(List<DefaultEventGeneratorFactory> playerGeneFactList) {
		this.playerGeneFactList = playerGeneFactList;
	}

	public List<PlayerEventDetectorFactory> getPlayerDetFactList() {
		return playerDetFactList;
	}

	protected void setPlayerDetFactList(List<PlayerEventDetectorFactory> playerDetFactList) {
		this.playerDetFactList = playerDetFactList;
	}

	public List<DefaultEventGeneratorFactory> getRecorderGeneFactList() {
		return recorderGeneFactList;
	}

	protected void setRecorderGeneFactList(List<DefaultEventGeneratorFactory> recorderGeneFactList) {
		this.recorderGeneFactList = recorderGeneFactList;
	}

	public List<RecorderEventDetectorFactory> getRecorderDetFactList() {
		return recorderDetFactList;
	}

	protected void setRecorderDetFactList(List<RecorderEventDetectorFactory> recorderDetFactList) {
		this.recorderDetFactList = recorderDetFactList;
	}

	public boolean isRecorder() {
		return recorder;
	}

	protected void setRecorder(boolean recorder) {
		this.recorder = recorder;
	}

	public boolean isPlayer() {
		return player;
	}

	protected void setPlayer(boolean player) {
		this.player = player;
	}

	public boolean isSignalgenerator() {
		return signalgenerator;
	}

	protected void setSignalgenerator(boolean signalgenerator) {
		this.signalgenerator = signalgenerator;
	}

	public boolean isSignaldetector() {
		return signaldetector;
	}

	protected void setSignaldetector(boolean signaldetector) {
		this.signaldetector = signaldetector;
	}

	public ParametersImpl getParameters() {
		return parameters;
	}

	protected void setParameters(ParametersImpl parameters) {
		this.parameters = parameters;
	}

	public List<DefaultEventGeneratorFactory> getSigDeteEveGeneFactList() {
		return sigDeteEveGeneFactList;
	}

	protected void setSigDeteEveGeneFactList(List<DefaultEventGeneratorFactory> sigDeteEveGeneFactList) {
		this.sigDeteEveGeneFactList = sigDeteEveGeneFactList;
	}

	public List<SignalDetectorEventDetectorFactory> getSigDeteEveDetFactList() {
		return SigDeteEveDetFactList;
	}

	protected void setSigDeteEveDetFactList(List<SignalDetectorEventDetectorFactory> sigDeteEveDetFactList) {
		SigDeteEveDetFactList = sigDeteEveDetFactList;
	}

}
