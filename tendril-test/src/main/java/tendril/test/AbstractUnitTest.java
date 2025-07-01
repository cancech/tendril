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
package tendril.test;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.listeners.MockCreationListener;

/**
 * Reusable Unit Test class which provides the necessary infrastructure to support mocks and perform the final verification that mocked interactions have been accounted for.
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractUnitTest {
    /** Mocked objects that have been created as part of the test */
    private final List<Object> mocks = new ArrayList<>();
    /** Reusable listener which is used to track the creation of mocks */
    private final MockCreationListener mockListener = (mock, settings) -> mocks.add(mock);

    /**
     * CTOR
     */
    protected AbstractUnitTest() {
    }

    /**
     * Before performing any/all tests, listen for the creation of mocks
     */
    @BeforeAll
    protected void setupMockListener() {
        Mockito.framework().addListener(mockListener);
    }

    /**
     * Upon termination of all tests, stop listening for the creation of mocks
     */
    @AfterAll
    protected void cleanupMockListener() {
        Mockito.framework().removeListener(mockListener);
    }

    /**
     * Perform the initial setup for each test, ensuring that mocked interactions have been accounted for
     */
    @BeforeEach
    public void setup() {
        prepareTest();
        verifyAllChecked();
    }

    /**
     * Perform the necessary setup before launching each specific test case.
     */
    protected abstract void prepareTest();

    /**
     * Perform the necessary tear down and clean up operations at the completion of each specific test case. This includes verifying that mocked operations have been accounted for, and preparing for
     * the next test case.
     */
    @AfterEach
    public void tearDown() {
        verifyAllChecked();
        cleanupTest();
        verifyAllChecked();
        mocks.clear();
    }

    /**
     * Perform the necessary tear down and clean up operations at the conclusion of the test case.
     */
    protected void cleanupTest() {
    }

    /**
     * Verify that all mocked interactions have been accounted for
     */
    protected void verifyAllChecked() {
        if (!mocks.isEmpty())
            verifyNoMoreInteractions(mocks.toArray());
    }

}
