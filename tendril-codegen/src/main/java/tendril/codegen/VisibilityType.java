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

/**
 * Enumeration of the different visibilities that can be employed in the code
 */
public enum VisibilityType {
    /** public */
    PUBLIC("public"),
    /** private */
    PRIVATE("private"),
    /** package private (no explicit visibility) */
    PACKAGE_PRIVATE(""),
    /** protected */
    PROTECTED("protected");

    /** The representation of the visibility type as code */
    private final String code;

    /**
     * CTOR
     * 
     * @param code {@link String} how the visibility is represented in code
     */
    private VisibilityType(String code) {
        this.code = code;
    }

    /**
     * Converts the "visibility" to code
     */
    @Override
    public String toString() {
        return code;
    }
}
