 /*
  * Mobicents: The Open Source SLEE Platform      
  *
  * Copyright 2003-2005, CocoonHive, LLC., 
  * and individual contributors as indicated
  * by the @authors tag. See the copyright.txt 
  * in the distribution for a full listing of   
  * individual contributors.
  *
  * This is free software; you can redistribute it
  * and/or modify it under the terms of the 
  * GNU Lesser General Public License as
  * published by the Free Software Foundation; 
  * either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that 
  * it will be useful, but WITHOUT ANY WARRANTY; 
  * without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
  * PURPOSE. See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the 
  * GNU Lesser General Public
  * License along with this software; 
  * if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, 
  * Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site:
  * http://www.fsf.org.
  */

package org.mobicents.slee.graph.viewer;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.slee.ComponentID;
import javax.slee.SbbID;
import javax.slee.ServiceID;
import javax.slee.UnrecognizedComponentException;
import javax.slee.management.ManagementException;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.swing.JComponent;
import javax.swing.undo.UndoableEdit;

import org.netbeans.graph.api.GraphFactory;
import org.netbeans.graph.api.IGraphEventHandler;
import org.netbeans.graph.api.control.builtin.DefaultViewController;
import org.netbeans.graph.api.model.GraphEvent;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphNode;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.api.model.ability.INameEditable;
import org.netbeans.graph.api.model.builtin.GraphDocument;
import org.netbeans.graph.api.model.builtin.GraphLink;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.api.model.builtin.GraphPort;
import org.netbeans.graph.vmd.VMDDocumentRenderer;
import org.netbeans.graph.vmd.VMDOrderingLogic;
import org.openide.util.Utilities;

/**
 * This class works with Netbeans Graph Library. 
 * It creates the graph model to be rendered in the GUI.
 * 
 * @author David Kaspar, NetBeans Graph Demo
 * @author Ivelin Ivanov
 */
public final class SleeGraph {

    private static final Image RA_IMAGE;
    private static final Image SBB_IMAGE;
    private static final Image Service_IMAGE;
    
    // map of (ComponentID, GraphNode) pairs
    private Map nodes = new HashMap();
    
    static {
    	String dir = SleeGraph.class.getPackage().getName().replace('.', '/') + '/';
    	RA_IMAGE = Utilities.loadImage ( dir + "RA.png"); // RA icon
    	SBB_IMAGE = Utilities.loadImage ( dir + "SBB.png"); // SBB icon
    	Service_IMAGE = Utilities.loadImage ( dir + "service.png"); // Service icon
    }
    	
    private GraphDocument document; // graph document
    private JComponent view; // graph view
    private int counter = 0; // id counter
    private float zoom = 1.0f; // zoom factory
    private GraphEvent selected; // helds selected objects
    
