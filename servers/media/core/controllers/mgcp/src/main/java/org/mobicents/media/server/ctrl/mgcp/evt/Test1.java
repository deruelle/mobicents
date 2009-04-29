/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.ctrl.mgcp.evt;

import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

/**
 *
 * @author kulikov
 */
public class Test1 {
    public static void main(String[] args) throws Exception {
        PackageName pkg = PackageName.factory("AAA");
        PackageName pkg1 = PackageName.factory("AAB");
        System.out.println(MgcpEvent.oc.toString());
    }
}
