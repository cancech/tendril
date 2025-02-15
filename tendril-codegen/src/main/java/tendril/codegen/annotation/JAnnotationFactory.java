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
package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.InterfaceMethodBuilder;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;
import tendril.util.TendrilStringUtil;

/**
 * Factory to facilitate the creation of {@link JAnnotation}s
 */
public abstract class JAnnotationFactory {

    /** Logger for creating log messages when running */
    private static Logger LOGGER = Logger.getLogger(JAnnotationFactory.class.getSimpleName());

    /**
     * Hidden CTOR
     */
    private JAnnotationFactory() {
    }

    /**
     * Create a new marker annotation
     * 
     * @param annotationClass {@link Class} extending {@link Annotation} where the annotation is defined
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(Class<? extends Annotation> annotationClass) {
        return createMarker(annotationClass, new ClassType(annotationClass));
    }

    /**
     * Create a new marker annotation
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the annotation
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String fullyQualifiedName) {
        return create(new ClassType(fullyQualifiedName));
    }

    /**
     * Create a new marker annotation
     * 
     * @param packageName {@link String} the name of the package where the annotation resides
     * @param className   {@link String} the name of the annotation
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String packageName, String className) {
        return create(new ClassType(packageName, className));
    }

    /**
     * Create a new marker annotation
     * 
     * @param annotationClass {@link ClassType} representing the annotation
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(ClassType annotationClass) {
        return createMarker(fromType(annotationClass), annotationClass);
    }

    /**
     * Create the appropriate {@link Class} for the given {@link ClassType}
     * 
     * @param classType {@link ClassType} representing the annotation
     * @return {@link Class} where the annotation is defined, or null if no such class is found
     */
    private static Class<?> fromType(ClassType classType) {
        try {
            Class<?> klass = Class.forName(classType.getFullyQualifiedName());
            if (!klass.isAnnotation())
                throw new DefinitionException(classType, "Cannot be used as an Annotation as it is not an Annotation");
            return klass;
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Cannot find Class for " + classType.getFullyQualifiedName() + ". It does not exist on the classpath");
        }

        return null;
    }

    /**
     * Create a marker {@link JAnnotation}, throwing an {@link DefinitionException} if the annotation has any actual attributes
     * 
     * @param annotationClass {@link Class} which defined the annotation itself
     * @param classType       {@link ClassType} representing the class
     * @return {@link JAnnotation}
     */
    private static JAnnotation createMarker(Class<?> annotationClass, ClassType classType) {
        if (annotationClass != null)
            checkUnsatisfiedAttributes(annotationClass, Collections.emptyList());

        return new JAnnotation(classType);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link Class} extending {@link Annotation} where the annotation is defined
     * @param value           {@link JValue} to assign to the default attribute
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(Class<? extends Annotation> annotationClass, JValue<?, ?> value) {
        return createDefaultValue(annotationClass, new ClassType(annotationClass), value);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the annotation
     * @param value              {@link JValue} to assign to the default attribute
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String fullyQualifiedName, JValue<?, ?> value) {
        return create(new ClassType(fullyQualifiedName), value);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param packageName {@link String} the name of the package where the annotation resides
     * @param className   {@link String} the name of the annotation
     * @param value       {@link JValue} to assign to the default attribute
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String packageName, String className, JValue<?, ?> value) {
        return create(new ClassType(packageName, className), value);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link ClassType} representing the annotation
     * @param value           {@link JValue} to assign to the default attribute
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(ClassType annotationClass, JValue<?, ?> value) {
        return createDefaultValue(fromType(annotationClass), annotationClass, value);
    }

    /**
     * Create an annotation which contains a single default value
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param annotationType  {@link ClassType} representing the class
     * @param value           {@link JValue} to assign to the default value
     * @return {@link JAnnotation}
     */
    private static JAnnotation createDefaultValue(Class<?> annotationClass, ClassType annotationType, JValue<?, ?> value) {
        // Make sure that the annotation is actually one which takes a default value
        if (annotationClass != null) {
            Method[] methods = annotationClass.getDeclaredMethods();
            if (methods.length != 1 || !"value".equals(methods[0].getName()))
                throw new DefinitionException(annotationType, "Cannot employ default value, to use default value Annotation must have exactly one attribute named value");
        } else {
            LOGGER.warning(buildAnnotationClassNotFoundWarning("Default Value", annotationType));
        }

        return createAnnotationWithValues(annotationClass, annotationType, Map.of("value", value));
    }

    /**
     * Create the warning message when creating an annotation that cannot be validated due to Class not found
     * 
     * @param annotationType  {@link String} describing the type of annotation to be created (in plain language)
     * @param annotationClass {@link ClassType} describing the annotation class
     * @return {@link String} the message toe present to the user
     */
    private static String buildAnnotationClassNotFoundWarning(String annotationType, ClassType annotationClass) {
        return "Cannot perform validation for " + annotationType + " Annotation " + annotationClass.getFullyQualifiedName() + ". It does not exist on the classpath";
    }

