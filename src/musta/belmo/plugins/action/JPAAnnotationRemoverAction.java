package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.TransformerType;


public class JPAAnnotationRemoverAction extends AbstractAction {

    @Override
    public TransformerType getType() {
        return TransformerType.JPA;
    }
}