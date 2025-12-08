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
package tendril;

import java.util.List;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;

/**
 * Exception to be thrown when there is a failure to retrieve a bean
 */
public class BeanRetrievalException  extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -1120827002093381126L;

    /**
     * CTOR
     * 
     * @param desc {@link Descriptor} describing the bean that was attempted to be retrieved
     */
    public BeanRetrievalException(Descriptor<?> desc) {
        super("No matching Bean found for " + desc.toString());
    }

    /**
     * CTOR
     * 
     * @param <BEAN_TYPE> indicating the type of bean that was attempted to be retrieved
     * @param desc {@link Descriptor} describing the bean that was attempted to be retrieved
     * @param options {@link List} of {@link AbstractRecipe}s that were all matched
     * @param beanLabel {@link String} to apply as a label (adjective) on the bean when generating the exception message
     */
    public <BEAN_TYPE> BeanRetrievalException(Descriptor<BEAN_TYPE> desc, List<AbstractRecipe<BEAN_TYPE>> options, String beanLabel) {
        super(buildMultipleOptionsMessage(desc, options, beanLabel));
    }

    /**
     * Helper to assemble the message which is to be reported when multiple bean options are available
     * 
     * @param desc {@link Descriptor} of the bean that was desired
     * @param options {@link List} of {@link AbstractRecipe}s that were all matched
     * @param beanLabel {@link String} to apply as a label (adjective) on the bean when generating the exception message
     * @return {@link String} message with the full details
     */
    private static <BEAN_TYPE> String buildMultipleOptionsMessage(Descriptor<BEAN_TYPE> desc, List<AbstractRecipe<BEAN_TYPE>> options, String beanLabel) {
        StringBuilder str = new StringBuilder("Multiple " + beanLabel + " matches available for " + desc.toString() + ":");
        for (AbstractRecipe<?> opt: options)
            str.append("\n    - " + opt.getDescription());
        return str.toString();
    }
}