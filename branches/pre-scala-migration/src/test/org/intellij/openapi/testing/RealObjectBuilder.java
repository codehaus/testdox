package org.intellij.openapi.testing;

public class RealObjectBuilder {

    public VirtualFileEventBuilder virtualFileEvent() {
        return new VirtualFileEventBuilder();
    }
}
