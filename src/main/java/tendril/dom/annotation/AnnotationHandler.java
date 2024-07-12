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

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for providing the necessary implementation of {@link Annotatable}.
 */
public class AnnotationHandler implements Annotatable {

    /** List of annotations that have been applied to the named type */
    private List<AppliedAnnotation> annotations = new ArrayList<>();

    /**
     * @see tendril.dom.annotation.Annotatable#addAnnotation(tendril.dom.annotation.AppliedAnnotation)
     */
    @Override
    public void addAnnotation(AppliedAnnotation data) {
        annotations.add(data);
    }

    /**
     * @see tendril.dom.annotation.Annotatable#getAnnotations()
     */
    @Override
    public List<AppliedAnnotation> getAnnotations() {
        return annotations;
    }
}
