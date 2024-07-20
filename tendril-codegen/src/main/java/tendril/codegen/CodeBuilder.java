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
package tendril.codegen;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;

/**
 * Builds up the code, by appending a single line of code at a time and allowing for proper indentation to be applied.
 */
public class CodeBuilder {
	/** Builder which will actually build up the String/code */
    private final TextStringBuilder builder = new TextStringBuilder();
    /** The indentation level of the code */
    private int numIndents = 0;

    /**
     * CTOR
     */
    public CodeBuilder() {
    }
    
    /**
     * Indent the code, such that subsequent lines are one additional "tab" indented
     */
    public void indent() {
        numIndents++;
    }
    
    /**
     * Decrease the indentation by one level, with subsequent lines being brought in by one "tab".
     * Cannot deindent beyond the start of the line.
     */
    public void deIndent() {
        if(numIndents == 0)
            return;
        numIndents--;
    }
    
    /**
     * Append a line to the code
     * 
     * @param line {@link String} to append
     */
    public void append(String line) {
        builder.append(StringUtils.repeat(' ', numIndents * 4));
        builder.appendln(line);
    }
    
    /**
     * Insert a blank line into the code
     */
    public void blankLine() {
        builder.appendln("");
    }

    /**
     * Get the full code that has been generated
     * 
     * @return {@link String}
     */
    public String get() {
        return builder.get();
    }
}
