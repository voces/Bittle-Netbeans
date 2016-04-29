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
import static org.bittle.beansmod.Connection.response;
import org.openide.*;

/**
 *
 * @author chmar
 */
public class TreePopup extends JPopupMenu{
    
    private final SyncList syncList;    // Synclist needed for removing files
    private Connection connection;
    
    public TreePopup(JTree tree, DefaultTreeModel treeModel) {
        
        // Get the instance of the SyncList
        syncList = SyncList.getInstance();
        
        // Get the instance of the Connection
        connection = Connection.getInstance();
        
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
                connection.invite(userName);
                
                // Wait for response from the server
                while(response == null)
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException ex) {
                    }
                
                if(connection.checkResponse("invite")){
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Sent " 
                                                                   + userName
                                                                   + " an invitation to join your session.", 
                                                                   NotifyDescriptor.INFORMATION_MESSAGE
                                                                  );
                    DialogDisplayer.getDefault().notify(nd);
                }
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
                syncList.removeFile((String) selectedNode.getUserObject());
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
