/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.annotationprocessor.element;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link ElementCache}
 */
public class ElementCacheTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassConverter mockConverter;
    @Mock
    private TypeElement mockClassType;
    @Mock
    private ExecutableElement mockMethodType;
    @Mock
    private VariableElement mockFieldType;
    @Mock
    private JClass mockClass;
    @Mock
    private JMethod<?> mockMethod;
    @Mock
    private JField<?> mockField;
    
    // Instance to test
    private ElementCache cache;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        cache = new ElementCache(mockConverter);
        verify(mockConverter).setCache(cache);
        

        lenient().when(mockMethodType.getEnclosingElement()).thenReturn(mockClassType);
        lenient().when(mockFieldType.getEnclosingElement()).thenReturn(mockClassType);
        try {
            lenient().doAnswer((inv) -> {
                cache.store(mockClassType, mockClass);
                cache.store(mockClassType, mockMethodType, mockMethod);
                cache.store(mockClassType, mockFieldType, mockField);
                return null;
            }).when(mockConverter).loadClassDetails(mockClassType);
        } catch (Exception e) {
            Assertions.fail("Exception should not be thrown...", e);
        }
    }

    /**
     * Verify that during the first load the class is loaded
     * @throws TendrilException 
     */
    @Test
    public void testFirstLoadClass() throws MissingAnnotationException, TendrilException {
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
    }

    /**
     * Verify that during the first load the class is loaded
     * @throws TendrilException 
     */
    @Test
    public void testFirstLoadMethod() throws MissingAnnotationException, TendrilException {
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
        verify(mockMethodType, times(1)).getEnclosingElement();
    }

    /**
     * Verify that during the first load the class is loaded
     * @throws TendrilException 
     */
    @Test
    public void testFirstLoadField() throws MissingAnnotationException, TendrilException {
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
        verify(mockFieldType, times(1)).getEnclosingElement();
    }
    
    /**
     * Verify that subsequent loads do not re-load the class
     * @throws TendrilException 
     */
    @Test
    public void testSubsequentLoadAfterClass() throws MissingAnnotationException, TendrilException {
        testFirstLoadClass();

        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(1)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(3)).getEnclosingElement();

        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(1)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(3)).getEnclosingElement();
        
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
    }
    
    /**
     * Verify that subsequent loads do not re-load the class
     * @throws TendrilException 
     */
    @Test
    public void testSubsequentLoadAfterMethod() throws MissingAnnotationException, TendrilException {
        testFirstLoadMethod();

        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(3)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(4)).getEnclosingElement();

        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(1)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(3)).getEnclosingElement();
        
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
    }
    
    /**
     * Verify that subsequent loads do not re-load the class
     * @throws TendrilException 
     */
    @Test
    public void testSubsequentLoadAfterField() throws MissingAnnotationException, TendrilException {
        testFirstLoadField();

        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        Assertions.assertEquals(mockClass, cache.retrieveClass(mockClassType));
        
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(1)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockMethod), cache.retrieveMethod(mockMethodType));
        verify(mockMethodType, times(3)).getEnclosingElement();

        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(2)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(3)).getEnclosingElement();
        Assertions.assertEquals(Pair.of(mockClass, mockField), cache.retrieveField(mockFieldType));
        verify(mockFieldType, times(4)).getEnclosingElement();
        
        verify(mockConverter, times(1)).loadClassDetails(mockClassType);
    }
}
