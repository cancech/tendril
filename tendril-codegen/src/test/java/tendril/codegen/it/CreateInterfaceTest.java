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
import tendril.codegen.field.value.JValueFactory;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestDefaultAttrAnnotation;
import tendril.test.helper.annotation.TestMarkerAnnotation;

/**
 * Test case to ensure that interfaces can be generated
 */
public class CreateInterfaceTest {
    
    /**
     * Verify that the empty interface generates properly
     */
    @Test
    public void createEmptyInterface() {
        JClass iface = JClassFactory.createInterface(VisibilityType.PACKAGE_PRIVATE, new ClassType("q.w.e.r.t", "Y"));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("interface Y {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
    
    /**
     * Verify that an interface with an annotation generates properly
     */
    @Test
    public void createAnnotatedInterface() {
        JClass iface = JClassFactory.createInterface(VisibilityType.PACKAGE_PRIVATE, new ClassType("q.w.e.r.t", "Y"));
        iface.addAnnotation(JAnnotationFactory.create(new ClassType("this.that", "Something"), Map.of("val1", JValueFactory.create("string"), "val2", JValueFactory.create(123))));
        iface.addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123")));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import this.that.Something;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@Something(val1 = \"string\", val2 = 123)");
        matcher.eq("@TestDefaultAttrAnnotation(\"abc123\")");
        matcher.eq("interface Y {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
    
    /**
     * Verify that an interface with methods generates properly
     */
    @Test
    public void createInterfaceWithMethods() {
        JClass iface = JClassFactory.createInterface(VisibilityType.PACKAGE_PRIVATE, new ClassType("q.w.e.r.t", "Y"));
        JParameter<ClassType> stringParam = new JParameter<ClassType>(new ClassType(String.class), "stringParam");
        stringParam.addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class));
        iface.buildMethod("voidMethod").setVisibility(VisibilityType.PUBLIC).addParameter(stringParam).build();
        iface.buildMethod(String.class, "annotatedMethod").addAnnotation(JAnnotationFactory.create(Deprecated.class)).addCode("abc123", "321cba").setVisibility(VisibilityType.PUBLIC).build();

        System.out.println(iface.generateCode());
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("interface Y {");
        matcher.eq("");
        matcher.eq("    void voidMethod(@TestMarkerAnnotation String stringParam);");
        matcher.eq("");
        matcher.eq("    @Deprecated");
        matcher.eq("    default String annotatedMethod() {");
        matcher.eq("        abc123");
        matcher.eq("        321cba");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
    
    /**
     * Verify that an interface with annotations and methods generates properly
     */
    @Test
    public void createInterfaceWithAnnotationsAndMethods() {
        JClass iface = JClassFactory.createInterface(VisibilityType.PACKAGE_PRIVATE, new ClassType("q.w.e.r.t", "Y"));
        JParameter<ClassType> stringParam = new JParameter<ClassType>(new ClassType(String.class), "stringParam");
        stringParam.addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class));
        iface.buildMethod("voidMethod").setVisibility(VisibilityType.PUBLIC).addParameter(stringParam).build();
        iface.buildMethod(String.class, "annotatedMethod").addAnnotation(JAnnotationFactory.create(Deprecated.class)).addCode("abc123", "321cba").setVisibility(VisibilityType.PUBLIC).build();
        iface.addAnnotation(JAnnotationFactory.create(new ClassType("this.that", "Something"), Map.of("val1", JValueFactory.create("string"), "val2", JValueFactory.create(123))));
        iface.addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123")));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("import this.that.Something;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@Something(val1 = \"string\", val2 = 123)");
        matcher.eq("@TestDefaultAttrAnnotation(\"abc123\")");
        matcher.eq("interface Y {");
        matcher.eq("");
        matcher.eq("    void voidMethod(@TestMarkerAnnotation String stringParam);");
        matcher.eq("");
        matcher.eq("    @Deprecated");
        matcher.eq("    default String annotatedMethod() {");
        matcher.eq("        abc123");
        matcher.eq("        321cba");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
}
