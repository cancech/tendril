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

import tendril.codegen.classes.method.AnnotationMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Builder for the creation/definition of annotation classes
 */
class AnnotationBuilder extends InterfaceBuilder {

    /**
     * CTOR
     * 
     * @param type {@link ClassType} which has the basic class description included
     */
    AnnotationBuilder(ClassType type) {
        super(type);
    }

    /**
     * @see tendril.codegen.classes.InterfaceBuilder#createMethodBuilder(java.lang.String)
     */
    @Override
    protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(String name) {
        return new AnnotationMethodBuilder<RETURN_TYPE>(this, name);
    }

    /**
     * @see tendril.codegen.classes.InterfaceBuilder#create()
     */
    @Override
    protected JClass create() {
        return new JClassAnnotation(type);
    }
}
