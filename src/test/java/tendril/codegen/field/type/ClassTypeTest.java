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

import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ImportElement;
import tendril.codegen.field.NamedType;
import tendril.codegen.field.value.JValueFactory;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link ClassType}
 */
public class ClassTypeTest extends SharedTypeTest<ClassType> {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        type = new ClassType(VisibilityType.class);
    }
    
    /**
     * Verify that the correct import is registered
     */
    @Test
    public void testRegisterImports() {
        type.registerImport(mockImports);
        verify(mockImports).add(type);
        verifyDataState();
    }
    
    /**
     * Verify that the appropriate Enum element is created
     */
    @Test
    public void testAsValueEnum() {
        // Incorrect object type
        Assertions.assertThrows(IllegalArgumentException.class, () -> type.asValue(new ImportElement("a", "b")));
        
        TendrilAssert.assertJValue(JValueFactory.create(VisibilityType.PUBLIC), type.asValue(VisibilityType.PUBLIC));
        verifyDataState();
    }
    
    /**
     * Verify that the appropriate Class element is created
     */
    @Test
    public void testAsValueClass() {
        type = new ClassType(ImportElement.class);
        Assertions.assertThrows(NotImplementedException.class, () -> type.asValue(new ImportElement("a", "b")));
        verifyDataState(ImportElement.class.getSimpleName(), false);
    }

    /**
     * Verify that the various CTORs work as expected
     */
    @Test
    public void testCtor() {
        TendrilAssert.assertImportData(NamedType.class, new ClassType(NamedType.class));
        TendrilAssert.assertImportData("a.b.c.d", "EfGh", new ClassType("a.b.c.d.EfGh"));
        TendrilAssert.assertImportData("1.2.3.4", "Abcd", new ClassType("1.2.3.4", "Abcd"));
    }

    /**
     * Verify that a new class data is properly generated from an existing one when a class name suffix is supplied
     */
    @Test
    public void testGenerateFromSuffix() {
        TendrilAssert.assertImportData(VisibilityType.class.getPackageName(), VisibilityType.class.getSimpleName() + "Suffix", type.generateFromClassSuffix("Suffix"));
        TendrilAssert.assertImportData("a.b.c.d", "EfGhQwerty", new ClassType("a.b.c.d.EfGh").generateFromClassSuffix("Qwerty"));
        TendrilAssert.assertImportData("1.2.3.4", "AbcdEfgh", new ClassType("1.2.3.4", "Abcd").generateFromClassSuffix("Efgh"));
    }

    /**
     * Verify that the assignment check is properly performed
     */
    @Test
    public void testAssignableTo() {
        ClassType lhs = new ClassType("a.b.c.d.E");

        // These are expected to fail
        for (PoDType pd : PoDType.values()) {
            Assertions.assertFalse(lhs.isAssignableTo(pd));
        }
        Assertions.assertFalse(lhs.isAssignableTo(VoidType.INSTANCE));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType("a.b.c.D")));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType(NamedType.class)));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType("a.b.c.d.e")));

        // These are expected to pass
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("a.b.c.d.E")));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("a.b.c.d", "E")));
        lhs = new ClassType(NamedType.class);
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType(NamedType.class)));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType(NamedType.class.getName())));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType(NamedType.class.getPackageName(), NamedType.class.getSimpleName())));
    }
    
    /**
     * Verify that the data state is correct
     */
    private void verifyDataState() {
        verifyDataState(VisibilityType.class.getSimpleName(), false);
    }
}
