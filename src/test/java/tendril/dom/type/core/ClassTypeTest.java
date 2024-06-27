package tendril.dom.type.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.dom.type.NamedTypeElement;
import test.AbstractUnitTest;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link ClassType}
 */
public class ClassTypeTest extends AbstractUnitTest {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that the various CTORs work as expected
     */
    @Test
    public void testCtor() {
        TendrilAssert.assertImportData("tendril.dom.type", "NamedTypeElement", new ClassType(NamedTypeElement.class));
        TendrilAssert.assertImportData("a.b.c.d", "EfGh", new ClassType("a.b.c.d.EfGh"));
        TendrilAssert.assertImportData("1.2.3.4", "Abcd", new ClassType("1.2.3.4", "Abcd"));
    }

    /**
     * Verify that a new class data is properly generated from an existing one when a class name suffix is supplied
     */
    @Test
    public void testGenerateFromSuffix() {
        TendrilAssert.assertImportData("tendril.dom.type", "NamedTypeElementSuffix", new ClassType(NamedTypeElement.class).generateFromClassSuffix("Suffix"));
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
        for (PoDType pd: PoDType.values()) {
            Assertions.assertFalse(lhs.isAssignableTo(pd));
        }
        Assertions.assertFalse(lhs.isAssignableTo(VoidType.INSTANCE));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType("a.b.c.D")));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType(NamedTypeElement.class)));
        Assertions.assertFalse(lhs.isAssignableTo(new ClassType("a.b.c.d.e")));
        
        // These are expected to pass
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("a.b.c.d.E")));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("a.b.c.d", "E")));
        lhs = new ClassType(NamedTypeElement.class);
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType(NamedTypeElement.class)));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("tendril.dom.type.NamedTypeElement")));
        Assertions.assertTrue(lhs.isAssignableTo(new ClassType("tendril.dom.type", "NamedTypeElement")));
    }
}
