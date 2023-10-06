package musta.belmo.plugins.action;

import com.intellij.psi.PsiJavaFile;
import musta.belmo.plugins.ast.LombokTransformer;
import musta.belmo.plugins.ast.Transformer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LombokifyAction extends AbstractAction {
    @Override
    protected Transformer getTransformer(List<PsiJavaFile> psiFiles) {
        JPanel al = new JPanel();
        List<JCheckBox> checkBoxes = getCheckBoxes();
        for (JCheckBox checkBox : checkBoxes) {
            al.add(checkBox);
        }

        int input = JOptionPane.showConfirmDialog(null, al, "Choose annotations", JOptionPane.OK_OPTION);
        java.util.List<String> annotations = new ArrayList<>();
        if (input == JOptionPane.OK_OPTION) {
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()){
                    annotations.add(checkBox.getText());
                }
            }
        }
        return new LombokTransformer(annotations);
    }
    @NotNull
    private static List<JCheckBox> getCheckBoxes() {
        JCheckBox getter = new JCheckBox("Getter");
        JCheckBox setter = new JCheckBox("Setter");
        JCheckBox allArgsConstructor = new JCheckBox("AllArgsConstructor");
        JCheckBox noArgsConstructor = new JCheckBox("NoArgsConstructor");
        JCheckBox builder = new JCheckBox("Builder");
        JCheckBox data = new JCheckBox("Data");
        List<JCheckBox> checkBoxes = new ArrayList<>();
        checkBoxes.add(getter);
        checkBoxes.add(setter);
        checkBoxes.add(allArgsConstructor);
        checkBoxes.add(noArgsConstructor);
        checkBoxes.add(builder);
        checkBoxes.add(data);
        return checkBoxes;
    }
}