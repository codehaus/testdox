package org.codehaus.testdox.intellij;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.intellij.psi.PsiElement;

import static jedi.functional.FunctionalPrimitives.array;

public abstract class NullPsiElement implements PsiElement {

    public static final PsiElement INSTANCE;
    static {
        InvocationHandler nullPsiElementInvocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };

        Class[] interfaces = array(PsiElement.class);
        ClassLoader classLoader = nullPsiElementInvocationHandler.getClass().getClassLoader();

        INSTANCE = (PsiElement) Proxy.newProxyInstance(classLoader, interfaces, nullPsiElementInvocationHandler);
    }

    private NullPsiElement() { }
}
