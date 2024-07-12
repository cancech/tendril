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
package tendril.dom.annotation;

import java.util.List;

/**
 * Represents an element which can be annotated
 */
public interface Annotatable {
    /**
     * Add an annotation instance to the element
     * 
     * @param data {@link AppliedAnnotation} with the details of the annotation applied to the element
     */
    void addAnnotation(AppliedAnnotation data);

    /**
     * Get all annotations applied to the element
     * 
     * @return {@link List} of {@link AppliedAnnotation} instances applied to the element
     */
    List<AppliedAnnotation> getAnnotations();
}
