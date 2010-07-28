package org.codehaus.testdox.intellij.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import static jedi.functional.Coercions.array;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class RenameDialog extends DialogWrapper implements RenameUI {

    private static final Insets INSETS = new Insets(1, 1, 1, 1);

    private JTextField sentenceField = new JTextField();
    private String originalSentence;
    private String currentSentence;
    private boolean cancelled;

    public RenameDialog(Project project, String sentence) {
        super(project, true);
        setTitle("Rename Test");
        setResizable(true);
        setSentence(sentence);
        setModal(true);
        init();
    }

    public void setSentence(String sentence) {
        this.originalSentence = sentence;
        this.currentSentence = sentence;
        sentenceField.setText(currentSentence);
        updatePanel();
    }

    public JComponent getPreferredFocusedComponent() {
        return sentenceField;
    }

    protected Action[] createActions() {
        return array(getOKAction(), getCancelAction());
    }

    private void updatePanel() {
        setOKActionEnabled(isValidSentence(currentSentence));
    }

    private boolean isValidSentence(String sentence) {
        boolean first = true;
        StringCharacterIterator iterator = new StringCharacterIterator(sentence);
        for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
            if (!Character.isWhitespace(c)) {
                if (first && !Character.isJavaIdentifierStart(c)) {
                    return false;
                }
                if (!first && !Character.isJavaIdentifierPart(c)) {
                    return false;
                }
            }
            first = false;
        }

        return true;
    }

    public void doCancelAction() {
        super.doCancelAction();
        cancelled = true;
    }

    public void doOKAction() {
        super.doOKAction();
        cancelled = false;
    }

    public String getSentence() {
        return cancelled ? originalSentence : currentSentence;
    }

    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        addDocumentListener(sentenceField);

        JLabel label = new JLabel("Type a sentence that describes the intention of the test: ");
        panel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d, GridBagConstraints.WEST, GridBagConstraints.NONE, INSETS, 0, 0));
        panel.add(sentenceField, new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, INSETS, 20, 0));

        return panel;
    }

    private void addDocumentListener(final JTextField sentenceField) {
        sentenceField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                handleRename(sentenceField.getText());
            }

            public void removeUpdate(DocumentEvent event) {
                handleRename(sentenceField.getText());
            }

            public void changedUpdate(DocumentEvent event) {
            }
        });
    }

    void handleRename(String newName) {
        currentSentence = newName;
        updatePanel();
    }
}
