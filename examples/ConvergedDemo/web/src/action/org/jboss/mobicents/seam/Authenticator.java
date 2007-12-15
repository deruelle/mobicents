package org.jboss.mobicents.seam;

import javax.ejb.Local;

@Local
public interface Authenticator
{
  boolean authenticate();
}
