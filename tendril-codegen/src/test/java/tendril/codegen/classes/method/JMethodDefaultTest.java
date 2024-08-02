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
public class JMethodDefaultTest extends SharedJMethodTest {

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
    public void testSignatureStart() {
        JMethodDefault<Type> method = new JMethodDefault<>(mockReturnType, "defaultMethod", Collections.emptyList());
        method.setVisibility(mockVisibility);
        verifyMethodInit("defaultMethod", method);

        Assertions.assertEquals("mockVisibility abstract ", method.generateSignatureStart(false));
        Assertions.assertEquals("mockVisibility ", method.generateSignatureStart(true));
    }
    
    /**
     * Verify that the package private special case works as expected
     */
    @Test
    public void testPackagePrivateSignatureStart() {
        JMethodDefault<Type> method = new JMethodDefault<>(mockReturnType, "defaultMethod", Collections.emptyList());
        method.setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verifyMethodInit("defaultMethod", method);

        Assertions.assertEquals("abstract ", method.generateSignatureStart(false));
        Assertions.assertEquals("", method.generateSignatureStart(true));
    }
}
