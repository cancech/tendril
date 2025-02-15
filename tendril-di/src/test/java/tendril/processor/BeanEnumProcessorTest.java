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

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.ProcessingException;
import tendril.bean.qualifier.BeanId;
import tendril.bean.qualifier.EnumQualifier;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.util.TendrilStringUtil;

/**
 * Test case for the {@link BeanEnumProcessor}
 */
public class BeanEnumProcessorTest extends AbstractUnitTest {

    /** Test override to apply the mock processing environment */
    private class BeanEnumProcessorForTest extends BeanEnumProcessor {

        private BeanEnumProcessorForTest() {
            super();
            this.processingEnv = mockProcessingEnv;
        }
    }

    // Mocks to use for testing
    @Mock
    private TypeElement mockType;
    @Mock
    private Name mockName;
    @Mock
    private Elements mockElementUtils;
    @Mock
    private Types mockTypeUtils;
    @Mock
    private ProcessingEnvironment mockProcessingEnv;
    @Mock
    private TypeElement mockBeanIdElement;
    @Mock
    private TypeMirror mockBeanIdMirror;
    @Mock
    private TypeMirror mockTypeMirror;
    @Mock
    private ClassType mockAnnotatedClass;
    @Mock
    private JMethod<?> mockAnnotatedMethod;
    @Mock
    private ClassType mockClassToGenerate;

    // Instance to test
    private BeanEnumProcessor processor;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        processor = new BeanEnumProcessorForTest();
    }

    /**
     * Verify that the type validation is performed properly
     */
    @Test
    public void testValidateType() {
        int timesGetKind = 0;
        int timesGetQualifiedName = 0;
        when(mockType.getQualifiedName()).thenReturn(mockName);

        // Non-Enum types throw an exception
        for (ElementKind kind : ElementKind.values()) {
            if (kind == ElementKind.ENUM)
                continue;
            when(mockType.getKind()).thenReturn(kind);
            Assertions.assertThrows(ProcessingException.class, () -> processor.validateType(mockType));
            verify(mockType, times(++timesGetKind)).getKind();
            verify(mockType, times(++timesGetQualifiedName)).getQualifiedName();
        }

        when(mockType.getKind()).thenReturn(ElementKind.ENUM);
        when(mockProcessingEnv.getElementUtils()).thenReturn(mockElementUtils);
        when(mockElementUtils.getTypeElement(BeanId.class.getCanonicalName())).thenReturn(mockBeanIdElement);
        when(mockBeanIdElement.asType()).thenReturn(mockBeanIdMirror);
        when(mockProcessingEnv.getTypeUtils()).thenReturn(mockTypeUtils);
        when(mockType.asType()).thenReturn(mockTypeMirror);

        when(mockTypeUtils.isAssignable(mockTypeMirror, mockBeanIdMirror)).thenReturn(false);
        Assertions.assertThrows(ProcessingException.class, () -> processor.validateType(mockType));
        verify(mockType, times(++timesGetKind)).getKind();
        verify(mockType, times(++timesGetQualifiedName)).getQualifiedName();
        verify(mockType).asType();
        verify(mockProcessingEnv).getElementUtils();
        verify(mockElementUtils).getTypeElement(anyString());
        verify(mockProcessingEnv).getTypeUtils();
        verify(mockTypeUtils).isAssignable(mockTypeMirror, mockBeanIdMirror);

        when(mockTypeUtils.isAssignable(mockTypeMirror, mockBeanIdMirror)).thenReturn(true);
        processor.validateType(mockType);
        verify(mockType, times(++timesGetKind)).getKind();
        verify(mockType, times(2)).asType();
        verify(mockProcessingEnv, times(2)).getElementUtils();
        verify(mockElementUtils, times(2)).getTypeElement(anyString());
        verify(mockProcessingEnv, times(2)).getTypeUtils();
        verify(mockTypeUtils, times(2)).isAssignable(mockTypeMirror, mockBeanIdMirror);
    }

    /**
     * Verify that attempting to process a method generates an exception
     */
    @Test
    public void testProcessMethod() {
        when(mockAnnotatedMethod.getName()).thenReturn("mockMethod");
        Assertions.assertThrows(ProcessingException.class, () -> processor.processMethod(mockAnnotatedClass, mockAnnotatedMethod));
        verify(mockAnnotatedMethod).getName();
    }

    /**
     * Verify that a class is properly processed
     */
    @Test
    public void testProcessType() {
        when(mockAnnotatedClass.generateFromClassSuffix("Id")).thenReturn(mockClassToGenerate);
        when(mockAnnotatedClass.isVoid()).thenReturn(false);
        when(mockAnnotatedClass.getSimpleName()).thenReturn("MockEnum");
        when(mockClassToGenerate.getSimpleName()).thenReturn("MockEnumId");
        when(mockClassToGenerate.getClassName()).thenReturn("MockEnumId");
        when(mockClassToGenerate.getPackageName()).thenReturn("a.b.c.d");
        when(mockClassToGenerate.getFullyQualifiedName()).thenReturn("a.b.c.d.MockEnumId");
        when(mockClassToGenerate.getGenerics()).thenReturn(Collections.emptyList());

        ClassDefinition generated = processor.processType(mockAnnotatedClass);
        verify(mockAnnotatedClass).generateFromClassSuffix("Id");
        verify(mockAnnotatedClass).isVoid();
        verify(mockAnnotatedClass).registerImport(anySet());
        verify(mockAnnotatedClass).getSimpleName();
        verify(mockClassToGenerate).getSimpleName();
        verify(mockClassToGenerate).getClassName();
        verify(mockClassToGenerate, times(2)).getPackageName();

        // The code which should be generated
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + ElementType.class.getName() + ";");
        matcher.eq("import " + Retention.class.getName() + ";");
        matcher.eq("import " + RetentionPolicy.class.getName() + ";");
        matcher.eq("import " + Target.class.getName() + ";");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + EnumQualifier.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Retention.class.getSimpleName() + "(" + enumToString(RetentionPolicy.RUNTIME) + ")");
        matcher.eq("@" + Target.class.getSimpleName() + "({" + enumsToString(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER) + "})");
        matcher.eq("@" + EnumQualifier.class.getSimpleName());
        matcher.eq("public @interface MockEnumId {");
        matcher.eq("");
        matcher.eq("    MockEnum value();");
        matcher.eq("");
        matcher.eq("}");

        // Verify that the correct thing was generated
        Assertions.assertEquals(mockClassToGenerate, generated.getType());
        matcher.match(generated.getCode());
    }

    /**
     * Helper to convert an {@link Enum} to how it would be presented in the code
     * 
     * @param <T>   type of {@link Enum} that is to be converted
     * @param value {@link Enum} to convert
     * @return {@link String} representation of the enum value in code
     */
    private <T extends Enum<T>> String enumToString(Enum<T> value) {
        return value.getClass().getSimpleName() + "." + value.name();
    }

    /**
     * Convert a series of enums to code
     * 
     * @param <T>    type of {@link Enum} that is to be converted
     * @param values {@link Enum}s... to convert
     * @return {@link String} representation of the enums value in code
     */
    @SafeVarargs
    private <T extends Enum<T>> String enumsToString(Enum<T>... values) {
        return TendrilStringUtil.join(Arrays.asList(values), ", ", (val) -> enumToString(val));
    }
}
