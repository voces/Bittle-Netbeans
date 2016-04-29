/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.TopComponent;
import org.openide.util.NbPreferences;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bittle.beansmod//BittleTree//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BittleTree",
        iconBase = "org/bittle/beansmod/BittleLogo16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.bittle.beansmod.BittleTreeTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window" /*, position = 333 */),
    @ActionReference(path = "Shortcuts", name = "D-B")
})
@TopComponent.OpenActionRegistration(
        displayName = "BittleFiles",
        preferredID = "BittleTree"
)
public final class BittleTreeTopComponent extends TopComponent {
    
    private Preferences loginState = NbPreferences.forModule(BittlePanel.class);
    private boolean loggedIn;
    private final TreePopup treePopup;
    private SyncList syncList = SyncList.getInstance();

    public BittleTreeTopComponent() {
        initComponents();
        treePopup = new TreePopup(fileTree, treeModel);
        setName("Bittle Files");
        setToolTipText("These are the files being synced by Bittle");   
    }
    
    public void open() {
        Mode m = WindowManager.getDefault().findMode ("explorer");
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
        updateTree();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoggedInScreen = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileTree = new javax.swing.JTree();
        RemoveAllButton = new javax.swing.JButton();
        NotLoggedInScreen = new javax.swing.JPanel();
        FlipGuy = new javax.swing.JLabel();
        NotLoggedInMessage = new javax.swing.JLabel();
        ToOptionsButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        rootNode = new DefaultMutableTreeNode("Bittle Files");
        treeModel = new DefaultTreeModel(rootNode);
        fileTree = new JTree(treeModel);
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTree.setShowsRootHandles(true);
        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileTreeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fileTree);

        org.openide.awt.Mnemonics.setLocalizedText(RemoveAllButton, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.RemoveAllButton.text")); // NOI18N
        RemoveAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LoggedInScreenLayout = new javax.swing.GroupLayout(LoggedInScreen);
        LoggedInScreen.setLayout(LoggedInScreenLayout);
        LoggedInScreenLayout.setHorizontalGroup(
            LoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoggedInScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoggedInScreenLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton)
                        .addGap(18, 18, 18)
                        .addComponent(RemoveAllButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                .addContainerGap())
        );
        LoggedInScreenLayout.setVerticalGroup(
            LoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LoggedInScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(LoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(RemoveAllButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        FlipGuy.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(FlipGuy, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.FlipGuy.text")); // NOI18N

        NotLoggedInMessage.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(NotLoggedInMessage, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.NotLoggedInMessage.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ToOptionsButton, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.ToOptionsButton.text")); // NOI18N
        ToOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToOptionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NotLoggedInScreenLayout = new javax.swing.GroupLayout(NotLoggedInScreen);
        NotLoggedInScreen.setLayout(NotLoggedInScreenLayout);
        NotLoggedInScreenLayout.setHorizontalGroup(
            NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotLoggedInScreenLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(FlipGuy)
                    .addComponent(NotLoggedInMessage)
                    .addComponent(ToOptionsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        NotLoggedInScreenLayout.setVerticalGroup(
            NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotLoggedInScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FlipGuy)
                .addGap(18, 18, 18)
                .addComponent(NotLoggedInMessage)
                .addGap(18, 18, 18)
                .addComponent(ToOptionsButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(NotLoggedInScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(38, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(138, 138, 138)
                    .addComponent(NotLoggedInScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(138, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int choice = fileChooser.showOpenDialog(null);

        if(choice == JFileChooser.APPROVE_OPTION){
            String filePath = fileChooser.getSelectedFile().toString();
            try {
                syncList.addFile(filePath);
            } catch (IOException ex) {
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void fileTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTreeMouseClicked
        
        // Get the selected row
        int row = fileTree.getClosestRowForLocation(evt.getX(), evt.getY());
        fileTree.setSelectionRow(row);
        TreePath selection = fileTree.getSelectionPath();
        if(selection != null){
            
            // Get the selected node
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getLastPathComponent();
            
            // If it is not the root node
            if(!selectedNode.getUserObject().equals("Bittle Files")){
                
                // If the user right clicked, show the pop up menu
                if (SwingUtilities.isRightMouseButton(evt))
                    treePopup.show(evt.getComponent(), evt.getX(), evt.getY());
                
                // Otherwise
                else if (evt.getClickCount() == 2){
                    // Get the file object from the selected node
                    FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(syncList.getBittleFilePath((String)selectedNode.getUserObject()))));
                    try {
                        // Open it
                        DataObject.find(fo).getLookup().lookup(OpenCookie.class).open();
                    } catch (DataObjectNotFoundException ex) {
                    }
                }
            }
        }
    }//GEN-LAST:event_fileTreeMouseClicked

    private void ToOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToOptionsButtonActionPerformed
        OptionsDisplayer.getDefault().open("BittleOptions");
    }//GEN-LAST:event_ToOptionsButtonActionPerformed

    private void RemoveAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveAllButtonActionPerformed
        NotifyDescriptor d = new NotifyDescriptor.Confirmation("This will completely remove all the files being synced from your computer!", 
                                                               "Are you sure?",
                                                               NotifyDescriptor.OK_CANCEL_OPTION);
        
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            if(syncList.isEmpty()){
                NotifyDescriptor nd = new NotifyDescriptor.Message("Nothing to Remove...", NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
            else
                try {
                    clearFiles();
                } catch (IOException ex) {
                }
        }

    }//GEN-LAST:event_RemoveAllButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel FlipGuy;
    private javax.swing.JPanel LoggedInScreen;
    private javax.swing.JLabel NotLoggedInMessage;
    private javax.swing.JPanel NotLoggedInScreen;
    private javax.swing.JButton RemoveAllButton;
    private javax.swing.JButton ToOptionsButton;
    private javax.swing.JButton addButton;
    private javax.swing.JTree fileTree;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    
    @Override
    public void componentOpened() {
        updateTree();
    }
    
    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    /**
     * Updates Log In State and Bittle Directory Path
     * - If the user is logged in:
     *    * Shows the contents of the bittle folder 
     * - Otherwise, displays the not logged in screen
     */
    public void updateTree(){
        loggedIn = loginState.getBoolean("status", false);
        if(loggedIn){
            if(syncList == null)
                syncList = SyncList.getInstance();
            syncList.scanFolder();
            treeModel.reload();
            LoggedInScreen.setVisible(true);
            NotLoggedInScreen.setVisible(false);
        }
        else{
            NotLoggedInScreen.setVisible(true);
            LoggedInScreen.setVisible(false);
            NotLoggedInScreen.requestFocusInWindow();
            ToOptionsButton.requestFocusInWindow();
        }
    }
    
   private void clearFiles() throws IOException{
       rootNode.removeAllChildren();
       syncList.clearList();
       treeModel.reload();
   }
   
   public DefaultMutableTreeNode addObject(Object child){
       return addObject(rootNode, child, true);
   }
   
   private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean visible){
       DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
       
       treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
       
       if(visible)
           fileTree.scrollPathToVisible(new TreePath(childNode.getPath()));
       
       return childNode;
   }
}