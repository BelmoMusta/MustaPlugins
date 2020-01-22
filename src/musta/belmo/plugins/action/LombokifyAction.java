package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.TransformerType;


public class LombokifyAction extends AbstractAction {

    @Override
    public TransformerType getType() {
        return TransformerType.LOMBOK;
    }
}