package org.intellij.openapi.testing;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;

public class MockIntentionManager extends IntentionManager {

    public void addAction(IntentionAction action) {
    }

    public IntentionAction[] getIntentionActions() {
        return new IntentionAction[0];
    }

    public void registerIntentionAndMetaData(IntentionAction action, String... category) {
    }

    @Deprecated
    public void registerIntentionAndMetaData(IntentionAction action, String[] category, String descriptionDirectoryName) {
    }

    public java.util.List<IntentionAction> getStandardIntentionOptions(HighlightDisplayKey displayKey, PsiElement context) {
        return null;
    }

    public LocalQuickFix convertToFix(IntentionAction action) {
        return null;
    }
}
