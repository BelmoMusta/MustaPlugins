package musta.belmo.plugins.ast;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;

import java.util.List;
import java.util.stream.Collectors;

public class PsiLombokTransformer implements Transformer {
    private final List<LombokAnnotation> annotations;

    public PsiLombokTransformer(List<String> annotations) {
        this.annotations =
                annotations.stream().map(LombokAnnotation::new).collect(Collectors.toList());
    }

    public void transformPsi(PsiElement psiElement) {
        if (annotations.isEmpty()) {
            return;
        }
        if (psiElement instanceof PsiJavaFile psiJavaFile) {
            List<PsiClass> allClassesInFile = PsiUtils.getAllClassesInFile(psiJavaFile);
            PsiClassUtils.deleteMethods(allClassesInFile, annotations);
            PsiClassUtils.addAnnotations(annotations, allClassesInFile);
            PsiUtils.addImports(psiJavaFile, annotations);
        }
    }

    @Override
    public String getActionName() {
        return "Lombokify";
    }
    @Override
    public boolean isApplied() {
        return !annotations.isEmpty();
    }
}
