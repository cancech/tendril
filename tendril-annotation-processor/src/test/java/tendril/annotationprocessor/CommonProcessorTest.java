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
package tendril.annotationprocessor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * 
 */
public abstract class CommonProcessorTest extends AbstractUnitTest {


    @Mock
    protected ClassDefinition mockGeneratedDef;
    @Mock
    protected ClassType mockGeneratedType;
    @Mock
    protected ProcessingEnvironment mockProcessingEnv;
    @Mock
    protected Filer mockFiler;
    @Mock
    protected JavaFileObject mockFileObject;
    @Mock
    protected Writer mockFileWriter;
    
    /**
     * Helper for configuring the necessary mocks for file generation 
     * @throws IOException
     */
    protected void setupMocksForWriting() throws IOException {
        when(mockGeneratedDef.getType()).thenReturn(mockGeneratedType);
        when(mockGeneratedDef.getCode()).thenReturn("classCode");
        when(mockGeneratedType.getFullyQualifiedName()).thenReturn("z.x.c.V");
        when(mockProcessingEnv.getFiler()).thenReturn(mockFiler);
        when(mockFiler.createSourceFile("z.x.c.V")).thenReturn(mockFileObject);
        when(mockFileObject.openWriter()).thenReturn(mockFileWriter);
    }
    
    /**
     * Helper to verify that file generation took place
     * @throws IOException
     */
    protected void verifyFileWritten() throws IOException {
        verify(mockGeneratedType).getFullyQualifiedName();
        verify(mockFileWriter).write("classCode", 0, "classCode".length());
        verify(mockFileWriter).close();
    }
}
