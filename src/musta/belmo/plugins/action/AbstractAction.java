package musta.belmo.plugins.action;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import musta.belmo.plugins.ast.SingletonFactory;
import musta.belmo.plugins.ast.Transformer;
import musta.belmo.plugins.ast.TransformerType;
import org.jetbrains.annotations.NotNull;

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

        if (psiFile != null && psiFile.getName().endsWith(".java")) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
            Document document = fileDocumentManager.getDocument(virtualFile);
            if (document != null) {
                try {
                    final String text = new String(virtualFile.contentsToByteArray());
                    final CompilationUnit generate = getTransformer().generate(text);

                    final Runnable runnable = () -> {
                        PrettyPrinterConfiguration prettyPrinterConfiguration = new PrettyPrinterConfiguration();
                        prettyPrinterConfiguration.setEndOfLineCharacter("\n");
                        document.setText(generate.toString(prettyPrinterConfiguration));
                    };
                    ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable, event.getProject()));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Runnable getRunnableWrapper(final Runnable runnable, Project project) {
        return () -> CommandProcessor.getInstance().executeCommand(project, runnable, "cut", ActionGroup.EMPTY_GROUP);
    }

    public abstract TransformerType getType();

    private Transformer getTransformer() {
        return SingletonFactory.getTransformer(getType());
    }
}
