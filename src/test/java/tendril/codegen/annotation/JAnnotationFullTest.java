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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JValue;

/**
 * Test case for {@link JAnnotationFull}
 */
public class JAnnotationFullTest extends AbstractJAnnotationTest {

	// Mocks to use for testing
	@Mock
	private JValue<String> mockValStr;
	@Mock
	private JValue<Integer> mockValInt;

	// Instance to use for testing
	private JAnnotationFull annotation;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		annotation = new JAnnotationFull(TestMultiParamAnnotation.class, Map.of("valStr", mockValStr, "valInt", mockValInt));
	}

	/**
	 * Verify that the code is properly generated
	 */
	@Test
	public void testGenerateCode() {
		when(mockValStr.generate(mockImportSet)).thenReturn("mockValStr");
		when(mockValInt.generate(mockImportSet)).thenReturn("mockValInt");
		annotation.generateSelf(mockBuilder, mockImportSet);
		
		verify(mockValStr).generate(mockImportSet);
		verify(mockValInt).generate(mockImportSet);
		verify(mockBuilder).append("@TestMultiParamAnnotation(valInt = mockValInt, valStr = mockValStr)");
	}
	
	/**
	 * Verify that only proper Annotations can be employed with the proper parameters
	 */
	@Test
	public void testCtorValidation() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationFull(TestMarkerAnnotation.class, Map.of()));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationFull(TestDefaultParamAnnotation.class, Map.of("valStr", mockValStr)));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationFull(TestMultiParamAnnotation.class, Map.of("value", mockValStr)));
	}
}
