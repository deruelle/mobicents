package org.openxdm.xcap.common.appusage;

/**
 * Factory to generate AppUsage instances.
 * @author Eduardo Martins
 *
 */
public interface AppUsageFactory {

	/**
	 * Returns a new AppUsage instance.
	 * @return
	 */
	public AppUsage getAppUsageInstance();
	
	/**
	 * Retreives the id of the AppUsage objecs created by this factory.
	 * @return
	 */
	public String getAppUsageId();
	
}
