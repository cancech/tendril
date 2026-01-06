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
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestDefaultAttrAnnotation;
import tendril.test.helper.annotation.TestMarkerAnnotation;
import tendril.test.helper.annotation.TestMultiAttrsAnnotation;
import tendril.test.helper.annotation.TestNonDefaultAttrAnnotation;

/**
 * Test case to ensure that abstract classes can be generated
 */
public class CreateAbstractClassTest {
    
    /**
     * Verify that the empty abstract class generates properly
     */
    @Test
    public void testCreateEmptyAbstractClass() {
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the empty abstract class generates properly when it has some annotations
     */
    @Test
    public void testCreateEmptyAnnotatedAbstractClass() {
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))))
                .addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789)))).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMultiAttrsAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Deprecated.class.getSimpleName() + "(forRemoval = true, since = \"yesterday\")");
        matcher.eq("@" + TestMultiAttrsAnnotation.class.getSimpleName() + "(valInt = 789, valStr = \"qwerty\")");
        matcher.eq("protected abstract class B {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that an abstract class containing fields generates properly
     */
    @Test
    public void testCreateAbstractClassWithFields() {
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildField(TypeFactory.createClassType("q.a.z", "Wsx"), "field1").setVisibility(VisibilityType.PACKAGE_PRIVATE).finish()
                .buildField(PrimitiveType.BOOLEAN, "field2").setVisibility(VisibilityType.PUBLIC).setStatic(true).setFinal(true).setValue(JValueFactory.create(false)).finish()
                .build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.a.z.Wsx;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B {");
        matcher.eq("");
        matcher.eq("    Wsx field1;");
        matcher.eq("");
        matcher.eq("    public static final boolean field2 = false;");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the abstract class generates properly when it has only abstract methods
     */
    @Test
    public void testCreateAbstractClassOnlyAbstractMethods() {

        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildMethod(PrimitiveType.FLOAT, "floatMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(PrimitiveType.SHORT, "shortParam").finish().finish()
                .buildMethod(VisibilityType.class, "visibilityMethod").setVisibility(VisibilityType.PUBLIC).finish()
                .buildMethod(String.class, "stringMethod").setVisibility(VisibilityType.PACKAGE_PRIVATE)
                    .buildParameter(TypeFactory.createClassType(String.class), "param1")
                        .addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class))
                        .addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123"))).finish()
                    .buildParameter(PrimitiveType.DOUBLE, "param2").finish()
                    .finish().build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + VisibilityType.class.getName() + ";");
        matcher.eq("import " + TestDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B {");
        matcher.eq("");
        matcher.eq("    protected abstract float floatMethod(short shortParam);");
        matcher.eq("");
        matcher.eq("    public abstract VisibilityType visibilityMethod();");
        matcher.eq("");
        matcher.eq("    abstract String stringMethod(@TestMarkerAnnotation @TestDefaultAttrAnnotation(\"abc123\") String param1, double param2);");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the abstract class generates properly when it has only concrete methods
     */
    @Test
    public void testCreateAbstractClassOnlyConcreteMethods() {
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(TypeFactory.createClassType(String.class), "strParam").finish()
                    .emptyImplementation().finish()
                .buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish().build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B {");
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
     * Verify that the abstract class can extend and implement other classes/interfaces.
     */
    @Test
    public void testCreateAbstractClassWithParents() {
        JClass parentCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).build();
        JClass ifaceYCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).build();
        JClass ifaceQwertyCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("a.s.d.f", "Qwerty")).build();
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .extendsClass(parentCls).implementsInterface(ifaceYCls).implementsInterface(ifaceQwertyCls).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import a.s.d.f.Qwerty;");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.w.e.r.t.Y;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B extends Y implements Y, Qwerty {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that the abstract class generates properly when it has only constructors
     */
    @Test
    public void testCreateAbstractWithConstructors() {
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().finish()
                .buildConstructor().setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(TypeFactory.createClassType(String.class), "strParam").finish()
                    .addCode("a", "b", "c", "d").finish()
                .buildConstructor().setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish().build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("protected abstract class B {");
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
        matcher.match(abstractCls.generateCode());
    }
    
    /**
     * Verify that the class can contain a generic type
     */
    @Test
    public void testCreateSimpleGeneric() {
        GenericType genericT = GenericFactory.create("T");
        GenericType genericU = GenericFactory.createExtends("U", TypeFactory.createClassType("a", "B"));
        GenericType superType = GenericFactory.createSuper(TypeFactory.createClassType("z.x.c", "V"));
        ClassType listClass = TypeFactory.createClassType(List.class);
        listClass.addGeneric(superType);
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).setVisibility(VisibilityType.PROTECTED).addGeneric(genericT)
                .buildMethod("abc123").addGeneric(genericU).buildParameter(genericT, "t").finish().buildParameter(genericU, "u").finish()
                    .buildParameter(listClass, "list").finish().finish()
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
        matcher.eq("protected abstract class Y<T> {");
        matcher.eq("");
        matcher.eq("    T tField;");
        matcher.eq("");
        matcher.eq("    List<? super V> listField;");
        matcher.eq("");
        matcher.eq("    abstract <U extends B> void abc123(T t, U u, List<? super V> list);");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }

    /**
     * Verify that a complex class with a little everything can be properly generated
     */
    @Test
    public void testCreateComplexAbstractClass() {
        GenericType generic1 = GenericFactory.create("T");
        GenericType generic2 = GenericFactory.createExtends("U", TypeFactory.createClassType("a.b.c", "D"));
        GenericType generic3 = GenericFactory.create(TypeFactory.createClassType("a.s.d", "F"));
        ClassType listClass = TypeFactory.createClassType(List.class);
        listClass.addGeneric(GenericFactory.createSuper(TypeFactory.createClassType("z.x.c", "V")));
        
        JClass parentCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).addGeneric(generic1).build();
        JClass ifaceYCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).addGeneric(generic2).build();
        JClass ifaceQwertyCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("a.s.d.f", "Qwerty")).addGeneric(generic3).build();
        
        JClass abstractCls = ClassBuilder.forAbstractClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
                .addGeneric(generic1).addGeneric(generic2)
                .extendsClass(parentCls).implementsInterface(ifaceYCls).implementsInterface(ifaceQwertyCls)
                .buildMethod(PrimitiveType.FLOAT, "floatMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(PrimitiveType.SHORT, "shortParam").finish().finish()
                .buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(TypeFactory.createClassType(String.class), "strParam").finish()
                    .emptyImplementation().finish()
                .addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))))
                .buildMethod(VisibilityType.class, "visibilityMethod").setVisibility(VisibilityType.PUBLIC).finish()
                .buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .buildMethod(String.class, "stringMethod").setVisibility(VisibilityType.PACKAGE_PRIVATE)
                    .buildParameter(TypeFactory.createClassType(String.class), "param1")
                        .addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class))
                        .addAnnotation(JAnnotationFactory.create(TestDefaultAttrAnnotation.class, JValueFactory.create("abc123"))).finish()
                    .buildParameter(PrimitiveType.DOUBLE, "param2").finish().finish()
                .addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))))
                .buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().finish()
                .buildConstructor().setVisibility(VisibilityType.PROTECTED)
                    .buildParameter(TypeFactory.createClassType(String.class), "strParam").finish()
                    .addCode("a", "b", "c", "d").finish()
                .buildConstructor().setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
                    .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
                .buildField(TypeFactory.createClassType("q.a.z", "Wsx"), "field1").setVisibility(VisibilityType.PACKAGE_PRIVATE).finish()
                .buildField(PrimitiveType.BOOLEAN, "field2").setVisibility(VisibilityType.PUBLIC).setStatic(true).setFinal(true).setValue(JValueFactory.create(false)).finish()
                .buildField(generic1, "tField").finish()
                .buildField(listClass, "listField").finish()
                .buildMethod("abc123").addGeneric(generic2).buildParameter(generic1, "t").finish().buildParameter(generic2, "u").finish()
                    .buildParameter(listClass, "list").finish().finish()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package z.x.c.v;");
        matcher.eq("");
        matcher.eq("import a.b.c.D;");
        matcher.eq("import a.s.d.F;");
        matcher.eq("import a.s.d.f.Qwerty;");
        matcher.eq("import " + List.class.getName() + ";");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import q.a.z.Wsx;");
        matcher.eq("import q.w.e.r.t.Y;");
        matcher.eq("import " + VisibilityType.class.getName() + ";");
        matcher.eq("import " + TestDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("import " + TestMultiAttrsAnnotation.class.getName() + ";");
        matcher.eq("import " + TestNonDefaultAttrAnnotation.class.getName() + ";");
        matcher.eq("import z.x.c.V;");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@" + Deprecated.class.getSimpleName() + "(forRemoval = true, since = \"yesterday\")");
        matcher.eq("@" + TestMultiAttrsAnnotation.class.getSimpleName() + "(valInt = 789, valStr = \"qwerty\")");
        matcher.eq("protected abstract class B<T, U extends D> extends Y<T> implements Y<U>, Qwerty<F> {");
        matcher.eq("");
        matcher.eq("    Wsx field1;");
        matcher.eq("");
        matcher.eq("    public static final boolean field2 = false;");
        matcher.eq("");
        matcher.eq("    T tField;");
        matcher.eq("");
        matcher.eq("    List<? super V> listField;");
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
        matcher.eq("    protected abstract float floatMethod(short shortParam);");
        matcher.eq("");
        matcher.eq("    protected char charMethod(String strParam) {");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    public abstract VisibilityType visibilityMethod();");
        matcher.eq("");
        matcher.eq("    @TestNonDefaultAttrAnnotation(myString = \"qazwsx\")");
        matcher.eq("    private long longMethod() {");
        matcher.eq("        abc");
        matcher.eq("        123");
        matcher.eq("        qwerty");
        matcher.eq("    }");
        matcher.eq("");
        matcher.eq("    abstract String stringMethod(@TestMarkerAnnotation @TestDefaultAttrAnnotation(\"abc123\") String param1, double param2);");
        matcher.eq("");
        matcher.eq("    abstract <U extends D> void abc123(T t, U u, List<? super V> list);");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(abstractCls.generateCode());
    }
}
