package org.intellij.openapi.testing.diana;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;

public class MockUserDataHolder implements UserDataHolder {

    public <T> T getUserData(Key<T> key) {
        return null;
    }

    public <T> void putUserData(Key<T> key, T value) {
    }
}
