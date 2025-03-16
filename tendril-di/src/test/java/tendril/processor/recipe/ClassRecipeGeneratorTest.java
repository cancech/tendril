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
package tendril.processor.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.annotationprocessor.ProcessingException;
import tendril.bean.Singleton;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;

/**
 * Integration test for verifying that the {@link ClassRecipeGenerator} produces the proper results.
 * 
 * Note this test case is only checking for failures, as the successes are far more easily tested in the test-app
 */
public class ClassRecipeGeneratorTest {
    
    private class TestClassRecipeGenerator extends ClassRecipeGenerator {
        
        private boolean populateCalled = false;

        /**
         * @param beanType
         * @param creator
         */
        TestClassRecipeGenerator(ClassType beanType, JClass creator) {
            super(beanType, creator);
        }

        /**
         * @see tendril.processor.recipe.RecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
         */
        @Override
        protected void populateBuilder(ClassBuilder builder) {
            populateCalled = true;
        }
        
    }

    /**
     * Cannot process the class if it is abstract
     */
    @Test
    public void testCannotBeAbstract() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAbstractClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(ProcessingException.class, () -> generator.generate(new ClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Cannot process the class if it is an interface
     */
    @Test
    public void testCannotBeInterface() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forInterface(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(ProcessingException.class, () -> generator.generate(new ClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Cannot process the class if it is an annotation
     */
    @Test
    public void testCannotBeAnnotation() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAnnotation(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(ProcessingException.class, () -> generator.generate(new ClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Can process the class if it is a concrete class
     */
    @Test
    public void testMustBeConcrete() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertFalse(generator.generate(new ClassType("a.b.c.D"), false).getCode().isBlank());
        Assertions.assertTrue(generator.populateCalled);
    }
}
