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
package tendril.codegen.classes.method;

import java.util.HashSet;

import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Representation of a method that appears in an annotation
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class JMethodAnnotation<RETURN_TYPE extends Type> extends JMethodInterface<RETURN_TYPE> {

    /** The default value to apply to the annotation attribute */
    private final JValue<RETURN_TYPE, ?> defaultValue;

    /**
     * CTOR
     * 
     * @param returnType   RETURN_TYPE representing what the method returns
     * @param name         {@link String} the name of the method
     * @param defaultValue {@link JValue} for the attribute (null if no default value is to be applied)
     */
    public JMethodAnnotation(RETURN_TYPE returnType, String name, JValue<RETURN_TYPE, ?> defaultValue) {
        super(returnType, name, null);
        this.defaultValue = defaultValue;
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureEnd(boolean)
     */
    @Override
    protected String generateSignatureEnd(boolean hasImplementation) {
        if (defaultValue == null)
            return ";";

        return " default " + defaultValue.generate(new HashSet<>()) + ";";
    }

}
