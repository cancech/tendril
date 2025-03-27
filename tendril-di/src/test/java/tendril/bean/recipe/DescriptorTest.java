/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.bean.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import tendril.codegen.field.type.PrimitiveType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;
import tendril.test.bean.SingleCtorBean;
import tendril.test.recipe.Double1TestRecipe;
import tendril.test.recipe.Double2TestRecipe;
import tendril.test.recipe.IntTestRecipe;
import tendril.test.recipe.StringTestRecipe;

/**
 * Test class for {@link Descriptor}
 */
public class DescriptorTest extends AbstractUnitTest {

    // Instance to test
    private Descriptor<SingleCtorBean> descriptor;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        descriptor = new Descriptor<>(SingleCtorBean.class);
    }

    /**
     * Verify that the default values are as per expectations
     */
    @Test
    public void testDefaultValues() {
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals("", descriptor.getName());
        Assertions.assertEquals(Collections.emptySet(), descriptor.getEnumQualifiers());
    }

    /**
     * Verify that the name can be updated
     */
    @Test
    public void testUpdateName() {
        descriptor.setName("SomeBeanName");
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals("SomeBeanName", descriptor.getName());
    }

    /**
     * Verify that the name can be updated
     */
    @Test
    public void testUpdateEnumQualifiers() {
        // None by default
        Assertions.assertEquals(Collections.emptySet(), descriptor.getEnumQualifiers());
        
        // Can add one
        descriptor.addEnumQualifier(PrimitiveType.BYTE);
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals(Sets.newSet(PrimitiveType.BYTE), descriptor.getEnumQualifiers());
        
        // Can add another
        descriptor.addEnumQualifier(PrimitiveType.SHORT);
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals(Sets.newSet(PrimitiveType.BYTE, PrimitiveType.SHORT), descriptor.getEnumQualifiers());
        
        // Can add some more
        descriptor.addEnumQualifier(PrimitiveType.CHAR);
        descriptor.addEnumQualifier(PrimitiveType.BOOLEAN);
        descriptor.addEnumQualifier(PrimitiveType.INT);
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals(Sets.newSet(PrimitiveType.BYTE, PrimitiveType.SHORT, PrimitiveType.CHAR, PrimitiveType.BOOLEAN, PrimitiveType.INT), descriptor.getEnumQualifiers());
    }
    
    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        // Must be the same type
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).equals(new String("")));
        
        // So long as it's assignable, it will pass
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(Double1TestRecipe.class)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(AbstractRecipe.class)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(Object.class)));
        
        // So long as it's assignable and the exact same name, it will pass
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").equals(new Descriptor<>(Double1TestRecipe.class).setName("qwerty")));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").equals(new Descriptor<>(AbstractRecipe.class).setName("qwerty")));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").equals(new Descriptor<>(Object.class).setName("qwerty")));
        
        // Assignable but with a different name fails
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").equals(new Descriptor<>(Double1TestRecipe.class).setName("asdfg")));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").equals(new Descriptor<>(AbstractRecipe.class).setName("zxcvb")));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").equals(new Descriptor<>(Object.class)));
        
        // So long as it's assignable, has the exact same name and enum qualifiers, it will pass
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").addEnumQualifier(PrimitiveType.CHAR)
                .equals(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.BYTE)
                .equals(new Descriptor<>(AbstractRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.BYTE).addEnumQualifier(PrimitiveType.BOOLEAN)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.INT).addEnumQualifier(PrimitiveType.FLOAT).addEnumQualifier(PrimitiveType.SHORT)
                .equals(new Descriptor<>(Object.class).setName("qwerty").addEnumQualifier(PrimitiveType.SHORT).addEnumQualifier(PrimitiveType.INT).addEnumQualifier(PrimitiveType.FLOAT)));
        
        // Assignable with the exact same name but different enum qualifiers, it will fail
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").addEnumQualifier(PrimitiveType.BOOLEAN)
                .equals(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.INT)
                .equals(new Descriptor<>(AbstractRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.BYTE).addEnumQualifier(PrimitiveType.BOOLEAN)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.INT).addEnumQualifier(PrimitiveType.FLOAT).addEnumQualifier(PrimitiveType.SHORT)
                .equals(new Descriptor<>(Object.class).setName("qwerty").addEnumQualifier(PrimitiveType.SHORT).addEnumQualifier(PrimitiveType.FLOAT)));
        
        
        // If it is not assignable, it will fail
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(Double2TestRecipe.class)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(StringTestRecipe.class)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).equals(new Descriptor<>(IntTestRecipe.class)));
    }
    
    /**
     * Verify that the matching is done properly
     */
    @Test
    public void testMatchesNoNameNoEnumQualifiers() {
        // Matches that pass, if no name and in the same hierarchy
        Descriptor<Double1TestRecipe> lhs = new Descriptor<>(Double1TestRecipe.class);
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("")));
        
        // Fails if a name is applied
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123")));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("321cba")));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Object.class).setName("qwerty")));
        
        // Fails if a different type is requested
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double2TestRecipe.class)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(StringTestRecipe.class)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(IntTestRecipe.class)));
    }
    
    /**
     * Verify that the matching is done properly
     */
    @Test
    public void testMatchesWithName() {
        Descriptor<Double1TestRecipe> lhs = new Descriptor<>(Double1TestRecipe.class).setName("abc123");
        
        // Pass if the exact name is applied
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("abc123")));
        
        // Pass if no name is applied
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("")));
        
        // Fails if a different name is applied
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("ABC123")));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("321cba")));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Object.class).setName("qwerty")));
        
        // Fails if a different type is requested
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double2TestRecipe.class)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(StringTestRecipe.class)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(ArrayList.class)));
    }
    
    /**
     * Verify that the matching is done properly
     */
    @Test
    public void testMatchesWithEnumQualifiers() {
        Descriptor<Double1TestRecipe> lhs = new Descriptor<>(Double1TestRecipe.class).setName("abc123");
        lhs.addEnumQualifier(PrimitiveType.BOOLEAN);
        lhs.addEnumQualifier(PrimitiveType.BYTE);
        lhs.addEnumQualifier(PrimitiveType.CHAR);
        
        // Pass if no name and no enum qualifers
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class)));
        
        // Pass if same name and no enum qualifiers
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123")));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("abc123")));
     
        // Pass if same name and one matching enum qualifier
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BOOLEAN)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BYTE)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("abc123").addEnumQualifier(PrimitiveType.CHAR)));
     
        // Pass if same name and two matching enum qualifiers
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BYTE).addEnumQualifier(PrimitiveType.BOOLEAN)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("abc123").addEnumQualifier(PrimitiveType.CHAR).addEnumQualifier(PrimitiveType.BYTE)));
     
        // Pass if same name and all matching enum qualifiers
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR).addEnumQualifier(PrimitiveType.BYTE)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.BYTE).addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertTrue(lhs.matches(new Descriptor<>(Object.class).setName("abc123").addEnumQualifier(PrimitiveType.CHAR).addEnumQualifier(PrimitiveType.BYTE).addEnumQualifier(PrimitiveType.BOOLEAN)));
        
        // Fail if one enum qualifier isn't present on LHS
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.DOUBLE)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.FLOAT)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Object.class).setName("abc123").addEnumQualifier(PrimitiveType.INT)));

        // Fail if one enum qualifier isn't present on LHS even if other are
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.DOUBLE).addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(AbstractRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.FLOAT).addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR)));
        Assertions.assertFalse(lhs.matches(new Descriptor<>(Object.class).setName("abc123").addEnumQualifier(PrimitiveType.INT).addEnumQualifier(PrimitiveType.BOOLEAN).addEnumQualifier(PrimitiveType.CHAR)));
    }
    
    /**
     * Verify that the toString provides the full details of the bean description
     */
    @Test
    public void testToString() {
        // No name
        Descriptor<?> descriptor = new Descriptor<>(Double1TestRecipe.class);
        assertDescriptorToStringMatches(descriptor, Double1TestRecipe.class, null);
        descriptor = new Descriptor<>(StringTestRecipe.class);
        assertDescriptorToStringMatches(descriptor, StringTestRecipe.class, null);
        
        // With a concrete name
        descriptor = new Descriptor<>(Double1TestRecipe.class).setName("abc123");
        assertDescriptorToStringMatches(descriptor, Double1TestRecipe.class, "abc123");
        descriptor = new Descriptor<>(StringTestRecipe.class).setName("qwerty");
        assertDescriptorToStringMatches(descriptor, StringTestRecipe.class, "qwerty");
        
        // With one enum qualifier
        descriptor = new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.INT);
        assertDescriptorToStringMatches(descriptor, Double1TestRecipe.class, "abc123", PrimitiveType.INT);
        descriptor = new Descriptor<>(StringTestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.FLOAT);
        assertDescriptorToStringMatches(descriptor, StringTestRecipe.class, "qwerty", PrimitiveType.FLOAT);
        
        // With multiple enum qualifiers
        descriptor = new Descriptor<>(Double1TestRecipe.class).setName("abc123").addEnumQualifier(PrimitiveType.INT).addEnumQualifier(PrimitiveType.LONG).addEnumQualifier(PrimitiveType.BOOLEAN);
        assertDescriptorToStringMatches(descriptor, Double1TestRecipe.class, "abc123", PrimitiveType.INT, PrimitiveType.LONG, PrimitiveType.BOOLEAN);
        descriptor = new Descriptor<>(StringTestRecipe.class).setName("qwerty").addEnumQualifier(PrimitiveType.FLOAT).addEnumQualifier(PrimitiveType.SHORT).addEnumQualifier(PrimitiveType.CHAR);
        assertDescriptorToStringMatches(descriptor, StringTestRecipe.class, "qwerty", PrimitiveType.FLOAT, PrimitiveType.SHORT, PrimitiveType.CHAR);
    }
    
    /**
     * Verify that the Descriptor toString matches expectations
     * 
     * @param actualDescriptor {@link Descriptor} to check
     * @param recipe {@link Class} that the descriptor is describing
     * @param name {@link String} the expected name of the bean
     * @param qualifiers {@link Enum}... that are expected to be used as qualifiers
     */
    private void assertDescriptorToStringMatches(Descriptor<?> actualDescriptor, Class<?> recipe, String name, Enum<?>...qualifiers) {
        String actual = actualDescriptor.toString();
        String expected = "Bean type " + recipe.getSimpleName();
        if (name == null) {
            Assertions.assertEquals(expected, actual.toString());
            return;
        }
        
        expected += " named \"" + name + "\"";
        if (qualifiers.length == 0) {
            Assertions.assertEquals(expected, actual.toString());
            return;
        }
        
        expected += " Enum Qualifiers[";
        List<String> qualifierNames = new ArrayList<>();
        for (Enum<?> q: qualifiers)
            qualifierNames.add(q.getClass().getSimpleName() + "." + q.name());

        // Verify the "static" part
        Assertions.assertTrue(actual.startsWith(expected), "Should start with \"" + expected + "\" but was \"" + actual + "\"");
        Assertions.assertTrue(actual.endsWith("]"), "Should end with \"]\" but was \"" + actual + "\"");
        
        actual = actual.substring(expected.length(), actual.length() - 1);
        List<String> actualElements = new ArrayList<>();
        for (String element: actual.split(","))
            actualElements.add(element.strip());
        CollectionAssert.assertEquivalent(qualifierNames, actualElements);
    }
}
