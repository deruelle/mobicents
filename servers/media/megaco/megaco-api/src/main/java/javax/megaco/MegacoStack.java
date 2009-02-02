package javax.megaco;

/**
 * This interface defines the methods that are required to represent a
 * proprietary JAIN MEGACO protocol stack, the implementation of which will be
 * vendor-specific. Methods are defined for creating and deleting instances of a
 * MegacoProvider.
 * 
 * 
 */
public interface MegacoStack {
	public MegacoProvider createProvider()
			throws javax.megaco.CreateProviderException;

	public void deleteProvider(MegacoProvider MegacoProvider)
			throws javax.megaco.DeleteProviderException;

	public MegacoProvider getProvider(int assocHandle)
			throws javax.megaco.NonExistentAssocException;

	public java.lang.String getProtocolVersion();

	public void setProtocolVersion(java.lang.String protocolVersion)
			throws javax.megaco.VersionNotSupportedException;
}
