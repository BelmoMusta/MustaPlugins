package musta.belmo.plugins.action;

import com.intellij.psi.PsiJavaFile;
import musta.belmo.plugins.ast.LombokAnnotation;

import java.util.ArrayList;
import java.util.List;

public class LombokWrapper {
    private final PsiJavaFile javaFile;
    private final List<LombokAnnotation> annotations = new ArrayList<>();
    public LombokWrapper(PsiJavaFile javaFile) {
        this.javaFile = javaFile;
    }
    public void add(LombokAnnotation annotation) {
        annotations.add(annotation);
    }
}
