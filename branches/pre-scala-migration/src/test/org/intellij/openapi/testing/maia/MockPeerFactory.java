package org.intellij.openapi.testing.maia;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilder;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diff.DiffRequestFactory;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileChooser.FileSystemTreeFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.ui.DialogWrapperPeerFactory;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusFactory;
import com.intellij.openapi.vcs.actions.VcsContextFactory;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.search.scope.packageSet.PackageSetFactory;
import com.intellij.ui.UIHelper;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.errorView.ErrorViewFactory;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcServer;

import java.awt.*;
import java.net.InetAddress;

public class MockPeerFactory extends PeerFactory {

    public FileStatusFactory getFileStatusFactory() {
        return new FileStatusFactory() {
            public FileStatus createFileStatus(String id, String description, Color color) {
                return new FileStatus() {
                    public Color getColor() {
                        return null;
                    }

                    public ColorKey getColorKey() {
                        return null;
                    }

                    public Color getDefaultColor() {
                        return null;
                    }

                    public String getText() {
                        return null;
                    }
                };
            }

            public FileStatus[] getAllFileStatuses() {
                return new FileStatus[0];
            }
        };
    }

    public DialogWrapperPeerFactory getDialogWrapperPeerFactory() {
        return new MockDialogWrapperPeerFactory();
    }

    public PackageSetFactory getPackageSetFactory() {
        return null;
    }

    public UIHelper getUIHelper() {
        return null;
    }

    public ErrorViewFactory getErrorViewFactory() {
        return null;
    }

    public ContentFactory getContentFactory() {
        return null;
    }

    public FileSystemTreeFactory getFileSystemTreeFactory() {
        return null;
    }

    public DiffRequestFactory getDiffRequestFactory() {
        return null;
    }

    public VcsContextFactory getVcsContextFactory() {
        return null;
    }

    public PsiBuilder createBuilder(ASTNode tree, Language language, CharSequence sequence, Project project) {
        return null;
    }

    public PsiBuilder createBuilder(ASTNode tree, Lexer lexer, Language lang, CharSequence seq, final Project project) {
        return null;
    }

    public XmlRpcServer createRpcServer() {
        return null;
    }

    public WebServer createWebServer(int port, InetAddress addr, XmlRpcServer xmlrpc) {
        return null;
    }

    public EditorHighlighter createEditorHighlighter(SyntaxHighlighter syntaxHighlighter, EditorColorsScheme colors) {
        return null;
    }

    public Sdk createProjectJdk(String name, String version, String homePath, SdkType sdkType) {
        return null;
    }

    public ModuleConfigurationEditor createModuleConfigurationEditor(String s, ModuleConfigurationState moduleConfigurationState) {
        return null;
    }
}
