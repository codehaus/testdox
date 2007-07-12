package org.intellij.openapi.testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;

public class MockVirtualFile extends VirtualFile {

    private String path;
    private boolean isDirectory;

    public MockVirtualFile(String path, boolean isDirectory) {
        this.path = path;
        this.isDirectory = isDirectory;
    }

    public VirtualFileSystem getFileSystem() {
        return null;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return path;
    }

    public void rename(Object requestor, String newName) throws IOException {
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isValid() {
        return false;
    }

    public VirtualFile getParent() {
        return null;
    }

    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    public VirtualFile createChildDirectory(Object requestor, String name)
            throws IOException {
        return null;
    }

    public VirtualFile createChildData(Object requestor, String name)
            throws IOException {
        return null;
    }

    public void delete(Object requestor) throws IOException {
    }

    public void move(Object requestor, VirtualFile newParent) throws IOException {
    }

    public InputStream getInputStream() throws IOException {
        return null;
    }

    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        return null;
    }

    public byte[] contentsToByteArray() throws IOException {
        return new byte[0];
    }

    public char[] contentsToCharArray() throws IOException {
        return new char[0];
    }

    public long getModificationStamp() {
        return 0;
    }

    public long getTimeStamp() {
        return 0;
    }

    public long getActualTimeStamp() {
        return 0;
    }

    public long getLength() {
        return 0;
    }

    public void refresh(boolean asynchronous, boolean recursive, Runnable postRunnable) {}
}
