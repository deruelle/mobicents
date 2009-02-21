package javax.megaco.pkg.NetworkPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Quality Alert event class extends the PkgEventItem class. This is
 * a final class. This class defines Quality Alert event of MEGACO Network
 * package. The methods shall define that this event item belongs to the Network
 * package.
 */
public final class NetworkQualertEvt extends PkgEventItem {

	/**
	 * Identifies Quality Alert event of the MEGACO Network Package. Its value
	 * shall be set equal to 0x0006.
	 */
	public static final int NETWORK_QLTY_ALERT_EVENT = 0x0006;

	/**
	 * Constructs a Jain MEGACO Object representing event Item of the MEGACO
	 * Package for event Quality alert and package as Network.
	 */
	public NetworkQualertEvt() {
		super();
		super.eventId = NETWORK_QLTY_ALERT_EVENT;
		super.itemId = NETWORK_QLTY_ALERT_EVENT;
		super.packageId = new NetworkPkg();
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Quality Alert event of Network Package.
	 * 
	 * @return It shall return {@link NETWORK_QLTY_ALERT_EVENT}.
	 */
	public int getEventId() {
		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the
	 * Quality Alert event of Network Package.
	 * 
	 * @return It shall return {@link NETWORK_QLTY_ALERT_EVENT}.
	 */
	public int getItemId() {
		return super.itemId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the
	 * Quality Alert event is defined in the Network Package of MEGACO protocol,
	 * this method returns the NetworkPkg class object.
	 * 
	 * @return The package is NetworkPkg.
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
