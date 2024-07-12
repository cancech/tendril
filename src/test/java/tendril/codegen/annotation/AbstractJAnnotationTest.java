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

import java.util.Set;

import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Helper which contains the common JAnnotation test capabilities
 */
public abstract class AbstractJAnnotationTest extends AbstractUnitTest {

	/** Test annotation which takes no parameters */
	protected static @interface TestMarkerAnnotation {
	}

	/** Test annotation which takes a default parameter */
	protected static @interface TestDefaultParamAnnotation {
		String value();
	}

	/** Test annotation which takes a multiple parameters */
	protected static @interface TestMultiParamAnnotation {
		String valStr();

		int valInt();
	}
	
	// Mocks to use for testing
	@Mock
	protected CodeBuilder mockBuilder;
	@Mock
	protected Set<ClassType> mockImportSet;
}
