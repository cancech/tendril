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

import tendril.bean.duplicate.Blueprint;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link ApplicationContextBuilder}
 */
public class ApplicationContextBuilderTest extends AbstractUnitTest {
	
	/**
	 * Blueprint enum to use for testing. Should cause an exception when added as a dynamic blueprint
	 */
	private enum EnumBlueprint implements Blueprint {
		COPY_1, COPY_2;
		
		@Override
		public String getName() {
			return name();
		}
		
	}

    // Mocks to use for testing
    @Mock
    private Engine mockEngine;
    @Mock
    private Blueprint mockBlueprint1;
    @Mock
    private Blueprint mockBlueprint2;
    @Mock
    private Blueprint mockBlueprint3;

    // Instance to test
    private ApplicationContextBuilder ctx;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        ctx = new ApplicationContextBuilder(mockEngine);
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
    
    /**
     * Verify that dynamic blueprints are properly handled
     */
    @Test
    public void testAddBlueprints() {
    	// Can add one blueprint
    	ctx.addBlueprint(mockBlueprint1);
    	verify(mockEngine).addBlueprint(mockBlueprint1);
    	
    	// Can add enum based blueprints
    	for (EnumBlueprint b: EnumBlueprint.values()) {
    		ctx.addBlueprint(b);
        	verify(mockEngine).addBlueprint(b);
    	}

    	// Can add a second blueprint
    	ctx.addBlueprint(mockBlueprint2);
    	verify(mockEngine).addBlueprint(mockBlueprint2);

    	// Can add a third blueprint
    	ctx.addBlueprint(mockBlueprint3);
    	verify(mockEngine).addBlueprint(mockBlueprint3);
    }
    
    /**
     * Verify that the context can be built
     */
    @Test
    public void testBuild() {
    	Assertions.assertEquals(mockEngine, ctx.build());
    	verify(mockEngine).init();
    }
}
