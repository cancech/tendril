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

import static org.mockito.Mockito.times;
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
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;
import tendril.test.helper.annotation.TestDefaultAttrAnnotation;
import tendril.test.helper.annotation.TestMarkerAnnotation;
import tendril.test.helper.annotation.TestNonDefaultAttrAnnotation;

/**
 * Test case for {@link JAnnotation}
 */
public class JAnnotationTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockAnnotationClass;
    @Mock
    private ClassType mockOtherAnnotationClass;
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
        Assertions.assertEquals(Collections.emptyList(), annotation.getAttributes());

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
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultAttrAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("value");
        when(mockStringValue.generate(mockImportSet)).thenReturn("abc123");

        String expectedName = "@TestDefaultAttrAnnotation";
        String expectedCode = expectedName + "(abc123)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addAttribute(mockMethod1, mockStringValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        CollectionAssert.assertEquals(Collections.singleton(mockMethod1), annotation.getAttributes());
        Assertions.assertEquals(mockStringValue, annotation.getValue(mockMethod1));

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedCode);
        verify(mockStringValue).generate(mockImportSet);
        verify(mockImportSet).add(mockAnnotationClass);
    }

    /**
     * Verify that an annotation with a single non-default attribute is properly represented
     */
    @Test
    public void testSingleValueNonDefaultAnnotation() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestNonDefaultAttrAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("myString");
        when(mockStringValue.generate(mockImportSet)).thenReturn("abc123");

        String expectedName = "@TestNonDefaultAttrAnnotation";
        String expectedCode = expectedName + "(myString = abc123)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addAttribute(mockMethod1, mockStringValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        CollectionAssert.assertEquals(Collections.singleton(mockMethod1), annotation.getAttributes());
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
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultAttrAnnotation.class.getSimpleName());
        when(mockMethod1.getName()).thenReturn("valStr");
        when(mockStringValue.generate(mockImportSet)).thenReturn("123abc");
        when(mockMethod2.getName()).thenReturn("valInt");
        when(mockIntValue.generate(mockImportSet)).thenReturn("5678");

        String expectedName = "@TestDefaultAttrAnnotation";
        String expectedCode = expectedName + "(valStr = 123abc, valInt = 5678)";

        // Create the annotation
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();
        annotation.addAttribute(mockMethod1, mockStringValue);
        annotation.addAttribute(mockMethod2, mockIntValue);

        // Verify its contents
        Assertions.assertEquals(mockAnnotationClass, annotation.getType());
        Assertions.assertEquals(expectedName, annotation.getName());
        Assertions.assertEquals(Arrays.asList(mockMethod1, mockMethod2), annotation.getAttributes());
        Assertions.assertEquals(mockStringValue, annotation.getValue(mockMethod1));
        Assertions.assertEquals(mockIntValue, annotation.getValue(mockMethod2));

        // Verify the code is properly generated
        annotation.generate(mockBuilder, mockImportSet);
        verify(mockBuilder).append(expectedCode);
        verify(mockImportSet).add(mockAnnotationClass);
        verify(mockStringValue).generate(mockImportSet);
        verify(mockIntValue).generate(mockImportSet);
    }
    
    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsNoAttributes() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultAttrAnnotation.class.getSimpleName());
        when(mockOtherAnnotationClass.getClassName()).thenReturn(TestMarkerAnnotation.class.getSimpleName());
        
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();

        JAnnotation differentAnnotation = new JAnnotation(mockOtherAnnotationClass);
        verify(mockOtherAnnotationClass).getClassName();
        JAnnotation sameAnnotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(2)).getClassName();
        
        // Verify equality
        Assertions.assertFalse(annotation.equals(null));
        Assertions.assertFalse(annotation.equals("abc123"));
        Assertions.assertFalse(annotation.equals(differentAnnotation));
        Assertions.assertTrue(annotation.equals(sameAnnotation));
    }
    
    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsSingleAttribute() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultAttrAnnotation.class.getSimpleName());
        when(mockOtherAnnotationClass.getClassName()).thenReturn(TestMarkerAnnotation.class.getSimpleName());
        
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();

        JAnnotation differentAnnotation = new JAnnotation(mockOtherAnnotationClass);
        verify(mockOtherAnnotationClass).getClassName();
        JAnnotation sameAnnotationDifferentAttribute = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(2)).getClassName();
        JAnnotation sameAnnotationSameAttributeDifferentValue = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(3)).getClassName();
        JAnnotation sameAnnotationSameAttributeSametValue = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(4)).getClassName();
        
        
        // Add a single attribute
        annotation.addAttribute(mockMethod1, mockIntValue);
        differentAnnotation.addAttribute(mockMethod1, mockIntValue);
        sameAnnotationDifferentAttribute.addAttribute(mockMethod2, mockStringValue);
        sameAnnotationSameAttributeDifferentValue.addAttribute(mockMethod1, mockStringValue);
        sameAnnotationSameAttributeSametValue.addAttribute(mockMethod1, mockIntValue);
        
        // Verify equality
        Assertions.assertFalse(annotation.equals(null));
        Assertions.assertFalse(annotation.equals("abc123"));
        Assertions.assertFalse(annotation.equals(differentAnnotation));
        Assertions.assertFalse(annotation.equals(sameAnnotationDifferentAttribute));
        Assertions.assertFalse(annotation.equals(sameAnnotationSameAttributeDifferentValue));
        Assertions.assertTrue(annotation.equals(sameAnnotationSameAttributeSametValue));
    }
    
    /**
     * Verify that equality is properly determined
     */
    @Test
    public void testEqualsMultipleAttributes() {
        when(mockAnnotationClass.getClassName()).thenReturn(TestDefaultAttrAnnotation.class.getSimpleName());
        
        JAnnotation annotation = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass).getClassName();

        JAnnotation sameAnnotationNoAttribute = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(2)).getClassName();
        JAnnotation sameAnnotationDifferentFirstNoSecond = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(3)).getClassName();
        JAnnotation sameAnnotationSameFirstNoSecond = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(4)).getClassName();
        JAnnotation sameAnnotationSameFirstDifferentSecond = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(5)).getClassName();
        JAnnotation sameAnnotationSameFirstSameSecond = new JAnnotation(mockAnnotationClass);
        verify(mockAnnotationClass, times(6)).getClassName();
        
        
        // Add a single attribute
        annotation.addAttribute(mockMethod1, mockIntValue);
        annotation.addAttribute(mockMethod2, mockStringValue);
        sameAnnotationDifferentFirstNoSecond.addAttribute(mockMethod2, mockStringValue);
        sameAnnotationSameFirstNoSecond.addAttribute(mockMethod1, mockIntValue);
        sameAnnotationSameFirstDifferentSecond.addAttribute(mockMethod1, mockIntValue);
        sameAnnotationSameFirstDifferentSecond.addAttribute(mockMethod2, mockIntValue);
        sameAnnotationSameFirstSameSecond.addAttribute(mockMethod1, mockIntValue);
        sameAnnotationSameFirstSameSecond.addAttribute(mockMethod2, mockStringValue);

        
        // Verify equality
        Assertions.assertFalse(annotation.equals(sameAnnotationNoAttribute));
        Assertions.assertFalse(annotation.equals(sameAnnotationDifferentFirstNoSecond));
        Assertions.assertFalse(annotation.equals(sameAnnotationSameFirstNoSecond));
        Assertions.assertFalse(annotation.equals(sameAnnotationSameFirstDifferentSecond));
        Assertions.assertTrue(annotation.equals(sameAnnotationSameFirstSameSecond));
    }
}
