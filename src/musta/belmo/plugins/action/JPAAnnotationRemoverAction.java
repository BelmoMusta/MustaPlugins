package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.JPAAnnotationsTransformer;
import musta.belmo.plugins.ast.Transformer;

public class JPAAnnotationRemoverAction extends AbstractAction {
    
    @Override
    protected Transformer getTransformer() {
        return new JPAAnnotationsTransformer();
    }
}