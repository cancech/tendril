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
package tendril.bean.requirement;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import tendril.test.assertions.CollectionAssert;

/**
 * Test case for {@link Requirement}
 */
public class RequirementTest {

    /**
     * Verify that the required are properly tracked
     */
    @Test
    public void testNoRequired() {
        Requirement req = new Requirement();
        CollectionAssert.assertEmpty(req.getRequired());
        CollectionAssert.assertEmpty(req.getRequiredNot());
    }

    /**
     * Verify that the required are properly tracked
     */
    @Test
    public void testSingleRequired() {
        Requirement req = new Requirement();
        req.addRequired("qwerty");
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequired());
        CollectionAssert.assertEmpty(req.getRequiredNot());
        CollectionAssert.assertEmpty(req.getRequiredOneOf());
    }

    /**
     * Verify that the not required are properly tracked
     */
    @Test
    public void testSingleNotRequired() {
        Requirement req = new Requirement();
        req.addRequiredNot("qwerty");
        CollectionAssert.assertEmpty(req.getRequired());
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequiredNot());
        CollectionAssert.assertEmpty(req.getRequiredOneOf());
    }
    
    /**
     * Verify that the one-of required are properly tracked
     */
    @Test
    public void testSingleOneOfRequirement() {
        Requirement req = new Requirement();
        req.addRequiredOneOf("a", "b", "c");
        CollectionAssert.assertEmpty(req.getRequired());
        CollectionAssert.assertEmpty(req.getRequiredNot());
        CollectionAssert.assertEquals(Collections.singletonList(Arrays.asList("a", "b", "c")), req.getRequiredOneOf());
    }

    /**
     * Verify that the not and required are properly tracked
     */
    @Test
    public void testSingleNotAndRequired() {
        Requirement req = new Requirement();
        req.addRequired("qwerty");
        req.addRequiredNot("abc123");
        req.addRequiredOneOf("a", "b", "c");
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequired());
        CollectionAssert.assertEquals(Collections.singletonList("abc123"), req.getRequiredNot());
        CollectionAssert.assertEquals(Collections.singletonList(Arrays.asList("a", "b", "c")), req.getRequiredOneOf());
    }

    /**
     * Verify that the required are properly tracked
     */
    @Test
    public void testMultipleRequired() {
        Requirement req = new Requirement();
        req.addRequired("a");
        req.addRequired("b");
        req.addRequired("c");
        req.addRequired("d");
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequired());
        CollectionAssert.assertEmpty(req.getRequiredNot());
        CollectionAssert.assertEmpty(req.getRequiredOneOf());
    }
    
    /**
     * Verify that the not required are properly tracked
     */
    @Test
    public void testMultipleRequiredNot() {
        Requirement req = new Requirement();
        req.addRequiredNot("a");
        req.addRequiredNot("b");
        req.addRequiredNot("c");
        req.addRequiredNot("d");
        CollectionAssert.assertEmpty(req.getRequired());
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequiredNot());
        CollectionAssert.assertEmpty(req.getRequiredOneOf());
    }
    
    /**
     * Verify that the one-of required are properly tracked
     */
    @Test
    public void testMultipleOneOfRequirement() {
        Requirement req = new Requirement();
        req.addRequiredOneOf("a", "b", "c");
        req.addRequiredOneOf("d", "e", "f");
        req.addRequiredOneOf("g", "h", "i");
        req.addRequiredOneOf("j", "k", "l");
        CollectionAssert.assertEmpty(req.getRequired());
        CollectionAssert.assertEmpty(req.getRequiredNot());
        CollectionAssert.assertEquals(Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"), Arrays.asList("g", "h", "i"), Arrays.asList("j", "k", "l")), req.getRequiredOneOf());
    }

    /**
     * Verify that the required are properly tracked
     */
    @Test
    public void testMultipleNotAndRequired() {
        Requirement req = new Requirement();
        req.addRequired("a");
        req.addRequired("b");
        req.addRequired("c");
        req.addRequired("d");
        req.addRequiredNot("e");
        req.addRequiredNot("f");
        req.addRequiredNot("g");
        req.addRequiredNot("h");
        req.addRequiredOneOf("a", "b", "c");
        req.addRequiredOneOf("d", "e", "f");
        req.addRequiredOneOf("g", "h", "i");
        req.addRequiredOneOf("j", "k", "l");
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequired());
        CollectionAssert.assertEquals(Arrays.asList("e", "f", "g", "h"), req.getRequiredNot());
        CollectionAssert.assertEquals(Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"), Arrays.asList("g", "h", "i"), Arrays.asList("j", "k", "l")), req.getRequiredOneOf());
    }
}
