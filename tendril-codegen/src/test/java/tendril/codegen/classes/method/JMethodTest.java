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
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.classes.JParameter;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.assertions.CollectionAssert;

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
         * @param name {@link String} the name of the method
         */
        protected TestJMethod(String name) {
            this(mockReturnType, name);
        }

        /**
         * CTOR
         * 
         * @param returnType {@link Type} the method is to return
         * @param name       {@link String} the name of the method
         */
        protected TestJMethod(Type returnType, String name) {
            this(returnType, name, null);
        }

        /**
         * CTOR
         * 
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestJMethod(List<String> implementation) {
            this(mockReturnType, "mockMethodName", implementation);
        }

        /**
         * CTOR
         * 
         * @param returnType     {@link Type} the method is to return
         * @param name           {@link String} the name of the method
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestJMethod(Type returnType, String name, List<String> implementation) {
            super(returnType, name, implementation);
            setVisibility(mockVisibility);
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
    private JParameter<Type> mockParam1;
    @Mock
    private Type mockParam1Type;
    @Mock
    private JParameter<Type> mockParam2;
    @Mock
    private Type mockParam2Type;
    @Mock
    private JParameter<Type> mockParam3;
    @Mock
    private Type mockParam3Type;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private Type mockOtherReturnType;

    // Instance to use for testing
    private TestJMethod method;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();

        lenient().when(mockParam1.generateSelf(mockImports)).thenReturn("mockParam1");
        lenient().when(mockParam2.generateSelf(mockImports)).thenReturn("mockParam2");
        lenient().when(mockParam3.generateSelf(mockImports)).thenReturn("mockParam3");
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     * @param code       {@link List} of {@link String} lines of code to use for the method implementation
     * @param parameters {@link List} of {@link JParameter}s that are be used as parameters for the method
     */
    private void initMethod(List<String> code, List<JParameter<?>> parameters) {
        method = new TestJMethod(code);
        for (JParameter<?> t : parameters)
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
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1);");

        // Populate the method details
        initMethod(null, Collections.singletonList(mockParam1));
        Assertions.assertIterableEquals(Collections.singleton(mockParam1), method.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).generateSelf(mockImports);
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and several parameters.
     */
    @Test
    public void testNoImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1, mockParam2, mockParam3);");

        // Populate the method details
        initMethod(null, Arrays.asList(mockParam1, mockParam2, mockParam3));
        Assertions.assertIterableEquals(Arrays.asList(mockParam1, mockParam2, mockParam3), method.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).generateSelf(mockImports);
        verify(mockParam2).generateSelf(mockImports);
        verify(mockParam3).generateSelf(mockImports);
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
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).generateSelf(mockImports);
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and several parameters.
     */
    @Test
    public void testEmptyImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1, mockParam2, mockParam3) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).generateSelf(mockImports);
        verify(mockParam2).generateSelf(mockImports);
        verify(mockParam3).generateSelf(mockImports);
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
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1) {");
        matcher.eq("    qwerty");
        matcher.eq("    asdfg");
        matcher.eq("    zxcvb");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "asdfg", "zxcvb"), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).generateSelf(mockImports);
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and several parameters.
     */
    @Test
    public void testSomeImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(mockParam1, mockParam2, mockParam3) {");
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
        verify(mockParam1).generateSelf(mockImports);
        verify(mockParam2).generateSelf(mockImports);
        verify(mockParam3).generateSelf(mockImports);
    }

    /**
     * Verify that the signature end if properly generated
     */
    @Test
    public void testGenerateSignatureEnd() {
        Assertions.assertEquals(" {", method.generateSignatureEnd(true));
        Assertions.assertEquals(";", method.generateSignatureEnd(false));
    }

    /**
     * Verify that equality works as expected
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        method = new TestJMethod("method");

        // Only basic information
        Assertions.assertFalse(method.equals(null));
        Assertions.assertFalse(method.equals("abc123"));
        Assertions.assertFalse(method.equals(new TestJMethod(mockOtherReturnType, "method")));
        Assertions.assertFalse(method.equals(new TestJMethod("otherMethod")));
        Assertions.assertTrue(method.equals(new TestJMethod(mockReturnType, "method")));

        // Implementation has no impact
        Assertions.assertTrue(method.equals( new TestJMethod(mockReturnType, "method", null)));
        Assertions.assertTrue(method.equals( new TestJMethod(mockReturnType, "method", Collections.emptyList())));
        Assertions.assertTrue(method.equals( new TestJMethod(mockReturnType, "method", Arrays.asList("a", "b", "c", "d"))));
        
        // Single parameter
        method.addParameter(mockParam1);
        Assertions.assertFalse(method.equals(build("method", mockParam2)));
        Assertions.assertFalse(method.equals(build("method", mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam2, mockParam3)));
        Assertions.assertTrue(method.equals(build("method", mockParam1)));
        
        // Multiple parameters
        method.addParameter(mockParam2);
        method.addParameter(mockParam3);
        Assertions.assertFalse(method.equals(build("method", mockParam1)));
        Assertions.assertFalse(method.equals(build("method", mockParam2)));
        Assertions.assertFalse(method.equals(build("method", mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(method.equals(build("method", mockParam1, mockParam3, mockParam2)));
        Assertions.assertFalse(method.equals(build("method", mockParam3, mockParam2, mockParam1)));
        Assertions.assertTrue(method.equals(build("method", mockParam1, mockParam2, mockParam3)));
    }
    
    private TestJMethod build(String name, JParameter<?>...paramToApply) {
        TestJMethod method = new TestJMethod(name);
        for (JParameter<?> param: paramToApply)
            method.addParameter(param);
        
        return method;
    }

    /**
     * Generate the code and verify that it matches expectations
     * 
     * @param expectedHasImplementation boolean true if generateSignatureStart is expected to be called with an implementation
     */
    private void generateAndVerifyCode(boolean expectedHasImplementation) {
        method.generate(builder, mockImports);
        method.verifyGenerateCalled(1, expectedHasImplementation);
        verify(mockReturnType).registerImport(mockImports);
        verify(mockReturnType).getSimpleName();

        matcher.match(builder.get());
    }

}
