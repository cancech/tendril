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
package tendril.codegen.annotation;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JValue;
import test.assertions.ClassAssert;

/**
 * Test case for {@link JAnnotationFactory}
 */
public class JAnnotationFactoryTest extends AbstractJAnnotationTest{

	// Mocks to use for testing
	@Mock
	private JValue<String> mockValue;
	
	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		// Nothing to do...
	}

	/**
	 * Verify that the factory produces the expected instances
	 */
	@Test
	public void testFactoryMethods() {
		ClassAssert.assertInstance(JAnnotationMarker.class, JAnnotationFactory.create(TestMarkerAnnotation.class));
		ClassAssert.assertInstance(JAnnotationDefaultValue.class, JAnnotationFactory.create(TestDefaultParamAnnotation.class, mockValue));
		ClassAssert.assertInstance(JAnnotationFull.class, JAnnotationFactory.create(TestMultiParamAnnotation.class, Map.of()));
	}
}
