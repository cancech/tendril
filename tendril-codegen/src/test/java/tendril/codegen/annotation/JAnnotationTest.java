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
package tendril.codegen.annotation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.helper.annotation.TestDefaultParamAnnotation;
import tendril.helper.annotation.TestMarkerAnnotation;
import tendril.helper.annotation.TestNonDefaultParamAnnotation;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;

/**
 * Test case for {@link JAnnotation}
 */
public class JAnnotationTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockAnnotationClass;
    @Mock
    private CodeBuilder mockBuilder;
    @Mock
    private Set<ClassType> mockImportSet;
    @Mock
    private JMethod<Type> mockMethod1;
    @Mock
    private JValue<Type, String> mockStringValue;
    @Mock
    private JMethod<Type> mockMethod2;
    @Mock
    private JValue<Type, Integer> mockIntValue;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Nothing to do
    }

    /**
     * Verify that a marker annotation is properly represented
     */
    @Test
    public void testMarkerAnnotation() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestMarkerAnnotation.class.getSimpleName());
        String expectedName = "@TestMarkerAnnotation";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        Assertions.assertEquals(Collections.emptyList(), annotation.getParameters());

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedName);
        verify(mockImportSet).add(mockAnnotationClass);
    }

    /**
     * Verify that a marker annotation is properly represented
     */
    @Test
    public void testDefaultValueAnnotation() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultParamAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("value");
        when(mockStringValue.generate(mockImportSet)).thenReturn("abc123");

        String expectedName = "@TestDefaultParamAnnotation";
        String expectedCode = expectedName + "(abc123)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addParameter(mockMethod1, mockStringValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        CollectionAssert.assertEquals(Collections.singleton(mockMethod1), annotation.getParameters());
        Assertions.assertEquals(mockStringValue, annotation.getValue(mockMethod1));

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedCode);
        verify(mockStringValue).generate(mockImportSet);
        verify(mockImportSet).add(mockAnnotationClass);
    }

    /**
     * Verify that an annotation with a single non-default parameter is properly represented
     */
    @Test
    public void testSingleValueNonDefaultAnnotation() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestNonDefaultParamAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("myString");
        when(mockStringValue.generate(mockImportSet)).thenReturn("abc123");

        String expectedName = "@TestNonDefaultParamAnnotation";
        String expectedCode = expectedName + "(myString = abc123)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addParameter(mockMethod1, mockStringValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        CollectionAssert.assertEquals(Collections.singleton(mockMethod1), annotation.getParameters());
        Assertions.assertEquals(mockStringValue, annotation.getValue(mockMethod1));

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedCode);
        verify(mockStringValue).generate(mockImportSet);
        verify(mockImportSet).add(mockAnnotationClass);
    }

    /**
     * Verify that a marker annotation is properly represented
     */
    @Test
    public void testMultiParamAnnotation() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultParamAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("valStr");
        when(mockStringValue.generate(mockImportSet)).thenReturn("123abc");
        when(mockMethod2.getName()).thenReturn("valInt");
        when(mockIntValue.generate(mockImportSet)).thenReturn("5678");

        String expectedName = "@TestDefaultParamAnnotation";
        String expectedCode = expectedName + "(valStr = 123abc, valInt = 5678)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addParameter(mockMethod1, mockStringValue);
        annotation.addParameter(mockMethod2, mockIntValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        Assertions.assertEquals(Arrays.asList(mockMethod1, mockMethod2), annotation.getParameters());
        Assertions.assertEquals(mockStringValue, annotation.getValue(mockMethod1));
        Assertions.assertEquals(mockIntValue, annotation.getValue(mockMethod2));

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedCode);
        verify(mockImportSet).add(mockAnnotationClass);
        verify(mockStringValue).generate(mockImportSet);
        verify(mockIntValue).generate(mockImportSet);
    }
}
