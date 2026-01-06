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

import javax.annotation.processing.Messager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Singleton;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.test.AbstractUnitTest;

/**
 * Integration test for verifying that the {@link ClassRecipeGenerator} produces the proper results.
 * 
 * Note this test case is only checking for failures, as the successes are far more easily tested in the test-app
 */
public class ClassRecipeGeneratorTest extends AbstractUnitTest {
    
    private class TestClassRecipeGenerator extends ClassRecipeGenerator {
        
        private boolean populateCalled = false;

        /**
         * @param beanType
         * @param creator
         */
        TestClassRecipeGenerator(ClassType beanType, JClass creator) {
            super(beanType, creator, mockMessager);
        }

        /**
         * @see tendril.processor.recipe.RecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
         */
        @Override
        protected void populateBuilder(ClassBuilder builder) {
            populateCalled = true;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Messager mockMessager;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Cannot process the class if it is abstract
     */
    @Test
    public void testCannotBeAbstract() {
        ClassType type = TypeFactory.createClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAbstractClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(TendrilException.class, () -> generator.generate(TypeFactory.createClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Cannot process the class if it is an interface
     */
    @Test
    public void testCannotBeInterface() {
        ClassType type = TypeFactory.createClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forInterface(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(TendrilException.class, () -> generator.generate(TypeFactory.createClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Cannot process the class if it is an annotation
     */
    @Test
    public void testCannotBeAnnotation() {
        ClassType type = TypeFactory.createClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAnnotation(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertThrows(TendrilException.class, () -> generator.generate(TypeFactory.createClassType("a.b.c.D"), false));
        Assertions.assertFalse(generator.populateCalled);
    }

    /**
     * Can process the class if it is a concrete class
     * @throws TendrilException 
     */
    @Test
    public void testMustBeConcrete() throws TendrilException {
        ClassType type = TypeFactory.createClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        TestClassRecipeGenerator generator = new TestClassRecipeGenerator(type, builder.build());
        Assertions.assertFalse(generator.generate(TypeFactory.createClassType("a.b.c.D"), false).getCode().isBlank());
        Assertions.assertTrue(generator.populateCalled);
    }
}
