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

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.DefinitionException;
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.assertions.CollectionAssert;
import tendril.test.assertions.matchers.MultiLineStringMatcher;

/**
 * Test case for {@link JAbstractMethodElement}
 */
public class JAbstractMethodElementTest extends AbstractMethodTest {

    private static final String GENERATED_TEXT = "GENERATED_SIGNATURE";
    
    private class TestMethodElement extends JAbstractMethodElement<Type> {
        /** Counter for how many times generateSignature has been called */
        private int timesCalled = 0;
        /** The last hasImplementation that was passed to generateSignature */
        private boolean hasImplementation = false;

        /**
         * CTOR
         * 
         * @param name {@link String} the name of the method
         */
        protected TestMethodElement(String name) {
            this(mockReturnType, name);
        }

        /**
         * CTOR
         * 
         * @param returnType {@link Type} the method is to return
         * @param name       {@link String} the name of the method
         */
        protected TestMethodElement(Type returnType, String name) {
            this(returnType, name, null);
        }

        /**
         * CTOR
         * 
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestMethodElement(List<String> implementation) {
            this(mockReturnType, "mockMethodName", implementation);
        }
        
        /**
         * CTOR
         * 
         * @param returnType     {@link Type} the method is to return
         * @param name           {@link String} the name of the method
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestMethodElement(Type returnType, String name, List<String> implementation) {
            super(returnType, name, implementation);
            setVisibility(mockVisibility);
        }

        /**
         * @see tendril.codegen.classes.method.JAbstractMethodElement#generateSignature(java.util.Set, boolean)
         */
        @Override
        protected String generateSignature(boolean hasImplementation) {
            timesCalled++;
            this.hasImplementation = hasImplementation;
            return GENERATED_TEXT;
        }
        
        /**
         * Verify that the generateSignature method has been called the expected number of times.
         * 
         * @param expectedTimesCalled int time the method should have been called.
         */
        private void verifyTimesCalled(int expectedTimesCalled, boolean expectedHasImplementation) {
            Assertions.assertEquals(expectedTimesCalled, timesCalled);
            Assertions.assertEquals(expectedHasImplementation, hasImplementation);
        }
        
    }

    // For tracking the generated code
    private CodeBuilder builder;
    // Matcher to match the generated code
    private MultiLineStringMatcher matcher;
    
    // Mocks to use for testing
    @Mock
    private JParameter<Type> mockParam1;
    @Mock
    private JParameter<Type> mockParam2;
    @Mock
    private JParameter<Type> mockParam3;
    @Mock
    private ClassType mockEx1;
    @Mock
    private ClassType mockEx2;
    @Mock
    private ClassType mockEx3;
    @Mock
    private Type mockOtherReturnType;
    
    // Instance to use for testing
    private TestMethodElement element;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();
        builder = new CodeBuilder();
        matcher = new MultiLineStringMatcher();
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     * @param code       {@link List} of {@link String} lines of code to use for the method implementation
     * @param parameters {@link List} of {@link JParameter}s that are be used as parameters for the method
     */
    private void initMethod(List<String> code, List<JParameter<?>> parameters) {
        element = new TestMethodElement(code);
        for (JParameter<?> t : parameters)
            element.addParameter(t);

        verifyMethodInit("mockMethodName", element);
    }
    
    /**
     * Verify that the parameter list can be properly generated
     */
    @Test
    public void testGenerateNoParameters() {
        initMethod(null, Collections.emptyList());
        CollectionAssert.assertEmpty(element.getParameters());
        Assertions.assertEquals("", element.generateParameters());
    }
    
    /**
     * Verify that the parameter list can be properly generated
     */
    @Test
    public void testGenerateSingleParameter() {
        when(mockParam1.generateSelf()).thenReturn("mockParam1");
        initMethod(null, Collections.singletonList(mockParam1));
        Assertions.assertIterableEquals(Collections.singletonList(mockParam1), element.getParameters());
        Assertions.assertEquals("mockParam1", element.generateParameters());
    }
    
