package org.spiderland.Psh;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class TestUtil {

    public static File getFileFromResource(final String fileName) {
        final ClassLoader classLoader = PushGpBenchmarkTest.class.getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File with name " + fileName + " was not found");
        } else {
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
