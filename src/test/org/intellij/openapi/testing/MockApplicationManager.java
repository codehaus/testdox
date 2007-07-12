package org.intellij.openapi.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.intellij.openapi.application.ApplicationManager;

public class MockApplicationManager extends ApplicationManager {

    private static final String DEMETRA = "org.intellij.openapi.testing.demetra.MockApplication";

    private static MockApplication applicationMock = createMockApplication();

    public static void reset() {
        stubInternalApplication();
    }

    public static MockApplication getMockApplication() {
        if (ourApplication == null) {
            stubInternalApplication();
        }
        return applicationMock;
    }

    private static void stubInternalApplication() {
        ourApplication = applicationMock;
    }

    private static MockApplication createMockApplication() {
        try {
            Class clazz = Class.forName(DEMETRA);
            Constructor constructor = clazz.getConstructor();
            return (MockApplication) constructor.newInstance();
        } catch (NumberFormatException e) {
            throw newFactoryRuntimeException(e);
        } catch (InstantiationException e) {
            throw newFactoryRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw newFactoryRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw newFactoryRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw newFactoryRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw newFactoryRuntimeException(e);
        }
    }

    private static RuntimeException newFactoryRuntimeException(Exception e) {
        return new RuntimeException("Could not load MockApplication", e);
    }
}
