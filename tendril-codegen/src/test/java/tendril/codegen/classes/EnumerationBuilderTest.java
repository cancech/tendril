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
package tendril.codegen.classes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link EnumerationBuilder}
 */
public class EnumerationBuilderTest extends AbstractUnitTest {

    // Mocks to test
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private JValue<?, ?> mockValue1;
    @Mock
    private JValue<?, ?> mockValue2;
    @Mock
    private JValue<?, ?> mockValue3;
    @Mock
    private JValue<?, ?> mockValue4;
    @Mock
    private JValue<?, ?> mockValue5;
    
    // Instance to test
    private EnumerationBuilder builder;
    
    private List<JValue<?, ?>> expectedParams;
    private boolean wasVerified = false;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        wasVerified = false;
        builder = new EnumerationBuilder(mockClassBuilder, "EnumName");
        
        doAnswer((invocation) -> { 
            verifyEnumeration((EnumerationEntry) invocation.getArgument(0));
            return null;
        }).when(mockClassBuilder).add(any(EnumerationEntry.class));
    }
    
    /**
     * @see tendril.test.AbstractUnitTest#cleanupTest()
     */
    @Override
    protected void cleanupTest() {
        Assertions.assertTrue(wasVerified);
    }

    /**
     * Verify that an enum without any parameters can be created
     */
    @Test
    public void testBuildNoParams() {
        expectedParams = Collections.emptyList();
        builder.build();
    }

    /**
     * Verify that an enum with a single parameter can be created
     */
    @Test
    public void testBuildSingleParam() {
        expectedParams = Collections.singletonList(mockValue1);
        builder.addParameter(mockValue1);
        builder.build();
    }

    /**
     * Verify that an enum with multiple parameters can be created
     */
    @Test
    public void testBuildMultipleParams() {
        expectedParams = Arrays.asList(mockValue1, mockValue2, mockValue3, mockValue4, mockValue5);
        builder.addParameter(mockValue1);
        builder.addParameter(mockValue2, mockValue3, mockValue4);
        builder.addParameter(mockValue5);
        builder.build();
    }
    
    /**
     * Helper to verify that the proper entry was created
     * 
     * @param actual {@link EnumerationEntry} that was created
     */
    private void verifyEnumeration(EnumerationEntry actual) {
        Assertions.assertEquals("EnumName", actual.getName());
        Assertions.assertIterableEquals(expectedParams, actual.getParameters());
        wasVerified = true;
    }
}
