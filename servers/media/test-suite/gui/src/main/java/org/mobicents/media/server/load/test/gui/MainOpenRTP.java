/*
 * MainOpenRTP.java
 *
 * Created on September 2, 2008, 3:17 PM
 */
package org.mobicents.media.server.load.test.gui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.StartEvent;

/**
 *
 * @author  amit bhayani
 */
public class MainOpenRTP extends javax.swing.JDialog {

    Player player = null;

    /** Creates new form MainOpenRTP */
    public MainOpenRTP(java.awt.Frame parent, String clientIP) {
        super(parent);
        initComponents();
        this.clientIP = clientIP;

    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldRTPAddress = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldRTPPort = new javax.swing.JTextField();
        jButtonRTPOk = new javax.swing.JButton();
        jButtonRTPCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.mobicents.media.server.load.test.gui.Main.class).getContext().getResourceMap(MainOpenRTP.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextFieldRTPAddress.setText(resourceMap.getString("jTextFieldRTPAddress.text")); // NOI18N
        jTextFieldRTPAddress.setName("jTextFieldRTPAddress"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jTextFieldRTPPort.setText(resourceMap.getString("jTextFieldRTPPort.text")); // NOI18N
        jTextFieldRTPPort.setName("jTextFieldRTPPort"); // NOI18N

        jButtonRTPOk.setText(resourceMap.getString("jButtonRTPOk.text")); // NOI18N
        jButtonRTPOk.setName("jButtonRTPOk"); // NOI18N
        jButtonRTPOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRTPOkActionPerformed(evt);
            }
        });

        jButtonRTPCancel.setText(resourceMap.getString("jButtonRTPCancel.text")); // NOI18N
        jButtonRTPCancel.setEnabled(false);
        jButtonRTPCancel.setName("jButtonRTPCancel"); // NOI18N
        jButtonRTPCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRTPCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldRTPAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(33, 33, 33)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldRTPPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(157, Short.MAX_VALUE)
                .add(jButtonRTPOk)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonRTPCancel)
                .add(156, 156, 156))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldRTPAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(jTextFieldRTPPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonRTPOk)
                    .add(jButtonRTPCancel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonRTPOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRTPOkActionPerformed
    try {


        String url = "rtp://" + jTextFieldRTPAddress.getText() + ":" + jTextFieldRTPPort.getText();

        System.out.println("Connecting to " + url);

        MediaLocator mrl = new MediaLocator(url);

        player = Manager.createPlayer(mrl);

        ControllerListener listener = new ControllerListenerImpl();
        player.addControllerListener(listener);

        player.realize();

        jButtonRTPOk.setEnabled(false);
        jButtonRTPCancel.setEnabled(true);

    } catch (IOException ex) {
        Logger.getLogger(MainOpenRTP.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoPlayerException ex) {
        Logger.getLogger(MainOpenRTP.class.getName()).log(Level.SEVERE, null, ex);
    }





}//GEN-LAST:event_jButtonRTPOkActionPerformed

private void jButtonRTPCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRTPCancelActionPerformed
// TODO add your handling code here:
    if (player != null) {
        player.stop();
        player = null;
    }

    jButtonRTPOk.setEnabled(true);
    jButtonRTPCancel.setEnabled(false);

}//GEN-LAST:event_jButtonRTPCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRTPCancel;
    private javax.swing.JButton jButtonRTPOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextFieldRTPAddress;
    private javax.swing.JTextField jTextFieldRTPPort;
    // End of variables declaration//GEN-END:variables
    private String clientIP = null;
    private List<Integer> listOfPort;

    private class ControllerListenerImpl implements ControllerListener {

        public void controllerUpdate(ControllerEvent event) {
            System.out.println("controllerUpdate = " + event);

            if (event instanceof StartEvent) {
                System.out.println("controllerUpdate = StartEvent");
            } else if (event instanceof RealizeCompleteEvent) {
                player.start();
                System.out.println("controllerUpdate = start called on player");
            }

        }
    }
}
