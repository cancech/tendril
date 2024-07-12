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

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.Type;
import test.AbstractUnitTest;

/**
 * Test case for {@link VoidType}
 */
public class VoidTypeTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    
    @Override
    protected void prepareTest() {
        // Not required
    }
    
    /**
     * Verify that the equals comparison can be properly made
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        // Anything which is of type VoidType should pass
        Assertions.assertTrue(VoidType.INSTANCE.equals(VoidType.INSTANCE));
        try {
            Constructor<VoidType> ctor = VoidType.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            VoidType newInstance = ctor.newInstance();
            Assertions.assertTrue(VoidType.INSTANCE != newInstance);
            Assertions.assertTrue(VoidType.INSTANCE.equals(newInstance));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Anything else should fail
        Assertions.assertFalse(VoidType.INSTANCE.equals(PoDType.BOOLEAN));
        Assertions.assertFalse(VoidType.INSTANCE.equals("abc123"));
        Assertions.assertFalse(VoidType.INSTANCE.equals(new ClassType("a.b.c.d.E")));
    }
    
    /**
     * Verify that the other (simple) capabilities work as expected
     */
    @Test
    public void testOthers() {
        Assertions.assertEquals("void", VoidType.INSTANCE.toString());
        Assertions.assertFalse(VoidType.INSTANCE.isAssignableTo(null));
        Assertions.assertFalse(VoidType.INSTANCE.isAssignableTo(mockType));
    }

}
