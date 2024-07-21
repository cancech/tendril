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

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.exceptions.verification.NoInteractionsWanted;

/**
 * Test case for {@link AbstractUnitTest}
 */
public class AbstractUnitTestWithMocksTest extends AbstractUnitTest {

    // Counters to use for testing
    private int timesPrepareCalled = 0;
    private int timesTestCalled = 0;
    private int timesCleanupCalled = 0;
    private int timesVerifyAllCalled = 0;

    // Mocks to use for testing
    @Mock
    private Comparable<String> mockObject;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        verifyTimesCalled(0, 0, 0, 0);
        timesPrepareCalled++;
    }

    /**
     * Verify that mocks are properly tracked/registered and verifyAllChecked() is verifying them
     */
    @Test
    public void testMocksArePropertlyVerified() {
        verifyTimesCalled(1, 0, 0, 1);
        timesTestCalled++;

        super.verifyAllChecked();
        mockObject.compareTo("abc123");
        Assertions.assertThrows(NoInteractionsWanted.class, () -> super.verifyAllChecked());
        verify(mockObject).compareTo("abc123");
        super.verifyAllChecked();
    }

    /**
     * @see tendril.test.AbstractUnitTest#cleanupTest()
     */
    @Override
    protected void cleanupTest() {
        super.cleanupTest();
        verifyTimesCalled(1, 1, 0, 2);
        timesCleanupCalled++;
        
    }

    /**
     * @see tendril.test.AbstractUnitTest#tearDown()
     */
    @AfterEach
    @Override
    public void tearDown() {
        super.tearDown();
        verifyTimesCalled(1, 1, 1, 3);
    }

    /**
     * @see tendril.test.AbstractUnitTest#verifyAllChecked()
     */
    @Override
    protected void verifyAllChecked() {
        super.verifyAllChecked();
        timesVerifyAllCalled++;
    }

    /**
     * Verify that the overridden methods are called the expected number of times.
     * 
     * @param expectedPrepare   int times prepareTest is expected to be called
     * @param expectedTest      int times the test is expected to be called
     * @param expectedCleanup   int times cleaupTest is expected to be called
     * @param expectedVerifyAll int times the verifyAllChecked is expected to be called
     */
    private void verifyTimesCalled(int expectedPrepare, int expectedTest, int expectedCleanup, int expectedVerifyAll) {
        Assertions.assertEquals(expectedPrepare, timesPrepareCalled);
        Assertions.assertEquals(expectedTest, timesTestCalled);
        Assertions.assertEquals(expectedCleanup, timesCleanupCalled);
        Assertions.assertEquals(expectedVerifyAll, timesVerifyAllCalled);

    }

}
