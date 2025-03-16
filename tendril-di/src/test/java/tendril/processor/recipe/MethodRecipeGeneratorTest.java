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
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.method.ConcreteMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;

/**
 * Integration test for verifying that the {@link MethodRecipeGenerator} produces the proper results.
 * 
 * Note this test case is only checking for failures, as the successes are far more easily tested in the test-app
 */
public class MethodRecipeGeneratorTest {

    /**
     * Cannot process the method if it is static
     */
    @Test
    public void testCannotBeStatic() {
        ClassType configType = new ClassType("a.b.c.D");
        MethodBuilder<Type> builder = new ConcreteMethodBuilder<>(null, "method").setType(PrimitiveType.INT)
                .addAnnotation(JAnnotationFactory.create(Singleton.class)).emptyImplementation();
        builder.setStatic(true);
        
        Assertions.assertThrows(ProcessingException.class, () -> RecipeGenerator.generate(configType, builder.build()));
    }

    /**
     * Cannot process the method if it is private
     */
    @Test
    public void testCannotBePrivate() {
        ClassType configType = new ClassType("a.b.c.D");
        MethodBuilder<Type> builder = new ConcreteMethodBuilder<>(null, "method").setType(PrimitiveType.INT)
                .addAnnotation(JAnnotationFactory.create(Singleton.class)).emptyImplementation();
        builder.setVisibility(VisibilityType.PRIVATE);
        
        Assertions.assertThrows(ProcessingException.class, () -> RecipeGenerator.generate(configType, builder.build()));
    }

    /**
     * Make sure that the method can be processed
     */
    @Test
    public void testMustBeConcrete() {
        ClassType configType = new ClassType("a.b.c.D");
        MethodBuilder<Type> builder = new ConcreteMethodBuilder<>(null, "method").setType(PrimitiveType.INT)
                .addAnnotation(JAnnotationFactory.create(Singleton.class)).emptyImplementation();
        
        Assertions.assertFalse(RecipeGenerator.generate(configType, builder.build()).getCode().isBlank());
    }
}
