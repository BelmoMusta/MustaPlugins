package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates Fields from gettes
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class HibernateAnnotationsTransformer extends Transformer {
    private static HibernateAnnotationsTransformer INSTANCE;

    public static HibernateAnnotationsTransformer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HibernateAnnotationsTransformer();
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
        CompilationUnit clone = compilationUnitSrc.clone();
        List<MethodDeclaration> methodDeclarations = clone.findAll(MethodDeclaration.class);
        List<FieldDeclaration> fields = clone.findAll(FieldDeclaration.class);

        Iterator<MethodDeclaration> iterator = methodDeclarations.iterator();
        while (iterator.hasNext()) {
            final MethodDeclaration aMethod = iterator.next();
            if (CodeUtils.isGetter(aMethod) || CodeUtils.isIs(aMethod)) {
                if (hasHibernateAnnotations(aMethod)) {
                    MethodDeclaration setterMethod = getSetterMethod(aMethod, methodDeclarations);
                    moveAnnotationsToField(aMethod, fields);
                    aMethod.remove();
                    setterMethod.remove();
                }
            }
        }

        return clone;
    }

    private void moveAnnotationsToField(MethodDeclaration aMethod, List<FieldDeclaration> fields) {
        final FieldDeclaration fieldDeclaration = getFieldFromMethod(aMethod, fields);
        if (fieldDeclaration != null) {
            aMethod.getAnnotations().forEach(fieldDeclaration::addAnnotation);
        }
    }

    private FieldDeclaration getFieldFromMethod(MethodDeclaration aMethod, List<FieldDeclaration> fields) {
        return fields.stream()
                .filter(fieldDeclaration -> {
                    VariableDeclarator variableDeclarator = fieldDeclaration.getVariables().get(0);
                    boolean findSetterFromGetter = variableDeclarator.getNameAsString().equals(CodeUtils.toLowerCaseFirstLetter(aMethod.getNameAsString().substring(3)));
                    boolean findSetterFromIs = variableDeclarator.getNameAsString().equals(CodeUtils.toLowerCaseFirstLetter(aMethod.getNameAsString().substring(2)));
                    return findSetterFromGetter || findSetterFromIs;
                })
                .findFirst()
                .orElse(null);
    }

    private MethodDeclaration getSetterMethod(MethodDeclaration aMethod, List<MethodDeclaration> methodDeclarations) {
        return methodDeclarations.stream()
                .filter(methodDeclaration -> {
                    final String nameAsString = methodDeclaration.getNameAsString();
                    final boolean findSetterFromGetter = nameAsString.equals("set" + aMethod.getNameAsString().substring(3));
                    final boolean findSetterFromIs = nameAsString.equals("set" + aMethod.getNameAsString().substring(2));
                    return findSetterFromGetter || findSetterFromIs;
                })
                .findFirst()
                .orElse(aMethod);
    }

    private boolean hasHibernateAnnotations(MethodDeclaration aMethod) {
        final List<String> hibernateAnnotations = Arrays.asList("Enumerated", "Column", "Id", "OneToOne", "ManyToMany", "OneToMany", "ManyToOne");
        final List<String> annotations = aMethod.getAnnotations().stream()
                .map(NodeWithName::getNameAsString)
                .collect(Collectors.toList());
        return !Collections.disjoint(hibernateAnnotations, annotations);

    }
}
