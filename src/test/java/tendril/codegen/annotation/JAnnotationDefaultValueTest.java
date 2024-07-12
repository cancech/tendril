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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JValue;

/**
 * Test case for {@link JAnnotationDefaultValue}
 */
public class JAnnotationDefaultValueTest extends AbstractJAnnotationTest {

	// Mocks to use for testing
	@Mock
	private JValue<String> mockValue;
	
	// Instance to test
	private JAnnotationDefaultValue annotation;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		annotation = new JAnnotationDefaultValue(TestDefaultParamAnnotation.class, mockValue);
	}
	
	/**
	 * Verify that the appropriate code is generated
	 */
	@Test
	public void testGenerate() {
		when(mockValue.generate(mockImportSet)).thenReturn("annotationValue");
		annotation.generateSelf(mockBuilder, mockImportSet);
		verify(mockBuilder).append("@TestDefaultParamAnnotation(annotationValue)");
	}
	
	/**
	 * Ensure that only an annotation with a single default value is accepted
	 */
	@Test
	public void testInvalidAnnotations() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationDefaultValue(TestMarkerAnnotation.class, mockValue));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationDefaultValue(TestMultiParamAnnotation.class, mockValue));
	}

}
