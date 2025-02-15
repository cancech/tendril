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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.test.AbstractUnitTest;
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
     * Verify that equality is properly determined
     */
    @Test
    public void testEquals() {
        // So long as it's assignable, it will pass
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(Double1TestRecipe.class)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(AbstractRecipe.class)));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(Object.class)));
        
        // So long as it's assignable and the exact same name, it will pass
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").matches(new Descriptor<>(Double1TestRecipe.class).setName("qwerty")));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").matches(new Descriptor<>(AbstractRecipe.class).setName("qwerty")));
        Assertions.assertTrue(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").matches(new Descriptor<>(Object.class).setName("qwerty")));
        
        // Assignable but with a different name fails
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").setName("qwerty").matches(new Descriptor<>(Double2TestRecipe.class).setName("qwerty")));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").matches(new Descriptor<>(StringTestRecipe.class).setName("qwerty")));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).setName("qwerty").matches(new Descriptor<>(IntTestRecipe.class).setName("qwerty")));
        
        // If it is not assignable, it will fail
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(Double2TestRecipe.class)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(StringTestRecipe.class)));
        Assertions.assertFalse(new Descriptor<>(Double1TestRecipe.class).matches(new Descriptor<>(IntTestRecipe.class)));
    }
    
    /**
     * Verify that the matching is done properly
     */
    @Test
    public void testMatchesNoName() {
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
        // Matches that pass, if no name and in the same hierarchy
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
     * Verify that the toString provides the full details of the bean description
     */
    @Test
    public void testToString() {
        // No name
        Assertions.assertEquals("Bean type " + Double1TestRecipe.class.getSimpleName(), new Descriptor<>(Double1TestRecipe.class).toString());
        Assertions.assertEquals("Bean type " + StringTestRecipe.class.getSimpleName(), new Descriptor<>(StringTestRecipe.class).toString());
        
        // With a concrete name
        Assertions.assertEquals("Bean type " + Double1TestRecipe.class.getSimpleName() + " named \"abc123\"", new Descriptor<>(Double1TestRecipe.class).setName("abc123").toString());
        Assertions.assertEquals("Bean type " + StringTestRecipe.class.getSimpleName() + " named \"qwerty\"", new Descriptor<>(StringTestRecipe.class).setName("qwerty").toString());
    }
}
