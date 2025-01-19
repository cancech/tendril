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
package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.apache.commons.lang3.NotImplementedException;

import com.google.auto.service.AutoService;

import tendril.bean.recipe.Recipe;
import tendril.bean.recipe.Registry;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericFactory;

@SupportedAnnotationTypes("tendril.bean.Provider")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ProviderProcessor extends AbstractTendrilProccessor {

    @Override
    protected void validateType(TypeElement type) {
        super.validateType(type);
    }

    @Override
    public ClassDefinition processType(ClassType data) {
        ClassType providerClass = data.generateFromClassSuffix("Recipe");
        return new ClassDefinition(providerClass, generateCode(providerClass, data));
    }

    private String generateCode(ClassType provider, ClassType bean) {
        JClass parent = ClassBuilder.forConcreteClass(new ClassType(Recipe.class)).addGeneric(GenericFactory.create(bean)).build();
        
        JClass cls = ClassBuilder.forConcreteClass(provider).setVisibility(VisibilityType.PUBLIC).extendsClass(parent)
                .addAnnotation(JAnnotationFactory.create(Registry.class))
                .buildConstructor().setVisibility(VisibilityType.PUBLIC).addCode("super(" + bean.getSimpleName() + ".class);").finish()
                .build();
        return cls.generateCode();
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        throw new NotImplementedException();
    }
}
