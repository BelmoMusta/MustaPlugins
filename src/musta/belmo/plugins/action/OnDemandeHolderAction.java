package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.GenerateOnDemandHolderPattern;
import musta.belmo.plugins.ast.Transformer;


public class OnDemandeHolderAction extends AbstractAction {
    @Override
    protected Transformer getTransformer() {
        return new GenerateOnDemandHolderPattern();
    }
}