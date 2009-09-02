/**
 * Start time:20:09:02 2009-09-02<br>
 * Project: mobicents-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.transaction;

/**
 * Represents something that needs to happen when a SLEE transaction ends (commit or rollback)
 * Implements the command pattern
 * 
 * @author Tim
 *
 */
public interface TransactionalAction {
    public void execute();
}
