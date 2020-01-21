package musta.belmo.plugins.action;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import musta.belmo.plugins.ast.JPAAnnotationsTransformer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class JPAAnnotationRemoverAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final JPAAnnotationsTransformer instance = JPAAnnotationsTransformer.getInstance();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        if (psiFile != null && psiFile.getName().endsWith(".java")) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
            Document document = fileDocumentManager.getDocument(virtualFile);
            if (document != null)
                try {
                    String text = new String(virtualFile.contentsToByteArray());
                    CompilationUnit generate = instance.generate(text);
                    WriteAction.run(() -> {
                        PrettyPrinterConfiguration prettyPrinterConfiguration = new PrettyPrinterConfiguration();
                        prettyPrinterConfiguration.setEndOfLineCharacter("\n");
                        document.setText(generate.toString(prettyPrinterConfiguration));

                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }


    }

}