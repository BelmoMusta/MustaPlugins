package musta.belmo.plugins.action;

import musta.belmo.plugins.ast.LombokTransformer;
import musta.belmo.plugins.ast.Transformer;

public class LombokifyAction extends AbstractAction {
    @Override
    protected Transformer getTransformer() {
        return new LombokTransformer();
    }
}