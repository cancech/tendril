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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.lang.model.element.AnnotationMirror;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.AnnotationGeneratedListener;
import tendril.annotationprocessor.GeneratedAnnotationLoader;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * 
 */
public class GeneratedAnnotationHandlerTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private GeneratedAnnotationLoader mockLoader1;
    @Mock
    private GeneratedAnnotationLoader mockLoader2;
    @Mock
    private GeneratedAnnotationLoader mockLoader3;
    @Mock
    private AnnotationGeneratedListener mockListener1;
    @Mock
    private AnnotationGeneratedListener mockListener2;
    @Mock
    private AnnotationGeneratedListener mockListener3;
    @Mock
    private ClassType mockClassType;
    @Mock
    private AnnotationMirror mockMirror;
    @Mock
    private JAnnotation mockAnnotation;
    
    // Instance to test
    private GeneratedAnnotationHandler handler;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        handler = new GeneratedAnnotationHandler();
    }

    /**
     * Verify that listeners are properly added
     */
    @Test
    public void testListenersAdded() {
        handler.addListener(mockListener1);
        
        handler.registerLoader(mockLoader1);
        verify(mockLoader1).addListener(mockListener1);
        
        handler.addListener(mockListener2);
        verify(mockLoader1).addListener(mockListener2);
        
        handler.registerLoader(mockLoader2);
        verify(mockLoader2).addListener(mockListener1);
        verify(mockLoader2).addListener(mockListener2);
        
        handler.addListener(mockListener3);
        verify(mockLoader1).addListener(mockListener3);
        verify(mockLoader2).addListener(mockListener3);
        
        handler.registerLoader(mockLoader3);
        verify(mockLoader3).addListener(mockListener1);
        verify(mockLoader3).addListener(mockListener2);
        verify(mockLoader3).addListener(mockListener3);
    }
    
    /**
     * Verify that no annotation is produced if there is no loader
     */
    @Test
    public void testGetInstanceNoLoader() {
        Assertions.assertNull(handler.getAnnotationInstance(mockClassType, mockMirror));
    }
    
    /**
     * Verify that no annotation is produced if no loader provides one
     */
    @Test
    public void testGetInstanceNoLoaderProvides() {
        handler.registerLoader(mockLoader1);
        handler.registerLoader(mockLoader2);
        handler.registerLoader(mockLoader3);
        
        when(mockLoader1.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        when(mockLoader2.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        when(mockLoader3.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        
        Assertions.assertNull(handler.getAnnotationInstance(mockClassType, mockMirror));
        verify(mockLoader1).getAnnotationInstance(mockClassType, mockMirror);
        verify(mockLoader2).getAnnotationInstance(mockClassType, mockMirror);
        verify(mockLoader3).getAnnotationInstance(mockClassType, mockMirror);
    }
    
    /**
     * Verify that an annotation is produced if one loader provides
     */
    @Test
    public void testGetInstanceLoader1Provides() {
        handler.registerLoader(mockLoader1);
        handler.registerLoader(mockLoader2);
        handler.registerLoader(mockLoader3);
        
        when(mockLoader1.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(mockAnnotation);
        Assertions.assertEquals(mockAnnotation, handler.getAnnotationInstance(mockClassType, mockMirror));
        verify(mockLoader1).getAnnotationInstance(mockClassType, mockMirror);
    }
    
    /**
     * Verify that an annotation is produced if one loader provides
     */
    @Test
    public void testGetInstanceLoader2Provides() {
        handler.registerLoader(mockLoader1);
        handler.registerLoader(mockLoader2);
        handler.registerLoader(mockLoader3);

        when(mockLoader1.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        when(mockLoader2.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(mockAnnotation);
        Assertions.assertEquals(mockAnnotation, handler.getAnnotationInstance(mockClassType, mockMirror));
        verify(mockLoader1).getAnnotationInstance(mockClassType, mockMirror);
        verify(mockLoader2).getAnnotationInstance(mockClassType, mockMirror);
    }
    
    /**
     * Verify that an annotation is produced if one loader provides
     */
    @Test
    public void testGetInstanceLoader3Provides() {
        handler.registerLoader(mockLoader1);
        handler.registerLoader(mockLoader2);
        handler.registerLoader(mockLoader3);

        when(mockLoader1.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        when(mockLoader2.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(null);
        when(mockLoader3.getAnnotationInstance(mockClassType, mockMirror)).thenReturn(mockAnnotation);
        Assertions.assertEquals(mockAnnotation, handler.getAnnotationInstance(mockClassType, mockMirror));
        verify(mockLoader1).getAnnotationInstance(mockClassType, mockMirror);
        verify(mockLoader2).getAnnotationInstance(mockClassType, mockMirror);
        verify(mockLoader3).getAnnotationInstance(mockClassType, mockMirror);
    }
}
