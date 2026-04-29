package tendril.processor.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import tendril.context.Engine;

/**
 * Helper containing shared reusable methods and features for managing registry files
 */
class RegistryFileHelper {

    /**
     * Reads the registry file and returns a list of all recipes that have been registered
     * 
     * @return {@link Set} of {@link String}s containing the registered recipes
     * @throws IOException if there is an issue opening the file
     */
    static Set<String> read(String filePath) throws IOException {
        Set<String> registers = new HashSet<>();

        Enumeration<URL> resEnum = Engine.class.getClassLoader().getResources(filePath);
        for (URL url : Collections.list(resEnum)) {
            try (InputStream ios = url.openStream(); InputStreamReader iosReader = new InputStreamReader(ios); BufferedReader reader = new BufferedReader(iosReader)) {
                for (String line = ""; line != null; line = reader.readLine()) {
                    line = line.trim();
                    if (!line.isEmpty())
                        registers.add(line);
                }
            }
        }

        return registers;
    }

    /**
     * CTOR - should only ever be used as a static class
     */
    private RegistryFileHelper() {
    }
	
}
