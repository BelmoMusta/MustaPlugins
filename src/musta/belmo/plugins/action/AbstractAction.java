package musta.belmo.plugins.action;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import musta.belmo.plugins.ast.Transformer;
import musta.belmo.plugins.dialog.LombokSelectorDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

public abstract class AbstractAction extends AnAction {


    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Caret caret = event.getData(CommonDataKeys.CARET);
        int line = -1;
        if (caret != null) {
            VisualPosition leadSelectionPosition = caret.getLeadSelectionPosition();
            line = leadSelectionPosition.getLine();
        }
        if (psiFile != null && psiFile.getName().endsWith(".java")) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
            Document document = fileDocumentManager.getDocument(virtualFile);
            if (document != null) {
                fileDocumentManager.saveDocument(document); // get the last state of a document
                try {
                    final String text = document.getText();
                    final CompilationUnit generate = getTransformer().generate(text, line);

                    if (generate == null) {
                        return;
                    }
                    final Runnable runnable = () -> {
//                        PrettyPrinterConfiguration prettyPrinterConfiguration = new PrettyPrinterConfiguration();
//						prettyPrinterConfiguration.setColumnAlignFirstMethodChain(true);
//						prettyPrinterConfiguration.setColumnAlignParameters(true);
//                        prettyPrinterConfiguration.setEndOfLineCharacter("\n");
                        document.setText(generate.toString());
                        fileDocumentManager.saveDocument(document);
                    };
                    ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable,
                            event.getProject()));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Runnable getRunnableWrapper(final Runnable runnable, Project project) {
        return () -> CommandProcessor.getInstance().executeCommand(project, runnable, "cut", ActionGroup.EMPTY_GROUP);
    }

    protected abstract Transformer getTransformer();
}
