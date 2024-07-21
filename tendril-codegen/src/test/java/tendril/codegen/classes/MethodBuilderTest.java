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
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.Type;
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
        private int timesValidateCalled = 0;
        private int timesBuildMethodCalled = 0;
        // Track the last methodElement so that it can be verified
        private Type returnType;
        private String name;

        /**
         * CTOR
         */
        protected TestMethodBuilder() {
            super(mockClass, mockReturnType, "MethodName");
        }

        /**
         * Track the number of times it has been called
         */
        @Override
        protected void validateData() throws IllegalArgumentException {
            timesValidateCalled++;
        }

        /**
         * Track the number of times it have been called and the method element provided
         */
        @Override
        protected JMethod<Type> buildMethod(Type returnType, String name) {
            timesBuildMethodCalled++;
            this.returnType = returnType;
            this.name = name;
            return mockMethod;
        }

        /**
         * Verify the number of times the abstract methods have been called.
         * 
         * @param expectedTimesValidateCalled int expected number of times validateData() should have been called
         * @param expetedTimesBuildCalled     int expected number of times buildMethod() should have been called
         * @param expectedReturnType          {@link Type} that should have been provided to buildMethod()
         * @param expectedName                {@link String} that should have been provided to buildMethod()
         */
        public void verifyTimesCalled(int expectedTimesValidateCalled, int expetedTimesBuildCalled, Type expectedReturnType, String expectedName) {
            Assertions.assertEquals(expectedTimesValidateCalled, timesValidateCalled);
            Assertions.assertEquals(expetedTimesBuildCalled, timesBuildMethodCalled);
            if (expectedReturnType == null)
                Assertions.assertNull(returnType);
            else {
                Assertions.assertEquals(returnType, expectedReturnType);
                Assertions.assertEquals(name, expectedName);
            }
        }

        /**
         * Verify that the visibility parameter is correct
         * 
         * @param expected {@link VisibilityType} that should be applied to the method
         */
        public void verifyVisibility(VisibilityType expected) {
            Assertions.assertEquals(expected, visibility);
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
    private JClass mockClass;
    @Mock
    private Type mockReturnType;
    @Mock
    private VisibilityType mockVisibilityType;
    @Mock
    private JMethod<Type> mockMethod;

    // Instance to test
    private TestMethodBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestMethodBuilder();

        // Verify the default values
        builder.verifyVisibility(VisibilityType.PUBLIC);
    }

    /**
     * Verify that the visibility can be properly updated
     */
    @Test
    public void changeVisibility() {
        builder.verifyVisibility(VisibilityType.PUBLIC);
        builder.setVisibility(mockVisibilityType);
        builder.verifyVisibility(mockVisibilityType);

        for (VisibilityType t : VisibilityType.values()) {
            builder.setVisibility(t);
            builder.verifyVisibility(t);
        }

        // Make sure that nothing in the build chain is called
        builder.verifyTimesCalled(0, 0, null, null);
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
        builder.verifyTimesCalled(0, 0, null, null);
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
        builder.verifyTimesCalled(0, 0, null, null);
    }

    /**
     * Verify that the process of building the method works as expected.
     */
    @Test
    public void testBuild() {
        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            builder.build();
            mockUtil.verify(() -> Utilities.throwIfNotValidIdentifier("MethodName"));
            verify(mockClass).addMethod(mockMethod);
            builder.verifyTimesCalled(1, 1, mockReturnType, "MethodName");
        }
    }
}
