package musta.belmo.plugins.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import musta.belmo.plugins.ast.Transformer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class AbstractAction extends AnAction {

    private Transformer transformer;
    private PsiDocumentManager psiDocumentManager;

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (event.getProject() == null) {
            return;
        }

        transformer = getTransformer();
        psiDocumentManager = PsiDocumentManager.getInstance(event.getProject());
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Navigatable navigatable = event.getData(CommonDataKeys.NAVIGATABLE);
        List<PsiJavaFile> allFiles = new ArrayList<>();

        if (psiFile instanceof PsiJavaFile psiJavaFile) {
            allFiles.add(psiJavaFile);
        } else if (navigatable instanceof PsiJavaDirectoryImpl directory) {
            allFiles.addAll(getAllJavaFiles(directory));
        }
        for (PsiJavaFile file : allFiles) {
            applyActionToDocument(event, file);
        }
    }
    private void applyActionToDocument(@NotNull AnActionEvent event,
                                       PsiJavaFile psiJavaFile) {
        VirtualFile virtualFile = psiJavaFile.getVirtualFile();

        Caret caret = event.getData(CommonDataKeys.CARET);
        int line = -1;
        if (caret != null) {
            LogicalPosition logicalPosition = caret.getLogicalPosition();
            line = logicalPosition.line;
        }
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(virtualFile);
        if (document != null) {
            fileDocumentManager.saveDocument(document); // get the last state of a document
            try {
                final LombokWrapper lombokWrapper = transformer.transform(psiJavaFile, line);

                if (lombokWrapper == null) {
                    return;
                }
                final Runnable runnable = () -> {

                    applyLombokWrapperToFile(lombokWrapper);
                    psiDocumentManager.commitDocument(document);
                };
                ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable,
                        event.getProject()));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void applyLombokWrapperToFile(LombokWrapper lombokWrapper) {

    }

    private Runnable getRunnableWrapper(final Runnable runnable, Project project) {
        return () -> CommandProcessor.getInstance().executeCommand(project, runnable, "cut", ActionGroup.EMPTY_GROUP);
    }

    protected  Transformer getTransformer(){
        return getTransformer(new ArrayList<>());
    }
    protected abstract Transformer getTransformer(List<PsiJavaFile> psiFiles);
    private List<PsiJavaFile> getAllJavaFiles(PsiDirectory dir) {
        List<PsiJavaFile> files = new ArrayList<>();
        Stack<PsiElement> stack = new Stack<>();
        stack.push(dir);
        while (!stack.isEmpty()) {
            PsiElement child = stack.pop();
            if (child instanceof PsiDirectory psiDirectory) {
                for (PsiElement f : psiDirectory.getChildren()) {
                    stack.push(f);
                }
            } else if (child instanceof PsiJavaFile psiFile) {
                files.add(psiFile);
            }
        }
        return files;
    }

}
