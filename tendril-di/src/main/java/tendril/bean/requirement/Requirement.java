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
package tendril.bean.requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tracks what is required for a bean to be allowed to be created.
 */
public class Requirement {
    /** List of environments, all of which must be present */
    private final List<String> requiredEnvs = new ArrayList<>();
    /** List of environments groups, where at least one from each group must be present */
    private final List<List<String>> requiredOneOfEnvs = new ArrayList<>();
    /** List of environments, none of which must be present */
    private final List<String> requiredNotEnvs = new ArrayList<>();
    
    /**
     * Add an environment which must be present for the bean to be creatable
     * 
     * @param env {@link String} environment name
     */
    public void addRequiredEnvironment(String env) {
        requiredEnvs.add(env);
    }

    /**
     * Get the list of all required environments for the bean
     * 
     * @return {@link List} of {@link String}
     */
    public List<String> getRequiredEnvironments() {
        return requiredEnvs;
    }
    
    /**
     * Add a series of environments, at least one of which must be present for the bean to be creatable
     * 
     * @param envs {@link String}... names of the environments of which at least one must be present
     */
    public void addRequiredOneOfEnvironment(String... envs) {
        requiredOneOfEnvs.add(Arrays.asList(envs));
    }

    /**
     * Get all of the "one-of" groups, where at least one environment from each group must be present.
     * 
     * @return {@link List} of {@link String}
     */
    public List<List<String>> getRequiredOneOfEnvironments() {
        return requiredOneOfEnvs;
    }
    
    /**
     * Add an environment which must <b>NOT</b> be present for the bean to be creatable
     * 
     * @param env {@link String} environment name
     */
    public void addRequiredNotEnvironment(String env) {
        requiredNotEnvs.add(env);
    }

    /**
     * Get the list of all environments that cannot be present for the bean
     * 
     * @return {@link List} of {@link String}
     */
    public List<String> getRequiredNotEnvironments() {
        return requiredNotEnvs;
    }
}
