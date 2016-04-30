package org.bittle.beansmod;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import org.bittle.messages.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

final class BittlePanel extends javax.swing.JPanel {

    private final BittleOptionsPanelController controller;
    private final BittleTreeTopComponent fileTree; 
    private final Connection connection;
    private final Share share;
    private final String serverName = "wss://notextures.io:8086";

    
    private String username = "";
    private String password = "";
    private String rootpath = System.getProperty("user.home") + "\\Bittle";
    private boolean loggedIn = false;
    private boolean waitingForResponse = false;

    BittlePanel(BittleOptionsPanelController controller) {
        this.controller = controller;
        this.fileTree = (BittleTreeTopComponent) WindowManager.getDefault().findTopComponent("BittleTree");
        this.connection = Connection.getInstance();
        this.share = Share.getInstance();
        
        // Store default preferences
        store();
        
        // Load the GUI
        initComponents();
        
        // Set the initial log in screen 
        LogInPanel.setVisible(true);
        LoggedInPanel.setVisible(false);
        //focusLoginButton();
       
        // Options panel message listner
        // Only interested in Response messages
        // If the component is waiting for a response from the server
        // And it recieves a response message, it will handle it 
        this.connection.addMessageListener((Message m) -> {
            if(m instanceof Response){
                if(waitingForResponse){
                    handleResponse((Response) m);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        OptionsLayeredPane = new javax.swing.JLayeredPane();
        LogInPanel = new javax.swing.JPanel();
        LoginLabel = new javax.swing.JLabel();
        UsernameLabel = new javax.swing.JLabel();
        PasswordLabel = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        PasswordField = new javax.swing.JPasswordField();
        LoginButton = new javax.swing.JButton();
        RegisterCheckbox = new javax.swing.JCheckBox();
        LoggedInPanel = new javax.swing.JPanel();
        LoggedInLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        ChangePassPanel = new javax.swing.JPanel();
        ChangePassLabel = new javax.swing.JLabel();
        CurrPassLabel = new javax.swing.JLabel();
        NewPassLabel = new javax.swing.JLabel();
        currPassField = new javax.swing.JPasswordField();
        newPassField = new javax.swing.JPasswordField();
        ChangePassButton = new javax.swing.JButton();
        LogOutPanel = new javax.swing.JPanel();
        LogOutButton = new javax.swing.JButton();
        DoneLabel = new javax.swing.JLabel();
        ChangeDirectoryPanel = new javax.swing.JPanel();
        BrowseButton = new javax.swing.JButton();
        CurrentDirectoryLabel = new javax.swing.JLabel();
        CurrentDirectoryField = new javax.swing.JTextField();

        LoginLabel.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        LoginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(LoginLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.LoginLabel.text")); // NOI18N

        UsernameLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(UsernameLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.UsernameLabel.text")); // NOI18N

        PasswordLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(PasswordLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.PasswordLabel.text")); // NOI18N

        UsernameField.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        UsernameField.setText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.UsernameField.text")); // NOI18N

        PasswordField.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        PasswordField.setText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.PasswordField.text")); // NOI18N

        LoginButton.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(LoginButton, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.LoginButton.text")); // NOI18N
        LoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginButtonActionPerformed(evt);
            }
        });

        RegisterCheckbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(RegisterCheckbox, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.RegisterCheckbox.text")); // NOI18N
        RegisterCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterCheckboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LogInPanelLayout = new javax.swing.GroupLayout(LogInPanel);
        LogInPanel.setLayout(LogInPanelLayout);
        LogInPanelLayout.setHorizontalGroup(
            LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogInPanelLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LoginLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(LogInPanelLayout.createSequentialGroup()
                        .addGroup(LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UsernameLabel)
                            .addComponent(PasswordLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LoginButton)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LogInPanelLayout.createSequentialGroup()
                        .addComponent(RegisterCheckbox)
                        .addGap(112, 112, 112)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        LogInPanelLayout.setVerticalGroup(
            LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogInPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoginLabel)
                .addGap(18, 18, 18)
                .addGroup(LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UsernameLabel))
                .addGap(18, 18, 18)
                .addGroup(LogInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PasswordLabel)
                    .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(LoginButton)
                .addGap(18, 18, 18)
                .addComponent(RegisterCheckbox)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        LoggedInPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        LoggedInLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        LoggedInLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(LoggedInLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.LoggedInLabel.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        ChangePassLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(ChangePassLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.ChangePassLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(CurrPassLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.CurrPassLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(NewPassLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.NewPassLabel.text")); // NOI18N

        currPassField.setText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.currPassField.text")); // NOI18N

        newPassField.setText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.newPassField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ChangePassButton, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.ChangePassButton.text")); // NOI18N
        ChangePassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangePassButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ChangePassPanelLayout = new javax.swing.GroupLayout(ChangePassPanel);
        ChangePassPanel.setLayout(ChangePassPanelLayout);
        ChangePassPanelLayout.setHorizontalGroup(
            ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChangePassPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChangePassPanelLayout.createSequentialGroup()
                        .addGroup(ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CurrPassLabel)
                            .addComponent(NewPassLabel))
                        .addGap(18, 18, 18)
                        .addGroup(ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(currPassField, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(newPassField)))
                    .addComponent(ChangePassButton)
                    .addComponent(ChangePassLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ChangePassPanelLayout.setVerticalGroup(
            ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChangePassPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ChangePassLabel)
                .addGap(18, 18, 18)
                .addGroup(ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CurrPassLabel)
                    .addComponent(currPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ChangePassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NewPassLabel)
                    .addComponent(newPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(ChangePassButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(LogOutButton, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.LogOutButton.text")); // NOI18N
        LogOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogOutButtonActionPerformed(evt);
            }
        });

        DoneLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        DoneLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(DoneLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.DoneLabel.text")); // NOI18N

        javax.swing.GroupLayout LogOutPanelLayout = new javax.swing.GroupLayout(LogOutPanel);
        LogOutPanel.setLayout(LogOutPanelLayout);
        LogOutPanelLayout.setHorizontalGroup(
            LogOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogOutPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LogOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DoneLabel)
                    .addComponent(LogOutButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LogOutPanelLayout.setVerticalGroup(
            LogOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LogOutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DoneLabel)
                .addGap(18, 18, 18)
                .addComponent(LogOutButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(BrowseButton, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.BrowseButton.text")); // NOI18N
        BrowseButton.setToolTipText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.BrowseButton.toolTipText")); // NOI18N
        BrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseButtonActionPerformed(evt);
            }
        });

        CurrentDirectoryLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        CurrentDirectoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(CurrentDirectoryLabel, org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.CurrentDirectoryLabel.text")); // NOI18N

        CurrentDirectoryField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        CurrentDirectoryField.setText(org.openide.util.NbBundle.getMessage(BittlePanel.class, "BittlePanel.CurrentDirectoryField.text")); // NOI18N

        javax.swing.GroupLayout ChangeDirectoryPanelLayout = new javax.swing.GroupLayout(ChangeDirectoryPanel);
        ChangeDirectoryPanel.setLayout(ChangeDirectoryPanelLayout);
        ChangeDirectoryPanelLayout.setHorizontalGroup(
            ChangeDirectoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChangeDirectoryPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ChangeDirectoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CurrentDirectoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CurrentDirectoryLabel)
                    .addComponent(BrowseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ChangeDirectoryPanelLayout.setVerticalGroup(
            ChangeDirectoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ChangeDirectoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CurrentDirectoryLabel)
                .addGap(18, 18, 18)
                .addComponent(CurrentDirectoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BrowseButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout LoggedInPanelLayout = new javax.swing.GroupLayout(LoggedInPanel);
        LoggedInPanel.setLayout(LoggedInPanelLayout);
        LoggedInPanelLayout.setHorizontalGroup(
            LoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoggedInPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ChangeDirectoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(LoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChangePassPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LogOutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LoggedInPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LoggedInLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LoggedInPanelLayout.setVerticalGroup(
            LoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoggedInPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoggedInLabel)
                .addGap(18, 18, 18)
                .addGroup(LoggedInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LoggedInPanelLayout.createSequentialGroup()
                        .addComponent(ChangePassPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(LogOutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoggedInPanelLayout.createSequentialGroup()
                        .addComponent(ChangeDirectoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        OptionsLayeredPane.setLayer(LogInPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        OptionsLayeredPane.setLayer(LoggedInPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout OptionsLayeredPaneLayout = new javax.swing.GroupLayout(OptionsLayeredPane);
        OptionsLayeredPane.setLayout(OptionsLayeredPaneLayout);
        OptionsLayeredPaneLayout.setHorizontalGroup(
            OptionsLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoggedInPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(OptionsLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsLayeredPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(LogInPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        OptionsLayeredPaneLayout.setVerticalGroup(
            OptionsLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OptionsLayeredPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoggedInPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(OptionsLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsLayeredPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(LogInPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(OptionsLayeredPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(OptionsLayeredPane)
        );
    }// </editor-fold>//GEN-END:initComponents
  
    private void BrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrowseButtonActionPerformed
        
        // Create a Java file chooser for folders
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the file chooser
        int choice = fileChooser.showOpenDialog(null);
        
        // If the user selected a folder
        if(choice == JFileChooser.APPROVE_OPTION){
            
            // Try to create a Bittle folder in it, updates rootpath if it does
            try {
                createBittleDirectory(fileChooser.getSelectedFile().toString(), true);
            } catch (IOException ex) {
            }
            
            store();    // Stores the new rootpath
            load();     // Shows the new rootpath in the text box
        }
    }//GEN-LAST:event_BrowseButtonActionPerformed

    private void RegisterCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterCheckboxActionPerformed
        
        // If the checkbox is selected, show the register prompts
        if(RegisterCheckbox.isSelected()){
            LoginLabel.setText("Sign Up");
            LoginButton.setText("Sign Up");
        }
        // Otherwise show the log in prompts
        else{
            LoginLabel.setText("Log In");
            LoginButton.setText("Log In");
        }
    }//GEN-LAST:event_RegisterCheckboxActionPerformed

    private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed
        
        // Do clilent side log in validation
        // If log in was invalid, do nothing
        if(!validLogin())
            return;      
        
        // Connect to the server 
        connection.connect(serverName);
        
        // Update the username and password with the input
        username = UsernameField.getText();
        password = PasswordField.getText();
        
        // Set up the wait flag
        waitingForResponse = true;
        
        // If the user wants to register, do that
        if(RegisterCheckbox.isSelected())
            connection.register(username, password);
        // Otherwise try to log in
        else
            connection.login(username, password);
        
        // If the server didn't respond
        // Notify the time out and do nothing 
        if(!waitForResponse()){
            NotifyDescriptor nd = new NotifyDescriptor.Message("Waiting for server timed out...", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        
        // Wait for the message handler to set log in status
        // Ugly but won't work without it :(
        try {
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (InterruptedException ex) {
        }
        
        // If the log in/register failed, back out 
        if(!loggedIn)
            return;        
        
        // Try to create the bittle directory
        // If it already exists, rootpath will be updated
        // NOTE: We need to find a way to store each user's preference for this
        // - As it is now, the bittle folder is created in user's home folder
        // - They have to reset it to thier desired path each time
        // - Perhaps we could store their desired path in the server
        // - We would use that instead of "rootpath" for the create call
        try {
            createBittleDirectory(rootpath, false);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // Add the current user to the share
        share.addUser(username);
        
        // Store all the new information 
        store();
        
        // Show the appropriate screens and text 
        load();
        LogInPanel.setVisible(false);
        LoggedInLabel.setText("Hey There, " + username);
        LoggedInPanel.setVisible(true);
        fileTree.updateTree();
    }//GEN-LAST:event_LoginButtonActionPerformed

    private void LogOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogOutButtonActionPerformed

        // Log out from the server and initialize state
        logout();
        
        // Update the login screen and file tree
        LoggedInPanel.setVisible(false);
        LogInPanel.setVisible(true);
        fileTree.updateTree();
        //focusLoginButton();
    }//GEN-LAST:event_LogOutButtonActionPerformed

    private void ChangePassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangePassButtonActionPerformed
        
        // Get the user input 
        String pass = currPassField.getText();
        String newPass = newPassField.getText();
        
        // Set up the wait flag
        waitingForResponse = true;
        
        // Send the input to the server 
        connection.changePass(username, pass, newPass);
        
        // If the server didn't respond, notify the timeout 
        if(!waitForResponse()){
            NotifyDescriptor nd = new NotifyDescriptor.Message("Waiting for server timed out...", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }//GEN-LAST:event_ChangePassButtonActionPerformed
    
    /**
     * If the user is logged in
     * Logs out from the server and resets preferences 
     */
    public void logout(){
        if(loggedIn){
            connection.logout();
            loggedIn = false;
            username = "";
            password = "";
            store();
        }
    }
    
    /**
     * Loads GUI labels with most up to date preferences 
     */
    void load() {
        UsernameField.setText(NbPreferences.forModule(BittlePanel.class).get("username", ""));
        PasswordField.setText(NbPreferences.forModule(BittlePanel.class).get("password", ""));
        CurrentDirectoryField.setText(NbPreferences.forModule(BittlePanel.class).get("path", ""));
    }

    /**
     * Stores new preferences 
     */
    void store() {
        NbPreferences.forModule(BittlePanel.class).put("username", username);
        NbPreferences.forModule(BittlePanel.class).put("password", password);
        NbPreferences.forModule(BittlePanel.class).putBoolean("status", loggedIn);
        NbPreferences.forModule(BittlePanel.class).put("path", rootpath);
    }

    boolean valid() {
        // N/A
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BrowseButton;
    private javax.swing.JPanel ChangeDirectoryPanel;
    private javax.swing.JButton ChangePassButton;
    private javax.swing.JLabel ChangePassLabel;
    private javax.swing.JPanel ChangePassPanel;
    private javax.swing.JLabel CurrPassLabel;
    private javax.swing.JTextField CurrentDirectoryField;
    private javax.swing.JLabel CurrentDirectoryLabel;
    private javax.swing.JLabel DoneLabel;
    private javax.swing.JPanel LogInPanel;
    private javax.swing.JButton LogOutButton;
    private javax.swing.JPanel LogOutPanel;
    private javax.swing.JLabel LoggedInLabel;
    private javax.swing.JPanel LoggedInPanel;
    private javax.swing.JButton LoginButton;
    private javax.swing.JLabel LoginLabel;
    private javax.swing.JLabel NewPassLabel;
    private javax.swing.JLayeredPane OptionsLayeredPane;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JCheckBox RegisterCheckbox;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel UsernameLabel;
    private javax.swing.JPasswordField currPassField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPasswordField newPassField;
    // End of variables declaration//GEN-END:variables

    /**
     * Checks that the user's input is not blank
     * Should probably do more than just check that
     * @return false if the user's input was blank, true otherwise
     */
    private boolean validLogin() {
        if("".equals(PasswordField.getText()) || "".equals(UsernameField.getText())){
            NotifyDescriptor nd = new NotifyDescriptor.Message("Fields cannot be blank!", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return false;
        }
        return true;
    }
    
    /**
     * Creates a new directory for Bittle files
     * @param newPath - the folder where the Bittle directory will be created
     * @param moveFiles - true if files need to be migrated to new folder, false otherwise 
     */
    private void createBittleDirectory(String newPath, boolean moveFiles) throws IOException{
        
        // If the new folder is not a folder named "Bittle"
        // Make the a "Bittle" folder in it 
        if(!newPath.substring(newPath.lastIndexOf("\\")).equals("\\Bittle"))
            newPath = newPath + "\\Bittle";
        
        // Create a path for the new folder
        Path newFolder = Paths.get(newPath);
            
        // If the folder does not exist
        if(!Files.exists(newFolder)){
            
            // Attempt to create it
            try {
                Files.createDirectories(newFolder);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                }
        }
        // need to add else here to handle files already existing in new folder 
        
        // If we need to migrate files
        // Do so unless we aren't really moving folders 
        if(moveFiles && !rootpath.equals(newPath))
                moveDirectories(rootpath, newPath);
        
        // Update the path 
        rootpath = newPath;
    }
    
    /**
     * Moves the contents of one directory to another
     * @param from - the path of the folder you are moving from
     * @param to - the path of the folder you are moving to
     * @throws IOException 
     */
    private void moveDirectories(String from, String to) throws IOException{
    
        // Get the path of the previous folder 
        Path oldBittleFolder = Paths.get(from);
        
        // Get the path of the new folder
        Path newBittleFolder = Paths.get(to);
            
        // Visit all the files in the Bittle Folder
        Files.walkFileTree(oldBittleFolder, new SimpleFileVisitor<Path>()
        {
            // When a file is encountered, move it to the new path
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Files.move(file, newBittleFolder.resolve(oldBittleFolder.relativize(file)), ATOMIC_MOVE);
                return FileVisitResult.CONTINUE;
            }
        });
        
        // Delete the old bittle folder
        Files.delete(oldBittleFolder);
    }

    /* Doesn't work :/
    private void focusLoginButton() {
        this.requestFocusInWindow();
        LogInPanel.requestFocusInWindow();
        LoginButton.requestFocusInWindow();
    }*/

    /**
     * Handles response events from the server 
     * If the response is a failed response, tells the user why and returns
     * Otherwise perform the appropriate action depending on the response type 
     * @param r Response message from the server 
     */
    private void handleResponse(Response r) {
        
        // Response has arrived, stop waiting 
        waitingForResponse = false;
        
        // Notify any failures
        if(r.getStatus().equals("failed")){
            loggedIn = false;
            NotifyDescriptor nd = new NotifyDescriptor.Message(r.getReason(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        
        // If no failures, check what kind of success response is being given 
        switch (r.getID()) {
            
            // Thank the user and log in  on register response 
            // Set logged in flag to true 
            case "register":
                {
                    loggedIn = true;
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Thanks for signing up, " + username, NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    connection.login(username, password);
                    break;
                }
            // Welcome the user back on log in response
            // Set logged in flag to true 
            case "login":
                {
                    loggedIn = true;
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Welcome Back, " + username, NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    break;
                }
            // Notify success of password change response    
            case "changePass":
                {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Password successfully changed!", NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    break;
                }
            default:
                break;
        }
    }

    /**
     * Waits by putting thread to sleep for 50ms
     * Spins on response flag, which is changed in response handler
     * If the thread has been spinning for too long, time out
     * @return false if waiting timed out, true otherwise 
     */
    private boolean waitForResponse() {
        int timer = 0;
                
        while(waitingForResponse){
            if(timer > 1000)
                return false;
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ex) {
            }
            
            timer++;
        }
        return true;
    }
}
