package org.bittle.installer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
//import org.netbeans.modules.editor.EditorModule;
import org.openide.windows.WindowManager;

public class Installer extends org.openide.modules.ModuleInstall implements Runnable {

    private Document currentDocument;
    private DocumentListener currentDocumentListener;
    private JTextComponent currentTextComponent;

    @Override
    public void restored() {

        WindowManager manager = WindowManager.getDefault();
        manager.invokeWhenUIReady(this);
    }

    @Override
    public void run() {

        PropertyChangeListener l = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();
                //make sure we're changing editors before doing anything
                if (currentTextComponent == null || jtc != currentTextComponent) {
                    if (jtc != null) {
                        currentTextComponent = jtc;
                        if (currentDocumentListener != null) {
                            currentDocument.removeDocumentListener(currentDocumentListener); //remove last listener from last document on changing editor
                        }
                        currentDocument = jtc.getDocument();

                        //IF THE DOCUMENT IS ONE BEING TRACKED:
                        currentDocument.addDocumentListener(currentDocumentListener = new DocumentListener() {
                            public void changedUpdate(DocumentEvent e) {
                                JOptionPane.showMessageDialog(null, "changedUpdate: Changed " + e.getLength()
                                        + " characters, document length = " + e.getDocument().getLength());
                            }

                            public void insertUpdate(DocumentEvent e) {
                                JOptionPane.showMessageDialog(null, "insertUpdate: Added " + e.getLength()
                                        + " characters, document length = " + e.getDocument().getLength());
                            }

                            public void removeUpdate(DocumentEvent e) {
                                JOptionPane.showMessageDialog(null, "removeUpdate: Removed " + e.getLength()
                                        + " characters, document length = " + e.getDocument().getLength());
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
                }
            }
        };

        EditorRegistry.addPropertyChangeListener(l);
    }
}
