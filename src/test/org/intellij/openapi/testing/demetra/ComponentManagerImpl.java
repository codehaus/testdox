package org.intellij.openapi.testing.demetra;

import java.util.HashMap;
import java.util.Map;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.PicoContainer;

public class ComponentManagerImpl implements ComponentManager {

    private final Map componentRegistry = new HashMap();

    public <T> void registerComponent(Class<T> clazz, Object component) {
        componentRegistry.put(clazz, component);
    }

    public void removeComponent(Class componentClass) {
        componentRegistry.remove(componentClass);
    }

    public boolean hasComponent(@NotNull Class clazz) {
        return false;
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseInterfaceClass) {
        return null;
    }

    public PicoContainer getPicoContainer() {
        return null;
    }

    public boolean isDisposed() {
        return false;
    }

    @NotNull
    public Class[] getComponentInterfaces() {
        return new Class[0];
    }

    public BaseComponent getComponent(String s) {
        return null;
    }

    public <T> T getComponent(Class<T> interfaceClass) {
        return (T) componentRegistry.get(interfaceClass);
    }

    public <T> T getComponent(Class<T> interfaceClass, T defaultImplementationIfAbsent) {
        return null;
    }

    public <T> T getUserData(Key<T> key) {
        return null;
    }

    public <T> void putUserData(Key<T> key, T value) {
    }

    public void dispose() {
    }
}