    /**
     * Create an annotation which contains any number of values/attributes
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param annotationType  {@link ClassType} representing the class
     * @param values          {@link Map} of {@link String} attribute names to their desired {@link JValue}
     * @return {@link JAnnotation}
     */
    private static JAnnotation createAnnotationWithValues(Class<?> annotationClass, ClassType annotationType, Map<String, JValue<?, ?>> values) {
        JAnnotation annotation = new JAnnotation(annotationType);
        ArrayList<String> sortedNames = new ArrayList<>(values.keySet());
        sortedNames.sort((lhs, rhs) -> lhs.compareTo(rhs));

        for (String name : sortedNames) {
            JValue<?, ?> value = values.get(name);
            validateCorrectType(annotationClass, name, value);
            JMethod<?> method = new InterfaceMethodBuilder<>(annotationType, name).setType(value.getType()).setVisibility(VisibilityType.PUBLIC).build();
            annotation.addAttribute(method, value);
        }

        return annotation;
    }

    /**
     * Validate that the {@link JValue} can be assigned to the annotation attribute
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param attrName        {@link String} the name of the attribute (i.e.: annotation method)
     * @param value           {@link JValue} to be assigned to the attribute
     */
    private static void validateCorrectType(Class<?> annotationClass, String attrName, JValue<?, ?> value) {
        // If the class is not known, then no validation can be performed
        if (annotationClass == null)
            return;

        try {
            // The annotation method cannot be void
            Class<?> expectedReturn = annotationClass.getDeclaredMethod(attrName).getReturnType();
            if (Void.TYPE.equals(expectedReturn))
                throw new DefinitionException(annotationClass, "Attribute " + attrName + " cannot be void");

            // Make sure that this is a correct instance
            Type returnType = TypeFactory.create(expectedReturn);
            if (!value.isInstanceOf(returnType))
                throw new DefinitionException(annotationClass, "Incompatible attribute " + attrName + ", expect " + returnType.getSimpleName() + " but got " + value.getType());
        } catch (NoSuchMethodException e) {
            throw new DefinitionException(annotationClass, "Attribute " + attrName + " does not exist in " + annotationClass.getName());
        } catch (SecurityException e) {
            throw new DefinitionException(annotationClass, "Unable to apply attribute " + attrName + " to " + annotationClass.getName(), e);
        }
    }

    /**
     * Create an annotation which takes an arbitrary number of named values (at least one)
     * 
     * @param annotationClass {@link Class} extending {@link Annotation} where the annotation is defined
     * @param values          {@link Map} of {@link String} attribute name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(Class<? extends Annotation> annotationClass, Map<String, JValue<?, ?>> values) {
        return createMultiValue(annotationClass, new ClassType(annotationClass), values);
    }

    /**
     * Create an annotation which takes an arbitrary number of named values (at least one)
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the annotation
     * @param values             {@link Map} of {@link String} attribute name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String fullyQualifiedName, Map<String, JValue<?, ?>> values) {
        return create(new ClassType(fullyQualifiedName), values);
    }

    /**
     * Create an annotation which takes an arbitrary number of named values (at least one)
     * 
     * @param packageName {@link String} the name of the package where the annotation resides
     * @param className   {@link String} the name of the annotation
     * @param values      {@link Map} of {@link String} attribute name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String packageName, String className, Map<String, JValue<?, ?>> values) {
        return create(new ClassType(packageName, className), values);
    }

    /**
     * Create an annotation which takes an arbitrary number of named values (at least one)
     * 
     * @param annotationClass {@link ClassType} representing the annotation
     * @param values          {@link Map} of {@link String} attribute name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(ClassType annotationClass, Map<String, JValue<?, ?>> values) {
        return createMultiValue(fromType(annotationClass), annotationClass, values);
    }

    /**
     * Create an annotation which contains an arbitrary number of attributes (at least one)
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param annotationType  {@link ClassType} representing the annotation
     * @param values          {@link Map} of {@link String} attribute name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    private static JAnnotation createMultiValue(Class<?> annotationClass, ClassType annotationType, Map<String, JValue<?, ?>> values) {
        // Verify that at a basic level all attributes are accounted for
        if (annotationClass != null) {
            if (annotationClass.getDeclaredMethods().length == 0)
                throw new DefinitionException(annotationClass, "Annotations with named attributes must have at least one attribute");
            checkUnsatisfiedAttributes(annotationClass, values.keySet());
        } else {
            LOGGER.warning(buildAnnotationClassNotFoundWarning("Multi-Value", annotationType));
        }

        return createAnnotationWithValues(annotationClass, annotationType, values);
    }

    /**
     * Check if there are any unsatisfied attributes for the annotation (i.e.: any attributes that need a value to be specified that are not specified).
     * 
     * @param annotationClass {@link Class} defining the annotation
     * @param providedValues  {@link Collection} of {@link String}s indicating which attributes have been provided with values
     */
    private static void checkUnsatisfiedAttributes(Class<?> annotationClass, Collection<String> providedValues) {
        List<Method> methods = new ArrayList<>(Arrays.asList(annotationClass.getDeclaredMethods()));

        // Make sure that all of the specified attributes apply to the annotation
        for (String s : providedValues) {
            Method foundMethod = null;
            for (Method m : methods) {
                if (m.getName().equals(s)) {
                    foundMethod = m;
                    break;
                }
            }

            if (foundMethod == null)
                throw new DefinitionException(annotationClass, "Does not contain specified attribute " + s);
            else
                methods.remove(foundMethod);
        }

        // Make sure that there are no extra attributes
        methods.removeIf(m -> m.getDefaultValue() != null);

        if (!methods.isEmpty())
            throw new DefinitionException(annotationClass, "Annotation has attributes without assigned values [" + TendrilStringUtil.join(methods, m -> m.getName()) + "]");
    }
}
