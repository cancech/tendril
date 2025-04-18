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
package tendril.codegen.classes;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.TypeBuilder;
import tendril.codegen.field.type.Type;

/**
 * Used to build parameters, allowing for their possibilities to be accounted for in a relatively straightforward manner. Error checking is performed to ensure that the parameter is properly defined,
 * such that it can be considered valid for the encompassing method.
 * 
 * Note, if a valid {@link MethodBuilder} is provided, parameters can only be created via {@code finish()} and are automatically added to the encompassing method. Note, if an invalid
 * {@link MethodBuilder} is provided (null), parameters can only be created via {@code build()}.
 * 
 * @param <DATA_TYPE> extends {@link Type} indicating what the type of the parameter is
 * @param <BUILDER>   indicating the type of builder that is to be employed with the {@link ParameterBuilder}
 */
public class ParameterBuilder<DATA_TYPE extends Type, BUILDER> extends TypeBuilder<DATA_TYPE, JParameter<DATA_TYPE>, ParameterBuilder<DATA_TYPE, BUILDER>> {

    private final ParameterizedElementBuilder<BUILDER> elementBuilder;

    /**
     * CTOR - for use when creating an arbitrary parameter
     * 
     * @param type DATA_TYPE indicating the type of the parameter
     * @param name {@link String} of the parameter
     */
    public ParameterBuilder(DATA_TYPE type, String name) {
        this(null, type, name);
    }

    /**
     * CTOR - for use when creating parameters nested within a method being defined
     * 
     * @param elementBuilder {@link ParameterizedElementBuilder} building the element to which the parameter belongs
     * @param type           DATA_TYPE indicating the type of the parameter
     * @param name           {@link String} the name of the method
     */
    public ParameterBuilder(ParameterizedElementBuilder<BUILDER> elementBuilder, DATA_TYPE type, String name) {
        super(name);
        this.elementBuilder = elementBuilder;
        setType(type);
    }

    /**
     * @see tendril.codegen.field.TypeBuilder#validate()
     */
    @Override
    protected void validate() {
        super.validate();

        if (type.isVoid())
            throw new DefinitionException(type, "Parameter " + name + " cannot be void");
    }

    /**
     * @see tendril.codegen.BaseBuilder#build()
     */
    @Override
    public JParameter<DATA_TYPE> build() {
        if (elementBuilder != null)
            throw new IllegalStateException(
                    "Cannot be built directly - MethodBuilder for encompassing method provided. Use finish() instead to build the parameter and add it to the encompassing method");

        return super.build();
    }

    /**
     * Finish specifying the details of the method, build it, and apply it to the target class
     * 
     * @return {@link ClassBuilder} to which the method is applied
     */
    public BUILDER finish() {
        if (elementBuilder == null)
            throw new IllegalStateException("No MethodBuilder for encompassing method provided. Use build() to build the parameter without adding it to an encompassing method");

        return elementBuilder.addParameter(super.build());
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JParameter<DATA_TYPE> create() {
        return new JParameter<DATA_TYPE>(type, name);
    }

}
