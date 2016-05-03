package org.bittle.utilities;

import com.eclipsesource.json.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Paths;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
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
    private int numberOfLinesBeingRemoved;
    private DocumentFilter documentFilter;

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
                            
                            //add DocumentFilter to catch text before it gets deleted, something Document alone cannot do
                            ((AbstractDocument)currentDocument).setDocumentFilter(documentFilter = new DocumentFilter() {
                                
                                @Override
                                public void remove(DocumentFilter.FilterBypass fb, int offset, int length) {
                                    //needed for the removeUpdate
                                    int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(offset);
                                    int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(offset + length);
                                    numberOfLinesBeingRemoved = endingLineNumber - startingLineNumber + 1; //+1 because if lines 1 through 3 are affected, then 3 lines total are affected
                                    
                                    //continue with the remove
                                    try {
                                        if (offset + length <= fb.getDocument().getLength()) {
                                            fb.remove(offset, length);
                                        }
                                        else {
                                            fb.remove(offset, length - 1);
                                        }
                                    } catch (BadLocationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                                
                                @Override
                                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) {
                                    //needed for the removeUpdate
                                    int startingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(offset);
                                    int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(offset + length);
                                    numberOfLinesBeingRemoved = endingLineNumber - startingLineNumber + 1;
                                    
                                    //continue with the replace
                                    try {
                                        fb.replace(offset, length, text, null);
                                    } catch (BadLocationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                    
                                }
                                
                            });
                            
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
                                            int endingCharacterOffset = currentDocument.getDefaultRootElement().getElement(endingLineNumber).getEndOffset() - 1; //-1, otherwise it goes too far
                                            String text = null;
                                            try {
                                                text = currentDocument.getText(startingCharacterOffset, endingCharacterOffset - startingCharacterOffset);
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                            numberOfLines = currentNumberOfLines;
                                            String[] lines = text.split("(\\r?\\n)", -1); //regex: "(\\r?\\n)", splits on \r \n
                                            
                                            

                                            connection.lines(currentFileName, startingLineNumber, 1, Json.array(lines));
                                        } else {
                                            //single-line insert
                                            String addedText = null;
                                            try {
                                                addedText = currentDocument.getText(e.getOffset(), e.getLength());
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                            connection.line(currentFileName, startingLineNumber, e.getOffset() - currentDocument.getDefaultRootElement().getElement(startingLineNumber).getStartOffset(), 0, addedText);
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
                                                currentLineText = currentDocument.getText(currentLine.getStartOffset(), currentLine.getEndOffset() - currentLine.getStartOffset());
                                                currentLineText = currentLineText.replace("\n", ""); //strip the newline
                                                
//                                                int endingLineNumber = currentDocument.getDefaultRootElement().getElementIndex(e.getOffset() + e.getLength());
                                                
                                                connection.lines(currentFileName, currentDocument.getDefaultRootElement().getElementIndex(e.getOffset()), numberOfLinesBeingRemoved, Json.array(currentLineText));
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                        } else {
                                            //single-line delete
                                            connection.line(currentFileName, startingLineNumber, e.getOffset() - currentDocument.getDefaultRootElement().getElement(startingLineNumber).getStartOffset(), e.getLength(), "");
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
        if (listener != null) {
            EditorRegistry.removePropertyChangeListener(listener);
        }
    }

    public synchronized void insertText(String text, String fileName, int startPosition, int line) {
        //this should at some point check if the file is open in an editor,
        //if it is then change the opened file's editor
        //if not, then write to the file itself
        //currently, it will only update if its the current file open
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            try {
                currentDocument.insertString(currentDocument.getDefaultRootElement().getElement(line).getStartOffset() + startPosition, text, null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            shouldIgnoreUpdates = false;
        }
    }

    public synchronized void lines(String[] lines, String fileName, int startLine, int deleteCount) {
        Element currentLine = currentDocument.getDefaultRootElement().getElement(startLine);
        shouldIgnoreUpdates = true;
        if (fileName.equals(currentFileName)) {
            //delete whatever needs to be deleted
            try {
                Element line;
                for (int i = 0; i < deleteCount; i++) {
                    if (currentDocument.getText(0, currentDocument.getLength()).equals(""))
                        break; //because there's nothing left to remove
                    line = currentDocument.getDefaultRootElement().getElement(startLine);
                    currentDocument.remove(line.getStartOffset(), line.getEndOffset() - line.getStartOffset());
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }

            //insert lines
            String text = String.join("\n", lines);// + ((lines.length == 1 && deleteCount != numberOfLines || deleteCount == 0) ? "\n" : ""); //append \n if needed
            //add a newline if text follows
            int startingPoint = currentDocument.getDefaultRootElement().getElement(startLine).getStartOffset();
            if (startingPoint != currentDocument.getLength())
                text += "\n";
            try {
                currentDocument.insertString(startingPoint, text/* + (currentDocument.getText(0, currentDocument.getLength()).equals("") ? "" : "")*/, null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }

            shouldIgnoreUpdates = false;
        }
    }

    public synchronized void deleteText(String fileName, int startPosition, int deleteCount, int lineIndex) {
        if (fileName.equals(currentFileName)) {
            shouldIgnoreUpdates = true;
            try {
                Element line = currentDocument.getDefaultRootElement().getElement(lineIndex);
                currentDocument.remove(line.getStartOffset() + startPosition, deleteCount);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            shouldIgnoreUpdates = false;
        }
    }

//    public synchronized void deleteLines(String[] lines, String fileName, int startPosition, int deleteCount) {
//        if (fileName.equals(currentFileName)) {
//            shouldIgnoreUpdates = true;
//            try {
//                currentDocument.remove(startPosition, deleteCount);
//            } catch (BadLocationException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            shouldIgnoreUpdates = false;
//        }
//    }
}
