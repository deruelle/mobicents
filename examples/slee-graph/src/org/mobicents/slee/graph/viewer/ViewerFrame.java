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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class defines the UI of the SLEE Graph Viewer.
 * @author David Kaspar, NetBeans Graph Demo
 * @author Ivelin Ivanov
 * 
 */
public final class ViewerFrame extends javax.swing.JFrame {

    SleeGraph graph;

    public ViewerFrame () {
        this (new SleeGraph ());
    }

    public ViewerFrame(SleeGraph graph) {
        this.graph = graph;
        initComponents();
        demoPanel.setViewportView (graph.getView ());
    }

    private String loadResource (String resource) {
        StringBuffer sb = new StringBuffer ();
        try {
            InputStream stream = getClass ().getResourceAsStream (resource);
            if (stream != null) {
                BufferedReader br = new BufferedReader (new InputStreamReader (stream));
                for (;;) {
                    String line = br.readLine ();
                    if (line == null)
                        break;
                    sb.append (line).append ("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return sb.toString ();
    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        demoPanel = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        closeItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        addNodeItem = new javax.swing.JMenuItem();
        deleteItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        realignItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        zoomInItem = new javax.swing.JMenuItem();
        zoomOutItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        snapToGridItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        cloneViewItem = new javax.swing.JMenuItem();
        createNewViewItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mobicents SLEE Graph Viewer");

        getContentPane().add(demoPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setMnemonic('F');
        jMenu1.setText("File");
        closeItem.setMnemonic('C');
        closeItem.setText("Close");
        closeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeItemActionPerformed(evt);
            }
        });

        jMenu1.add(closeItem);

        exitItem.setMnemonic('X');
        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });

        jMenu1.add(exitItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setMnemonic('E');
        jMenu2.setText("Edit");
        addNodeItem.setMnemonic('A');
        addNodeItem.setText("Add Node");
        addNodeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNodeItemActionPerformed(evt);
            }
        });

        jMenu2.add(addNodeItem);

        deleteItem.setMnemonic('D');
        deleteItem.setText("Delete Selected");
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });

        jMenu2.add(deleteItem);

        jMenu2.add(jSeparator1);

        realignItem.setMnemonic('R');
        realignItem.setText("Realign Nodes");
        realignItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realignItemActionPerformed(evt);
            }
        });

        jMenu2.add(realignItem);

        jMenuBar1.add(jMenu2);

        jMenu3.setMnemonic('V');
        jMenu3.setText("View");
        zoomInItem.setMnemonic('I');
        zoomInItem.setText("Zoom In");
        zoomInItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInItemActionPerformed(evt);
            }
        });

        jMenu3.add(zoomInItem);

        zoomOutItem.setMnemonic('O');
        zoomOutItem.setText("Zoom Out");
        zoomOutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutItemActionPerformed(evt);
            }
        });

        jMenu3.add(zoomOutItem);

        jMenu3.add(jSeparator2);

        snapToGridItem.setMnemonic('S');
        snapToGridItem.setSelected(true);
        snapToGridItem.setText("Snap to Grid");
        snapToGridItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapToGridItemActionPerformed(evt);
            }
        });

        jMenu3.add(snapToGridItem);

        jMenu3.add(jSeparator3);

        cloneViewItem.setMnemonic('C');
        cloneViewItem.setText("Clone View");
        cloneViewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cloneViewItemActionPerformed(evt);
            }
        });

        jMenu3.add(cloneViewItem);

        createNewViewItem.setMnemonic('N');
        createNewViewItem.setText("Create New View");
        createNewViewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewViewItemActionPerformed(evt);
            }
        });

        jMenu3.add(createNewViewItem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-700)/2, (screenSize.height-500)/2, 700, 500);
    }// </editor-fold>//GEN-END:initComponents

    private void closeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeItemActionPerformed
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeItemActionPerformed

    private void createNewViewItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewViewItemActionPerformed
        new ViewerFrame (graph.createNewView ()).setVisible (true);
    }//GEN-LAST:event_createNewViewItemActionPerformed

    private void cloneViewItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cloneViewItemActionPerformed
        new ViewerFrame (graph.cloneView ()).setVisible (true);
    }//GEN-LAST:event_cloneViewItemActionPerformed

    private void snapToGridItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapToGridItemActionPerformed
        graph.setSnapToGrid (snapToGridItem.isSelected ());
    }//GEN-LAST:event_snapToGridItemActionPerformed

    private void zoomOutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutItemActionPerformed
        graph.zoomOut ();
    }//GEN-LAST:event_zoomOutItemActionPerformed

    private void zoomInItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInItemActionPerformed
        graph.zoomIn ();
    }//GEN-LAST:event_zoomInItemActionPerformed

    private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
        graph.deleteSelected ();
    }//GEN-LAST:event_deleteItemActionPerformed

    private void addNodeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNodeItemActionPerformed
        graph.addNode ();
    }//GEN-LAST:event_addNodeItemActionPerformed

    private void realignItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realignItemActionPerformed
        graph.realignNodes ();
    }//GEN-LAST:event_realignItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        System.exit (0);
    }//GEN-LAST:event_exitItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	Thread.currentThread().setContextClassLoader( ViewerFrame.class.getClassLoader() );
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addNodeItem;
    private javax.swing.JMenuItem cloneViewItem;
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JMenuItem createNewViewItem;
    private javax.swing.JMenuItem deleteItem;
    private javax.swing.JScrollPane demoPanel;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenuItem realignItem;
    private javax.swing.JCheckBoxMenuItem snapToGridItem;
    private javax.swing.JMenuItem zoomInItem;
    private javax.swing.JMenuItem zoomOutItem;
    // End of variables declaration//GEN-END:variables

}
