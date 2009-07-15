package org.mobicents.javax.media.mscontrol.spi;

import java.util.Properties;

import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.spi.Driver;
import javax.media.mscontrol.spi.DriverManager;
import javax.media.mscontrol.spi.PropertyInfo;

import org.mobicents.javax.media.mscontrol.MsControlFactoryImpl;

/**
 * 
 * @author amit bhayani
 *
 */
public class DriverImpl implements Driver {

	public static final String DRIVER_NAME = "org.mobicents.Driver_1.0";

	static {
		DriverManager.registerDriver(new DriverImpl());
		System.out.println("Driver "+DRIVER_NAME +" registered with DriverManager");
	}

	public DriverImpl() {
	}

	public MsControlFactory getFactory(Properties arg0) {
		MsControlFactory msControlFactory = new MsControlFactoryImpl(arg0);
		return msControlFactory;
	}

	public PropertyInfo[] getFactoryPropertyInfo() {
		return null;
	}

	public String getName() {
		return DRIVER_NAME;
	}

}
