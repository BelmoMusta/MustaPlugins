package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.TransformerType;


public class FieldsFromGettersAction extends AbstractAction {

    @Override
    public TransformerType getType() {
        return TransformerType.FIELDS_FROM_GETTERS;
    }
}