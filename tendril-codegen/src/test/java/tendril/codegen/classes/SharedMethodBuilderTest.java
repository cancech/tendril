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

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Class which contains the shared elements of the specific method builders
 */
public abstract class SharedMethodBuilderTest<T extends MethodBuilder<Type>> extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    protected JClass mockClass;
    @Mock
    protected Type mockReturnType;
    @Mock
    protected VisibilityType mockVisibilityType;

    // Instance to test
    protected MethodBuilder<Type> builder;

    /**
     * Apply the visibility and ensure that no exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     */
    protected void verifyValidateDoesNotThrow(VisibilityType visibility) {
        builder.setVisibility(visibility);
        Assertions.assertDoesNotThrow(() -> builder.validateData());
    }

    /**
     * Apply the visibility and ensure that the exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     */
    protected void verifyValidateDoesThrow(VisibilityType visibility) {
        builder.setVisibility(visibility);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validateData());
    }

    /**
     * Verify that the buildMethod() produces the appropriate {@link JMethod} type
     * 
     * @param expectedClass {@link Class} extending {@link JMethod} that is expected to be built
     */
    protected void verifyBuildMethodType(@SuppressWarnings("rawtypes") Class<? extends JMethod> expectedClass) {
        JMethod<Type> method = builder.buildMethod(mockReturnType, "myMethodName");
        ClassAssert.assertInstance(expectedClass, method);
        Assertions.assertEquals(mockReturnType, method.getType());
        Assertions.assertEquals("myMethodName", method.getName());
    }
}
