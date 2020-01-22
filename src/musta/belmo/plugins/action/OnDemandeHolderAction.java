package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.TransformerType;


public class OnDemandeHolderAction extends AbstractAction {

    @Override
    public TransformerType getType() {
        return TransformerType.ON_DEMAND_HOLDER;
    }
}