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
import org.bittle.beansmod.*;
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
    
    DocumentManipulator() {
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
                                        String addedText = null;
                                        try {
                                            addedText = currentDocument.getText(e.getOffset(), e.getLength());
                                        } catch (BadLocationException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                        int currentNumberOfLines = currentDocument.getDefaultRootElement().getElementCount();
                                       if (numberOfLines != currentNumberOfLines) {
                                            //multi-line insert, split lines and send as JSON array of strings
                                            numberOfLines = currentNumberOfLines;
                                            String[] lines = addedText.split("\\r?\\n");
                                            //String JSONlines = JSONArray.toJSONString(Arrays.asList(lines));
                                            connection.lines(currentFileName, e.getOffset(), 0, Json.array(lines));
                                        } else {
                                            //single-line insert
                                            connection.line(currentFileName, startingLineNumber, e.getOffset(), 0, addedText);
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
    
    public synchronized void insertLines(String[] lines, String fileName, int startLineIndex) {
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            for (String text : lines) {
                Element line = currentDocument.getDefaultRootElement().getElement(startLineIndex);
                if (line != null) {
                    //remove
                    try {
                        currentDocument.remove(line.getStartOffset(), line.getEndOffset() - line.getStartOffset());
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                //add
                try {
                    currentDocument.insertString(line.getEndOffset(), text, null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                startLineIndex++;
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
    
    public synchronized void deleteLines(String[] lines, String fileName, int startLineIndex, int deleteCount) {
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            int lineNumber = startLineIndex;
            Element currentLine;
            //remove lines
            for (int i = 0; i < deleteCount; i++) {
                currentLine = currentDocument.getDefaultRootElement().getElement(lineNumber);
                try {
                    currentDocument.remove(currentLine.getStartOffset(), currentLine.getEndOffset());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                lineNumber++;
            }
            //add lines
            for (String line : lines) {
                currentLine = currentDocument.getDefaultRootElement().getElement(startLineIndex);
                try {
                    currentDocument.insertString(currentLine.getEndOffset(), line, null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                startLineIndex++;
            }
            shouldIgnoreUpdates = false;
        }
    }
}
