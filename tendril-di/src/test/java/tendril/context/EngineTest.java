package tendril.context;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.TendrilStartupException;
import tendril.processor.registration.RunnerFile;
import tendril.test.AbstractUnitTest;
import tendril.test.runner.TestTendrilRunner1;
import tendril.test.runner.TestTendrilRunner2;
import tendril.test.runner.TestTendrilRunner3;
import tendril.test.runner.TestTendrilRunner4;
import tendril.test.runner.TestTendrilRunnerRecipe1;
import tendril.test.runner.TestTendrilRunnerRecipe2;
import tendril.test.runner.TestTendrilRunnerRecipe3;
import tendril.test.runner.TestTendrilRunnerRecipe4;

public class EngineTest extends AbstractUnitTest {
	
	private Engine engine;

	@Override
	protected void prepareTest() {
        // Ensure a clean state before running the test
        TestTendrilRunner1.reset();
        TestTendrilRunner2.reset();
        TestTendrilRunner3.reset();
        TestTendrilRunner4.reset();
        TestTendrilRunnerRecipe1.reset();
        TestTendrilRunnerRecipe2.reset();
        TestTendrilRunnerRecipe3.reset();
        TestTendrilRunnerRecipe4.reset();
		
		engine = new Engine();
	}
	
    /**
     * Verify that the context generates an exception is no runner is provided
     */
    @Test
    public void testStartNoRunner() {
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.emptyList());
            Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
        }

        Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
        Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
        Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
        Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
    }
	
    /**
     * Verify that the context generates an exception is no runner is provided
     */
    @Test
    public void testStartSingleUnavailableRunner() {
    	TestTendrilRunnerRecipe1.envAnd.add("a");
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.singletonList(TestTendrilRunnerRecipe1.class.getName()));
            Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }


    /**
     * Verify that the context can be started
     */
    @Test
    public void testStartSingleAvailableRunner() {
    	TestTendrilRunnerRecipe1.envAnd.add("a");
    	engine.addEnvironments("a");
    	
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Collections.singletonList(TestTendrilRunnerRecipe1.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            engine.start();
            Assertions.assertTrue(TestTendrilRunner1.hasBeenRun());
        }
    }

    /**
     * Verify that the context generates an exception when none of the runners do not meet requirements
     */
    @Test
    public void testStartMultipleUnavailableRunners() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("2", "4");
    	
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersOneAvailable() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("1", "2", "4");

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            engine.start();
            Assertions.assertTrue(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersTwoAvailable() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("4");

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            engine.start();
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersThreeAvailable() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("2", "3", "4");

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            engine.start();
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersFourAvailable() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("2");

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            engine.start();
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertTrue(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersTwoSeparateAvailable() {
    	TestTendrilRunnerRecipe1.envAnd.add("1");
    	TestTendrilRunnerRecipe2.envNot.add("2");
    	TestTendrilRunnerRecipe3.envOr.add("3");
    	TestTendrilRunnerRecipe4.envAnd.add("2");
    	TestTendrilRunnerRecipe4.envNot.add("4");
    	engine.addEnvironments("1", "2");

        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

    /**
     * Verify that the context works when only a single runner meets requirements
     */
    @Test
    public void testStartMultipleRunnersAllAvailable() {
        try (MockedStatic<RunnerFile> runnerFile = Mockito.mockStatic(RunnerFile.class)) {
            runnerFile.when(RunnerFile::read).thenReturn(Arrays.asList(TestTendrilRunnerRecipe1.class.getName(), TestTendrilRunnerRecipe2.class.getName(), TestTendrilRunnerRecipe3.class.getName(),
                    TestTendrilRunnerRecipe4.class.getName()));
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
            Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
            Assertions.assertFalse(TestTendrilRunner1.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner2.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner3.hasBeenRun());
            Assertions.assertFalse(TestTendrilRunner4.hasBeenRun());
        }
    }

}
