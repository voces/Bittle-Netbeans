package org.bittle.installer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.bittle.beansmod.*;
import org.json.simple.JSONArray;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

public class Installer extends org.openide.modules.ModuleInstall implements Runnable {

    public static boolean awaitingServerResponseForEdit = false;
    private Document currentDocument;
    private DocumentListener currentDocumentListener;
    private JTextComponent currentComponent;
    private Connection connection;

    @Override
    public void restored() {
        WindowManager manager = WindowManager.getDefault();
        manager.invokeWhenUIReady(this);
    }

    @Override
    public void close() {
        BittleOptionsPanelController.getInstance().logOut();
        connection.close();
    }

    @Override
    public void run() {

        connection = Connection.getInstance();
        connection.connect("wss://notextures.io:8086");
//        connection.clean();
//        connection.register("temp_evan", "tacosaregreat");
        connection.login("temp_evan", "tacosaregreat");

        PropertyChangeListener l = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //make sure the event is changing editors before doing anything
                JTextComponent lastFocusedComponent = EditorRegistry.lastFocusedComponent();
                if (currentComponent == null || lastFocusedComponent != currentComponent) {
                    if (lastFocusedComponent != null) {
                        currentComponent = lastFocusedComponent;
                        if (currentDocumentListener != null) {
                            currentDocument.removeDocumentListener(currentDocumentListener); //remove listener associated with the last editor
                        }
                        currentDocument = lastFocusedComponent.getDocument(); //grab the new editor

                        //----------------------------------------------------
                        //IF THE DOCUMENT IS ONE BEING TRACKED:
                        //Look up in the hash set
                        //----------------------------------------------------
                        String filePath = FileUtil.toFile(TopComponent.getRegistry().getActivated().getLookup().lookup(DataObject.class).getPrimaryFile()).getAbsolutePath();
                        String fileName = Paths.get(filePath).getFileName().toString();
                        //if (filePath is being tracked) ..........

                        //attach a new listener to the editor
                        currentDocument.addDocumentListener(currentDocumentListener = new DocumentListener() {

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                //Don't think this can fire for the Document class
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                //send message
                                int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset());
                                int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength());
                                String addedText = null;
                                try {
                                    addedText = currentDocument.getText(e.getOffset(), e.getLength());
                                } catch (BadLocationException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                                if (startingLineNumber != endingLineNumber) {
                                    //multi-line insert, split lines and send as JSON array of strings
                                    String[] lines = addedText.split("\\r?\\n");
                                    String JSONlines = JSONArray.toJSONString(Arrays.asList(lines));
                                    connection.lines(fileName, e.getOffset(), 0, JSONlines);
                                } else {
                                    //single-line insert
                                    connection.line(fileName, startingLineNumber, e.getOffset(), 0, addedText);
                                }
                                awaitingServerResponseForEdit = true; //not the best solution
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                //send message
                                int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset()); //line # of cursor after the delete
                                int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength()); //line # of the last part of deleted text
                                //lineEnd will default to the last line if the line number it used to be on exceeds the new line count, so check for that as well
                                if (startingLineNumber != endingLineNumber || e.getOffset() + e.getLength() > currentDocument.getLength()) {
                                    //get line of text of the current line (lineStart)
                                    Element currentLine = currentDocument.getDefaultRootElement().getElement(startingLineNumber);
                                    String currentLineText = null;
                                    try {
                                        currentLineText = currentDocument.getText(currentLine.getStartOffset(), currentLine.getEndOffset() - currentLine.getStartOffset());
                                        currentLineText = currentLineText.replace("\n", ""); //strip the newline
                                        connection.lines(fileName, e.getOffset(), e.getLength(), "[\"" + currentLineText + "\"]");
                                    } catch (BadLocationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                } else {
                                    //single-line insert
                                    connection.line(fileName, startingLineNumber, e.getOffset(), e.getLength(), "");
                                }
                                awaitingServerResponseForEdit = true; //not the best solution
                            }
                        });
                    }
                }
            }
        };

        EditorRegistry.addPropertyChangeListener(l);
    }
}
