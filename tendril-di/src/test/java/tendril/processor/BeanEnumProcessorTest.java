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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.qualifier.EnumQualifier;
import tendril.bean.qualifier.GeneratedQualifier;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
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
            this.currentClassType = mockAnnotatedClass;
            this.currentMethod = mockAnnotatedMethod;
        }
    }

    // Mocks to use for testing
    @Mock
    private ProcessingEnvironment mockProcessingEnv;
    @Mock
    private ClassType mockAnnotatedClass;
    @Mock
    private JMethod<?> mockAnnotatedMethod;

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
     * Verify that a class is properly processed
     * @throws TendrilException 
     */
    @Test
    public void testProcessType() throws TendrilException {
        when(mockAnnotatedClass.isVoid()).thenReturn(false);
        when(mockAnnotatedClass.getPackageName()).thenReturn("a.b.c.d");
        when(mockAnnotatedClass.getClassName()).thenReturn("MockEnum");
        when(mockAnnotatedClass.getSimpleName()).thenReturn("MockEnum");

        ClassDefinition generated = processor.processType();
        verify(mockAnnotatedClass).isVoid();
        verify(mockAnnotatedClass).registerImport(anySet());
        verify(mockAnnotatedClass).getPackageName();
        verify(mockAnnotatedClass).getClassName();
        verify(mockAnnotatedClass).getSimpleName();
        verify(mockAnnotatedClass).getGenerics();

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
        matcher.eq("import " + GeneratedQualifier.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + GeneratedQualifier.class.getSimpleName());
        matcher.eq("@" + Retention.class.getSimpleName() + "(" + enumToString(RetentionPolicy.RUNTIME) + ")");
        matcher.eq("@" + Target.class.getSimpleName() + "({" + enumsToString(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER) + "})");
        matcher.eq("@" + EnumQualifier.class.getSimpleName());
        matcher.eq("public @interface MockEnumId {");
        matcher.eq("");
        matcher.eq("    MockEnum value();");
        matcher.eq("");
        matcher.eq("}");

        // Verify that the correct thing was generated
        Assertions.assertEquals(TypeFactory.createClassType("a.b.c.d", "MockEnumId"), generated.getType());
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
