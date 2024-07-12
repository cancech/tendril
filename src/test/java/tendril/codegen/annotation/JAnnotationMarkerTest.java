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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link JAnnotationMarker}
 */
public class JAnnotationMarkerTest extends AbstractJAnnotationTest {

	// Instance to test
	private JAnnotationMarker annotation;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		annotation = new JAnnotationMarker(TestMarkerAnnotation.class);
	}

	/**
	 * Verify that the code is properly generated
	 */
	@Test
	public void testCodeGeneration() {
		annotation.generateSelf(mockBuilder, mockImportSet);
		verify(mockBuilder).append("@TestMarkerAnnotation");
	}

	/**
	 * Verify that an error is generated if an annotation that is not merely a marker is employed
	 */
	@Test
	public void testInvalidAnnotations() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationMarker(TestDefaultParamAnnotation.class));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationMarker(TestMultiParamAnnotation.class));
	}
}
