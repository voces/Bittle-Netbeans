/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.openide.*;

/**
 *
 * @author chmar
 */
public class TreePopup extends JPopupMenu{
    
    private final SyncList syncList;    // Synclist needed for removing files
    
    public TreePopup(JTree tree, DefaultTreeModel treeModel) {
        
        // Get the instance of the SyncList
        syncList = SyncList.getInstance();
        
        // Set up the pop up options
        JMenuItem shareWith = new JMenuItem("Share With...");
        JMenuItem stopSharing = new JMenuItem("Stop Sharing With...");
        JMenuItem remove = new JMenuItem("Remove");
        
        /**
         * shareWith Action
         * Gets the selected node in the tree
         * Asks the user who they want to share the file with
         */
        shareWith.addActionListener((ActionEvent e) -> {
            NotifyDescriptor.InputLine shareMessage = new NotifyDescriptor.InputLine("Username: ",
                                                                                     "Who do you want to share this file with?", 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE, 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE
                                                                                    );
            shareMessage.setInputText("Enter collaborator's username");
            Object result = DialogDisplayer.getDefault().notify(shareMessage);
            if(result == NotifyDescriptor.OK_OPTION){
                TreePath selection = tree.getSelectionPath();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                String userName = shareMessage.getInputText();
                NotifyDescriptor nd = new NotifyDescriptor.Message("TODO: Share " 
                                                                   + selectedNode.getUserObject()
                                                                   + " with "
                                                                   + userName, 
                                                                   NotifyDescriptor.WARNING_MESSAGE
                                                                  );
                DialogDisplayer.getDefault().notify(nd);
            }
        });
        
        /**
         * stopSharing Action
         * Does the same as shareWith with different text
         */
        stopSharing.addActionListener((ActionEvent e) -> {
            NotifyDescriptor.InputLine shareMessage = new NotifyDescriptor.InputLine("Username: ",
                                                                                     "Who do you want to stop sharing this file with?", 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE, 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE
                                                                                    );
            shareMessage.setInputText("Enter collaborator's username");
            Object result = DialogDisplayer.getDefault().notify(shareMessage);
            if(result == NotifyDescriptor.OK_OPTION){
                TreePath selection = tree.getSelectionPath();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
                String userName = shareMessage.getInputText();
                NotifyDescriptor nd = new NotifyDescriptor.Message("TODO: Stop sharing " 
                                                                   + selectedNode.getUserObject()
                                                                   + " with "
                                                                   + userName, 
                                                                   NotifyDescriptor.WARNING_MESSAGE
                                                                  );
                DialogDisplayer.getDefault().notify(nd);
            }
        });
        
        /**
         * remove Action
         * Gets the selected node from the tree
         * Attempts to remove it from the SyncList
         * If it does, it will remove it from the tree as well
         */
        remove.addActionListener((ActionEvent e) -> {
            TreePath selection = tree.getSelectionPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
            try {
                SyncList.removeFile((String) selectedNode.getUserObject());
                treeModel.removeNodeFromParent(selectedNode);
            } catch (IOException ex) {
            }
        });
        
        // Add options to the pop up menu
        add(shareWith);
        add(stopSharing);
        add(new JSeparator());
        add(remove);
    }
}
