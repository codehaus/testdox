package org.intellij.openapi.testing.demetra;

import java.awt.Component;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.EmptySubstitutor;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.PicoContainer;

public class MockApplication extends MockUserDataHolder implements org.intellij.openapi.testing.MockApplication {

    private final ComponentManagerImpl manager = createComponentManager();

    private ComponentManagerImpl createComponentManager() {
        ComponentManagerImpl componentManager = new ComponentManagerImpl();
        componentManager.registerComponent(PeerFactory.class, new MockPeerFactory());
        componentManager.registerComponent(EmptySubstitutor.class, null);
        return componentManager;
    }

    public <T> void registerComponent(Class<T> componentClass, Object component) {
        manager.registerComponent(componentClass, component);
    }

    public void removeComponent(Class componentClass) {
        manager.removeComponent(componentClass);
    }

    public void runReadAction(Runnable runnable) {
        runnable.run();
    }

    public <T> T runReadAction(Computable<T> computation) {
        return computation.compute();
    }

    public void runWriteAction(Runnable runnable) {
        runnable.run();
    }

    public <T> T runWriteAction(Computable<T> computation) {
        return computation.compute();
    }

    public Object getCurrentWriteAction(Class aClass) {
        return null;
    }

    public boolean isReadAccessAllowed() {
        return false;
    }

    public boolean isWriteAccessAllowed() {
        return false;
    }

    public void assertReadAccessAllowed() { }

    public void assertWriteAccessAllowed() { }

    public void assertIsDispatchThread() { }

    public void addApplicationListener(ApplicationListener applicationListener) { }

    public void removeApplicationListener(ApplicationListener applicationListener) { }

    public void saveAll() { }

    public void saveSettings() { }

    public void exit() { }

    public BaseComponent getComponent(String componentName) {
        return null;
    }

    public <T> T getComponent(Class<T> interfaceClass) {
        return manager.getComponent(interfaceClass);
    }

    public <T> T getComponent(Class<T> interfaceClass, T defaultImplementationIfAbsent) {
        T component = getComponent(interfaceClass);
        return component != null ? component : defaultImplementationIfAbsent;
    }

    public Class[] getComponentInterfaces() {
        return manager.getComponentInterfaces();
    }

    public PicoContainer getPicoContainer() {
        return manager.getPicoContainer();
    }

    public boolean hasComponent(@NotNull Class clazz) {
        return manager.hasComponent(clazz);
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseInterfaceClass) {
        return manager.getComponents(baseInterfaceClass);
    }

    public void invokeLater(Runnable runnable) { }

    public void invokeLater(Runnable runnable, @NotNull ModalityState modalityState) { }

    public void invokeAndWait(Runnable runnable, @NotNull ModalityState modalityState) { }

    public ModalityState getCurrentModalityState() {
        return null;
    }

    public ModalityState getModalityStateForComponent(Component component) {
        return null;
    }

    public ModalityState getDefaultModalityState() {
        return null;
    }

    public ModalityState getNoneModalityState() {
        return null;
    }

    public long getStartTime() {
        return 0;
    }

    public long getIdleTime() {
        return 0;
    }

    public boolean isUnitTestMode() {
        return false;
    }

    public boolean isHeadlessEnvironment() {
        return false;
    }

    public IdeaPluginDescriptor getPlugin(PluginId id) {
        return null;
    }

    public IdeaPluginDescriptor[] getPlugins() {
        return new IdeaPluginDescriptor[0];
    }

    public boolean isDisposed() {
        return false;
    }

    public boolean isDispatchThread() {
        return false;
    }

    public boolean runProcessWithProgressSynchronously(Runnable runnable, String s, boolean b, Project project) {
        return false;
    }

    public void dispose() {
    }
}
