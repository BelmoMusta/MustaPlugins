package musta.belmo.plugins;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TestUtils {

    public static String getContentFromFile(String fileName) throws URISyntaxException, IOException {
        final URL url = TestUtils.class.getClassLoader().getResource(fileName);
        final URI u = new URI(url.getFile().trim().replaceAll("\\u0020", "%20"));
        return FileUtils.readFileToString(new File(u.getPath()), "UTF-8");
    }
}