    /**
     * Verify that the parameter list can be properly generated
     */
    @Test
    public void testGenerateMultipleParameters() {
        when(mockParam1.generateSelf()).thenReturn("mockParam1");
        when(mockParam2.generateSelf()).thenReturn("mockParam2");
        when(mockParam3.generateSelf()).thenReturn("mockParam3");
        initMethod(null, Arrays.asList(mockParam1, mockParam2, mockParam3));
        Assertions.assertIterableEquals(Arrays.asList(mockParam1, mockParam2, mockParam3), element.getParameters());
        Assertions.assertEquals("mockParam1, mockParam2, mockParam3", element.generateParameters());
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     * @param code       {@link List} of {@link String} lines of code to use for the method implementation
     * @param ClassType {@link Classtype}... that are be used as exceptions for the method
     */
    private void initMethod(List<String> code, ClassType... exceptions) {
        element = new TestMethodElement(code);
        for (ClassType ex : exceptions)
            element.addException(ex);

        verifyMethodInit("mockMethodName", element);
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateNoExceptionWithSpace() {
    	initMethod(null);
    	CollectionAssert.assertEmpty(element.getExceptions());
    	Assertions.assertEquals(" ", element.generateThrows(true));
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateNoExceptionWithoutSpace() {
    	initMethod(null);
    	CollectionAssert.assertEmpty(element.getExceptions());
    	Assertions.assertEquals("", element.generateThrows(false));
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateSingleExceptionWithSpace() {
    	when(mockEx1.getCodeName()).thenReturn("mockEx1");
    	
    	initMethod(null, mockEx1);
    	CollectionAssert.assertEquivalent(Collections.singletonList(mockEx1), element.getExceptions());
    	Assertions.assertEquals(" throws mockEx1 ", element.generateThrows(true));
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateSingleExceptionWithoutSpace() {
    	when(mockEx1.getCodeName()).thenReturn("mockEx1");
    	
    	initMethod(null, mockEx1);
    	CollectionAssert.assertEquivalent(Collections.singletonList(mockEx1), element.getExceptions());
    	Assertions.assertEquals("throws mockEx1", element.generateThrows(false));
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateMultipleExceptionsWithSpace() {
    	when(mockEx1.getCodeName()).thenReturn("mockEx1");
    	when(mockEx2.getCodeName()).thenReturn("mockEx2");
    	when(mockEx3.getCodeName()).thenReturn("mockEx3");
    	
    	initMethod(null, mockEx1, mockEx2, mockEx3);
    	CollectionAssert.assertEquivalent(Arrays.asList(mockEx1, mockEx2, mockEx3), element.getExceptions());
    	Assertions.assertEquals(" throws mockEx1, mockEx2, mockEx3 ", element.generateThrows(true));
    }
    
    /**
     * Verify that throws exception can be generated
     */
    @Test
    public void testGenerateMultipleExceptionsWithoutSpace() {
    	when(mockEx1.getCodeName()).thenReturn("mockEx1");
    	when(mockEx2.getCodeName()).thenReturn("mockEx2");
    	when(mockEx3.getCodeName()).thenReturn("mockEx3");
    	
    	initMethod(null, mockEx1, mockEx2, mockEx3);
    	CollectionAssert.assertEquivalent(Arrays.asList(mockEx1, mockEx2, mockEx3), element.getExceptions());
    	Assertions.assertEquals("throws mockEx1, mockEx2, mockEx3", element.generateThrows(false));
    }

    /**
     * Verify that the code is properly generated when there is no implementation.
     */
    @Test
    public void testGenerateNoImplementation() {
        // What code is expected
        matcher.eq(GENERATED_TEXT);

        // Populate the method details
        initMethod(null, Collections.emptyList());
        CollectionAssert.assertEmpty(element.getParameters());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
    }

    /**
     * Verify that the method code is properly generated when there is some implementation
     */
    @Test
    public void testSingleLineImplementation() {
        // What code is expected
        matcher.eq(GENERATED_TEXT);
        matcher.eq("    qwerty");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.singletonList("qwerty"), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that the method code is properly generated when there is some implementation
     */
    @Test
    public void testMultiLineImplementation() {
        // What code is expected
        matcher.eq(GENERATED_TEXT);
        matcher.eq("    qwerty");
        matcher.eq("    abc123");
        matcher.eq("    321cba");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "abc123", "321cba"), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that equality works as expected
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsParamFirst() {
        element = new TestMethodElement("method");

        // Only basic information
        Assertions.assertFalse(element.equals(null));
        Assertions.assertFalse(element.equals("abc123"));
        Assertions.assertFalse(element.equals(new TestMethodElement(mockOtherReturnType, "method")));
        Assertions.assertFalse(element.equals(new TestMethodElement("otherMethod")));
        Assertions.assertTrue(element.equals(new TestMethodElement(mockReturnType, "method")));

        // Implementation has no impact
        Assertions.assertTrue(element.equals( new TestMethodElement(mockReturnType, "method", null)));
        Assertions.assertTrue(element.equals( new TestMethodElement(mockReturnType, "method", Collections.emptyList())));
        Assertions.assertTrue(element.equals( new TestMethodElement(mockReturnType, "method", Arrays.asList("a", "b", "c", "d"))));
        
        // Single parameter
        element.addParameter(mockParam1);
        Assertions.assertFalse(element.equals(build("method", mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2, mockParam3)));
        Assertions.assertTrue(element.equals(build("method", mockParam1)));
        
        // Multiple parameters
        element.addParameter(mockParam2);
        element.addParameter(mockParam3);
        Assertions.assertFalse(element.equals(build("method", mockParam1)));
        Assertions.assertFalse(element.equals(build("method", mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3, mockParam2, mockParam1)));
        Assertions.assertTrue(element.equals(build("method", mockParam1, mockParam2, mockParam3)));
        
        // Single exception
        element.addException(mockEx1);
        Assertions.assertFalse(element.equals(build("method", mockParam1)));
        Assertions.assertFalse(element.equals(build("method", mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3, mockParam2, mockParam1)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx2})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx3})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx2})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx3})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx2, mockEx3})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1})));
        
        // Multiple exceptions
        element.addException(mockEx2);
        element.addException(mockEx3);
        Assertions.assertFalse(element.equals(build("method", mockParam1)));
        Assertions.assertFalse(element.equals(build("method", mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam3, mockParam2)));
        Assertions.assertFalse(element.equals(build("method", mockParam3, mockParam2, mockParam1)));
        Assertions.assertFalse(element.equals(build("method", mockParam1, mockParam2, mockParam3)));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx2})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx3})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx2})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx3})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx2, mockEx3})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx2, mockEx3})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx1, mockEx3, mockEx2})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx2, mockEx1, mockEx3})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
    }
    
    /**
     * Verify that equality works as expected
     */
    @Test
    public void testEqualsExceptionFirst() {
        element = new TestMethodElement("method");

        // Single exception
        element.addException(mockEx1);
        Assertions.assertFalse(element.equals(build("method", mockEx2)));
        Assertions.assertFalse(element.equals(build("method", mockEx3)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx2)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx3)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx3)));
        Assertions.assertTrue(element.equals(build("method", mockEx1)));

        // Multiple exceptions
        element.addException(mockEx2);
        element.addException(mockEx3);
        Assertions.assertFalse(element.equals(build("method", mockEx1)));
        Assertions.assertFalse(element.equals(build("method", mockEx2)));
        Assertions.assertFalse(element.equals(build("method", mockEx3)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx2)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx3)));
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx3)));
        Assertions.assertTrue(element.equals(build("method", mockEx1, mockEx2, mockEx3)));
        Assertions.assertTrue(element.equals(build("method", mockEx1, mockEx3, mockEx2)));
        Assertions.assertTrue(element.equals(build("method", mockEx2, mockEx1, mockEx3)));
        Assertions.assertTrue(element.equals(build("method", mockEx3, mockEx2, mockEx1)));

        // Single parameter
        element.addParameter(mockParam1);
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx2, mockEx3)));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam2}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam2, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        
        // Multiple parameters
        element.addParameter(mockParam2);
        element.addParameter(mockParam3);
        Assertions.assertFalse(element.equals(build("method", mockEx1, mockEx2, mockEx3)));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam2}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam2, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam2, mockParam1, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertFalse(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam3, mockParam2}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
        Assertions.assertTrue(element.equals(build("method", new JParameter<?>[] {mockParam1, mockParam2, mockParam3}, new ClassType[] {mockEx3, mockEx2, mockEx1})));
    }
    
    /**
     * Verify that the ordinal can be properly set.
     */
    @Test
    public void testInvalidOrdinal() {
    	for (int i = -10; i <= 0; i++) {
    		final int ordinal = i;
    		
        	TestMethodElement m = new TestMethodElement("name");
        	Assertions.assertEquals("name", m.getOrdinalName());
	    	Assertions.assertThrows(DefinitionException.class, () -> m.setOrdinal(ordinal));
	    	Assertions.assertEquals("name", m.getOrdinalName());
    	}
    }
    
    /**
     * Verify that the ordinal can be properly set.
     */
    @Test
    public void testOrdinalOne() {
    	TestMethodElement m = new TestMethodElement("name");
    	Assertions.assertEquals("name", m.getOrdinalName());
    	m.setOrdinal(1);
    	Assertions.assertEquals("name", m.getOrdinalName());

    	for (int i = 2; i <= 10; i++) {
    		final int ordinal = i;
	    	Assertions.assertThrows(DefinitionException.class, () -> m.setOrdinal(ordinal));
	    	Assertions.assertEquals("name", m.getOrdinalName());
    	}
    }
    
    /**
     * Verify that the ordinal can be properly set.
     */
    @Test
    public void testValidOrdinal() {
    	for (int i = 2; i <= 10; i++) {
    		final int ordinal = i;

        	TestMethodElement m = new TestMethodElement("name");
        	Assertions.assertEquals("name", m.getOrdinalName());
        	m.setOrdinal(ordinal);
        	Assertions.assertEquals("name" + ordinal, m.getOrdinalName());
        	
	    	Assertions.assertThrows(DefinitionException.class, () -> m.setOrdinal(ordinal+1));
	    	Assertions.assertEquals("name" + ordinal, m.getOrdinalName());
    	}
    }
    
    private TestMethodElement build(String name, JParameter<?>... paramToApply) {
    	return build(name, paramToApply, new ClassType[] {});
    }
    
    private TestMethodElement build(String name, ClassType... exToApply) {
    	return build(name, new JParameter<?>[] {}, exToApply);
    }
    
    private TestMethodElement build(String name, JParameter<?>[] paramToApply, ClassType[] exToApply) {
        TestMethodElement method = new TestMethodElement(name);
        for (JParameter<?> param: paramToApply)
            method.addParameter(param);
        for (ClassType ex: exToApply)
        	method.addException(ex);
        
        return method;
    }

    /**
     * Generate the code and verify that it matches expectations
     * 
     * @param expectedHasImplementation boolean true if generateSignatureStart is expected to be called with an implementation
     */
    private void generateAndVerifyCode(boolean expectedHasImplementation) {
        element.generate(builder);
        element.verifyTimesCalled(1, expectedHasImplementation);

        matcher.match(builder.get());
    }
}
