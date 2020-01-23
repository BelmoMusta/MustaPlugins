package musta.belmo.plugins.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class UnicodifyAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        WriteCommandAction.runWriteCommandAction(project, () -> {
                    String text = document.getText(new TextRange(start, end));
                    document.replaceString(start, end, unicodify(text));
                }
        );
        // De-select the text range that was just replaced

        //יייייייייייייייייייייייייי
        primaryCaret.removeSelection();
    }

    public static String unicodify(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (ch >= 127) {
                String hexDigits = Integer.toHexString(ch);//.toUpperCase();
                String escapedCh = "\\u" + "0000".substring(hexDigits.length()) + hexDigits;
                stringBuilder.append(escapedCh);
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }
}
