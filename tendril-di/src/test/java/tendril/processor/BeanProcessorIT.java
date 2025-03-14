/*
 * Copyright 2025 Jaroslav Bosak
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.annotationprocessor.ProcessingException;
import tendril.bean.Singleton;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;

/**
 * Integration test for verifying that the {@link BeanProcessor} produces the proper results.
 * 
 * Note this test case is only checking for failures, as the successes are far more easily tested in the test-app
 */
public class BeanProcessorIT {
    
    private class TestBeanProcessor extends BeanProcessor {
        
        private TestBeanProcessor(ClassType type, JClass klass) {
            currentClassType = type;
            currentClass = klass;
        }
    }

    /**
     * Failure should be indicated if not just one recipe type is indicated
     */
    @Test
    public void testMustHaveOneRecipeType() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type);
        
        // No recipe type indicated
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
        
        // More than one recipe type indicated
        builder.addAnnotation(JAnnotationFactory.create(Singleton.class)).addAnnotation(JAnnotationFactory.create(Factory.class));
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
    
    /**
     * Failure should be indicated if there is not exactly one (viable) constructor
     */
    @Test
    public void testMustHaveOnlyOneNonAnnotatedConstructor() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        // No constructor
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
        
        // No viable constructor
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
        
        // More than one constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PROTECTED).emptyImplementation().buildParameter(PrimitiveType.DOUBLE, "param").finish().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
    
    /**
     * Failure should be indicated if there is not exactly one (viable) constructor
     */
    @Test
    public void testMustHaveOnlyOneInjectAnnotatedConstructor() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        // No constructor
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
        
        // No viable constructor
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
        
        // More than one constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.DOUBLE, "param").finish().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
    
    /**
     * Failure should be indicated if a PostContruct method is private
     */
    @Test
    public void testPostConstructMustNotBePrivate() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
    
    /**
     * Failure should be indicated if a PostContruct method is not void
     */
    @Test
    public void testPostConstructMustNotBeVoid() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod(PrimitiveType.BOOLEAN, "method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
    
    /**
     * Failure should be indicated if a PostContruct method must not take a parameter
     */
    @Test
    public void testPostConstructMustNotTakeAnyParameter() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class))
            .buildParameter(PrimitiveType.LONG, "param").finish().emptyImplementation().finish();
        Assertions.assertThrows(ProcessingException.class, () -> new TestBeanProcessor(type, builder.build()).processType());
    }
}
