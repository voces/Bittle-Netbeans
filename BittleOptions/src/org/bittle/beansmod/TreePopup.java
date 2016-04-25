/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private SyncList syncList;
    
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
                    JOptionPane.showMessageDialog(null, selectedNode.getUserObject());
                    // ADD CODE FOR SHARING FILES HERE
                    // Filename can be accessed through selectedNode.getUserObject()
            }
        });
        stopSharing.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            if(selection != null){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                if(!selectedNode.getUserObject().equals("Bittle Files"))
                    JOptionPane.showMessageDialog(null, selectedNode.getUserObject());
                // ADD CODE FOR SHARING FILES HERE
                // Filename can be accessed through selectedNode.getUserObject()
            }
        });
        remove.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            if(selection != null){
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                treeModel.removeNodeFromParent(selectedNode);
                syncList.remove((String)selectedNode.getUserObject());
                try {
                    Files.deleteIfExists(Paths.get(SyncList.bittlePath + "\\" + (String)selectedNode.getUserObject()));
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
