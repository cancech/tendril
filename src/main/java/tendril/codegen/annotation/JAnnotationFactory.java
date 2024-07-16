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
import java.util.List;
import java.util.Map;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethodInterface;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValue;
import tendril.util.TendrilStringUtil;

/**
 * Factory to facilitate the creation of {@link JAnnotation}s
 */
public abstract class JAnnotationFactory {

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
                throw new IllegalArgumentException(classType.getFullyQualifiedName() + " is not an enumeration");
            return klass;
        } catch (ClassNotFoundException e) {
            // TODO generate warning, unable to find class file
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a marker {@link JAnnotation}, throwing an {@link IllegalArgumentException} if the annotation has any actual parameters
     * 
     * @param annotationClass {@link Class} which defined the annotation itself
     * @param classType       {@link ClassType} representing the class
     * @return {@link JAnnotation}
     */
    private static JAnnotation createMarker(Class<?> annotationClass, ClassType classType) {
        if (annotationClass != null && annotationClass.getDeclaredMethods().length != 0)
            throw new IllegalArgumentException(classType.getClassName() + " is not a marker annotation, it has parameters.");

        return new JAnnotation(classType);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link Class} extending {@link Annotation} where the annotation is defined
     * @param value           {@link JValue} to assign to the default parameter
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(Class<? extends Annotation> annotationClass, JValue<?, ?> value) {
        return createDefaultValue(annotationClass, new ClassType(annotationClass), value);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the annotation
     * @param value              {@link JValue} to assign to the default parameter
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
     * @param value       {@link JValue} to assign to the default parameter
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String packageName, String className, JValue<?, ?> value) {
        return create(new ClassType(packageName, className), value);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link ClassType} representing the annotation
     * @param value           {@link JValue} to assign to the default parameter
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
                throw new IllegalArgumentException(annotationClass.getSimpleName() + " annotation must have exactly one parameter named value");
        } else {
            // TODO warning that cannot validate
        }

        return createAnnotationWithValues(annotationClass, annotationType, Map.of("value", value));
    }

    /**
     * Create an annotation which contains any number of values/parameters
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param annotationType  {@link ClassType} representing the class
     * @param values          {@link Map} of {@link String} parameter names to their desired {@link JValue}
     * @return {@link JAnnotation}
     */
    private static JAnnotation createAnnotationWithValues(Class<?> annotationClass, ClassType annotationType, Map<String, JValue<?, ?>> values) {
        JAnnotation annotation = new JAnnotation(annotationType);
        ArrayList<String> sortedNames = new ArrayList<>(values.keySet());
        sortedNames.sort((lhs, rhs) -> lhs.compareTo(rhs));

        for (String name : sortedNames) {
            JValue<?, ?> value = values.get(name);
            validateCorrectType(annotationClass, name, value);
            annotation.addParameter(new JMethodInterface<>(VisibilityType.PUBLIC, value.getType(), name, null), value);
        }

        return annotation;
    }

    /**
     * Validate that the {@link JValue} can be assigned to the annotation parameter
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param paramName       {@link String} the name of the parameter (i.e.: annotation method)
     * @param value           {@link JValue} to be assigned to the parameter
     */
    private static void validateCorrectType(Class<?> annotationClass, String paramName, JValue<?, ?> value) {
        // If the class is not known, then no validation can be performed
        if (annotationClass == null)
            return;

        try {
            // The annotation method cannot be void
            Class<?> expectedReturn = annotationClass.getDeclaredMethod(paramName).getReturnType();
            if (Void.TYPE.equals(expectedReturn))
                throw new IllegalArgumentException(annotationClass.getName() + " cannot have a void parameter " + paramName);

            // TODO fix this
//            // Determine the appropriate type
//            Type returnType;
//            if (expectedReturn.isPrimitive())
//                returnType = PrimitiveType.from(expectedReturn);
//            else
//                returnType = new ClassType(expectedReturn);
//
//            // Make sure that this is a correct instance
//            if (!value.isInstanceOf(returnType))
//                throw new IllegalArgumentException("Incompatible parameter " + paramName + ", expect " + returnType + " but got " + value.getType());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Parameter " + paramName + " does not exist in " + annotationClass.getName());
        } catch (SecurityException e) {
            throw new IllegalArgumentException("Unable to apply parameter " + paramName + " to " + annotationClass.getName(), e);
        }
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link Class} extending {@link Annotation} where the annotation is defined
     * @param values          {@link Map} of {@link String} parameter name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(Class<? extends Annotation> annotationClass, Map<String, JValue<?, ?>> values) {
        return createMultiValue(annotationClass, new ClassType(annotationClass), values);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the annotation
     * @param values             {@link Map} of {@link String} parameter name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String fullyQualifiedName, Map<String, JValue<?, ?>> values) {
        return create(new ClassType(fullyQualifiedName), values);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param packageName {@link String} the name of the package where the annotation resides
     * @param className   {@link String} the name of the annotation
     * @param values      {@link Map} of {@link String} parameter name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(String packageName, String className, Map<String, JValue<?, ?>> values) {
        return create(new ClassType(packageName, className), values);
    }

    /**
     * Create an annotation which takes a single default value
     * 
     * @param annotationClass {@link ClassType} representing the annotation
     * @param values          {@link Map} of {@link String} parameter name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    public static JAnnotation create(ClassType annotationClass, Map<String, JValue<?, ?>> values) {
        return createMultiValue(fromType(annotationClass), annotationClass, values);
    }

    /**
     * Create an annotation which contains an arbitrary number of parameters (at least one)
     * 
     * @param annotationClass {@link Class} where the annotation is defined
     * @param annotationType  {@link ClassType} representing the annotation
     * @param values          {@link Map} of {@link String} parameter name to it intended {@link JValue}
     * @return {@link JAnnotation}
     */
    private static JAnnotation createMultiValue(Class<?> annotationClass, ClassType annotationType, Map<String, JValue<?, ?>> values) {
        // Verify that at a basic level all parameters are accounted for
        if (annotationClass != null) {
            List<Method> methods = new ArrayList<>(Arrays.asList(annotationClass.getDeclaredMethods()));
            if (methods.size() == 0)
                throw new IllegalArgumentException(annotationClass.getName() + " annotation must have at least one parameter");

            // Make sure that all of the specified parameters apply to the annotation
            for (String s : values.keySet()) {
                Method foundMethod = null;
                for (Method m : methods) {
                    if (m.getName().equals(s)) {
                        foundMethod = m;
                        break;
                    }
                }

                if (foundMethod == null)
                    throw new IllegalArgumentException("Specified paramter " + s + " does not appear in " + annotationClass.getName());
                else
                    methods.remove(foundMethod);
            }

            // Make sure that there are not extra parameters
            methods.removeIf(m -> !m.isDefault());

            if (!methods.isEmpty())
                throw new IllegalArgumentException(annotationClass.getName() + " annotation has parameters without assigned values [" + TendrilStringUtil.join(methods, m -> m.getName()) + "]");
        } else {
            // TODO warning that cannot validate
        }

        return createAnnotationWithValues(annotationClass, annotationType, values);
    }
}
