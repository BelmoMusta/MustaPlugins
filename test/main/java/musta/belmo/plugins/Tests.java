package musta.belmo.plugins;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import musta.belmo.plugins.ast.JPAAnnotationsTransformer;
import musta.belmo.plugins.ast.LombokTransformer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;


public class Tests {
    @Test
    public void testLombok() throws Exception {
        final LombokTransformer instance = LombokTransformer.getInstance();
        final URL url = getClass().getClassLoader().getResource("Bean.java");
        final URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        final String content = FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
        final CompilationUnit compilationUnit = instance.generate(content);
        final List<MethodDeclaration> gettersAndSetters = compilationUnit.findAll(MethodDeclaration.class,
                aMethod -> aMethod.getNameAsString().matches("[sg]et[A-Z$_]([A-Za-z0-9_$])*"));
        Assert.assertTrue(gettersAndSetters.isEmpty());
    }

    @Test
    public void testJPAAnnotations() throws Exception {
        JPAAnnotationsTransformer instance = JPAAnnotationsTransformer.getInstance();
        URL url = getClass().getClassLoader().getResource("BeanWithJPAAnnotations.java");
        URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        String content = FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
        CompilationUnit compilationUnit = instance.generate(content);
        System.out.println(compilationUnit);


    }
}
