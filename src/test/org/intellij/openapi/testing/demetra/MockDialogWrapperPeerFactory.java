package org.intellij.openapi.testing.demetra;

import java.awt.Component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.DialogWrapperPeer;
import com.intellij.openapi.ui.DialogWrapperPeerFactory;

class MockDialogWrapperPeerFactory extends DialogWrapperPeerFactory {

    public DialogWrapperPeer createPeer(DialogWrapper wrapper, Project project, boolean canBeParent) {
        return new MockDialogWrapperPeer();
    }

    public DialogWrapperPeer createPeer(DialogWrapper wrapper, boolean canBeParent) {
        return new MockDialogWrapperPeer();
    }

    public DialogWrapperPeer createPeer(DialogWrapper wrapper, Component parent, boolean canBeParent) {
        return new MockDialogWrapperPeer();
    }
}
