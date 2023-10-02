package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import java.util.EnumSet;
import java.util.stream.Stream;

/**
 * Created by DELL on 12/03/2018.
 */
public class ClassBuilder extends Transformer {

    @Override
    public CompilationUnit generate(CompilationUnit code, int line) {
        return buildFromClass(code, "Builder");
    }

    public CompilationUnit generate(CompilationUnit code, String destClassName) {
        return buildFromClass(code, destClassName);
    }

    private CompilationUnit buildFromClass(CompilationUnit compilationUnit, String destClassName) {
        final CompilationUnit resultUnit = compilationUnit.clone();

        resultUnit.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(srcClass ->
                        !srcClass.isAbstract()
                                && !srcClass.isInterface()
                                && !srcClass.isInnerClass()).
                forEach(classDef -> {
                    ClassOrInterfaceDeclaration builderInnerClass = new ClassOrInterfaceDeclaration();
                    builderInnerClass.setName(String.format("%s" + destClassName, classDef.getNameAsString()));
                    builderInnerClass.addModifier(Modifier.PUBLIC, Modifier.STATIC);
                    classDef.addMember(builderInnerClass);
                    VariableDeclarator variableDeclarator = createConstructor(classDef, builderInnerClass);
                    createMethods(classDef, builderInnerClass, variableDeclarator);
                    createBuildMethod(classDef, builderInnerClass, variableDeclarator);
                });
        return resultUnit;
    }

    private void createBuildMethod(ClassOrInterfaceDeclaration classDef, ClassOrInterfaceDeclaration classDeclaration, VariableDeclarator variableDeclarator) {
        MethodDeclaration buildMethod = classDeclaration.addMethod("build");
        buildMethod.setType(classDef.getNameAsString());
        ReturnStmt returnStatement = new ReturnStmt();
        returnStatement.setExpression(variableDeclarator.getNameAsExpression());
        buildMethod.addModifier(Modifier.PUBLIC);
        buildMethod.getBody().ifPresent(body -> body.addStatement(returnStatement));
    }

    private void createMethods(ClassOrInterfaceDeclaration classDef, ClassOrInterfaceDeclaration classDeclaration, VariableDeclarator variableDeclarator) {
        Stream<MethodDeclaration> methodDeclarationStream = classDef.findAll(MethodDeclaration.class).stream().filter(methodDeclaration ->
                !methodDeclaration.isAnnotationPresent("Override"))
                .filter(methodDeclaration ->
                        !methodDeclaration.getName().asString().matches("(get|is).+")
                                && !"clone".equals(methodDeclaration.getName().asString()));

        methodDeclarationStream.forEach(methodDeclaration -> {
            createMethod(classDeclaration, variableDeclarator, methodDeclaration);
        });
    }

    private VariableDeclarator createConstructor(ClassOrInterfaceDeclaration srcClass, ClassOrInterfaceDeclaration destClass) {
        FieldDeclaration fieldDeclaration = createField(srcClass, destClass);

        VariableDeclarator variableDeclarator = fieldDeclaration.getVariable(0);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(srcClass.getName().asString());
        AssignExpr assignExpr = new AssignExpr(variableDeclarator.getNameAsExpression(),
                objectCreationExpr, AssignExpr.Operator.ASSIGN);
        ConstructorDeclaration constructorDeclaration = destClass.addConstructor(Modifier.PUBLIC);
        BlockStmt constructorBody = new BlockStmt();
        constructorBody.addStatement(assignExpr);
        constructorDeclaration.setBody(constructorBody);
        return variableDeclarator;
    }

    private FieldDeclaration createField(ClassOrInterfaceDeclaration srcClass, ClassOrInterfaceDeclaration destClass) {
        String srcClassName = srcClass.getNameAsString();
        return destClass.addField(srcClassName, String.format("m%s", srcClassName)).
                addModifier(Modifier.PRIVATE, Modifier.FINAL);
    }

    private void createMethod(ClassOrInterfaceDeclaration classDeclaration, VariableDeclarator variableDeclarator, MethodDeclaration methodDeclaration) {
        String srcMethodName = methodDeclaration.getNameAsString();
        String destMethodName = srcMethodName;

        if (srcMethodName.startsWith("set")) {
            destMethodName = CodeUtils.unCapitalize(srcMethodName.substring(3));
        }

        MethodDeclaration addedMethod = methodDeclaration.clone();
        classDeclaration.addMember(addedMethod);
        addedMethod.setName(destMethodName)
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setType(classDeclaration.getName().asString())
                .setBody(new BlockStmt());

        NodeList<Parameter> parameters = addedMethod.getParameters();

        MethodCallExpr methodCallExpr = new MethodCallExpr(variableDeclarator.getNameAsExpression(), methodDeclaration.getNameAsString());
        parameters.forEach(parameter -> methodCallExpr.addArgument(parameter.getNameAsString()));
        methodCallExpr.setName(srcMethodName);

        addedMethod.getBody().ifPresent(blockStmt -> {
            blockStmt.addStatement(methodCallExpr);
            ReturnStmt returnStatement = new ReturnStmt();
            returnStatement.setExpression(new ThisExpr());
            blockStmt.addStatement(returnStatement);
        });
    }


}

