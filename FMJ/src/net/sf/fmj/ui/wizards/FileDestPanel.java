/*
 * ChooseDestPanel.java
 *
 * Created on June 20, 2007, 12:51 PM
 */

package net.sf.fmj.ui.wizards;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import net.sf.fmj.ui.utils.ErrorDialog;

/**
 *
 * @author Ken Larson
 */
public class FileDestPanel extends javax.swing.JPanel {
    
    /** Creates new form ChooseDestPanel */
    public FileDestPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelFile = new javax.swing.JLabel();
        textFile = new javax.swing.JTextField();
        buttonBrowse = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        labelFile.setText("Destination file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(labelFile, gridBagConstraints);

        textFile.setPreferredSize(new java.awt.Dimension(250, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(textFile, gridBagConstraints);

        buttonBrowse.setText("Browse...");
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(buttonBrowse, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseActionPerformed
        final JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try {
                textFile.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException ex) {
                ErrorDialog.showError(this, ex);
            }
        }
    }//GEN-LAST:event_buttonBrowseActionPerformed
    
    public JTextField getTextFile()
    {
        return textFile;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBrowse;
    private javax.swing.JLabel labelFile;
    private javax.swing.JTextField textFile;
    // End of variables declaration//GEN-END:variables
    
}
