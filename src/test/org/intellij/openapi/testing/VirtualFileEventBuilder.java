package org.intellij.openapi.testing;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile;

public class VirtualFileEventBuilder {

    private Object requestor;
    private VirtualFile file = new StubVirtualFile() {
        @Override
        public boolean isDirectory() {
            return false;
        }
    };
    private String fileName;
    private VirtualFile parent;

    public VirtualFileEventBuilder withRequestor(Object requestor) {
        this.requestor = requestor;
        return this;
    }

    public VirtualFileEventBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public VirtualFileEventBuilder withFileDeleted() {
//        new VirtualFileEvent(psiManagerMock, virtualFileMock, CLASS_NAME + ".java", false, null)
        return this;
    }

    public VirtualFileEventBuilder withIsDirectory(boolean expected) {
        file = new MockVirtualFile("dir", expected);
        return this;
    }

    public VirtualFileEvent build() {
        return new VirtualFileEvent(requestor, file, fileName, parent);
    }
}
