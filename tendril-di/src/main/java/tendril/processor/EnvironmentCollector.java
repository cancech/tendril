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
package tendril.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

/**
 * Annotation process which is to be triggered with every round of annotation processing and which tracks all of the encountered environments. These environments can then be used by other processors
 * for the purpose of "backtracking" and re-processing previously seen/process classes (i.e.: {@link RoundEnvironment}s). This is to be used for the purpose of allowing generated annotations to be
 * processed and allowing these annotations to work across the entire code base, rather than just the most recently generated code.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class EnvironmentCollector extends AbstractProcessor {
    /** List of all environments that have been encountered thus far */
    private static List<RoundEnvironment> environments = new ArrayList<>();

    /**
     * Get a listing of all environments that have been encountered thus far. The current environment is provided and added into the returned list, in case the collector has not been yet triggered for
     * the current round. There is no guarantee the order in which the various processors are executed, thus the environment for the current round may or may not have already been incorporated.
     * 
     * @param currentEnv {@link RoundEnvironment} from the current round. If null, it will not be processed and only all known environments will be returned.
     * @return {@link List} of {@link RoundEnvironment} which includes the environments from all rounds, including the current one
     */
    public static List<RoundEnvironment> getAllEnvironments(RoundEnvironment currentEnv) {
        List<RoundEnvironment> combined = new ArrayList<>(environments);
        if (currentEnv != null && !combined.contains(currentEnv))
            combined.add(currentEnv);
        return combined;
    }

    /**
     * Adds the {@link RoundEnvironment} for the current round to the list of known environments. Performs no processing otherwise.
     * 
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        environments.add(roundEnv);
        return false;
    }

}
