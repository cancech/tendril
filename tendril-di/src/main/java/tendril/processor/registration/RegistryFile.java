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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tendril.context.Engine;

/**
 * 
 */
public class RegistryFile {

    public static String PATH = "META-INF/tendril/registry";

    public static void write(OutputStream out, List<String> registers) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
        for (String registry : registers) {
            writer.write(registry);
            writer.newLine();
        }
        writer.flush();
    }

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
}
