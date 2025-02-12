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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.bean.qualifier.BeanId;
import tendril.bean.qualifier.BeanIdEnum;
import tendril.bean.qualifier.EnumQualifier;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValueFactory;

/**
 * Processor for {@link Enum}s annotated with {@link BeanIdEnum}. This will generated the appropriate Id {@link EnumQualifier} annotation for the enumeration, which can then be employed in the client
 * code for the purpose of qualifying beans with an enumeration ID.
 */
@SupportedAnnotationTypes("tendril.bean.qualifier.BeanIdEnum")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanEnumProcessor extends AbstractTendrilProccessor {

    /**
     * CTOR
     */
    public BeanEnumProcessor() {
    }

    /**
     * The annotated {@link TypeElement} must be an {@link Enum} and it must implement the {@link BeanId} interface
     * 
     * @see tendril.processor.AbstractTendrilProccessor#validateType(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void validateType(TypeElement type) {
        if (type.getKind() != ElementKind.ENUM)
            throwValidationException(type, "Must be an enum");
        if (!isTypeOf(type, BeanId.class))
            throwValidationException(type, "Must implement the " + BeanId.class.getName() + " interface");
    }

    /**
     * Generates an exception based on the {@link TypeElement} being validated and the concrete reason.
     * 
     * @param type {@link TypeElement} whose processing is generating the exception
     * @param reason {@link String} the reason why the exception is being produced
     */
    private void throwValidationException(TypeElement type, String reason) {
        throw new IllegalArgumentException("Unable to use " + type.getQualifiedName() + " - " + reason);
    }

    /**
     * Process the annotated class, generating the appropriate {@link EnumQualifier} for the {@link Enum}
     * 
     * @see tendril.processor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
     */
    @Override
    public ClassDefinition processType(ClassType data) {
        ClassType providerClass = data.generateFromClassSuffix("Id");
        return new ClassDefinition(providerClass, generateCode(providerClass, data));
    }

    /**
     * Generate the code for the {@link EnumQualifier} which treats the annotated {@link Enum} as an ID
     * 
     * @param qualifier {@link ClassType} representing the qualifier annotation that is to be created
     * @param sourceEnum {@link ClassType} representing the {@link Enum} that is to be used as the ID
     * @return {@link String} containing the generated code
     * @throws ClassNotFoundException if the sourceEnum representing as unknown type
     */
    private String generateCode(ClassType qualifier, ClassType sourceEnum) {
        JClass cls = ClassBuilder.forAnnotation(qualifier).setVisibility(VisibilityType.PUBLIC)
                .addAnnotation(JAnnotationFactory.create(Retention.class, JValueFactory.create(RetentionPolicy.RUNTIME)))
                .addAnnotation(JAnnotationFactory.create(Target.class, JValueFactory.createArray(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER)))
                .addAnnotation(JAnnotationFactory.create(EnumQualifier.class))
                .buildMethod(sourceEnum, "value").setVisibility(VisibilityType.PUBLIC).finish().build();
        return cls.generateCode();
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        throw new IllegalArgumentException(BeanId.class.getName() + " cannot be applied to any method [" + methodData.getName() + "]");
    }
}
