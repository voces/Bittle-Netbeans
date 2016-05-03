package org.bittle.utilities;

import com.eclipsesource.json.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Paths;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

public class DocumentManipulator {
    private Document currentDocument;
    private DocumentListener currentDocumentListener;
    private JTextComponent currentComponent;
    private Connection connection;
    private String currentFileName = "";
    private PropertyChangeListener listener;
    private boolean shouldIgnoreUpdates = false;
    private int numberOfLines;
    
    private static DocumentManipulator instance;
    
    public static DocumentManipulator getInstance() {
        return instance == null ? instance = new DocumentManipulator() : instance;
    }
    
    private DocumentManipulator() {
        connection = Connection.getInstance();
        
        //set up the document listener
        listener = new PropertyChangeListener() {
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

                        FileObject file = TopComponent.getRegistry().getActivated().getLookup().lookup(DataObject.class).getPrimaryFile();
                        String filePath = FileUtil.toFile(file).getAbsolutePath();
                        currentFileName = Paths.get(filePath).getFileName().toString();

                        if (Share.getInstance().files.contains(currentFileName)) {
                            numberOfLines = currentDocument.getDefaultRootElement().getElementCount();
                            //if the file is being shared, attach a listener to its document
                            currentDocument.addDocumentListener(currentDocumentListener = new DocumentListener() {

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    //Don't think this can fire for the Document class
                                }

                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    if (!shouldIgnoreUpdates) {
                                        //send message
                                        int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset());
                                        int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength());
                                        int currentNumberOfLines = currentDocument.getDefaultRootElement().getElementCount();
                                       if (numberOfLines != currentNumberOfLines) {
                                            //multi-line insert, split lines and send as JSON array of strings
                                            int startingCharacterOffset = currentDocument.getDefaultRootElement().getElement(startingLineNumber).getStartOffset();
                                            String text = null;
                                            try {
                                                text = currentDocument.getText(startingCharacterOffset, e.getOffset() + e.getLength() - startingCharacterOffset);
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                            numberOfLines = currentNumberOfLines;
                                            String[] lines = text.split("(\\r?\\n)"); //splits on \r or \n, and appends it to the end of the string if it's there
                                            
                                            connection.lines(currentFileName, e.getOffset(), 0, Json.array(lines));
                                        } else {
                                            //single-line insert
                                            String addedText = null;
                                            try {
                                                addedText = currentDocument.getText(e.getOffset(), e.getLength());
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                            connection.line(currentFileName, startingLineNumber, currentDocument.getDefaultRootElement().getElementIndex(e.getOffset()), 0, addedText);
                                        }
                                    }
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    if (!shouldIgnoreUpdates) {
                                        //e.offset is where the cursor ends up after the delete
                                        //e.length is the number of characters deleted (including newlines)
                                        
                                        //send message
                                        int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset()); //line # of cursor after the delete
                                        int currentNumberOfLines = currentDocument.getDefaultRootElement().getElementCount();
                                        if (numberOfLines != currentNumberOfLines) {
                                            //multi-line delete
                                            numberOfLines = currentNumberOfLines;
                                            //get line of text of the current line (lineStart)
                                            Element currentLine = currentDocument.getDefaultRootElement().getElement(startingLineNumber);
                                            String currentLineText;
                                            try {
                                                currentLineText = currentDocument.getText(currentLine.getStartOffset(), currentLine.getElementCount());
                                                currentLineText = currentLineText.replace("\n", ""); //strip the newline
                                                connection.lines(currentFileName, e.getOffset(), e.getLength(), Json.array(currentLineText));
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                        } else {
                                            //single-line delete
                                            connection.line(currentFileName, startingLineNumber, e.getOffset(), e.getLength(), "");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        };

        EditorRegistry.addPropertyChangeListener(listener);
    }
    
    public void removeListener() {
        if (listener != null)
            EditorRegistry.removePropertyChangeListener(listener);
    }
    
    public synchronized void insertText(String text, String fileName, int startPosition) {
        //this should at some point check if the file is open in an editor,
        //if it is then change the opened file's editor
        //if not, then write to the file itself
        //currently, it will only update if its the current file open
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            try {
                currentDocument.insertString(startPosition, text, null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            shouldIgnoreUpdates = false;
        }
    }
    
    public synchronized void insertLines(String[] lines, String fileName, int startLine) {
        Element currentLine = currentDocument.getDefaultRootElement().getElement(startLine);
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            
            String text = String.join("\n", lines); //System.getProperty("line.separator")
            try {
                currentDocument.insertString(currentDocument.getDefaultRootElement().getElement(startLine).getStartOffset(), text, null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            shouldIgnoreUpdates = false;
        }
    }
    
    public synchronized void deleteText(String fileName, int startPosition, int deleteCount) {
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            try {
                currentDocument.remove(startPosition, deleteCount);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            shouldIgnoreUpdates = false;
        }
    }
    
    public synchronized void deleteLines(String[] lines, String fileName, int startPosition, int deleteCount) {
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
                try {
                    currentDocument.remove(startPosition, deleteCount);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            shouldIgnoreUpdates = false;
        }
    }
}
