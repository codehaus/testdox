package org.intellij.openapi.testing;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile;

public class VirtualFileEventBuilder {

    private Object requester;
    private String fileName;

    private VirtualFile file = new StubVirtualFile() {
        public boolean isDirectory() {
            return false;
        }
    };

    public VirtualFileEventBuilder withRequester(Object requester) {
        this.requester = requester;
        return this;
    }

    public VirtualFileEventBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public VirtualFileEventBuilder withIsDirectory(boolean expected) {
        file = new MockVirtualFile("dir", expected);
        return this;
    }

    public VirtualFileEvent build() {
        return new VirtualFileEvent(requester, file, fileName, null);
    }
}
