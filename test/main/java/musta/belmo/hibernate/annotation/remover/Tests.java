package musta.belmo.hibernate.annotation.remover;

import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;


public class Tests {
    @Test
    public void testLombok()  throws Exception{
        LombokTransformer instance = LombokTransformer.getInstance();
        URL url = getClass().getClassLoader().getResource("Bean.java");
        URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        String content = FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
        CompilationUnit compilationUnit = instance.generate(content);
        System.out.println(compilationUnit);


    }
    @Test
    public void testHibernate()  throws Exception{
        HibernateAnnotationsTransformer instance = HibernateAnnotationsTransformer.getInstance();
        URL url = getClass().getClassLoader().getResource("BeanWithJPAAnnotations.java");
        URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        String content = FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
        CompilationUnit compilationUnit = instance.generate(content);
        System.out.println(compilationUnit);


    }
}
