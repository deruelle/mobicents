package javax.megaco.pkg.ToneGenPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgSignalItem;

/**
 * The MEGACO Play Tone signal class extends the PkgSignalItem class. This is a
 * final class. This class defines Play Tone signal of MEGACO Tone Generator
 * package. The methods shall define that this signal item belongs to the Tone
 * Generator package.
 */
public final class ToneGenPlayToneSignal extends PkgSignalItem {

	/**
	 * Identifies Play Tone of the MEGACO Tone Generator Package. Its value
	 * shall be set equal to 0x0001.
	 */
	public static final int TONE_GEN_PLAY_TONE_SIGNAL = 0x0001;

	protected int signalId = TONE_GEN_PLAY_TONE_SIGNAL;

	/**
	 * Constructs a Jain MEGACO Object representing signal item of the MEGACO
	 * Package for Signal Play Tone and Package as Tone Gen.
	 */
	public ToneGenPlayToneSignal() {
		super();
		super.itemId = TONE_GEN_PLAY_TONE_SIGNAL;
		super.packageId = new ToneGenPkg();
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Play
	 * Tone signal of Tone Generator Package.
	 */
	public int getItemId() {

		return TONE_GEN_PLAY_TONE_SIGNAL;
	}

	/**
	 * This method is used to get the signal identifier from an Signal Item
	 * object. The implementations of this method in this class returns the id
	 * of the Play Tone signal of Tone Generator Package.
	 * 
	 * @return It shall return {@link TONE_GEN_PLAY_TONE_SIGNAL}.
	 */
	public int getSignalId() {
		return signalId;
	}

	/**
	 * This method gets the package id to which the item belongs. Since the Play
	 * Tone signal is defined in the Tone Generator Package of MEGACO protocol,
	 * this method returns the ToneGenPkg class object.is defined in the Tone
	 * Generator Package of MEGACO protocol, this method returns the ToneGenPkg
	 * class object.
	 * 
	 * @return The package is {@link ToneGenPkg}
	 */
	public MegacoPkg getItemsPkgId() {
		return super.packageId;
	}

}
