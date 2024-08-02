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

import javax.annotation.processing.Generated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestMarkerAnnotation;

/**
 * Test case to ensure that annotation classes can be generated
 */
public class CreateAnnotationTest {
    
    /**
     * Verify that attempting to create a private or protected annotation is not allowed
     */
    @Test
    public void cannotCreatePrivateOrProtected() {
        ClassBuilder builder = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D"));
        ClassAssert.assertInstance(JClassAnnotation.class, builder.setVisibility(VisibilityType.PUBLIC).build());
        ClassAssert.assertInstance(JClassAnnotation.class, builder.setVisibility(VisibilityType.PACKAGE_PRIVATE).build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setVisibility(VisibilityType.PROTECTED).build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setVisibility(VisibilityType.PRIVATE).build());
    }
    
    /**
     * Verify that only supported method can be added to the annotation
     */
    @Test
    public void cannotAddInvalidMethods() {
        ClassBuilder builder = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D")).setVisibility(VisibilityType.PUBLIC);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod("voidNotAllowed").finish());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "implementationNotAllowed").emptyImplementation().finish());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "implementationNotAllowed").addCode("").finish());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "privateNotAllowed").setVisibility(VisibilityType.PRIVATE).finish());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "protectedNotAllowed").setVisibility(VisibilityType.PROTECTED).finish());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.buildMethod(PrimitiveType.BOOLEAN, "packagePrivateNotAllowed").setVisibility(VisibilityType.PACKAGE_PRIVATE).finish());
    }
    
    /**
     * Verify that the empty annotation generates properly
     */
    @Test
    public void createEmptyAnnotation() {
        JClass annotation = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D")).setVisibility(VisibilityType.PUBLIC).build();

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public @interface D {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(annotation.generateCode());
    }
    
    /**
     * Verify that annotation classes can be generated with annotations
     */
    @Test
    public void createAnnotatedAnnotation() {
        JClass annotation = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D")).setVisibility(VisibilityType.PUBLIC).build();
        annotation.addAnnotation(JAnnotationFactory.create(new ClassType("d.e.f", "G")));
        annotation.addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class));

        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c;");
        matcher.eq("");
        matcher.eq("import d.e.f.G;");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@G");
        matcher.eq("@TestMarkerAnnotation");
        matcher.eq("public @interface D {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(annotation.generateCode());
    }
    
    /**
     * Verify that annotation classes can be generated with parameters
     */
    @Test
    public void createAnnotationWithMethods() {
        JClass annotation = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D")).setVisibility(VisibilityType.PUBLIC)
                .buildMethod(String.class, "strMethod").setDefaultValue(JValueFactory.create("abc123")).finish()
                .buildMethod(Integer.class, "intMethod").finish()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public @interface D {");
        matcher.eq("");
        matcher.eq("    String strMethod() default \"abc123\";");
        matcher.eq("");
        matcher.eq("    Integer intMethod();");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(annotation.generateCode());
    }
    
    /**
     * Verify that a complex annotation can be created containing both annotations and methods.
     */
    @Test
    public void createComplexAnnotation() {
        JClass annotation = ClassBuilder.forAnnotation(new ClassType("a.b.c", "D")).setVisibility(VisibilityType.PUBLIC)
                .buildMethod(String.class, "strMethod").addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class)).finish()
                .addAnnotation(JAnnotationFactory.create(new ClassType("d.e.f", "G")))
                .buildMethod(PrimitiveType.INT, "intMethod").addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class)).setDefaultValue(JValueFactory.create(123456)).finish()
                .addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class))
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c;");
        matcher.eq("");
        matcher.eq("import d.e.f.G;");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TestMarkerAnnotation.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("@G");
        matcher.eq("@TestMarkerAnnotation");
        matcher.eq("public @interface D {");
        matcher.eq("");
        matcher.eq("    @TestMarkerAnnotation");
        matcher.eq("    String strMethod();");
        matcher.eq("");
        matcher.eq("    @TestMarkerAnnotation");
        matcher.eq("    int intMethod() default 123456;");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(annotation.generateCode());
    }
}
