package org.spiderland.Psh;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;

public class TestUtil {

    @SneakyThrows
    public static File getFileFromResource(final String fileName) {
        final ClassLoader classLoader = PushGpBenchmarkTest.class.getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File with name " + fileName + " was not found");
        } else {
            return new File(resource.toURI());
        }
    }
}
