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

import java.util.List;
import java.util.Map;

import javax.annotation.processing.Generated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassInterface;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestDefaultAttrAnnotation;
import tendril.test.helper.annotation.TestMarkerAnnotation;

/**
 * Test case to ensure that interfaces can be generated
 */
public class CreateInterfaceTest {

    /**
     * Verify that attempting to create a private or protected annotation is not allowed
     */
    @Test
    public void cannotCreatePrivateOrProtected() {
        ClassBuilder builder = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y"));
        ClassAssert.assertInstance(JClassInterface.class, builder.setVisibility(VisibilityType.PUBLIC).build());
        ClassAssert.assertInstance(JClassInterface.class, builder.setVisibility(VisibilityType.PACKAGE_PRIVATE).build());
        Assertions.assertThrows(DefinitionException.class, () -> builder.setVisibility(VisibilityType.PROTECTED).build());
        Assertions.assertThrows(DefinitionException.class, () -> builder.setVisibility(VisibilityType.PRIVATE).build());
    }

    /**
     * Verify that only supported method can be added to the annotation
     */
    @Test
    public void cannotAddInvalidMethods() {
        ClassBuilder builder = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "privateNoCodeNotAllowed").setVisibility(VisibilityType.PRIVATE).finish());
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "protectedNotAllowed").setVisibility(VisibilityType.PROTECTED).finish());
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "packagePrivateNotAllowed").setVisibility(VisibilityType.PACKAGE_PRIVATE).finish());
    }

    /**
     * Verify that the empty interface generates properly
     */
    @Test
    public void createEmptyInterface() {
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE).build();

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
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE)
                .addAnnotation(JAnnotationFactory.create(new ClassType("this.that", "Something"), Map.of("val1", JValueFactory.create("string"), "val2", JValueFactory.create(123))))
                .addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123")))
                .build();

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
     * Verify that an interface with fields generates properly
     */
    @Test
    public void createInterfaceWithFields() {
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE).build();

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
     * Verify that an interface with methods generates properly
     */
    @Test
    public void createInterfaceWithMethods() {
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE)
                .buildMethod("voidMethod").setVisibility(VisibilityType.PUBLIC)
                    .buildParameter(new ClassType(String.class), "stringParam")
                        .addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class)).finish()
                    .finish()
                .buildMethod(String.class, "annotatedMethod").addAnnotation(JAnnotationFactory.create(Deprecated.class)).addCode("abc123", "321cba").setVisibility(VisibilityType.PUBLIC).finish()
                .build();

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
     * Verify that can create an interface which extends another interface
     */
    @Test
    public void createInterfaceExtendingInterface() {
        JClass parentCls = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Z")).build();
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE).extendsClass(parentCls).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("interface Y extends Z {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
    
    /**
     * Verify that the class can contain a generic type
     */
    @Test
    public void testCreateSimpleGeneric() {
        GenericType genericT = GenericFactory.create("T");
        GenericType genericU = GenericFactory.createExtends("U", new ClassType("a", "B"));
        GenericType superType = GenericFactory.createSuper(new ClassType("z.x.c", "V"));
        ClassType listClass = new ClassType(List.class);
        listClass.addGeneric(superType);
        JClass abstractCls = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE).addGeneric(genericT)
                .buildMethod("abc123").setVisibility(VisibilityType.PUBLIC).addGeneric(genericU).buildParameter(genericT, "t").finish().buildParameter(genericU, "u").finish()
                    .buildParameter(listClass, "list").finish().addCode("a", "b", "c").finish()
                .buildField(genericT, "tField").finish()
                .buildField(listClass, "listField").finish()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import a.B;");
        matcher.eq("import " + List.class.getName() + ";");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import z.x.c.V;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("interface Y<T> {");
        matcher.eq("");
        matcher.eq("    T tField;");
        matcher.eq("");
        matcher.eq("    List<? super V> listField;");
        matcher.eq("");
        matcher.eq("    default <U extends B> void abc123(T t, U u, List<? super V> list) {");
        matcher.eq("        a");
        matcher.eq("        b");
        matcher.eq("        c");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that an interface with annotations and methods generates properly
     */
    @Test
    public void createInterfaceWithAnnotationsAndMethods() {
        JClass ifaceZCls = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Z")).build();
        JClass ifaceFCls = ClassBuilder.forInterface(new ClassType("a.b.c.d.e", "F")).build();
        JClass ifaceWsxCls = ClassBuilder.forInterface(new ClassType("q.a.z", "Wsx")).build();
        JClass iface = ClassBuilder.forInterface(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PACKAGE_PRIVATE)
                .extendsClass(ifaceZCls)
                .extendsClass(ifaceFCls)
                .extendsClass(ifaceWsxCls)
                .buildMethod("voidMethod").setVisibility(VisibilityType.PUBLIC)
                    .buildParameter(new ClassType(String.class), "stringParam").addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class)).finish()
                    .finish()
                .buildMethod(String.class, "annotatedMethod").setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Deprecated.class)).addCode("abc123", "321cba").finish()
                .addAnnotation(JAnnotationFactory.create(new ClassType("this.that", "Something"), Map.of("val1", JValueFactory.create("string"), "val2", JValueFactory.create(123))))
                .addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123")))
                .build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package q.w.e.r.t;");
        matcher.eq("");
        matcher.eq("import a.b.c.d.e.F;");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.a.z.Wsx;");
        matcher.eq("import " + TestDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("import this.that.Something;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@Something(val1 = \"string\", val2 = 123)");
        matcher.eq("@TestDefaultAttrAnnotation(\"abc123\")");
        matcher.eq("interface Y extends Z, F, Wsx {");
        matcher.eq("");
        matcher.eq("    void voidMethod(@TestMarkerAnnotation String stringParam);");
        matcher.eq("");
        matcher.eq("    @Deprecated");
        matcher.eq("    private String annotatedMethod() {");
        matcher.eq("        abc123");
        matcher.eq("        321cba");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(iface.generateCode());
    }
}
