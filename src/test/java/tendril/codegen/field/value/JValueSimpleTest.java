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
package tendril.codegen.field.value;

import org.junit.jupiter.api.Test;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;

/**
 * Test case for {@link JValueSimple}
 */
public class JValueSimpleTest extends SharedJValueTest {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Nothing to do
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerate() {
        assertCode("`dsf'", new JValueSimple<ClassType, String>(new ClassType(String.class), "dsf", "`", "'"));
        assertCode("abc123efg", new JValueSimple<PrimitiveType, Integer>(PrimitiveType.INT, 123, "abc", "efg"));
        assertCode("1.23", new JValueSimple<PrimitiveType, Double>(PrimitiveType.DOUBLE, 1.23, "", ""));
        assertCode("321", new JValueSimple<PrimitiveType, Short>(PrimitiveType.SHORT, (short) 321));
    }

}
