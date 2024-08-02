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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.codegen.Utilities;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link MethodBuilder}
 */
public class MethodBuilderTest extends AbstractUnitTest {

    /**
     * Concrete test implementation for verify the builder
     */
    private class TestMethodBuilder extends MethodBuilder<Type> {
        // Counters to verify times abstract methods have been called
        private int timesBuildMethodCalled = 0;

        /**
         * CTOR
         */
        protected TestMethodBuilder() {
            super(mockClassBuilder, "MethodName");
            setType(mockReturnType);
        }

        /**
         * Track the number of times it have been called
         */
        @Override
        protected JMethod<Type> create() {
            timesBuildMethodCalled++;
            return mockMethod;
        }

        /**
         * Verify the number of times the abstract methods have been called.
         * 
         * @param expetedTimesBuildCalled int expected number of times buildMethod() should have been called
         */
        public void verifyTimesCalled(int expetedTimesBuildCalled) {
            Assertions.assertEquals(expetedTimesBuildCalled, timesBuildMethodCalled);
        }

        /**
         * Verify that the implementation has the correct lines of code
         * 
         * @param expected {@link List} of {@link String} representing the code that should be present for the implementation
         */
        public void verifyImplementation(List<String> expected) {
            Assertions.assertEquals(expected, linesOfCode);
        }
    }

    // Mocks to use for testing
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private Type mockReturnType;
    @Mock
    private JMethod<Type> mockMethod;
    @Mock
    private JAnnotation mockAnnotation1;
    @Mock
    private JAnnotation mockAnnotation2;
    @Mock
    private JAnnotation mockAnnotation3;
    @Mock
    private JValue<Type, ?> mockValue;

    // Instance to test
    private TestMethodBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestMethodBuilder();
    }

    /**
     * Verify that the implementation can be updated
     */
    @Test
    public void testImplementation() {
        builder.verifyImplementation(null);
        Assertions.assertFalse(builder.hasCode());

        // Add some lines
        String[] toAdd = new String[] { "a", "b", "c", "d" };
        builder.addCode(toAdd);
        Assertions.assertTrue(builder.hasCode());

        List<String> expected = new ArrayList<String>();
        expected.addAll(Arrays.asList(toAdd));
        builder.verifyImplementation(expected);

        // Add some more
        toAdd = new String[] { "qwerty", "abc123", "", "3089723", "-_=+[{]};:'" };
        builder.addCode(toAdd);
        Assertions.assertTrue(builder.hasCode());

        expected.addAll(Arrays.asList(toAdd));
        builder.verifyImplementation(expected);

        // Clear the implementation
        builder.emptyImplementation();
        Assertions.assertTrue(builder.hasCode());
        builder.verifyImplementation(Collections.emptyList());

        // Make sure that some more can be added
        toAdd = new String[] { "asd", "asd", "asd", "dsa", "dsa", "dsa" };
        builder.addCode(toAdd);
        Assertions.assertTrue(builder.hasCode());
        builder.verifyImplementation(Arrays.asList(toAdd));

        // Make sure that nothing in the build chain is called
        builder.verifyTimesCalled(0);
    }

    /**
     * Verify that the empty implementation is considered valid code
     */
    @Test
    public void testEmptyImplementation() {
        builder.verifyImplementation(null);
        Assertions.assertFalse(builder.hasCode());

        builder.emptyImplementation();
        Assertions.assertTrue(builder.hasCode());
        builder.verifyImplementation(Collections.emptyList());

        // Make sure that nothing in the build chain is called
        builder.verifyTimesCalled(0);
    }

    /**
     * Verify that the process of building the method works as expected.
     */
    @Test
    public void testBuildWithoutAnnotation() {
        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            builder.build();
            mockUtil.verify(() -> Utilities.throwIfNotValidIdentifier("MethodName"));
            verify(mockMethod).setFinal(false);
            verify(mockMethod).setStatic(false);
            verify(mockMethod).setVisibility(VisibilityType.PACKAGE_PRIVATE);
            builder.verifyTimesCalled(1);
        }
    }

    /**
     * Verify that the process of building the method works as expected.
     */
    @Test
    public void testFinishWithoutAnnotation() {
        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            builder.finish();
            mockUtil.verify(() -> Utilities.throwIfNotValidIdentifier("MethodName"));
            verify(mockMethod).setFinal(false);
            verify(mockMethod).setStatic(false);
            verify(mockMethod).setVisibility(VisibilityType.PACKAGE_PRIVATE);
            verify(mockClassBuilder).addMethod(mockMethod);
            builder.verifyTimesCalled(1);
        }
    }

    /**
     * Verify that the process of building the method works as expected.
     */
    @Test
    public void testBuildWithAnnotation() {
        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            builder.addAnnotation(mockAnnotation1);
            builder.addAnnotation(mockAnnotation2);
            builder.addAnnotation(mockAnnotation3);
            builder.build();
            mockUtil.verify(() -> Utilities.throwIfNotValidIdentifier("MethodName"));
            verify(mockMethod).addAnnotation(mockAnnotation1);
            verify(mockMethod).addAnnotation(mockAnnotation2);
            verify(mockMethod).addAnnotation(mockAnnotation3);
            verify(mockMethod).setFinal(false);
            verify(mockMethod).setStatic(false);
            verify(mockMethod).setVisibility(VisibilityType.PACKAGE_PRIVATE);
            builder.verifyTimesCalled(1);
        }
    }

    /**
     * Verify that the process of building the method works as expected.
     */
    @Test
    public void testFinishWithAnnotation() {
        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            builder.addAnnotation(mockAnnotation1);
            builder.addAnnotation(mockAnnotation2);
            builder.addAnnotation(mockAnnotation3);
            builder.finish();
            mockUtil.verify(() -> Utilities.throwIfNotValidIdentifier("MethodName"));
            verify(mockMethod).addAnnotation(mockAnnotation1);
            verify(mockMethod).addAnnotation(mockAnnotation2);
            verify(mockMethod).addAnnotation(mockAnnotation3);
            verify(mockClassBuilder).addMethod(mockMethod);
            verify(mockMethod).setFinal(false);
            verify(mockMethod).setStatic(false);
            verify(mockMethod).setVisibility(VisibilityType.PACKAGE_PRIVATE);
            builder.verifyTimesCalled(1);
        }
    }

    /**
     * Verify that default values are not supported by default
     */
    @Test
    public void testDefaultValueThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setDefaultValue(mockValue));
    }
}
