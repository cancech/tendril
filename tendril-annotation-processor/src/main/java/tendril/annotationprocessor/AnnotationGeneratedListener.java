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
package tendril.annotationprocessor;

import tendril.codegen.field.type.ClassType;

/**
 * Listener which is to be used to notify when an annotation has been generated
 */
public interface AnnotationGeneratedListener {
    /**
     * Called when an annotation has been generated
     * 
     * @param annotation {@link ClassType} indicating the type of annotation generated
     */
    void annotationGenerated(ClassType annotation);
}
