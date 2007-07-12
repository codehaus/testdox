package org.codehaus.testdox.intellij.panel;

public interface RenameUI {

    void setSentence(String sentence);

    String getSentence();

    void doCancelAction();

    void doOKAction();

    boolean isOK();

    void show();
}
