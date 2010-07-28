package org.codehaus.testdox.intellij.panel;

public interface ItemSelectionUI {

    Object getSelectedItem();

    void setSelectedIndex(int index);

    void show();

    boolean isOK();

    boolean wasCancelled();
}
