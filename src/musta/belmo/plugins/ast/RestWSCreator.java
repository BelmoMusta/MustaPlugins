package musta.belmo.plugins.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;
import musta.belmo.plugins.action.WsParam;
import musta.belmo.plugins.action.WsSignature;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class RestWSCreator extends Transformer {
    @Override
    public CompilationUnit generate(CompilationUnit code) {
        final String wsSignature = JOptionPane.showInputDialog("Enter WS signature");
        return createWSSignature(code, wsSignature);

    }

    public CompilationUnit createWSSignature(CompilationUnit code_, String wsSignature) {
        CompilationUnit compilationUnit = Optional.ofNullable(code_)
                .map(CompilationUnit::clone)
                .orElse(new CompilationUnit());

        Optional<ClassOrInterfaceDeclaration> first = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class);
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

        classOrInterfaceDeclaration = first.orElseGet(() -> compilationUnit.addClass("EmptyClass"));
        WsSignature signature = WsSignature.createWsSignature(wsSignature);
        if (!classOrInterfaceDeclaration.isInterface()) {
            MethodDeclaration restMethod =
                    classOrInterfaceDeclaration.addMethod(signature.getPossibleMethodName(),
                            Modifier.PUBLIC);
            NormalAnnotationExpr normalAnnotationExpr = new NormalAnnotationExpr();
            normalAnnotationExpr.setName(StringUtils.capitalize(signature.getMethod().toLowerCase()) + "Mapping");
            normalAnnotationExpr.addPair("value", "\"" + signature.getUrl() + "\"");
            normalAnnotationExpr.addPair("produces", "\"application/json\"");
            restMethod.addAnnotation(normalAnnotationExpr);

            BlockStmt body = new BlockStmt();
            for (WsParam wsParam : signature.getWsParams()) {
                Parameter parameter = new Parameter();
                parameter.setType(wsParam.getType());
                parameter.setName(wsParam.getName());
                AnnotationExpr pathVariable = new MarkerAnnotationExpr();
                pathVariable.setName(wsParam.getAnnotation());
                parameter.addAnnotation(pathVariable);
                TypeParameter type = new TypeParameter();
                type.setName("ResponseEntity");
                restMethod.setType(type);
                restMethod.addParameter(parameter);
            }
            ReturnStmt returnStmt = new ReturnStmt();
            returnStmt.setExpression(new NullLiteralExpr());
            returnStmt.setComment(new BlockComment("TODO complete this method"));
            body.addStatement(returnStmt);
            restMethod.setBody(body);
        }
        return compilationUnit;
    }


}
