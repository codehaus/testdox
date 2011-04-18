package org.intellij.openapi.testing.maia;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ModalityInvokator;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.impl.ModalityInvokatorImpl;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.EmptySubstitutor;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.impl.file.JavaDirectoryServiceImpl;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MockApplication extends MockUserDataHolder implements org.intellij.openapi.testing.MockApplication {

    private final ComponentManagerImpl manager = createComponentManager();

    private ComponentManagerImpl createComponentManager() {
        ComponentManagerImpl componentManager = new ComponentManagerImpl();
        componentManager.registerComponent(PeerFactory.class, new MockPeerFactory());
        componentManager.registerComponent(JavaDirectoryService.class, new JavaDirectoryServiceImpl());
        componentManager.removeComponent(EmptySubstitutor.class);
        return componentManager;
    }

    public <T> void registerComponent(Class<T> componentClass, Object component) {
        manager.removeComponent(componentClass);
        manager.registerComponent(componentClass, component);
    }

    public <T> void removeComponent(Class<T> componentClass) {
        manager.removeComponent(componentClass);
    }

    public void runReadAction(@NotNull Runnable runnable) {
        runnable.run();
    }

    public <T> T runReadAction(@NotNull Computable<T> computation) {
        return computation.compute();
    }

    public void runWriteAction(@NotNull Runnable runnable) {
        runnable.run();
    }

    public <T> T runWriteAction(@NotNull Computable<T> computation) {
        return computation.compute();
    }

    public <T> T getCurrentWriteAction(Class<T> clazz) {
        return null;
    }

    public boolean isReadAccessAllowed() {
        return false;
    }

    public boolean isWriteAccessAllowed() {
        return false;
    }

    public void assertReadAccessAllowed() {
    }

    public void assertWriteAccessAllowed() {
    }

    public void assertIsDispatchThread() {
    }

    public void addApplicationListener(@NotNull ApplicationListener applicationListener) {
    }

    public void addApplicationListener(@NotNull ApplicationListener applicationListener, @NotNull Disposable disposable) {
    }

    public void removeApplicationListener(@NotNull ApplicationListener applicationListener) {
    }

    public void saveAll() {
    }

    public void saveSettings() {
    }

    public void exit() {
    }

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

    @NotNull
    public Class[] getComponentInterfaces() {
        return manager.getComponentInterfaces();
    }

    @NotNull
    public PicoContainer getPicoContainer() {
        return manager.getPicoContainer();
    }

    public MessageBus getMessageBus() {
        return null;
    }

    public boolean hasComponent(@NotNull Class clazz) {
        return manager.hasComponent(clazz);
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseInterfaceClass) {
        return manager.getComponents(baseInterfaceClass);
    }

    public void invokeLater(@NotNull Runnable runnable) {
    }

    public void invokeLater(@NotNull Runnable runnable, @NotNull Condition expired) {
    }

    public void invokeLater(@NotNull Runnable runnable, @NotNull ModalityState modalityState) {
    }

    public void invokeLater(@NotNull Runnable runnable, @NotNull ModalityState state, @NotNull Condition expired) {
    }

    public void invokeAndWait(@NotNull Runnable runnable, @NotNull ModalityState modalityState) {
        runnable.run();
    }

    public ModalityState getCurrentModalityState() {
        return null;
    }

    public ModalityState getModalityStateForComponent(@NotNull Component component) {
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

    public boolean isCommandLine() {
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

    @NotNull
    public ComponentConfig[] getComponentConfigurations() {
        return new ComponentConfig[0];
    }

    public Future<?> executeOnPooledThread(@NotNull Runnable action) {
        return null;
    }

    public <T> Future<T> executeOnPooledThread(@NotNull Callable<T> callable) {
        return null;
    }

    public boolean isDisposeInProgress() {
        return false;
    }

    public boolean isRestartCapable() {
        return false;
    }

    public void restart() {
    }

    public boolean isActive() {
        return false;
    }

    @Nullable
    public Object getComponent(final ComponentConfig componentConfig) {
        return null;
    }

    public <T> T[] getExtensions(ExtensionPointName<T> extensionPointName) {
        return null;
    }

    public ComponentConfig getConfig(Class componentImplementation) {
        return null;
    }

    @NotNull
    public Condition getDisposed() {
        return Condition.NOT_NULL;
    }

    public boolean isDispatchThread() {
        return false;
    }

    @NotNull
    public ModalityInvokator getInvokator() {
        return new ModalityInvokatorImpl();
    }

    public void dispose() {
    }
}
