package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.FieldsFromGetters;
import musta.belmo.plugins.ast.Transformer;

public class FieldsFromGettersAction extends AbstractAction {
    
    @Override
    protected Transformer getTransformer() {
        return new FieldsFromGetters();
    }
}