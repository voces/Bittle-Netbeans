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
 * Pop Up menu for file tree
 * @author chmar
 */
public class TreePopup extends JPopupMenu{
    
    private Connection connection;
    private boolean waitingForResponse = false;
    
    public TreePopup(JTree tree, DefaultTreeModel treeModel) {
        
        // Get the instance of the Connection
        // Listen for response messages from the server
        // If a response is recieved while waiting for one, handle it
        connection = Connection.getInstance();
        
        // Set up the pop up options
        JMenuItem invite = new JMenuItem("Invite");
        JMenuItem remove = new JMenuItem("Remove");
        
        // invite choice action
        // Prompts the user for who they want to invite
        // Gets user input and attempts to invite that user to the session
        invite.addActionListener((ActionEvent e) ->{
            NotifyDescriptor.InputLine shareMessage = new NotifyDescriptor.InputLine("Username: ",
                                                                                     "Who do you want to invite to the session?", 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE, 
                                                                                     NotifyDescriptor.QUESTION_MESSAGE
                                                                                    );
            shareMessage.setInputText("Enter collaborator's username");
            Object result = DialogDisplayer.getDefault().notify(shareMessage);
            if(result == NotifyDescriptor.OK_OPTION){
                String userName = shareMessage.getInputText();
                NotifyDescriptor nd = new NotifyDescriptor.Message("Sending invitation to: " + userName, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                
                connection.invite(userName);
            }
        });
        
        /**
         * remove Action
         * Gets the selected node from the tree
         * Attempts to remove it from the Share
         * If it does, it will remove it from the tree as well
         */
        remove.addActionListener((ActionEvent e) -> {
            Share share = Share.getInstance();
            TreePath selection = tree.getSelectionPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
            try {
                System.out.println(share.getBittleFilePath((String) selectedNode.getUserObject()));
                share.removeFile((String) selectedNode.getUserObject());
                treeModel.removeNodeFromParent(selectedNode);
            } catch (IOException ex) {
            }
        });
        
        // Add options to the pop up menu
        add(invite);
        add(new JSeparator());
        add(remove);
    }
}
