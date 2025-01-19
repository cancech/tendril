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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.ConcreteMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link ConcreteClassBuilder}
 */
public class ConcreteClassBuilderTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    
    // Instance to test
    private ConcreteClassBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getGenerics()).thenReturn(Collections.emptyList());
        when(mockClassType.getSimpleName()).thenReturn("MockClass");
        builder = new ConcreteClassBuilder(mockClassType);
        verify(mockClassType).getSimpleName();
        verify(mockClassType).getGenerics();
    }
    
    /**
     * Verify that the correct method builder is created
     */
    @Test
    public void testCreatMethodBuilder() {
        ClassAssert.assertInstance(ConcreteMethodBuilder.class, builder.createMethodBuilder("method"));
    }
    
    /**
     * Verify that validation properly verifies the details
     */
    @Test
    public void testValidate() {
        for (VisibilityType type: VisibilityType.values()) {
            builder.setVisibility(type);
            if (type == VisibilityType.PRIVATE)
                Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validate());
            else
                Assertions.assertDoesNotThrow(() -> builder.validate());
        }
    }
    
    /**
     * Verify that create produces the correct class type
     */
    @Test
    public void testCreate() {
        when(mockClassType.getPackageName()).thenReturn("package");
        when(mockClassType.getClassName()).thenReturn("ClassName");
        ClassAssert.assertInstance(JClassDefault.class, builder.create());
        verify(mockClassType).getClassName();
        verify(mockClassType).getPackageName();
    }

}
