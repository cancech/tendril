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

import tendril.context.Engine;

/**
 * Helper file for finding and processing the runner metadata file, containing the recipe which is to create the application runner
 */
public class RunnerFile {
    /** Where the file is to be located */
    public static String PATH = "META-INF/tendril/runner";

    /**
     * Load the file and return its contents
     * 
     * @return {@link String} the contents of the file
     * @throws IOException if there are issues reading the file
     */
    public static String read() throws IOException {
        String runner = null;
        // TODO perform verification to ensure that exactly one runner was found
        Enumeration<URL> resEnum = Engine.class.getClassLoader().getResources(PATH);
        for (URL url : Collections.list(resEnum)) {
            try (InputStream ios = url.openStream(); InputStreamReader iosReader = new InputStreamReader(ios); BufferedReader reader = new BufferedReader(iosReader)) {
                for (String line = ""; line != null; line = reader.readLine()) {
                    line = line.trim();
                    if (!line.isEmpty())
                        runner = line;
                }
            }
        }

        return runner;
    }

    /**
     * CTOR - should only ever be used as a static class
     */
    private RunnerFile() {
    }
}
