package tendril.dom.classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.dom.type.NamedTypeElement;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link ImportElement}
 */
public class ImportElementTest {

    /**
     * Verify that the various CTORs work as expected
     */
    @Test
    public void testCtor() {
        TendrilAssert.assertImportData("tendril.dom.type", "NamedTypeElement", new ImportElement(NamedTypeElement.class));
        TendrilAssert.assertImportData("a.b.c.d", "EfGh", new ImportElement("a.b.c.d.EfGh"));
        TendrilAssert.assertImportData("1.2.3.4", "Abcd", new ImportElement("1.2.3.4", "Abcd"));
    }
    
    /**
     * Verify that the fully qualified name is proper generated
     */
    @Test
    public void testFullyQualifiedName() {
        ImportElement data = new ImportElement(NamedTypeElement.class);
        Assertions.assertEquals("tendril.dom.type.NamedTypeElement", data.getFullyQualifiedName());
        Assertions.assertEquals("tendril.dom.type.NamedTypeElement", data.toString());
        
        data = new ImportElement("a.b.c.d.EfGh");
        Assertions.assertEquals("a.b.c.d.EfGh", data.getFullyQualifiedName());
        Assertions.assertEquals("a.b.c.d.EfGh", data.toString());
        
        data = new ImportElement("1.2.3.4", "Abcd");
        Assertions.assertEquals("1.2.3.4.Abcd", data.getFullyQualifiedName());
        Assertions.assertEquals("1.2.3.4.Abcd", data.toString());
    }
    
    /**
     * Verify that a class that appears in the default package generates an exception
     */
    @Test
    public void testClassInDefaultPackage() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ImportElement("SomeClass"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ImportElement(null, "SomeOtherClass"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ImportElement("", "YetOtherClass"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ImportElement(" ", "MyClass"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ImportElement("        ", "MyOtherClass"));
    }
    
    /**
     * Verify that the hash code is properly generated
     */
    @Test
    public void testHashCode() {
        String packageName = "my.package.com.abc123";
        String className = "MyClassName";
        Assertions.assertEquals(packageName.hashCode() + className.hashCode(), new ImportElement(packageName, className).hashCode());
    }
    
    /**
     * Verify that equals works as expected
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        ImportElement lhs = new ImportElement("a.b.c.d.E");
        
        // These are expected to fail
        Assertions.assertFalse(lhs.equals("abc123"));
        Assertions.assertFalse(lhs.equals(new ImportElement("a.b.c.D")));
        Assertions.assertFalse(lhs.equals(new ImportElement(NamedTypeElement.class)));
        Assertions.assertFalse(lhs.equals(new ImportElement("a.b.c.d.e")));
        
        // These are expected to pass
        Assertions.assertTrue(lhs.equals(new ImportElement("a.b.c.d.E")));
        Assertions.assertTrue(lhs.equals(new ImportElement("a.b.c.d", "E")));
        lhs = new ImportElement(NamedTypeElement.class);
        Assertions.assertTrue(lhs.equals(new ImportElement(NamedTypeElement.class)));
        Assertions.assertTrue(lhs.equals(new ImportElement("tendril.dom.type.NamedTypeElement")));
        Assertions.assertTrue(lhs.equals(new ImportElement("tendril.dom.type", "NamedTypeElement")));
    }
}
