package musta.belmo.plugins.action.text;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class AbstractTextAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        final String[] text = new String[1];
        int[] start = {0};
        int[] end = {0};
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            if (!primaryCaret.isValid()) {
                final VirtualFile virtualFile = psiFile.getVirtualFile();
                try {
                    text[0] = new String(virtualFile.contentsToByteArray());
                    end[0] = text[0].length();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                start[0] = primaryCaret.getSelectionStart();
                end[0] = primaryCaret.getSelectionEnd();
                text[0] = document.getText(new TextRange(start[0], end[0]));
            }

        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
                    document.replaceString(start[0], end[0], changeText(text[0]));
                }
        );
        // De-select the text range that was just replaced


        primaryCaret.removeSelection();
    }


    public abstract String changeText(String string);


}
