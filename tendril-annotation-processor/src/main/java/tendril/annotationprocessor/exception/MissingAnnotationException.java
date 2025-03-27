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
package tendril.annotationprocessor.exception;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;

/**
 * Exception which is to be thrown when an annotation fails to be resolved/found.
 */
public class MissingAnnotationException extends Exception {
    /** Serial UID */
    private static final long serialVersionUID = -7150861364083782309L;

    /** The name of the annotation which failed to resolve */
    private final String annotationName;

    /**
     * CTOR
     * 
     * @param missingType {@link DeclaredType} representing the missing annotation
     * @param appliedTo   {@link Element} to which the annotation was applied
     */
    public MissingAnnotationException(DeclaredType missingType, Element appliedTo) {
        super("Unable to find a definition for the annotation " + missingType + " applied to " + appliedTo);
        this.annotationName = missingType.asElement().getSimpleName().toString();
    }

    /**
     * Get the name of the annotation that was missing. Note that this will only contain the "simple name" rather than the fully qualified name (including package).
     * 
     * @return {@link String} missing annotation name
     */
    public String getMissingAnnotationName() {
        return annotationName;
    }
}
