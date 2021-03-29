package musta.belmo.plugins;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import musta.belmo.plugins.ast.ClassBuilder;
import musta.belmo.plugins.ast.JPAAnnotationsTransformer;
import musta.belmo.plugins.ast.LombokTransformer;
import musta.belmo.plugins.ast.Transformer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;


public class Tests {
    @Test
    public void testLombok() throws Exception {
        final Transformer lombokTransformer = new LombokTransformer();
        final String content = getContentFromFile("Bean.java");
        final CompilationUnit compilationUnit = lombokTransformer.generate(content);
        final List<MethodDeclaration> gettersAndSetters = compilationUnit.findAll(MethodDeclaration.class,
                aMethod -> aMethod.getNameAsString().matches("[sg]et[A-Z$_]([A-Za-z0-9_$])*"));
        Assert.assertTrue(gettersAndSetters.isEmpty());
    }

    @Test
    public void testJPAAnnotations() throws Exception {
        final Transformer jpaTransformer = new JPAAnnotationsTransformer();
        final String content = getContentFromFile("BeanWithJPAAnnotations.java");
        final CompilationUnit compilationUnit = jpaTransformer.generate(content);
        System.out.println(compilationUnit);


    }
    @Test
    public void testBuilder() throws Exception {
        final Transformer jpaTransformer = new ClassBuilder();
        final String content = getContentFromFile("ASimpleClass.java");
        final CompilationUnit compilationUnit = jpaTransformer.generate(content);
        System.out.println(compilationUnit);


    }

    private String getContentFromFile(String fileName) throws URISyntaxException, IOException {
        final URL url = getClass().getClassLoader().getResource(fileName);
        final URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        return FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
    }
}
