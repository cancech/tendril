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

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;

/**
 * Test case for {@link JMethodDefault}
 */
public class JMethodInterfaceTest extends AbstractMethodTest {

    /**
     * Verify that final is properly accounted for.
     */
    @Test
    public void testVisibility() {
        JMethodInterface<Type> method = new JMethodInterface<>(mockReturnType, "publicInterfaceMethod", Collections.emptyList());
        Assertions.assertEquals(VisibilityType.PUBLIC, method.getVisibility());

        Assertions.assertThrows(IllegalArgumentException.class, () -> method.setVisibility(VisibilityType.PACKAGE_PRIVATE));
        Assertions.assertEquals(VisibilityType.PUBLIC, method.getVisibility());
        Assertions.assertThrows(IllegalArgumentException.class, () -> method.setVisibility(VisibilityType.PROTECTED));
        Assertions.assertEquals(VisibilityType.PUBLIC, method.getVisibility());
        
        method.setVisibility(VisibilityType.PRIVATE);
        Assertions.assertEquals(VisibilityType.PRIVATE, method.getVisibility());
    }

    /**
     * Verify that final is properly accounted for.
     */
    @Test
    public void testFinal() {
        JMethodInterface<Type> method = new JMethodInterface<>(mockReturnType, "publicInterfaceMethod", Collections.emptyList());
        Assertions.assertFalse(method.isFinal());
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> method.setFinal(true));
        Assertions.assertFalse(method.isFinal());
        
        method.setFinal(false);
        Assertions.assertFalse(method.isFinal());
    }
    
    /**
     * Verify that the appropriate method signature start is generated
     */
    @Test
    public void testSignatureStartPublic() {
        JMethodInterface<Type> method = new JMethodInterface<>(mockReturnType, "publicInterfaceMethod", Collections.emptyList());
        verifyMethodInit("publicInterfaceMethod", method);
        Assertions.assertEquals("", method.generateSignatureStart(false));
        Assertions.assertEquals("default ", method.generateSignatureStart(true));
        
        method.setStatic(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> method.generateSignatureStart(false));
        Assertions.assertEquals("static ", method.generateSignatureStart(true));
    }
    
    /**
     * Verify that the appropriate method signature start is generated
     */
    @Test
    public void testSignatureStartPrivate() {
        JMethodInterface<Type> method = new JMethodInterface<>(mockReturnType, "publicInterfaceMethod", Collections.emptyList());
        method.setVisibility(VisibilityType.PRIVATE);
        verifyMethodInit("publicInterfaceMethod", method);
        Assertions.assertThrows(IllegalArgumentException.class, () -> method.generateSignatureStart(false));
        Assertions.assertEquals("private ", method.generateSignatureStart(true));
        
        method.setStatic(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> method.generateSignatureStart(false));
        Assertions.assertEquals("private static ", method.generateSignatureStart(true));
    }
}
