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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ConstructorBuilder}
 */
public class ConstructorBuilderTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private ClassType mockEnclosingClass;
    @Mock
    private ClassType mockOTherType;
    @Mock
    private JConstructor mockConstructor;
    
    // Instance to test
    private ConstructorBuilder builder;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockEnclosingClass.getSimpleName()).thenReturn("MockClass");
        builder = new ConstructorBuilder(mockClassBuilder, mockEnclosingClass);
        verify(mockEnclosingClass).getSimpleName();
    }
    
    /**
     * Verify that cannot change various the type of the constructor
     */
    @Test
    public void testSetType() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.setType(mockEnclosingClass));
        verify(mockEnclosingClass).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.setType(mockOTherType));
        verify(mockEnclosingClass, times(2)).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.setType(null));
        verify(mockEnclosingClass, times(3)).getFullyQualifiedName();
    }
    
    /**
     * Verify that validation fails if attributes that cannot be applied to the constructor are set
     */
    @Test
    public void testValidate() {
        // Must have an implementation
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockEnclosingClass).getFullyQualifiedName();
        builder.emptyImplementation();
        builder.validate();
        builder.addCode("a", "b", "c", "d", "e");
        builder.validate();
        
        // Cannot be static
        builder.setStatic(true);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockEnclosingClass, times(2)).getFullyQualifiedName();
        builder.setStatic(false);
        builder.validate();
        
        // Cannot be final
        builder.setFinal(true);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockEnclosingClass, times(3)).getFullyQualifiedName();
        builder.setFinal(false);
        builder.validate();
    }
    
    /**
     * Verify that the constructor is properly added to the class.
     */
    @Test
    public void testAddToClass() {
        builder.addToClass(mockClassBuilder, mockConstructor);
        verify(mockClassBuilder).add(mockConstructor);
    }
    
    /**
     * Verify that the constructor is properly created
     */
    @Test
    public void testCreate() {
        JConstructor actual = builder.create();
        Assertions.assertEquals("MockClass", actual.getName());
    }
}
