package org.intellij.openapi.testing;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class MockIntentionManager extends IntentionManager {

    public void addAction(IntentionAction action) {
    }

    public IntentionAction[] getIntentionActions() {
        return new IntentionAction[0];
    }

    @NotNull
    @Override
    public IntentionAction[] getAvailableIntentionActions() {
        return new IntentionAction[0];
    }

    public void registerIntentionAndMetaData(IntentionAction action, String... category) {
    }

    @Deprecated
    public void registerIntentionAndMetaData(IntentionAction action, String[] category, String descriptionDirectoryName) {
    }

    public void registerIntentionAndMetaData(IntentionAction action, String[] category, String description, String exampleFileExtension, String[] exampleTextBefore, String[] exampleTextAfter) {
    }

    @Override
    public void unregisterIntention(@NotNull IntentionAction intentionAction) {
    }

    public java.util.List<IntentionAction> getStandardIntentionOptions(HighlightDisplayKey displayKey, PsiElement context) {
        return null;
    }

    public LocalQuickFix convertToFix(IntentionAction action) {
        return null;
    }
}
