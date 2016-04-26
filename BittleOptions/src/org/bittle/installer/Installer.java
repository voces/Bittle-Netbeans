package org.bittle.installer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.bittle.beansmod.Connection;
import org.json.simple.JSONArray;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

public class Installer extends org.openide.modules.ModuleInstall implements Runnable {

    public static boolean shouldSendMessage = true;
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
        
        Connection connection = Connection.getInstance();
        connection.connect("wss://notextures.io:8086");
        connection.clean();
        connection.register("temp_evan", "tacosaregreat");
        connection.login("temp_evan", "tacosaregreat");

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

                        //----------------------------------------------------
                        //IF THE DOCUMENT IS ONE BEING TRACKED:
                        //Look up in the hash set
                        //----------------------------------------------------
                        
                        currentDocument.addDocumentListener(currentDocumentListener = new DocumentListener() {
                            
                            String filePath = FileUtil.toFile(TopComponent.getRegistry().getActivated().getLookup().lookup(DataObject.class).getPrimaryFile()).getAbsolutePath();
                            String fileName = Paths.get(filePath).getFileName().toString();
                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                JOptionPane.showMessageDialog(null, "changedUpdate: Changed " + e.getLength()
                                        + " characters, document length = " + e.getDocument().getLength());
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                if (shouldSendMessage) {
                                    //send message
                                    int lineStart = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset());
                                    int lineEnd = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength());
                                    String text = null;
                                    try {
                                        text = currentDocument.getText(e.getOffset(), e.getLength());
                                    } catch (BadLocationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                    if (lineStart != lineEnd) {
                                        //multi-line insert, split lines and send as JSON array of strings
                                        String[] lines = text.split("\\r?\\n");
                                        String JSONlines = JSONArray.toJSONString(Arrays.asList(lines));
                                        connection.lines(fileName, e.getOffset(), 0, JSONlines);
                                    }
                                    else {
                                        //single-line insert
                                        connection.line(fileName, lineStart, e.getOffset(), 0, text);
                                    }
                                }
                                else {
                                    shouldSendMessage = true;
                                }
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                if (shouldSendMessage) {
                                    //send message
                                    int lineStart = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset()); //line # of cursor after the delete
                                    int lineEnd = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength()); //line # of the last part of deleted text
                                    //lineEnd will default to the last line if the line number it used to be on exceeds the new line count, so check for that as well
                                    if (lineStart != lineEnd || e.getOffset() + e.getLength() > currentDocument.getLength()) {
                                        //get line of text of the current line (lineStart)
                                        Element currentLine = currentDocument.getDefaultRootElement().getElement(lineStart);
                                        String currentLineText = null;
                                        try {
                                            currentLineText = currentDocument.getText(currentLine.getStartOffset(), currentLine.getEndOffset() - currentLine.getStartOffset());
                                            currentLineText = currentLineText.replace("\n", ""); //strip the newline
                                            connection.lines(fileName, e.getOffset(), e.getLength(), "[\"" + currentLineText + "\"]");
                                        } catch (BadLocationException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                    }
                                    else {
                                        //single-line insert
                                        connection.line(fileName, lineStart, e.getOffset(), e.getLength(), "");
                                    }
                                }
                                else {
                                    shouldSendMessage = true;
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