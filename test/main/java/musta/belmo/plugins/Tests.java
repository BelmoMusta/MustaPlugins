package musta.belmo.plugins;

import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.plugins.action.text.ToOptionalAction;
import musta.belmo.plugins.ast.ClassBuilder;
import musta.belmo.plugins.ast.FieldsFromGetters;
import musta.belmo.plugins.ast.JPAAnnotationsTransformer;
import musta.belmo.plugins.ast.RestWSCreator;
import musta.belmo.plugins.ast.Transformer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class Tests {


    @Test
    public void testJPAAnnotations() throws Exception {
        final Transformer jpaTransformer = new JPAAnnotationsTransformer();
        final String content = TestUtils.getContentFromFile("BeanWithJPAAnnotations.java");
        final CompilationUnit compilationUnit = jpaTransformer.generate(content, -1);
        System.out.println(compilationUnit);


    }

    @Test
    public void testBuilder() throws Exception {
        final Transformer jpaTransformer = new ClassBuilder();
        final String content = TestUtils.getContentFromFile("ASimpleClass.java");
        final CompilationUnit compilationUnit = jpaTransformer.generate(content, -1);
        System.out.println(compilationUnit);


    }

    @Test
    public void testFieldsFromGetters() throws Exception {
        final Transformer jpaTransformer = new FieldsFromGetters();
        final String content = getContentFromFile("ASimpleClass.java");
        final CompilationUnit compilationUnit = jpaTransformer.generate(content, -1);
        System.out.println(compilationUnit);


    }

    @Test
    public void optaionalWrapper() throws Exception {
        ToOptionalAction toOptionalAction = new ToOptionalAction();
        String falseOption = toOptionalAction.changeText("false");
        String strOptions = toOptionalAction.changeText("0");
        String chained = toOptionalAction.changeText("a.b().c().d().e()");

        Assert.assertEquals("Optional.of(false);", falseOption);
        Assert.assertEquals("Optional.of(0);", strOptions);
        Assert.assertEquals("Optional.of(0);", strOptions);
        Assert.assertEquals("Optional.ofNullable(a)\n" +
                "     .map(mapped -> mapped.b())\n" +
                "     .map(mapped -> mapped.c())\n" +
                "     .map(mapped -> mapped.d())\n" +
                "     .map(mapped -> mapped.e());", chained);
    }

    private String getContentFromFile(String fileName) throws URISyntaxException, IOException {
        final URL url = getClass().getClassLoader().getResource(fileName);
        final URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        return FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
    }

    @Test
    public void testCreateWsSignature() {
        RestWSCreator transformer = new RestWSCreator();

//        String signatureAsString = "GET /interne/instance/{idInstance}";
//        System.out.println(transformer.generate(null, signatureAsString));
//        signatureAsString = "POST /interne/instance/{idInstance}";
//        System.out.println(transformer.createWSSignature(null, signatureAsString));

    }
}
