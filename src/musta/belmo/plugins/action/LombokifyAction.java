package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.LombokTransformer;
import musta.belmo.plugins.ast.Transformer;

import javax.swing.*;
import java.util.ArrayList;

public class LombokifyAction extends AbstractAction {
    @Override
    protected Transformer getTransformer() {
        JPanel al = new JPanel();
        JCheckBox getter = new JCheckBox("Getter");
        JCheckBox setter = new JCheckBox("Setter");
        JCheckBox allArgsConstructor = new JCheckBox("AllArgsConstructor");
        JCheckBox noArgsConstructor = new JCheckBox("NoArgsConstructor");
        al.add(getter);
        al.add(setter);
        al.add(allArgsConstructor);
        al.add(noArgsConstructor);

        int input = JOptionPane.showConfirmDialog(null, al, "Choose annotations", JOptionPane.OK_OPTION);

        java.util.List<String> annotations = new ArrayList<>();
        if (input != JOptionPane.OK_OPTION) {
            if (getter.isSelected()) {
                annotations.add(getter.getText());
            }
            if (setter.isSelected()) {
                annotations.add(setter.getText());
            }
        }
        return new LombokTransformer(annotations);
    }
}