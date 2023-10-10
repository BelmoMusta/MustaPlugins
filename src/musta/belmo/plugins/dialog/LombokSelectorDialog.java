package musta.belmo.plugins.dialog;

import javax.swing.*;
import java.awt.*;

public class LombokSelectorDialog extends JFrame {
    public LombokSelectorDialog(Component component){
        JCheckBox checkBox1 = new JCheckBox("C++");
        checkBox1.setBounds(100,100, 50,50);
        JCheckBox checkBox2 = new JCheckBox("Java", true);
        checkBox2.setBounds(100,150, 50,50);
        add(checkBox1);
        add(checkBox2);
        setSize(400,400);
        setLayout(component.getParent().getLayout());
        setVisible(true);
    }
}
