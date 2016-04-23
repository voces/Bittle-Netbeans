package org.bittle.installer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
//import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
//import org.netbeans.modules.editor.EditorModule;
import org.openide.windows.WindowManager;

public class Installer extends org.openide.modules.ModuleInstall implements Runnable {
        
    @Override
    public void restored() {
        
        WindowManager manager = WindowManager.getDefault();
        manager.invokeWhenUIReady(this);
    }

    @Override
    public void run() {
        
        PropertyChangeListener l = (PropertyChangeEvent evt) -> {
            JTextComponent jtc = EditorRegistry.lastFocusedComponent();
            if (jtc != null) {
                Document d = jtc.getDocument();
                //document listener test
                d.addDocumentListener( new DocumentListener() {
                    public void changedUpdate( DocumentEvent e )
                    {
                        System.out.println( "changedUpdate: Added " + e.getLength() + 
                          " characters, document length = " + e.getDocument().getLength() );
                    }

                    public void insertUpdate( DocumentEvent e )
                    {
                      System.out.println( "insertUpdate: Added " + e.getLength() + 
                          " characters, document length = " + e.getDocument().getLength() );
                    }

                    public void removeUpdate( DocumentEvent e )
                    {
                      System.out.println( "removeUpdate: Removed " + e.getLength() +
                          " characters, document length = " + e.getDocument().getLength() );
                    }
                });
                //key listener test
                jtc.addKeyListener(new KeyListener() {
                    
                @Override
                public void keyTyped(KeyEvent e) {
                    int keycode = e.getKeyCode();
                    switch (keycode) {
                        case KeyEvent.VK_LEFT:
//                            JOptionPane.showMessageDialog(null, "1 Left: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_RIGHT:
//                            JOptionPane.showMessageDialog(null, "1 Right: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_DOWN:
//                            JOptionPane.showMessageDialog(null, "1 Down: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_UP:
//                            JOptionPane.showMessageDialog(null, "1 Up: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_ENTER:
//                            JOptionPane.showMessageDialog(null, "Enter: "+e.getKeyCode());
                            break;
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    int keycode = e.getKeyCode();
                    switch (keycode) {
                        case KeyEvent.VK_LEFT:
//                            JOptionPane.showMessageDialog(null, "2 Left: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_RIGHT:
//                            JOptionPane.showMessageDialog(null, "2 Right: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_DOWN:
//                            JOptionPane.showMessageDialog(null, "2 Down: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_UP:
//                            JOptionPane.showMessageDialog(null, "2 Up: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_ENTER:
//                            JOptionPane.showMessageDialog(null, "Enter: "+e.getKeyCode());
                            break;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int keycode = e.getKeyCode();
                    switch (keycode) {
                        case KeyEvent.VK_LEFT:
//                            JOptionPane.showMessageDialog(null, "3 Left: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_RIGHT:
//                            JOptionPane.showMessageDialog(null, "3 Right: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_DOWN:
//                            JOptionPane.showMessageDialog(null, "3 Down: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_UP:
//                            JOptionPane.showMessageDialog(null, "3 Up: " + e.getKeyCode());
                            break;
                        case KeyEvent.VK_ENTER:
//                            JOptionPane.showMessageDialog(null, "Enter: "+e.getKeyCode());
                            break;
                    }
                }

            });
            }
        };

        EditorRegistry.addPropertyChangeListener(l);
    }
}
