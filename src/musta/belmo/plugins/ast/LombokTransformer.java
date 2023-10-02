package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generates Fields from gettes
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class LombokTransformer extends Transformer {
    private final List<String> annotations;
    public LombokTransformer(List<String> annotations) {
        this.annotations = annotations;
    }

    /**
     * @param compilationUnitSrc {@link CompilationUnit}
     * @param line
     * @return CompilationUnit
     */
    @Override
    public CompilationUnit generate(CompilationUnit compilationUnitSrc, int line) {
        MyCloneVisitor myCloneVisitor = new MyCloneVisitor();
        CompilationUnit clone = (CompilationUnit) compilationUnitSrc.accept(myCloneVisitor, null);
        if (line == -1) {
            return null;
        }
        Optional<ClassOrInterfaceDeclaration> first =
                clone.findAll(ClassOrInterfaceDeclaration.class).stream().filter(cls -> {
                    return cls.getBegin().get().line <= line + 1
                            && cls.getEnd().get().line >= line + 1;
                }).findFirst();
        if (first.isEmpty()) {
            return null;
        }

        Optional<ClassOrInterfaceDeclaration> innerClass =
                first.get().findAll(ClassOrInterfaceDeclaration.class).stream().filter(cls ->
                        !cls.equals(first.get())
                                && cls.getBegin().get().line <= line + 1
                                && cls.getEnd().get().line >= line + 1).findFirst();
        final MethodToFieldBinderVisitor visitor;
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

        classOrInterfaceDeclaration = innerClass.orElseGet(first::get);
        visitor = new MethodToFieldBinderVisitor(classOrInterfaceDeclaration);


        int nbDeletedMethods = 0;
        for (Map.Entry<String, MethodFieldPair> keyValue : visitor.entrySet()) {
            MethodFieldPair value = keyValue.getValue();
            MethodDeclaration getterMethod = value.getGetterMethod();
            MethodDeclaration setterMethod = value.getSetterMethod();
            if (getterMethod != null) {
                if (annotations.contains("Getter")) {
                    getterMethod.remove();
                    nbDeletedMethods++;
                }
            }
            if (setterMethod != null) {
                if (annotations.contains("Setter")) {
                    setterMethod.remove();
                    nbDeletedMethods++;
                }
            }
        }

        if (nbDeletedMethods > 0) {
            addLombokAnnotations(classOrInterfaceDeclaration);
            if (annotations.contains("Getter")) {
                long countOfLombokGetterImports = clone.getImports().stream()
                        .filter(imp -> imp.toString().contains("lombok.Getter"))
                        .count();
                if (countOfLombokGetterImports == 0L) {
                    clone.addImport("lombok.Getter");
                }
            }

            if (annotations.contains("Setter")) {
                long countOfLombokSetterImports = clone.getImports().stream()
                        .filter(imp -> imp.toString().contains("lombok.Setter"))
                        .count();
                if (countOfLombokSetterImports == 0L) {
                    clone.addImport("lombok.Setter");
                }
            }

        }

        return clone;
    }

    private void addLombokAnnotations(ClassOrInterfaceDeclaration cls) {
        if (annotations.contains("Getter")) {
            long countGetterAnnotations = cls.findAll(MarkerAnnotationExpr.class,
                    p -> p.getNameAsString().equals("Getter")).size();
            if (countGetterAnnotations == 0L) {
                cls.addAnnotation(new MarkerAnnotationExpr("Getter"));
            }
        }

        if (annotations.contains("Setter")) {
            long countSetterAnnotations = cls.findAll(MarkerAnnotationExpr.class,
                    p -> p.getNameAsString().equals("Setter")).size();
            if (countSetterAnnotations == 0L) {
                cls.addAnnotation(new MarkerAnnotationExpr("Setter"));
            }
        }
    }

}
