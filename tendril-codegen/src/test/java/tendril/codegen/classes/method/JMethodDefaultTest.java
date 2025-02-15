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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;

/**
 * Test case for {@link JMethodDefault}
 */
public class JMethodDefaultTest extends AbstractMethodTest {

    /**
     * @see tendril.codegen.classes.method.SharedJMethodTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();
    }

    /**
     * Verify that the appropriate method signature start is generated
     */
    @Test
    public void testSignatureStartNoImplementation() {
        JMethodDefault<Type> method = new JMethodDefault<>(mockReturnType, "defaultMethod", Collections.emptyList());
        method.setVisibility(mockVisibility);
        verifyMethodInit("defaultMethod", method);
        Assertions.assertEquals("mockVisibility abstract ", method.generateSignatureStart(false));
        verify(mockVisibility).getKeyword();
        
        method.setFinal(true);
        Assertions.assertThrows(DefinitionException.class, () -> method.generateSignatureStart(false));
        verify(mockReturnType).getSimpleName();
        verify(mockVisibility, times(2)).getKeyword();
        
        method.setFinal(false);
        method.setStatic(true);
        Assertions.assertThrows(DefinitionException.class, () -> method.generateSignatureStart(false));
        verify(mockReturnType, times(2)).getSimpleName();
        verify(mockVisibility, times(3)).getKeyword();
        
        method.setStatic(false);
        method.setVisibility(VisibilityType.PACKAGE_PRIVATE);
        Assertions.assertEquals("abstract ", method.generateSignatureStart(false));
    }
    
    /**
     * Verify that the package private special case works as expected
     */
    @Test
    public void testSignatureStartWithImplementation() {
        JMethodDefault<Type> method = new JMethodDefault<>(mockReturnType, "defaultMethod", Collections.emptyList());
        method.setVisibility(mockVisibility);
        verifyMethodInit("defaultMethod", method);
        Assertions.assertEquals("mockVisibility ", method.generateSignatureStart(true));
        verify(mockVisibility).getKeyword();
        
        method.setFinal(true);
        Assertions.assertEquals("mockVisibility final ", method.generateSignatureStart(true));
        verify(mockVisibility, times(2)).getKeyword();
        
        method.setStatic(true);
        Assertions.assertEquals("mockVisibility static final ", method.generateSignatureStart(true));
        verify(mockVisibility, times(3)).getKeyword();
        
        method.setVisibility(VisibilityType.PACKAGE_PRIVATE);
        Assertions.assertEquals("static final ", method.generateSignatureStart(true));
    }
}
