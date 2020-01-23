package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
public class FieldsFromGettersVisitor extends VoidVisitorAdapter<CompilationUnit> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration aClass, CompilationUnit compilationUnit) {
        aClass.setInterface(false);
        aClass.setName(aClass.getNameAsString() + "Impl");
        List<MethodDeclaration> aClassAll = aClass.findAll(MethodDeclaration.class);


        CodeUtils.reversedStream(aClassAll
                .stream())
                .forEach(method -> {

                    method.setPublic(true);
                    BlockStmt blockStmt = new BlockStmt();
                    method.setBody(blockStmt);
                    String methodName;

                    if (CodeUtils.IS_GETTER.test(method)) {
                        methodName = method.getName().toString().substring(3);
                        FieldDeclaration fieldDeclaration = CodeUtils.newField(method.getType(), "a" + methodName, Modifier.PRIVATE);
                        aClass.getMembers().add(0, fieldDeclaration);
                        ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                        blockStmt.addStatement(returnStmt);
                        aClass.getMethodsByName("set" + methodName)
                                .forEach(setterMethod -> {
                                    BlockStmt setterBlockStmt = new BlockStmt();
                                    Expression assignStmt = new AssignExpr(fieldDeclaration.getVariables().get(0).getNameAsExpression(), setterMethod.getParameter(0).getNameAsExpression(), AssignExpr.Operator.ASSIGN);
                                    setterBlockStmt.addStatement(assignStmt);
                                    setterMethod.setBody(setterBlockStmt);
                                });

                    } else if (CodeUtils.IS_BOOLEAN_ACCESSOR.test(method)) {

                        methodName = method.getNameAsString().substring(2);
                        FieldDeclaration fieldDeclaration = CodeUtils.newField(method.getType(), "a" + methodName, Modifier.PRIVATE);
                        ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                        blockStmt.addStatement(returnStmt);
                    } else {
                        ThrowStmt throwExpression = new ThrowStmt();
                        ObjectCreationExpr expression = new ObjectCreationExpr();
                        expression.setType("UnsupportedOperationException");
                        throwExpression.setExpression(expression);
                        blockStmt.addStatement(throwExpression);
                    }

                });

        super.visit(aClass, compilationUnit);
    }
}
