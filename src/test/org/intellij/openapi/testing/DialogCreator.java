package org.intellij.openapi.testing;

import com.intellij.openapi.ui.DialogWrapper;

public abstract class DialogCreator<T extends DialogWrapper> implements Runnable {

    private T dialog;

    public final void run() {
        dialog = create();
    }

    protected abstract T create();

    public T getDialog() {
        return dialog;
    }
}
