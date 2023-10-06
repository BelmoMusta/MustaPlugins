package musta.belmo.plugins.ast;

import com.intellij.psi.PsiJavaFile;
import musta.belmo.plugins.action.LombokWrapper;

public abstract class Transformer {
    public abstract LombokWrapper transform(PsiJavaFile code, int line);


}
