package musta.belmo.plugins.ast;

import com.github.javaparser.JavaParser;
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

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.stream.Stream;

/**
 * Created by DELL on 12/03/2018.
 */
public class ClassBuilder extends Transformer {

    @Override
    public CompilationUnit generate(CompilationUnit code) {
        return buildFromClass(code);
    }


    public CompilationUnit buildFormFile(File f) throws IOException {
        return buildFromClass(JavaParser.parse(f));
    }

    public CompilationUnit buildFormFile(String code) {
        return buildFromClass(JavaParser.parse(code));
    }

    private CompilationUnit buildFromClass(CompilationUnit compilationUnit) {
        final CompilationUnit resultUnit = new CompilationUnit();

        setupPackageandImports(compilationUnit, resultUnit);
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(srcClass ->
                        !srcClass.isAbstract()
                                && !srcClass.isInterface()
                                && !srcClass.isInnerClass()).
                forEach(classDef -> {
                    final ClassOrInterfaceDeclaration destClass = resultUnit.addClass(String.format("%sBuilder", classDef.getNameAsString()));
                    VariableDeclarator variableDeclarator = createConstructor(classDef, destClass);
                    createMethods(classDef, destClass, variableDeclarator);
                    createBuildMethod(classDef, destClass, variableDeclarator);
                });
        return resultUnit;
    }

    private void setupPackageandImports(CompilationUnit compilationUnit, CompilationUnit resultUnit) {
        resultUnit.setPackageDeclaration(compilationUnit.getPackageDeclaration()
                .map(pkg -> pkg.clone().setName(pkg.getNameAsString() + ".builder"))
                .orElse(null));
        resultUnit.getImports().addAll(compilationUnit.getImports());
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
                        !methodDeclaration.getName().asString().startsWith("get")
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

