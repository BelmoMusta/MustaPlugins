package musta.belmo.plugins.ast;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodFieldPair {
    MethodDeclaration setterMethod;
    MethodDeclaration getterMethod;
    FieldDeclaration field;

    public MethodDeclaration getSetterMethod() {
        return setterMethod;
    }
    public void setSetterMethod(MethodDeclaration setterMethod) {
        this.setterMethod = setterMethod;
    }
    public FieldDeclaration getField() {
        return field;
    }
    public void setField(FieldDeclaration field) {
        this.field = field;
    }
    public MethodDeclaration getGetterMethod() {
        return getterMethod;
    }
    public void setGetterMethod(MethodDeclaration getterMethod) {
        this.getterMethod = getterMethod;
    }
}
