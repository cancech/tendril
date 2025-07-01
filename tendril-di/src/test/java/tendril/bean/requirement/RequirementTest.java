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
     * Verify that the required environments are properly tracked
     */
    @Test
    public void testNoRequiredEnvironments() {
        Requirement req = new Requirement();
        CollectionAssert.assertEmpty(req.getRequiredEnvironments());
        CollectionAssert.assertEmpty(req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the required environments are properly tracked
     */
    @Test
    public void testSingleRequiredEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredEnvironment("qwerty");
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequiredEnvironments());
        CollectionAssert.assertEmpty(req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the not required environments are properly tracked
     */
    @Test
    public void testSingleNotRequiredEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredNotEnvironment("qwerty");
        CollectionAssert.assertEmpty(req.getRequiredEnvironments());
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the not and required environments are properly tracked
     */
    @Test
    public void testSingleNotAndRequiredEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredEnvironment("qwerty");
        req.addRequiredNotEnvironment("abc123");
        CollectionAssert.assertEquals(Collections.singletonList("qwerty"), req.getRequiredEnvironments());
        CollectionAssert.assertEquals(Collections.singletonList("abc123"), req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the required environments are properly tracked
     */
    @Test
    public void testMultipleRequiredEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredEnvironment("a");
        req.addRequiredEnvironment("b");
        req.addRequiredEnvironment("c");
        req.addRequiredEnvironment("d");
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequiredEnvironments());
        CollectionAssert.assertEmpty(req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the not required environments are properly tracked
     */
    @Test
    public void testMultipleRequiredNotEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredNotEnvironment("a");
        req.addRequiredNotEnvironment("b");
        req.addRequiredNotEnvironment("c");
        req.addRequiredNotEnvironment("d");
        CollectionAssert.assertEmpty(req.getRequiredEnvironments());
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequiredNotEnvironments());
    }

    /**
     * Verify that the required environments are properly tracked
     */
    @Test
    public void testMultipleNotAndRequiredEnvironments() {
        Requirement req = new Requirement();
        req.addRequiredEnvironment("a");
        req.addRequiredEnvironment("b");
        req.addRequiredEnvironment("c");
        req.addRequiredEnvironment("d");
        req.addRequiredNotEnvironment("e");
        req.addRequiredNotEnvironment("f");
        req.addRequiredNotEnvironment("g");
        req.addRequiredNotEnvironment("h");
        CollectionAssert.assertEquals(Arrays.asList("a", "b", "c", "d"), req.getRequiredEnvironments());
        CollectionAssert.assertEquals(Arrays.asList("e", "f", "g", "h"), req.getRequiredNotEnvironments());
    }
}
