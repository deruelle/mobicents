/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.ctrl.mgcp.evt;

import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import java.util.List;
import org.mobicents.media.server.ctrl.mgcp.MgcpController;

/**
 *
 * @author kulikov
 */
public class MgcpPackage {

    private String name;
    private int id;
    private MgcpController controller;
    private List<GeneratorFactory> generators;
    private List<DetectorFactory> detectors;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MgcpController getController() {
        return controller;
    }

    public void setController(MgcpController controller) {
        this.controller = controller;
    }

    public List<GeneratorFactory> getGenerators() {
        return generators;
    }

    public void setGenerators(List<GeneratorFactory> signals) {
        this.generators = signals;
        try {
        if (signals != null) {
            for (GeneratorFactory factory : generators) {
                factory.setPackageName(this.name);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DetectorFactory> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<DetectorFactory> events) {
        this.detectors = events;
        if (events != null) {
            for (DetectorFactory factory : detectors) {
                factory.setPackageName(this.name);
            }
        }
    }

    public SignalGenerator getGenerator(MgcpEvent evt) {
        for (GeneratorFactory factory : generators) {
            if (factory.getEventName().equals(evt.getName())) {
                return factory.getInstance(controller, evt.getParms());
            }
        }
        return null;
    }

    public EventDetector getDetector(MgcpEvent evt, RequestedAction[] actions) {
        for (DetectorFactory factory : detectors) {
            if (factory.getEventName().equals(evt.getName())) {
                return factory.getInstance(evt.getParms(), actions);
            }
        }
        return null;
    }
}
