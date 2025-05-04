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
package tendril.bean.recipe;

import java.util.HashSet;
import java.util.Set;

import tendril.context.ApplicationContext;
import tendril.context.Engine;
import tendril.util.TendrilStringUtil;

/**
 * Description of a bean that is (expected to be) available within the {@link ApplicationContext} and accessible via its {@link Engine}.
 * 
 * @param <BEAN_TYPE> the type of bean that the {@link Descriptor} describes
 */
public class Descriptor<BEAN_TYPE> {
    
    /** The {@link Class} of the bean */
    private final Class<BEAN_TYPE> beanClass;
    /** The name of the bean */
    private String name = "";
    /** List of enums that have been applied as qualifiers on the bean */
    private Set<Enum<?>> enumQualifiers = new HashSet<>();
    /** List of qualifiers that have been applied to the bean */
    private Set<Class<?>> qualifiers = new HashSet<>();
    
    /**
     * CTOR
     * 
     * @param beanClass {@link Class} of the bean that is described
     */
    public Descriptor(Class<BEAN_TYPE> beanClass) {
        this.beanClass = beanClass;
    }
    
    /**
     * Get the {@link Class} of the described bean
     * 
     * @return {@link Class}
     */
    public Class<BEAN_TYPE> getBeanClass() {
        return beanClass;
    }
    
    /**
     * Set the name of the described bean
     * 
     * @param name {@link String} of the bean
     * 
     * @return {@link Descriptor} describing the bean
     */
    public Descriptor<BEAN_TYPE> setName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Get the name of the bean.
     * 
     * @return {@link String} name of the bean
     */
    String getName() {
        return name;
    }
    
    /**
     * Add a qualifying Enum to the bean
     * 
     * @param qualifier {@link Enum} which is used to describe/find the bean
     * @return {@link Descriptor} describing the bean
     */
    public Descriptor<BEAN_TYPE> addEnumQualifier(Enum<?> qualifier) {
        this.enumQualifiers.add(qualifier);
        return this;
    }
    
    /**
     * Get all qualifying {@link Enum}s for the bean
     * @return {@link Set} of {@link Enum}s which describe/qualify a bean
     */
    Set<Enum<?>> getEnumQualifiers() {
        return enumQualifiers;
    }
    
    /**
     * Add a qualifier to the bean
     * 
     * @param qualifier {@link Class} which is used to describe/find the bean
     * @return {@link Descriptor} describing the bean
     */
    public Descriptor<BEAN_TYPE> addQualifier(Class<?> qualifier) {
        this.qualifiers.add(qualifier);
        return this;
    }
    
    /**
     * Get all qualifying {@link Class}s for the bean
     * @return {@link Set} of {@link Class}s which describe/qualify a bean
     */
    Set<Class<?>> getQualifiers() {
        return qualifiers;
    }
    
    /**
     * For the purpose of equality, the defined class need not be 100% equal, so long as the other class is assignable from this one.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Descriptor))
            return false;

        Descriptor<?> other = (Descriptor<?>) obj;
        
        return other.beanClass.isAssignableFrom(beanClass) && other.name.equals(name) && other.enumQualifiers.size() == enumQualifiers.size() &&
                enumQualifiers.containsAll(other.enumQualifiers) && qualifiers.containsAll(other.qualifiers);
    }

    /**
     * Perform a matching to check whether this description matches the other. This is not the same as equals (where all values are expected to be identical), but rather
     * a comparison where all of the features described in the other must match this. There may be other features described in this which are not present in the other, but
     * not vice-versa. Note that for the purpose of the matching a "blank" name is deemed "unset", ergo a blank name from the other will match a concrete name on this.
     * 
     * @param other {@link Descriptor} to perform the matching against
     * 
     * @return boolean true if the 
     */
    public boolean matches(Descriptor<?> other) {
        if (!other.beanClass.isAssignableFrom(beanClass))
            return false;
        
        if (!other.name.isBlank() && !other.name.equals(name))
            return false;
        
        if (!other.enumQualifiers.isEmpty() && !enumQualifiers.containsAll(other.enumQualifiers))
            return false;
        
        if (!other.qualifiers.isEmpty() && !qualifiers.containsAll(other.qualifiers))
            return false;
        
        return true;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Bean type " + beanClass.getSimpleName());
        
        if (!name.isEmpty())
            str.append(" named \"" + name + "\"");
        if (!enumQualifiers.isEmpty()) {
            str.append(" Enum Qualifiers[");
            str.append(TendrilStringUtil.join(enumQualifiers, e -> e.getClass().getSimpleName() + "." + e.name()));
            str.append("]");
        }
        if (!qualifiers.isEmpty()) {
            str.append(" Qualifiers[");
            str.append(TendrilStringUtil.join(qualifiers, e -> "@" + e.getSimpleName()));
            str.append("]");
        }
        
        return str.toString();
    }
}