    // creates new graph document and view
    public SleeGraph () {
        document = new GraphDocument ();
        try {
			initDocument();
	        view = GraphFactory.createView (document, new VMDDocumentRenderer (), new SleeGraphViewController (), new SleeGraphEventHandler ());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private SleeGraph (GraphDocument document, JComponent view) {
        this.document = document;
        this.view = view;
    }

    // returns the view
    public JComponent getView () {
        return view;
    }

    private int createID () {
        return counter ++;
    }

    // initializes the document
    private void initDocument () throws Exception {
    	
        // create Services nodes
        ServiceID[] services = MBeanUtils.getServices();
        for (int i = 0; i < services.length; i++) {
            SleeGraphNode node = createServiceNode (services[i].toString());
            document.addComponents (GraphEvent.createSingle (node));
            nodes.put(services[i], node);
        }

        // create SBB nodes
        SbbID[] sbbs = MBeanUtils.getSbbs();
        for (int i = 0; i < sbbs.length; i++) {
            SleeGraphNode node = createSBBNode (sbbs[i].toString());
            document.addComponents (GraphEvent.createSingle (node));
            nodes.put(sbbs[i], node);
        }

        // create RA Type nodes
        ResourceAdaptorTypeID[] rts = MBeanUtils.getResourceAdaptorTypes();
        for (int i = 0; i < rts.length; i++) {
            SleeGraphNode node = createRANode (rts[i].toString());
            document.addComponents (GraphEvent.createSingle (node));
            nodes.put(rts[i], node);
        }
        
        drawReferences();

    }

    private void drawReferences() throws NullPointerException, UnrecognizedComponentException, ManagementException {
    	Iterator iter = nodes.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry entry = (Map.Entry)iter.next();
    		ComponentID cid = (ComponentID)entry.getKey();
    		SleeGraphNode node = (SleeGraphNode)entry.getValue();
    		ComponentID[] refs = MBeanUtils.getReferringComponents(cid);
    		for (int i = 0; i < refs.length; i++) {
    			ComponentID ref = refs[i];
    			SleeGraphNode refNode = (SleeGraphNode)nodes.get(ref);
    			drawReference(refNode, node);
    			System.out.println(ref);
    		}
    	}
	}

    /**
     * draw a directed link from nodeA to nodeB
     * @param refNode
     * @param node
     */
	private void drawReference(SleeGraphNode nodeA, SleeGraphNode nodeB) {
        SleeGraphPort portA = new SleeGraphPort ();
        portA.setSource(true);
        portA.setTarget(false);
        nodeA.addPort (portA);
        portA.setPreferredOrderPosition(new Integer(nodeA.getPorts().length));
        
        SleeGraphPort portB = new SleeGraphPort ();
        portB.setSource(false);
        portB.setTarget(true);
        nodeB.addPort (portB);
        portB.setPreferredOrderPosition(new Integer(nodeB.getPorts().length));
        
        GraphLink link = createLink();
        
        link.setSourcePort ((GraphPort) portA);
        link.setTargetPort ((GraphPort) portB);
        document.addComponents (GraphEvent.createSingle (link));
	}

	// creates node with 4 ports (each port on each side)
    private SleeGraphNode createServiceNode(String name) {
        SleeGraphNode node = new ServiceNode (name);
        return node;
    }

    // creates node with 4 ports (each port on each side)
    private SleeGraphNode createSBBNode(String name) {
        SleeGraphNode node = new SBBNode (name);
        return node;
    }

    // creates node with 4 ports (each port on each side)
    private SleeGraphNode createRANode(String name) {
        SleeGraphNode node = new RANode (name);
        return node;
    }

    // creates link
    private GraphLink createLink () {
        GraphLink link = new GraphLink ();
        link.setID ("link" + createID ());
        return link;
    }

    // menu: realign nodes
    public void realignNodes () {
        GraphFactory.layoutNodes (view);
    }

    // menu: add new node
    public void addNode () {
        SleeGraphNode node = createSBBNode ("New SBB Node");
        document.addComponents (GraphEvent.createSingle (node));
    }
 
    // menu: delete selected
    public void deleteSelected () {
        if (selected == null)
            return;
        IGraphNode[] nodes = selected.getNodes ();
        HashSet links = new HashSet ();
        IGraphLink[] linksArray = selected.getLinks ();
        if (linksArray != null)
            links.addAll (Arrays.asList (linksArray));
        for (int i = 0; i < nodes.length; i++) {
            IGraphNode node = nodes[i];
            IGraphPort[] ports = node.getPorts ();
            if (ports != null) {
	            for (int j = 0; j < ports.length; j++) {
	                IGraphPort port = ports[j];
	                IGraphLink[] portLinks = port.getLinks ();
	                if (portLinks != null)
	                    links.addAll (Arrays.asList (portLinks));
	            }
            }
        }
        document.removeComponents (GraphEvent.create (nodes, (GraphLink[]) links.toArray (new GraphLink[links.size ()])));
    }

    // menu: zoom in
    public void zoomIn () {
        zoom *= 2.0f;
        GraphFactory.setZoom (view, zoom);
    }

    // menu: zoom out
    public void zoomOut () {
        zoom /= 2.0f;
        GraphFactory.setZoom (view, zoom);
    }

    // menu: snap to grid
    public void setSnapToGrid (boolean snapToGrid) {
        GraphFactory.setSnapToGrid (view, snapToGrid);
	view.repaint ();
    }

    // menu: clone view
    public SleeGraph cloneView () {
        return new SleeGraph (document, GraphFactory.cloneView (view, new SleeGraphViewController ()));
    }

    // menu: create new view
    public SleeGraph createNewView () {
        return new SleeGraph (document, GraphFactory.createView (document, new VMDDocumentRenderer (), new SleeGraphViewController (), new SleeGraphEventHandler ()));
    }

    // a node model that extends default built-in implementation and adds support for display name renaming
    public abstract class SleeGraphNode extends GraphNode implements INameEditable {

        public SleeGraphNode (String name) {
        	setDisplayName(name);
            setID ("node" + createID ());
            setPortsOrderingLogic (new VMDOrderingLogic ());
        }

        public boolean canRename () {
            return true;
        }

        public String getName () {
            return getDisplayName ();
        }

        public void setName (String name) {
            setDisplayName (name);
        }

    }
    
    public class RANode extends SleeGraphNode {
    	public RANode(String name) {
    		super(name);
    		setIcon(RA_IMAGE);
    	}
    }

    public class SBBNode extends SleeGraphNode {
    	public SBBNode(String name) {
    		super(name);
    		setIcon(SBB_IMAGE);
    	}
    }

    public class ServiceNode extends SleeGraphNode {
    	public ServiceNode(String name) {
    		super(name);
    		setIcon(Service_IMAGE);
    	}
    }

    
    // a port model that extends default built-in implementation and adds support for display name renaming
    public class SleeGraphPort extends GraphPort implements INameEditable {

        public SleeGraphPort () {
            setID ("port" + createID ());
            setDisplayName ("Port");
            setSource (true);
            setTarget (true);
        }

        public boolean canRename () {
            return true;
        }

        public String getName () {
            return getDisplayName ();
        }

        public void setName (String name) {
            setDisplayName (name);
        }

    }

    // a port model that extends default built-in implementation and adds support for display name renaming and allows to be used/attached by one link only at the time
    public class SingleLinkPort extends SleeGraphPort {

        public boolean isSource () {
            IGraphLink[] links = getLinks ();
            return links == null  ||  links.length == 0;
        }

        public boolean isTarget () {
            IGraphLink[] links = getLinks ();
            return links == null || links.length == 0;
        }

    }

    // defines an event handler that processes all model events that come from view controller
    public class SleeGraphEventHandler extends IGraphEventHandler {

        // called when model should change source port of a link
        public void setSourcePort (IGraphLink link, IGraphPort sourcePort) {
            ((GraphLink) link).setSourcePort ((GraphPort) sourcePort);
        }

        // called when model should change target port of a link
        public void setTargetPort (IGraphLink link, IGraphPort targetPort) {
            ((GraphLink) link).setTargetPort ((GraphPort) targetPort);
        }

        // called when view controller needs to observe whether a link could be created
        public boolean isLinkCreateable (IGraphPort sourcePort, IGraphPort targetPort) {
            return true;
        }

        // called when a link should be created
        public void createLink (IGraphPort sourcePort, IGraphPort targetPort) {
            GraphLink link = SleeGraph.this.createLink ();
            link.setSourcePort ((GraphPort) sourcePort);
            link.setTargetPort ((GraphPort) targetPort);
            document.addComponents (GraphEvent.createSingle (link));
        }

        // called when components should be selected
        public void componentsSelected (GraphEvent event) {
            selected = event;
            document.selectComponents (event);
        }

        // called when an user D'N'D-ing is over the scene
        public boolean isAcceptable (IGraphNode node, DataFlavor[] dataFlavors) {
            return false;
        }

        // called when an user D'N'D-dropped
        public void accept (IGraphNode node, Transferable transferable) {
        }

        // called when an undoable edit has to be added into undo/redo queue
        public void undoableEditHappened (UndoableEdit edit) {
        }

        // called when a document has to be notified about modification
        public void notifyModified () {
        }

    }

    // view controller that extends default feel-behavior
    public class SleeGraphViewController extends DefaultViewController {

        // called when an user double-clicks on a component, then if the component is a node, it adds a new port
        protected boolean componentDoubleClicked (Object component, Point point) {
            Object modelComponent = getHelper ().getModelComponent (component);
            if (modelComponent instanceof GraphNode) {
                SleeGraphPort port = new SleeGraphPort ();
                ((GraphNode) modelComponent).addPort (port);
                return true;
            }
            return super.componentDoubleClicked (component, point);
        }

    }

}
