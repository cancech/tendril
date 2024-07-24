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
package tendril.codegen.it;

import java.util.Map;

import javax.annotation.processing.Generated;

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassFactory;
import tendril.codegen.field.JParameter;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestMultiAttrsAnnotation;
import tendril.test.helper.annotation.TestNonDefaultAttrAnnotation;

/**
 * Test case to ensure that concrete classes can be generated
 */
public class CreateConcreteClassTest {

    /**
     * Verify that the empty class generates properly
     */
    @Test
    public void testCreateEmptyClass() {
        JClass abstractCls = JClassFactory.createClass(VisibilityType.PROTECTED, new ClassType("z.x.c.v", "B"));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the empty class generates properly when it has some annotations
     */
    @Test
    public void testCreateEmptyAnnotatedClass() {
        JClass abstractCls = JClassFactory.createClass(VisibilityType.PROTECTED, new ClassType("z.x.c.v", "B"));
        abstractCls.addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))));
        abstractCls.addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMultiAttrsAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Deprecated.class.getSimpleName() + "(forRemoval = true, since = \"yesterday\")");
        matcher.eq("@" + TestMultiAttrsAnnotation.class.getSimpleName() + "(valInt = 789, valStr = \"qwerty\")");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the class generates properly when it has some methods
     */
    @Test
    public void testCreateClassWithMethods() {
        JClass abstractCls = JClassFactory.createClass(VisibilityType.PROTECTED, new ClassType("z.x.c.v", "B"));
        abstractCls.buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED).addParameter(new JParameter<>(new ClassType(String.class), "strParam")).emptyImplementation()
                .build();
        abstractCls.buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).build();

        System.out.println(abstractCls.generateCode());

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("    protected char charMethod(String strParam) {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    @TestNonDefaultAttrAnnotation(myString = \"qazwsx\")");
        matcher.eq("    private long longMethod() {");
        matcher.eq("        abc");
        matcher.eq("        123");
        matcher.eq("        qwerty");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }
    
    /**
     * Verify that a complex class with a little everything can be properly generated
     */
    @Test
    public void testCreateComplexClass() {
        JClass abstractCls = JClassFactory.createClass(VisibilityType.PROTECTED, new ClassType("z.x.c.v", "B"));
        abstractCls.buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED).addParameter(new JParameter<>(new ClassType(String.class), "strParam")).emptyImplementation()
                .build();
        abstractCls.addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))));
        abstractCls.buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).build();
        abstractCls.addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMultiAttrsAnnotation.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Deprecated.class.getSimpleName() + "(forRemoval = true, since = \"yesterday\")");
        matcher.eq("@" + TestMultiAttrsAnnotation.class.getSimpleName() + "(valInt = 789, valStr = \"qwerty\")");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("    protected char charMethod(String strParam) {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    @TestNonDefaultAttrAnnotation(myString = \"qazwsx\")");
        matcher.eq("    private long longMethod() {");
        matcher.eq("        abc");
        matcher.eq("        123");
        matcher.eq("        qwerty");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }
}
