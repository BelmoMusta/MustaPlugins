package musta.belmo.plugins.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import musta.belmo.plugins.ast.PsiUtils;
import musta.belmo.plugins.ast.Transformer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAction extends AnAction {

    private Transformer transformer;

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
        final int selectedLine = PsiUtils.getSelectedLine(event);

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Navigatable navigatable = event.getData(CommonDataKeys.NAVIGATABLE);
        List<PsiElement> selectedFiles = new ArrayList<>();

        if (psiFile instanceof PsiJavaFile psiJavaFile) {
            selectedFiles.add(psiJavaFile);
        } else if (navigatable instanceof PsiJavaDirectoryImpl directory) {
            selectedFiles.addAll(PsiUtils.getAllJavaFiles(directory));
        }
        applyAction(event, selectedFiles, selectedLine);
    }
    private void applyAction(@NotNull AnActionEvent event, List<PsiElement> psiJavaFiles, int line) {
        transformer = getTransformer();
        try {
            CommandProcessor.getInstance().executeCommand(getEventProject(event),
                    () -> ApplicationManager.getApplication().runWriteAction(() -> {
                        for (PsiElement psiJavaFile : psiJavaFiles) {
                            transformer.transformPsi(psiJavaFile, line);
                        }
                    }), "Lombokify", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    protected abstract Transformer getTransformer();
}
