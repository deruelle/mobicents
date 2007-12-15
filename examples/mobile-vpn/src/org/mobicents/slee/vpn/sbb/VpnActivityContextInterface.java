/**
 * Mobile Virtual Private Network service.
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.slee.vpn.sbb;

import javax.slee.ActivityContextInterface;

import org.mobicents.slee.vpn.profile.MemberProfileCMP;
import org.mobicents.slee.vpn.profile.CosProfileCMP;

/**
 *
 * @author Oleg Kulikov
 */
public interface VpnActivityContextInterface extends ActivityContextInterface  {
    public MemberProfileCMP getMember();
    public void setMember(MemberProfileCMP member);  
    
    public CosProfileCMP getCos();
    public void setCos(CosProfileCMP cos);
}
