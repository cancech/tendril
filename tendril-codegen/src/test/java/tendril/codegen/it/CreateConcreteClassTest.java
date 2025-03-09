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

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;
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
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }

    /**
     * Verify that the empty class generates properly when it has some annotations
     */
    @Test
    public void testCreateEmptyAnnotatedClass() {
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED).build();
        cls.add(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))));
        cls.add(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))));

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
        matcher.match(cls.generateCode());
    }

    /**
     * Verify that a class with fields generates properly
     */
    @Test
    public void testCreateClassWithFields() {
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildField(PrimitiveType.BOOLEAN, "booleanField").setVisibility(VisibilityType.PUBLIC).setValue(JValueFactory.create(false)).finish()
                .buildField(VisibilityType.class, "enumField").setVisibility(VisibilityType.PRIVATE).setValue(JValueFactory.create(VisibilityType.PACKAGE_PRIVATE)).finish()
                .build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + VisibilityType.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("    public boolean booleanField = false;");
        matcher.eq("");
        matcher.eq("    private VisibilityType enumField = VisibilityType.PACKAGE_PRIVATE;");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }

    /**
     * Verify that the class generates properly when it has some methods
     */
    @Test
    public void testCreateClassWithMethods() {
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(new ClassType(String.class), "strParam").finish()
                    .emptyImplementation().finish()
                .buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .build();

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
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify the class generates properly if it has a parent class
     */
    @Test
    public void testCreateClassWithParent() {
        JClass parentCls = ClassBuilder.forConcreteClass(new ClassType("q.w.e.r.t", "Y")).build();
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED).extendsClass(parentCls).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.w.e.r.t.Y;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B extends Y {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify the class generates properly if it implements an interface
     */
    @Test
    public void testCreateClassWithInterface() {
        JClass ifaceCls = ClassBuilder.forConcreteClass(new ClassType("q.w.e.r.t", "Y")).build();
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED).implementsInterface(ifaceCls).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.w.e.r.t.Y;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B implements Y {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }

    /**
     * Verify the class generates properly if has constructors
     */
    @Test
    public void testCreateWithConsturctors() {
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().finish()
                .buildConstructor().setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(new ClassType(String.class), "strParam").finish()
                    .addCode("a", "b", "c", "d").finish()
                .buildConstructor().setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected class B {");
        matcher.eq("");
        matcher.eq("    public B() {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    protected B(String strParam) {");
        matcher.eq("        a");
        matcher.eq("        b");
        matcher.eq("        c");
        matcher.eq("        d");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    @TestNonDefaultAttrAnnotation(myString = \"qazwsx\")");
        matcher.eq("    private B() {");
        matcher.eq("        abc");
        matcher.eq("        123");
        matcher.eq("        qwerty");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
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
        JClass abstractCls = ClassBuilder.forConcreteClass(new ClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PROTECTED).addGeneric(genericT)
                .buildMethod("abc123").addGeneric(genericU).buildParameter(genericT, "t").finish().buildParameter(genericU, "u").finish()
                    .buildParameter(listClass, "list").finish().addCode().finish()
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
        matcher.eq("protected class Y<T> {");
        matcher.eq("");
        matcher.eq("    T tField;");
        matcher.eq("");
        matcher.eq("    List<? super V> listField;");
        matcher.eq("");
        matcher.eq("    <U extends B> void abc123(T t, U u, List<? super V> list) {");
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
        GenericType genericT = GenericFactory.create("T");
        GenericType genericU = GenericFactory.createExtends("U", new ClassType("a", "B"));
        GenericType superType = GenericFactory.createSuper(new ClassType("z.x.c", "V"));
        ClassType listClass = new ClassType(List.class);
        listClass.addGeneric(superType);
        
        JClass parentCls = ClassBuilder.forConcreteClass(new ClassType("q.w.e.r.t", "Y")).build();
        JClass ifaceYCls = ClassBuilder.forConcreteClass(new ClassType("q.w.e.r.t", "Y")).build();
        JClass ifaceFCls = ClassBuilder.forConcreteClass(new ClassType("a.b.c.d", "F")).build();
        JClass cls = ClassBuilder.forConcreteClass(new ClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .extendsClass(parentCls)
                .implementsInterface(ifaceYCls).implementsInterface(ifaceFCls)
                .buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(new ClassType(String.class), "strParam").finish().emptyImplementation().finish()
                .addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))))
                .buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))))
                .buildField(PrimitiveType.BOOLEAN, "booleanField").setVisibility(VisibilityType.PUBLIC).setValue(JValueFactory.create(false)).finish()
                .buildField(VisibilityType.class, "enumField").setVisibility(VisibilityType.PRIVATE).setValue(JValueFactory.create(VisibilityType.PACKAGE_PRIVATE)).finish()
                .buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().finish()
                .buildConstructor().addGeneric(genericU).setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(new ClassType(String.class), "strParam").finish()
                    .buildParameter(genericU, "u").finish()
                    .addCode("a", "b", "c", "d").finish()
                .buildConstructor().setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .buildMethod("abc123").addGeneric(genericT).addGeneric(genericU).buildParameter(genericT, "t").finish().buildParameter(genericU, "u").finish()
                    .buildParameter(listClass, "list").finish().addCode().finish()
                .buildField(genericT, "tField").finish()
                .buildField(listClass, "listField").finish()
                .build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import a.B;");
        matcher.eq("import a.b.c.d.F;");
        matcher.eq("import " + List.class.getName() + ";");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.w.e.r.t.Y;");
        matcher.eq("import " + VisibilityType.class.getName() + ";");
        matcher.eq("import " + TestMultiAttrsAnnotation.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import z.x.c.V;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Deprecated.class.getSimpleName() + "(forRemoval = true, since = \"yesterday\")");
        matcher.eq("@" + TestMultiAttrsAnnotation.class.getSimpleName() + "(valInt = 789, valStr = \"qwerty\")");
        matcher.eq("protected class B extends Y implements Y, F {");
        matcher.eq("");
        matcher.eq("    public boolean booleanField = false;");
        matcher.eq("");
        matcher.eq("    private VisibilityType enumField = VisibilityType.PACKAGE_PRIVATE;");
        matcher.eq("");
        matcher.eq("    T tField;");
        matcher.eq("");
        matcher.eq("    List<? super V> listField;");
        matcher.eq("");
        matcher.eq("    public B() {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    protected <U extends B> B(String strParam, U u) {");
        matcher.eq("        a");
        matcher.eq("        b");
        matcher.eq("        c");
        matcher.eq("        d");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    @TestNonDefaultAttrAnnotation(myString = \"qazwsx\")");
        matcher.eq("    private B() {");
        matcher.eq("        abc");
        matcher.eq("        123");
        matcher.eq("        qwerty");
        matcher.eq("    }");
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
        matcher.eq("    <T, U extends B> void abc123(T t, U u, List<? super V> list) {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
}
