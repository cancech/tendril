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
package tendril.codegen.field.type;

import org.junit.jupiter.api.Test;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import test.AbstractUnitTest;
import test.assertions.ClassAssert;

/**
 * Test case for {@link TypeDataFactory}
 */
public class TypeDataFactoryTest extends AbstractUnitTest {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that all types can be properly created
     */
    @Test
    public void testFactoryMethods() {
        ClassAssert.assertInstance(TypeDataVoid.class, TypeDataFactory.create());
        ClassAssert.assertInstance(TypeDataDeclared.class, TypeDataFactory.create(TypeDataFactory.class));
        ClassAssert.assertInstance(TypeDataDeclared.class, TypeDataFactory.create(new ClassType("a", "b")));
        for (PoDType type: PoDType.values())
            ClassAssert.assertInstance(TypeDataPoD.class, TypeDataFactory.create(type));
    }
    
}
