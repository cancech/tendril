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
    /** List of options, all of which must be present */
    private final List<String> required = new ArrayList<>();
    /** List of options groups, where at least one from each group must be present */
    private final List<List<String>> requiredOneOf = new ArrayList<>();
    /** List of options, none of which must be present */
    private final List<String> requiredNot = new ArrayList<>();
    
    /**
     * CTOR
     */
    public Requirement() {
    }
    
    /**
     * Add an value which must be present for the bean to be creatable
     * 
     * @param name {@link String} name
     */
    public void addRequired(String name) {
        required.add(name);
    }

    /**
     * Get the list of all required options for the bean
     * 
     * @return {@link List} of {@link String}s
     */
    public List<String> getRequired() {
        return required;
    }
    
    /**
     * Add a series of options, at least one of which must be present for the bean to be creatable
     * 
     * @param names {@link String}... names of the options of which at least one must be present
     */
    public void addRequiredOneOf(String... names) {
        requiredOneOf.add(Arrays.asList(names));
    }

    /**
     * Get all of the "one-of" groups, where at least one option from each group must be present.
     * 
     * @return {@link List} of {@link List}s {@link String}s
     */
    public List<List<String>> getRequiredOneOf() {
        return requiredOneOf;
    }
    
    /**
     * Add an option which must <b>NOT</b> be present for the bean to be creatable
     * 
     * @param name {@link String} environment name
     */
    public void addRequiredNot(String name) {
        requiredNot.add(name);
    }

    /**
     * Get the list of all options that cannot be present for the bean
     * 
     * @return {@link List} of {@link String}s
     */
    public List<String> getRequiredNot() {
        return requiredNot;
    }
}
