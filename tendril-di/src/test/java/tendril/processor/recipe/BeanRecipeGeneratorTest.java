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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.annotation.processing.Messager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link BeanRecipeGenerator}
 */
public class BeanRecipeGeneratorTest extends AbstractUnitTest {
    
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
     * Failure should be indicated if not just one recipe type is indicated
     */
    @Test
    public void testMustHaveOneRecipeType_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type);
        
        // No recipe type indicated
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        
        // More than one recipe type indicated
        builder.addAnnotation(JAnnotationFactory.create(Singleton.class)).addAnnotation(JAnnotationFactory.create(Factory.class));
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if the class is abstract
     */
    @Test
    public void testClassMustNotAbstract_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAbstractClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if the class is an interface
     */
    @Test
    public void testClassMustNotInterface_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forInterface(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if the class is an annotation
     */
    @Test
    public void testClassMustNotAnnotation_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forAnnotation(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if there is not exactly one (viable) constructor
     */
    @Test
    public void testMustHaveOnlyOneNonAnnotatedConstructor_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        // No constructor
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        
        // No viable constructor
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        
        // More than one constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PROTECTED).emptyImplementation().buildParameter(PrimitiveType.DOUBLE, "param").finish().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if there is not exactly one (viable) constructor
     */
    @Test
    public void testMustHaveOnlyOneInjectAnnotatedConstructor_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        
        // No constructor
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        
        // No viable constructor
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        verify(mockMessager, times(2)).printWarning("q.w.e.Rty has a private @Inject constructor");
        
        // More than one constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.DOUBLE, "param").finish().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        verify(mockMessager, times(4)).printWarning("q.w.e.Rty has a private @Inject constructor");
    }
    
    /**
     * Failure should be indicated if a PostContruct method is private
     */
    @Test
    public void testPostConstructMustNotBePrivate_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        verify(mockMessager, times(2)).printWarning("q.w.e.Rty has a private @Inject constructor");
    }
    
    /**
     * Failure should be indicated if a PostContruct method is not void
     */
    @Test
    public void testPostConstructMustNotBeVoid_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod(PrimitiveType.BOOLEAN, "method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
    }
    
    /**
     * Failure should be indicated if a PostContruct method must not take a parameter
     */
    @Test
    public void testPostConstructMustNotTakeAnyParameter_Fails() {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class))
            .buildParameter(PrimitiveType.LONG, "param").finish().emptyImplementation().finish();
        Assertions.assertThrows(TendrilException.class, () -> RecipeGenerator.generate(type, builder.build(), mockMessager));
        verify(mockMessager, times(2)).printWarning("q.w.e.Rty has a private @Inject constructor");
    }
    
    /**
     * Can generate if there is a single Inject constructor (with other viables not annotated), and no PostConstruct method is present
     * @throws TendrilException 
     */
    @Test
    public void testSingleAnnotatedConstructorNoPostConstruct_Passes() throws TendrilException {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        Assertions.assertFalse(RecipeGenerator.generate(type, builder.build(), mockMessager).getCode().isBlank());
    }
    
    /**
     * Can generate if there is a single Inject constructor (with other viables not annotated), and the PostContruct method is valid
     * @throws TendrilException 
     */
    @Test
    public void testSingleAnnotatedConstructorValidPostConstruct_Passes() throws TendrilException {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertFalse(RecipeGenerator.generate(type, builder.build(), mockMessager).getCode().isBlank());
    }
    
    /**
     * Can generate if there is a single viable constructor (even if not annotated), and no PostContruct method is present
     * @throws TendrilException 
     */
    @Test
    public void testSingleViableConstructorNoPostConstruct_Passes() throws TendrilException {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        Assertions.assertFalse(RecipeGenerator.generate(type, builder.build(), mockMessager).getCode().isBlank());
        verify(mockMessager, times(2)).printWarning("q.w.e.Rty has a private @Inject constructor");
    }
    
    /**
     * Can generate if there is a single viable constructor (even if not annotated), and the PostContruct method is valid
     * @throws TendrilException 
     */
    @Test
    public void testSingleViableConstructorValidPostConstruct_Passes() throws TendrilException {
        ClassType type = new ClassType("q.w.e.Rty");
        ClassBuilder builder = ClassBuilder.forConcreteClass(type).addAnnotation(JAnnotationFactory.create(Singleton.class));
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PRIVATE).addAnnotation(JAnnotationFactory.create(Inject.class)).emptyImplementation().buildParameter(PrimitiveType.BOOLEAN, "param").finish().finish();
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().buildParameter(PrimitiveType.INT, "param").finish().finish();
        
        builder.buildMethod("method1").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(PostConstruct.class)).emptyImplementation().finish();
        Assertions.assertFalse(RecipeGenerator.generate(type, builder.build(), mockMessager).getCode().isBlank());
        verify(mockMessager, times(2)).printWarning("q.w.e.Rty has a private @Inject constructor");
    }
}
