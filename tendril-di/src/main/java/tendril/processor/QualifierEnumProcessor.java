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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AnnotationFromEnumProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.qualifier.GeneratedQualifier;
import tendril.bean.qualifier.Qualifier;
import tendril.bean.qualifier.QualifierEnum;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassEnum;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueFactory;

/**
 * Processor for generating qualifier annotations from the values of an Enum annotated with @QualifierEnum. The generated annotation can then be employed as qualifiers on
 * beans.
 */
@SupportedAnnotationTypes("tendril.bean.qualifier.QualifierEnum")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class QualifierEnumProcessor extends AnnotationFromEnumProcessor {

    /**
     * CTOR
     */
    public QualifierEnumProcessor() {
        super(QualifierEnum.class);
    }
    
    /**
     * CTOR
     * 
     * @param processorAnnotation {@link Annotation} {@link Class} that the processor is to process
     */
    public QualifierEnumProcessor(Class<? extends Annotation> processorAnnotation) {
        super(processorAnnotation);
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() throws TendrilException {
        JClassEnum enumClass = (JClassEnum) currentClass;
        // Generate an annotation for each value, rather than one for the whole enum
        for (EnumerationEntry entry: enumClass.getEnumerations()) {
            ClassType type = TypeFactory.createClassType(currentClassType.getPackageName(), entry.getName());
            writeCode(new ClassDefinition (type, generateCode(type)));
        }
        return null;
    }
    
    /**
     * Generate the code for the annotation which is to be created.
     * 
     * @param type {@link ClassType} of the generated annotation
     * @return {@link String} containing the full code for the class
     */
    protected String generateCode(ClassType type) {
        JClass cls = ClassBuilder.forAnnotation(type).setVisibility(VisibilityType.PUBLIC)
                .addAnnotation(JAnnotationFactory.create(GeneratedQualifier.class))
                .addAnnotation(JAnnotationFactory.create(Retention.class, JValueFactory.create(RetentionPolicy.RUNTIME)))
                .addAnnotation(JAnnotationFactory.create(Target.class, JValueFactory.createArray(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER)))
                .addAnnotation(JAnnotationFactory.create(Qualifier.class)).build();
        return cls.generateCode();
    }

}
