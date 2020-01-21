package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.List;

/**
 * Generates Fields from gettes
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class LombokTransformer extends Transformer {
    private static LombokTransformer INSTANCE;

    public static LombokTransformer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LombokTransformer();
        }
        return INSTANCE;
    }

    /**
     * @param compilationUnitSrc {@link CompilationUnit}
     * @return CompilationUnit
     */
    @Override
    public CompilationUnit generate(String compilationUnitSrc) {
        return generate(JavaParser.parse(compilationUnitSrc));
    }

    public CompilationUnit generate(CompilationUnit compilationUnitSrc) {
        boolean isAddLombok = false;
        CompilationUnit clone = compilationUnitSrc.clone();
        List<MethodDeclaration> methodDeclarations = clone.findAll(MethodDeclaration.class);
        List<FieldDeclaration> fields = clone.findAll(FieldDeclaration.class);

        for (MethodDeclaration aMethod : methodDeclarations) {
            if (CodeUtils.isGetter(aMethod) || CodeUtils.isIs(aMethod) || CodeUtils.isGetter(aMethod)) {
                if (hasAssociatedField(aMethod, fields)) {
                    aMethod.remove();
                    if (!isAddLombok) {
                        isAddLombok = true;
                    }
                }

            }
        }
        if (isAddLombok) {
            addLombokAnnotations(clone);
        }

        return clone;
    }

    private void addLombokAnnotations(CompilationUnit clone) {
        clone.addImport("lombok.Getter");
        clone.addImport("lombok.Setter");
        clone.addAnnotationDeclaration("Getter");
        clone.addAnnotationDeclaration("Setter");
    }

    private boolean hasAssociatedField(MethodDeclaration aMethod, List<FieldDeclaration> fields) {
        return fields.stream()
                .anyMatch(fieldDeclaration -> {
                    VariableDeclarator variableDeclarator = fieldDeclaration.getVariables().get(0);
                    boolean findFieldFromGetterOrSetter = variableDeclarator.getNameAsString().equals(CodeUtils.toLowerCaseFirstLetter(aMethod.getNameAsString().substring(3)));
                    boolean findFieldFromIs = variableDeclarator.getNameAsString().equals(CodeUtils.toLowerCaseFirstLetter(aMethod.getNameAsString().substring(2)));
                    return findFieldFromGetterOrSetter || findFieldFromIs;
                });
    }
}
