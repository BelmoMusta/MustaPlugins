package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.ClassBuilder;
import musta.belmo.plugins.ast.Transformer;

public class ClassBuilderAction extends AbstractAction {
    
    @Override
    protected Transformer getTransformer() {
        return new ClassBuilder();
    }
}