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
package tendril.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VisibilityType}
 */
public class VisibilityTypeTest {
	
	/**
	 * Verify that the items produce the correct code
	 */
	@Test
	public void testItems() {
		Assertions.assertEquals("public", VisibilityType.PUBLIC.toString());
		Assertions.assertEquals("private", VisibilityType.PRIVATE.toString());
		Assertions.assertEquals("", VisibilityType.PACKAGE_PRIVATE.toString());
		Assertions.assertEquals("protected", VisibilityType.PROTECTED.toString());
	}

}
