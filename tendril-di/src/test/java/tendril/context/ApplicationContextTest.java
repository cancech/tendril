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

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.processor.registration.RunnerFile;
import tendril.test.AbstractUnitTest;
import tendril.test.runner.TestTendrilRunner;
import tendril.test.runner.TestTendrilRunnerRecipe;

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
        ctx = new ApplicationContext(mockEngine);
    }
    
    /**
     * Verify that the context can be started
     */
    @Test
    public void testStart() {
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(TestTendrilRunnerRecipe.class.getName());
            Assertions.assertFalse(TestTendrilRunner.hasBeenRun());
            ctx.start();
            Assertions.assertTrue(TestTendrilRunner.hasBeenRun());
        }
        
        verify(mockEngine).init();
    }

    /**
     * Verify that the environments are properly handled
     */
    @Test
    public void testEnvironments() {
        ctx.setEnvironments("a");
        verify(mockEngine).setEnvironments("a");

        ctx.setEnvironments("a", "b", "c");
        verify(mockEngine).setEnvironments("a", "b", "c");
    }
}
