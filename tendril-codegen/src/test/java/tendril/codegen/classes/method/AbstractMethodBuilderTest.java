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

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.SharedMethodBuilderTest;
import tendril.codegen.field.type.Type;

/**
 * Test case for {@link AbstractMethodBuilder}
 */
public class AbstractMethodBuilderTest extends SharedMethodBuilderTest<AbstractMethodBuilder<Type>> {

    /**
     * 
     * @see tendril.codegen.classes.SharedMethodBuilderTest#createBuilder()
     */
    @Override
    protected MethodBuilder<Type> createBuilder() {
        return new AbstractMethodBuilder<Type>(mockClassBuilder, METHOD_NAME);
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidate() {
        // No code, only private fails
        verifyValidateDoesNotThrow(VisibilityType.PUBLIC);
        verifyValidateDoesNotThrow(VisibilityType.PROTECTED);
        verifyValidateDoesNotThrow(VisibilityType.PACKAGE_PRIVATE);
        verifyValidateDoesThrow(VisibilityType.PRIVATE, true);

        // With code, all pass
        builder.emptyImplementation();
        verifyValidateDoesNotThrow(VisibilityType.PUBLIC);
        verifyValidateDoesNotThrow(VisibilityType.PROTECTED);
        verifyValidateDoesNotThrow(VisibilityType.PACKAGE_PRIVATE);
        verifyValidateDoesNotThrow(VisibilityType.PRIVATE);
    }
    
    /**
     * Verify that the proper method is created
     */
    @Test
    public void testBuildMethod() {
        verifyBuildMethodType(JMethodDefault.class);
    }
}
