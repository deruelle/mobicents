/**
 * Start time:12:19:59 2009-09-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import org.mobicents.ss7.isup.ISUPMessageFactory;
import org.mobicents.ss7.isup.message.AddressCompleteMessage;
import org.mobicents.ss7.isup.message.AnswerMessage;
import org.mobicents.ss7.isup.message.ApplicationTransportMessage;
import org.mobicents.ss7.isup.message.BlockingAckMessage;
import org.mobicents.ss7.isup.message.BlockingMessage;
import org.mobicents.ss7.isup.message.CallProgressMessage;
import org.mobicents.ss7.isup.message.ChargeInformationMessage;
import org.mobicents.ss7.isup.message.CircuitGroupBlockingAckMessage;
import org.mobicents.ss7.isup.message.CircuitGroupBlockingMessage;
import org.mobicents.ss7.isup.message.CircuitGroupQueryMessage;
import org.mobicents.ss7.isup.message.CircuitGroupQueryResponseMessage;
import org.mobicents.ss7.isup.message.CircuitGroupResetAckMessage;
import org.mobicents.ss7.isup.message.CircuitGroupResetMessage;
import org.mobicents.ss7.isup.message.CircuitGroupUnblockingAckMessage;
import org.mobicents.ss7.isup.message.CircuitGroupUnblockingMessage;
import org.mobicents.ss7.isup.message.ConfusionMessage;
import org.mobicents.ss7.isup.message.ConnectMessage;
import org.mobicents.ss7.isup.message.ContinuityCheckRequestMessage;
import org.mobicents.ss7.isup.message.ContinuityMessage;
import org.mobicents.ss7.isup.message.FacilityAcceptedMessage;
import org.mobicents.ss7.isup.message.FacilityMessage;
import org.mobicents.ss7.isup.message.FacilityRejectedMessage;
import org.mobicents.ss7.isup.message.FacilityRequestMessage;
import org.mobicents.ss7.isup.message.ForwardTransferMessage;
import org.mobicents.ss7.isup.message.ISUPMessage;
import org.mobicents.ss7.isup.message.IdentificationRequestMessage;
import org.mobicents.ss7.isup.message.IdentificationResponseMessage;
import org.mobicents.ss7.isup.message.InformationMessage;
import org.mobicents.ss7.isup.message.InformationRequestMessage;
import org.mobicents.ss7.isup.message.InitialAddressMessage;
import org.mobicents.ss7.isup.message.LoopPreventionMessage;
import org.mobicents.ss7.isup.message.LoopbackAckMessage;
import org.mobicents.ss7.isup.message.NetworkResourceManagementMessage;
import org.mobicents.ss7.isup.message.OverloadMessage;
import org.mobicents.ss7.isup.message.PassAlongMessage;
import org.mobicents.ss7.isup.message.PreReleaseInformationMessage;
import org.mobicents.ss7.isup.message.ReleaseCompleteMessage;
import org.mobicents.ss7.isup.message.ReleaseMessage;
import org.mobicents.ss7.isup.message.ResetCircuitMessage;
import org.mobicents.ss7.isup.message.ResumeMessage;
import org.mobicents.ss7.isup.message.SegmentationMessage;
import org.mobicents.ss7.isup.message.SubsequentAddressMessage;
import org.mobicents.ss7.isup.message.SubsequentDirectoryNumberMessage;
import org.mobicents.ss7.isup.message.SuspendMessage;
import org.mobicents.ss7.isup.message.UnblockingAckMessage;
import org.mobicents.ss7.isup.message.UnblockingMessage;
import org.mobicents.ss7.isup.message.UnequippedCICMessage;
import org.mobicents.ss7.isup.message.User2UserInformationMessage;
import org.mobicents.ss7.isup.message.UserPartAvailableMessage;
import org.mobicents.ss7.isup.message.UserPartTestMessage;

/**
 * Start time:12:19:59 2009-09-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class ISUPMessageFactoryImpl implements ISUPMessageFactory {

	private ISUPProviderImpl providerImpl;

	/**
	 * 
	 * @param providerImpl
	 */
	public ISUPMessageFactoryImpl(ISUPProviderImpl providerImpl) {
		this.providerImpl = providerImpl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createACM()
	 */
	public AddressCompleteMessage createACM() {
		return new AddressCompleteMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createANM()
	 */
	public AnswerMessage createANM() {
		return new AnswerMessageImpl(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createAPT()
	 */
	public ApplicationTransportMessage createAPT() {
		return new ApplicationTransportMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createBLA()
	 */
	public BlockingAckMessage createBLA() {
		return new BlockingAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createBLO()
	 */
	public BlockingMessage createBLO() {
		return new BlockingMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCCR()
	 */
	public ContinuityCheckRequestMessage createCCR() {
		return new ContinuityCheckRequestMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCGB()
	 */
	public CircuitGroupBlockingMessage createCGB() {
		return new CircuitGroupBlockingMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCGBA()
	 */
	public CircuitGroupBlockingAckMessage createCGBA() {
		return new CircuitGroupBlockingAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCGU()
	 */
	public CircuitGroupUnblockingMessage createCGU() {
		return new CircuitGroupUnblockingMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCGUA()
	 */
	public CircuitGroupUnblockingAckMessage createCGUA() {
		return new CircuitGroupUnblockingAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCIM()
	 */
	public ChargeInformationMessage createCIM() {
		return new ChargeInformationMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCNF()
	 */
	public ConfusionMessage createCNF() {
		return new ConfusionMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCON()
	 */
	public ConnectMessage createCON() {
		return new ConnectMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCOT()
	 */
	public ContinuityMessage createCOT() {
		return new ContinuityMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCPG()
	 */
	public CallProgressMessage createCPG() {
		return new CallProgressMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCQM()
	 */
	public CircuitGroupQueryMessage createCQM() {
		return new CircuitGroupQueryMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCQR()
	 */
	public CircuitGroupQueryResponseMessage createCQR() {
		return new CircuitGroupQueryResponseMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createFAA()
	 */
	public FacilityAcceptedMessage createFAA() {
		return new FacilityAcceptedMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createFAC()
	 */
	public FacilityMessage createFAC() {
		return new FacilityMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createFAR()
	 */
	public FacilityRequestMessage createFAR() {
		return new FacilityRequestMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createFOT()
	 */
	public ForwardTransferMessage createFOT() {
		return new ForwardTransferMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createFRJ()
	 */
	public FacilityRejectedMessage createFRJ() {
		return new FacilityRejectedMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createGRA()
	 */
	public CircuitGroupResetAckMessage createGRA() {
		return new CircuitGroupResetAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createGRS()
	 */
	public CircuitGroupResetMessage createGRS() {
		return new CircuitGroupResetMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createIAM()
	 */
	public InitialAddressMessage createIAM() {
		return new InitialAddressMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createIDR()
	 */
	public IdentificationRequestMessage createIDR() {
		return new IdentificationRequestMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createINF()
	 */
	public InformationMessage createINF() {
		return new InformationMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createINR()
	 */
	public InformationRequestMessage createINR() {
		return new InformationRequestMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createIRS()
	 */
	public IdentificationResponseMessage createIRS() {
		return new IdentificationResponseMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createLPA()
	 */
	public LoopbackAckMessage createLPA() {
		return new LoopbackAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createLPP()
	 */
	public LoopPreventionMessage createLPP() {
		return new LoopPreventionMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createNRM()
	 */
	public NetworkResourceManagementMessage createNRM() {
		return new NetworkResourceManagementMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createOLM()
	 */
	public OverloadMessage createOLM() {
		return new OverloadMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createPAM()
	 */
	public PassAlongMessage createPAM() {
		return new PassAlongMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createPRI()
	 */
	public PreReleaseInformationMessage createPRI() {
		return new PreReleaseInformationMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createREL()
	 */
	public ReleaseMessage createREL() {
		return new ReleaseMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createRES()
	 */
	public ResumeMessage createRES() {
		return new ResumeMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createRLC()
	 */
	public ReleaseCompleteMessage createRLC() {
		return new ReleaseCompleteMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createRSC()
	 */
	public ResetCircuitMessage createRSC() {
		return new ResetCircuitMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createSAM()
	 */
	public SubsequentAddressMessage createSAM() {
		return new SubsequentAddressMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createSDN()
	 */
	public SubsequentDirectoryNumberMessage createSDN() {
		return new SubsequentDirectoryNumberMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createSGM()
	 */
	public SegmentationMessage createSGM() {
		return new SegmentationMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createSUS()
	 */
	public SuspendMessage createSUS() {
		return new SuspendMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUBA()
	 */
	public UnblockingAckMessage createUBA() {
		return new UnblockingAckMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUBL()
	 */
	public UnblockingMessage createUBL() {
		return new UnblockingMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUCIC()
	 */
	public UnequippedCICMessage createUCIC() {
		return new UnequippedCICMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUPA()
	 */
	public UserPartAvailableMessage createUPA() {
		return new UserPartAvailableMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUPT()
	 */
	public UserPartTestMessage createUPT() {
		return new UserPartTestMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createUSR()
	 */
	public User2UserInformationMessage createUSR() {
		return new User2UserInformationMessageImpl(providerImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageFactory#createCommand(int)
	 */
	public ISUPMessage createCommand(int commandCode) {
		switch (commandCode) {
		case ISUPMessage._MESSAGE_CODE_IAM:
			InitialAddressMessage IAM = createIAM();
			return IAM;
		case ISUPMessage._MESSAGE_CODE_ACM:
			AddressCompleteMessage ACM = createACM();
			return ACM;
		case ISUPMessage._MESSAGE_CODE_REL:
			ReleaseMessage REL = createREL();
			return REL;
		case ISUPMessage._MESSAGE_CODE_RLC:
			ReleaseCompleteMessage RLC = createRLC();
			return RLC;

		case ISUPMessage._MESSAGE_CODE_APT:
			ApplicationTransportMessage APT = createAPT();
			return APT;

		case ISUPMessage._MESSAGE_CODE_ANM:
			AnswerMessage ANM = createANM();
			return ANM;

		case ISUPMessage._MESSAGE_CODE_CPG:
			CallProgressMessage CPG = createCPG();
			return CPG;

		case ISUPMessage._MESSAGE_CODE_GRA:
			CircuitGroupResetAckMessage GRA = createGRA();
			return GRA;

		case ISUPMessage._MESSAGE_CODE_CFN:
			ConfusionMessage CFN = createCNF();
			return CFN;

		case ISUPMessage._MESSAGE_CODE_CON:
			ConnectMessage CON = createCON();
			return CON;

		case ISUPMessage._MESSAGE_CODE_COT:
			ContinuityMessage COT = createCOT();
			return COT;

		case ISUPMessage._MESSAGE_CODE_FRJ:
			FacilityRejectedMessage FRJ = createFRJ();
			return FRJ;

		case ISUPMessage._MESSAGE_CODE_INF:
			InformationMessage INF = createINF();
			return INF;

		case ISUPMessage._MESSAGE_CODE_INR:
			InformationRequestMessage INR = createINR();
			return INR;

		case ISUPMessage._MESSAGE_CODE_SAM:
			SubsequentAddressMessage SAM = createSAM();
			return SAM;

		case ISUPMessage._MESSAGE_CODE_SDN:
			SubsequentDirectoryNumberMessage SDN = createSDN();
			return SDN;

		case ISUPMessage._MESSAGE_CODE_FOT:
			ForwardTransferMessage FOT = createFOT();
			return FOT;

		case ISUPMessage._MESSAGE_CODE_RES:
			ResumeMessage RES = createRES();
			return RES;
		case ISUPMessage._MESSAGE_CODE_BLO:
			BlockingMessage BLO = createBLO();
			return BLO;

		case ISUPMessage._MESSAGE_CODE_BLA:
			BlockingAckMessage BLA = createBLA();
			return BLA;

		case ISUPMessage._MESSAGE_CODE_CCR:
			ContinuityCheckRequestMessage CCR = createCCR();
			return CCR;

		case ISUPMessage._MESSAGE_CODE_LPA:
			LoopbackAckMessage LPA = createLPA();
			return LPA;

		case ISUPMessage._MESSAGE_CODE_LPP:
			LoopPreventionMessage LPP = createLPP();
			return LPP;

		case ISUPMessage._MESSAGE_CODE_OLM:
			OverloadMessage OLM = createOLM();
			return OLM;

		case ISUPMessage._MESSAGE_CODE_SUS:
			SuspendMessage SUS = createSUS();
			return SUS;

		case ISUPMessage._MESSAGE_CODE_RSC:
			ResetCircuitMessage RSC = createRSC();
			return RSC;

		case ISUPMessage._MESSAGE_CODE_UBL:
			UnblockingMessage UBL = createUBL();
			return UBL;

		case ISUPMessage._MESSAGE_CODE_UBA:
			UnblockingAckMessage UBA = createUBA();
			return UBA;

		case ISUPMessage._MESSAGE_CODE_UCIC:
			UnequippedCICMessage UCIC = createUCIC();
			return UCIC;

		case ISUPMessage._MESSAGE_CODE_CGB:
			CircuitGroupBlockingMessage CGB = createCGB();
			return CGB;

		case ISUPMessage._MESSAGE_CODE_CGBA:
			CircuitGroupBlockingAckMessage CGBA = createCGBA();
			return CGBA;

		case ISUPMessage._MESSAGE_CODE_CGU:
			CircuitGroupUnblockingMessage CGU = createCGU();
			return CGU;

		case ISUPMessage._MESSAGE_CODE_CGUA:
			CircuitGroupUnblockingAckMessage CGUA = createCGUA();
			return CGUA;

		case ISUPMessage._MESSAGE_CODE_GRS:
			CircuitGroupResetMessage GRS = createGRS();
			return GRS;

		case ISUPMessage._MESSAGE_CODE_CQR:
			CircuitGroupQueryResponseMessage CQR = createCQR();
			return CQR;

		case ISUPMessage._MESSAGE_CODE_CQM:
			CircuitGroupQueryMessage CQM = createCQM();
			return CQM;

		case ISUPMessage._MESSAGE_CODE_FAA:
			FacilityAcceptedMessage FAA = createFAA();
			return FAA;

		case ISUPMessage._MESSAGE_CODE_FAR:
			FacilityRequestMessage FAR = createFAR();
			return FAR;

		case ISUPMessage._MESSAGE_CODE_PAM:
			PassAlongMessage PAM = createPAM();
			return PAM;

		case ISUPMessage._MESSAGE_CODE_PRI:
			PreReleaseInformationMessage PRI = createPRI();
			return PRI;

		case ISUPMessage._MESSAGE_CODE_FAC:
			FacilityMessage FAC = createFAC();
			return FAC;

		case ISUPMessage._MESSAGE_CODE_NRM:
			NetworkResourceManagementMessage NRM = createNRM();
			return NRM;

		case ISUPMessage._MESSAGE_CODE_IDR:
			IdentificationRequestMessage IDR = createIDR();
			return IDR;

		case ISUPMessage._MESSAGE_CODE_IRS:
			IdentificationResponseMessage IRS = createIRS();
			return IRS;

		case ISUPMessage._MESSAGE_CODE_SGM:
			SegmentationMessage SGM = createSGM();
			return SGM;

		case ISUPMessage._MESSAGE_CODE_CIM:
			ChargeInformationMessage CIM = createCIM();
			return CIM;

		case ISUPMessage._MESSAGE_CODE_UPA:
			UserPartAvailableMessage UPA = createUPA();
			return UPA;

		case ISUPMessage._MESSAGE_CODE_UPT:
			UserPartTestMessage UPT = createUPT();
			return UPT;

		case ISUPMessage._MESSAGE_CODE_USR:
			User2UserInformationMessage USR = createUSR();
			return USR;
		default:
			throw new IllegalArgumentException("Not supported comamnd code: " + commandCode);
		}
	}
}
