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

import java.util.List;
import java.util.Set;

import tendril.codegen.field.type.ClassType;

/**
 * Representation of a Constructor for a class.
 */
public class JConstructor extends JAbstractMethodElement<ClassType> {

    /**
     * Create the CTOR
     * 
     * @param enclosingClass {@link ClassType} that the CTOR belongs to
     * @param implementation {@link List} of {@link String}s being the code the CTOR executes
     */
    public JConstructor(ClassType enclosingClass, List<String> implementation) {
        super(enclosingClass, enclosingClass.getSimpleName(), implementation);
    }
    
    /**
     * @see tendril.codegen.field.JVisibleType#setStatic(boolean)
     * 
     * Override to ensure that a static constructor is not defined.
     */
    @Override
    public void setStatic(boolean isStatic) {
        if (isStatic)
            throw new IllegalArgumentException("CTOR cannot be static");
        
        super.setStatic(isStatic);
    }
    
    /**
     * @see tendril.codegen.field.JVisibleType#setStatic(boolean)
     * 
     * Override to ensure that a final constructor is not defined.
     */
    @Override
    public void setFinal(boolean isFinal) {
        if (isFinal)
            throw new IllegalArgumentException("CTOR cannot be final");
        
        super.setStatic(isFinal);
    }

    /**
     * @see tendril.codegen.classes.method.JAbstractMethodElement#generateSignature(java.util.Set, boolean)
     */
    @Override
    protected String generateSignature(Set<ClassType> classImports, boolean hasImplementation) {
        // A CTOR without implementation is not possible
        if (!hasImplementation)
            throw new IllegalArgumentException("Constructor must have a valid implementation");
        
        return visibility.getKeyword() + getGenericsDefinitionKeyword(false) + getName() + "(" + generateParameters(classImports) + ") {";
    }
}
