package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
                    removeGetterAndSetter(aMethod, clone);
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

    private void removeGetterAndSetter(MethodDeclaration aMethod, CompilationUnit clone) {
        final String nameAsString = aMethod.getNameAsString();
        Predicate<MethodDeclaration> setterFromGetter = methodDeclaration -> ("set" + nameAsString.substring(3)).equals(methodDeclaration.getNameAsString());
        Predicate<MethodDeclaration> setterFromIs = methodDeclaration -> ("set" + nameAsString.substring(2)).equals(methodDeclaration.getNameAsString());
        final Optional<MethodDeclaration> setter = clone.findFirst(MethodDeclaration.class, setterFromGetter.or(setterFromIs));
        setter.ifPresent(MethodDeclaration::remove);
        aMethod.remove();
    }

    private void addLombokAnnotations(CompilationUnit clone) {
        clone.addImport("lombok.Getter");
        clone.addImport("lombok.Setter");
        ClassOrInterfaceDeclaration cls = clone.findAll(ClassOrInterfaceDeclaration.class).get(0);
        cls.addAnnotation(new MarkerAnnotationExpr("Getter"));
        cls.addAnnotation(new MarkerAnnotationExpr("Setter"));
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
