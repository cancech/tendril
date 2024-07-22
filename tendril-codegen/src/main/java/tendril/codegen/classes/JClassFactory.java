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
package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.util.TendrilUtil;

/**
 * Factory for creating {@link JClass} representations of distinct types of classes
 */
public abstract class JClassFactory {

    /**
     * Hidden CTOR
     */
    private JClassFactory() {
    }

	/**
	 * Create a default (non-abstract, concrete) class
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createClass(VisibilityType visibility, ClassType data) {
		return new JClassDefault(visibility, data);
	}

	/**
	 * Create an abstract class
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createAbstractClass(VisibilityType visibility, ClassType data) {
		return new JClassAbstract(visibility, data);
	}

	/**
	 * Create an interface
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createInterface(VisibilityType visibility, ClassType data) {
	    validateForInterface(visibility);
		return new JClassInterface(visibility, data);
	}
	
	/**
	 * Verify that the {@link VisibilityType} specified is valid for an interface
	 * 
	 * @param visibility {@link VisibilityType} desired
	 */
	private static void validateForInterface(VisibilityType visibility) {
	    if (TendrilUtil.oneOfMany(visibility, VisibilityType.PROTECTED, VisibilityType.PRIVATE))
	        throw new IllegalArgumentException("Illegal visibility " + visibility.name() + ". Only PUBLIC and PACKAGE_PRIVATE are allowed");
	}

	/**
	 * Create an annotation
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createAnnotation(VisibilityType visibility, ClassType data) {
        validateForInterface(visibility);
		return new JClassAnnotation(visibility, data);
	}
}
