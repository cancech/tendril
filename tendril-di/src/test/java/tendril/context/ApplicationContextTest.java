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
package tendril.context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.TendrilStartupException;
import tendril.processor.registration.RunnerFile;
import tendril.test.AbstractUnitTest;
import tendril.test.runner.TestTendrilRunner;
import tendril.test.runner.TestTendrilRunner2;
import tendril.test.runner.TestTendrilRunner3;
import tendril.test.runner.TestTendrilRunner4;
import tendril.test.runner.TestTendrilRunnerRecipe;
import tendril.test.runner.TestTendrilRunnerRecipe2;
import tendril.test.runner.TestTendrilRunnerRecipe3;
import tendril.test.runner.TestTendrilRunnerRecipe4;

/**
 * Test case for the {@link ApplicationContext}
 */
public class ApplicationContextTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Engine mockEngine;

    // Instance to test
    private ApplicationContext ctx;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Ensure a clean state before running the test
        TestTendrilRunner.reset();
        TestTendrilRunner2.reset();
        TestTendrilRunner3.reset();
        TestTendrilRunner4.reset();
        
        ctx = new ApplicationContext(mockEngine);
    }

    /**
     * Verify that the context generates an exception is no runner is provided
     */
    @Test
    public void testStartNoRunner() {
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.emptyList());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> ctx.start());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context generates an exception when a single runner does not meet requirements
     */
    @Test
    public void testStartSingleUnavailableRunner() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.singletonList(TestTendrilRunnerRecipe.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> ctx.start());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context can be started
     */
    @Test
    public void testStartSingleAvailableRunner() {
        when(mockEngine.requirementsMet(any())).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.singletonList(TestTendrilRunnerRecipe.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            ctx.start();
            Assertions.assertTrue(TestTendrilRunner.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context generates an exception when none of the runners do not meet requirements
     */
    @Test
    public void testStartMultipleUnavailableRunners() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> ctx.start());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersOneAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe.class))).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            ctx.start();
            Assertions.assertTrue(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersTwoAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe2.class))).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            ctx.start();
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersThreeAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe3.class))).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            ctx.start();
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersFourAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe4.class))).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            ctx.start();
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersTwoSeparateAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(false);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe3.class))).thenReturn(true);
        when(mockEngine.requirementsMet(any(TestTendrilRunnerRecipe4.class))).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> ctx.start());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersAllAvailable() {
        when(mockEngine.requirementsMet(any())).thenReturn(true);

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> ctx.start());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }

        verify(mockEngine).init();
    }

    /**
     * Verify that the environments are properly handled
     */
    @Test
    public void testEnvironments() {
        ctx.setEnvironments("a");
        verify(mockEngine).addEnvironments("a");

        ctx.setEnvironments("a", "b", "c");
        verify(mockEngine).addEnvironments("a", "b", "c");
    }
}
