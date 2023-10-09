package musta.belmo.plugins.ast;

import com.intellij.psi.PsiElement;

public interface Transformer {
    void transformPsi(PsiElement psiJavaFile, int selectedLine);
}
