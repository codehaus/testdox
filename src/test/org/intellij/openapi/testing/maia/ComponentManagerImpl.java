package org.intellij.openapi.testing.maia;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Key;
import com.intellij.util.messages.MessageBus;
import static jedi.functional.Coercions.asArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.List;

public class ComponentManagerImpl implements ComponentManager {

    private final MutablePicoContainer container = new DefaultPicoContainer();

    public <T> void registerComponent(Class<T> clazz, Object component) {
        container.registerComponentInstance(clazz.getName(), component);
    }

    public void removeComponent(Class componentClass) {
        container.unregisterComponent(componentClass.getName());
    }

    public boolean hasComponent(@NotNull Class clazz) {
        return container.getComponentInstanceOfType(clazz) != null;
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseInterfaceClass) {
        return asArray((List<T>) container.getComponentInstancesOfType(baseInterfaceClass));
    }

    @NotNull
    public PicoContainer getPicoContainer() {
        return container;
    }

    public MessageBus getMessageBus() {
        return null;
    }

    public boolean isDisposed() {
        return false;
    }

    @NotNull
    public ComponentConfig[] getComponentConfigurations() {
        return new ComponentConfig[0];
    }

    @Nullable
    public Object getComponent(ComponentConfig componentConfig) {
        return null;
    }

    public <T> T[] getExtensions(ExtensionPointName<T> extensionPointName) {
        return null;
    }

    public ComponentConfig getConfig(Class componentImplementation) {
        return null;
    }

    @NotNull
    public Class[] getComponentInterfaces() {
        return new Class[0];
    }

    public BaseComponent getComponent(String s) {
        return null;
    }

    public <T> T getComponent(Class<T> interfaceClass) {
        return (T) container.getComponentInstanceOfType(interfaceClass);
    }

    public <T> T getComponent(Class<T> interfaceClass, T defaultImplementationIfAbsent) {
        return hasComponent(interfaceClass) ? getComponent(interfaceClass) : defaultImplementationIfAbsent;
    }

    public <T> T getUserData(Key<T> key) {
        return null;
    }

    public <T> void putUserData(Key<T> key, T value) {
    }

    public void dispose() {
    }
}
