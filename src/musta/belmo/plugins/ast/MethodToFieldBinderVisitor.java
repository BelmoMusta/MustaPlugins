package musta.belmo.plugins.ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodToFieldBinderVisitor extends LinkedHashMap<String, MethodFieldPair> {
    public MethodToFieldBinderVisitor(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
        final List<FieldDeclaration> fields = classOrInterfaceDeclaration.getFields();

        for (FieldDeclaration field : fields) {
            MethodFieldPair methodFieldPair = new MethodFieldPair();
            methodFieldPair.setField(field);
            for (MethodDeclaration method : methods) {
                if (fieldIsAssociatedToSetterMethod(field, method)) {
                    methodFieldPair.setSetterMethod(method);
                } else if (fieldIsAssociatedToGetterMethod(field, method)) {
                    methodFieldPair.setGetterMethod(method);
                }
                put(field.getVariables().get(0).getNameAsString(), methodFieldPair);
            }
        }
    }
    private boolean fieldIsAssociatedToGetterMethod(FieldDeclaration field, MethodDeclaration method) {
        if (!method.getParameters().isEmpty()) {
            return false;
        }
        boolean methodAssociatedWithField = isMethodAssociatedWithField(field, method);
        return methodAssociatedWithField && !method.isStatic() && method.getType().equals(field.getVariables().get(0).getType());
    }

    private boolean fieldIsAssociatedToSetterMethod(FieldDeclaration field, MethodDeclaration method) {
        if (method.getParameters().size()!=1) {
            return false;
        }
        boolean methodAssociatedWithField = isMethodAssociatedWithField(field, method);
        Parameter parameter = method.getParameter(0);
        boolean sameType = parameter.getType().equals(field.getVariable(0).getType());
        return methodAssociatedWithField && sameType && !method.isStatic() && method.getType().isVoidType();
    }

    private static boolean isMethodAssociatedWithField(FieldDeclaration field, MethodDeclaration method) {
        String fieldName = field.getVariable(0).getNameAsString();
        String methodName = method.getNameAsString();
        fieldName = CodeUtils.capitalize(fieldName);
        String fieldNameFromMethod = "";
        if (methodName.startsWith("is")) {
            fieldNameFromMethod = methodName.substring(2);
        }
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            fieldNameFromMethod = methodName.substring(3);
        }
        return fieldNameFromMethod.equals(fieldName);
    }


}
