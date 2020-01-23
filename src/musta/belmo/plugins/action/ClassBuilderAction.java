package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.TransformerType;


public class ClassBuilderAction extends AbstractAction {

    @Override
    public TransformerType getType() {
        return TransformerType.CLASS_BUILDER;
    }
}