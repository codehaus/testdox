package org.codehaus.testdox.intellij.panel;

import org.codehaus.testdox.intellij.TestDoxActionPresentationUpdater;

public interface TestDoxToolWindowUI extends TestDoxActionPresentationUpdater {

    void renameSelectedTestElement();

    void deleteSelectedTestElement();
}
