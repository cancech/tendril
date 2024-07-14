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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import test.AbstractUnitTest;

/**
 * Test case for {@link TypeFactory}
 */
public class TypeFactoryTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private TypeMirror mockMirror;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that a Void type can be created properly
     */
    @Test
    public void testCreateVoid() {
        performCreateTest(TypeKind.VOID, VoidType.INSTANCE);
        verify(mockMirror).getKind();
    }

    /**
     * Verify that a primitive type can be created properly
     */
    @Test
    public void testCreatePoD() {
        int timesGetKind = 0;
        performCreateTest(TypeKind.BOOLEAN, PoDType.BOOLEAN);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.BYTE, PoDType.BYTE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.CHAR, PoDType.CHAR);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.DOUBLE, PoDType.DOUBLE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.FLOAT, PoDType.FLOAT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.INT, PoDType.INT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.LONG, PoDType.LONG);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.SHORT, PoDType.SHORT);
        verify(mockMirror, times(++timesGetKind)).getKind();
    }

    /**
     * Verify that a class can be created Properly
     */
    @Test
    public void testCreateClass() {
        when(mockMirror.toString()).thenReturn("a.b.c.D");
        performCreateTest(TypeKind.DECLARED, new ClassType("a.b.c.D"));
        verify(mockMirror).getKind();
    }
    
    /**
     * Verify that an exception is thrown if any other type of creation is attempted
     */
    @Test
    public void testCreateOther() {
        int timesGetKind = 0;
        for (TypeKind kind: TypeKind.values()) {
            if (kind.isPrimitive() || kind == TypeKind.VOID || kind == TypeKind.DECLARED)
                continue;
            
            when(mockMirror.getKind()).thenReturn(kind);
            Assertions.assertThrows(IllegalArgumentException.class, () -> TypeFactory.create(mockMirror));
            verify(mockMirror, times(++timesGetKind)).getKind();
        }
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirrorKind    {@link TypeKind} the mirror passed into the create method returns
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(TypeKind mirrorKind, Type expectCreated) {
        when(mockMirror.getKind()).thenReturn(mirrorKind);
        Assertions.assertEquals(expectCreated, TypeFactory.create(mockMirror));
    }
}
