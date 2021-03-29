package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.RestWSCreator;
import musta.belmo.plugins.ast.Transformer;

public class RestWsCreatorAction extends AbstractAction {
    @Override
    protected Transformer getTransformer() {
        return new RestWSCreator();
    }
}