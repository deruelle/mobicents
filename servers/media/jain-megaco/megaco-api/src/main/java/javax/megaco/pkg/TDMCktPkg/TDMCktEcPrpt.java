package javax.megaco.pkg.TDMCktPkg;

import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgPrptyItem;

/**
 * The MEGACO Echo Cancellation property class extends the PkgPrptyItem class.
 * This is a final class. This class defines Echo Cancellation property of
 * MEGACO TDM Circuit package. The methods shall define that this property item
 * belongs to the TDM Circuit package.
 */
public class TDMCktEcPrpt extends PkgPrptyItem {

	/**
	 * Identifies Echo cancellation property of the MEGACO TDM Circuit Package. Its value shall be set equal to 0x0008.
	 */
	public static final int TDMC_EC_PRPT = 0x0008;

     
	
	/**
	 * Constructs a Jain MEGACO Object representing property Item of the MEGACO
	 * Package for property Echo Cancellation and Package as TDM Circuit.
	 */
	public TDMCktEcPrpt() {
		super();
		super.itemId = TDMC_EC_PRPT;
		super.propertyId = TDMC_EC_PRPT;
		super.itemValueType = ParamValueType.M_BOOLEAN;
		
	}

	@Override
	public int getItemId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemValueType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPropertyId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
