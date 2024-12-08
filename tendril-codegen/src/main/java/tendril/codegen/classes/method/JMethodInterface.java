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

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;

/**
 * Representation of a method that appears in an interface. By default these methods will be public
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
class JMethodInterface<RETURN_TYPE extends Type> extends JMethod<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    JMethodInterface(RETURN_TYPE returnType, String name, List<String> implementation) {
        super(returnType, name, implementation);
        setVisibility(VisibilityType.PUBLIC);
    }
    
    /**
     * @see tendril.codegen.field.JVisibleType#setVisibility(tendril.codegen.VisibilityType)
     * 
     * Override to prevent anything other than public or private
     */
    @Override
    public void setVisibility(VisibilityType visibility) {
        if(visibility != VisibilityType.PUBLIC && visibility != VisibilityType.PRIVATE)
            throw new IllegalArgumentException("Interface methods can only be public or private");
        
        super.setVisibility(visibility);
    }
    
    /**
     * @see tendril.codegen.JBase#setFinal(boolean)
     * 
     * Override to prevent making the method final
     */
    @Override
    public void setFinal(boolean isFinal) {
        if (isFinal)
            throw new IllegalArgumentException("Interface methods cannot be final");
        
        super.setFinal(isFinal);
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
     */
    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        if (isStatic() && !hasImplementation)
            throw new IllegalArgumentException("Static interface methods must have an implementation");
        
        if (VisibilityType.PUBLIC == visibility) {
            if (hasImplementation)
                return isStatic() ? getStaticKeyword() : "default ";

            return "";
        }
        
        if (!hasImplementation)
            throw new IllegalArgumentException("Private interface methods must have an implementation");
        
        return visibility.getKeyword() + getStaticKeyword();
    }

}
