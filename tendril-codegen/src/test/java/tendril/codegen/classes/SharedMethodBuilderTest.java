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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Class which contains the shared elements of the specific method builders
 */
public abstract class SharedMethodBuilderTest<T extends MethodBuilder<Type>> extends AbstractUnitTest {
    /** The name of the method to be used for testing */
    protected static final String METHOD_NAME = "myMethodName";

    // Mocks to use for testing
    @Mock
    protected ClassBuilder mockClassBuilder;
    @Mock
    protected ClassType mockEnclosingClassType;
    @Mock
    protected Type mockReturnType;
    @Mock
    protected VisibilityType mockVisibilityType;

    // Instance to test
    protected MethodBuilder<Type> builder;
    
    private int timesValidateExceptionThrown;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        timesValidateExceptionThrown = 0;
        
        lenient().when(mockClassBuilder.getType()).thenReturn(mockEnclosingClassType);
        builder = createBuilder();
        builder.setType(mockReturnType);
    }
    
    protected abstract MethodBuilder<Type> createBuilder();

    /**
     * Apply the visibility and ensure that no exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     */
    protected void verifyValidateDoesNotThrow(VisibilityType visibility) {
        builder.setVisibility(visibility);
        Assertions.assertDoesNotThrow(() -> builder.validate());
    }

    /**
     * Apply the visibility and ensure that the exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     * @param retrievesType boolean true if the type is retrieved when generating the exception
     */
    protected void verifyValidateDoesThrow(VisibilityType visibility, boolean retrievesType) {
        builder.setVisibility(visibility);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockEnclosingClassType, times(++timesValidateExceptionThrown)).getFullyQualifiedName();
        if (retrievesType)
            verify(mockClassBuilder, times(timesValidateExceptionThrown)).getType();
    }

    /**
     * Verify that the buildMethod() produces the appropriate {@link JMethod} type
     * 
     * @param expectedClass {@link Class} extending {@link JMethod} that is expected to be built
     */
    protected void verifyBuildMethodType(@SuppressWarnings("rawtypes") Class<? extends JMethod> expectedClass) {
        try {
            // Use reflection to trigger the method call
            Method m = builder.getClass().getDeclaredMethod("create");
            m.setAccessible(true);
            @SuppressWarnings("unchecked")
            JMethod<Type> method = (JMethod<Type>) m.invoke(builder);
            
            ClassAssert.assertInstance(expectedClass, method);
            Assertions.assertEquals(mockReturnType, method.getType());
            Assertions.assertEquals(METHOD_NAME, method.getName());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
