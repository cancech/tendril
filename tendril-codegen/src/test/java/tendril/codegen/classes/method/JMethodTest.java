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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.NamedType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import test.assertions.CollectionAssert;

/**
 * Test case for {@link JMethod}
 */
public class JMethodTest extends SharedJMethodTest {

    private static final String GENERATED_TEXT = "Generated_Method_Start ";

    /**
     * Concrete implementation to employ for testing
     */
    private class TestJMethod extends JMethod<Type> {
        /** Counter for the number of times that generateSignatureStart was called */
        private int timesGenerateCalled = 0;
        /** The last hasImplementation that was passed to generateSignatureStart */
        private boolean hasImplementation = false;

        /**
         * CTOR
         * 
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestJMethod(List<String> implementation) {
            super(mockVisibility, mockReturnType, "mockMethodName", implementation);
        }

        /**
         * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
         */
        @Override
        protected String generateSignatureStart(boolean hasImplementation) {
            timesGenerateCalled++;
            this.hasImplementation = hasImplementation;
            return GENERATED_TEXT;
        }

        /**
         * Verify that generateSignatureStart was called appropriately
         * 
         * @param expectedTimes             int times expected to have been called
         * @param expectedHasImplementation boolean the expected hasImplementation from the last call
         */
        private void verifyGenerateCalled(int expectedTimes, boolean expectedHasImplementation) {
            Assertions.assertEquals(expectedTimes, timesGenerateCalled);
            Assertions.assertEquals(expectedHasImplementation, hasImplementation);
        }
    }

    // Mocks to use for testing
    @Mock
    private NamedType<Type> mockParam1;
    @Mock
    private Type mockParam1Type;
    @Mock
    private NamedType<Type> mockParam2;
    @Mock
    private Type mockParam2Type;
    @Mock
    private NamedType<Type> mockParam3;
    @Mock
    private Type mockParam3Type;

    // Instance to use for testing
    private TestJMethod method;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();

        lenient().when(mockParam1.getName()).thenReturn("mockParam1");
        lenient().when(mockParam2.getName()).thenReturn("mockParam2");
        lenient().when(mockParam3.getName()).thenReturn("mockParam3");

        lenient().when(mockParam1.getType()).thenReturn(mockParam1Type);
        lenient().when(mockParam2.getType()).thenReturn(mockParam2Type);
        lenient().when(mockParam3.getType()).thenReturn(mockParam3Type);

        lenient().when(mockParam1Type.getSimpleName()).thenReturn("Type1");
        lenient().when(mockParam2Type.getSimpleName()).thenReturn("Type2");
        lenient().when(mockParam3Type.getSimpleName()).thenReturn("Type3");
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     * @param code       {@link List} of {@link String} lines of code to use for the method implementation
     * @param parameters {@link List} of {@link NamedType}s that are be used as parameters for the method
     */
    private void initMethod(List<String> code, List<NamedType<?>> parameters) {
        method = new TestJMethod(code);
        for (NamedType<?> t : parameters)
            method.addParameter(t);

        verifyMethodInit("mockMethodName", method);
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and no parameters.
     */
    @Test
    public void testNoImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName();");

        // Populate the method details
        initMethod(null, Collections.emptyList());
        CollectionAssert.assertEmpty(method.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and a single parameter.
     */
    @Test
    public void testNoImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1);");

        // Populate the method details
        initMethod(null, Collections.singletonList(mockParam1));
        Assertions.assertIterableEquals(Collections.singleton(mockParam1), method.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and several parameters.
     */
    @Test
    public void testNoImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3);");

        // Populate the method details
        initMethod(null, Arrays.asList(mockParam1, mockParam2, mockParam3));
        Assertions.assertIterableEquals(Arrays.asList(mockParam1, mockParam2, mockParam3), method.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and no parameters.
     */
    @Test
    public void testEmptyImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName() {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and a single parameter.
     */
    @Test
    public void testEmptyImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and several parameters.
     */
    @Test
    public void testEmptyImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and no parameters.
     */
    @Test
    public void testSomeImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName() {");
        matcher.eq("    qwerty");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.singletonList("qwerty"), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and a single parameter.
     */
    @Test
    public void testSomeImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1) {");
        matcher.eq("    qwerty");
        matcher.eq("    asdfg");
        matcher.eq("    zxcvb");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "asdfg", "zxcvb"), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and several parameters.
     */
    @Test
    public void testSomeImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3) {");
        matcher.eq("    qwerty");
        matcher.eq("    asdfg");
        matcher.eq("    zxcvb");
        matcher.eq("    hjhio");
        matcher.eq("    12345");
        matcher.eq("    inlknfginsdr934");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "asdfg", "zxcvb", "hjhio", "12345", "inlknfginsdr934"), Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Generate the code and verify that it matches expectations
     * 
     * @param expectedHasImplementation boolean true if generateSignatureStart is expected to be called with an implementation
     */
    private void generateAndVerifyCode(boolean expectedHasImplementation) {
        Set<ClassType> imports = new HashSet<ClassType>();
        method.generate(builder, imports);
        method.verifyGenerateCalled(1, expectedHasImplementation);
        verify(mockReturnType).registerImport(imports);
        verify(mockReturnType).getSimpleName();

        matcher.match(builder.get());
    }
}
