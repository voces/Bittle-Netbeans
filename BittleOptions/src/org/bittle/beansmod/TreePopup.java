/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author chmar
 */
public class TreePopup extends JPopupMenu{
    private final SyncList syncList;
    
    public TreePopup(JTree tree, DefaultTreeModel treeModel) {
        syncList = SyncList.getInstance();
        JMenuItem shareWith = new JMenuItem("Share With...");
        JMenuItem stopSharing = new JMenuItem("Stop Sharing With...");
        JMenuItem remove = new JMenuItem("Remove");
        shareWith.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            if(selection != null){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                if(!selectedNode.getUserObject().equals("Bittle Files"))
                    JOptionPane.showMessageDialog(null, "TODO: Share" + selectedNode.getUserObject());
                    // ADD CODE FOR SHARING FILES HERE
                    // Filename can be accessed through selectedNode.getUserObject()
            }
        });
        stopSharing.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            if(selection != null){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                if(!selectedNode.getUserObject().equals("Bittle Files"))
                    JOptionPane.showMessageDialog(null, "TODO: Stop Sharing" + selectedNode.getUserObject());
                // ADD CODE FOR SHARING FILES HERE
                // Filename can be accessed through selectedNode.getUserObject()
            }
        });
        remove.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            if(selection != null){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                try {
                    SyncList.removeFile((String) selectedNode.getUserObject());
                    treeModel.removeNodeFromParent(selectedNode);
                } catch (IOException ex) {
                }
            }
        });
        
        add(shareWith);
        add(stopSharing);
        add(new JSeparator());
        add(remove);
    }
}
