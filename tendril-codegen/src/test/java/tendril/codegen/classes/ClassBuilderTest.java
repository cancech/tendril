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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ClassBuilder}
 */
public class ClassBuilderTest extends AbstractUnitTest {
    
    /**
     * Test implementation of the {@link ClassBuilder} so that it can be tested
     */
    private class TestClassBuilder extends ClassBuilder {

        /**
         * CTOR
         */
        public TestClassBuilder() {
            super(mockClassType);
        }

        /**
         * @see tendril.codegen.classes.ClassBuilder#createMethodBuilder(java.lang.String)
         */
        @SuppressWarnings("unchecked")
        @Override
        protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(String name) {
            return (MethodBuilder<RETURN_TYPE>) mockMethodBuilder;
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JClass create() {
            return mockClass;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private ClassType mockMethodClassType;
    @Mock
    private MethodBuilder<Type> mockMethodBuilder;
    @Mock
    private JClass mockClass;
    @Mock
    private JMethod<?> mockMethod1;
    @Mock
    private JMethod<?> mockMethod2;
    @Mock
    private JMethod<?> mockMethod3;
    
    // Instance to test
    private TestClassBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getSimpleName()).thenReturn("MockClass");
        builder = new TestClassBuilder();
        verify(mockClassType).getSimpleName();
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateVoidMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod("voidMethod"));
        verify(mockMethodBuilder).setType(VoidType.INSTANCE);
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreatePrimitiveMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(PrimitiveType.CHAR, "primitiveMethod"));
        verify(mockMethodBuilder).setType(PrimitiveType.CHAR);
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateClassMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(PrimitiveType.class, "classMethod"));
        verify(mockMethodBuilder).setType(new ClassType(PrimitiveType.class));
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateClassTypeMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(mockMethodClassType, "classMethod"));
        verify(mockMethodBuilder).setType(mockMethodClassType);
    }
    
    /**
     * Verify that class details are applied when no methods are added
     */
    @Test
    public void testApplyDetailsNoMethod() {
        builder.applyDetails(mockClass);
        verify(mockClass).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockClass).setFinal(false);
        verify(mockClass).setStatic(false);
    }
    
    /**
     * Verify that class details are applied when methods are added
     */
    @Test
    public void testApplyDetailsWithMethods() {
        builder.addMethod(mockMethod1);
        builder.addMethod(mockMethod2);
        builder.addMethod(mockMethod3);
        
        builder.applyDetails(mockClass);

        verify(mockClass).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockClass).setFinal(false);
        verify(mockClass).setStatic(false);
        verify(mockClass).addMethod(mockMethod1);
        verify(mockClass).addMethod(mockMethod2);
        verify(mockClass).addMethod(mockMethod3);
    }

}
