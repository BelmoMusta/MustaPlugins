package musta.belmo.plugins.ast;

import com.intellij.psi.PsiJavaFile;

public abstract class Transformer {
    public abstract String transform(PsiJavaFile code, int line);


}
