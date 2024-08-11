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
package tendril.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link EnvironmentCollector}
 */
@TestMethodOrder(OrderAnnotation.class)
public class EnvironmentCollectorTest extends AbstractUnitTest {

    // Mocks to use for testing
    private RoundEnvironment mockEnv1;
    private RoundEnvironment mockEnv2;
    private RoundEnvironment mockEnv3;
    private RoundEnvironment mockEnv4;
    @Mock
    private Set<TypeElement> mockSet;

    // Instance to test
    private EnvironmentCollector collector;

    /**
     * Setup the mocks which are to be shared across all tests
     */
    @BeforeAll
    public void setupStaticMocks() {
        // Mocks are traditionally re-created with each test case, which makes tracking/comparing with the static collector environment a challenge.
        // They must be created in a "static" manner to ensure that the same instance is employed for each test
        mockEnv1 = Mockito.mock(RoundEnvironment.class);
        mockEnv2 = Mockito.mock(RoundEnvironment.class);
        mockEnv3 = Mockito.mock(RoundEnvironment.class);
        mockEnv4 = Mockito.mock(RoundEnvironment.class);
    }

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        collector = new EnvironmentCollector();

    }

    /**
     * Verify that retrieving with no environments work as expected
     */
    @Test
    @Order(1)
    public void testRetrieveNoEnvironment() {
        Assertions.assertIterableEquals(Collections.emptyList(), EnvironmentCollector.getAllEnvironments(null));
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv1), EnvironmentCollector.getAllEnvironments(mockEnv1));
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv2), EnvironmentCollector.getAllEnvironments(mockEnv2));
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv3));
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv4), EnvironmentCollector.getAllEnvironments(mockEnv4));
    }

    /**
     * Verify that a single environment can be retrieved
     */
    @Test
    @Order(2)
    public void testRetrieveSingleEnvironment() {
        collector.process(mockSet, mockEnv1);
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv1), EnvironmentCollector.getAllEnvironments(null));
        Assertions.assertIterableEquals(Collections.singletonList(mockEnv1), EnvironmentCollector.getAllEnvironments(mockEnv1));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2), EnvironmentCollector.getAllEnvironments(mockEnv2));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv3));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv4), EnvironmentCollector.getAllEnvironments(mockEnv4));
    }

    /**
     * Verify that multiple environments can be retrieved
     */
    @Test
    @Order(3)
    public void testRetrieveMultipleEnvironments() {
        collector.process(mockSet, mockEnv2);
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2), EnvironmentCollector.getAllEnvironments(null));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2), EnvironmentCollector.getAllEnvironments(mockEnv1));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2), EnvironmentCollector.getAllEnvironments(mockEnv2));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv3));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv4), EnvironmentCollector.getAllEnvironments(mockEnv4));

        collector.process(mockSet, mockEnv3);
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3), EnvironmentCollector.getAllEnvironments(null));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv1));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv2));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3), EnvironmentCollector.getAllEnvironments(mockEnv3));
        Assertions.assertIterableEquals(Arrays.asList(mockEnv1, mockEnv2, mockEnv3, mockEnv4), EnvironmentCollector.getAllEnvironments(mockEnv4));
    }
}
