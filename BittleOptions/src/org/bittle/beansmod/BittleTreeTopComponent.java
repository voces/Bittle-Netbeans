/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

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
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.bittle.beansmod.BittleTreeTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BittleTreeAction",
        preferredID = "BittleTree"
)
@Messages({
    "CTL_BittleTreeAction=BittleFiles",
    "CTL_BittleTreeTopComponent=Bittle Files",
    "HINT_BittleTreeTopComponent=This is your current Bittle directory"
})
public final class BittleTreeTopComponent extends TopComponent {
    
    private String rootpath = System.getProperty("user.home");
    private String optionsRoot = null;
    private Boolean loggedIn;
    private TreePopup treePopup;

    public BittleTreeTopComponent() {     
        rootpath = NbPreferences.forModule(BittlePanel.class).get("rootpath", "");
        initComponents();
        setName(Bundle.CTL_BittleTreeTopComponent());
        setToolTipText(Bundle.HINT_BittleTreeTopComponent());   
        updateScreen();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NotLoggedInScreen = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        OpenOptionsButton = new javax.swing.JButton();
        LoggedInScreen = new javax.swing.JScrollPane();
        fileTree = new javax.swing.JTree();

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(OpenOptionsButton, org.openide.util.NbBundle.getMessage(BittleTreeTopComponent.class, "BittleTreeTopComponent.OpenOptionsButton.text")); // NOI18N
        OpenOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenOptionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NotLoggedInScreenLayout = new javax.swing.GroupLayout(NotLoggedInScreen);
        NotLoggedInScreen.setLayout(NotLoggedInScreenLayout);
        NotLoggedInScreenLayout.setHorizontalGroup(
            NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotLoggedInScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(OpenOptionsButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        NotLoggedInScreenLayout.setVerticalGroup(
            NotLoggedInScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotLoggedInScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(OpenOptionsButton)
                .addContainerGap(286, Short.MAX_VALUE))
        );

        fileTree.setModel(new FileTreeModel(new File(rootpath)));
        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileTreeMouseClicked(evt);
            }
        });
        LoggedInScreen.setViewportView(fileTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(NotLoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(LoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(NotLoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(LoggedInScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void OpenOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenOptionsButtonActionPerformed
        OptionsDisplayer.getDefault().open("BittleOptions");
    }//GEN-LAST:event_OpenOptionsButtonActionPerformed

    private void fileTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTreeMouseClicked
        if (SwingUtilities.isRightMouseButton(evt)) {

            int row = fileTree.getClosestRowForLocation(evt.getX(), evt.getY());
            fileTree.setSelectionRow(row);
            treePopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_fileTreeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane LoggedInScreen;
    private javax.swing.JPanel NotLoggedInScreen;
    private javax.swing.JButton OpenOptionsButton;
    private javax.swing.JTree fileTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
   
    @Override
    public void componentOpened() {
        updateScreen();
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
     * Displays the appropriate screen in the top component
     * - Gets the most up to date log in state from the preferences
     * - If the user is logged in:
     *    * Gets the most up to date root from the preferences 
     *    * Instantiates a new tree with that root and displays it
     * - Otherwise, displays the not logged in screen
     */
    public void updateScreen(){
        loggedIn = NbPreferences.forModule(BittlePanel.class).getBoolean("status", false);
        
        if(loggedIn){
            rootpath = NbPreferences.forModule(BittlePanel.class).get("rootpath", "");
            fileTree = null;
            fileTree = new JTree(new FileTreeModel(new File(rootpath)));
            fileTree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    fileTreeMouseClicked(evt);
                }
            });
            treePopup = new TreePopup(fileTree);
            LoggedInScreen.setViewportView(fileTree); 
            LoggedInScreen.setVisible(true);
            NotLoggedInScreen.setVisible(false);
        }
        else{
            NotLoggedInScreen.setVisible(true);
            LoggedInScreen.setVisible(false);
        }
    }
   class TreePopup extends JPopupMenu {
       public TreePopup(JTree tree) {
           JMenuItem itemDelete = new JMenuItem("Delete");
           JMenuItem itemAdd = new JMenuItem("Add");
           itemDelete.addActionListener((ActionEvent e) -> {
               System.out.println("Delete child");
           });
           itemAdd.addActionListener((ActionEvent ae) -> {
               System.out.println("Add child");
           });
  
           add(itemDelete);
           add(new JSeparator());
           add(itemAdd);
       }
   }
}
