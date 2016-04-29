package org.bittle.beansmod;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.bittle.messages.*;
import org.openide.*;

/**
 *
 * @author chmar
 */
public class TreePopup extends JPopupMenu{
    
    private Connection connection;
    private boolean waitingForResponse = false;
    
    public TreePopup(JTree tree, DefaultTreeModel treeModel) {
        
        // Get the instance of the Connection
        connection = Connection.getInstance();
        connection.addMessageListener((Message m) -> {
            if(m instanceof Response){
                if(waitingForResponse){
                    waitingForResponse = false;
                    checkResponse((Response) m);
                }
            }
        });
        
        // Set up the pop up options
        JMenuItem invite = new JMenuItem("Invite");
        JMenuItem remove = new JMenuItem("Remove");
        
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
                
                waitingForResponse = true;
                connection.invite(userName);
                waitForResponse();
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

    private void checkResponse(Response r){
        if(r.getStatus().equals("failed")){
            NotifyDescriptor nd = new NotifyDescriptor.Message(r.getReason(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            NotifyDescriptor nd = new NotifyDescriptor.Message("Invitation Sucessful!", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    private void waitForResponse() {
        int timer = 0;
        while(waitingForResponse){
            if(timer > 100){
                NotifyDescriptor nd = new NotifyDescriptor.Message("Waiting for server timed out...", NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ex) {
            }
            timer ++;
        }
    }
}
