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
package tendril.dom.annotation;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import test.AbstractUnitTest;

/**
 * Test case for {@link AnnotationHandler}
 */
public class AnnotationHandlerTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private AppliedAnnotation mockAnnotation1;
    @Mock
    private AppliedAnnotation mockAnnotation2;
    @Mock
    private AppliedAnnotation mockAnnotation3;
    
    // The instance to test
    private AnnotationHandler handler;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        handler = new AnnotationHandler();
    }
    
    /**
     * No annotation registered, none returned
     */
    @Test
    public void testNoAnnotation() {
        Assertions.assertEquals(Collections.emptyList(), handler.getAnnotations());
    }
    
    /**
     * A single annotation registered
     */
    @Test
    public void testSingleAnnotation() {
        handler.addAnnotation(mockAnnotation1);
        Assertions.assertEquals(Collections.singletonList(mockAnnotation1), handler.getAnnotations());
    }
    
    /**
     * Multiple annotations registered
     */
    @Test
    public void testMultipleAnnotations() {
        handler.addAnnotation(mockAnnotation1);
        handler.addAnnotation(mockAnnotation2);
        handler.addAnnotation(mockAnnotation3);
        Assertions.assertEquals(Arrays.asList(mockAnnotation1, mockAnnotation2, mockAnnotation3), handler.getAnnotations());
    }
}
