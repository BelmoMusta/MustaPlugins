package musta.belmo.plugins.ast;

import com.intellij.psi.PsiElement;

public interface Transformer {
    void transformPsi(PsiElement psiElement);

    String getActionName();

    boolean isApplied();
}
