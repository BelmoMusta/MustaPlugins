package musta.belmo.plugins.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import musta.belmo.plugins.ast.RestWSCreatorTransformer;
import musta.belmo.plugins.ast.Transformer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class RestWsCreatorAction extends AbstractAction {
    @Nullable
    @Override
    protected Transformer getTransformer() {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        int line = Optional.ofNullable(editor)
                .map(ed -> ed.getSelectionModel())
                .map(sm -> sm.getLeadSelectionOffset())
                //.map(leadPosition -> leadPosition.line)
                .orElse(-1);
        final String wsSignature = JOptionPane.showInputDialog("Enter WS signature");
        if (wsSignature == null || wsSignature.trim().isEmpty()) {
            return null;
        }
        return new RestWSCreatorTransformer(wsSignature, line);
    }
}