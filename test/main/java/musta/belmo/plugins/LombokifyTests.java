package musta.belmo.plugins;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import musta.belmo.plugins.ast.LombokTransformer;
import musta.belmo.plugins.ast.MethodFieldPair;
import musta.belmo.plugins.ast.MethodToFieldBinderVisitor;
import musta.belmo.plugins.ast.Transformer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;


public class LombokifyTests {
    @Test
    public void testLombok() throws Exception {
//        final Transformer lombokTransformer = new LombokTransformer(Arrays.asList());
//        final String content = TestUtils.getContentFromFile("Bean.java");
//        final CompilationUnit compilationUnit = lombokTransformer.generate(content, -1);
//        final MethodToFieldBinderVisitor visitor =
//                new MethodToFieldBinderVisitor(compilationUnit.findAll(ClassOrInterfaceDeclaration.class).get(0));
//
//        int nbMethodsAssociatedToFields = 0;
//        for (Map.Entry<String, MethodFieldPair> keyValue : visitor.entrySet()) {
//            MethodFieldPair value = keyValue.getValue();
//            MethodDeclaration getterMethod = value.getGetterMethod();
//            MethodDeclaration setterMethod = value.getSetterMethod();
//            if (getterMethod != null) {
//                nbMethodsAssociatedToFields++;
//            }
//            if (setterMethod != null) {
//                nbMethodsAssociatedToFields++;
//            }
//        }
//
//        Assert.assertEquals(0, nbMethodsAssociatedToFields);
    }

    @Test
    public void testAssociateFieldsToMethods() throws Exception {
        final String content = TestUtils.getContentFromFile("Bean.java");
        ParseResult<CompilationUnit> parse = new JavaParser().parse(content);
        if (parse.getResult().isPresent()) {
            final CompilationUnit compilationUnit = parse.getResult().get();
            final MethodToFieldBinderVisitor visitor =
                    new MethodToFieldBinderVisitor(compilationUnit.findAll(ClassOrInterfaceDeclaration.class).get(0));
            System.out.println(visitor);
        }
    }


}
