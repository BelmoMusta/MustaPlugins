package musta.belmo.plugins.ast;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import musta.belmo.plugins.action.LombokWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    private void addBuilder(ClassOrInterfaceDeclaration selectedClass, CompilationUnit clone) {
        if (annotations.contains("Builder")) {
            addLombokAnnotations(selectedClass, Arrays.asList("Builder"));
            addImportIfNotExists(clone, "lombok.Builder");
        }
    }
    private void addData(ClassOrInterfaceDeclaration selectedClass, CompilationUnit clone) {
        if (annotations.contains("Data")) {
            addLombokAnnotations(selectedClass, Arrays.asList("Data"));
            addImportIfNotExists(clone, "lombok.Data");
        }
    }
    private void deleteConstructors(ClassOrInterfaceDeclaration selectedClass, CompilationUnit clone) {

        if (annotations.contains("NoArgsConstructor")) {
            Stream<ConstructorDeclaration> allConstructors = selectedClass.findAll(ConstructorDeclaration.class)
                    .stream()
                    .filter(constructor -> constructor.getParentNode().get().equals(selectedClass))
                    .filter(constructor -> constructor.getParameters().isEmpty());
            allConstructors.forEach(Node::remove);
            addImportIfNotExists(clone, "lombok.NoArgsConstructor");
            addLombokAnnotations(selectedClass, Arrays.asList("NoArgsConstructor"));
        }
        if (annotations.contains("AllArgsConstructor")) {
            Stream<ConstructorDeclaration> allConstructors = selectedClass.findAll(ConstructorDeclaration.class)
                    .stream()
                    .filter(constructor -> constructor.getParentNode().get().equals(selectedClass));
            allConstructors.forEach(Node::remove);
            addImportIfNotExists(clone, "lombok.AllArgsConstructor");
            addLombokAnnotations(selectedClass, Arrays.asList("AllArgsConstructor"));
        }

    }
    @Nullable
    private static ClassOrInterfaceDeclaration getSelectedClass(int line, CompilationUnit clone) {
        Optional<ClassOrInterfaceDeclaration> first =
                clone.findAll(ClassOrInterfaceDeclaration.class).stream().filter(cls -> {
                    return !cls.isInterface()
                            && !cls.isEnumDeclaration()
                            && cls.getBegin().map(pos -> pos.line).orElse(Integer.MIN_VALUE) <= line + 1
                            && cls.getEnd().map(pos -> pos.line).orElse(Integer.MAX_VALUE) >= line + 1;
                }).findFirst();
        if (first.isEmpty()) {
            return null;
        }

        Optional<ClassOrInterfaceDeclaration> innerClass =
                first.get().findAll(ClassOrInterfaceDeclaration.class).stream().filter(cls ->
                        !cls.equals(first.get())
                                && cls.getBegin().map(pos -> pos.line).orElse(Integer.MIN_VALUE) <= line + 1
                                && cls.getEnd().map(pos -> pos.line).orElse(Integer.MAX_VALUE) >= line + 1).findFirst();

        return innerClass.orElseGet(first::get);
    }
    private void deleteGettersAndSetters(ClassOrInterfaceDeclaration classOrInterfaceDeclaration,
                                         CompilationUnit clone) {
        final MethodToFieldBinderVisitor visitor = new MethodToFieldBinderVisitor(classOrInterfaceDeclaration);
        for (Map.Entry<String, MethodFieldPair> keyValue : visitor.entrySet()) {
            MethodFieldPair value = keyValue.getValue();
            MethodDeclaration getterMethod = value.getGetterMethod();
            MethodDeclaration setterMethod = value.getSetterMethod();
            if (getterMethod != null && (annotations.contains("Getter"))) {
                getterMethod.remove();
            }

            if (setterMethod != null && annotations.contains("Setter")) {
                setterMethod.remove();
            }
        }

        if (annotations.contains("Getter")) {
            addLombokAnnotations(classOrInterfaceDeclaration, Arrays.asList("Getter"));
            addImportIfNotExists(clone, "lombok.Getter");
        }

        if (annotations.contains("Setter")) {
            addLombokAnnotations(classOrInterfaceDeclaration, Arrays.asList("Setter"));
            addImportIfNotExists(clone, "lombok.Setter");
        }
    }
    private static void addImportIfNotExists(CompilationUnit clone, String importName) {
        long countOfLombokGetterImports = clone.getImports().stream()
                .filter(imp -> imp.toString().contains(importName))
                .count();
        if (countOfLombokGetterImports == 0L) {
            clone.addImport(importName);
        }
    }

    private void addLombokAnnotations(ClassOrInterfaceDeclaration cls, List<String> annotations) {
        BiConsumer<String, ClassOrInterfaceDeclaration> consumer
                = (annotation, aCls) -> {
            long countGetterAnnotations = cls.findAll(MarkerAnnotationExpr.class,
                    ann -> cls.equals(ann.getParentNode().orElse(null))
                            && ann.getNameAsString().equals(annotation)).size();
            if (countGetterAnnotations == 0L) {
                MarkerAnnotationExpr markerAnnotationExpr = new MarkerAnnotationExpr(annotation);
                cls.addAnnotation(markerAnnotationExpr);
            }
        };
        for (String annotation : annotations) {
            consumer.accept(annotation, cls);
        }

        if (annotations.contains("Setter")) {
            long countSetterAnnotations = cls.findAll(MarkerAnnotationExpr.class,
                    ann -> cls.equals(ann.getParentNode().orElse(null))
                            && ann.getNameAsString().equals("Setter")).size();
            if (countSetterAnnotations == 0L) {
                MarkerAnnotationExpr setter = new MarkerAnnotationExpr("Setter");
                cls.addAnnotation(setter);
            }
        }
    }
    @Override
    public LombokWrapper transform(PsiJavaFile psiJavaFile, int line) {
        LombokWrapper lombokWrapper = new LombokWrapper(psiJavaFile);
        CompilationUnit compilationUnit = MyJavaParser.parse(psiJavaFile.getText());

        if (line == -1) {
            List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                    .stream().filter(cls ->
                            !cls.isInterface()
                                    && !cls.isEnumDeclaration())
                    .collect(Collectors.toList());
            if (classes.isEmpty()) {
                return null;
            }
            for (ClassOrInterfaceDeclaration selectedClass :
                    classes) {
                PsiClass[] classes1 = psiJavaFile.getClasses();
                deleteGettersAndSetters(selectedClass, compilationUnit);
                deleteConstructors(selectedClass, compilationUnit);
                addBuilder(selectedClass, compilationUnit);
                addData(selectedClass, compilationUnit);
            }
            return lombokWrapper;
        }

        ClassOrInterfaceDeclaration selectedClass = getSelectedClass(line, compilationUnit);
        if (selectedClass == null) {
            return null;
        }
        deleteGettersAndSetters(selectedClass, compilationUnit);
        deleteConstructors(selectedClass, compilationUnit);
        addBuilder(selectedClass, compilationUnit);
        addData(selectedClass, compilationUnit);

        return lombokWrapper;
    }
}
