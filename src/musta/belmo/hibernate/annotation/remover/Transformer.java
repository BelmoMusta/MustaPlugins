package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.ast.CompilationUnit;

public abstract class Transformer {
    public abstract CompilationUnit generate(String compilationUnitSrc);
}
