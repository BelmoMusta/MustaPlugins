package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Generates Fields from gettes
 *
 * @since 0.0.0.SNAPSHOT
 * @author default author
 * @version 0.0.0
 */
public class FieldsFromGetters extends Transformer {

    /**
     * @param compilationUnitSrc {@link CompilationUnit}
     * @param line
     * @return CompilationUnit
     */
    public CompilationUnit generate(CompilationUnit compilationUnitSrc, int line) {
        CompilationUnit compilationUnit = compilationUnitSrc.clone();
        compilationUnit.accept(new FieldsFromGettersVisitor(), compilationUnit);
        return compilationUnit;
    }
}
