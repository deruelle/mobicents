package net.java.slee.resource.diameter.cxdx;

/**
 * 
 * Superinterface for Cx/Dx activities.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface CxDxSession {

  /**
   * Returns the session ID of the Cx/Dx session, which uniquely identifies the session.
   * 
   * @return 
   */
  public String getSessionId();

  /**
   * 
   * @return
   */
  public CxDxAVPFactory getCxDxAvpFactory();

  /**
   * 
   * @return
   */
  public CxDxMessageFactory getCxDxMessageFactory();
}
