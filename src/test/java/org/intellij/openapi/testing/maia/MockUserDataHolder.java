package org.intellij.openapi.testing.maia;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import org.jetbrains.annotations.NotNull;

public class MockUserDataHolder implements UserDataHolder {

    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    public <T> void putUserData(@NotNull Key<T> key, T value) {
    }
}
