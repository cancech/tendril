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
package tendril.dom.type.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PoDType}
 */
public class PoDTypeTest {

    /**
     * Verify elements produce the proper string
     */
    @Test
    public void testToString() {
        Assertions.assertEquals(8, PoDType.values().length);
        Assertions.assertEquals("boolean", PoDType.BOOLEAN.toString());
        Assertions.assertEquals("byte", PoDType.BYTE.toString());
        Assertions.assertEquals("char", PoDType.CHAR.toString());
        Assertions.assertEquals("double", PoDType.DOUBLE.toString());
        Assertions.assertEquals("float", PoDType.FLOAT.toString());
        Assertions.assertEquals("int", PoDType.INT.toString());
        Assertions.assertEquals("long", PoDType.LONG.toString());
        Assertions.assertEquals("short", PoDType.SHORT.toString());
    }
    
    /**
     * Verify that the assignability is properly configured
     */
    @Test
    public void testAssignable() {
        for (PoDType i: PoDType.values()) {
            for (PoDType j: PoDType.values())
                Assertions.assertEquals(i == j, i.isAssignableTo(j));
        }
    }
}
