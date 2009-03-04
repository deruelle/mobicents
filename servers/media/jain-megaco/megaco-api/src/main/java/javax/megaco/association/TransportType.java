package javax.megaco.association;

import java.io.Serializable;

public class TransportType implements Serializable {
	public static final int M_TCP_TPT = 1;
	public static final int M_UDP_TPT = 2;
	public static final int M_SCTP_TPT = 3;
	public static final int M_ATM_TPT = 4;
	public static final int M_MTP3B_TPT = 5;

	public static final TransportType TCP_TPT = new TransportType(M_TCP_TPT);
	public static final TransportType UDP_TPT = new TransportType(M_UDP_TPT);
	public static final TransportType SCTP_TPT = new TransportType(M_SCTP_TPT);
	public static final TransportType ATM_TPT = new TransportType(M_ATM_TPT);
	public static final TransportType MTP3B_TPT = new TransportType(M_MTP3B_TPT);

	private int transport_type;

	private TransportType(int transport_type) {
		this.transport_type = transport_type;
	}

	public int getTransportType() {
		return this.transport_type;
	}

	public static final TransportType getObject(int value) throws IllegalArgumentException {
		TransportType t = null;
		switch (value) {
		case M_TCP_TPT:
			t = TCP_TPT;
			break;

		case M_UDP_TPT:
			t = UDP_TPT;
			break;

		case M_SCTP_TPT:
			t = SCTP_TPT;
			break;

		case M_ATM_TPT:
			t = ATM_TPT;
			break;

		case M_MTP3B_TPT:
			t = MTP3B_TPT;
			break;
		default:
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException("No TransportType defined for value = " + value);
			throw illegalArgumentException;
		}
		return t;

	}

	private Object readResolve() {
		return this.getObject(this.transport_type);
	}

	@Override
	public String toString() {
		String t = null;
		switch (this.transport_type) {
		case M_TCP_TPT:
			t = "TransportType[TCP_TPT]";
			break;

		case M_UDP_TPT:
			t = "TransportType[UDP_TPT]";
			break;

		case M_SCTP_TPT:
			t = "TransportType[SCTP_TPT]";
			break;

		case M_ATM_TPT:
			t = "TransportType[ATM_TPT]";
			break;

		case M_MTP3B_TPT:
			t = "TransportType[MTP3B_TPT]";
			break;
		default:
			t = "TransportType[" + this.transport_type + "]";
		}
		return t;
	}

}
