/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import tendril.context.ApplicationContext;
import tendril.context.Engine;

/**
 * Class for loading and processing the tendril registry file on {@link ApplicationContext} start
 */
public class RegistryFile {

    /** The path where to find the registry file */
    public static String PATH = "META-INF/tendril/registry";

    /**
     * Reads the registry file and returns a list of all recipes that have been registered
     * 
     * @return {@link Set} of {@link String}s containing the registered recipes
     * @throws IOException if there is an issue opening the file
     */
    public static Set<String> read() throws IOException {
        Set<String> registers = new HashSet<>();

        Enumeration<URL> resEnum = Engine.class.getClassLoader().getResources(PATH);
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
    private RegistryFile() {
    }
}
